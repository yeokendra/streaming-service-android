package com.nontivi.nonton.features.home.homepage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.nontivi.nonton.R;
import com.nontivi.nonton.data.model.Appdata;
import com.nontivi.nonton.data.model.Channel;
import com.nontivi.nonton.data.model.Genre;
import com.nontivi.nonton.data.response.ChannelListResponse;
import com.nontivi.nonton.data.response.GenreListResponse;
import com.nontivi.nonton.data.response.HomeFeedResponse;
import com.nontivi.nonton.data.response.HttpResponse;
import com.nontivi.nonton.features.base.BaseFragment;
import com.nontivi.nonton.features.base.BaseRecyclerAdapter;
import com.nontivi.nonton.features.base.BaseRecyclerViewHolder;
import com.nontivi.nonton.features.genre.GenreActivity;
import com.nontivi.nonton.features.search.SearchActivity;
import com.nontivi.nonton.features.streaming.StreamActivity;
import com.nontivi.nonton.injection.component.FragmentComponent;
import com.nontivi.nonton.util.NetworkUtil;
import com.nontivi.nonton.util.RxBus;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.nontivi.nonton.app.ConstantGroup.KEY_CHANNEL;
import static com.nontivi.nonton.app.ConstantGroup.KEY_FROM;
import static com.nontivi.nonton.app.ConstantGroup.KEY_GENRE;
import static com.nontivi.nonton.app.ConstantGroup.KEY_SEARCH_STRING;
import static com.nontivi.nonton.app.ConstantGroup.LOG_TAG;
import static com.nontivi.nonton.app.StaticGroup.HOME_ADS1;
import static com.nontivi.nonton.app.StaticGroup.HOME_CHANNEL_LIST;
import static com.nontivi.nonton.app.StaticGroup.HOME_GENRE;
import static com.nontivi.nonton.app.StaticGroup.HOME_TRENDING;


public class HomepageFragment extends BaseFragment implements HomePageMvpView{

    @BindView(R.id.rv_home)
    RecyclerView mRvHome;

    @BindView(R.id.et_search)
    EditText mEtSearch;

    @BindView(R.id.scrollview)
    ScrollView mScrollview;

    private GridLayoutManager layoutListManager;
    private BaseRecyclerAdapter<HomeFeedResponse> mAdapter;
    private ArrayList<HomeFeedResponse> data;

    private ArrayList<Channel> channelList;
    private ArrayList<Channel> trendingList;
    private ArrayList<Genre> genreList;

    @Inject
    HomePagePresenter homePagePresenter;

    private Realm mRealm;

