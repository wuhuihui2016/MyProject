package com.fengyang.myproject.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.fengyang.myproject.R;

/**
 * Created by wuhuihui on 2017/3/28.
 */
public class WebViewActivity extends BaseActivity{

    private WebView contentWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentWebView = (WebView) findViewById(R.id.webview);
        // 启用javascript
        contentWebView.getSettings().setJavaScriptEnabled(true);
        // 从assets目录下面的加载html
        contentWebView.loadUrl("file:///android_asset/web.html");
        contentWebView.addJavascriptInterface(this,"android");


        //无参调用Js点击
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 无参数调用
                contentWebView.loadUrl("javascript:javacalljs()");

            }
        });
        //有参调用Js点击
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 传递参数调用
                contentWebView.loadUrl("javascript:javacalljswith(" + "'http://blog.csdn.net/Leejizhou'" + ")");
            }
        });


    }
}
