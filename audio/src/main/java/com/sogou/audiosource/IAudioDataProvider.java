// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
package com.sogou.audiosource;

/**
 * Created by zhouqilin on 16/9/23.
 */

public interface IAudioDataProvider {
    int read(short[] buffer, int offset, int len);
    int read(byte[] buffer, int offset, int len);

    boolean isInitialized();
    void start();
    void stop();
    void release();

    void reinitRecorder();
    void setCallbacks(Runnable scoConnected, Runnable wireMicInput);
    boolean isReady();
}