package com.nontivi.nonton.data;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.nontivi.nonton.data.model.Pokemon;
import com.nontivi.nonton.data.model.Setting;
import com.nontivi.nonton.data.remote.PokemonService;
import com.nontivi.nonton.data.remote.ApiService;
import com.nontivi.nonton.data.response.ChannelListResponse;
import com.nontivi.nonton.data.response.GenreListResponse;
import com.nontivi.nonton.data.response.HttpResponse;
import com.nontivi.nonton.data.response.ScheduleListResponse;
import com.nontivi.nonton.data.response.SettingListResponse;

import org.json.JSONObject;

import io.reactivex.Single;

/**
 * Created by shivam on 29/5/17.
 */
@Singleton
public class DataManager {

//    private PokemonService pokemonService;
    private ApiService apiService;

    @Inject
    public DataManager(ApiService apiService) {
        this.apiService = apiService;
    }

//    public Single<List<String>> getPokemonList(int limit) {
//        return pokemonService
//                .getPokemonList(limit)
//                .toObservable()
//                .flatMapIterable(namedResources -> namedResources.results)
//                .map(namedResource -> namedResource.name)
//                .toList();
//    }
//
//    public Single<Pokemon> getPokemon(String name) {
//        return pokemonService.getPokemon(name);
//    }

    public Single<HttpResponse<SettingListResponse>> getSettingList(){
        return apiService.getSettingList();
    }

    public Single<HttpResponse<ChannelListResponse>> getChannelList(){
        return apiService.getChannelList();
    }

    public Single<HttpResponse<GenreListResponse>> getGenreList(){
        return apiService.getGenreList();
    }

    public Single<HttpResponse<ScheduleListResponse>> getScheduleList(int id){
        return apiService.getScheduleList(id);
    }
}
