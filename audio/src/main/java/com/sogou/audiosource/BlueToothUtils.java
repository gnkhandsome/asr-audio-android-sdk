package com.sogou.audiosource;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017/10/25.
 */

public class BlueToothUtils {
    public static void showUsageDetail(AudioManager am,String extraInfo) {
        Log.d("blue", extraInfo+">>>>>>>>>>>>>>>>>>>>>>>>begin");
        int mode = am.getMode();
        boolean scoAvailable =am.isBluetoothScoAvailableOffCall();
        boolean a2dpOn =am.isBluetoothA2dpOn();
        boolean scoOn = am.isBluetoothScoOn();
        boolean isMusicActive =am.isMusicActive();
        boolean speakerPhoneOn = am.isSpeakerphoneOn();
        int ringerMode = am.getRingerMode();
        int r1 = am.getRouting(AudioManager.MODE_IN_COMMUNICATION);
        int r2 = am.getRouting(AudioManager.MODE_NORMAL);
        reflectMethod(am, "isExtraSpeakerDockOn");
        reflectMethod(am, "isFMActive");
        reflectMethod(am, "isMediaSpeakerOn");
        reflectMethod(am, "isRadioSpeakerOn");
        reflectMethod(am, "isVoiceCallActive");
        reflectMethod(am, "isRecordActive");
        Log.d("blue","mode:"+mode+",scoAvailable:"+scoAvailable+",a2dpOn:"+a2dpOn+",scoOn:"+scoOn+",isMusicActive:"+isMusicActive+
                ",speakerPhoneOn:"+speakerPhoneOn+",ringerMode:"+ringerMode+",conmmunication rounter:"+r1+",normal router:"+r2);
        Log.d("blue", extraInfo+"<<<<<<<<<<<<<<<<<<<<<<<<<<<end");
    }

    public static void reflectMethod(AudioManager am,String mName) {
        Object res = ReflectUtils.invoke(am,mName,null,new Object[0]);
        Log.d("blue",mName+":"+res);

    }

    public static void addChangeLis(AudioManager am) {
        am.abandonAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
                Log.d("blue","focusChange:"+i);
            }
        });
    }

    public static void hasBluetoothConnected(Context context, final blueToothListener l){


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener(){

            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.HEADSET) {
                    BluetoothHeadset mBluetoothHeadset = (BluetoothHeadset) proxy;

                    if (mBluetoothHeadset == null){
                        if (l != null ){
                            l.onResult(false,null);
                        }
                    }

                    List<BluetoothDevice> devices = mBluetoothHeadset.getConnectedDevices();
                    for (final BluetoothDevice dev : devices) {
                        if (mBluetoothHeadset.isAudioConnected(dev)){
                            if (l != null){
                                l.onResult(true, dev);
                            }

                        }
                    }

                    if (l != null ){
                        l.onResult(false,null);
                    }
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                if (profile == BluetoothProfile.HEADSET) {
                    if (l != null ){
                        l.onResult(false,null);
                    }
                }
            }
        }, BluetoothProfile.HEADSET);

    }



    public static interface blueToothListener{
        void onResult(boolean isConnected,@Nullable BluetoothDevice device);

    }
}
