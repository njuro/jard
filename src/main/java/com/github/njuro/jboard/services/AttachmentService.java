package com.github.njuro.jboard.services;

import com.github.njuro.jboard.helpers.Constants;
import com.github.njuro.jboard.models.Attachment;
import com.github.njuro.jboard.repositories.AttachmentRepository;
import com.github.njuro.jboard.utils.Images;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public AttachmentService(final AttachmentRepository attachmentRepository) {
    this.attachmentRepository = attachmentRepository;
  }

  public Attachment saveAttachment(final Attachment attachment) {
    if (attachment.getSourceFile() != null) {
      final Path path = Paths.get(attachment.getPath(), attachment.getFilename());
      try {
        final File destFile = Constants.USER_CONTENT_PATH.resolve(path).toFile();
        destFile.getParentFile().mkdirs();
        attachment.getSourceFile().transferTo(destFile);
      } catch (final IOException e) {
        throw new IllegalArgumentException("Cannot save to destination " + path.toString(), e);
      }
    }

    Images.setDimensions(attachment);
    return attachmentRepository.save(attachment);
  }

  public static void deleteAttachmentFile(final Attachment attachment) {
    if (!attachment.getFile().delete()) {
      throw new IllegalStateException("Attachment file could not be deleted");
    }
  }

  public static void deleteAttachmentFiles(final List<Attachment> attachments) {
    attachments.forEach(AttachmentService::deleteAttachmentFile);
  }
}
