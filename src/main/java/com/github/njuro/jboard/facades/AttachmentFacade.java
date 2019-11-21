package com.github.njuro.jboard.facades;

import com.github.njuro.jboard.models.Attachment;
import com.github.njuro.jboard.models.Board;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@Component
public class AttachmentFacade {

    public Attachment createAttachment(MultipartFile file, Board board) {
        return createAttachment(file, Paths.get(board.getLabel()));
    }

    public Attachment createAttachment(MultipartFile file, Path destination) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String generatedName = Instant.now().toEpochMilli() + "." + ext.toLowerCase();
        Attachment attachment = new Attachment(destination.toString(), file.getOriginalFilename(), generatedName);

        attachment.setSourceFile(file);
        return attachment;

    }


}
