package com.nontivi.nonton.injection.component;

import dagger.Subcomponent;
import com.nontivi.nonton.features.detail.DetailActivity;
import com.nontivi.nonton.features.home.HomeActivity;
import com.nontivi.nonton.features.main.MainActivity;
import com.nontivi.nonton.injection.PerActivity;
import com.nontivi.nonton.injection.module.ActivityModule;

@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

    void inject(DetailActivity detailActivity);

    void inject(HomeActivity homeActivity);
}
