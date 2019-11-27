package com.nontivi.nonton.features.home.settingpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nontivi.nonton.BuildConfig;
import com.nontivi.nonton.R;
import com.nontivi.nonton.data.model.Setting;
import com.nontivi.nonton.features.base.BaseFragment;
import com.nontivi.nonton.features.base.BaseRecyclerAdapter;
import com.nontivi.nonton.features.base.BaseRecyclerViewHolder;
import com.nontivi.nonton.injection.component.FragmentComponent;

import java.util.ArrayList;

import butterknife.BindView;


public class SettingFragment extends BaseFragment {

    private final int ID_VERSION = 0;
    private final int ID_MOBILE_DATA_WARNING = 1;
    private final int ID_CHANGE_LANGUAGE = 2;

    @BindView(R.id.rv_setting)
    RecyclerView mRvSetting;

    @BindView(R.id.tv_box_title)
    TextView mTvTitle;


    @BindView(R.id.scrollview)
    ScrollView mScrollview;

    private GridLayoutManager layoutListManager;
    private BaseRecyclerAdapter<Setting> mAdapter;
    private ArrayList<Setting> data;


    public static SettingFragment newInstance() {
        return new SettingFragment();
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

        mTvTitle.setText(getString(R.string.title_setting));


        layoutListManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        loadData();
        initSettingList();
    }

    private void loadData(){

        data = new ArrayList<>();

        data.add(new Setting(ID_CHANGE_LANGUAGE,"Change Language"));
        data.add(new Setting(ID_MOBILE_DATA_WARNING,"Mobile Data Warning"));
        data.add(new Setting(ID_VERSION,"Version"));



    }

    private void initSettingList() {
        mAdapter = new BaseRecyclerAdapter<Setting>(getActivity(), data, layoutListManager) {
            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_list_setting;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final Setting item) {
                holder.setText(R.id.tv_title, item.getName());
                switch (item.getId()){
                    case ID_VERSION:
                        String title = "App Version " + BuildConfig.VERSION_CODE;
                        holder.setText(R.id.tv_title, title);
                        break;
                    case ID_MOBILE_DATA_WARNING:
                        Switch switchSetting = (Switch)holder.getView(R.id.switch_setting);
                        switchSetting.setVisibility(View.VISIBLE);
                        holder.setOnClickListener(R.id.rl_setting, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(switchSetting.isChecked()) {
                                    switchSetting.setChecked(false);
                                }else {
                                    switchSetting.setChecked(true);
                                }
                            }
                        });
                        break;
                    case ID_CHANGE_LANGUAGE:

                        break;
                }

            }
        };
        mAdapter.setHasStableIds(true);
        mRvSetting.setLayoutManager(layoutListManager);
        if (this.getActivity() != null)
            mRvSetting.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mRvSetting.setItemAnimator(new DefaultItemAnimator());
        mRvSetting.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRvSetting.setItemViewCacheSize(30);
        mRvSetting.setNestedScrollingEnabled(false);
        mRvSetting.setAdapter(mAdapter);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_setting;
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