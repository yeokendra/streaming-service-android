package com.nontivi.nonton;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.nontivi.nonton.app.ConstantGroup;
import com.nontivi.nonton.app.StaticGroup;
import com.singhajit.sherlock.core.Sherlock;
import com.squareup.leakcanary.LeakCanary;
import com.tspoon.traceur.Traceur;

import com.nontivi.nonton.injection.component.AppComponent;
import com.nontivi.nonton.injection.component.DaggerAppComponent;
import com.nontivi.nonton.injection.module.AppModule;
import com.nontivi.nonton.injection.module.NetworkModule;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import timber.log.Timber;

public class MvpStarterApplication extends Application {

    private AppComponent appComponent;

    private static Context mApplicationContext;

    public static MvpStarterApplication getApplication() {
        return (MvpStarterApplication) mApplicationContext;
    }

    public static Context getContext() {
        return mApplicationContext;
    }

    public static MvpStarterApplication get(Context context) {
        return (MvpStarterApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v(ConstantGroup.LOG_TAG,"=========== BaseApplication onCreate ===========");
        mApplicationContext = this;

        StaticGroup.initialize(this);
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("streamingaja.db")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Stetho.initializeWithDefaults(this);
            LeakCanary.install(this);
            Sherlock.init(this);
            Traceur.enableLogging();
        }
    }

    public AppComponent getComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .networkModule(new NetworkModule(this, BuildConfig.POKEAPI_API_URL))
                    .appModule(new AppModule(this))
                    .build();
        }
        return appComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(AppComponent appComponent) {
        this.appComponent = appComponent;
    }
}
