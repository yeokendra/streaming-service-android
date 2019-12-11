package com.nontivi.nonton.features.home.settingpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import com.nontivi.nonton.data.model.Appdata;
import com.nontivi.nonton.data.model.Option;
import com.nontivi.nonton.data.model.Setting;
import com.nontivi.nonton.features.base.BaseFragment;
import com.nontivi.nonton.features.base.BaseRecyclerAdapter;
import com.nontivi.nonton.features.base.BaseRecyclerViewHolder;
import com.nontivi.nonton.features.setting.ChangeLangActivity;
import com.nontivi.nonton.features.setting.FaqActivity;
import com.nontivi.nonton.injection.component.FragmentComponent;
import com.nontivi.nonton.util.ClickUtil;
import com.nontivi.nonton.util.LocaleUtil;
import com.nontivi.nonton.util.ViewUtil;
import com.nontivi.nonton.widget.dialog.CustomDialog;
import com.nontivi.nonton.widget.dialog.DialogAction;
import com.nontivi.nonton.widget.dialog.DialogOptionType;

import java.util.ArrayList;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.nontivi.nonton.app.ConstantGroup.LOG_TAG;


public class SettingFragment extends BaseFragment {

    private final int ID_VERSION = 0;
    private final int ID_MOBILE_DATA_WARNING = 1;
    private final int ID_CHANGE_LANGUAGE = 2;
    private final int ID_WATCH_HISTORY = 3;
    private final int ID_RATE_US = 4;
    private final int ID_FAQ = 5;
    private final int ID_FEEDBACK = 6;

    @BindView(R.id.rv_setting)
    RecyclerView mRvSetting;

    @BindView(R.id.tv_box_title)
    TextView mTvTitle;


    @BindView(R.id.scrollview)
    ScrollView mScrollview;

    private GridLayoutManager layoutListManager;
    private BaseRecyclerAdapter<Option> mAdapter;
    private ArrayList<Option> data;

    private Realm mRealm;
    private String mVersion = "Version x.x.x.x";


    public static SettingFragment newInstance() {
        return new SettingFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        mScrollview.scrollTo(0,0);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRealm = Realm.getDefaultInstance();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvTitle.setText(getString(R.string.title_setting));

        layoutListManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.VERTICAL, false);
        layoutListManager.setItemPrefetchEnabled(false);

        mVersion = getString(R.string.setting_version) + " " + BuildConfig.VERSION_NAME;
        ((TextView)view.findViewById(R.id.tv_version)).setText(mVersion);

