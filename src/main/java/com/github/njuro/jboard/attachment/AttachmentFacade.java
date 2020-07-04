package com.github.njuro.jboard.attachment;

import com.github.njuro.jboard.board.Board;
import com.github.njuro.jboard.board.BoardFacade;
import com.github.njuro.jboard.utils.validation.FormValidationException;
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
public class AttachmentFacade {

  private final BoardFacade boardFacade;
  private final AttachmentService attachmentService;
  private final Tika mimeTypeDetector;

  @Autowired
  public AttachmentFacade(BoardFacade boardFacade, AttachmentService attachmentService) {
    this.boardFacade = boardFacade;
    this.attachmentService = attachmentService;
    mimeTypeDetector = new Tika();
  }

  public Attachment createAttachment(MultipartFile file, Board board) {
    String mimeType = detectMimeType(file);
    if (!boardFacade.isMimeTypeSupported(board, mimeType)) {
      throw new FormValidationException("Uploaded file mime type not supported on this board");
    }

    return createAttachment(file, mimeType, Paths.get(board.getLabel()));
  }

  public Attachment createAttachment(MultipartFile file, Path folder) {
    return createAttachment(file, detectMimeType(file), folder);
  }

  public Attachment createAttachment(MultipartFile file, String mimeType, Path folder) {
    String ext = FilenameUtils.getExtension(file.getOriginalFilename());
    String generatedName = Instant.now().toEpochMilli() + "." + ext.toLowerCase();
    Attachment attachment =
        Attachment.builder()
            .originalFilename(file.getOriginalFilename())
            .filename(generatedName)
            .folder(folder.toString())
            .build();
    attachment.getMetadata().setMimeType(mimeType);

    return attachmentService.saveAttachment(attachment, file);
  }

  private String detectMimeType(MultipartFile file) {
    try {
      return mimeTypeDetector.detect(file.getInputStream());
    } catch (IOException ex) {
      log.error("Failed to detect mime type: " + ex.getMessage());
      return null;
    }
  }
}
