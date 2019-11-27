package com.nontivi.nonton.features.home.bookmarkpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.nontivi.nonton.features.search.SearchActivity;
import com.nontivi.nonton.features.streaming.StreamActivity;
import com.nontivi.nonton.injection.component.FragmentComponent;

import java.util.ArrayList;

import butterknife.BindView;

import static com.nontivi.nonton.app.ConstantGroup.KEY_FROM;
import static com.nontivi.nonton.app.ConstantGroup.KEY_SEARCH_STRING;
import static com.nontivi.nonton.app.StaticGroup.HOME_ADS1;
import static com.nontivi.nonton.app.StaticGroup.HOME_CHANNEL_LIST;
import static com.nontivi.nonton.app.StaticGroup.HOME_GENRE;
import static com.nontivi.nonton.app.StaticGroup.HOME_TRENDING;


public class BookmarkFragment extends BaseFragment {

    @BindView(R.id.rv_bookmark)
    RecyclerView mRvBookmark;

    @BindView(R.id.tv_box_title)
    TextView mTvTitle;

    @BindView(R.id.et_search)
    EditText mEtSearch;

    @BindView(R.id.scrollview)
    ScrollView mScrollview;

    private GridLayoutManager layoutListManager;
    private BaseRecyclerAdapter<Channel> mAdapter;
    private ArrayList<Channel> data;


    public static BookmarkFragment newInstance() {
        return new BookmarkFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        mScrollview.scrollTo(0,0);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvTitle.setText(getString(R.string.title_bookmark));

        getActivity().findViewById(R.id.rl_search_bar).requestFocus();

        layoutListManager = new GridLayoutManager(this.getActivity(), 2, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        loadData();
        initBookmarkList();

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

        data = new ArrayList<>();
        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/Cnn_logo_red_background.png/300px-Cnn_logo_red_background.png";

        data.add(new Channel(1,"CNN ASIA",imageUrl));
        data.add(new Channel(2,"CNN ASIA",imageUrl));
        data.add(new Channel(3,"CNN ASIA",imageUrl));
        data.add(new Channel(4,"CNN ASIA",imageUrl));
        data.add(new Channel(5,"CNN ASIA",imageUrl));
        data.add(new Channel(6,"CNN ASIA",imageUrl));
        data.add(new Channel(7,"CNN ASIA",imageUrl));
        data.add(new Channel(8,"CNN ASIA",imageUrl));

    }

    private void initBookmarkList() {
        mAdapter = new BaseRecyclerAdapter<Channel>(getActivity(), data, layoutListManager) {
            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_list_trending;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final Channel item) {
                holder.setText(R.id.tv_title,item.getTitle());
                holder.setText(R.id.tv_subtitle,"0 viewer");
                holder.setImageUrl(R.id.iv_preview, item.getImage_url(), R.drawable.ic_home);

                holder.setOnClickListener(R.id.rl_trending, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent myIntent1 = new Intent(getActivity(), StreamActivity.class);
                        getActivity().startActivity(myIntent1);
                    }
                });
            }
        };
        mAdapter.setHasStableIds(true);
        mRvBookmark.setLayoutManager(layoutListManager);
        mRvBookmark.setItemAnimator(new DefaultItemAnimator());
//        if (mRvBookmark.getItemAnimator() != null)
//            mRvBookmark.getItemAnimator().setAddDuration(250);
//        mRvBookmark.getItemAnimator().setMoveDuration(250);
//        mRvBookmark.getItemAnimator().setChangeDuration(250);
//        mRvBookmark.getItemAnimator().setRemoveDuration(250);
        mRvBookmark.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRvBookmark.setItemViewCacheSize(30);
        mRvBookmark.setNestedScrollingEnabled(false);
        mRvBookmark.setAdapter(mAdapter);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_bookmark;
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