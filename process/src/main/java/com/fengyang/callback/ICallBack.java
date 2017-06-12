package com.fengyang.callback;

import android.view.View;

import org.json.JSONObject;

/**
 * Created by FengYang on 2016/11/18.
 */

public interface ICallBack {
    void onSuccess(JSONObject jsonObject);
    void onFailure();
}
