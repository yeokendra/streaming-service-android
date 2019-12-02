package com.nontivi.nonton.features.streaming;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;

import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.nontivi.nonton.BuildConfig;
import com.nontivi.nonton.R;
import com.nontivi.nonton.data.local.PreferencesHelper;
import com.nontivi.nonton.data.model.Appdata;
import com.nontivi.nonton.data.model.Channel;
import com.nontivi.nonton.data.model.Schedule;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.features.base.BaseRecyclerAdapter;
import com.nontivi.nonton.features.base.BaseRecyclerViewHolder;
import com.nontivi.nonton.features.common.ErrorView;
import com.nontivi.nonton.features.detail.DetailActivity;
import com.nontivi.nonton.features.main.MainMvpView;
import com.nontivi.nonton.features.main.MainPresenter;
import com.nontivi.nonton.features.main.PokemonAdapter;
import com.nontivi.nonton.injection.component.ActivityComponent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.Realm;

public class StreamActivity extends BaseActivity implements StreamMvpView {

    private final String STREAM_1 = "http://45.126.83.51/session/7b78becc-fbb2-11e9-b358-9e05a09d9a91/dr9445/h/h02/01.m3u8";

    @Inject
    StreamPresenter streamPresenter;

    @BindView(R.id.player_view)
    PlayerView playerView;

    @BindView(R.id.expand_text_view)
    ExpandableTextView tvDesc;

    @BindView(R.id.rv_schedule_day)
    RecyclerView rvScheduleDayList;

    @BindView(R.id.rv_schedule_detail)
    RecyclerView rvScheduleDetailList;

    @BindView(R.id.ib_fullscreen)
    ImageButton btnFullscreen;

    @BindView(R.id.rl_data_warning)
    RelativeLayout rlDataWarning;

    private SimpleExoPlayer player;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private Context context = this;
    private boolean playWhenReady = true;

    private BaseRecyclerAdapter<Schedule> mScheduleDetailAdapter;
    private BaseRecyclerAdapter<Schedule> mSCheduleDayAdapter;

    private ArrayList<Schedule> scheduleDayList;

