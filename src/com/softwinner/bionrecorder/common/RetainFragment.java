package com.softwinner.bionrecorder.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class RetainFragment extends Fragment{
    private static final String TAG = "RetainFragment";
    private static final boolean DEBUG = true;

    public ThumbCache mThumbCache;

    public static RetainFragment findOrCreateRetainFragment(FragmentManager fragmentManager) {
        RetainFragment fragment = (RetainFragment) fragmentManager.findFragmentByTag(TAG);

        if (fragment == null) {
            fragment = new RetainFragment();
            fragmentManager.beginTransaction().add(fragment, TAG).commit();
        }

        return fragment;
    }

}
