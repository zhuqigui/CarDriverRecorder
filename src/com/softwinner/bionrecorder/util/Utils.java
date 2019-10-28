package com.softwinner.bionrecorder.util;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.sql.DriverManager.println;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class Utils {
    private static final String TAG = "Utils";
    private static final boolean DEBUG = true;

    /**
     * 获得当前时间的格式化字符串表示，用于文件命名
     * @return
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = formater.format(new Date(System.currentTimeMillis()));

        if (DEBUG) {
            Log.d(TAG, "----getCurrentDateTime = " + date);
        }

        return date;
    }

    /**
     * 获得当前时间的格式化字符串表示，用于水印
     * @return
     */
    public static String getCurrentTimeForWaterMark() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formater.format(new Date(System.currentTimeMillis()));

        if (DEBUG) {
            Log.d(TAG, "----getCurrentTimeForWaterMark = " + date);
        }

        return date;
    }

    /**
     * 判断当前camera是否是松涵的
     * @param cameraId
     * @return
     */
    public static int isUVCCameraSonix(int cameraId) {
        if (cameraId == 0 && new File("/dev/video1").exists()) {
            return 1;
        }

        return cameraId;
    }

    /**
     * 格式化录制时间
     * @param duration
     * @return
     */
    public static String getFormatRecordTime(long duration) {
        int hours = (int)(duration / 3600);
        int temp = (int)duration % 3600;
        int minutes = temp / 60;
        temp %= 60;
        int seconds = temp;

        String result;
        if (String.valueOf(hours).length() == 1) {
            result = ("0" + hours + ":");
        } else {
            result = (hours + ":");
        }
        if (String.valueOf(minutes).length() == 1) {
            result += ("0" + minutes + ":");
        } else {
            result += (minutes + ":");
        }
        if (String.valueOf(seconds).length() == 1) {
            result += ("0" + seconds);
        } else {
            result += seconds;
        }

        if (DEBUG) {
            Log.d(TAG, "-----getFormatRecordTime = " + result);
        }

        return result;
    }

    /**
     * 获得bitmap的size
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }

        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
