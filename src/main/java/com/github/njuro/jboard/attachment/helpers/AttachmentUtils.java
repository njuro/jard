package com.github.njuro.jboard.attachment.helpers;

import static com.github.njuro.jboard.common.Constants.IMAGE_MAX_THUMB_HEIGHT;
import static com.github.njuro.jboard.common.Constants.IMAGE_MAX_THUMB_WIDTH;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.github.njuro.jboard.attachment.Attachment;
import com.github.njuro.jboard.attachment.AttachmentCategory;
import com.github.njuro.jboard.attachment.AttachmentMetadata;
import com.github.njuro.jboard.attachment.helpers.GifDecoder.GifImage;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.MediaDescriptor.Type;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.AlphaInterpolation;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import net.coobird.thumbnailator.resizers.configurations.Dithering;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.util.DigestUtils;

@UtilityClass
@Slf4j
public class AttachmentUtils {

  public void setMetadata(Attachment attachment) {
    setAttachmentCategory(attachment);

    switch (attachment.getCategory()) {
      case IMAGE:
        setImageMetadata(attachment);
        break;
      case VIDEO:
        setVideoMetadata(attachment);
        break;
      case AUDIO:
        setAudioMetadata(attachment);
        break;
    }

    setFileMetadata(attachment);
  }

  private void setAttachmentCategory(Attachment attachment) {
    String mimeType = attachment.getMetadata().getMimeType();
    attachment.setCategory(AttachmentCategory.determineAttachmentCategory(mimeType));

    if (attachment.getCategory() == null) {
      throw new IllegalArgumentException("Unknown type of attachment");
    }
  }

  private void setImageMetadata(Attachment attachment) {
    BufferedImage img = getImageFromAttachment(attachment);
    if (img == null) {
      return;
    }

    attachment.getMetadata().setWidth(img.getWidth());
    attachment.getMetadata().setHeight(img.getHeight());
  }

  private void setVideoMetadata(Attachment attachment) {
    try {
      Demuxer demuxer = Demuxer.make();
      demuxer.open(attachment.getFile().toPath().toString(), null, false, true, null, null);
      attachment.getMetadata().setDuration(convertDuration(demuxer.getDuration()));

      for (int i = 0; i < demuxer.getNumStreams(); i++) {
        Decoder decoder = demuxer.getStream(i).getDecoder();
        if (decoder.getCodecType() == Type.MEDIA_VIDEO) {
          attachment.getMetadata().setWidth(decoder.getWidth());
          attachment.getMetadata().setHeight(decoder.getHeight());
          break;
        }
      }

    } catch (InterruptedException | IOException ex) {
      log.error("Failed to open video file: " + ex.getMessage());
    }
  }

  private void setAudioMetadata(Attachment attachment) {
    try {
      Demuxer demuxer = Demuxer.make();
      demuxer.open(attachment.getFile().toPath().toString(), null, false, true, null, null);
      attachment.getMetadata().setDuration(convertDuration(demuxer.getDuration()));
    } catch (InterruptedException | IOException ex) {
      log.error("Failed to open audio file: " + ex.getMessage());
    }
  }

