package com.eointm.radiopirate.job.ffmpeg.provider;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class FFmpegDownloader {

  private static final Logger LOG = LoggerFactory.getLogger(FFmpegDownloader.class);

  private static final String FFMPEG_BINARY_BASE_URL =
    "https://github.com/ffbinaries/ffbinaries-prebuilt/releases/download/v4.2.1/ffmpeg-4.2.1-";
  private static final String ZIPPED_FOLDER_FILE_EXTENSION = ".zip";

  @VisibleForTesting
  static final String ZIPPED_FFMPEG_PATH = "./temp/ffmpeg.zip";

  // Only providing 64-bit for now because 32-bit is more or less deprecated.
  // This may change in the future though.
  @VisibleForTesting
  static final String SYSTEM_CODE_LINUX = "linux-64";

  @VisibleForTesting
  static final String SYSTEM_CODE_MAC = "osx-64";

  @VisibleForTesting
  static final String SYSTEM_CODE_WINDOWS = "win-64";

  public void downloadFFMpegBinary(File ffmpegOutputLocation)
    throws UnknownEnvironmentException, IOException {
    String systemCode = getSystemCode()
      .orElseThrow(
        () -> {
          LOG.error(
            "Couldn't determine which OS this is running on for the purpose of creating the FFMpeg download URL."
          );
          return new UnknownEnvironmentException(
            "Couldn't determine which OS this JVM is running on. Perhaps this OS isn't supported by RadioPirate?"
          );
        }
      );

    String downloadUrl = String.format(
      "%s%s%s",
      FFMPEG_BINARY_BASE_URL,
      systemCode,
      ZIPPED_FOLDER_FILE_EXTENSION
    );

    File zipFile = new File(ZIPPED_FFMPEG_PATH);

    FileUtils.copyURLToFile(new URL(downloadUrl), zipFile);

    try {
      new ZipFile(zipFile)
        .extractFile(
          ffmpegOutputLocation.getName(),
          ffmpegOutputLocation.getParentFile().getPath()
        );
    } catch (ZipException e) {
      LOG.error("An error occured while unzipping FFmpeg", e);
      throw e;
    }

    if (!ffmpegOutputLocation.exists()) {
      LOG.error("An unknown exception occurred while downloading FFmpeg.");
      throw new RuntimeException("FFmpeg not present after attempted download");
    }

    boolean tempDeletedSuccessfully = new File(ZIPPED_FFMPEG_PATH).delete();

    if (!tempDeletedSuccessfully) {
      LOG.warn("Failed to clean up download.");
    }
  }

  @VisibleForTesting
  Optional<String> getSystemCode() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return Optional.of(SYSTEM_CODE_WINDOWS);
    } else if (SystemUtils.IS_OS_MAC) {
      return Optional.of(SYSTEM_CODE_MAC);
    } else if (SystemUtils.IS_OS_LINUX) {
      return Optional.of(SYSTEM_CODE_LINUX);
    } else {
      return Optional.empty();
    }
  }
}
