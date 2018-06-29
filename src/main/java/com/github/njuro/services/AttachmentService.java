package com.github.njuro.services;

import com.github.njuro.models.Attachment;
import com.github.njuro.models.Board;
import helpers.Constants;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@Repository
interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}

@Service
@Transactional
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public Attachment uploadAttachment(MultipartFile src, Board board) {
        return uploadAttachment(src, Paths.get(board.getLabel()));
    }

    public Attachment uploadAttachment(MultipartFile src, Path dest) {

        String ext = FilenameUtils.getExtension(src.getOriginalFilename());
        String generatedName = Instant.now().toEpochMilli() + "." + ext.toLowerCase();
        Path path = Paths.get(dest.toString(), generatedName);

        try {
            File destFile = Constants.USER_CONTENT_PATH.resolve(path).toFile();
            destFile.getParentFile().mkdirs();
            src.transferTo(destFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot upload to destination " + path.toString(), e);
        }

        Attachment attachment = new Attachment(dest.toString(), src.getOriginalFilename(), generatedName);
        return attachmentRepository.save(attachment);
    }

}