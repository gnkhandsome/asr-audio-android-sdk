// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
package com.sogou.audiosource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouqilin on 16/9/22.
 */

public abstract class AbstractAudioSource implements IAudioSource {
    private List<WeakReference<IAudioSourceListener>> mListeners = new ArrayList<WeakReference<IAudioSourceListener>>();

    @Override
    public void addAudioSourceListener(IAudioSourceListener listener) {
        synchronized (mListeners){
            mListeners.add(new WeakReference<>(listener));
        }
    }

    @Override
    public void removeAudioSourceListener(IAudioSourceListener listener) {
        synchronized (mListeners){
            int index = -1;
            final int cnt = mListeners.size();
            for (int i = 0; i < cnt; ++i){
                if (mListeners.get(i).get() == listener){
                    index = i;
                    break;
                }
            }
            if (index >= 0){
                mListeners.remove(index);
            }
        }
    }

    @Override
    public void clearAudioSourceListeners() {
        synchronized (mListeners){
            mListeners.clear();
        }
    }

    @Override
    public int bytesPerSecond() {
        return 0;
    }

    protected void fireOnBegin(){
        synchronized (mListeners){
            for (WeakReference<IAudioSourceListener> item:mListeners){
                IAudioSourceListener listener = item.get();
                if (listener != null){
                    try {
                        listener.onBegin(this);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected void fireOnNewData(Object dataArray, long packIndex, long sampleIndex, int flag){
        synchronized (mListeners){
            for (WeakReference<IAudioSourceListener> item:mListeners){
                IAudioSourceListener listener = item.get();
                if (listener != null){
                    try {
                        listener.onNewData(this, dataArray, packIndex, sampleIndex, flag);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected void fireOnEnd(int status, Exception e, long sampleCount){
        synchronized (mListeners){
            for (WeakReference<IAudioSourceListener> item:mListeners){
                IAudioSourceListener listener = item.get();
                if (listener != null){
                    try {
                        listener.onEnd(this, status, e, sampleCount);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }



}