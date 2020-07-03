package com.github.njuro.jboard.attachment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.springframework.http.MediaType;

public enum AttachmentType {
  IMAGE(
      true,
      MediaType.IMAGE_JPEG,
      MediaType.IMAGE_PNG,
      MediaType.IMAGE_GIF,
      new MediaType("image", "bmp"),
      new MediaType("image", "webp"),
      new MediaType("image", "svg+xml")),
  VIDEO(
      true,
      new MediaType("video", "webm"),
      new MediaType("video", "mp4"),
      new MediaType("application", "mp4"),
      new MediaType("video", "x-matroska"), // .mkv
      new MediaType("video", "x-msvideo")), // .avi
  AUDIO(
      false,
      new MediaType("audio", "mpeg"), // .mp3
      new MediaType("audio", "wav"),
      new MediaType("audio", "ogg")),
  TEXT(
      false,
      MediaType.TEXT_PLAIN, // .txt
      MediaType.TEXT_XML,
      MediaType.APPLICATION_PDF, // .pdf
      new MediaType("application", "msword"), // .doc
      new MediaType(
          "application", "vnd.openxmlformats-officedocument.wordprocessingml.document")); // .docx

  private final boolean thubmnail;

  @Getter private final Set<MediaType> mediaTypes = new HashSet<>();

  AttachmentType(boolean thubmnail, MediaType... mediaTypes) {
    this.thubmnail = thubmnail;
    this.mediaTypes.addAll(Arrays.asList(mediaTypes));
  }

  public boolean hasThumbnail() {
    return thubmnail;
  }

  public static AttachmentType determineAttachmentType(String mimeType) {
    return Arrays.stream(AttachmentType.values())
        .filter(
            attachmentType ->
                attachmentType.getMediaTypes().stream()
                    .map(MediaType::toString)
                    .anyMatch(mediaType -> mediaType.equalsIgnoreCase(mimeType)))
        .findAny()
        .orElse(null);
  }
}
