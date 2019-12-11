package com.nontivi.nonton.features.setting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nontivi.nonton.R;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.features.home.HomeActivity;
import com.nontivi.nonton.injection.component.ActivityComponent;
import com.nontivi.nonton.util.ClickUtil;
import com.nontivi.nonton.util.LocaleUtil;

import butterknife.BindView;

public class ChangeLangActivity extends BaseActivity {


    @BindView(R.id.tv_box_title)
    TextView mTvTitle;

    @BindView(R.id.ib_back)
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTvTitle.setText(getString(R.string.setting_change_lang));
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String lang = LocaleUtil.getLanguage(this);

        switch (lang) {
            case "id":
                ((TextView)findViewById(R.id.tv_id)).setTextColor(getResources().getColor(R.color.primary));
                findViewById(R.id.ivSelected_id).setVisibility(View.VISIBLE);
                findViewById(R.id.ivSelected_en).setVisibility(View.GONE);
                break;
            case "en":
            default:
                ((TextView)findViewById(R.id.tv_en)).setTextColor(getResources().getColor(R.color.primary));
                findViewById(R.id.ivSelected_en).setVisibility(View.VISIBLE);
                findViewById(R.id.ivSelected_id).setVisibility(View.GONE);
                break;
        }

        findViewById(R.id.rl_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                LocaleUtil.setLocale(ChangeLangActivity.this, "id");
                //recreate();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        findViewById(R.id.rl_en).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                LocaleUtil.setLocale(ChangeLangActivity.this, "en");
                //recreate();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }

    @Override
    public int getLayout() {
        return R.layout.activity_change_lang;
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {

    }

    @Override
    protected void attachView() {
    }

    @Override
    protected void detachPresenter() {

    }


}
