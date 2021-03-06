package com.nontivi.nonton.injection.component;

import dagger.Subcomponent;

import com.nontivi.nonton.features.home.homepage.HomepageFragment;
import com.nontivi.nonton.features.main.MainActivity;
import com.nontivi.nonton.injection.PerFragment;
import com.nontivi.nonton.injection.module.FragmentModule;

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {
    void inject(HomepageFragment homepageFragment);
}
