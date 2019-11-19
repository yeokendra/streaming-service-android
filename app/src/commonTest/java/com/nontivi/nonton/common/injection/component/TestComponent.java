package com.nontivi.nonton.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import com.nontivi.nonton.common.injection.module.ApplicationTestModule;
import com.nontivi.nonton.injection.component.AppComponent;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends AppComponent {
}
