package com.nontivi.nonton.features.home;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nontivi.nonton.BuildConfig;
import com.nontivi.nonton.R;
import com.nontivi.nonton.app.ConstantGroup;
import com.nontivi.nonton.data.model.Setting;
import com.nontivi.nonton.data.response.HttpResponse;
import com.nontivi.nonton.data.response.SettingListResponse;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.features.common.ErrorView;
import com.nontivi.nonton.features.home.bookmarkpage.BookmarkFragment;
import com.nontivi.nonton.features.home.homepage.HomepageFragment;
import com.nontivi.nonton.features.home.settingpage.SettingFragment;
import com.nontivi.nonton.injection.component.ActivityComponent;
import com.nontivi.nonton.util.LocaleUtil;
import com.nontivi.nonton.util.RxBus;
import com.nontivi.nonton.widget.CustomTabBarView;
import com.nontivi.nonton.widget.CustomViewPager;
import com.nontivi.nonton.widget.dialog.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import timber.log.Timber;

import static com.nontivi.nonton.app.ConstantGroup.LATEST_VERSION;
import static com.nontivi.nonton.app.ConstantGroup.LOG_TAG;
import static com.nontivi.nonton.app.ConstantGroup.MIN_VERSION;

public class HomeActivity extends BaseActivity implements HomeMvpView, ErrorView.ErrorListener {


    public static final int HOME_FRAGMENT = 0;
    public static final int BOOKMARK_FRAGMENT = 1;
    public static final int SETTING_FRAGMENT = 2;
    public static final int PAGE_COUNT = 3;

    @Inject
    HomePresenter homePresenter;

    CustomTabBarView mCustomTabBarView;

    @BindView(R.id.custom_viewpager)
    CustomViewPager mCustomViewPager;

    private MainScreenPagerAdapter mainScreenPagerAdapter;
    private HomepageFragment homepageFragment;
    private BookmarkFragment bookmarkFragment;
    private SettingFragment settingFragment;

    private FirebaseAnalytics mFirebaseAnalytics;

    private Observable<Integer> mChannelClickedObservable;

