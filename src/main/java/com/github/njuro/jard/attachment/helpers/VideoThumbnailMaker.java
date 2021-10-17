package com.github.njuro.jard.attachment.helpers;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.MediaDescriptor.Type;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.awt.MediaPictureConverterFactory;
import java.awt.image.BufferedImage;
import java.io.IOException;
import lombok.experimental.UtilityClass;

/** Utility class with methods for making images from video files. */
@UtilityClass
public class VideoThumbnailMaker {

  private Demuxer demuxer;

  /**
   * Gets image from video file.
   *
   * @param pathToFile full path to video file
   * @return image from first complete frame of given video file
   * @throws IOException if opening video file or getting image fails
   */
  public BufferedImage getImageFromVideo(String pathToFile)
      throws IOException, InterruptedException {

    demuxer = Demuxer.make();
    demuxer.open(pathToFile, null, false, true, null, null);

    for (int i = 0; i < demuxer.getNumStreams(); i++) {
      var decoder = demuxer.getStream(i).getDecoder();
      if (decoder != null && decoder.getCodecType() == Type.MEDIA_VIDEO) {
        return getImageFromVideoStream(decoder, i);
      }
    }

    demuxer.close();
    throw new IOException("Failed to get image from video file");
  }

  /**
   * Retrieves image from given video stream.
   *
   * @param decoder decoder for given stream
   * @param streamIndex index of given stream
   * @return image from first complete frame of given stream
   * @throws IOException if getting image fails
   */
  private BufferedImage getImageFromVideoStream(Decoder decoder, int streamIndex)
      throws IOException, InterruptedException {
    decoder.open(null, null);
    var picture =
        MediaPicture.make(decoder.getWidth(), decoder.getHeight(), decoder.getPixelFormat());
    var converter =
        MediaPictureConverterFactory.createConverter(
            MediaPictureConverterFactory.HUMBLE_BGR_24, picture);

    var packet = MediaPacket.make();
    while (demuxer.read(packet) >= 0) {
      if (packet.getStreamIndex() != streamIndex) {
        continue;
      }
      int offset = 0;
      int bytesRead = 0;
      decoder.decodeVideo(picture, packet, offset);
      do {
        bytesRead += decoder.decode(picture, packet, offset);
        if (picture.isComplete()) {
          demuxer.close();
          return converter.toImage(null, picture);
        }
        offset += bytesRead;

      } while (offset < packet.getSize());
    }

    throw new IOException("Failed to get image from video file");
  }
}
