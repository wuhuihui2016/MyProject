package com.fengyang.myproject.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fengyang.myproject.R;
import com.fengyang.toollib.base.BaseActivity;
import com.fengyang.toollib.utils.StringUtils;

/**
 * Created by wuhuihui on 2017/3/28.
 */
public class WebViewActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("JS交互", R.layout.activity_web);
        webView = (WebView) findViewById(R.id.webview);
        // 启用javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 从assets目录下面的加载html
        webView.loadUrl("file:///android_asset/web.html");
        webView.addJavascriptInterface(this,"android");

        //拦截url
        webView.setWebViewClient(new WebViewClient() {

            @Override
            // 在点击请求的是链接是才会调用，读取到某些特殊的URL解析跳转到指定APP页面,而不是浏览器。
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 判断url链接中是否含有某个字段，如果有就执行指定的跳转（不执行跳转url链接），如果没有就加载url链接
                if (url.contains("activities")) {
                    new AlertDialog.Builder(WebViewActivity.this).setMessage(url).show();
                    return true;
                } else {
                    return false;
                }
            }
        });


        //无参调用Js点击
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 无参数调用
                webView.loadUrl("javascript:javacalljs()");

            }
        });
        //有参调用Js点击
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 传递参数调用
                webView.loadUrl("javascript:javacalljswith(" + "'http://blog.csdn.net/Leejizhou'" + ")");
            }
        });
    }

    //自定义协议方法********* 由于安全原因 需要加 @JavascriptInterface
    @JavascriptInterface
    public void startFunction(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StringUtils.show1Toast(getApplicationContext(), "无参");
            }
        });
    }

    @JavascriptInterface
    public void startFunction(final String text){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                new AlertDialog.Builder(WebViewActivity.this).setMessage(text).show();

            }
        });
    }
    //*********自定义协议方法



    //点击返回键，返回上一个页面，而不是退出程序
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.clearCache(true);//清除webView缓存
    }

}
