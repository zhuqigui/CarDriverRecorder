package com.softwinner.bionrecorder.common;

import android.app.Application;

import com.softwinner.bionrecorder.util.Storage;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class BionApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Storage.init(getApplicationContext());
    }
}
