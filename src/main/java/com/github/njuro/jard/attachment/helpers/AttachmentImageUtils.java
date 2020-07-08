package com.github.njuro.jard.attachment.helpers;

import static com.github.njuro.jard.common.Constants.IMAGE_MAX_THUMB_HEIGHT;
import static com.github.njuro.jard.common.Constants.IMAGE_MAX_THUMB_WIDTH;

import com.github.njuro.jard.attachment.Attachment;
import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.attachment.AttachmentMetadata;
import com.github.njuro.jard.attachment.helpers.GifDecoder.GifImage;
import com.github.njuro.jard.common.Constants;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.experimental.UtilityClass;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.AlphaInterpolation;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import net.coobird.thumbnailator.resizers.configurations.Dithering;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * Utility class with methods for manipulating with image representation of specific attachment
 * categories and creating their thumbnails.
 *
 * @see AttachmentCategory
 */
@UtilityClass
public class AttachmentImageUtils {

  /**
   * Creates thumbnail for given attachment. Thumbnail dimensions are automatically calculated.
   *
   * @param attachment to create thumbnail for
   * @return created thumbnail
   * @throws NullPointerException if attachment or its category is null
   * @throws IllegalArgumentException if thumbnail creation failed
   */
  public RenderedImage createThumbnail(Attachment attachment) {
    Objects.requireNonNull(attachment, "Attachment cannot be null");
    Objects.requireNonNull(attachment.getCategory(), "Attachment category cannot be null");

    if (!attachment.getCategory().hasThumbnail()) {
      throw new IllegalArgumentException(
          "Cannot create thumbnail for attachment category " + attachment.getCategory().name());
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
      throw new IllegalArgumentException("Failed to create thumbnail", ex);
    }
  }

  /**
   * Calculates thumbnail dimensions for attachment.
   *
   * @param attachment to calculate thumbnail dimensions for
   * @see Constants#IMAGE_MAX_THUMB_HEIGHT
   * @see Constants#IMAGE_MAX_THUMB_WIDTH
   */
  private void setThumbnailDimensions(Attachment attachment) {
    AttachmentMetadata metadata = attachment.getMetadata();

    if (attachment.getCategory() == AttachmentCategory.PDF) {
      // TODO: For now we assume every PDF is A4 format with portrait orientation.
      metadata.setThumbnailHeight((int) IMAGE_MAX_THUMB_HEIGHT);
      metadata.setThumbnailWidth((int) (IMAGE_MAX_THUMB_HEIGHT / Math.sqrt(2)));
      return;
    }

    if (metadata.getWidth() == 0 || metadata.getHeight() == 0) {
      // set real dimensions first
      AttachmentMetadataUtils.setMetadata(attachment);
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
   * Retrieves image representation of attachment. What constitutes for image representation is
   * explained in documentation of different getImageFromXAttachment methods.
   *
   * @param attachment to process
   * @return image representation of attachment
   * @throws IllegalArgumentException when reading image data fails
   */
  BufferedImage getImageFromAttachment(Attachment attachment) {
    switch (attachment.getCategory()) {
      case IMAGE:
        return getImageFromImageAttachment(attachment);
      case VIDEO:
        return getImageFromVideoAttachment(attachment);
      case PDF:
        return getImageFromPdfAttachment(attachment);
      default:
        throw new IllegalArgumentException(
            "Cannot get image from attachment category " + attachment.getCategory().name());
    }
  }

  /**
   * Gets image representation of {@link AttachmentCategory#IMAGE} attachments.
   *
   * @param attachment to process
   * @return image file
   * @throws IllegalArgumentException if opening image file fails
   */
  private BufferedImage getImageFromImageAttachment(Attachment attachment) {
    try {
      return ImageIO.read(attachment.getFile());
    } catch (ArrayIndexOutOfBoundsException ex) {
      // bug in JDK see documentation of method below
      return getImageFromGifAttachment(attachment);
    } catch (IOException ex) {
      throw new IllegalArgumentException("Reading image file failed", ex);
    }
  }

  /**
   * Gets image representation of {@link AttachmentCategory#IMAGE} attachments. This method handles
   * special case for some of the {@code gif} files.
   *
   * <p>Due to <a href="https://bugs.openjdk.java.net/browse/JDK-7132728">bug in JDK</a>, sometimes
   * the {@link ImageIO#read(File)} throws {@link ArrayIndexOutOfBoundsException} while reading a
   * {@code gif} file. To solve this, we use custom {@code gif} decoder to read such files.
   *
   * @param attachment to process
   * @return image
   * @throws IllegalArgumentException if opening {@code gif} file with custom decoder fails
   * @see GifDecoder
   */
  private BufferedImage getImageFromGifAttachment(Attachment attachment) {
    try (var inputFile = new FileInputStream(attachment.getFile())) {
      GifImage gif = GifDecoder.read(inputFile);
      return gif.getFrame(0);
    } catch (IOException ex) {
      throw new IllegalArgumentException("Reading GIF file failed", ex);
    }
  }

  /**
   * Gets image representation of {@link AttachmentCategory#VIDEO} attachments.
   *
   * @param attachment to process
   * @return image created from first complete frame of video
   * @throws IllegalArgumentException if opening video file fails
   * @see VideoThumbnailMaker
   */
  private BufferedImage getImageFromVideoAttachment(Attachment attachment) {
    try {
      return VideoThumbnailMaker.getImageFromVideo(attachment.getFile().toPath().toString());
    } catch (IOException | InterruptedException ex) {
      throw new IllegalArgumentException("Reading video file failed", ex);
    }
  }

  /**
   * Gets image representation of {@link AttachmentCategory#PDF} attachments.
   *
   * @param attachment to process
   * @return image created from the first page of PDF document
   * @throws IllegalArgumentException if opening PDF file fails
   * @see VideoThumbnailMaker
   */
  private BufferedImage getImageFromPdfAttachment(Attachment attachment) {
    try {
      PDDocument document = PDDocument.load(attachment.getFile());
      PDFRenderer pdfRenderer = new PDFRenderer(document);
      BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
      document.close();
      return bim;
    } catch (IOException ex) {
      throw new IllegalArgumentException("Reading PDF file failed", ex);
    }
  }
}
