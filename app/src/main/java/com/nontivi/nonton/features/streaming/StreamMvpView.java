package com.nontivi.nonton.features.streaming;

import com.nontivi.nonton.data.response.HttpResponse;
import com.nontivi.nonton.data.response.ScheduleListResponse;
import com.nontivi.nonton.features.base.MvpView;

import java.util.List;

public interface StreamMvpView extends MvpView {

    void showScheduleList(HttpResponse<ScheduleListResponse> scheduleList);

    void showProgress(boolean show);

    void showError(Throwable error);
}