        loadData();
        initSettingList();
    }

    private void loadData(){

        data = new ArrayList<>();

        data.add(new Option(ID_CHANGE_LANGUAGE,getString(R.string.setting_change_lang),R.drawable.ic_language));
        data.add(new Option(ID_MOBILE_DATA_WARNING,getString(R.string.setting_mobile_data_warning),R.drawable.ic_history));
        //data.add(new Option(ID_VERSION,getString(R.string.setting_version),R.drawable.ic_language));
        data.add(new Option(ID_RATE_US,getString(R.string.setting_rate_us),R.drawable.ic_rate));
        data.add(new Option(ID_FAQ,getString(R.string.setting_faq),R.drawable.ic_faq));
        data.add(new Option(ID_FEEDBACK,getString(R.string.setting_feedback),R.drawable.ic_feedback));
    }

    private void initSettingList() {
        mAdapter = new BaseRecyclerAdapter<Option>(getActivity(), data, layoutListManager) {
            @Override
            public int getItemViewType(int position) {
                return mData.get(position).getId();
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_list_setting;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final Option item) {
                holder.setText(R.id.tv_title, item.getName());
                holder.setImageResource(R.id.iv_icon, item.getDrawableId());
                switch (item.getId()){
                    case ID_VERSION:

                        holder.setText(R.id.tv_title, mVersion);
                        break;
                    case ID_MOBILE_DATA_WARNING:
                        Switch switchSetting = (Switch)holder.getView(R.id.switch_setting);
                        switchSetting.setVisibility(View.VISIBLE);
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Appdata appdata = realm.where(Appdata.class).equalTo("id", Appdata.ID_DATA_WARNING).findFirst();
                                if (appdata != null) {
                                    Log.e(LOG_TAG,"not null");
                                    if (appdata.getValue() == 0) {
                                        switchSetting.setChecked(false);
                                    } else {
                                        switchSetting.setChecked(true);
                                    }
                                }else{
                                    Log.e(LOG_TAG,"null");
                                    appdata = mRealm.createObject(Appdata.class);
                                    appdata.setId(Appdata.ID_DATA_WARNING);
                                    appdata.setValue(0);
                                }
                            }
                        });

                        holder.setOnClickListener(R.id.rl_setting, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onMobileDataWarningChanged(switchSetting);
                            }
                        });

                        switchSetting.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                    onMobileDataWarningChanged(switchSetting);
                                }
                                return true;
                            }
                        });

                        break;
                    case ID_CHANGE_LANGUAGE:
                        holder.getTextView(R.id.tv_value).setVisibility(View.VISIBLE);
                        String lang = LocaleUtil.getLanguage(getContext());
                        switch (lang){
                            case "id":
                                holder.setText(R.id.tv_value,"Bahasa");
                                break;
                            case "en":
                                holder.setText(R.id.tv_value,"English");
                                break;
                        }

                        holder.setOnClickListener(R.id.rl_setting, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                View viewLang = View.inflate(getActivity(), R.layout.include_choose_language, null);
//                                final CustomDialog customDialog = new CustomDialog.Builder(getActivity())
//                                        .optionType(DialogOptionType.NONE)
//                                        .addCustomView(viewLang)
//                                        .autoDismiss(false)
//                                        .canceledOnTouchOutside(true).build();
//
//                                initLanguageDialog(customDialog, viewLang);
//                                customDialog.show();
                                Intent intent = new Intent(getActivity(), ChangeLangActivity.class);
                                getActivity().startActivity(intent);
                            }
                        });
                        break;

                    case ID_FAQ:
                        holder.setOnClickListener(R.id.rl_setting, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), FaqActivity.class);
                                getActivity().startActivity(intent);
                            }
                        });
                        break;
                }

            }
        };
        mAdapter.setHasStableIds(true);
        mRvSetting.setLayoutManager(layoutListManager);
//        if (this.getActivity() != null)
//            mRvSetting.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mRvSetting.setItemAnimator(new DefaultItemAnimator());
        mRvSetting.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRvSetting.setNestedScrollingEnabled(false);
        mRvSetting.setAdapter(mAdapter);
    }

    private void onMobileDataWarningChanged(Switch switchSetting){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Appdata appdata = realm.where(Appdata.class).equalTo("id", Appdata.ID_DATA_WARNING).findFirst();
                if (switchSetting.isChecked()) {
                    appdata.setValue(0);
                    switchSetting.setChecked(false);
                } else {
                    appdata.setValue(1);
                    switchSetting.setChecked(true);
                }
            }
        });

    }

    private void initLanguageDialog(final CustomDialog customDialog, View view) {
        String lang = LocaleUtil.getLanguage(getContext());

        switch (lang) {
            case "id":
                view.findViewById(R.id.ivSelected_id).setVisibility(View.VISIBLE);
                view.findViewById(R.id.ivSelected_en).setVisibility(View.GONE);
                break;
            case "en":
            default:
                view.findViewById(R.id.ivSelected_en).setVisibility(View.VISIBLE);
                view.findViewById(R.id.ivSelected_id).setVisibility(View.GONE);
                break;
        }

        view.findViewById(R.id.rl_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                LocaleUtil.setLocale(getActivity(), "id");
                customDialog.dismiss();
                getActivity().recreate();
            }
        });
        view.findViewById(R.id.rl_en).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) return;
                LocaleUtil.setLocale(getActivity(), "en");
                customDialog.dismiss();
                getActivity().recreate();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mRealm != null && !mRealm.isClosed()){
            mRealm.close();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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