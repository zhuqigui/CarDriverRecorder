package com.softwinner.bionrecorder.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.softwinner.bionrecorder.bean.ImageInfo;
import com.softwinner.bionrecorder.bean.VideoInfo;
import com.softwinner.bionrecorder.common.AppConfig;
import com.softwinner.bionrecorder.common.ThumbCache;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static android.R.attr.filter;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class ThumbWorker {
    private static final String TAG = "ThumbsWorker";
    private static final boolean DEBUG = true;

    /**
     * 获得前路摄像头的视频信息
     */
    public static void getImageInfos(final FragmentActivity activity,
                                          final OnLoadImageFileListener listener){
        if (DEBUG) {
            Log.d(TAG, "-------getImageInfos-----");
        }

        AppConfig appConfig = AppConfig.getInstance(activity.getApplicationContext());
        File[] dirs = new File[] {new File(appConfig.getPicturePath())};

        final ArrayList<ImageInfo> imageInfos = new ArrayList<>();

        Consumer<ImageInfo> onNext = new Consumer<ImageInfo>() {
            @Override
            public void accept(ImageInfo imageInfo) throws Exception {
                if (imageInfo != null && imageInfo.bitmap != null) {
                    if (DEBUG) Log.d(TAG, "----onNext imageInfo name---------" + imageInfo.name);
                    imageInfos.add(imageInfo);
                }
            }
        };

        Consumer<Throwable> onError = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG, "----onError get imageInfo---------");
                throwable.printStackTrace();
            }
        };

        Action onComplete = new Action() {
            @Override
            public void run() throws Exception {
                listener.onSuccess(imageInfos);
                if (DEBUG) {
                    Log.d(TAG, "----onComplete videoInfo size----------" + imageInfos.size());
                }
            }
        };

        Flowable.fromArray(dirs)
                .flatMap(new Function<File, Publisher<File>>() {
                    @Override
                    public Publisher<File> apply(@NonNull File file) throws Exception {
                        return Flowable.fromArray(file.listFiles());
                    }
                })
                .filter(new Predicate<File>() {
                    @Override
                    public boolean test(@NonNull File file) throws Exception {
                        return file.getName().endsWith(".jpg");
                    }
                })
                .map(new Function<File, ImageInfo>() {
                    @Override
                    public ImageInfo apply(@NonNull File file) throws Exception {
                        return getImageInfoFromFile(file, activity);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError, onComplete);
    }

    private static void getVideoInfosInternal(File[] dirs, final FragmentActivity activity,
                                              final boolean isLock, final OnLoadVideoFileListener listener) {

        final ArrayList<VideoInfo> videoInfos = new ArrayList<>();

        Consumer<VideoInfo> onNext = new Consumer<VideoInfo>() {
            @Override
            public void accept(VideoInfo videoInfo) throws Exception {
                if (videoInfo != null && videoInfo.bitmap != null) {
                    if (DEBUG) Log.d(TAG, "----onNext videoInfo name---------" + videoInfo.name);
                    videoInfos.add(videoInfo);
                }
            }
        };

        Consumer<Throwable> onError = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e(TAG, "----onError get videoInfo---------");
                throwable.printStackTrace();
            }
        };

        Action onComplete = new Action() {
            @Override
            public void run() throws Exception {
                listener.onSuccess(videoInfos);
                if (DEBUG) {
                    Log.d(TAG, "----onComplete videoInfo size----------" + videoInfos.size());
                }
            }
        };

        Flowable.fromArray(dirs)
                .flatMap(new Function<File, Publisher<File>>() {
                    @Override
                    public Publisher<File> apply(@NonNull File file) throws Exception {
                        return Flowable.fromArray(file.listFiles());
                    }
                })
                .filter(new Predicate<File>() {
                    @Override
                    public boolean test(@NonNull File file) throws Exception {
                        return fileFilter(isLock, file.getName());
                    }
                })
                .map(new Function<File, VideoInfo>() {
                    @Override
                    public VideoInfo apply(@NonNull File file) throws Exception {
                        return getVideoInfoFromFile(file, activity);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError, onComplete);
    }

    /**
     * 文件过滤
     */
    private static boolean fileFilter(boolean isLock, String fileName) {
        fileName = fileName.toLowerCase();
        if (isLock) {
            // 紧急视频
            return (fileName.endsWith(".ts") || fileName.endsWith(".mp4")) &&
                    (fileName.contains("lock"));
        } else {
            return (fileName.endsWith(".ts") || fileName.endsWith(".mp4")) &&
                    (!fileName.contains("lock"));
        }
    }


    /**
     * 获得前路摄像头的视频信息
     */
    public static void getFrontVideoInfos(final FragmentActivity activity,
                                          final OnLoadVideoFileListener listener){
        if (DEBUG) {
            Log.d(TAG, "-------getFrontVideoInfos-----");
        }

        File[] files = new File[] {
                new File(AppConfig.getInstance(activity.getApplicationContext()).getFrontVideoPath())
        };
        getVideoInfosInternal(files, activity, false, listener);
    }

    /**
     * 获得前路摄像头的视频信息
     */
    public static void getBackVideoInfos(final FragmentActivity activity,
                                         final OnLoadVideoFileListener listener){
        if (DEBUG) {
            Log.d(TAG, "-------getBackVideoInfos-----");
        }

        File[] files = new File[] {
                new File(AppConfig.getInstance(activity.getApplicationContext()).getBackVideoPath())
        };
        getVideoInfosInternal(files, activity, false, listener);
    }

    /**
     * 获得前路摄像头的视频信息
     */
    public static void getLockVideoInfos(final FragmentActivity activity,
                                          final OnLoadVideoFileListener listener){
        if (DEBUG) {
            Log.d(TAG, "-------getFrontVideoInfos-----");
        }

        File[] files = new File[] {
                new File(AppConfig.getInstance(activity.getApplicationContext()).getFrontVideoPath()),
                new File(AppConfig.getInstance(activity.getApplicationContext()).getBackVideoPath())
        };
        getVideoInfosInternal(files, activity, true, listener);
    }



    /**
     * 从文件中获取图片信息
     */
    private static ImageInfo getImageInfoFromFile(File file, FragmentActivity activity) {
        try {
            String name = file.getName();
            String path = file.getPath();
            int hashcode = (name + path).hashCode();

            Bitmap bitmap = ThumbCache.findOrCreateThumbsCache(activity)
                    .getBitmapFromCache(String.valueOf(hashcode));

            if (bitmap == null) {
                bitmap = BitmapFactory.decodeFile(path);
                ThumbCache.findOrCreateThumbsCache(activity)
                        .putBitmpaIntoCache(String.valueOf(hashcode), bitmap);
            }

            return new ImageInfo(hashcode, name, path, bitmap);
        } catch (Exception e) {
            Log.e(TAG, "----------Error To Get Image Info From " + file.getName());
            e.printStackTrace();
        }

        return new ImageInfo(0, "", "", null);
    }

    /**
     * 从文件中获取视频信息
     */
    private static VideoInfo getVideoInfoFromFile(File file, FragmentActivity activity) {
        try {
            String name = file.getName();
            String path = file.getPath();
            long size = file.length();
            int hashcode = (name + path).hashCode();

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);

            int duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            // 如果缓存中没有，则获取并保存到缓存中
            Bitmap bitmap = ThumbCache.findOrCreateThumbsCache(activity)
                    .getBitmapFromCache(String.valueOf(hashcode));
            if (bitmap == null) {
                bitmap = retriever.getFrameAtTime();
                ThumbCache.findOrCreateThumbsCache(activity)
                        .putBitmpaIntoCache(String.valueOf(hashcode), bitmap);
            }

            retriever.release();

            return new VideoInfo(hashcode, name, duration, path, size, bitmap);
        } catch (Exception e) {
            Log.e(TAG, "----------Error To Get Info From " + file.getName());
            e.printStackTrace();
        }

        return new VideoInfo(0, "", 0, "", 0L, null);
    }

    public interface OnLoadImageFileListener {
        void onSuccess(ArrayList<ImageInfo> imageInfos);
    }

    public interface OnLoadVideoFileListener {
        void onSuccess(ArrayList<VideoInfo> videoInfos);
    }
}
