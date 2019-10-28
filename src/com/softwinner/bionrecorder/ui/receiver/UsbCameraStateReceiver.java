package com.softwinner.bionrecorder.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.softwinner.bionrecorder.common.AppConfig;

/**
 * @author zhongzhiwen
 * @date 2017/9/18
 * @email zhongzhiwen24@gmail.com
 */

public class UsbCameraStateReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbCameraStateReceiver";
    private static final boolean DEBUG = true;
    private CameraStatusListener mListener;


    public UsbCameraStateReceiver(CameraStatusListener listener) {
        mListener = listener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("UsbCameraName");
        int state = intent.getIntExtra("UsbCameraState", 5);

        if (DEBUG) Log.d(TAG, "------onReceive---name = " + name);
        if (DEBUG) Log.d(TAG, "------onReceive---state = " + state);
        int cameraType = -1;
        int cameraId = Integer.parseInt(name.substring(name.length() - 1));
        if (DEBUG) Log.d(TAG, "------onReceive---state = " + state +
                ", cameraId = " + cameraId);

        if (state == 0) {
            // 拔出
            if ((cameraId == AppConfig.FRONT_CAMERA_ID) || (cameraId == AppConfig.FRONT_CAMERA_ID + 1)) {
                cameraType = AppConfig.FRONT_CAMERA;
            } else if ((cameraId == AppConfig.BACK_CAMERA_ID) || (cameraId == AppConfig.BACK_CAMERA_ID + 1)) {
                cameraType = AppConfig.BACK_CAMERA;
            }
            mListener.onPlugOut(cameraType);
        } else if (state == 1) {
            // 插入
            if ((cameraId == AppConfig.FRONT_CAMERA_ID) || (cameraId == AppConfig.FRONT_CAMERA_ID + 1)) {
                cameraType = AppConfig.FRONT_CAMERA;
            } else if ((cameraId == AppConfig.BACK_CAMERA_ID) || (cameraId == AppConfig.BACK_CAMERA_ID + 1)) {
                cameraType = AppConfig.BACK_CAMERA;
            }
            mListener.onPlugIn(cameraType);
        }
    }

    public interface CameraStatusListener {
        void onPlugIn(int cameraType);
        void onPlugOut(int cameraType);
    }
}
