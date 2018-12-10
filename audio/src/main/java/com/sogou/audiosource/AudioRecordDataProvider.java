// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
package com.sogou.audiosource;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.util.Log;

import com.sogou.sogocommon.ErrorIndex;

import static com.sogou.sogocommon.ErrorIndex.ERROR_AUDIO_FORBIDDEN;

/**
 * Created by zhouqilin on 16/9/23.
 */

public class AudioRecordDataProvider implements IAudioDataProvider {
    private AudioRecord mSysRecorder;
    private boolean mInitSucceed;
    private Context mContext;
    private int audioSource;
    private int sampleRateInHz;
    private int channelConfig;
    private int audioFormat;
    private int bufferSizeInBytes;
    private boolean isSumsung5;//android5.0
    private Object aLock = new Object();
    protected boolean isScoConnected = false;

    public AudioRecordDataProvider(Context c,int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes){
        isSumsung5 = SDKUtils.isSumsung5();

        mContext = c;
        mAudioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
//        Log.d("focus","init");
//        mAudioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
//            @Override
//            public void onAudioFocusChange(int i) {
//                Log.d("focus","i1:"+i);
//            }
//        },AudioManager.MODE_NORMAL,AudioManager.AUDIOFOCUS_GAIN);
//        mAudioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
//            @Override
//            public void onAudioFocusChange(int i) {
//                Log.d("focus","i2:"+i);
//            }
//        },AudioManager.MODE_IN_CALL,AudioManager.AUDIOFOCUS_GAIN);
//        mAudioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
//            @Override
//            public void onAudioFocusChange(int i) {
//                Log.d("focus","i3:"+i);
//            }
//        },AudioManager.MODE_IN_COMMUNICATION,AudioManager.AUDIOFOCUS_GAIN);
        this.audioSource = audioSource;
        this.sampleRateInHz = sampleRateInHz;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
        this.bufferSizeInBytes = bufferSizeInBytes;

        init();
        BlueToothUtils.addChangeLis(mAudioManager);
        registerAudioInputReceiver();
        startSCO();
    }
    private Runnable scoConnectedCallback;
    private Runnable wireMicInputCallback;
    public void setCallbacks(Runnable scoConnected,Runnable wireMicInput) {
        this.scoConnectedCallback = scoConnected;
        this.wireMicInputCallback = wireMicInput;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    private void init() {
        mSysRecorder = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        mInitSucceed = mSysRecorder.getState() == AudioRecord.STATE_INITIALIZED;
        if (!mInitSucceed){
            mSysRecorder.release();
            mSysRecorder = null;
        }
    }

    public void reinitRecorder() {
        synchronized (aLock) {
            if (mInitSucceed) {
                mSysRecorder.release();
            }
            init();
        }
    }


    @Override
    public int read(short[] buffer, int offset, int len) {
        synchronized (aLock) {
            int result = mSysRecorder.read(buffer, offset, len);
            if(result == 0 && mSysRecorder.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED){
                result = -ErrorIndex.ERROR_AUDIO_FORBIDDEN;
            }
            return result;
        }
    }

    @Override
    public int read(byte[] buffer, int offset, int len) {
        synchronized (aLock) {
            int result = mSysRecorder.read(buffer, offset, len);
            if(result == 0 && mSysRecorder.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED){
                result = -ErrorIndex.ERROR_AUDIO_FORBIDDEN;
            }
            return result;
        }
    }

    @Override
    public boolean isInitialized() {
        return mInitSucceed;
    }

    @Override
    public void start() {
        synchronized (aLock) {
            if (mSysRecorder != null) {
                mSysRecorder.startRecording();
            }
        }
    }

    @Override
    public void stop() {
        synchronized (aLock) {
            if (mSysRecorder != null) {
                mSysRecorder.stop();
            }
        }
    }

    @Override
    public void release() {
        synchronized (aLock) {
            if (mSysRecorder != null) {
                mSysRecorder.release();
            }
            unregisterAudioInputReceiver();
        }
    }

    private AudioManager mAudioManager;
    private AudioInputReceiver mAudioInputReceiver;
    private boolean hasWiredMic;
    public class AudioInputReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(isSumsung5) {
                doReceiveSumsung5(context,intent);
            }else {
                doReceiveGeneral(context,intent);
            }
        }
    }

    public void startSCO() {
        try {
            if (!mAudioManager.isBluetoothScoAvailableOffCall()/*||mAudioManager.isBluetoothScoOn()*/) {
                isScoConnected = true;
                return;
            }
            Log.d("blue","startSCO");
            mAudioManager.startBluetoothSco();
            // sanxing ceshi
//            Object o = ReflectUtils.invoke(mAudioManager,"startBluetoothScoVirtualCall",null,new Object[0]);
//            Log.d("blue",o.toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopSCO() {
        try {
            Log.d("blue","stopSCO");
            mAudioManager.stopBluetoothSco();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerAudioInputReceiver() {
        try {
            if (null != mAudioInputReceiver) {
                unregisterAudioInputReceiver();
            }
            mAudioInputReceiver = new AudioInputReceiver();
            IntentFilter audioFilter = new IntentFilter();
            audioFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            audioFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            audioFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED);
            audioFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
            audioFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
            mContext.registerReceiver(mAudioInputReceiver, audioFilter);
        }catch (Exception e) {
        }
    }

    private void unregisterAudioInputReceiver() {
        try {
            if (mAudioInputReceiver != null) {
                mContext.unregisterReceiver(mAudioInputReceiver);
                mAudioInputReceiver = null;
            }
        }catch (Exception e ) {
        }
    }

    // sunsumg 5
    private void doReceiveSumsung5(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(AudioManager.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            boolean hasMic = intent.getIntExtra("microphone",-1) == 1;
            Log.d("blue", "wire state:" + state+",hasMic:"+hasMic);
            if(hasMic&&state==1) {
                hasWiredMic = true;
                mAudioManager.setSpeakerphoneOn(false);
                if(null!= wireMicInputCallback) {
                    wireMicInputCallback.run();
                }
            }else {
                hasWiredMic = false;
                mAudioManager.setSpeakerphoneOn(true);
            }
        }else
        if(action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);
            if(state == BluetoothAdapter.STATE_CONNECTED) {
                startSCO();
            }
        }
        if (action.equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)) {
            final int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
            Log.d("blue", "state2:" + state);
            if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
                if(!isRecordActiveInsumsung()){
                    reinitRecorder();
                }
                if(null!= scoConnectedCallback) {
                    scoConnectedCallback.run();
                }
            }else if (state == AudioManager.SCO_AUDIO_STATE_DISCONNECTED) {
                BlueToothUtils.showUsageDetail(mAudioManager,"SCO down 0");
                stopSCO();
                mAudioManager.setSpeakerphoneOn(hasWiredMic?false:true);
                mAudioManager.setBluetoothScoOn(false);
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                BlueToothUtils.showUsageDetail(mAudioManager,"SCO down 1");
                if(!isRecordActiveInsumsung()){
                    reinitRecorder();
                }

            }
        }
    }

    private boolean isRecordActiveInsumsung() {
        try {
            Object res = ReflectUtils.invoke(mAudioManager, "isRecordActive", null, new Object[0]);
            if (res instanceof Boolean) {
                Log.d("blue","recordactive:"+res);
                return (Boolean)res;
            }
        }catch (Exception e) {

        }
        return false;
    }

    // 普通类型手机
    private void doReceiveGeneral(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("blue", "action:" + action);
        if(action.equals(AudioManager.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            boolean hasMic = intent.getIntExtra("microphone",-1) == 1;
//            Log.d("blue", "wire state:" + state+",hasMic:"+hasMic);
            if(hasMic&&state==1) {
                hasWiredMic = true;
                mAudioManager.setSpeakerphoneOn(false);
                if(null!= wireMicInputCallback) {
                    wireMicInputCallback.run();
                }
            }else {
                hasWiredMic = false;
                mAudioManager.setSpeakerphoneOn(true);
            }
        } else if(action.equals(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED)) {
            int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
//            Log.d("blue", "state00:" + state);
        }
        else
        if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
//            Log.d("blue", "state0:" + state);
            if(state==BluetoothAdapter.STATE_OFF) {
//                    stopSCO();
            }
        }else
        if(action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);
            Log.d("blue", "state1:" + state);
            if(state == BluetoothAdapter.STATE_CONNECTED) {
                // 这里不要移除，某些机型有适配问题。。。。
                BlueToothUtils.showUsageDetail(mAudioManager,"CONNECTED 0");
                mAudioManager.setSpeakerphoneOn(false);
                mAudioManager.setBluetoothScoOn(true);
                mAudioManager.setBluetoothA2dpOn(true);
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                BlueToothUtils.showUsageDetail(mAudioManager,"CONNECTED 1");
                startSCO();
                BlueToothUtils.showUsageDetail(mAudioManager,"CONNECTED 2");
//
            }else if(state==BluetoothAdapter.STATE_DISCONNECTED) {
//                BlueToothUtils.showUsageDetail(mAudioManager,"DISCONNECTED 0");
//                    stopSCO();
            }
        }else
        if (action.equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)) {
            int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
//            Log.d("blue", "state2:" + state);
            if (state == AudioManager.SCO_AUDIO_STATE_DISCONNECTED) {
                isScoConnected = false;
                BlueToothUtils.showUsageDetail(mAudioManager,"SCO down 0");
//                    stopSCO();
                mAudioManager.setSpeakerphoneOn(hasWiredMic?false:true);
                mAudioManager.setBluetoothScoOn(false);

                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                BlueToothUtils.showUsageDetail(mAudioManager,"SCO down 1");
            } else if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
                isScoConnected = true;
                BlueToothUtils.showUsageDetail(mAudioManager,"SCO up 0");
                mAudioManager.setSpeakerphoneOn(false);
                mAudioManager.setBluetoothScoOn(true);
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                BlueToothUtils.showUsageDetail(mAudioManager,"SCO up 1");
                if(null!= scoConnectedCallback) {
                    scoConnectedCallback.run();
                }
            }

        }
    }
}