package com.nontivi.nonton.features.home.homepage;

import com.nontivi.nonton.data.response.ChannelListResponse;
import com.nontivi.nonton.data.response.GenreListResponse;
import com.nontivi.nonton.data.response.HttpResponse;
import com.nontivi.nonton.data.response.SettingListResponse;
import com.nontivi.nonton.features.base.MvpView;

public interface HomePageMvpView extends MvpView {

    void showChannelList(HttpResponse<ChannelListResponse> channels);

    void showGenreList(HttpResponse<GenreListResponse> genres);

    void showProgress(boolean show);

    void showError(Throwable error);
}
