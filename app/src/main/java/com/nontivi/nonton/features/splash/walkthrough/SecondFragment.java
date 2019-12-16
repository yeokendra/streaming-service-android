package com.nontivi.nonton.features.splash.walkthrough;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nontivi.nonton.R;
import com.nontivi.nonton.features.base.BaseFragment;
import com.nontivi.nonton.injection.component.FragmentComponent;


public class SecondFragment extends BaseFragment {

    public static SecondFragment newInstance() {
        return new SecondFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_walkthrough_2;
    }

    @Override
    protected void inject(FragmentComponent fragmentComponent) {

    }

    @Override
    protected void attachView() {

    }

    @Override
    protected void detachPresenter() {

    }
}
