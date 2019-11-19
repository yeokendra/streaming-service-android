package com.nontivi.nonton.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import com.nontivi.nonton.data.DataManager;
import com.nontivi.nonton.injection.ApplicationContext;
import com.nontivi.nonton.injection.module.AppModule;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    @ApplicationContext
    Context context();

    Application application();

    DataManager apiManager();
}
