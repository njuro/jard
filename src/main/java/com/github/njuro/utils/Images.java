package com.github.njuro.utils;

import com.github.njuro.models.Attachment;
import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.github.njuro.helpers.Constants.IMAGE_MAX_THUMB_HEIGHT;
import static com.github.njuro.helpers.Constants.IMAGE_MAX_THUMB_WIDTH;

@UtilityClass
public class Images {

    public void setDimensions(Attachment att) {
        BufferedImage img = getImageFromAttachment(att);
        if (img == null) return;

        att.setWidth(img.getWidth());
        att.setHeight(img.getHeight());

        setThumbnailDimensions(att);
    }

    private BufferedImage getImageFromAttachment(Attachment att) {
        if (att.getFile() == null) return null;

        try {
            return ImageIO.read(att.getFile());
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while reading image", e);
        }
    }

    private void setThumbnailDimensions(Attachment att) {
        Objects.requireNonNull(att);

        if (att.getWidth() == 0 || att.getHeight() == 0) {
            // set real dimensions first
            setDimensions(att);
        }

        if (att.getWidth() > IMAGE_MAX_THUMB_WIDTH || att.getHeight() > IMAGE_MAX_THUMB_HEIGHT) {
            double factor = Math.min(IMAGE_MAX_THUMB_WIDTH / att.getWidth(), IMAGE_MAX_THUMB_HEIGHT / att.getHeight());
            att.setThumbWidth(((int) Math.ceil(att.getWidth() * factor)) + 1);
            att.setThumbHeight(((int) Math.ceil(att.getHeight() * factor)) + 1);
        }
    }

}
