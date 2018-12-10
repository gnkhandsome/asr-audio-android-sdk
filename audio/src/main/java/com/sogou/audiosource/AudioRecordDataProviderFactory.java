// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
package com.sogou.audiosource;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by zhouqilin on 16/9/23.
 */

public class AudioRecordDataProviderFactory implements IAudioDataProviderFactory {
    private static final int DEFAULT_BUFFER_SIZE = 4 * 1024;
    private static final int DEFAULT_SAMPLING_RATE_HZ = 16 * 1000;

    final int mAudioSource;
    final int mSampleRateInHz;
    final int mChannelConfig;
    final int mAudioFormat;
    final int mBufferSizeInBytes;
    private Context mContext;
    boolean needBlueToothAudio = false;

    public AudioRecordDataProviderFactory(Context context,int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes){
        mAudioSource = audioSource;
        mSampleRateInHz = sampleRateInHz;
        mChannelConfig = channelConfig;
        mAudioFormat = audioFormat;
        mContext = context;
        if (bufferSizeInBytes <= 0){
            bufferSizeInBytes = Math.max(DEFAULT_BUFFER_SIZE, AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat));
        }
        mBufferSizeInBytes = bufferSizeInBytes;
    }

    public AudioRecordDataProviderFactory(Context context){

//        this(context,MediaRecorder.AudioSource.MIC, DEFAULT_SAMPLING_RATE_HZ,  AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 0);
        this(context,MediaRecorder.AudioSource.DEFAULT, DEFAULT_SAMPLING_RATE_HZ,  AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 0);
//        this(context,MediaRecorder.AudioSource.VOICE_COMMUNICATION, DEFAULT_SAMPLING_RATE_HZ,  AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 0);
//        this(context,MediaRecorder.AudioSource.VOICE_CALL, DEFAULT_SAMPLING_RATE_HZ,  AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 0);
    }

    public AudioRecordDataProviderFactory(Context context,boolean needBluetoothAudio){
        this(context,MediaRecorder.AudioSource.DEFAULT, DEFAULT_SAMPLING_RATE_HZ,  AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 0);
        this.needBlueToothAudio = needBluetoothAudio;
    }

    @Override
    public IAudioDataProvider create() {
        if (needBlueToothAudio){
            return new BluetoothAudioRecordDataProvider(mContext,mAudioSource, mSampleRateInHz, mChannelConfig, mAudioFormat, mBufferSizeInBytes);
        }else {
            return new AudioRecordDataProvider(mContext,mAudioSource, mSampleRateInHz, mChannelConfig, mAudioFormat, mBufferSizeInBytes);
        }
    }

    @Override
    public int samplingRateInHz() {
        return mSampleRateInHz;
    }

    @Override
    public int bufferSizeInBytes() {
        return mBufferSizeInBytes;
    }

    @Override
    public int bytesPerFrame() {
        return mAudioFormat == AudioFormat.ENCODING_PCM_16BIT ? 2 : 1;
    }
}