package com.github.njuro.jboard.attachment;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Paths;
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
      ImageUtils.setDimensions(attachment);

      if (storageMode == UserContentStorageMode.AWS) {
        awsFileService.uploadFile(
            Paths.get(attachment.getPath(), attachment.getFilename()).toString(),
            attachment.getFile());
      }

      saveAttachmentThumbnail(attachment);
    } catch (IOException ex) {
      log.error("Failed to save attachment: " + ex.getMessage());
    }

    return attachmentRepository.save(attachment);
  }

  private void saveAttachmentThumbnail(Attachment attachment) throws IOException {
    attachment.getThumbnailFile().getParentFile().mkdirs();
    RenderedImage thumbnail = ImageUtils.createThumbnail(attachment);
    ImageIO.write(
        thumbnail,
        FilenameUtils.getExtension(attachment.getFilename()),
        attachment.getThumbnailFile());

    if (storageMode == UserContentStorageMode.AWS) {
      awsFileService.uploadFile(
          Paths.get(attachment.getThumbnailPath(), attachment.getFilename()).toString(),
          attachment.getThumbnailFile());
    }
  }

  public void deleteAttachmentFile(Attachment attachment) {
    if (storageMode == UserContentStorageMode.AWS) {
      awsFileService.deleteFile(attachment.getPath());
      awsFileService.deleteFile(attachment.getThumbnailPath());
    }

    if (!attachment.getFile().delete()) {
      log.error("Failed to delete attachment");
    }
  }

  public void deleteAttachmentFiles(List<Attachment> attachments) {
    attachments.forEach(this::deleteAttachmentFile);
  }
}
