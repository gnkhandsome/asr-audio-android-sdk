// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
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