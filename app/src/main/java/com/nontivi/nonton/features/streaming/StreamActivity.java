package com.nontivi.nonton.features.streaming;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;

import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.nontivi.nonton.R;
import com.nontivi.nonton.app.StaticGroup;
import com.nontivi.nonton.data.model.Appdata;
import com.nontivi.nonton.data.model.Channel;
import com.nontivi.nonton.data.model.ChannelContainer;
import com.nontivi.nonton.data.model.Schedule;
import com.nontivi.nonton.data.model.ScheduleDay;
import com.nontivi.nonton.data.response.HttpResponse;
import com.nontivi.nonton.data.response.ScheduleListResponse;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.features.base.BaseRecyclerAdapter;
import com.nontivi.nonton.features.base.BaseRecyclerViewHolder;
import com.nontivi.nonton.features.home.HomeActivity;
import com.nontivi.nonton.injection.component.ActivityComponent;
import com.nontivi.nonton.util.ClickUtil;
import com.nontivi.nonton.util.LocaleUtil;
import com.nontivi.nonton.util.NetworkUtil;
import com.nontivi.nonton.util.ViewUtil;
import com.nontivi.nonton.widget.dialog.CustomDialog;
import com.nontivi.nonton.widget.dialog.DialogAction;
import com.nontivi.nonton.widget.dialog.DialogOptionType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.Realm;

import static com.nontivi.nonton.app.ConstantGroup.KEY_CHANNEL;
import static com.nontivi.nonton.app.ConstantGroup.KEY_CHANNEL_ID;
import static com.nontivi.nonton.app.ConstantGroup.LOG_TAG;
import static com.nontivi.nonton.app.ConstantGroup.SUPPORT_EMAIL;
import static com.nontivi.nonton.data.model.ChannelContainer.ID_CHANNEL_FAVORITES;

public class StreamActivity extends BaseActivity implements StreamMvpView {

//    private final String STREAM_1 = "http://45.126.83.51/session/7b78becc-fbb2-11e9-b358-9e05a09d9a91/dr9445/h/h02/01.m3u8";

    @Inject
    StreamPresenter streamPresenter;

    @BindView(R.id.player_view)
    PlayerView playerView;

    @BindView(R.id.tv_streaming_title)
    TextView mTvTitle;

    @BindView(R.id.tv_viewer)
    TextView mTvViewer;

    @BindView(R.id.expand_text_view)
    ExpandableTextView mTvDesc;

    @BindView(R.id.rv_schedule_day)
    RecyclerView rvScheduleDayList;

    @BindView(R.id.rv_schedule_detail)
    RecyclerView rvScheduleDetailList;

    @BindView(R.id.ib_fullscreen)
    ImageButton btnFullscreen;

    @BindView(R.id.rl_data_warning)
    RelativeLayout rlDataWarning;

    @BindView(R.id.btn_fav)
    Button btnFav;

    @BindView(R.id.rl_empty_view)
    RelativeLayout mRlEmptyView;

    private SimpleExoPlayer player;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private Context context = this;
    private boolean playWhenReady = true;

    private BaseRecyclerAdapter<Schedule> mScheduleDetailAdapter;
    private BaseRecyclerAdapter<ScheduleDay> mSCheduleDayAdapter;

    private ArrayList<Schedule> scheduleList;
    private ArrayList<ScheduleDay> scheduleDayList;
    private ArrayList<String> days;

    private int lastSelectedItem = 0;
    private AdView mAdView;

    private Realm mRealm;
    private Channel mChannel;

    private boolean isSavedAsFav = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
        setIsChannelFavorite(false);

        mChannel = (Channel)getIntent().getSerializableExtra(KEY_CHANNEL);

        if(mChannel ==null){
            int channelId = getIntent().getIntExtra(KEY_CHANNEL_ID,-1);
            if(channelId != -1){
                mChannel = mRealm.where(Channel.class).equalTo("id",channelId).findFirst();
            }
        }

