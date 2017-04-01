package com.fengyang.myproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.fengyang.myproject.utils.StringUtils;
import com.fengyang.myproject.utils.SystemUtils;
import com.fengyang.myproject.receiver.NetReceiver;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class BaseActivity extends Activity {

    protected Context context;//获取当前对象
    protected Activity activity;//获取当前对象
    protected String TAG;//当前界面输出log时的标签字段

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化
        init();

    }

    /**
     * 初始化
     */
    private void init() {
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //初始化常量
        context = this;
        activity = this;
        TAG = getLocalClassName();

        //判断网络
        if (! SystemUtils.isNetworkConnected(context)) {
            StringUtils.show1Toast(context, "当前网络不可用");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //注册网络监听
        NetReceiver.registerHandler(new NetReceiver.OnNetEventHandler() {
            @Override
            public void onNetChange() {
                if (! SystemUtils.isNetworkConnected(context)) {
                    StringUtils.show1Toast(context, "当前网络不可用");
                }
            }
        });
    }
}
