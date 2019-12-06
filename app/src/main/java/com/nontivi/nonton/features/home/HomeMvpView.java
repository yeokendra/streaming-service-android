package com.nontivi.nonton.features.home;

import com.nontivi.nonton.data.response.HttpResponse;
import com.nontivi.nonton.data.response.SettingListResponse;
import com.nontivi.nonton.features.base.MvpView;

import java.util.List;

public interface HomeMvpView extends MvpView {

    void showSetting(HttpResponse<SettingListResponse> settings);

    void showProgress(boolean show);

    void showError(Throwable error);
}
