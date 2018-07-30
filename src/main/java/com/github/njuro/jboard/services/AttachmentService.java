package com.github.njuro.jboard.services;

import com.github.njuro.jboard.helpers.Constants;
import com.github.njuro.jboard.models.Attachment;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.repositories.AttachmentRepository;
import com.github.njuro.jboard.utils.Images;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * Service methods for manipulating {@link Attachment file attachments}
 *
 * @author njuro
 */
@Service
@Transactional
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    /**
     * Saves attachment to folder denoted by board's label
     *
     * @param src   uploaded file
     * @param board to which was file uploaded
     * @return saved attachment
     * @throws IllegalArgumentException if saving fails
     * @see #saveAttachment(MultipartFile, Path)
     */
    public Attachment saveAttachment(MultipartFile src, Board board) {
        return saveAttachment(src, Paths.get(board.getLabel()));
    }

    /**
     * Saves attachment to folder denoted by given path. Also generates new filename based on current UNIX time
     * and set dimensions of attachment (if it is an image)
     *
     * @param src  uploaded file
     * @param dest path where to save attachment
     * @return saved attachment
     * @throws IllegalArgumentException if saving fails
     */
    public Attachment saveAttachment(MultipartFile src, Path dest) {

        String ext = FilenameUtils.getExtension(src.getOriginalFilename());
        String generatedName = Instant.now().toEpochMilli() + "." + ext.toLowerCase();
        Path path = Paths.get(dest.toString(), generatedName);

        try {
            File destFile = Constants.USER_CONTENT_PATH.resolve(path).toFile();
            destFile.getParentFile().mkdirs();
            src.transferTo(destFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot save to destination " + path.toString(), e);
        }

        Attachment attachment = new Attachment(dest.toString(), src.getOriginalFilename(), generatedName);
        Images.setDimensions(attachment);

        return attachmentRepository.save(attachment);
    }

}