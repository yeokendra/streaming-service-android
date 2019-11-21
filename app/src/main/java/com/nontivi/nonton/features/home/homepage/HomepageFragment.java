package com.nontivi.nonton.features.home.homepage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.nontivi.nonton.R;
import com.nontivi.nonton.data.model.Channel;
import com.nontivi.nonton.data.response.HomeFeedResponse;
import com.nontivi.nonton.features.base.BaseFragment;
import com.nontivi.nonton.features.base.BaseRecyclerAdapter;
import com.nontivi.nonton.features.base.BaseRecyclerViewHolder;
import com.nontivi.nonton.features.streaming.StreamActivity;
import com.nontivi.nonton.injection.component.FragmentComponent;
import com.nontivi.nonton.util.RxBus;

import java.util.ArrayList;

import butterknife.BindView;

import static com.nontivi.nonton.app.StaticGroup.HOME_ADS1;
import static com.nontivi.nonton.app.StaticGroup.HOME_CHANNEL_LIST;
import static com.nontivi.nonton.app.StaticGroup.HOME_GENRE;
import static com.nontivi.nonton.app.StaticGroup.HOME_TRENDING;


public class HomepageFragment extends BaseFragment {

    @BindView(R.id.rv_home)
    RecyclerView mRvHome;

    private GridLayoutManager layoutListManager;
    private BaseRecyclerAdapter<HomeFeedResponse> mAdapter;
    private ArrayList<HomeFeedResponse> data;


    public static HomepageFragment newInstance() {
        return new HomepageFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
//        RxBus.get().post(RxBus.KEY_MY_BOX_GUIDANCE, view);

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

        loadData();
        initHomeList();
    }

    private void loadData(){
        data = new ArrayList<>();

        ArrayList<Channel> trendingList = new ArrayList<>();
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/Cnn_logo_red_background.png/300px-Cnn_logo_red_background.png";

        trendingList.add(new Channel(1,"CNN ASIA",imageUrl));
        trendingList.add(new Channel(2,"CNN ASIA",imageUrl));
        trendingList.add(new Channel(3,"CNN ASIA",imageUrl));
        trendingList.add(new Channel(4,"CNN ASIA",imageUrl));
        trendingList.add(new Channel(5,"CNN ASIA",imageUrl));
        trendingList.add(new Channel(6,"CNN ASIA",imageUrl));
        trendingList.add(new Channel(7,"CNN ASIA",imageUrl));
        trendingList.add(new Channel(8,"CNN ASIA",imageUrl));

        data.add(new HomeFeedResponse(HOME_TRENDING,trendingList));
        data.add(new HomeFeedResponse(HOME_ADS1));

        ArrayList<String> genreList = new ArrayList<>();
        genreList.add("Comedy");
        genreList.add("Drama");
        genreList.add("Horror");
        genreList.add("Documentary");
        genreList.add("Sci-Fi");
        genreList.add("Historical");
        genreList.add("Reality Show");

        data.add(new HomeFeedResponse(HOME_GENRE,genreList));
        data.add(new HomeFeedResponse(HOME_CHANNEL_LIST,trendingList));


    }

    private void initHomeList() {
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
                        if(item.object instanceof ArrayList<?>){
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
                                Toast.makeText(mContext, "ad loaded", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                // Code to be executed when an ad request fails.
                                Toast.makeText(mContext, "ad failed to load "+errorCode, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdOpened() {
                                // Code to be executed when an ad opens an overlay that
                                // covers the screen.
                                Toast.makeText(mContext, "ad opened", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                                Toast.makeText(mContext, "ad clicked", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdLeftApplication() {
                                // Code to be executed when the user has left the app.
                                Toast.makeText(mContext, "ad left app", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdClosed() {
                                // Code to be executed when the user is about to return
                                // to the app after tapping on an ad.
                                Toast.makeText(mContext, "ad closed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mAdView.loadAd(adRequest);
                        break;
                    case HOME_GENRE:
                        if(item.object instanceof ArrayList<?>) {
                            setGenreSection(holder, (ArrayList<String>) item.object);
                        }
                        break;
                    case HOME_CHANNEL_LIST:
                        if(item.object instanceof ArrayList<?>) {
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
                holder.setText(R.id.tv_subtitle,"0 viewer");
                holder.setImageUrl(R.id.iv_preview, item.getImage_url(), R.drawable.ic_home);

                holder.setOnClickListener(R.id.rl_trending, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RxBus.get().post(RxBus.KEY_CHANNEL_CLICKED, item.getId());
                        Intent myIntent1 = new Intent(getActivity(), StreamActivity.class);
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

    public void setGenreSection(BaseRecyclerViewHolder holder, ArrayList<String> channels){
        RecyclerView rvGenre = holder.getRecyclerView(R.id.rv_genre);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        layoutManager.setItemPrefetchEnabled(false);
        BaseRecyclerAdapter<String> genreAdapter = new BaseRecyclerAdapter<String>(getActivity(), channels, layoutManager) {
            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_list_genre;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final String item) {
                holder.setText(R.id.tv_title,item);

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
                holder.setText(R.id.tv_subtitle,"0 viewer");
                holder.setImageUrl(R.id.iv_preview, item.getImage_url(), R.drawable.ic_home);

                holder.setOnClickListener(R.id.rl_trending, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "position = "+position, Toast.LENGTH_SHORT).show();
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

    }

    @Override
    protected void attachView() {

    }

    @Override
    protected void detachPresenter() {

    }


}