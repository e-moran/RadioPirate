package com.eointm.radiopirate.job;

import java.io.IOException;

import com.eointm.radiopirate.job.ffmpeg.provider.FFmpegProviderModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import net.bramp.ffmpeg.FFmpeg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RadioPirateCoreRunner {

  private static final Logger LOG = LoggerFactory.getLogger(RadioPirateCoreRunner.class);

  public static void main(String... args) {
    Injector injector = Guice.createInjector(new FFmpegProviderModule());
    RadioPirateCoreRunner runner = injector.getInstance(RadioPirateCoreRunner.class);
    runner.start();
  }

  private FFmpeg ffmpeg;

  @Inject
  public RadioPirateCoreRunner(FFmpeg ffmpeg) {
    this.ffmpeg = ffmpeg;
  }

  public void start() {
    try {
      if(!ffmpeg.isFFmpeg()) {
        throw new IOException("File wasn't working ffmpeg binary");
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to provide working ffmpeg binary");
    }
  }
}