    public static HomepageFragment newInstance() {
        return new HomepageFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        mScrollview.scrollTo(0,0);
        mEtSearch.getText().clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mRealm != null && !mRealm.isClosed()){
            mRealm.close();
        }

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.rl_search_bar).requestFocus();

        layoutListManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        trendingList = new ArrayList<>();
        channelList = new ArrayList<>();
        genreList = new ArrayList<>();

        mRealm = Realm.getDefaultInstance();
        if(!NetworkUtil.isNetworkConnected(getActivity())) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Channel> trendings = realm.where(Channel.class).equalTo("isTrending", true).findAll();
                    if (trendings != null && trendings.size() > 0) {
                        trendingList.addAll(trendings);
                    }
                    RealmResults<Channel> channels = realm.where(Channel.class).findAll();
                    if (channels != null && channels.size() > 0) {
                        channelList.addAll(channels);
                    }
                    RealmResults<Genre> genres = realm.where(Genre.class).findAll();
                    if (genres != null && genres.size() > 0) {
                        genreList.addAll(genres);
                    }
                }
            });
            loadData();
        }else {
            homePagePresenter.getChannels();
        }
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if(mEtSearch.getText() == null || mEtSearch.getText().toString().equals("")){
                            return false;
                        }else {
                            Intent myIntent1 = new Intent(getActivity(), SearchActivity.class);
                            myIntent1.putExtra(KEY_SEARCH_STRING,mEtSearch.getText().toString());
                            myIntent1.putExtra(KEY_FROM,0);
                            try {
                                getActivity().startActivity(myIntent1);
                            }catch (NullPointerException e){
                                return false;
                            }
                            return true;
                        }
                }
                return false;
            }
        });
    }

    private void loadData(){
        if(data == null) {
            data = new ArrayList<>();
            if(trendingList.size() > 0) {
                data.add(new HomeFeedResponse(HOME_TRENDING, trendingList));
            }
            data.add(new HomeFeedResponse(HOME_ADS1));
            if(genreList.size() > 0) {
                data.add(new HomeFeedResponse(HOME_GENRE, genreList));
            }
            if(channelList.size() > 0) {
                data.add(new HomeFeedResponse(HOME_CHANNEL_LIST, channelList));
            }
        }
        initHomeList();
    }

    private void initHomeList() {
        if(mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<HomeFeedResponse>(getActivity(), data, layoutListManager) {
                @Override
                public int getItemViewType(int position) {
                    if (data != null && data.size() > 0) {
                        return data.get(position).type;
                    }
                    return 0;
                }

                @Override
                public int getItemLayoutId(int viewType) {
                    switch (viewType) {
                        case HOME_TRENDING:
                            return R.layout.item_home_trending;
                        case HOME_ADS1:
                            return R.layout.item_banner_ads;
                        case HOME_GENRE:
                            return R.layout.item_home_genre;
                        case HOME_CHANNEL_LIST:
                            return R.layout.item_home_channel_all;
                        default:
                            return R.layout.item_home_trending;
                    }
                }

                @Override
                public void bindData(BaseRecyclerViewHolder holder, int position, final HomeFeedResponse item) {
                    switch (getItemViewType(position)) {
                        case HOME_TRENDING:
                            if (item.object instanceof ArrayList<?>) {
                                setTrendingSection(holder, (ArrayList<Channel>) item.object);
                            }

                            break;
                        case HOME_ADS1:
                            MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
                                @Override
                                public void onInitializationComplete(InitializationStatus initializationStatus) {
                                }
                            });
                            AdView mAdView = new AdView(getActivity());
                            AdRequest adRequest = new AdRequest.Builder().build();
                            mAdView.setAdSize(AdSize.BANNER);
                            mAdView.setAdUnitId("ca-app-pub-1457023993566419/3318015448");
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                            RelativeLayout rlAdsContainer = (RelativeLayout) holder.getView(R.id.rl_ads_container);
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
                            break;
                        case HOME_GENRE:
                            if (item.object instanceof ArrayList<?>) {
                                setGenreSection(holder, (ArrayList<Genre>) item.object);
                            }
                            break;
                        case HOME_CHANNEL_LIST:
                            if (item.object instanceof ArrayList<?>) {
                                setAllChannelSection(holder, (ArrayList<Channel>) item.object);
                            }
                            break;

                    }
                }
            };
            mAdapter.setHasStableIds(true);
            mRvHome.setLayoutManager(layoutListManager);
            //if (this.getActivity() != null)
            //mRvHome.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this.getActivity(), 16)));
            mRvHome.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
