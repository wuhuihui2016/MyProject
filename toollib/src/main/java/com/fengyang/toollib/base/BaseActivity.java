package com.fengyang.toollib.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fengyang.toollib.R;
import com.fengyang.toollib.utils.ActivityUtils;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.NetReceiver;
import com.fengyang.toollib.utils.StringUtils;
import com.fengyang.toollib.utils.SystemUtils;
import com.fengyang.toollib.view.SildingFinishLinearLayout;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener{

    protected Context context;//获取当前对象
    protected Activity activity;//获取当前对象
    protected String TAG;//当前界面输出log时的标签字段
    protected SildingFinishLinearLayout sildingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity();

    }

    /**
     * 初始化Activity
     */
    private void initActivity () {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置竖屏
//        //沉浸式状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setContentView(R.layout.activity_base_layout);
        sildingLayout = (SildingFinishLinearLayout) findViewById(R.id.sildingLayout);

        ActivityUtils.addActivity(this);
        context = this; activity = this; TAG = getLocalClassName(); //初始化常量

        if (! SystemUtils.isNetworkConnected(context))  StringUtils.show1Toast(context, "当前网络不可用"); //判断网络
        if (isOtherActivity()) {
            //设置右滑关闭当前Activity
            SildingFinishLinearLayout sildingLayout = (SildingFinishLinearLayout) findViewById(R.id.sildingLayout);
            if (sildingLayout != null) {
                sildingLayout.setBackgroundResource(R.color.app_background);
                sildingLayout.setOnSildingFinishListener(new SildingFinishLinearLayout.OnSildingFinishListener() {

                    @Override
                    public void onSildingFinish() {
                        finish();
                    }
                });
            }
        }
    }

    public void setTitle(CharSequence title) {
        if(isOtherActivity()) {

            //设置当前界面的title
            TextView titleView = (TextView) findViewById(R.id.title);
            titleView.setText(title);

        } else {
            LogUtils.i("setContentView", "MainActivity's  layout is setten!");
        }

    }

    /**
     * 设置中间内容布局
     * @param layoutID
     * @param title
     */
    protected void setContentView(String title, int layoutID) {
        if(isOtherActivity()) {

            ImageButton return_btn = (ImageButton) findViewById(R.id.return_btn);
            return_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            //设置当前界面的title
            TextView titleView = (TextView) findViewById(R.id.title);
            titleView.setText(title);

            //加载中间布局
            FrameLayout content_layout = (FrameLayout) findViewById(R.id.content_layout);
            content_layout.removeAllViews();
            View view = LayoutInflater.from(this).inflate(layoutID, null);
            content_layout.addView(view);
        } else {
            LogUtils.i("setContentView", "MainActivity's  layout is setten!");
        }

    }

    /**
     * 设置界面右上角按钮的点击事件
     * @param text
     * @param listener
     */
    protected void setRightBtnListener(CharSequence text, View.OnClickListener listener) {
        if (! TextUtils.isEmpty(text)) {
            Button right_btn = (Button) findViewById(R.id.right_btn);
            right_btn.setVisibility(View.VISIBLE);
            right_btn.setText(text);
            right_btn.setOnClickListener(listener);
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

    @Override
    public void onClick(View v) {}

    @Override
    public void finish() {
        super.finish();
        if (isOtherActivity()) {
            overridePendingTransition(0, R.anim.slide_right_out);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isOtherActivity()) {
            overridePendingTransition(0, R.anim.slide_right_out);
        }
    }

    /**
     * 判断当前activity是不是非MainActivity
     * @return
     */
    private boolean isOtherActivity() {
        if (TAG.contains("MainActivity")) return false;
        else return true;
    }


}
