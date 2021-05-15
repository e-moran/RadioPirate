package com.eointm.radiopirate.job.ffmpeg.provider;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import net.bramp.ffmpeg.FFmpeg;
import org.apache.commons.lang3.SystemUtils;
import org.apache.tools.ant.types.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FFmpegProvider implements Provider<FFmpeg> {

  private static final Logger LOG = LoggerFactory.getLogger(FFmpegProvider.class);

  private final FFmpegDownloader ffmpegDownloader;

  @VisibleForTesting
  static final String BIN_FOLDER_PATH = "./bin";

  @VisibleForTesting
  static final String FILE_NAME_WINDOWS = "ffmpeg.exe";

  @VisibleForTesting
  static final String FILE_NAME_OTHER = "ffmpeg";

  @Inject
  public FFmpegProvider(FFmpegDownloader ffmpegDownloader) {
    this.ffmpegDownloader = ffmpegDownloader;
  }

  @Override
  public FFmpeg get() {
    File expectedFFmpegLocation = asFile(getExpectedFFmpegLocation());

    if (!expectedFFmpegLocation.exists()) {
      try {
        ffmpegDownloader.downloadFFMpegBinary(expectedFFmpegLocation);
      } catch (UnknownEnvironmentException | IOException e) {
        LOG.error("Cannot download FFmpeg.", e);

        throw new RuntimeException("Cannot download FFmpeg");
      }
    }

    try {
      return asFFmpeg(getExpectedFFmpegLocation());
    } catch (IOException e) {
      LOG.error("Cannot find FFmpeg after checking that its present.", e);
      throw new RuntimeException("Cannot find FFmpeg after checking that its present.");
    }
  }

  @VisibleForTesting
  FFmpeg asFFmpeg(String ffmpegLocation) throws IOException {
    return new FFmpeg(ffmpegLocation);
  }

  @VisibleForTesting
  File asFile(String fileLocation) {
    return new File(fileLocation);
  }

  @VisibleForTesting
  Optional<String> getFileName() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return Optional.of(FILE_NAME_WINDOWS);
    } else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
      return Optional.of(FILE_NAME_OTHER);
    } else {
      return Optional.empty();
    }
  }

  @VisibleForTesting
  String getExpectedFFmpegLocation() {
    return String.format("%s/%s", BIN_FOLDER_PATH, getFileName().orElseThrow());
  }
}
