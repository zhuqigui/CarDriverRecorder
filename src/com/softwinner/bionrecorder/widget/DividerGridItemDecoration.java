package com.softwinner.bionrecorder.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView网格布局时的ItemDecoration
 * @author: zhongzhiwen
 * @email: zhongzhiwen24@gmail
 * @date: 2017/9/17
 */

public class DividerGridItemDecoration extends RecyclerView.ItemDecoration{
    private static final int[] ATTRS = new int[]{ android.R.attr.listDivider };

    private Drawable mDivider;
    private Paint mPaint;
    private static final int DEFAULT_VERTICAL_ITEM_DECORATION_SIZE = 10;
    private static final int DEFAULT_HORIZONTAL_ITEM_DECORATION_SIZE = 10;
    private int mVerticalItemDecorationSize = DEFAULT_VERTICAL_ITEM_DECORATION_SIZE;
    private int mHorizontalItemDecorationSize = DEFAULT_HORIZONTAL_ITEM_DECORATION_SIZE;

    public DividerGridItemDecoration(Context context) {
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#ffffffff"));
    }

    /**
     * 设置竖直分割线的尺寸
     * @param size
     */
    public void setVerticalItemDecorationSize(int size) {
        mVerticalItemDecorationSize = size;
    }

    /**
     * 设置横向分割线的尺寸
     * @param size
     */
    public void setHorizontalItemDecorationSize(int size) {
        mHorizontalItemDecorationSize = size;
    }

    /**
     * 设置分割线的颜色
     */
    public void setItemDecorationColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(canvas, parent);
        drawVertical(canvas, parent);
    }

    /**
     * 横向分割线
     * @param canvas
     * @param parent
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft() - lp.leftMargin;
            int right = child.getRight() + lp.rightMargin + mDivider.getIntrinsicWidth();
            int top = child.getBottom() + lp.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    /**
     * 竖直分割线
     * @param canvas
     * @param parent
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getTop() - lp.topMargin;
            int bottom = child.getBottom() + lp.bottomMargin;
            int left = child.getRight() + lp.rightMargin;
            int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    /**
     * 判断是否是最后一列
     * @param parent
     * @param pos
     * @param spanCount
     * @return
     */
    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否是最后一行
     * @param parent
     * @param pos
     * @param spanCount
     * @param itemCount
     * @return
     */
    private boolean isLastRow(RecyclerView parent, int pos, int spanCount, int itemCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            itemCount = itemCount - itemCount % spanCount;
            if (pos >= itemCount) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int spanCount = ((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
        int itemCount = parent.getAdapter().getItemCount();
        if (isLastColumn(parent, pos, spanCount)) {
            outRect.set(0, 0, 0, mHorizontalItemDecorationSize);
        } else if (isLastRow(parent, pos, spanCount, itemCount)) {
            outRect.set(0, 0, mVerticalItemDecorationSize, 0);
        } else {
            outRect.set(0, 0, mVerticalItemDecorationSize, mHorizontalItemDecorationSize);
        }
    }
}
