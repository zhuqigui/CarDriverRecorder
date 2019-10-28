package com.softwinner.bionrecorder.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author zhongzhiwen
 * @date 2017/9/18
 * @email zhongzhiwen24@gmail.com
 */

public class BionRecyclerView extends RecyclerView{
    public BionRecyclerView(Context context) {
        super(context);
    }

    public BionRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
