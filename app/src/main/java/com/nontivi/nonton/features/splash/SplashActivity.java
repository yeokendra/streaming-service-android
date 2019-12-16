package com.nontivi.nonton.features.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nontivi.nonton.R;
import com.nontivi.nonton.data.model.Appdata;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.features.home.HomeActivity;
import com.nontivi.nonton.injection.component.ActivityComponent;
import com.nontivi.nonton.util.ClickUtil;
import com.nontivi.nonton.util.LocaleUtil;

import butterknife.BindView;
import io.realm.Realm;

import static com.nontivi.nonton.app.ConstantGroup.LOG_TAG;

public class SplashActivity extends BaseActivity {

    private Realm mRealm;
    private Appdata mAppDataWalkthrough;
    private Class<? extends Activity>  mDestinationActivity;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        hideSystemUI();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mRealm != null && !mRealm.isClosed()){
            mRealm.close();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        mRealm = Realm.getDefaultInstance();

        mAppDataWalkthrough = mRealm.where(Appdata.class).equalTo("id",Appdata.ID_WALKTHROUGH).findFirst();
        if(mAppDataWalkthrough == null){
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mAppDataWalkthrough = mRealm.createObject(Appdata.class);
                    mAppDataWalkthrough.setId(Appdata.ID_WALKTHROUGH);
                    mAppDataWalkthrough.setValue(0);
                }
            });

        }

        if(mAppDataWalkthrough.getValue() == 0){
            mDestinationActivity = WalkthroughActivity.class;
        }else{
            mDestinationActivity = HomeActivity.class;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, mDestinationActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        },2000);


    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_splash;
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
