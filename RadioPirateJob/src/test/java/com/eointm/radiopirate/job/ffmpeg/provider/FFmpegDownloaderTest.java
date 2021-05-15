package com.eointm.radiopirate.job.ffmpeg.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class FFmpegDownloaderTest {

  private static final File WINDOWS_FFMPEG_DESTINATION = new File(
    String.format(
      "%s/%s",
      FFmpegProvider.BIN_FOLDER_PATH,
      FFmpegProvider.FILE_NAME_WINDOWS
    )
  );
  private static final File MAC_FFMPEG_DESTINATION = new File(
    String.format("%s/%s", FFmpegProvider.BIN_FOLDER_PATH, FFmpegProvider.FILE_NAME_OTHER)
  );
  private static final File LINUX_FFMPEG_DESTINATION = new File(
    String.format("%s/%s", FFmpegProvider.BIN_FOLDER_PATH, FFmpegProvider.FILE_NAME_OTHER)
  );

  private static final Logger LOG = LoggerFactory.getLogger(FFmpegDownloaderTest.class);

  @Mock
  FFmpegDownloader mockedFFmpegDownloader;

  private FFmpegDownloader ffMpegDownloader;

  @BeforeEach
  public void setup() {
    ffMpegDownloader = new FFmpegDownloader();
  }

  @AfterEach
  public void cleanup() {
    File tempFolder = new File(FFmpegDownloader.ZIPPED_FFMPEG_PATH).getParentFile();
    LOG.info(tempFolder.getAbsolutePath());

    if (!Objects.isNull(tempFolder.listFiles())) {
      boolean deletedAllInTemp = Arrays
          .stream(tempFolder.listFiles())
          .map(File::delete)
          .allMatch(Predicate.isEqual(true));
      if (deletedAllInTemp) {
        if (tempFolder.delete()) {
          LOG.info("Cleaned up temp successfully");
        }
      }
    }

    File binFolder = new File(FFmpegProvider.BIN_FOLDER_PATH);
    LOG.info(binFolder.getAbsolutePath());

    if(!Objects.isNull(tempFolder.listFiles())) {
      boolean deletedAllInBin = Arrays
          .stream(binFolder.listFiles())
          .map(File::delete)
          .allMatch(Predicate.isEqual(true));
      if (deletedAllInBin) {
        if (binFolder.delete()) {
          LOG.info("Cleaned up bin successfully");
        }
      }
    }
  }

  @Test
  public void itDownloadsWindowsFFMpegBinaryCorrectly() {
    FFmpegDownloader FFmpegDownloaderSpy = spy(FFmpegDownloader.class);
    when(FFmpegDownloaderSpy.getSystemCode())
      .thenReturn(Optional.of(FFmpegDownloader.SYSTEM_CODE_WINDOWS));

    try {
      FFmpegDownloaderSpy.downloadFFMpegBinary(WINDOWS_FFMPEG_DESTINATION);

      assertThat(WINDOWS_FFMPEG_DESTINATION.exists()).isTrue();
    } catch (UnknownEnvironmentException | IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void itDownloadsMacFFMpegBinaryCorrectly() {
    FFmpegDownloader FFmpegDownloaderSpy = spy(FFmpegDownloader.class);
    when(FFmpegDownloaderSpy.getSystemCode())
      .thenReturn(Optional.of(FFmpegDownloader.SYSTEM_CODE_MAC));

    try {
      FFmpegDownloaderSpy.downloadFFMpegBinary(MAC_FFMPEG_DESTINATION);

      assertThat(MAC_FFMPEG_DESTINATION.exists()).isTrue();
    } catch (UnknownEnvironmentException | IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void itDownloadsLinuxFFMpegBinaryCorrectly() {
    FFmpegDownloader FFmpegDownloaderSpy = spy(FFmpegDownloader.class);
    when(FFmpegDownloaderSpy.getSystemCode())
      .thenReturn(Optional.of(FFmpegDownloader.SYSTEM_CODE_LINUX));

    try {
      FFmpegDownloaderSpy.downloadFFMpegBinary(LINUX_FFMPEG_DESTINATION);

      assertThat(LINUX_FFMPEG_DESTINATION.exists()).isTrue();
    } catch (UnknownEnvironmentException | IOException e) {
      e.printStackTrace();
    }
  }
}
