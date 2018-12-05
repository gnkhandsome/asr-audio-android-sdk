package com.sogou.audiosource;

/**
 * Created by zhouqilin on 16/9/22.
 */

public interface IAudioSource {
    void addAudioSourceListener(IAudioSourceListener listener);
    void removeAudioSourceListener(IAudioSourceListener listener);
    void clearAudioSourceListeners();

    int bytesPerSecond();

    int start();
    int pause();
    int stop();
}
