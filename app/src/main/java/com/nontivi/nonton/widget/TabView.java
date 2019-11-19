package com.nontivi.nonton.widget;

import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nontivi.nonton.R;


/**
 * Created by mac on 11/17/17.
 */

public class TabView extends LinearLayout {
    private static int _selectedIconColor = R.color.material_black;
    private static int _iconColor = R.color.material_grey_600;
    private int idx;
    @DrawableRes
    private int selectedIcon;
    @DrawableRes
    private int icon;
    @ColorRes
    private int selectedIconColor;
    @ColorRes
    private int iconColor;
    private String text;
    private View view;
    private ImageView ivIcon;
    private TextView tvText;
    private Context context;

    public TabView(Context context) {
        super(context);
        init(context);
    }

    public TabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

//    public TabView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init(context);
//    }

    public static int get_selectedIconColor() {
        return _selectedIconColor;
    }

    public static void set_selectedIconColor(int _selectedIconColor) {
        TabView._selectedIconColor = _selectedIconColor;
    }

    public static int get_iconColor() {
        return _iconColor;
    }

    public static void set_iconColor(int _iconColor) {
        TabView._iconColor = _iconColor;
    }

    public int getSelectedIconColor() {
        return selectedIconColor;
    }

    public void setSelectedIconColor(int selectedIconColor) {
        this.selectedIconColor = selectedIconColor;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getSelectedIcon() {
        return selectedIcon;
    }

    public void setSelectedIcon(int selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
        ivIcon.setImageResource(icon);
    }

    public void setText(String text) {
        this.text = text;
        tvText.setText(text);
    }

    private void init(Context context) {
        this.context = context;
        view = View.inflate(context, R.layout.item_tab_view, this);
        ivIcon = (ImageView) view.findViewById(R.id.iv_tab_icon);
        tvText = (TextView) view.findViewById(R.id.tv_tab_text);
        text = "";
    }

    public void updateView(boolean isSelected) {
        ivIcon.setImageResource(isSelected ? selectedIcon : icon);
        tvText.setTextColor(isSelected ? getResources().getColor(R.color.text_tab_active) : getResources().getColor(R.color.text_tab));
    }

    public void updateColor(boolean isSelected) {
        if (selectedIconColor == 0 || iconColor == 0) {
            selectedIconColor = _selectedIconColor;
            iconColor = _iconColor;
        }
        ivIcon.setColorFilter(ContextCompat.getColor(context, isSelected ? selectedIconColor : iconColor));
    }
}
