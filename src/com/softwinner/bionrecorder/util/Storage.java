package com.softwinner.bionrecorder.util;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.Toast;

import com.softwinner.bionrecorder.bean.VideoInfo;
import com.softwinner.bionrecorder.common.AppConfig;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * @author zhongzhiwen
 * @date 2017/9/5
 * @mail zhongzhiwen24@gmail.com
 */

public class Storage {
    private static final String TAG = "Storage";
    private static final boolean DEBUG = true;
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 反射获取内、外存储设备路径
     *
     * @return
     */
    public static String[] getStoragePaths() {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method getState = storageVolumeClazz.getMethod("getState");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);

            ArrayList<String> pathList = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);

                String state = (String) getState.invoke(storageVolumeElement);
                if ("mounted".equals(state)) {
                    pathList.add(path);
                }
            }
            if (pathList != null && pathList.size() > 0) {
                String paths[] = new String[pathList.size()];
                pathList.toArray(paths);
                return paths;
            }
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存照片
     * @param data
     */
    public static void savePicture(byte[] data) {
        if (DEBUG) {
            Log.d(TAG, "------savePicture-----");
        }
        String picturePath = AppConfig.getInstance(mContext).getPicturePath() + "/" +
                Utils.getCurrentDateTime() + ".jpg";
        File file = new File(picturePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);

            Toast.makeText(mContext, "拍照成功", Toast.LENGTH_SHORT).show();
            return;
        } catch (FileNotFoundException e) {
            Toast.makeText(mContext, "拍照失败", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "-----Fail To Take Picture For FileNotFoundException----");
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(mContext, "拍照失败", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "-----Fail To Take Picture For IOException----");
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取视频文件名
     * @return
     */
    public static String getVideoFile(int cameraType) {
        String suffix;
        if (Build.VERSION.SDK_INT >= 21) {
            suffix = ".ts";
        } else {
            suffix = ".mp4";
        }
        if (cameraType == AppConfig.FRONT_CAMERA) {
            // 前路
            return AppConfig.getInstance(mContext).getFrontVideoPath() + "/" +
                    Utils.getCurrentDateTime() + suffix;
        } else if (cameraType == AppConfig.BACK_CAMERA) {
            // 后路
            return AppConfig.getInstance(mContext).getBackVideoPath() + "/" +
                    Utils.getCurrentDateTime() + suffix;
        }

        Consumer<VideoInfo> onNext = new Consumer<VideoInfo>() {
            @Override
            public void accept(VideoInfo videoInfo) throws Exception {

            }
        };

        Action onComplete = new Action() {
            @Override
            public void run() throws Exception {

            }
        };
        VideoInfo videoInfo = null;
        Flowable.just(videoInfo).subscribe(onNext, null, onComplete);

        return null;
    }

    /**
     * 查询是否有足够的空间用于录制（可覆盖）或拍照（不可覆盖）
     * @param path
     * @param type 1为录像，2为拍照
     * @param size 需要的空间
     * @return
     */
    public static boolean hasEnoughSpace(String path, int type, long size) {
        if (type == 1) {
            if (getRemainingSpace(path) > size) {
                return true;
            } else if (getAvailableSpace(path) > size) {
                // 清理出足够的空间
                cleanFiles(path, size - getRemainingSpace(path));
                return true;
            } else {
                return false;
            }

        } else if (type == 2) {
            if (getRemainingSpace(path) > size) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * 清理空间，先清理后录视频文件，再清理前录视频文件
     * @param path
     */
    private static void cleanFiles(String path, long needSize) {
        long cleanSize = 0;

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (!pathname.getName().toLowerCase().contains("lock"));
            }
        };

        File backDir = new File(path + "/DVR/back");
        File[] backFiles = backDir.listFiles(filter);
        for (File file : backFiles) {
            if (DEBUG) {
                Log.e(TAG, "---will delete " + file.getPath());
            }
            file.delete();
            cleanSize += file.length();
            if (cleanSize > needSize) {
                break;
            }
        }

        if (cleanSize > needSize) {
            return;
        }

        File frontDir = new File(path + "/DVR/front");
        File[] frontFiles = frontDir.listFiles();
        for (File file : frontFiles) {
            if (DEBUG) {
                Log.e(TAG, "---will delete " + file.getPath());
            }
            file.delete();
            cleanSize += file.length();
            if (cleanSize > needSize) {
                break;
            }
        }
    }

    /**
     * 获得对应路径的可用空间（循环录制，是可以覆盖原来的视频文件的）
     * @param path
     * @return
     */
    private static long getAvailableSpace(String path) {
        long totalSize = 0;

        totalSize += getVideosSize(path + "/DVR/front");
        totalSize += getVideosSize(path + "/DVR/back");
        totalSize += getRemainingSpace(path);

        return totalSize;
    }


    /**
     * 获取视频文件所占的大小（非紧急视频文件）
     * @return
     */
    private static long getVideosSize(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        long totalSize = 0;
        for (File file : files) {
            if (!file.getName().toLowerCase().contains("lock")) {
                totalSize += file.length();
            }
        }

        return totalSize;
    }


    /**
     * 获得对应路径的剩余空间
     * @param path
     * @return
     */
    private static long getRemainingSpace(String path) {
        if (new File(path).exists()) {
            StatFs statFs = new StatFs(path);
            long blockSize = statFs.getBlockSizeLong();
            long totalBlocks = statFs.getBlockCountLong();
            return blockSize * totalBlocks;
        }

        return 0;
    }
}
