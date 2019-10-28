package com.softwinner.bionrecorder.ui.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;

import com.softwinner.bionrecorder.bean.WrapedRecorder;
import com.softwinner.bionrecorder.ui.fragment.DoubleRecordFragment;
import com.softwinner.bionrecorder.ui.receiver.UsbCameraStateReceiver;

import java.util.ArrayList;

import static com.softwinner.bionrecorder.common.AppConfig.DEBUG;
import static com.softwinner.bionrecorder.common.AppConfig.TAG;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class RecordService extends Service implements UsbCameraStateReceiver.CameraStatusListener{
    private static final String TAG = "RecordService";
    private static final boolean DEBUG = true;

    private RecordBinder mBinder = new RecordBinder();
    private ArrayList<WrapedRecorder> mRecorderList = new ArrayList<>();
    private UsbCameraStateReceiver mUsbCameraStateReceiver;
    private UsbCameraStateReceiver.CameraStatusListener mUsbCameraStateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mUsbCameraStateReceiver = new UsbCameraStateReceiver(this);
        IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_CAMERA_PLUG_IN_OUT");
        registerReceiver(mUsbCameraStateReceiver, intentFilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUsbCameraStateReceiver != null) {
            unregisterReceiver(mUsbCameraStateReceiver);
            mUsbCameraStateReceiver = null;
        }
    }

    @Override
    public  void onPlugIn(int cameraType) {
        if (mUsbCameraStateListener != null) {
            mUsbCameraStateListener.onPlugIn(cameraType);
        }
    }

    @Override
    public  void onPlugOut(int cameraType) {
        if (mUsbCameraStateListener != null) {
            mUsbCameraStateListener.onPlugOut(cameraType);
        }
    }

    public void setUsbCameraStateListener(UsbCameraStateReceiver.CameraStatusListener listener) {
        mUsbCameraStateListener = listener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (DEBUG) {
            Log.d(TAG, "--------onBind------");
        }

        return mBinder;
    }

    public class RecordBinder extends Binder {
        public RecordService getRecordService() {
            return RecordService.this;
        }
    }

    /**
     * 添加camera
     */
    public synchronized void addCamera(int cameraType, int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "-----addCamera cameraId = " + cameraId);
        }

        if (!isCameraAdded(cameraId)) {
            mRecorderList.add(new WrapedRecorder(cameraType, cameraId, this));
        }
    }

    /**
     * 查询camera是否已经添加
     */
    public boolean isCameraAdded(int cameraId) {
        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                return true;
            }
        }

        return false;
    }

    /**
     * 移除camera
     */
    public void removeCamera(int cameraId) {
        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                mRecorderList.remove(recorder);
                break;
            }
        }
    }

    /**
     * 打开camera
     * @param cameraId: 要打开的cameraId
     */
    public void openCamera(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "----openCamera--cameraId = " + cameraId);
        }
        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                recorder.openCamera();
                break;
            }
        }
    }

    /**
     * 设置camera SurfaceHolder
     * @param cameraId: 要设置的cameraId
     */
    public void setPreviewDisplay(int cameraId, SurfaceHolder holder) {
        if (DEBUG) {
            Log.d(TAG, "----setPreviewDisplay--cameraId = " + cameraId);
        }

        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                recorder.setPreviewDisplay(holder);
                break;
            }
        }
    }

    /**
     * 开始预览
     * @param cameraId: 预览cameraId
     */
    public void startPreview(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "----startPreview--cameraId = " + cameraId);
        }

        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                recorder.startPreview();
                break;
            }
        }
    }

    /**
     * 停止预览
     * @param cameraId: 预览cameraId
     */
    public void stopPreview(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "----stopPreview--cameraId = " + cameraId);
        }

        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                recorder.stopPreview();
                break;
            }
        }
    }

    /**
     * 拍照
     * @param cameraId: 用于拍照的cameraId
     */
    public void takePictureAsync(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "----takePicture--cameraId = " + cameraId);
        }

        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                recorder.takePictureAsync();
                break;
            }
        }
    }

    /**
     * 开始录像
     * @param cameraId: 用于录像的cameraId
     */
    public void startRecordAsync(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "-----startRecord-------cameraId = " + cameraId);
        }

        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                recorder.startRecordAsync();
                break;
            }
        }
    }

    /**
     * 停止录像
     * @param cameraId: 用于录像的cameraId
     */
    public void stopRecord(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "-----stopRecord-------cameraId = " + cameraId);
        }

        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                recorder.stopRecord();
                break;
            }
        }

    }

    /**
     * 停止录像
     * @param cameraId: 用于录像的cameraId
     */
    public void stopRecordAsync(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "-----stopRecord-------cameraId = " + cameraId);
        }

        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                recorder.stopRecordAsync();
                break;
            }
        }

    }

    /**
     * 关闭camera
     * @param cameraId: 要关闭的cameraId
     */
    public void closeCamera(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "----closeCamera--cameraId = " + cameraId);
        }

        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                recorder.closeCamera();
                break;
            }
        }
    }

    /**
     * 关闭所有的camera
     */
    public void closeCameras() {
        if (DEBUG) {
            Log.d(TAG, "----close all camera that opened----");
        }

        for (WrapedRecorder recorder : mRecorderList) {
            recorder.closeCamera();
        }
    }

    public WrapedRecorder getRecorder(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "----getRecorder--cameraId = " + cameraId);
        }

        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                return recorder;
            }
        }

        return null;
    }

    /**
     * 查看是否有某个camera处于预览状态
     */
    public boolean isPreviewing() {
        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.isPreviewing) {
                return true;
            }
        }

        return false;
    }

    /**
     * 查看对应cameraId的camera是否正在预览
     * @param cameraId: 要查看的cameraId
     */
    public boolean isPreviewing(int cameraId) {
        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                return recorder.isPreviewing;
            }
        }

        return false;
    }

    /**
     * 锁定
     * @param cameraId
     * @param lock
     */
    public void setLock(int cameraId, boolean lock) {
        if (DEBUG) {
            Log.d(TAG, "--------setLock-----cameraId = " + cameraId +
                    ", lock=" + lock);
        }
        for (WrapedRecorder recorder : mRecorderList) {
            if (DEBUG) Log.d(TAG, "----setLock--- cameraId = " + recorder.mCameraId);
            if (recorder.mCameraId == cameraId) {
                recorder.isLocked = lock;
            }
        }
    }

    public boolean isRecording(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "--------isRecording----cameraId = " + cameraId);
        }
        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                return recorder.isRecording;
            }
        }

        return false;
    }

    /**
     * 设置下一个要保存的文件路径，用于循环录制
     * @param cameraId
     */
    public void setNextSaveFileAsync(int cameraId) {
        if (DEBUG) {
            Log.d(TAG, "--------setNextSaveFileAsync----cameraId = " + cameraId);
        }
        for (WrapedRecorder recorder : mRecorderList) {
            if (recorder.mCameraId == cameraId) {
                recorder.setNextSaveFileAsync();
            }
        }
    }
}