//        mRvHome.setItemAnimator(new DefaultItemAnimator());
//        if (mRvHome.getItemAnimator() != null)
//            mRvHome.getItemAnimator().setAddDuration(250);
//        mRvHome.getItemAnimator().setMoveDuration(250);
//        mRvHome.getItemAnimator().setChangeDuration(250);
//        mRvHome.getItemAnimator().setRemoveDuration(250);
            mRvHome.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRvHome.setItemViewCacheSize(30);
            mRvHome.setNestedScrollingEnabled(false);
            mRvHome.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setTrendingSection(BaseRecyclerViewHolder holder, ArrayList<Channel> channels){
        RecyclerView rvTrending = holder.getRecyclerView(R.id.rv_trending);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        layoutManager.setItemPrefetchEnabled(false);
        BaseRecyclerAdapter<Channel> trendingAdapter = new BaseRecyclerAdapter<Channel>(getActivity(), channels, layoutManager) {
            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_list_trending;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final Channel item) {
                holder.setText(R.id.tv_title,item.getTitle());
                holder.setText(R.id.tv_subtitle,item.getCurrentViewer()+ " Viewer(s)");
                holder.setImageUrl(R.id.iv_preview, item.getImageUrl(), R.drawable.ic_home);

                holder.setOnClickListener(R.id.rl_trending, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxBus.get().post(RxBus.KEY_CHANNEL_CLICKED, item.getId());
                        Intent myIntent1 = new Intent(getActivity(), StreamActivity.class);
                        myIntent1.putExtra(KEY_CHANNEL,item);
                        getActivity().startActivity(myIntent1);
                    }
                });

            }
        };

        trendingAdapter.setHasStableIds(true);
        rvTrending.setLayoutManager(layoutManager);
        rvTrending.setItemAnimator(new DefaultItemAnimator());
        if (rvTrending.getItemAnimator() != null)
            rvTrending.getItemAnimator().setAddDuration(250);
        rvTrending.getItemAnimator().setMoveDuration(250);
        rvTrending.getItemAnimator().setChangeDuration(250);
        rvTrending.getItemAnimator().setRemoveDuration(250);
        rvTrending.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvTrending.setItemViewCacheSize(30);
        rvTrending.setAdapter(trendingAdapter);
    }

    public void setGenreSection(BaseRecyclerViewHolder holder, ArrayList<Genre> genres){
        RecyclerView rvGenre = holder.getRecyclerView(R.id.rv_genre);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        layoutManager.setItemPrefetchEnabled(false);
        BaseRecyclerAdapter<Genre> genreAdapter = new BaseRecyclerAdapter<Genre>(getActivity(), genres, layoutManager) {
            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_list_genre;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final Genre item) {
                holder.setText(R.id.tv_title,item.getName());
                holder.setOnClickListener(R.id.rl_genre, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent1 = new Intent(getActivity(), GenreActivity.class);
                        myIntent1.putExtra(KEY_GENRE,item);
                        getActivity().startActivity(myIntent1);
                    }
                });

            }
        };

        genreAdapter.setHasStableIds(true);
        rvGenre.setLayoutManager(layoutManager);
        rvGenre.setItemAnimator(new DefaultItemAnimator());
        if (rvGenre.getItemAnimator() != null)
            rvGenre.getItemAnimator().setAddDuration(250);
        rvGenre.getItemAnimator().setMoveDuration(250);
        rvGenre.getItemAnimator().setChangeDuration(250);
        rvGenre.getItemAnimator().setRemoveDuration(250);
        rvGenre.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvGenre.setItemViewCacheSize(30);
        rvGenre.setAdapter(genreAdapter);
    }

    public void setAllChannelSection(BaseRecyclerViewHolder holder, ArrayList<Channel> channels){
        RecyclerView rvChannelList = holder.getRecyclerView(R.id.rv_channel_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 2, GridLayoutManager.VERTICAL, false);
        layoutManager.setItemPrefetchEnabled(false);
        BaseRecyclerAdapter<Channel> channelListAdapter = new BaseRecyclerAdapter<Channel>(getActivity(), channels, layoutManager) {
            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_list_trending;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final Channel item) {
                holder.setText(R.id.tv_title,item.getTitle());
                holder.setText(R.id.tv_subtitle,item.getCurrentViewer()+ " Viewer(s)");
                holder.setImageUrl(R.id.iv_preview, item.getImageUrl(), R.drawable.ic_home);

                holder.setOnClickListener(R.id.rl_trending, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxBus.get().post(RxBus.KEY_CHANNEL_CLICKED, item.getId());
                        Intent myIntent1 = new Intent(getActivity(), StreamActivity.class);
                        myIntent1.putExtra(KEY_CHANNEL,item);
                        getActivity().startActivity(myIntent1);
                    }
                });

            }
        };

        channelListAdapter.setHasStableIds(true);
        rvChannelList.setLayoutManager(layoutManager);
        rvChannelList.setItemAnimator(new DefaultItemAnimator());
        if (rvChannelList.getItemAnimator() != null)
            rvChannelList.getItemAnimator().setAddDuration(250);
        rvChannelList.getItemAnimator().setMoveDuration(250);
        rvChannelList.getItemAnimator().setChangeDuration(250);
        rvChannelList.getItemAnimator().setRemoveDuration(250);
        rvChannelList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvChannelList.setItemViewCacheSize(30);
        rvChannelList.setAdapter(channelListAdapter);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_homepage;
    }

    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    protected void attachView() {
        homePagePresenter.attachView(this);
    }

    @Override
    protected void detachPresenter() {
        homePagePresenter.detachView();
    }


    @Override
    public void showChannelList(HttpResponse<ChannelListResponse> channels) {
        channelList = new ArrayList<>();

        if(channels.getMeta().getData() != null){
            channelList.addAll(channels.getMeta().getData().channels);
            Log.v(LOG_TAG,"channel size = "+channelList.size());

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Channel> channels = realm.where(Channel.class).findAll();
                    channels.deleteAllFromRealm();

                    realm.insert(channelList);
                }
            });

            trendingList = new ArrayList<>();

            for(Channel channel : channelList){
                if(channel.isTrending()){
                    trendingList.add(channel);
                }
            }
        }

        homePagePresenter.getGenres();
    }

    @Override
    public void showGenreList(HttpResponse<GenreListResponse> genres) {
        genreList = new ArrayList<>();
        if(genres.getMeta().getData() != null){
            genreList.addAll(genres.getMeta().getData().genres);
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Genre> genres = realm.where(Genre.class).findAll();
                    genres.deleteAllFromRealm();

                    realm.insert(genreList);
                }
            });
        }

        loadData();
    }

    @Override
    public void showProgress(boolean show) {

    }

    @Override
    public void showError(Throwable error) {
        Toast.makeText(getActivity(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
    }
}