    private int lastSelectedItem = 0;
    private AdView mAdView;

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);


        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Appdata appdata = realm.where(Appdata.class).equalTo("id", Appdata.ID_DATA_WARNING)
                        .findFirst();
                if(appdata!=null){
                    switch (appdata.getValue()){
                        case 0:
                            rlDataWarning.setVisibility(View.GONE);
                            break;
                        case 1:
                            rlDataWarning.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        });

        findViewById(R.id.tv_data_warning).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Appdata appdata = realm.where(Appdata.class).equalTo("id", Appdata.ID_DATA_WARNING)
                                .findFirst();
                        appdata.setValue(0);
                        rlDataWarning.setVisibility(View.GONE);
                    }
                });

            }
        });

        findViewById(R.id.tv_data_warning_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlDataWarning.setVisibility(View.GONE);
            }
        });

        tvDesc.setText("NET. Televisi Masa Kini merupakan salah satu alternatif tontonan hiburan layar kaca. NET. hadir dengan format dan konten program yang berbeda dengan stasiun TV lain. Sesuai perkembangan teknologi informasi, NET. didirikan dengan semangat bahwa konten hiburan dan informasi di masa mendatang akan semakin terhubung, lebih memasyarakat, lebih mendalam, lebih pribadi, dan lebih mudah diakses. Karena itulah, sejak awal, NET. muncul dengan konsep multiplatform, sehingga pemirsanya bisa mengakses tayangan NET. secara tidak terbatas, kapan pun, dan di mana pun.");
        tvDesc.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });

        init1stBannerAds();
        initScheduleDayList();
        initScheduleDetailList();

        btnFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    //if(Build.VERSION.SDK_INT >= 26) {

                    //}
                }

                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //if(Build.VERSION.SDK_INT >= 26) {

                    //}
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            hideSystemUI();
        }else{
            showSystemUI();
        }

    }

    @Override
    public void onBackPressed() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();
    }

    @Override
    protected void onPause() {
        releasePlayer();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        mRealm.close();
        super.onDestroy();
    }

    private void initScheduleDayList(){
        scheduleDayList = new ArrayList<>();


        scheduleDayList.add(new Schedule(1,"Monday","",0));
        scheduleDayList.add(new Schedule(2,"Tuesday","",0));
        scheduleDayList.add(new Schedule(3,"Wednesday","",0));
        scheduleDayList.add(new Schedule(4,"Thursday","",0));
        scheduleDayList.add(new Schedule(5,"Friday","",0));
        scheduleDayList.add(new Schedule(6,"Saturday","",0));
        scheduleDayList.add(new Schedule(7,"Sunday","",0));

        scheduleDayList.get(lastSelectedItem).setSelected(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        layoutManager.setItemPrefetchEnabled(false);
        mSCheduleDayAdapter = new BaseRecyclerAdapter<Schedule>(this, scheduleDayList, layoutManager) {

            @Override
            public int getItemViewType(int position) {
                return mData.get(position).getId();
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_list_schedule_day;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final Schedule item) {
                Button btnScheduleDay = holder.getButton(R.id.btn_schedule_day);

                if(item.isSelected()){
                    btnScheduleDay.setBackgroundResource(R.drawable.shape_primary_rounded_selected);
                    btnScheduleDay.setTextColor(getResources().getColor(R.color.white));
                }else{
                    btnScheduleDay.setBackgroundResource(R.drawable.shape_ripple_primary_rounded);
                    btnScheduleDay.setTextColor(getResources().getColor(R.color.primary));
                }
                btnScheduleDay.setText(item.getTitle());
                btnScheduleDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!item.isSelected()){
                            mData.get(lastSelectedItem).setSelected(false);
                            //notifyItemChanged(lastSelectedItem);
                            lastSelectedItem = position;
                            mData.get(lastSelectedItem).setSelected(true);
                            //notifyItemChanged(lastSelectedItem);
                            notifyDataSetChanged();
                        }
                    }
                });

            }
        };

        mSCheduleDayAdapter.setHasStableIds(true);
        rvScheduleDayList.setLayoutManager(layoutManager);
        rvScheduleDayList.setItemAnimator(new DefaultItemAnimator());
        if (rvScheduleDayList.getItemAnimator() != null)
            rvScheduleDayList.getItemAnimator().setAddDuration(250);
        rvScheduleDayList.getItemAnimator().setMoveDuration(250);
        rvScheduleDayList.getItemAnimator().setChangeDuration(250);
        rvScheduleDayList.getItemAnimator().setRemoveDuration(250);
        rvScheduleDayList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvScheduleDayList.setItemViewCacheSize(30);
        rvScheduleDayList.setAdapter(mSCheduleDayAdapter);
        rvScheduleDayList.setNestedScrollingEnabled(false);
    }

    private void initScheduleDetailList(){
        ArrayList<Schedule> scheduleDetailList = new ArrayList<>();

        for(int i=0; i<10; i++) {
            scheduleDetailList.add(new Schedule(i,"Selamat Pagi Indonesia","News",0));
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        layoutManager.setItemPrefetchEnabled(false);
        mScheduleDetailAdapter = new BaseRecyclerAdapter<Schedule>(this, scheduleDetailList, layoutManager) {
            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_list_schedule;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final Schedule item) {
                if(position != 0){
                    holder.getView(R.id.v_line_top).setVisibility(View.VISIBLE);
                }else{
                    holder.getView(R.id.v_line_top).setVisibility(View.GONE);
                }

                if(position != scheduleDetailList.size()-1){
                    holder.getView(R.id.v_line_bottom).setVisibility(View.VISIBLE);
                }else{
                    holder.getView(R.id.v_line_bottom).setVisibility(View.GONE);
                }
                holder.setText(R.id.tv_schedule_show_title,item.getTitle());
                holder.setText(R.id.tv_schedule_show_subtitle, item.getSubtitle());
                holder.setText(R.id.tv_time, "00:00");

            }
        };

        mScheduleDetailAdapter.setHasStableIds(true);
        rvScheduleDetailList.setLayoutManager(layoutManager);
        rvScheduleDetailList.setItemAnimator(new DefaultItemAnimator());
        if (rvScheduleDetailList.getItemAnimator() != null)
            rvScheduleDetailList.getItemAnimator().setAddDuration(250);
        rvScheduleDetailList.getItemAnimator().setMoveDuration(250);
        rvScheduleDetailList.getItemAnimator().setChangeDuration(250);
        rvScheduleDetailList.getItemAnimator().setRemoveDuration(250);
        rvScheduleDetailList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvScheduleDetailList.setItemViewCacheSize(30);
        rvScheduleDetailList.setAdapter(mScheduleDetailAdapter);
        rvScheduleDetailList.setNestedScrollingEnabled(false);
    }

    private void init1stBannerAds(){
        if(mAdView == null) {
            mAdView = new AdView(this);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId("ca-app-pub-1457023993566419/3344001375");
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mAdView.setLayoutParams(layoutParams);
            RelativeLayout rlAdsContainer = (RelativeLayout) findViewById(R.id.rl_ads_container);
            rlAdsContainer.addView(mAdView);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    Toast.makeText(StreamActivity.this, "ad loaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    //Toast.makeText(StreamActivity.this, "ad failed to load " + errorCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                    Toast.makeText(StreamActivity.this, "ad opened", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                    Toast.makeText(StreamActivity.this, "ad clicked", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                    Toast.makeText(StreamActivity.this, "ad left app", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                    Toast.makeText(StreamActivity.this, "ad closed", Toast.LENGTH_SHORT).show();
                }
            });
            mAdView.loadAd(adRequest);
        }
    }


    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(context);
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
        //MediaSource mediaSource = buildMediaSource(Uri.parse(STREAM_1));
        //player.prepare(mediaSource, true, false);


//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
//        Util.getUserAgent(context, "Streaming Aja"));

        MediaSource videoSource = new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory("Streaming Aja"))
                    .createMediaSource(Uri.parse(STREAM_1));
// Prepare the player with the source.
        player.prepare(videoSource, true, false);
    }

//    private MediaSource buildMediaSource(Uri uri){
//
//        String userAgent = "exoplayer-codelab";
//
//        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {
//            return ExtractorMediaSource.Factory( DefaultHttpDataSourceFactory(userAgent))
//                    .createMediaSource(uri)
//        } else if (uri.getLastPathSegment().contains("m3u8")) {
//            return HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
//                    .createMediaSource(uri)
//        } else {
//            val dashChunkSourceFactory = DefaultDashChunkSource.Factory(DefaultHttpDataSourceFactory(userAgent))
//
//            return DashMediaSource.Factory(dashChunkSourceFactory, DefaultHttpDataSourceFactory(userAgent))
//                    .createMediaSource(uri)
//        }
//    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
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

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    @Override
    public int getLayout() {
        return R.layout.activity_stream;
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void attachView() {
        streamPresenter.attachView(this);
    }

    @Override
    protected void detachPresenter() {
        streamPresenter.detachView();
    }

    @Override
    public void showProgress(boolean show) {

    }

    @Override
    public void showError(Throwable error) {

    }
}
