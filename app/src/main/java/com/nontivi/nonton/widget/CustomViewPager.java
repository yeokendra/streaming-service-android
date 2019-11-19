package com.nontivi.nonton.widget;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by mac on 11/17/17.
 */

public class CustomViewPager extends ViewPager {
    private boolean isPagingEnabled;

    public CustomViewPager(Context context) {
        super(context);
        isPagingEnabled = false;
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isPagingEnabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.isPagingEnabled)
            return super.onTouchEvent(ev);
        else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.isPagingEnabled)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    public void setPagingEnabled(boolean isPagingEnabled) {
        this.isPagingEnabled = isPagingEnabled;
    }

}
