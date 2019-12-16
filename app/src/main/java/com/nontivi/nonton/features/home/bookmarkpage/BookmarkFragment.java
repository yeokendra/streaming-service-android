package com.nontivi.nonton.features.home.bookmarkpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nontivi.nonton.R;
import com.nontivi.nonton.app.StaticGroup;
import com.nontivi.nonton.data.model.Channel;
import com.nontivi.nonton.data.model.ChannelContainer;
import com.nontivi.nonton.features.base.BaseFragment;
import com.nontivi.nonton.features.base.BaseRecyclerAdapter;
import com.nontivi.nonton.features.base.BaseRecyclerViewHolder;
import com.nontivi.nonton.features.search.SearchActivity;
import com.nontivi.nonton.features.streaming.StreamActivity;
import com.nontivi.nonton.injection.component.FragmentComponent;
import com.nontivi.nonton.util.RxBus;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmList;

import static com.nontivi.nonton.app.ConstantGroup.KEY_CHANNEL_ID;
import static com.nontivi.nonton.app.ConstantGroup.KEY_FROM;
import static com.nontivi.nonton.app.ConstantGroup.KEY_SEARCH_STRING;
import static com.nontivi.nonton.app.ConstantGroup.LOG_TAG;
import static com.nontivi.nonton.data.model.ChannelContainer.ID_CHANNEL_FAVORITES;


public class BookmarkFragment extends BaseFragment {

    @BindView(R.id.rv_bookmark)
    RecyclerView mRvBookmark;

    @BindView(R.id.tv_box_title)
    TextView mTvTitle;

    @BindView(R.id.rl_search_bar)
    RelativeLayout mRlSearchBar;

    @BindView(R.id.et_search)
    EditText mEtSearch;

    @BindView(R.id.scrollview)
    ScrollView mScrollview;

    @BindView(R.id.rl_empty_view)
    RelativeLayout mRlEmptyView;

    private GridLayoutManager layoutListManager;
    private BaseRecyclerAdapter<Channel> mAdapter;

    private Realm mRealm;


    public static BookmarkFragment newInstance() {
        return new BookmarkFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        //mScrollview.scrollTo(0,0);
        mEtSearch.getText().clear();
        ChannelContainer favorites = mRealm.where(ChannelContainer.class).equalTo("id",ID_CHANNEL_FAVORITES).findFirst();
        if (favorites == null){
            mRealm.beginTransaction();
            Log.e(LOG_TAG,"create favorite");
            favorites = mRealm.createObject(ChannelContainer.class);
            favorites.setId(ID_CHANNEL_FAVORITES);
            favorites.setChannels(new RealmList<>());
            mRealm.commitTransaction();
        }
        initBookmarkList(favorites.getChannels());
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mEtSearch.hasFocus()){
            mEtSearch.clearFocus();
            getActivity().findViewById(R.id.rl_search_bar).requestFocus();
        }
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

        mTvTitle.setText(getString(R.string.title_bookmark));

        getActivity().findViewById(R.id.rl_search_bar).requestFocus();

        layoutListManager = new GridLayoutManager(this.getActivity(), 2, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        mRealm = Realm.getDefaultInstance();

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

    private void initBookmarkList(RealmList<Channel> channels) {
        if(mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<Channel>(getActivity(), channels, layoutListManager) {
                @Override
                public int getItemViewType(int position) {
                    return mData.get(position).getId();
                }

                @Override
                public int getItemLayoutId(int viewType) {
                    return R.layout.item_list_trending;
                }

                @Override
                public void bindData(BaseRecyclerViewHolder holder, int position, final Channel item) {
                    holder.setText(R.id.tv_title, item.getTitle());
                    holder.setText(R.id.tv_subtitle, item.getCurrentViewer() + " Viewer(s)");
                    holder.setImageUrl(R.id.iv_preview, item.getImageUrl(), R.drawable.ic_home);

                    holder.setOnClickListener(R.id.rl_trending, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RxBus.get().post(RxBus.KEY_CHANNEL_CLICKED, item.getId());
                            Intent myIntent1 = new Intent(getActivity(), StreamActivity.class);
                            myIntent1.putExtra(KEY_CHANNEL_ID, item.getId());
                            getActivity().startActivity(myIntent1);
                        }
                    });
                }
            };
            //mAdapter.setHasStableIds(true);
            mRvBookmark.setLayoutManager(layoutListManager);
            mRvBookmark.setItemAnimator(new DefaultItemAnimator());
            mRvBookmark.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRvBookmark.setItemViewCacheSize(30);
            mRvBookmark.setNestedScrollingEnabled(false);
            mRvBookmark.setAdapter(mAdapter);
        }else {
            mAdapter.notifyDataSetChanged();
        }

        if(channels.size() > 0){
            mRlSearchBar.setVisibility(View.VISIBLE);
            mRvBookmark.setVisibility(View.VISIBLE);
            mRlEmptyView.setVisibility(View.GONE);
        }else{
            mRlSearchBar.setVisibility(View.GONE);
            int textSize1 = getResources().getDimensionPixelSize(R.dimen.text_medium);
            int textSize2 = getResources().getDimensionPixelSize(R.dimen.text_small);

            String emptyMain = getString(R.string.error_empty_subscibe_main);
            String emptySubMain = getString(R.string.error_empty_subscibe_submain);

            ((TextView)mRlEmptyView.findViewById(R.id.tv_empty_view)).setText(StaticGroup.combineString(emptyMain,emptySubMain,textSize1,textSize2));
            mRvBookmark.setVisibility(View.GONE);
            mRlEmptyView.setVisibility(View.VISIBLE);
        }

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