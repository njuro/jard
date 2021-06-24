package com.github.njuro.jard.attachment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.MediaType;

/** Enum representing different categories of {@link Attachment} (more precisely of their files). */
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
      new MediaType("audio", "mpeg3", extension("mp3")),
      new MediaType("audio", "x-mpeg-3", extension("mp3")),
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
          extension("docx"))),
  EMBED(true);

  /** Name of special parameter to store file extension related to given MIME type */
  private static final String EXTENSION_PARAM = "extension";

  /** Whether we can generate and store thumbnail for this category. */
  private final boolean thubmnail;

  /** Collection of MIME types (wrapped by {@link MediaType}) belonging to this category. */
  @Getter private final Set<MediaType> mediaTypes;

  /** A {@link Preview} of this attachment category. */
  @Getter private final Preview preview;

  AttachmentCategory(boolean thubmnail, MediaType... mediaTypes) {
    this.thubmnail = thubmnail;
    this.mediaTypes = Set.of(mediaTypes);
    preview = generatePreview();
  }

  public boolean hasThumbnail() {
    return thubmnail;
  }

  /** @return {@link Preview} of this attachment category. */
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

  /** Adds parameter specifying file extension related to given MIME type. */
  private static Map<String, String> extension(String extension) {
    return Collections.singletonMap(EXTENSION_PARAM, extension);
  }

  /**
   * Determines category the given MIME type belongs to.
   *
   * @param mimeType MIME type to determine
   * @return determined category, or {@code null} if such category was not found
   */
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

  /** Client-friendly view (DTO) of {@link AttachmentCategory}. */
  @Builder
  @Getter
  @EqualsAndHashCode(onlyExplicitlyIncluded = true)
  @ToString(onlyExplicitlyIncluded = true)
  public static class Preview {

    /** Name of the attachment category. */
    @EqualsAndHashCode.Include @ToString.Include private final String name;

    /** Whether we can generate and store thumbnail for this category. */
    private final boolean hasThumbnail;

    /** File extensions associated with this category, e.g. {@code .jpg, .png}. */
    private final Set<String> extensions;

    /** MIME types associated with this category, e.g. {@code image/jpeg}. */
    private final Set<String> mimeTypes;
  }

  /** Custom JSON serializer to serialize {@link AttachmentCategory} as {@link Preview}. */
  public static class AttachmentCategorySerializer extends JsonSerializer<AttachmentCategory> {

    @Override
    public void serialize(
        AttachmentCategory value, JsonGenerator generator, SerializerProvider serializers)
        throws IOException {
      generator.writeObject(value.preview);
    }
  }

  /** Custom JSON deserializer to deserialize {@link Preview} as {@link AttachmentCategory} */
  public static class AttachmentCategoryDeserializer extends JsonDeserializer<AttachmentCategory> {

    @Override
    public AttachmentCategory deserialize(JsonParser parser, DeserializationContext context)
        throws IOException {
      var preview = parser.readValueAs(Preview.class);
      return AttachmentCategory.valueOf(preview.getName());
    }
  }
}
