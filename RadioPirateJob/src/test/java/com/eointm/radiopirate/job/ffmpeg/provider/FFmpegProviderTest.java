package com.eointm.radiopirate.job.ffmpeg.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.bramp.ffmpeg.FFmpeg;

@ExtendWith(MockitoExtension.class)
public class FFmpegProviderTest {
  private static final String EXPECTED_FILE_PATH_OTHER = String.format("%s/%s", FFmpegProvider.BIN_FOLDER_PATH, FFmpegProvider.FILE_NAME_OTHER);
  private static final String EXPECTED_FILE_PATH_WINDOWS = String.format("%s/%s", FFmpegProvider.BIN_FOLDER_PATH, FFmpegProvider.FILE_NAME_WINDOWS);

  @Mock
  private FFmpeg ffmpegMock;

  @Mock
  private FFmpegDownloader ffmpegDownloaderMock;

  @Mock
  private File fileMock;

  private FFmpegProvider ffmpegProviderMock;

  @BeforeEach
  public void setup() {
    ffmpegProviderMock = mock(FFmpegProvider.class, withSettings().useConstructor(ffmpegDownloaderMock));
  }

  @Test
  public void itProvidesTheCorrectExpectedFFmpegLocationOnMacAndLinux() {
    when(ffmpegProviderMock.getFileName()).thenReturn(Optional.of(FFmpegProvider.FILE_NAME_OTHER));
    when(ffmpegProviderMock.getExpectedFFmpegLocation()).thenCallRealMethod();

    assertThat(ffmpegProviderMock.getExpectedFFmpegLocation()).isEqualTo(EXPECTED_FILE_PATH_OTHER);
  }

  @Test
  public void itProvidesTheCorrectExpectedFFmpegLocationOnWindows() {
    when(ffmpegProviderMock.getFileName()).thenReturn(Optional.of(FFmpegProvider.FILE_NAME_WINDOWS));
    when(ffmpegProviderMock.getExpectedFFmpegLocation()).thenCallRealMethod();

    assertThat(ffmpegProviderMock.getExpectedFFmpegLocation()).isEqualTo(EXPECTED_FILE_PATH_WINDOWS);
  }

  @Test
  public void itSuccessfullyDownloadsAndReturnsAnFFmpegInstance() throws IOException {
    when(ffmpegProviderMock.asFile(anyString())).thenReturn(fileMock);
    when(ffmpegProviderMock.asFFmpeg(anyString())).thenReturn(ffmpegMock);
    when(ffmpegProviderMock.get()).thenCallRealMethod();
    when(ffmpegProviderMock.getExpectedFFmpegLocation()).thenReturn(EXPECTED_FILE_PATH_WINDOWS);
    when(fileMock.exists()).thenReturn(false);

    ffmpegProviderMock.get();

    verify(ffmpegProviderMock, times(1)).asFFmpeg(EXPECTED_FILE_PATH_WINDOWS);
  }

  @Test
  public void itThrowsOnFailedFFmpegInstantiation() throws IOException {
    when(ffmpegProviderMock.asFile(anyString())).thenReturn(fileMock);
    when(ffmpegProviderMock.asFFmpeg(anyString())).thenThrow(new IOException());
    when(fileMock.exists()).thenReturn(true);
    when(ffmpegProviderMock.get()).thenCallRealMethod();

    assertThatThrownBy(ffmpegProviderMock::get).isInstanceOf(RuntimeException.class);
  }

  @Test
  public void itThrowsOnFailedDownload() throws UnknownEnvironmentException, IOException {
    doThrow(IOException.class).when(ffmpegDownloaderMock).downloadFFMpegBinary(any());
    when(ffmpegProviderMock.asFile(anyString())).thenReturn(fileMock);
    when(fileMock.exists()).thenReturn(false);
    when(ffmpegProviderMock.get()).thenCallRealMethod();

    assertThatThrownBy(ffmpegProviderMock::get).isInstanceOf(RuntimeException.class);
  }
}
