package com.softwinner.bionrecorder.bean;

import android.graphics.Bitmap;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class VideoInfo {
    public int hashcode;
    public String name;
    public int duration;
    public String path;
    public long size;
    public Bitmap bitmap;

    public VideoInfo(int hashcode, String name, int duration, String path, long size, Bitmap bitmap) {
        this.hashcode = hashcode;
        this.name = name;
        this.duration = duration;
        this.path = path;
        this.size = size;
        this.bitmap = bitmap;
    }
}
