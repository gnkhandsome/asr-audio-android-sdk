package com.sogou.audiosource;

/**
 * Created by zhouqilin on 16/9/23.
 */

public interface IAudioDataProviderFactory {
    IAudioDataProvider create();

    int samplingRateInHz();
    int bufferSizeInBytes();
    int bytesPerFrame();
}
