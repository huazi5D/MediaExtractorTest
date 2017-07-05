package hz.mediaextractortest;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import hz.mediaextractortest.utils.SDCardUtils;

/**
 * Created by Administrator on 2017/7/5 0005.
 */

public class MediaExtractorManager {

    private MediaExtractor mMediaExtractor;
    private MediaMuxer mMediaMuxer;

    public MediaExtractorManager() {
    }

    public void exactorMedia() {
        try {
            mMediaExtractor = new MediaExtractor();

            FileOutputStream audioOutputStream = null;

            File audioFile = new File(SDCardUtils.getSDCardPath() + ".123/audio.mp3");

            audioOutputStream = new FileOutputStream(audioFile);

            mMediaExtractor.setDataSource(SDCardUtils.getSDCardPath() + "love.mp4");

            int trackCount = mMediaExtractor.getTrackCount();
            int audioTrackIndex = -1;
            int videoTrackIndex = -1;
            for (int i = 0; i < trackCount; i ++) {
                MediaFormat trackFormat = mMediaExtractor.getTrackFormat(i);
                String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                if (mineType.startsWith("video/")) {
                    videoTrackIndex = i;
                }

                if (mineType.startsWith("audio/")) {
                    audioTrackIndex = i;
                }
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);

            mMediaExtractor.selectTrack(videoTrackIndex);

            //合成器

            mMediaMuxer = new MediaMuxer(SDCardUtils.getSDCardPath() + ".123/video.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int trackIndex = mMediaMuxer.addTrack(mMediaExtractor.getTrackFormat(videoTrackIndex));
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mMediaMuxer.start();
            long videoSampleTime;
            //获取每帧的之间的时间
            {
                mMediaExtractor.readSampleData(byteBuffer, 0);
                //skip first I frame
                if (mMediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC)
                    mMediaExtractor.advance();
                mMediaExtractor.readSampleData(byteBuffer, 0);
                long firstVideoPTS = mMediaExtractor.getSampleTime();
                mMediaExtractor.advance();
                mMediaExtractor.readSampleData(byteBuffer, 0);
                long SecondVideoPTS = mMediaExtractor.getSampleTime();
                videoSampleTime = Math.abs(SecondVideoPTS - firstVideoPTS);
                Log.d("fuck", "videoSampleTime is " + videoSampleTime);
            }
            //重新切换此信道，不然上面跳过了3帧,造成前面的帧数模糊
            mMediaExtractor.unselectTrack(videoTrackIndex);
            mMediaExtractor.selectTrack(videoTrackIndex);
            while (true) {
                int videoSize = mMediaExtractor.readSampleData(byteBuffer, 0);
                if (videoSize < 0) {
                    break;
                }
                mMediaExtractor.advance();
                bufferInfo.size = videoSize;
                bufferInfo.offset = 0;
                bufferInfo.flags = mMediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs += videoSampleTime;
                mMediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo);

                /*byte[] buffer = new byte[videoSize];
                byteBuffer.get(buffer);
                videoOutputStream.write(buffer);
                byteBuffer.clear();
                mMediaExtractor.advance();*/
            }

            mMediaExtractor.selectTrack(audioTrackIndex);
            while (true) {
                int audioSize = mMediaExtractor.readSampleData(byteBuffer, 0);
                if (audioSize < 0) {
                    break;
                }

                byte[] buffer = new byte[audioSize];
                byteBuffer.get(buffer);
                audioOutputStream.write(buffer);
                byteBuffer.clear();
                mMediaExtractor.advance();
            }

            mMediaMuxer.stop();
            mMediaMuxer.release();
            mMediaExtractor.release();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
