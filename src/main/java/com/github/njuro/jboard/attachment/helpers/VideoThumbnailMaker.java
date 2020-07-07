package com.github.njuro.jboard.attachment.helpers;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.MediaDescriptor.Type;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class VideoThumbnailMaker {

  private static Demuxer demuxer;

  public static BufferedImage getImageFromVideo(String inputFilename)
      throws IOException, InterruptedException {

    demuxer = Demuxer.make();
    demuxer.open(inputFilename, null, false, true, null, null);

    for (int i = 0; i < demuxer.getNumStreams(); i++) {
      Decoder decoder = demuxer.getStream(i).getDecoder();
      if (decoder != null && decoder.getCodecType() == Type.MEDIA_VIDEO) {
        return getImageFromVideoStream(decoder, i);
      }
    }

    demuxer.close();
    throw new IOException("Failed to get image from video file");
  }

  private static BufferedImage getImageFromVideoStream(Decoder decoder, int streamIndex)
      throws IOException, InterruptedException {
    decoder.open(null, null);
    MediaPicture picture =
        MediaPicture.make(decoder.getWidth(), decoder.getHeight(), decoder.getPixelFormat());
    MediaPictureConverter converter =
        MediaPictureConverterFactory.createConverter(
            MediaPictureConverterFactory.HUMBLE_BGR_24, picture);

    MediaPacket packet = MediaPacket.make();
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
