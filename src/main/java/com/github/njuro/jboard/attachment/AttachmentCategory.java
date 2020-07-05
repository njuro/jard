package com.github.njuro.jboard.attachment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.MediaType;

public enum AttachmentCategory {
  IMAGE(
      true,
      MediaType.IMAGE_PNG,
      MediaType.IMAGE_GIF,
      new MediaType("image", "jpeg", extension("jpg")),
      new MediaType("image", "bmp"),
      new MediaType("image", "x-bmp", extension("bmp")),
      new MediaType("image", "svg+xml", extension("svg"))),
  VIDEO(
      true,
      new MediaType("video", "webm"),
      new MediaType("video", "mp4"),
      new MediaType("application", "mp4"),
      new MediaType("video", "quicktime", extension("mp4")),
      new MediaType("video", "x-matroska", extension("mkv")),
      new MediaType("video", "avi"),
      new MediaType("video", "msvideo", extension("avi")),
      new MediaType("video", "x-msvideo", extension("avi")),
      new MediaType("video", "vnd.avi", extension("avi"))),
  AUDIO(
      false,
      new MediaType("audio", "mpeg", extension("mp3")),
      new MediaType("audio", "MPA", extension("mp3")),
      new MediaType("audio", "mpa-robust", extension("mp3")),
      new MediaType("audio", "wav"),
      new MediaType("audio", "vnd.wave", extension("wav")),
      new MediaType("audio", "wave", extension("wave")),
      new MediaType("audio", "x-wav", extension("wav")),
      new MediaType("audio", "ogg"),
      new MediaType("application", "ogg")),
  PDF(
      true,
      MediaType.APPLICATION_PDF,
      new MediaType("application", "x-pdf", extension("pdf")),
      new MediaType("application", "x-bzpdf", extension("pdf")),
      new MediaType("application", "x-gzpdf", extension("pdf"))),
  TEXT(
      false,
      new MediaType("text", "plain", extension("txt")),
      new MediaType("application", "msword", extension("doc")),
      new MediaType(
          "application",
          "vnd.openxmlformats-officedocument.wordprocessingml.document",
          extension("docx")));

  private static final String EXTENSION_PARAM = "extension";

  private final boolean thubmnail;
  @Getter private final Set<MediaType> mediaTypes;
  @Getter private final Preview preview;

  AttachmentCategory(boolean thubmnail, MediaType... mediaTypes) {
    this.thubmnail = thubmnail;
    this.mediaTypes = Set.of(mediaTypes);
    preview = generatePreview();
  }

  public boolean hasThumbnail() {
    return thubmnail;
  }

  private AttachmentCategory.Preview generatePreview() {
    return Preview.builder()
        .name(name())
        .hasThumbnail(hasThumbnail())
        .mimeTypes(
            getMediaTypes().stream()
                .map(mediaType -> mediaType.toString().split(";")[0])
                .collect(Collectors.toSet()))
        .extensions(
            getMediaTypes().stream()
                .map(
                    mediaType ->
                        "."
                            + Optional.ofNullable(mediaType.getParameter(EXTENSION_PARAM))
                                .orElse(mediaType.getSubtype()))
                .collect(Collectors.toSet()))
        .build();
  }

  private static Map<String, String> extension(String extension) {
    return Collections.singletonMap(EXTENSION_PARAM, extension);
  }

  public static AttachmentCategory determineAttachmentCategory(String mimeType) {
    return Arrays.stream(AttachmentCategory.values())
        .filter(
            attachmentCategory ->
                attachmentCategory.getMediaTypes().stream()
                    .map(MediaType::toString)
                    .anyMatch(mediaType -> mediaType.startsWith(mimeType)))
        .findAny()
        .orElse(null);
  }

  @Builder
  @Getter
  public static class Preview {

    private final String name;
    private final boolean hasThumbnail;
    private final Set<String> extensions;
    private final Set<String> mimeTypes;
  }

  public static class AttachmentCategorySerializer extends JsonSerializer<AttachmentCategory> {

    @Override
    public void serialize(
        AttachmentCategory value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeObject(value.preview);
    }
  }
}
