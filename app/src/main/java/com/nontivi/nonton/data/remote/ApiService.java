package com.nontivi.nonton.data.remote;

import com.nontivi.nonton.data.model.Pokemon;
import com.nontivi.nonton.data.response.ChannelListResponse;
import com.nontivi.nonton.data.response.GenreListResponse;
import com.nontivi.nonton.data.response.HttpResponse;
import com.nontivi.nonton.data.response.PokemonListResponse;
import com.nontivi.nonton.data.response.ScheduleListResponse;
import com.nontivi.nonton.data.response.SettingListResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("setting/get-setting")
    Single<HttpResponse<SettingListResponse>> getSettingList();

    @POST("channel/get-channel")
    Single<HttpResponse<ChannelListResponse>> getChannelList();

    @POST("genre/get-genre")
    Single<HttpResponse<GenreListResponse>> getGenreList();

    @POST("schedule/get-schedule")
    Single<HttpResponse<ScheduleListResponse>> getScheduleList();
}