        if(mChannel!=null) {
            mTvTitle.setText(mChannel.getTitle());
            mTvViewer.setText(mChannel.getCurrentViewer() + " Viewer(s)");
            mTvDesc.setText(mChannel.getDescription());
            streamPresenter.getScheduleList(mChannel.getId());
            ViewUtil.setRoundImageUrl(this,R.id.iv_channel,mChannel.getImageUrl(),R.drawable.ic_home);
        }
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
                            if(NetworkUtil.isUsingMobileData(StreamActivity.this)) {
                                rlDataWarning.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                }

                ChannelContainer fav = realm.where(ChannelContainer.class).equalTo("id",ID_CHANNEL_FAVORITES).findFirst();
                if(fav!=null){
                    if(fav.getChannels()!=null && fav.getChannels().size() > 0){
                        for(Channel channel : fav.getChannels()){
                            if(channel!=null) {
                                if (mChannel.getId() == channel.getId()) {
                                    setIsChannelFavorite(true);
                                    break;
                                }
                            }
                        }
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
                        if(ClickUtil.isFastDoubleClick()) return;
                        Appdata appdata = realm.where(Appdata.class).equalTo("id", Appdata.ID_DATA_WARNING).findFirst();
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

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.isFastDoubleClick()) return;
                ChannelContainer fav = mRealm.where(ChannelContainer.class).equalTo("id",ID_CHANNEL_FAVORITES).findFirst();
                if(!isSavedAsFav){
                    if(fav!=null){
                        if(fav.getChannels()!=null){
                            boolean isAlreadySaved = false;
                            for(Channel channel : fav.getChannels()){
                                if(mChannel.getId() == channel.getId()){
                                    isAlreadySaved = true;
                                    break;
                                }
                            }
                            if(!isAlreadySaved){
                                mRealm.beginTransaction();
                                fav.addChannel(mChannel);
                                mRealm.insertOrUpdate(fav);
                                setIsChannelFavorite(true);
                                mRealm.commitTransaction();
                            }
                        }
                    }else{
                        mRealm.beginTransaction();
                        fav = mRealm.createObject(ChannelContainer.class);
                        fav.setId(ID_CHANNEL_FAVORITES);
                        fav.addChannel(mChannel);
                        setIsChannelFavorite(true);
                        mRealm.commitTransaction();
                    }
                }else{
                    if(fav!=null){
                        if(fav.getChannels()!=null && fav.getChannels().size() > 0){
                            for(int i = 0; i < fav.getChannels().size(); i++){
                                if(mChannel.getId() == fav.getChannels().get(i).getId()){
                                    mRealm.beginTransaction();
                                    fav.removeChannelByPosition(i);
                                    setIsChannelFavorite(false);
                                    mRealm.commitTransaction();
                                    break;
                                }
                            }
                        }
                    }
                }



            }
        });

        findViewById(R.id.ib_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.isFastDoubleClick()) return;
                String deepUrl;
                String storeUrl = "https://play.google.com/";
                deepUrl = "Hi! I've found a new way to watch TV show!\nClick the link below to download!\n"+storeUrl;
                StaticGroup.shareWithShareDialog(StreamActivity.this, deepUrl, "Streaming Aja");
            }
        });

        findViewById(R.id.ib_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.isFastDoubleClick()) return;
                View viewSetting = View.inflate(StreamActivity.this, R.layout.include_streaming_menu, null);
                RelativeLayout btnReport = viewSetting.findViewById(R.id.rl_report);
                btnReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ClickUtil.isFastDoubleClick()) return;
                        String subject = "[REPORT STREAMING AJA]";
                        String message = String.format("Hello Support!\nI have to report %s beacuse...",mChannel.getTitle());
                        StaticGroup.shareWithEmail(StreamActivity.this,SUPPORT_EMAIL,subject,message);
                    }
                });
                final CustomDialog customDialog = new CustomDialog.Builder(StreamActivity.this)
                        .optionType(DialogOptionType.NONE)
                        //.title(R.string.setting_change_lang)
                        .onPositive(new CustomDialog.MaterialDialogButtonCallback() {
                            @Override
                            public void onClick(@NonNull CustomDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .addCustomView(viewSetting)
                        .autoDismiss(false)
                        .canceledOnTouchOutside(true).build();

                customDialog.show();
            }
        });

        mTvDesc.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {

            }
        });
        init1stBannerAds();

        btnFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ClickUtil.isFastDoubleClick()) return;
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
        if(mChannel !=null) {
            initializePlayer(mChannel.getStreamingUrl());
        }
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
        if(mChannel !=null) {
            initializePlayer(mChannel.getStreamingUrl());
        }
    }

    @Override
    protected void onPause() {
        releasePlayer();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        if(mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
        super.onDestroy();
    }

    private void initScheduleDayList(){
        scheduleDayList = new ArrayList<>();

        int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        long currentTimeStamp = (System.currentTimeMillis() + offset)/1000;
        String currentDate = getDateFromTimestamp(currentTimeStamp);
        for(Schedule schedule: scheduleList){
            ScheduleDay day = getDayFromTimestamp(schedule.getTimestamp());
            day.setSubtitle(getDateFromTimestamp(schedule.getTimestamp()));
            if(scheduleDayList.size() > 0) {

                for (int i = 0; i < scheduleDayList.size(); i++) {
                    if(scheduleDayList.get(i).getSubtitle().equals(day.getSubtitle())){
                        scheduleDayList.get(i).addSchedule(schedule);
                        break;
                    }else{
                        if( i == scheduleDayList.size()-1){
                            day.addSchedule(schedule);
                            if(day.getSubtitle().equals(currentDate)){
                                lastSelectedItem = scheduleDayList.size();
                            }
                            scheduleDayList.add(day);
                            break;
                        }
                    }
                }
            }else{
                day.addSchedule(schedule);
                if(getDateFromTimestamp(schedule.getTimestamp()).equals(currentDate)){
                    lastSelectedItem = scheduleDayList.size();
                }
                scheduleDayList.add(day);
            }
        }

        if (scheduleDayList.size() > 0) {
            rvScheduleDayList.setVisibility(View.VISIBLE);
            rvScheduleDetailList.setVisibility(View.VISIBLE);
            mRlEmptyView.setVisibility(View.GONE);

            scheduleDayList.get(lastSelectedItem).setSelected(true);
            initScheduleDetailList(scheduleDayList.get(lastSelectedItem).getSchedules());

            GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
            layoutManager.setItemPrefetchEnabled(false);
            rvScheduleDayList.setLayoutManager(layoutManager);
            rvScheduleDayList.setItemAnimator(new DefaultItemAnimator());
            if (rvScheduleDayList.getItemAnimator() != null)
                rvScheduleDayList.getItemAnimator().setAddDuration(250);
            rvScheduleDayList.getItemAnimator().setMoveDuration(250);
            rvScheduleDayList.getItemAnimator().setChangeDuration(250);
            rvScheduleDayList.getItemAnimator().setRemoveDuration(250);
            rvScheduleDayList.setOverScrollMode(View.OVER_SCROLL_NEVER);
            rvScheduleDayList.setItemViewCacheSize(30);
            rvScheduleDayList.setNestedScrollingEnabled(false);
            mSCheduleDayAdapter = new BaseRecyclerAdapter<ScheduleDay>(this, scheduleDayList, layoutManager) {

                @Override
                public int getItemViewType(int position) {
                    return mData.get(position).getId();
                }

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_list_schedule_day;
                }

                @Override
                public void bindData(final BaseRecyclerViewHolder holder, int position, final ScheduleDay item) {
                    Button btnScheduleDay = holder.getButton(R.id.btn_schedule_day);

                    if (item.isSelected()) {
                        btnScheduleDay.setBackgroundResource(R.drawable.shape_primary_small_rounded_selected);
                        btnScheduleDay.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        btnScheduleDay.setBackgroundResource(R.drawable.shape_ripple_primary_small_rounded);
                        btnScheduleDay.setTextColor(getResources().getColor(R.color.primary));
                    }
                    btnScheduleDay.setText(item.getTitle());
                    btnScheduleDay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(ClickUtil.isFastDoubleClick()) return;
                            if (!item.isSelected()) {
                                mData.get(lastSelectedItem).setSelected(false);
                                //notifyItemChanged(lastSelectedItem);
                                lastSelectedItem = position;
                                mData.get(lastSelectedItem).setSelected(true);
                                //notifyItemChanged(lastSelectedItem);
                                notifyDataSetChanged();
                                initScheduleDetailList(item.getSchedules());
                            }
                        }
                    });

                }
            };
            mSCheduleDayAdapter.setHasStableIds(true);
            rvScheduleDayList.setAdapter(mSCheduleDayAdapter);
            rvScheduleDayList.scrollToPosition(lastSelectedItem);
        }else{
            findViewById(R.id.tv_schedule_note).setVisibility(View.GONE);
            ((TextView)mRlEmptyView.findViewById(R.id.tv_empty_view)).setText(getString(R.string.error_empty_schedule_main));
            rvScheduleDayList.setVisibility(View.GONE);
            rvScheduleDetailList.setVisibility(View.GONE);
            mRlEmptyView.setVisibility(View.VISIBLE);
        }
    }

    private void initScheduleDetailList(ArrayList<Schedule> schedules){

        if(mScheduleDetailAdapter==null) {
            GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            layoutManager.setItemPrefetchEnabled(false);
            rvScheduleDetailList.setLayoutManager(layoutManager);
            rvScheduleDetailList.setItemAnimator(new DefaultItemAnimator());
            if (rvScheduleDetailList.getItemAnimator() != null)
                rvScheduleDetailList.getItemAnimator().setAddDuration(250);
            rvScheduleDetailList.getItemAnimator().setMoveDuration(250);
            rvScheduleDetailList.getItemAnimator().setChangeDuration(250);
            rvScheduleDetailList.getItemAnimator().setRemoveDuration(250);
            rvScheduleDetailList.setOverScrollMode(View.OVER_SCROLL_NEVER);
            rvScheduleDetailList.setItemViewCacheSize(30);
            rvScheduleDetailList.setNestedScrollingEnabled(false);
            mScheduleDetailAdapter = new BaseRecyclerAdapter<Schedule>(this, schedules, layoutManager) {
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
                    if (position != 0) {
                        holder.getView(R.id.v_line_top).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.v_line_top).setVisibility(View.GONE);
                    }

                    if (position != mData.size() - 1) {
                        holder.getView(R.id.v_line_bottom).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.v_line_bottom).setVisibility(View.GONE);
                    }


                    holder.getTextView(R.id.tv_schedule_show_title).setTypeface(holder.getTextView(R.id.tv_schedule_show_title).getTypeface(), Typeface.NORMAL);
                    if(item.getTitle()!=null) {
                        String title = Html.fromHtml(item.getTitle()).toString();
                        holder.setText(R.id.tv_schedule_show_title, title);
                    }else{
                        holder.setText(R.id.tv_schedule_show_title, "Null");
                    }

                    String hour = getHourByTimestamp(item.getTimestamp());
                    int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
                    long currentTimeStamp = (System.currentTimeMillis() + offset)/1000;
                    //long currentTimeStamp = System.currentTimeMillis()/1000;
                    String currentDate = getDateFromTimestamp(currentTimeStamp);

                    if(scheduleDayList.get(lastSelectedItem).getSubtitle().equals(currentDate)){

                        if(currentTimeStamp >= item.getTimestamp()){
                            if(mData.size()-1 == position){
                                holder.getTextView(R.id.tv_schedule_show_title).setTypeface(holder.getTextView(R.id.tv_schedule_show_title).getTypeface(), Typeface.BOLD);
                            }else{
                                Log.e(LOG_TAG,"adapter position "+position);
                                if(mData.get(position+1).getTimestamp() > currentTimeStamp){
                                    Log.e(LOG_TAG,"adapter position bold "+position);
                                    holder.getTextView(R.id.tv_schedule_show_title).setTypeface(holder.getTextView(R.id.tv_schedule_show_title).getTypeface(), Typeface.BOLD);
                                }
                            }
                        }
                    }

                    if(item.getSubtitle()!=null) {
                        holder.setText(R.id.tv_schedule_show_subtitle, Html.fromHtml(item.getSubtitle()));
                    }else{
                        holder.setText(R.id.tv_schedule_show_subtitle, "null");
                    }
                    holder.setText(R.id.tv_time, hour);

                }
            };
            mScheduleDetailAdapter.setHasStableIds(true);
            rvScheduleDetailList.setAdapter(mScheduleDetailAdapter);

        }else{
            mScheduleDetailAdapter.setData(schedules);
            mScheduleDetailAdapter.notifyDataSetChanged();
        }
    }

    private void init1stBannerAds(){
        if(mAdView == null) {
            mAdView = new AdView(this);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId("ca-app-pub-8461471832857878/4383390929");
            //mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mAdView.setLayoutParams(layoutParams);
            RelativeLayout rlAdsContainer = (RelativeLayout) findViewById(R.id.rl_ads_container);
            rlAdsContainer.addView(mAdView);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
            mAdView.loadAd(adRequest);
        }
    }


    private void initializePlayer(String url) {
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
                    .createMediaSource(Uri.parse(url));
// Prepare the player with the source.
        player.prepare(videoSource, true, false);

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState){
                    case Player.STATE_READY:
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

                        findViewById(R.id.rl_player_controller).setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                switch (error.type) {
                    case ExoPlaybackException.TYPE_SOURCE:
                        Log.e(LOG_TAG, "TYPE_SOURCE: " + error.getSourceException().getMessage());
                        findViewById(R.id.tv_player_error).setVisibility(View.VISIBLE);
                        break;

                    case ExoPlaybackException.TYPE_RENDERER:
                        Log.e(LOG_TAG, "TYPE_RENDERER: " + error.getRendererException().getMessage());
                        findViewById(R.id.tv_player_error).setVisibility(View.VISIBLE);
                        break;

                    case ExoPlaybackException.TYPE_UNEXPECTED:
                        Log.e(LOG_TAG, "TYPE_UNEXPECTED: " + error.getUnexpectedException().getMessage());
                        findViewById(R.id.tv_player_error).setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
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
    public void showScheduleList(HttpResponse<ScheduleListResponse> schedules) {
        scheduleList = new ArrayList<>();
        if(schedules.getMeta().getData() != null){
            scheduleList.addAll(schedules.getMeta().getData().schedules);
            Collections.sort(scheduleList, new Comparator<Schedule>() {
                @Override
                public int compare(Schedule schedule, Schedule t1) {
                    int result = (int)(schedule.getTimestamp()-t1.getTimestamp());
                    return result;
                }
            });
        }

//        long currentTimestamp = System.currentTimeMillis()/1000;
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            scheduleList.removeIf(s -> s.getTimestamp() <= currentTimestamp);
//        }

        initScheduleDayList();
    }

    @Override
    public void showProgress(boolean show) {

    }

    @Override
    public void showError(Throwable error) {

    }

    private void setIsChannelFavorite(boolean isFav){
        isSavedAsFav = isFav;
        if(isFav){
            btnFav.setBackground(getResources().getDrawable(R.drawable.shape_gray_fill_small_rounded));
            btnFav.setText(R.string.channel_remove_fav);
        }else{
            btnFav.setBackground(getResources().getDrawable(R.drawable.shape_ripple_primary_fill_small_rounded));
            btnFav.setText(R.string.channel_save_as_fav);
        }
    }

    private String getDateFromTimestamp(long timestamp){
        long l = TimeUnit.SECONDS.toMillis(timestamp);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(l);

        //SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", getResources().getConfiguration().locale);
        SimpleDateFormat dayFormat = new SimpleDateFormat("d MMMM yyyy", getResources().getConfiguration().locale);
        dayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String day = dayFormat.format(c.getTime());

        return day;
    }

    private ScheduleDay getDayFromTimestamp(long timestamp){
        long l = TimeUnit.SECONDS.toMillis(timestamp);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(l);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", getResources().getConfiguration().locale);
        //SimpleDateFormat dayFormat = new SimpleDateFormat("d MMMM yyyy", getResources().getConfiguration().locale);
        dayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String day = dayFormat.format(c.getTime());

        ScheduleDay scheduleDay = new ScheduleDay();
        scheduleDay.setId(c.get(Calendar.DAY_OF_WEEK));
        scheduleDay.setTitle(day);

        return scheduleDay;
    }

    private String getHourByTimestamp(long timestamp){
        long l = TimeUnit.SECONDS.toMillis(timestamp);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(l);

        //SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm", getResources().getConfiguration().locale);
        SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm", getResources().getConfiguration().locale);
        dayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String hour = dayFormat.format(c.getTime());
        return hour;
    }
}
