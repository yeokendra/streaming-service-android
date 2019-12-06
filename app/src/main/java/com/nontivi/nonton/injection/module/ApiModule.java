package com.nontivi.nonton.injection.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import com.nontivi.nonton.data.remote.ApiService;

import retrofit2.Retrofit;

/**
 * Created by shivam on 29/5/17.
 */
@Module(includes = {NetworkModule.class})
public class ApiModule {

    @Provides
    @Singleton
    ApiService provideApi(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
