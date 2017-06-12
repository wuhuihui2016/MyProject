package com.fengyang.callback;

import android.content.Context;

import com.android.volley.VolleyError;
import com.fengyang.process.RequestParams;

/**
 * Created by FengYang on 2016/11/18.
 */

public interface IVolleyErro {
    /**
     * 调用那个url,出现的问题
     * @param volleyError
     * @param url
     * @param iCallBack
     */
    void onErrorResponse(Context context,byte method, VolleyError volleyError, String url, RequestParams params, ICallBack iCallBack);
}
