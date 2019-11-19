package com.nontivi.nonton.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.flexbox.FlexboxLayout;
import com.nontivi.nonton.R;
import com.nontivi.nonton.util.ClickUtil;

import java.util.ArrayList;


/**
 * Created by mac on 11/17/17.
 */

public class CustomTabBarView extends RelativeLayout {
    public static int mSelectedTab = 0;
    public ViewPager.OnPageChangeListener delegatePageListener;
    private ArrayList<TabView> tabViews;
    private View view;
    private FlexboxLayout flexboxLayout;
    private float mOffset = 0f;
    private Context context;
    private ViewPager viewPager;

    public CustomTabBarView(Context context) {
        super(context);
        init(context);
    }

    public CustomTabBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTabBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        setWillNotDraw(false);
    }

//    public CustomTabBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context);
//    }

    public void init(Context context) {
        this.context = context;
        view = View.inflate(context, R.layout.item_custom_tab_bar_view, this);
        flexboxLayout = (FlexboxLayout) view.findViewById(R.id.flexbox_tab_bar);
        tabViews = new ArrayList<>();
    }

    public void addTabView(final int idx, int icon, int selectedIcon, String text) {
        if (tabViews != null) {
            final TabView tabView = new TabView(context);
            tabView.setIdx(idx);
            tabView.setIcon(icon);
            tabView.setText(text);
            tabView.setSelectedIcon(selectedIcon);
            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    viewPager.setCurrentItem(idx, false);
                }
            });
//            tabView.setOnTouchListener(new OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            // PRESSED
//                            tabView.updateView(true);
//                            return true; // if you want to handle the touch event
//                        case MotionEvent.ACTION_UP:
//                            tabView.updateView(false);
//                            viewPager.setCurrentItem(idx, false);
//                            return true; // if you want to handle the touch event
//                        case MotionEvent.ACTION_CANCEL:
//                            tabView.updateView(false);
//                            return true;
//                    }
//                    return false;
//                }
//            });
            tabViews.add(idx, tabView);
        }
    }

    public void addTabView(final int idx, int icon, int iconColor, int selectedIconColor) {
        if (tabViews != null) {
            final TabView tabView = new TabView(context);
            tabView.setIdx(idx);
            tabView.setIcon(icon);
            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    viewPager.setCurrentItem(idx, false);
                }
            });
            tabViews.add(idx, tabView);
        }
    }

    public void addTabView(final int idx, int icon) {
        if (tabViews != null) {
            final TabView tabView = new TabView(context);
            tabView.setIdx(idx);
            tabView.setIcon(icon);
            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtil.isFastDoubleClick()) return;
                    viewPager.setCurrentItem(idx, false);
                }
            });
            tabViews.add(idx, tabView);
        }
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;

        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        notifyDataSetChanged();
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mSelectedTab = position;
        mOffset = positionOffset;

        invalidate();

        if (delegatePageListener != null) {
            delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        //based on the position setting tab alpha as 1 if it's selected or others set is as 0.5f
        for (TabView tabView : tabViews) {
            tabView.updateView(tabView.getIdx() == position);
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
        }

        if (delegatePageListener != null) {
            delegatePageListener.onPageScrollStateChanged(state);
        }
    }

    public void onPageSelected(int position) {
        if (delegatePageListener != null) {
            delegatePageListener.onPageSelected(position);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }


    public void notifyDataSetChanged() {
        if (viewPager == null) {
            throw new IllegalStateException("ViewPager is null.");
        }
        this.flexboxLayout.removeAllViews();

        int tabCount = viewPager.getAdapter().getCount();
        for (int i = 0; i < tabCount; i++) {
            this.flexboxLayout.addView(tabViews.get(i));
        }

        //removing swiping animation effect from viewPager
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mSelectedTab = viewPager.getCurrentItem();
            }
        });

    }
}
