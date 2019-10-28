package com.softwinner.bionrecorder.bean;

import android.graphics.Bitmap;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class ImageInfo {
    public int hashcode;
    public String name;
    public String path;
    public Bitmap bitmap;

    public ImageInfo(int hashcode, String name, String path, Bitmap bitmap) {
        this.hashcode = hashcode;
        this.name = name;
        this.path = path;
        this.bitmap = bitmap;
    }
}
