// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
package com.sogou.audiosource;

/**
 * Created by zhouqilin on 16/9/22.
 */

public interface IAudioSourceListener {
    /**
     * Flag indicating end of one session.
     * Eg. Recoding stopped or exceeds max recording time
     */
    int AUDIO_DATA_FLAG_SESSION_END = 1;

    void onBegin(IAudioSource audioSource);

    /**
     * Called when new data is ready
     * @param audioSource Audio data source
     * @param dataArray short[] or byte[] contains raw audio data
     * @param packIndex the sequence number of raw audio data
     * @param sampleIndex the sequence number of the first sample in dataArray.
     *                    total sample count = sampleIndex + dataArray.length
     * @param flag the data flag. Eg. AUDIO_DATA_FLAG_SESSION_END means end of one session
     */
    void onNewData(IAudioSource audioSource, Object dataArray, long packIndex, long sampleIndex, int flag);
    void onEnd(IAudioSource audioSource, int status, Exception e, long sampleCount);
}