  private String convertDuration(long durationInMicroseconds) {
    long ms = durationInMicroseconds / 1000;
    return String.format(
        "%02d:%02d:%02d",
        MILLISECONDS.toHours(ms),
        MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(MILLISECONDS.toHours(ms)),
        MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(ms)));
  }

  private void setFileMetadata(Attachment attachment) {
    try {
      attachment
          .getMetadata()
          .setHash(DigestUtils.md5DigestAsHex(Files.readAllBytes(attachment.getFile().toPath())));
    } catch (IOException ex) {
      log.error("Failed to calculate file hash: " + ex.getMessage());
    }

    attachment
        .getMetadata()
        .setFileSize(FileUtils.byteCountToDisplaySize(attachment.getFile().length()));
  }

  public RenderedImage createThumbnail(Attachment attachment) {
    if (!attachment.getCategory().hasThumbnail()) {
      return null;
    }

    setThumbnailDimensions(attachment);

    AttachmentMetadata metadata = attachment.getMetadata();
    BufferedImage image = getImageFromAttachment(attachment);

    try {
      return Thumbnails.of(image)
          .size(metadata.getThumbnailWidth(), metadata.getThumbnailHeight())
          .outputQuality(1.0)
          .alphaInterpolation(AlphaInterpolation.QUALITY)
          .scalingMode(ScalingMode.BILINEAR)
          .antialiasing(Antialiasing.ON)
          .dithering(Dithering.ENABLE)
          .asBufferedImage();
    } catch (IOException ex) {
      log.error("Error creating thumbnail: " + ex.getMessage());
      return null;
    }
  }

  /**
   * Calculates thumbnail dimensions for image attachments.
   *
   * @att image attachment
   */
  private void setThumbnailDimensions(Attachment attachment) {
    AttachmentMetadata metadata = attachment.getMetadata();

    if (attachment.getCategory() == AttachmentCategory.PDF) {
      metadata.setThumbnailHeight((int) IMAGE_MAX_THUMB_HEIGHT);
      metadata.setThumbnailWidth((int) (IMAGE_MAX_THUMB_HEIGHT / Math.sqrt(2)));
      return;
    }

    if (metadata.getWidth() == 0 || metadata.getHeight() == 0) {
      // set real dimensions first
      setMetadata(attachment);
    }

    if (metadata.getWidth() > IMAGE_MAX_THUMB_WIDTH
        || metadata.getHeight() > IMAGE_MAX_THUMB_HEIGHT) {
      double factor =
          Math.min(
              IMAGE_MAX_THUMB_WIDTH / metadata.getWidth(),
              IMAGE_MAX_THUMB_HEIGHT / metadata.getHeight());
      metadata.setThumbnailWidth(((int) Math.ceil(metadata.getWidth() * factor)));
      metadata.setThumbnailHeight(((int) Math.ceil(metadata.getHeight() * factor)));
    } else {
      // thumbnail dimensions are the same as real dimensions
      metadata.setThumbnailHeight(metadata.getHeight());
      metadata.setThumbnailWidth(metadata.getWidth());
    }
  }

  /**
   * Attempts to retrieve image data from image attachment
   *
   * @param att image attachment
   * @return image, or null if attachment does not have file associated with it
   * @throws IllegalArgumentException when reading image data fails
   */
  private BufferedImage getImageFromAttachment(Attachment att) {
    switch (att.getCategory()) {
      case IMAGE:
        return getImageFromBasicAttachment(att);
      case VIDEO:
        return getImageFromVideoAttachment(att);
      case PDF:
        return getImageFromPdfAttachment(att);
    }

    return null;
  }

  private BufferedImage getImageFromBasicAttachment(Attachment att) {
    try {
      return ImageIO.read(att.getFile());
    } catch (ArrayIndexOutOfBoundsException ex) {
      return getImageFromGifAttachment(att);
    } catch (IOException ex) {
      log.error("Error while reading image: " + ex.getMessage());
      return null;
    }
  }

  private BufferedImage getImageFromGifAttachment(Attachment att) {
    try (var inputFile = new FileInputStream(att.getFile())) {
      // bug in JDK, need to use custom GIF decoder
      GifImage gif = GifDecoder.read(inputFile);
      return gif.getFrame(0);
    } catch (IOException ex) {
      log.error("Error while reading GIF image: " + ex.getMessage());
      return null;
    }
  }

  private BufferedImage getImageFromVideoAttachment(Attachment att) {
    try {
      return VideoThumbnailMaker.getImageFromVideo(att.getFile().toPath().toString());
    } catch (IOException | InterruptedException ex) {
      log.error("Error while reading video attachment: " + ex.getMessage());
      return null;
    }
  }

  private BufferedImage getImageFromPdfAttachment(Attachment att) {
    try {
      PDDocument document = PDDocument.load(att.getFile());
      PDFRenderer pdfRenderer = new PDFRenderer(document);
      BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
      document.close();
      return bim;
    } catch (IOException ex) {
      log.error("Error while reading PDF attachment: " + ex.getMessage());
      return null;
    }
  }
}
