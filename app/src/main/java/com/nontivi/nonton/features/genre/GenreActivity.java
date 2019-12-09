package com.nontivi.nonton.features.genre;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nontivi.nonton.R;
import com.nontivi.nonton.data.model.Channel;
import com.nontivi.nonton.data.model.ChannelContainer;
import com.nontivi.nonton.data.model.Genre;
import com.nontivi.nonton.features.base.BaseActivity;
import com.nontivi.nonton.features.base.BaseRecyclerAdapter;
import com.nontivi.nonton.features.base.BaseRecyclerViewHolder;
import com.nontivi.nonton.features.common.ErrorView;
import com.nontivi.nonton.features.streaming.StreamActivity;
import com.nontivi.nonton.injection.component.ActivityComponent;
import com.nontivi.nonton.util.RxBus;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.nontivi.nonton.app.ConstantGroup.KEY_CHANNEL_ID;
import static com.nontivi.nonton.app.ConstantGroup.KEY_GENRE;
import static com.nontivi.nonton.app.ConstantGroup.KEY_SEARCH_STRING;
import static com.nontivi.nonton.app.ConstantGroup.LOG_TAG;
import static com.nontivi.nonton.data.model.ChannelContainer.ID_CHANNEL_ALL;
import static com.nontivi.nonton.data.model.ChannelContainer.ID_CHANNEL_FAVORITES;

public class GenreActivity extends BaseActivity implements GenreMvpView, ErrorView.ErrorListener {

    @Inject
    GenrePresenter genrePresenter;

    @BindView(R.id.tv_box_title)
    TextView mTvTitle;

    @BindView(R.id.ib_back)
    ImageButton btnBack;

    @BindView(R.id.rv_genre_result)
    RecyclerView mRvGenreResult;

    private GridLayoutManager layoutListManager;
    private BaseRecyclerAdapter<Channel> mAdapter;
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getDefaultInstance();

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
        Genre genre = (Genre)bundle.getSerializable(KEY_GENRE);
        mTvTitle.setText(genre.getName());

        RealmResults<Channel> channels = mRealm.where(Channel.class).equalTo("channelContainer.id", ID_CHANNEL_ALL).findAll();
        ArrayList<Channel> channelList = new ArrayList<>();
        for(Channel channel : channels){
            for(int i : channel.getGenreId()){
                if(i == genre.getId()){
                    channelList.add(channel);
                    break;
                }
            }
        }
        initGenreChildList(channelList);
    }

    private void initGenreChildList(ArrayList<Channel> channels){
        if(mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<Channel>(this, channels, layoutListManager) {
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
                            Intent myIntent1 = new Intent(GenreActivity.this, StreamActivity.class);
                            myIntent1.putExtra(KEY_CHANNEL_ID, item.getId());
                            startActivity(myIntent1);
                        }
                    });
                }
            };
            //mAdapter.setHasStableIds(true);
            mRvGenreResult.setLayoutManager(layoutListManager);
            mRvGenreResult.setItemAnimator(new DefaultItemAnimator());
            mRvGenreResult.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mRvGenreResult.setItemViewCacheSize(30);
            mRvGenreResult.setNestedScrollingEnabled(false);
            mRvGenreResult.setAdapter(mAdapter);
        }else {
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public int getLayout() {
        return R.layout.activity_genre;
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void attachView() {
        genrePresenter.attachView(this);
    }

    @Override
    protected void detachPresenter() {
        genrePresenter.detachView();
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
