package com.github.njuro.jboard.attachment;

import com.github.njuro.jboard.board.Board;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AttachmentFacade {

  private final AttachmentService attachmentService;

  @Autowired
  public AttachmentFacade(AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  public Attachment createAttachment(MultipartFile file, Board board) {
    return createAttachment(file, Paths.get(board.getLabel()));
  }

  public Attachment createAttachment(MultipartFile file, Path folder) {
    String ext = FilenameUtils.getExtension(file.getOriginalFilename());
    String generatedName = Instant.now().toEpochMilli() + "." + ext.toLowerCase();
    Attachment attachment =
        Attachment.builder()
            .originalFilename(file.getOriginalFilename())
            .filename(generatedName)
            .path(folder.toString())
            .build();

    return attachmentService.saveAttachment(attachment, file);
  }
}
