package com.github.njuro.jboard.attachment.helpers;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import java.awt.image.BufferedImage;
import lombok.Getter;

public class VideoThumbnailMaker {

  public static final double SECONDS_BETWEEN_FRAMES = 1;

  // The video stream index, used to ensure we display frames from one and
  // only one video stream from the media container.
  private static int videoStreamIndex = -1;

  // Time of last frame write
  private static long lastPtsWrite = Global.NO_PTS;

  public static final long MICRO_SECONDS_BETWEEN_FRAMES =
      (long) (Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES);

  public static BufferedImage getImageFromVideo(String inputFilename) {

    IMediaReader mediaReader = ToolFactory.makeReader(inputFilename);

    mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
    ImageSnapListener isListener = new ImageSnapListener();
    mediaReader.addListener(isListener);

    while (isListener.getImage() == null) {
      mediaReader.readPacket();
    }

    return isListener.getImage();
  }

  @Getter
  private static class ImageSnapListener extends MediaListenerAdapter {

    private BufferedImage image;

    @Override
    public void onVideoPicture(IVideoPictureEvent event) {

      if (event.getStreamIndex() != videoStreamIndex) {

        if (videoStreamIndex == -1) {
          videoStreamIndex = event.getStreamIndex();
        } else {
          return;
        }
      }

      if (lastPtsWrite == Global.NO_PTS) {
        lastPtsWrite = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;
      }

      if (event.getTimeStamp() - lastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES) {
        image = event.getImage();
        lastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
      }
    }
  }
}
