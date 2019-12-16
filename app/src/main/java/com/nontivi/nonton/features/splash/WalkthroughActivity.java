package com.nontivi.nonton.features.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nontivi.nonton.R;
import com.nontivi.nonton.data.model.Appdata;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.features.home.HomeActivity;
import com.nontivi.nonton.features.splash.walkthrough.FirstFragment;
import com.nontivi.nonton.features.splash.walkthrough.SecondFragment;
import com.nontivi.nonton.features.splash.walkthrough.ThirdFragment;
import com.nontivi.nonton.injection.component.ActivityComponent;

import butterknife.BindView;
import io.realm.Realm;

import static com.nontivi.nonton.app.ConstantGroup.LOG_TAG;

public class WalkthroughActivity extends BaseActivity {

    public static final int FRAGMENT_FIRST = 0;
    public static final int FRAGMENT_SECOND = 1;
    public static final int FRAGMENT_THIRD = 2;
    public static final int PAGE_COUNT = 3;

    private Realm mRealm;
    private FirstFragment firstFragment;
    private SecondFragment secondFragment;
    private ThirdFragment thirdFragment;

    @BindView(R.id.cvp_walkthrough)
    ViewPager mCustomViewPager;

    @BindView(R.id.btn_next)
    LinearLayout mNextButton;

    @BindView(R.id.btn_back)
    LinearLayout mBackButton;

    @BindView(R.id.btn_skip)
    TextView mSkipButton;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
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
        super.onCreate(savedInstanceState);

        mRealm = Realm.getDefaultInstance();

        mBackButton.setVisibility(View.GONE);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomViewPager.arrowScroll(View.FOCUS_LEFT);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCustomViewPager.getCurrentItem() != PAGE_COUNT -1){
                    mCustomViewPager.arrowScroll(View.FOCUS_RIGHT);
                }else{
                    finishWalkthrough();
                }
            }
        });

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishWalkthrough();
            }
        });

        setWalkthroughPager();
        setPagerListener();

    }

    private void finishWalkthrough(){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Appdata appdata = realm.where(Appdata.class).equalTo("id", Appdata.ID_WALKTHROUGH).findFirst();
                if(appdata != null){
                    appdata.setValue(1);

                } else {
                    appdata = mRealm.createObject(Appdata.class);
                    appdata.setId(Appdata.ID_WALKTHROUGH);
                    appdata.setValue(1);
                }
            }
        });

        Intent intent = new Intent();
        intent.setClass(WalkthroughActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void setWalkthroughPager() {
        WalkthroughAdapter walkthroughAdapter = new WalkthroughAdapter(getSupportFragmentManager());
        mCustomViewPager.setAdapter(walkthroughAdapter);
        mCustomViewPager.setOffscreenPageLimit(PAGE_COUNT);
        mCustomViewPager.setCurrentItem(0, false);
    }

    private void setPagerListener() {
        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    mBackButton.setVisibility(View.GONE);
                }else{
                    mBackButton.setVisibility(View.VISIBLE);
                }

                if(position == PAGE_COUNT-1){
                    mSkipButton.setVisibility(View.GONE);
                }else{
                    mSkipButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class WalkthroughAdapter extends FragmentStatePagerAdapter {

        WalkthroughAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case FRAGMENT_FIRST:
                    if (firstFragment == null) {
                        firstFragment = FirstFragment.newInstance();
                    }
                    return firstFragment;
                case FRAGMENT_SECOND:
                    if (secondFragment == null) {
                        secondFragment = SecondFragment.newInstance();
                    }
                    return secondFragment;
                case FRAGMENT_THIRD:
                    if (thirdFragment == null) {
                        thirdFragment = ThirdFragment.newInstance();
                    }
                    return thirdFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_walkthrough;
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
