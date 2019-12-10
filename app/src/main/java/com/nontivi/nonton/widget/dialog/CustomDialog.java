package com.nontivi.nonton.widget.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.nontivi.nonton.R;
import com.nontivi.nonton.util.ColorUtil;


/**
 * Created by mac on 5/26/17.
 */

public class CustomDialog extends BaseDialog implements View.OnClickListener {

    protected final Builder builder;
    protected RelativeLayout positiveButton;
    protected RelativeLayout negativeButton;
    protected TextView positiveButtonTxt;
    protected TextView negativeButtonTxt;
    protected TextView mdTitle;
    protected ImageView mdIcon;
    protected TextView mdContent;
    protected View mdDivider;
    protected RelativeLayout mdBackground;
    protected RelativeLayout lButtonContainer;
    protected LinearLayout mdCustomView;

    protected DialogTheme theme = DialogDefaultConfig.DEFALUT_DIALOG_THEME;
    protected DialogOptionType optionType = DialogDefaultConfig.DEFAULT_OPTION_TYPE;

    protected Activity activity;
    protected MaterialDialogButtonCallback onPositiveCallback;
    protected MaterialDialogButtonCallback onNegativeCallback;
    protected DialogInterface.OnDismissListener onDismissListener;
    protected DialogInterface.OnShowListener onShowListener;

    protected boolean cancelOnTouchOutside = true;
    protected boolean autoDismiss = true;
    protected boolean enableOnBackPressed = true;
    View lastPressedView;
    private String defaultPositiveText = "Yes";
    private String defaultNegativeText = "No";

    public CustomDialog(Builder builder) {
        super(builder.context, R.style.MaterialDialogSheet);
        this.builder = builder;
        this.theme = builder.theme;
        this.optionType = builder.optionType;

        final LayoutInflater inflater = LayoutInflater.from(builder.context);
        view = (RelativeLayout) inflater.inflate(DialogInit.getInflateLayout(theme), null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        //App.setTextViewStyle(view);
        DialogInit.init(this);
    }

    @Override
    @UiThread
    public void show() {
        try {
            super.show();
        } catch (WindowManager.BadTokenException e) {
            throw new WindowManager.BadTokenException(
                    "Bad window token, you cannot show a dialog "
                            + "before an Activity is created or after it's hidden.");
        }
    }

    public void animateButtonRelease(final View container) {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(container, "scaleX", 1.0f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(container, "scaleY", 1.0f);
        scaleUpX.setDuration(300);
        scaleUpY.setDuration(300);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(scaleUpX).with(scaleUpY);
        scaleUp.setInterpolator(new OvershootInterpolator());

        scaleUpX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                View p = (View) container.getParent();
                p.invalidate();
            }
        });

        scaleUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        scaleUp.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        boolean isClickOther = false;
        switch (v.getId()) {
            case R.id.md_background:
                if (cancelOnTouchOutside) {
                    if (onNegativeCallback != null) {
                        onNegativeCallback.onClick(this, DialogAction.NEGATIVE);
                    }
                    dismiss();
                }
                break;
            case R.id.positive_button:
                if (onPositiveCallback != null) {
                    onPositiveCallback.onClick(this, DialogAction.POSITIVE);
                }
                break;
            case R.id.negative_button:
                if (onNegativeCallback != null) {
                    onNegativeCallback.onClick(this, DialogAction.NEGATIVE);
                }
                break;
            default:
                isClickOther = true;

        }

