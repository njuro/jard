package com.github.njuro.jboard.attachment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.MediaType;

public enum AttachmentType {
  IMAGE(
      true,
      MediaType.IMAGE_PNG,
      MediaType.IMAGE_GIF,
      new MediaType("image", "jpeg", alias("jpg")),
      new MediaType("image", "bmp"),
      new MediaType("image", "webp"),
      new MediaType("image", "svg+xml", alias("svg"))),
  VIDEO(
      true,
      new MediaType("video", "webm"),
      new MediaType("video", "mp4"),
      new MediaType("application", "mp4"),
      new MediaType("video", "x-matroska", alias("mkv")),
      new MediaType("video", "x-msvideo", alias("avi"))),
  AUDIO(
      false,
      new MediaType("audio", "mpeg", alias("mp3")), // .mp3
      new MediaType("audio", "wav"),
      new MediaType("audio", "ogg")),
  PDF(true, MediaType.APPLICATION_PDF),
  TEXT(
      false,
      new MediaType("text", "plain", alias("txt")),
      new MediaType("application", "msword", alias("doc")),
      new MediaType(
          "application",
          "vnd.openxmlformats-officedocument.wordprocessingml.document",
          alias("docx")));

  private static final String ALIAS_PARAM = "alias";

  private final boolean thubmnail;
  @Getter private final Set<MediaType> mediaTypes = new HashSet<>();
  @Getter private final Preview preview;

  AttachmentType(boolean thubmnail, MediaType... mediaTypes) {
    this.thubmnail = thubmnail;
    this.mediaTypes.addAll(Arrays.asList(mediaTypes));
    preview = generatePreview();
  }

  public boolean hasThumbnail() {
    return thubmnail;
  }

  private AttachmentType.Preview generatePreview() {
    return Preview.builder()
        .name(name())
        .mimeTypes(
            getMediaTypes().stream()
                .map(mediaType -> mediaType.toString().split(";")[0])
                .collect(Collectors.toSet()))
        .extensions(
            getMediaTypes().stream()
                .map(
                    mediaType ->
                        "."
                            + Optional.ofNullable(mediaType.getParameter(ALIAS_PARAM))
                                .orElse(mediaType.getSubtype()))
                .collect(Collectors.toSet()))
        .build();
  }

  private static Map<String, String> alias(String alias) {
    return Collections.singletonMap(ALIAS_PARAM, alias);
  }

  public static AttachmentType determineAttachmentType(String mimeType) {
    return Arrays.stream(AttachmentType.values())
        .filter(
            attachmentType ->
                attachmentType.getMediaTypes().stream()
                    .map(MediaType::toString)
                    .anyMatch(mediaType -> mediaType.startsWith(mimeType)))
        .findAny()
        .orElse(null);
  }

  @Builder
  @Getter
  public static class Preview {

    private final String name;
    private final Set<String> extensions;
    private final Set<String> mimeTypes;
  }

  public static class AttachmentTypeSerializer extends JsonSerializer<AttachmentType> {

    @Override
    public void serialize(AttachmentType value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeObject(value.preview);
    }
  }
}
