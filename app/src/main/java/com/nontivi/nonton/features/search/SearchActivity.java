package com.nontivi.nonton.features.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import com.nontivi.nonton.R;
import com.nontivi.nonton.app.StaticGroup;
import com.nontivi.nonton.data.model.Channel;
import com.nontivi.nonton.data.model.Genre;
import com.nontivi.nonton.data.response.HomeFeedResponse;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.features.base.BaseRecyclerAdapter;
import com.nontivi.nonton.features.base.BaseRecyclerViewHolder;
import com.nontivi.nonton.features.common.ErrorView;
import com.nontivi.nonton.features.detail.DetailActivity;
import com.nontivi.nonton.features.main.MainMvpView;
import com.nontivi.nonton.features.main.MainPresenter;
import com.nontivi.nonton.features.main.PokemonAdapter;
import com.nontivi.nonton.features.streaming.StreamActivity;
import com.nontivi.nonton.injection.component.ActivityComponent;
import com.nontivi.nonton.util.ClickUtil;
import com.nontivi.nonton.util.NetworkUtil;
import com.nontivi.nonton.util.RxBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

import static com.nontivi.nonton.app.ConstantGroup.KEY_CHANNEL_ID;
import static com.nontivi.nonton.app.ConstantGroup.KEY_SEARCH_STRING;
import static com.nontivi.nonton.data.model.ChannelContainer.ID_CHANNEL_ALL;

public class SearchActivity extends BaseActivity implements SearchMvpView, ErrorView.ErrorListener {

    @Inject
    SearchPresenter searchPresenter;

    @BindView(R.id.tv_box_title)
    TextView mTvTitle;

    @BindView(R.id.tv_search_title)
    TextView mTvSearchTitle;

    @BindView(R.id.ib_back)
    ImageButton btnBack;

    @BindView(R.id.rv_search_result)
    RecyclerView mRvSearch;

    @BindView(R.id.rl_empty_view)
    RelativeLayout mRlEmptyView;

    private GridLayoutManager layoutListManager;
    private BaseRecyclerAdapter<Channel> mAdapter;

    ArrayList<Channel> channelList;
    String mSearch;

    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTvTitle.setText(getString(R.string.title_search));
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layoutListManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        Bundle bundle = getIntent().getExtras();
        mSearch = bundle.getString(KEY_SEARCH_STRING,"");
        String searchTitle = "Search result for \""+mSearch+"\"";
        mTvSearchTitle.setText(searchTitle);

        channelList = new ArrayList<>();

        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Channel> channels = realm.where(Channel.class).equalTo("channelContainer.id", ID_CHANNEL_ALL).contains("title",mSearch, Case.INSENSITIVE).findAll();
                channelList.addAll(channels);
                loadData();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRealm!=null){
            mRealm.close();
        }
    }

    private void loadData(){
        if(mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<Channel>(this, channelList, layoutListManager) {
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
                    holder.setText(R.id.tv_title, item.getTitle());
                    holder.setText(R.id.tv_subtitle, item.getCurrentViewer() + " Viewer(s)");
                    holder.setImageUrl(R.id.iv_preview, item.getImageUrl(), R.drawable.ic_home);

                    holder.setOnClickListener(R.id.rl_trending, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(ClickUtil.isFastDoubleClick()) return;
                            RxBus.get().post(RxBus.KEY_CHANNEL_CLICKED, item.getId());
                            Intent myIntent1 = new Intent(SearchActivity.this, StreamActivity.class);
                            myIntent1.putExtra(KEY_CHANNEL_ID, item.getId());
                            startActivity(myIntent1);
                        }
                    });
                }
            };
            mAdapter.setHasStableIds(true);
            mRvSearch.setLayoutManager(layoutListManager);
            mRvSearch.setItemAnimator(new DefaultItemAnimator());
//        if (mRvBookmark.getItemAnimator() != null)
//            mRvBookmark.getItemAnimator().setAddDuration(250);
//        mRvBookmark.getItemAnimator().setMoveDuration(250);
//        mRvBookmark.getItemAnimator().setChangeDuration(250);
//        mRvBookmark.getItemAnimator().setRemoveDuration(250);
            mRvSearch.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRvSearch.setItemViewCacheSize(30);
            mRvSearch.setNestedScrollingEnabled(false);
            mRvSearch.setAdapter(mAdapter);
        }else {
            mAdapter.notifyDataSetChanged();
        }

        if(channelList.size() > 0){
            mRvSearch.setVisibility(View.VISIBLE);
            mRlEmptyView.setVisibility(View.GONE);
        }else{
            int textSize1 = getResources().getDimensionPixelSize(R.dimen.text_medium);
            int textSize2 = getResources().getDimensionPixelSize(R.dimen.text_small);

            String emptyMain = String.format(getString(R.string.error_empty_search_main),mSearch);
            String emptySubMain = getString(R.string.error_empty_search_submain);

            ((TextView)mRlEmptyView.findViewById(R.id.tv_empty_view)).setText(StaticGroup.combineString(emptyMain,emptySubMain,textSize1,textSize2));
            mRvSearch.setVisibility(View.GONE);
            mRlEmptyView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void attachView() {
        searchPresenter.attachView(this);
    }

    @Override
    protected void detachPresenter() {
        searchPresenter.detachView();
    }


    @Override
    public void showProgress(boolean show) {

    }

    @Override
    public void showError(Throwable error) {

    }

    @Override
    public void onReloadData() {

    }
}
