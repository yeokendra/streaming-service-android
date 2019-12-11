package com.nontivi.nonton.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.nontivi.nonton.MvpStarterApplication;

public final class ViewUtil {

    public static float pxToDp(float px) {
        float densityDpi = Resources.getSystem().getDisplayMetrics().densityDpi;
        return px / (densityDpi / 160f);
    }

    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static <T extends View> T findViewById(View root, int viewId) {
        View view = root.findViewById(viewId);
        return (T) view;
    }

    public static <T extends View> T findViewById(Activity activity, int viewId) {
        View view = activity.findViewById(viewId);
        return (T) view;
    }

    public static void setRoundImageUrl(View root, int viewId, int img) {
        ImageView view = findViewById(root, viewId);
        Glide.with(MvpStarterApplication.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .format(DecodeFormat.PREFER_RGB_565))
                .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(20)))
                .load(img)
                .into(view);
    }

    public static void setRoundImageUrl(Activity activity, int viewId, int img) {
        ImageView view = findViewById(activity, viewId);
        Glide.with(MvpStarterApplication.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .format(DecodeFormat.PREFER_RGB_565))
                .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(20)))
                .load(img)
                .into(view);
    }

    public static void setRoundImageUrl(Activity activity, int viewId, String imgUrl, int placeholder) {
        ImageView view = findViewById(activity, viewId);
        Glide.with(MvpStarterApplication.getContext())
                .asBitmap()
                .apply(RequestOptions
                        .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                        .error(ContextCompat.getDrawable(MvpStarterApplication.getContext(), placeholder))
                        .format(DecodeFormat.PREFER_RGB_565))
                .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(20)))
                .load(imgUrl)
                .into(view);
    }
}
