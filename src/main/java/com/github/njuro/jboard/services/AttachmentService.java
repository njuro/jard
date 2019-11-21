package com.github.njuro.jboard.services;

import com.github.njuro.jboard.helpers.Constants;
import com.github.njuro.jboard.models.Attachment;
import com.github.njuro.jboard.repositories.AttachmentRepository;
import com.github.njuro.jboard.utils.Images;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public Attachment saveAttachment(Attachment attachment) {
        if (attachment.getSourceFile() != null) {
            Path path = Paths.get(attachment.getPath(), attachment.getFilename());
            try {
                File destFile = Constants.USER_CONTENT_PATH.resolve(path).toFile();
                destFile.getParentFile().mkdirs();
                attachment.getSourceFile().transferTo(destFile);
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot save to destination " + path.toString(), e);
            }
        }

        Images.setDimensions(attachment);
        return attachmentRepository.save(attachment);
    }

}