package com.softwinner.bionrecorder.bean;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.softwinner.bionrecorder.common.AppConfig;
import com.softwinner.bionrecorder.ui.service.RecordService;
import com.softwinner.bionrecorder.util.Storage;
import com.softwinner.bionrecorder.util.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.R.attr.width;
import static com.softwinner.bionrecorder.util.Utils.isUVCCameraSonix;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class WrapedRecorder {
    public static final String TAG = "WrapedRecorder";
    public static final boolean DEBUG = true;

    private Camera mCamera = null;
    private Camera mRecordCamera = null;
    public int mCameraId;
    public boolean isLocked = false;
    public boolean isPreviewing;
    private RecordService mRecordService;
    private MediaRecorder mRecorder;
    public boolean isRecording;
    private Camera.Parameters mParameters;
    public int mCameraType;
    private int mSpeed = 0;
    private String mVideoFilePath;
    private long MIN_RECORD_SIZE = 600 * 1024 * 1024L; // 要录像最少需要600M

    public WrapedRecorder(int cameraType, int cameraId, RecordService service) {
        mCameraType = cameraType;
        mCameraId = cameraId;
        mRecordService = service;
    }

    /**
     * 打开camera
     */
    public void openCamera() {
        if (DEBUG) {
            Log.d(TAG, "-------openCamera cameraId = " + mCameraId);
        }

        try {
            mCamera = Camera.open(mCameraId);
        } catch (Exception e) {
            Log.e(TAG, "------Error To openCamera cameraId = " + mCameraId);
            e.printStackTrace();
        }
    }

    public void openCameraAsync() {
        if (DEBUG) {
            Log.d(TAG, "-------openCameraAsync cameraId = " + mCameraId);
        }

        Flowable.just("openCameraAsync")
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (DEBUG) {
                            Log.d(TAG, "-------openCameraAsync in consumer cameraId = " + mCameraId);
                        }
                        try {
                            mCamera = Camera.open(mCameraId);
                        } catch (Exception e) {
                            Log.e(TAG, "-------Error To openCameraAsync cameraId = " + mCameraId);
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 设置SurfaceHolder
     * @param holder
     */
    public void setPreviewDisplay(SurfaceHolder holder) {
        if (DEBUG) {
            Log.d(TAG, mCamera + "-------setPreviewDisplay cameraId = " + mCameraId + "---holder---" + holder);
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (Exception e) {
                Log.e(TAG, "-----Error To setPreviewDisplay----");
                e.printStackTrace();
            }
        }
    }

    public void setPreviewDisplayAsync(final SurfaceHolder holder) {
        if (DEBUG) {
            Log.d(TAG, "-------setPreviewDisplayAsync cameraId = " + mCameraId);
        }

        Flowable.just("setPreviewDisplayAsync")
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (DEBUG) {
                            Log.d(TAG, mCamera + "-------setPreviewDisplayAsync in consumer cameraId = " + mCameraId);
                        }
                        if (mCamera != null) {
                            try {
                                mCamera.setPreviewDisplay(holder);
                            } catch (IOException e) {
                                Log.e(TAG, "-----Error To setPreviewDisplayAsync----");
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 开始预览
     */
    public synchronized void startPreview() {
        if (DEBUG) {
            Log.d(TAG, "------startPreview cameraId = " + mCameraId);
        }

        if (mCamera != null) {
            try {
                setPreviewSize();
                String waterMark = "20,100," + Utils.getCurrentTimeForWaterMark() +
                        ",500,100," + mSpeed + "KM/H";
                startWaterMark();
                if (DEBUG) {
                    Log.d(TAG, "------startPreview watermark = " + waterMark);
                }
                setWaterMarkMultiple(waterMark);
                mCamera.startPreview();
                isPreviewing = true;
            } catch (Exception e) {
                Log.e(TAG, "-----Error To startPreview-----");
                e.printStackTrace();
            }
        }
    }

    public void startPreviewAsync() {
        if (DEBUG) {
            Log.d(TAG, "------startPreviewAsync cameraId = " + mCameraId);
        }

        Flowable.just("startPreviewAsync")
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (DEBUG) {
                            Log.d(TAG, mCamera + "-------startPreviewAsync in consumer cameraId = " + mCameraId);
                        }
                        if (mCamera != null) {
                            setPreviewSize();
                            String waterMark = "20, 100, " + Utils.getCurrentTimeForWaterMark() +
                                    ", 500, 100, " + mSpeed + "KM";
                            startWaterMark();
                            setWaterMarkMultiple(waterMark);
                            try {
                                mCamera.startPreview();
                                isPreviewing = true;
                            } catch (Exception e) {
                                Log.e(TAG, "-----Error To startPreviewAsync-----");
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 设置预览size
     */
    public void setPreviewSize() {
        if (DEBUG) {
            Log.d(TAG, "-----setPreviewSize cameraId = " + mCameraId);
        }

        if (mCamera == null) {
            return;
        }

        if (mParameters == null) {
            mParameters = mCamera.getParameters();
        }

        try {
            mParameters.setPreviewSize(1280, 720);
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            Log.e(TAG, "----Error To setPreviewSize-----");
            e.printStackTrace();
        }

    }

    /**
     * 开启水印
     */
    public void startWaterMark() {
        Class<Camera> cls = Camera.class;
        try {
            Method method = cls.getMethod("startWaterMark");
            method.invoke(mCamera);
        } catch (Exception e) {
            Log.e(TAG, "-----Error To startWaterMark-------");
            e.printStackTrace();
        }
    }

    /**
     * 设置水印
     * @param waterMark
     */
    public void setWaterMarkMultiple(String waterMark) {
        Class<Camera> cls = Camera.class;
        try {
            Method method = cls.getMethod("setWaterMarkMultiple", String.class, int.class);
            method.invoke(mCamera, waterMark, 0);
        } catch (Exception e) {
            Log.e(TAG, "-----Error To setWaterMarkMultiple-------");
            e.printStackTrace();
        }
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        if (DEBUG) {
            Log.d(TAG, "----stopPreview cameraId = " + mCameraId);
        }

        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                isPreviewing = false;
            } catch (Exception e) {
                Log.e(TAG, "-----Error To stopPreview-------");
                e.printStackTrace();
            }
        }
    }

    public void stopPreviewAsync() {
        if (DEBUG) {
            Log.d(TAG, "----stopPreviewAsync cameraId = " + mCameraId);
        }

        Flowable.just("stopPreviewAsync")
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (DEBUG) {
                            Log.d(TAG, mCamera + "-------stopPreviewAsync in consumer cameraId = " + mCameraId);
                        }
                        if (mCamera != null) {
                            try {
                                mCamera.stopPreview();
                                isPreviewing = false;
                            } catch (Exception e) {
                                Log.e(TAG, "-----Error To stopPreviewAsync-------");
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 拍照
     */
    public void takePictureAsync() {
        if (DEBUG) {
            Log.d(TAG, "----takePictureAsync cameraId = " + mCameraId);
        }

        if (!canTakePicture()) {
            return;
        }

        Flowable.just("takePictureAsync")
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (DEBUG) {
                            Log.d(TAG, mCamera + "-------takePictureAsync in consumer cameraId = " + mCameraId);
                        }
                        if (mCamera != null) {
                            setPictureQuality();
                            try {
                                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                                    @Override
                                    public void onPictureTaken(byte[] data, Camera camera) {
                                        Storage.savePicture(data);
                                    }
                                });
                            } catch (Exception e) {
                                Log.e(TAG, "-----Error To takePictureAsync-------");
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 检查当前环境（如存储）是否可以拍照
     */
    private boolean canTakePicture() {
        String rootPath = AppConfig.getInstance(mRecordService.getApplicationContext())
                .getString(AppConfig.STORAGE_PATH, AppConfig.DEFAULT_STORAGE_PATH);
        Camera.Size size = mParameters.getPictureSize();
        int  space = size.width * size.height * 4;

        if (!Storage.hasEnoughSpace(rootPath, 2, space)) {
            Toast.makeText(mRecordService, "存储已满，请清理空间！", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设置图片分辨率
     */
    public void setPictureQuality() {
        // TODO 还未读取存储的图片分辨率
        if (DEBUG) {
            Log.d(TAG, "-----setPictureQuality cameraId = " + mCameraId);
        }

        if (mCamera == null) {
            return;
        }

        if (mParameters == null) {
            mParameters = mCamera.getParameters();
        }

        try {
            mParameters.setPictureSize(1280, 720);
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            Log.e(TAG, "----Error To setPictureQuality----");
            e.printStackTrace();
        }

    }

    /**
     * 开始录像
     */
    public void startRecordAsync() {
        if (DEBUG) {
            Log.d(TAG, "----startRecordAsync cameraId = " + mCameraId);
        }

        if (!canRecord()) {
            return;
        }

        Flowable.just("startRecordAsync")
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (DEBUG) {
                            Log.d(TAG, mCamera + "-------startRecordAsync in consumer cameraId = " + mCameraId);
                        }

                        if (mCameraId != isUVCCameraSonix(mCameraId)) {
                            // 松涵摄像头
                            if (DEBUG) {
                                Log.d(TAG, "----For Sonix Camera----" + isUVCCameraSonix(mCameraId));
                            }

                            if (mRecordCamera != null) {
                                mRecordCamera.release();
                                mRecordCamera = null;
                            }
                            try {
                                mRecordCamera = Camera.open(isUVCCameraSonix(mCameraId));
                            } catch (Exception e) {
                                Log.e(TAG, "------Error To open mRecordCamera for sonix");
                                e.printStackTrace();
                            }

                        } else {
                            mRecordCamera = mCamera;
                        }

                        if (Build.VERSION.SDK_INT >= 21) {
                            // 6.0以上
                            if (DEBUG) {
                                Log.d(TAG, "-----up 6.0-----");
                            }
                            Class<MediaRecorder> cls = MediaRecorder.class;
                            try {
                                Constructor<MediaRecorder> constructor = cls.getConstructor(int.class);
                                mRecorder = constructor.newInstance(1);
                            } catch (Exception e) {
                                Log.e(TAG, "------Error To New MediaRecorder by Reflecting------");
                                e.printStackTrace();
                            }
                        } else {
                            // 4.4
                            mRecorder = new MediaRecorder();
                        }

                        mRecorder.reset();

                        if (DEBUG) {
                            Log.d(TAG, "-----before setRecorderParameter in startRecord-----");
                        }
                        mRecordCamera.unlock();
                        setRecorderParameter();

                        try {
                            mRecorder.prepare();
                            mRecorder.start();
                            isRecording = true;
                        } catch (Exception e) {
                            releaseRecorder();
                            Log.e(TAG, "-----Error To startRecordAsync------");
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 检查当前环境（如存储）是否可以进行录制
     * 最少需要600M才能进行录制
     */
    private boolean canRecord() {
        String rootPath = AppConfig.getInstance(mRecordService.getApplicationContext())
                .getString(AppConfig.STORAGE_PATH, AppConfig.DEFAULT_STORAGE_PATH);
        if (!Storage.hasEnoughSpace(rootPath, 1, MIN_RECORD_SIZE)) {
            Toast.makeText(mRecordService, "空间不足，请清理空间！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设置MediaRecorder参数 TODO
     */
    public void setRecorderParameter() {
        if (DEBUG) {
            Log.d(TAG, "-----setRecorderParameter cameraId = " + mCameraId);
        }

        AppConfig appConfig = AppConfig.getInstance(mRecordService.getApplicationContext());
        mRecorder.setCamera(mCamera);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // TODO，未读取存储的关于audio的设置
        boolean mute = false;
        if (mCameraType == AppConfig.FRONT_CAMERA) {
            // 读取用于前路的摄像头的设置
            mute = appConfig.getBoolean(AppConfig.FRONT_MUTE, false);
        } else if (mCameraType == AppConfig.BACK_CAMERA) {
            // 读取用于后路的摄像头的设置
            mute = appConfig.getBoolean(AppConfig.BACK_MUTE, false);
        }
        if (mute) {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            mRecorder.setOutputFormat(8); // ts格式
        } else {
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // mp4格式
        }

        mRecorder.setVideoFrameRate(30);
        // TODO 录制分辨率的设置
        mRecorder.setVideoSize(1280, 720);
//        mRecorder?.setVideoSize(640, 480)
        mRecorder.setVideoEncodingBitRate(6000000);

        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); // 编码格式
        if (mute) {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        }
        mVideoFilePath = Storage.getVideoFile(mCameraType);
        mRecorder.setOutputFile(mVideoFilePath);

    }

    /**
     * 释放MediaRecorder
     */
    private void releaseRecorder() {
        if (DEBUG) {
            Log.d(TAG, "-----releaseRecorder cameraId = " + mCameraId);
        }

        try {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            mRecordCamera.lock();
        } catch (Exception e) {
            Log.e(TAG, "--------Error To releaseRecorder cameraId = " + mCameraId);
            e.printStackTrace();
        }
    }

    public synchronized void stopRecord() {
        if (DEBUG) {
            Log.d(TAG, "----stopRecord cameraId = " + mCameraId);
        }

        if (mRecorder != null) {
            try {
                mRecorder.stop();
                if (DEBUG) Log.d(TAG, "-----stopRecord isLocked = " + isLocked);
                if (isLocked) {
                    // 设置为紧急视频
                    int index = mVideoFilePath.lastIndexOf("/");
                    String destFilename = "LOCK_" + mVideoFilePath.substring(index + 1);
                    String destFilePath = mVideoFilePath.substring(0, index + 1) + destFilename;
                    if (DEBUG) Log.d(TAG, "------destFilePath = " + destFilePath);
                    new File(mVideoFilePath).renameTo(new File(destFilePath));
                }
            } catch (Exception e) {
                Log.e(TAG, "-----Error To stopRecord-------");
                e.printStackTrace();
            } finally {
                releaseRecorder();
                isRecording = false;
                isLocked = false;
            }
        }
    }

    /**
     * 停止录像
     */
    public void stopRecordAsync() {
        if (DEBUG) {
            Log.d(TAG, "----stopRecordAsync cameraId = " + mCameraId);
        }

        Flowable.just("stopRecordAsync")
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (DEBUG) {
                            Log.d(TAG, mCamera + "-------stopRecordAsync in consumer cameraId = " + mCameraId);
                        }
                        if (mRecorder != null) {
                            try {
                                mRecorder.stop();
                                if (DEBUG) Log.d(TAG, "-----stopRecord isLocked = " + isLocked);
                                if (isLocked) {
                                    // 设置为紧急视频
                                    int index = mVideoFilePath.lastIndexOf("/");
                                    String destFilename = "LOCK_" + mVideoFilePath.substring(index + 1);
                                    String destFilePath = mVideoFilePath.substring(0, index + 1) + destFilename;
                                    if (DEBUG) Log.d(TAG, "------destFilePath = " + destFilePath);
                                    new File(mVideoFilePath).renameTo(new File(destFilePath));
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "-----Error To stopRecordAsync-------");
                                e.printStackTrace();
                            } finally {
                                releaseRecorder();
                                isRecording = false;
                                isLocked = false;
                            }
                        }
                    }
                });
    }

    /**
     * 关闭Camera
     */
    public synchronized void closeCamera() {
        if (DEBUG) {
            Log.d(TAG, "-----closeCamera-----cameraId = " + mCameraId);
        }

        try {
            mCamera.release();
            mCamera = null;
        } catch (Exception e) {
            Log.e(TAG, "-----Error To closeCamera cameraId = " + mCameraId);
            e.printStackTrace();
        }
    }

    /**
     * 关闭Camera
     */
    public void closeCameraAsync() {
        if (DEBUG) {
            Log.d(TAG, "----closeCameraAsync cameraId = " + mCameraId);
        }

        Flowable.just("closeCameraAsync")
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (DEBUG) {
                            Log.d(TAG, mCamera + "-------closeCameraAsync in consumer cameraId = " + mCameraId);
                        }
                        if (mCamera != null) {
                            try {
                                mCamera.release();
                                mCamera = null;
                            } catch (Exception e) {
                                Log.e(TAG, "-----Error To closeCameraAsync cameraId = " + mCameraId);
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 设置下一个要保存的文件路径
     * 用于循环录制
     */
    public void setNextSaveFileAsync() {
        isLocked = false; // 锁定只针对单次录制
        mVideoFilePath = Storage.getVideoFile(mCameraType);

        Flowable.just("setNextSaveFileAsync")
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (DEBUG) {
                            Log.d(TAG, "-----setNextSaveFileAsync-----cameraId = " + mCameraId);
                        }
                        try {
                            Class<MediaRecorder> cls = MediaRecorder.class;
                            Method setNextSaveFile = cls.getMethod("setNextSaveFile", String.class);
                            setNextSaveFile.invoke(mRecorder, mVideoFilePath);
                        } catch (Exception e) {
                            Log.e(TAG, "-----Error To setNextSaveFileAsync");
                            e.printStackTrace();
                        }
                    }
                });
    }

}
