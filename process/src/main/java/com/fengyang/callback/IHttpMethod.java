package com.fengyang.callback;

import android.content.Context;

import com.fengyang.process.RequestParams;

/**
 * Created by FengYang on 2016/11/18.
 */

public interface IHttpMethod {
    //http请求的是哪一个方法
    byte REQUEST_GET = 0;
    byte REQUEST_POST = 1;
    byte REQUEST_SSL_GET = 2;
    byte REQUEST_SSL_POST = 3;
    void sendGETRequest(Context context,String url, RequestParams params, ICallBack icallBack);
    void sendPostRequest(Context context,String url, RequestParams params,ICallBack icallBack);
}
