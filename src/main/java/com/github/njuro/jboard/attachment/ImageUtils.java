package com.github.njuro.jboard.attachment;

import static com.github.njuro.jboard.common.Constants.IMAGE_MAX_THUMB_HEIGHT;
import static com.github.njuro.jboard.common.Constants.IMAGE_MAX_THUMB_WIDTH;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility methods for working with image attachments
 *
 * @author njuro
 */
@UtilityClass
@Slf4j
public class ImageUtils {

  /**
   * Sets dimension of a image attachment
   *
   * @param att attachment
   */
  public void setDimensions(Attachment att) {
    BufferedImage img = getImageFromAttachment(att);
    if (img == null) {
      return;
    }

    att.setWidth(img.getWidth());
    att.setHeight(img.getHeight());
  }

  public RenderedImage createThumbnail(Attachment attachment) {
    setThumbnailDimensions(attachment);

    BufferedImage image = getImageFromAttachment(attachment);
    Image originalImage =
        image.getScaledInstance(
            attachment.getThumbnailWidth(), attachment.getThumbnailHeight(), Image.SCALE_SMOOTH);

    int type = ((image.getType() == 0) ? BufferedImage.TYPE_INT_ARGB : image.getType());
    BufferedImage resizedImage =
        new BufferedImage(attachment.getThumbnailWidth(), attachment.getThumbnailHeight(), type);

    Graphics2D g2d = resizedImage.createGraphics();
    g2d.drawImage(
        originalImage, 0, 0, attachment.getThumbnailWidth(), attachment.getThumbnailHeight(), null);
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
  private void setThumbnailDimensions(Attachment att) {
    if (att.getWidth() == 0 || att.getHeight() == 0) {
      // set real dimensions first
      setDimensions(att);
    }

    if (att.getWidth() > IMAGE_MAX_THUMB_WIDTH || att.getHeight() > IMAGE_MAX_THUMB_HEIGHT) {
      double factor =
          Math.min(
              IMAGE_MAX_THUMB_WIDTH / att.getWidth(), IMAGE_MAX_THUMB_HEIGHT / att.getHeight());
      att.setThumbnailWidth(((int) Math.ceil(att.getWidth() * factor)));
      att.setThumbnailHeight(((int) Math.ceil(att.getHeight() * factor)));
    } else {
      // thumbnail dimensions are the same as real dimensions
      att.setThumbnailHeight(att.getHeight());
      att.setThumbnailWidth(att.getWidth());
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
    } catch (IOException ex) {
      log.error("Error while reading image: " + ex.getMessage());
      return null;
    }
  }
}
