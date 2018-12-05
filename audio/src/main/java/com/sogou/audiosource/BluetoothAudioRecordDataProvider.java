package com.sogou.audiosource;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.util.Log;

/**
 * Created by zhouqilin on 16/9/23.
 */

public class BluetoothAudioRecordDataProvider extends AudioRecordDataProvider {

    public BluetoothAudioRecordDataProvider(Context c, int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes) {
        super(c, audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
    }

    @Override
    public boolean isReady() {
        return isScoConnected;
    }
}
