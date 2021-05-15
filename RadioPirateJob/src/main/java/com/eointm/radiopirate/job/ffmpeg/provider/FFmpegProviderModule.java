package com.eointm.radiopirate.job.ffmpeg.provider;

import com.google.inject.AbstractModule;
import net.bramp.ffmpeg.FFmpeg;

public class FFmpegProviderModule extends AbstractModule {

  public static final String FFMPEG_INSTANCE = "radiopirate.ffmpeg";

  @Override
  protected void configure() {
    bind(FFmpeg.class).toProvider(FFmpegProvider.class).asEagerSingleton();
  }
}
