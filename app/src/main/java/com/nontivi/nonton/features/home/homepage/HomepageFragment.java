package com.nontivi.nonton.features.home.homepage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
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
import com.nontivi.nonton.injection.component.FragmentComponent;

import java.util.ArrayList;

import butterknife.BindView;

import static com.nontivi.nonton.app.StaticGroup.HOME_ADS1;
import static com.nontivi.nonton.app.StaticGroup.HOME_TRENDING;


public class HomepageFragment extends BaseFragment {

    @BindView(R.id.home_recyclerview)
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

        data.add(new HomeFeedResponse(HOME_TRENDING,trendingList));
        data.add(new HomeFeedResponse(HOME_ADS1));
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
                    case 1:
                        return R.layout.item_home_trending;
                    case 2:
                        return R.layout.item_banner_ads;
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
                        AdView mAdView = (AdView)holder.getView(R.id.adView);
                        AdRequest adRequest = new AdRequest.Builder().build();
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
                }
            }
        };
        mAdapter.setHasStableIds(true);
        mRvHome.setLayoutManager(layoutListManager);
        //if (this.getActivity() != null)
            //mRvHome.addItemDecoration(new BaseSpacesItemDecoration(MeasureUtil.dip2px(this.getActivity(), 16)));
        mRvHome.setItemAnimator(new DefaultItemAnimator());
        if (mRvHome.getItemAnimator() != null)
            mRvHome.getItemAnimator().setAddDuration(250);
        mRvHome.getItemAnimator().setMoveDuration(250);
        mRvHome.getItemAnimator().setChangeDuration(250);
        mRvHome.getItemAnimator().setRemoveDuration(250);
        mRvHome.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRvHome.setItemViewCacheSize(30);
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
                return R.layout.item_trending_list;
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