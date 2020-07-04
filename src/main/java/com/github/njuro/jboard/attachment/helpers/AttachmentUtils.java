package com.github.njuro.jboard.attachment.helpers;

import static com.github.njuro.jboard.common.Constants.IMAGE_MAX_THUMB_HEIGHT;
import static com.github.njuro.jboard.common.Constants.IMAGE_MAX_THUMB_WIDTH;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.github.njuro.jboard.attachment.Attachment;
import com.github.njuro.jboard.attachment.AttachmentMetadata;
import com.github.njuro.jboard.attachment.AttachmentType;
import com.github.njuro.jboard.attachment.helpers.GifDecoder.GifImage;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainer.Type;
import com.xuggle.xuggler.IStreamCoder;
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

@UtilityClass
@Slf4j
public class AttachmentUtils {

  public void setMetadata(Attachment attachment) {
    setAttachmentType(attachment);

    switch (attachment.getType()) {
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

    setFileSize(attachment);
  }

  private void setAttachmentType(Attachment attachment) {
    String mimeType = null;
    try {
      mimeType = Files.probeContentType(attachment.getFile().toPath());
    } catch (IOException ex) {
      log.error("Failed to find out file type: " + ex.getMessage());
    }

    attachment.getMetadata().setMimeType(mimeType);
    attachment.setType(AttachmentType.determineAttachmentType(mimeType));

    if (attachment.getType() == null) {
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
    IContainer container = IContainer.make();
    if (container.open(attachment.getFile().toPath().toString(), Type.READ, null) < 0) {
      log.error("Failed to open video file");
      return;
    }

    attachment.getMetadata().setDuration(convertDuration(container.getDuration()));

    for (int i = 0; i < container.getNumStreams(); i++) {
      IStreamCoder coder = container.getStream(i).getStreamCoder();
      if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
        attachment.getMetadata().setWidth(coder.getWidth());
        attachment.getMetadata().setHeight(coder.getHeight());
        break;
      }
    }
  }

  private void setAudioMetadata(Attachment attachment) {
    IContainer container = IContainer.make();
    if (container.open(attachment.getFile().toPath().toString(), Type.READ, null) < 0) {
      log.error("Failed to open audio file");
      return;
    }

    attachment.getMetadata().setDuration(convertDuration(container.getDuration()));
  }

  private String convertDuration(long durationInMicroseconds) {
    long ms = durationInMicroseconds / 1000;
    return String.format(
        "%02d:%02d:%02d",
        MILLISECONDS.toHours(ms),
        MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(MILLISECONDS.toHours(ms)),
        MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(ms)));
  }

  private void setFileSize(Attachment attachment) {
    attachment
        .getMetadata()
        .setFileSize(FileUtils.byteCountToDisplaySize(attachment.getFile().length()));
  }

  public RenderedImage createThumbnail(Attachment attachment) {
    if (!attachment.getType().hasThumbnail()) {
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

    if (attachment.getType() == AttachmentType.PDF) {
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
    switch (att.getType()) {
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
    try {
      // bug in JDK, need to use custom GIF decoder
      GifImage gif = GifDecoder.read(new FileInputStream(att.getFile()));
      return gif.getFrame(0);
    } catch (IOException ex) {
      log.error("Error while reading GIF image: " + ex.getMessage());
      return null;
    }
  }

  private BufferedImage getImageFromVideoAttachment(Attachment att) {
    try {
      return VideoThumbnailMaker.getImageFromVideo(att.getFile().toPath().toString());
    } catch (Exception ex) {
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