    private ArrayList<Setting> mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, "ca-app-pub-1457023993566419~3956309691");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        LocaleUtil.onAttach(this);

        mChannelClickedObservable = RxBus.get().register(RxBus.KEY_CHANNEL_CLICKED, Integer.class);
        mChannelClickedObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer id) {
                Log.v(LOG_TAG,"Channel "+id+" clicked");

                Bundle bundle = new Bundle();
                //bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, id);
                bundle.putInt("channel_id_clicked", id);
                mFirebaseAnalytics.logEvent("channel_clicked",bundle);
            }
        });

        setToolbar();
        setCustomTabs();
        setPagerListener();
        //setSupportActionBar(toolbar);

        homePresenter.getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mChannelClickedObservable != null) {
            RxBus.get().unregister(RxBus.KEY_CHANNEL_CLICKED, mChannelClickedObservable);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void attachView() {
        homePresenter.attachView(this);
    }

    @Override
    protected void detachPresenter() {
        homePresenter.detachView();
    }

    @Override
    public void showSetting(HttpResponse<SettingListResponse> settings) {
        //pokemonAdapter.setPokemon(pokemon);
        //pokemonRecycler.setVisibility(View.VISIBLE);
        //swipeRefreshLayout.setVisibility(View.VISIBLE);

        if(settings.getMeta().getData() != null){
            mSettings = new ArrayList<>();
            mSettings.addAll(settings.getMeta().getData().settings);
        }

        for(Setting setting : mSettings){
            switch (setting.getValue()){
                case MIN_VERSION:
                    if(BuildConfig.VERSION_CODE < Integer.valueOf(setting.getValue())){
                        Toast.makeText(this, "lower than min", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case LATEST_VERSION:
                    if(BuildConfig.VERSION_CODE < Integer.valueOf(setting.getValue())){
                        Toast.makeText(this, "lower than latest", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

    }

    @Override
    public void showProgress(boolean show) {
//        if (show) {
//            if (pokemonRecycler.getVisibility() == View.VISIBLE
//                    && pokemonAdapter.getItemCount() > 0) {
//                swipeRefreshLayout.setRefreshing(true);
//            } else {
//                progressBar.setVisibility(View.VISIBLE);
//
//                pokemonRecycler.setVisibility(View.GONE);
//                swipeRefreshLayout.setVisibility(View.GONE);
//            }
//
//            errorView.setVisibility(View.GONE);
//        } else {
//            swipeRefreshLayout.setRefreshing(false);
//            progressBar.setVisibility(View.GONE);
//        }
    }

    @Override
    public void showError(Throwable error) {
//        pokemonRecycler.setVisibility(View.GONE);
//        swipeRefreshLayout.setVisibility(View.GONE);
//        errorView.setVisibility(View.VISIBLE);
//        Timber.e(error, "There was an error retrieving the pokemon");
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
    }

    public void setToolbar() {

        mCustomTabBarView = findViewById(R.id.custom_tabbarview);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        mCustomTabBarView.addTabView(HOME_FRAGMENT, R.drawable.ic_home, R.drawable.ic_home_active,getString(R.string.title_home));
        mCustomTabBarView.addTabView(BOOKMARK_FRAGMENT, R.drawable.ic_star, R.drawable.ic_star_active,getString(R.string.title_bookmark));
        mCustomTabBarView.addTabView(SETTING_FRAGMENT, R.drawable.ic_setting_dock, R.drawable.ic_setting_dock_active,getString(R.string.title_setting));

        setFragmentToolbar(0);

    }

    public void setFragmentToolbar(int fragment) {
        switch (fragment) {
            case HOME_FRAGMENT:
                break;
            case BOOKMARK_FRAGMENT:
                break;
            case SETTING_FRAGMENT:
                break;
            default:
                break;
        }
    }

    private void setPagerListener() {
        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int lastPagePosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCustomTabBarView.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                Log.e(LOG_TAG, "Page position : " + position);
                mCustomTabBarView.onPageSelected(position);
                setFragmentToolbar(position);
                Fragment fragment = mainScreenPagerAdapter.getRegisteredFragment(position);
                if (fragment != null) {
                    fragment.onResume();
                }

                Fragment lastFragment = mainScreenPagerAdapter.getRegisteredFragment(lastPagePosition);
                if (lastFragment != null) {
                    lastFragment.onPause();
                }

                lastPagePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mCustomTabBarView.onPageScrollStateChanged(state);
            }
        });
    }

    private void setCustomTabs() {
        mainScreenPagerAdapter = new MainScreenPagerAdapter(getSupportFragmentManager());
        mCustomViewPager = findViewById(R.id.custom_viewpager);
        mCustomViewPager.setAdapter(mainScreenPagerAdapter);
        mCustomTabBarView.setViewPager(mCustomViewPager);
        mCustomViewPager.setOffscreenPageLimit(PAGE_COUNT);
        mCustomViewPager.setPagingEnabled(false);
        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mCustomViewPager.setCurrentItem(0, false);
    }

    @Override
    public void onReloadData() {
        //homePresenter.getPokemon(POKEMON_COUNT);
    }

    public class MainScreenPagerAdapter extends FragmentStatePagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        MainScreenPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case HOME_FRAGMENT:
                    if (homepageFragment == null) {
                        homepageFragment = HomepageFragment.newInstance();
                    }
                    return homepageFragment;
                case BOOKMARK_FRAGMENT:
                    if (bookmarkFragment == null) {
                        bookmarkFragment = BookmarkFragment.newInstance();
                    }
                    return bookmarkFragment;
                case SETTING_FRAGMENT:
                    if (settingFragment == null) {
                        settingFragment = SettingFragment.newInstance();
                    }
                    return settingFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}
