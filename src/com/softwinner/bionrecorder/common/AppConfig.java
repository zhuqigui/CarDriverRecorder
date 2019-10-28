package com.softwinner.bionrecorder.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.softwinner.bionrecorder.util.Storage;

import java.io.File;

import static android.R.attr.value;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class AppConfig {
    public static final String TAG = "AppConfig";
    public static final boolean DEBUG = true;
    // 存储路径
    public static final String DEFAULT_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath();

    public static final String PREFERENCE_NAME = "app_config";

    public static final String STORAGE_PATH = "storage_path";

    public static final String FRONT_MUTE = "front_mute"; // 是否录音
    public static final String FRONT_WATERMARK = "front_watermark"; // 是否加水印
    public static final String FRONT_PICTURE_QUALITY = "front_picture_quality";
    public static final String FRONT_RECORD_QUALITY = "front_record_quality";
    public static final String FRONT_RECORD_DURATION = "front_record_duration";

    public static final String BACK_MUTE = "back_mute"; // 是否录音
    public static final String BACK_WATERMARK = "back_watermark"; // 是否加水印
    public static final String BACK_PICTURE_QUALITY = "back_picture_quality";
    public static final String BACK_RECORD_QUALITY = "back_record_quality";
    public static final String BACK_RECORD_DURATION = "back_record_duration";

    /*
     * 默认配置
     */
    public static final int CAMERA_AMOUNT = 2; // 默认为两路
    public static final int FRONT_CAMERA = 1; // 前路摄像头
    public static final int BACK_CAMERA = 2; // 后路摄像头
    public static final int FRONT_CAMERA_ID = 0; // 前路摄像头id
    public static final int BACK_CAMERA_ID = 4; // 后路摄像头id

    public static final int DEFAULT_PICTURE_QUALITY = C.PictureQuality.PICTURE_QUALITY_720P;
    public static final int DEFAULT_RECORD_QUALITY = C.RecordQuality.RECORD_QUALITY_720P;
    public static final int DEFAULT_RECORD_DURATION = C.RecordDuraton.ONE_MINUTE_DURATION;

    private static AppConfig mAppconfig;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public AppConfig(Context context) {
        mPref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
    }


    public static AppConfig getInstance(Context context) {
        if (mAppconfig == null) {
            mAppconfig = new AppConfig(context);
        }

        return mAppconfig;
    }

    public String getString(String key, String defValue) {
        return mPref.getString(key, defValue);
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value).commit();
    }

    public void putInt(String key, int value) {
        mEditor.putInt(key, value).commit();
    }

    public int getInt(String key, int defValue) {
        return mPref.getInt(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPref.getBoolean(key, defValue);
    }

    /**
     * 获取前置摄像头拍摄的视频的存储位置
     */
    public String getFrontVideoPath() {
        // 先检查已经设置的路径是否存在（如果是设置了外置sd卡，该卡可能被拔出）
        String[] paths = Storage.getStoragePaths();
        String path = mPref.getString(STORAGE_PATH, DEFAULT_STORAGE_PATH);
        boolean exist = false;
        for (String p : paths) {
            if (p.equals(path)) {
                exist = true;
            }
        }
        if (!exist) {
            path = DEFAULT_STORAGE_PATH;
            mEditor.putString(STORAGE_PATH, path);
        }

        path += "/DVR/front";
        if (DEBUG) Log.d(TAG, "-------getFrontVideoPath-------" + path);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return path;
    }

    /**
     * 获取后置摄像头拍摄的视频的存储位置
     */
    public String getBackVideoPath() {
        // 先检查已经设置的路径是否存在（如果是设置了外置sd卡，该卡可能被拔出）
        String[] paths = Storage.getStoragePaths();
        String path = mPref.getString(STORAGE_PATH, DEFAULT_STORAGE_PATH);
        boolean exist = false;
        for (String p : paths) {
            if (p.equals(path)) {
                exist = true;
            }
        }
        if (!exist) {
            path = DEFAULT_STORAGE_PATH;
            mEditor.putString(STORAGE_PATH, path);
        }

        path += "/DVR/back";
        if (DEBUG) Log.d(TAG, "-------getBackVideoPath-------" + path);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return path;
    }

    /**
     * 获取照片的存储位置
     */
    public String getPicturePath() {
        // 先检查已经设置的路径是否存在（如果是设置了外置sd卡，该卡可能被拔出）
        String[] paths = Storage.getStoragePaths();
        String path = mPref.getString(STORAGE_PATH, DEFAULT_STORAGE_PATH);
        boolean exist = false;
        for (String p : paths) {
            if (p.equals(path)) {
                exist = true;
            }
        }
        if (!exist) {
            path = DEFAULT_STORAGE_PATH;
            mEditor.putString(STORAGE_PATH, path);
        }

        path += "/DVR/picture";
        if (DEBUG) Log.d(TAG, "-------getPicturePath-------" + path);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return path;
    }
}
