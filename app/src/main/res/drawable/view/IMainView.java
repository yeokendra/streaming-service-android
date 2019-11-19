package com.vexanium.vexgift.module.main.view;

import com.vexanium.vexgift.base.BaseView;
import com.vexanium.vexgift.bean.response.HttpResponse;

import java.io.Serializable;

public interface IMainView extends BaseView {
    void handleResult(Serializable data, HttpResponse errorResponse);
}
