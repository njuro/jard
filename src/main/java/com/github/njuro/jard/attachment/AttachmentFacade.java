package com.github.njuro.jard.attachment;

import ac.simons.oembed.OembedException;
import com.github.njuro.jard.attachment.dto.AttachmentDto;
import com.github.njuro.jard.attachment.embedded.EmbedService;
import com.github.njuro.jard.base.BaseFacade;
import com.github.njuro.jard.board.Board;
import com.github.njuro.jard.board.BoardFacade;
import com.github.njuro.jard.board.dto.BoardDto;
import com.github.njuro.jard.utils.validation.PropertyValidationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class AttachmentFacade extends BaseFacade<Attachment, AttachmentDto> {

  private final BoardFacade boardFacade;

  private final AttachmentService attachmentService;
  private final EmbedService embedService;
  private final Tika mimeTypeDetector;

  @Autowired
  public AttachmentFacade(
      BoardFacade boardFacade, AttachmentService attachmentService, EmbedService embedService) {
    this.boardFacade = boardFacade;
    this.attachmentService = attachmentService;
    this.embedService = embedService;
    mimeTypeDetector = new Tika();
  }

  /**
   * Creates and stores new attachment.
   *
   * @param file uploaded file
   * @param board {@link Board} to which the file was uploaded
   * @return created {@link Attachment}
   * @throws PropertyValidationException if MIME type of uploaded file is not supported on given
   *     board or saving of attachment fails
   */
  public AttachmentDto createAttachment(MultipartFile file, BoardDto board) {
    String mimeType = detectMimeType(file);
    if (!boardFacade.isMimeTypeSupported(board, mimeType)) {
      throw new PropertyValidationException("Uploaded file mime type not supported on this board");
    }

    return createAttachment(file, mimeType, Paths.get(board.getLabel()));
  }

  /**
   * Creates and stores new attachment.
   *
   * @param file uploaded file
   * @param folder path to folder where uploaded file should be stored
   * @return created {@link Attachment}
   * @throws PropertyValidationException if saving of attachment fails
   */
  public AttachmentDto createAttachment(MultipartFile file, Path folder) {
    return createAttachment(file, detectMimeType(file), folder);
  }

  /**
   * Creates and stores new attachment.
   *
   * @param file uploaded file
   * @param mimeType MIME type of uploaded file
   * @param folder path to folder where uploaded file should be stored
   * @return created {@link Attachment}
   * @throws PropertyValidationException if saving of attachment fails or filename has no extension
   */
  private AttachmentDto createAttachment(MultipartFile file, String mimeType, Path folder) {
    String ext = FilenameUtils.getExtension(file.getOriginalFilename());
    if (ext == null || ext.isEmpty()) {
      throw new PropertyValidationException("Name of uploaded file must have an extension");
    }

    String generatedName = Instant.now().toEpochMilli() + "." + ext.toLowerCase();
    var attachment =
        Attachment.builder()
            .originalFilename(file.getOriginalFilename())
            .filename(generatedName)
            .folder(folder.toString())
            .metadata(new AttachmentMetadata())
            .build();
    attachment.getMetadata().setMimeType(mimeType);

    try {
      return toDto(attachmentService.saveAttachment(attachment, file));
    } catch (IOException ex) {
      log.error("Saving of attachment failed", ex);
      throw new PropertyValidationException("Saving of attachment failed");
    }
  }

  /**
   * Detects MIME type of given file.
   *
   * @param file file to detect MIME type of
   * @return detected MIME type or {@code null} if none was found
   */
  private String detectMimeType(MultipartFile file) {
    try {
      return mimeTypeDetector.detect(file.getInputStream());
    } catch (IOException ex) {
      log.error("Failed to detect mime type: " + ex.getMessage());
      return null;
    }
  }

  /**
   * Creates and stores new embedded attachment.
   *
   * @param embedUrl URL of content to be embedded.
   * @param board {@link Board} to which the file was uploaded
   * @return created {@link Attachment}
   * @throws PropertyValidationException if embedded attachments are not allowed on given board
   */
  public AttachmentDto createEmbeddedAttachment(String embedUrl, BoardDto board) {
    if (!board.getSettings().getAttachmentCategories().contains(AttachmentCategory.EMBED)) {
      throw new PropertyValidationException("Embedded attachments are not allowed for this board");
    }

    var attachment = Attachment.builder().category(AttachmentCategory.EMBED).build();
    try {
      embedService.processEmbedded(embedUrl, attachment);
    } catch (IllegalArgumentException | OembedException ex) {
      log.error("Processing of embedded attachment failed", ex);
      throw new PropertyValidationException("Processing of embedded attachment failed");
    }

    return toDto(attachmentService.saveEmbeddedAttachment(attachment));
  }
}
