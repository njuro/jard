package com.github.njuro.jboard.attachment;

import static com.github.njuro.jboard.common.Constants.DEFAULT_THUMBNAIL_EXTENSION;
import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR_STR;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service methods for manipulating {@link Attachment file attachments}
 *
 * @author njuro
 */
@Service
@Transactional
@Slf4j
public class AttachmentService {

  @Value("${app.user.content.storage:LOCAL}")
  private UserContentStorageMode storageMode;

  private final AWSFileService awsFileService;
  private final AttachmentRepository attachmentRepository;

  @Autowired
  public AttachmentService(
      AWSFileService awsFileService, AttachmentRepository attachmentRepository) {
    this.awsFileService = awsFileService;
    this.attachmentRepository = attachmentRepository;
  }

  public Attachment saveAttachment(Attachment attachment, MultipartFile source) {
    try {
      attachment.getFile().getParentFile().mkdirs();
      source.transferTo(attachment.getFile());
      AttachmentUtils.setMetadata(attachment);

      if (storageMode == UserContentStorageMode.AWS) {
        String url =
            awsFileService.uploadFile(
                attachment.getFolder(), attachment.getFilename(), attachment.getFile());
        attachment.setAwsUrl(url);
      }

      if (attachment.getType().hasThumbnail()) {
        saveAttachmentThumbnail(attachment);
      }
    } catch (IOException ex) {
      log.error("Failed to save attachment: " + ex.getMessage());
    }

    attachment.getMetadata().setAttachment(attachment);
    return attachmentRepository.save(attachment);
  }

  private void saveAttachmentThumbnail(Attachment attachment) throws IOException {
    String extension =
        attachment.getType() == AttachmentType.IMAGE
            ? FilenameUtils.getExtension(attachment.getFilename())
            : DEFAULT_THUMBNAIL_EXTENSION;
    attachment.setThumbnailFilename(
        FilenameUtils.removeExtension(attachment.getFilename())
            + EXTENSION_SEPARATOR_STR
            + extension);
    attachment.getThumbnailFile().getParentFile().mkdirs();
    RenderedImage thumbnail = AttachmentUtils.createThumbnail(attachment);

    ImageIO.write(thumbnail, extension, attachment.getThumbnailFile());

    if (storageMode == UserContentStorageMode.AWS) {
      String url =
          awsFileService.uploadFile(
              attachment.getThumbnailFolder(),
              attachment.getFilename(),
              attachment.getThumbnailFile());
      attachment.setAwsThumbnailUrl(url);
    }
  }

  public void deleteAttachmentFile(Attachment attachment) {
    if (storageMode == UserContentStorageMode.AWS) {
      awsFileService.deleteFile(attachment.getFolder(), attachment.getFilename());
      awsFileService.deleteFile(attachment.getThumbnailFolder(), attachment.getFilename());
    }

    if (!attachment.getFile().delete()) {
      log.error("Failed to delete attachment file");
    }

    if (!attachment.getThumbnailFile().delete()) {
      log.error("Failed to delete attachment thumbnail");
    }
  }

  public void deleteAttachmentFiles(List<Attachment> attachments) {
    attachments.forEach(this::deleteAttachmentFile);
  }
}