        if (autoDismiss && !isClickOther) {
            if (onNegativeCallback != null) {
                onNegativeCallback.onClick(this, DialogAction.NEGATIVE);
            }
            dismiss();
        }


    }

    @Override
    public void onShow(DialogInterface dialog) {
        super.onShow(dialog);
    }

    public interface MaterialDialogButtonCallback {
        void onClick(@NonNull CustomDialog dialog, @NonNull DialogAction which);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public static class Builder {
        protected final Context context;
        protected CharSequence title;
        protected CharSequence content;
        protected CharSequence okText;
        protected CharSequence positiveText;
        protected CharSequence negativeText;

        protected boolean positiveFocus;
        protected boolean okFocus;
        protected boolean negativeFocus;
        protected int widgetColor;
        protected ColorStateList okColor;
        protected ColorStateList positiveColor;
        protected ColorStateList negativeColor;
        protected ColorStateList linkColor;
        protected ButtonCallback callback;
        protected DialogOptionType optionType;
        protected boolean showNotTodayBtn;

        protected MaterialDialogButtonCallback onTodayCloseCallback;
        protected MaterialDialogButtonCallback onPositiveCallback;
        protected MaterialDialogButtonCallback onNegativeCallback;
        protected DialogTheme theme = DialogTheme.BASIC;
        protected boolean cancelable = true;
        protected boolean canceledOnTouchOutside = true;
        protected boolean autoDismiss = true;
        protected Drawable icon;
        protected String iconUrl;
        protected DialogInterface.OnDismissListener dismissListener;
        protected DialogInterface.OnCancelListener cancelListener;
        protected DialogInterface.OnShowListener showListener;
        protected int backgroundColor;
        protected int contentColor;
        protected int titleColor;
        protected int iconBackgroundColor;
        protected int itemColor;

        protected boolean titleColorSet = false;
        protected boolean contentColorSet = false;
        protected boolean itemColorSet = false;
        protected boolean positiveColorSet = false;
        protected boolean negativeColorSet = false;
        protected boolean widgetColorSet = false;
        protected boolean dividerColorSet = false;
        protected boolean isNegativeTextBold = false;
        protected boolean isPositiveTextBold = true;
        protected boolean isOkTextBold = true;

        protected View customView;

        public Builder(Context context) {
            this.context = context;
        }

        public final Context getContext() {
            return context;
        }

        public final int getItemColor() {
            return itemColor;
        }


        public Builder title(@StringRes int titleRes) {
            title(this.context.getText(titleRes));
            return this;
        }

        public Builder title(@NonNull CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder titleColor(@ColorInt int color) {
            this.titleColor = color;
            this.titleColorSet = true;
            return this;
        }

        public Builder titleColorRes(@ColorRes int colorRes) {
            return titleColor(ContextCompat.getColor(this.context, colorRes));
        }

        public Builder optionType(DialogOptionType optionType) {
            this.optionType = optionType;
            return this;
        }

        public Builder icon(@NonNull Drawable icon) {
            this.icon = icon;
            return this;
        }

        public Builder icon(@DrawableRes int icon) {
            this.icon = ContextCompat.getDrawable(context, icon);
            return this;
        }

        public Builder iconUrl(String iconId) {
            this.iconUrl = iconId;
            return this;
        }

        public Builder iconRes(@DrawableRes int icon) {
            this.icon = ResourcesCompat.getDrawable(context.getResources(), icon, null);
            return this;
        }

        public Builder content(@StringRes int contentRes) {
            return content(contentRes, false);
        }

        public Builder content(@StringRes int contentRes, boolean html) {
            CharSequence text = this.context.getText(contentRes);
            if (html) {
                text = Html.fromHtml(text.toString().replace("\n", "<br/>"));
            }
            return content(text);
        }

        public Builder content(@NonNull CharSequence content) {
            this.content = content;
            return this;
        }

        public Builder content(@StringRes int contentRes, Object... formatArgs) {
            String str =
                    String.format(this.context.getString(contentRes), formatArgs).replace("\n", "<br/>");
            //noinspection deprecation
            return content(Html.fromHtml(str));
        }

        public Builder contentColor(@ColorInt int color) {
            this.contentColor = color;
            this.contentColorSet = true;
            return this;
        }

        public Builder contentColorRes(@ColorRes int colorRes) {
            contentColor(ContextCompat.getColor(this.context, colorRes));
            return this;
        }

        public Builder itemsColor(@ColorInt int color) {
            this.itemColor = color;
            this.itemColorSet = true;
            return this;
        }

        public Builder itemsColorRes(@ColorRes int colorRes) {
            return itemsColor(ContextCompat.getColor(this.context, colorRes));
        }

        public Builder okText(@NonNull CharSequence message) {
            this.okText = message;
            return this;
        }

        public Builder okColor(@NonNull ColorStateList colorStateList) {
            this.okColor = colorStateList;
            this.positiveColorSet = true;
            return this;
        }

        public Builder positiveText(@StringRes int positiveRes) {
            if (positiveRes == 0) {
                return this;
            }
            positiveText(this.context.getText(positiveRes));
            return this;
        }

        public Builder positiveText(@NonNull CharSequence message) {
            this.positiveText = message;
            return this;
        }

        public Builder positiveColor(@ColorInt int color) {
            return positiveColor(ColorUtil.getActionTextStateList(context, color));
        }

        public Builder positiveColor(@NonNull ColorStateList colorStateList) {
            this.positiveColor = colorStateList;
            this.positiveColorSet = true;
            return this;
        }

        public Builder positiveFocus(boolean isFocusedDefault) {
            this.positiveFocus = isFocusedDefault;
            return this;
        }

        public Builder negativeColor(@ColorInt int color) {
            return negativeColor(ColorUtil.getActionTextStateList(context, color));
        }

        public Builder negativeColor(@NonNull ColorStateList colorStateList) {
            this.negativeColor = colorStateList;
            this.negativeColorSet = true;
            return this;
        }

        public Builder negativeText(@StringRes int negativeRes) {
            if (negativeRes == 0) {
                return this;
            }
            return negativeText(this.context.getText(negativeRes));
        }

        public Builder negativeText(@NonNull CharSequence message) {
            this.negativeText = message;
            return this;
        }

        public Builder negativeFocus(boolean isFocusedDefault) {
            this.negativeFocus = isFocusedDefault;
            return this;
        }

        public Builder isNegativeTextBold(@NonNull boolean isNegativeTextBold) {
            this.isNegativeTextBold = isNegativeTextBold;
            return this;
        }

        public Builder isPositiveTextBold(@NonNull boolean isPositiveTextBold) {
            this.isPositiveTextBold = isPositiveTextBold;
            return this;
        }

        public Builder isOkTextBold(@NonNull boolean isOkTextBold) {
            this.isOkTextBold = isOkTextBold;
            return this;
        }


        public Builder backgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder backgroundColorRes(@ColorRes int colorRes) {
            return backgroundColor(ContextCompat.getColor(this.context, colorRes));
        }


        public Builder callback(@NonNull ButtonCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder onPositive(@NonNull MaterialDialogButtonCallback callback) {
            this.onPositiveCallback = callback;
            return this;
        }

        public Builder onNegative(@NonNull MaterialDialogButtonCallback callback) {
            this.onNegativeCallback = callback;
            return this;
        }

        public Builder theme(@NonNull DialogTheme theme) {
            this.theme = theme;
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            this.canceledOnTouchOutside = cancelable;
            return this;
        }

        public Builder canceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder autoDismiss(boolean dismiss) {
            this.autoDismiss = dismiss;
            return this;
        }

        public Builder showListener(@NonNull DialogInterface.OnShowListener listener) {
            this.showListener = listener;
            return this;
        }

        public Builder dismissListener(@NonNull DialogInterface.OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder cancelListener(@NonNull DialogInterface.OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }

        public Builder addCustomView(@NonNull View customView) {
            this.customView = customView;
            return this;
        }

        @UiThread
        public CustomDialog build() {
            return new CustomDialog(this);
        }

        @UiThread
        public CustomDialog show() {
            CustomDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }

    @SuppressWarnings({"WeakerAccess", "UnusedParameters"})
    @Deprecated
    public abstract static class ButtonCallback {

        public ButtonCallback() {
            super();
        }

        @Deprecated
        public void onAny(CustomDialog dialog) {
        }

        @Deprecated
        public void onPositive(CustomDialog dialog) {
        }

        @Deprecated
        public void onNegative(CustomDialog dialog) {
        }

        // The overidden methods below prevent Android Studio from suggesting that they are overidden by developers

        @Deprecated
        public void onNeutral(CustomDialog dialog) {
        }

        @Override
        protected final Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public final boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        protected final void finalize() throws Throwable {
            super.finalize();
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        @Override
        public final String toString() {
            return super.toString();
        }
    }


}
