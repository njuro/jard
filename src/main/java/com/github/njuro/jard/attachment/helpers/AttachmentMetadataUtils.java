package com.github.njuro.jard.attachment.helpers;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.github.njuro.jard.attachment.Attachment;
import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.attachment.AttachmentMetadata;
import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.MediaDescriptor.Type;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;
import org.springframework.util.DigestUtils;

/**
 * Utility class with methods for retrieving and storing metadata of uploaded attachments.
 *
 * @see AttachmentMetadata
 */
@UtilityClass
public class AttachmentMetadataUtils {

  /**
   * Determines category of {@link Attachment} and sets appropriate metadata for this category. May
   * also create thumbnail for attachment (if this category supports it).
   *
   * @param attachment attachment to set metadata on
   * @throws IllegalArgumentException if attachment has missing/unknown values for properly setting
   *     metadata
   * @throws NullPointerException if attachment is null
   * @see AttachmentCategory
   */
  public void setMetadata(Attachment attachment) {
    Objects.requireNonNull(attachment);
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
      default:
        break;
    }

    setFileMetadata(attachment);
  }

  /**
   * Determines and sets category of attachment based on its MIME type.
   *
   * @param attachment attachment to set category on
   * @throws IllegalArgumentException if attachment has MIME type which does not belong to any
   *     category
   * @see AttachmentCategory
   */
  private void setAttachmentCategory(Attachment attachment) {
    String mimeType = attachment.getMetadata().getMimeType();
    attachment.setCategory(AttachmentCategory.determineAttachmentCategory(mimeType));

    if (attachment.getCategory() == null) {
      throw new IllegalArgumentException(
          "Unknown type of attachment - mime type is: " + attachment.getMetadata().getMimeType());
    }
  }

  /**
   * Sets metadata for {@link AttachmentCategory#IMAGE} attachments: {@code width } and {@code
   * height}.
   *
   * @param attachment attachment to set image metadata on
   * @throws IllegalArgumentException if opening image file fails
   */
  private void setImageMetadata(Attachment attachment) {
    BufferedImage image = AttachmentImageUtils.getImageFromAttachment(attachment);
    attachment.getMetadata().setWidth(image.getWidth());
    attachment.getMetadata().setHeight(image.getHeight());
  }

  /**
   * Sets metadata for {@link AttachmentCategory#VIDEO} attachments: {@code duration}, {@code width}
   * and {@code height}.
   *
   * @param attachment attachment to set video metadata on
   * @throws IllegalArgumentException if opening or closing video file fails
   */
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

      demuxer.close();
    } catch (IOException ex) {
      throw new IllegalArgumentException("Failed to open video file", ex);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Sets metadata for {@link AttachmentCategory#AUDIO} attachments: {@code duration}.
   *
   * @param attachment attachment to set audio metadata on
   * @throws IllegalArgumentException if opening or closing audio file fails
   */
  private void setAudioMetadata(Attachment attachment) {
    try {
      Demuxer demuxer = Demuxer.make();
      demuxer.open(attachment.getFile().toPath().toString(), null, false, true, null, null);
      attachment.getMetadata().setDuration(convertDuration(demuxer.getDuration()));
      demuxer.close();
    } catch (IOException ex) {
      throw new IllegalArgumentException("Failed to open audio file", ex);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Converts duration of video/audio file to human readable form:
   *
   * @param duration duration in microseconds
   * @return duration in format hh:mm:ss
   */
  private String convertDuration(long duration) {
    long ms = duration / 1000;
    return String.format(
        "%02d:%02d:%02d",
        MILLISECONDS.toHours(ms),
        MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(MILLISECONDS.toHours(ms)),
        MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(MILLISECONDS.toMinutes(ms)));
  }

  /**
   * Sets metadata common for all attachment categories: {@code fileSize} and {@code checksum}.
   *
   * @param attachment attachment to set file metadata on
   * @throws IllegalArgumentException if calculation of checksum fails
   */
  private void setFileMetadata(Attachment attachment) {
    try {
      attachment
          .getMetadata()
          .setChecksum(
              DigestUtils.md5DigestAsHex(Files.readAllBytes(attachment.getFile().toPath())));
    } catch (IOException ex) {
      throw new IllegalArgumentException("Failed to calculate file checksum", ex);
    }

    attachment
        .getMetadata()
        .setFileSize(FileUtils.byteCountToDisplaySize(attachment.getFile().length()));
  }
}
