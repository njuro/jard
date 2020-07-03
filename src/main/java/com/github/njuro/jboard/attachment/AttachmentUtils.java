package com.github.njuro.jboard.attachment;

import static com.github.njuro.jboard.common.Constants.IMAGE_MAX_THUMB_HEIGHT;
import static com.github.njuro.jboard.common.Constants.IMAGE_MAX_THUMB_WIDTH;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.github.njuro.jboard.attachment.GifDecoder.GifImage;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainer.Type;
import com.xuggle.xuggler.IStreamCoder;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

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
    if (attachment.getType() != AttachmentType.IMAGE) {
      // TODO support for video
      return null;
    }

    setThumbnailDimensions(attachment);

    AttachmentMetadata metadata = attachment.getMetadata();
    BufferedImage image = getImageFromAttachment(attachment);
    Image originalImage =
        image.getScaledInstance(
            metadata.getThumbnailWidth(), metadata.getThumbnailHeight(), Image.SCALE_SMOOTH);

    int type = ((image.getType() == 0) ? BufferedImage.TYPE_INT_ARGB : image.getType());
    BufferedImage resizedImage =
        new BufferedImage(metadata.getThumbnailWidth(), metadata.getThumbnailHeight(), type);

    Graphics2D g2d = resizedImage.createGraphics();
    g2d.drawImage(
        originalImage, 0, 0, metadata.getThumbnailWidth(), metadata.getThumbnailHeight(), null);
    g2d.dispose();
    g2d.setComposite(AlphaComposite.Src);
    g2d.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    return resizedImage;
  }

  /**
   * Calculates thumbnail dimensions for image attachments.
   *
   * @att image attachment
   */
  private void setThumbnailDimensions(Attachment attachment) {
    AttachmentMetadata metadata = attachment.getMetadata();
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
}
