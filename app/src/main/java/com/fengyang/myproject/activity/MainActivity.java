package com.fengyang.myproject.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fengyang.myproject.R;
import com.fengyang.myproject.receiver.MyReceiver;
import com.fengyang.myproject.utils.StringUtils;

/**
 * TODO 子功能的主架构
 *   TODO 1.跳转ContactActivity获取联系人权限
 *   TODO 2.跳转页面后获取SDcard权限下载图片并显示
 *   TODO 3.跳转TakePhotoActivity获取相机权限
 *   TODO 4.发送广播的方法在MyApp.class中
 *   TODO 5.JS交互
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView("", R.layout.activity_main);
    }

    public void onClick(View v) {

        if (v.getId() == R.id.toContact) {
            //TODO 1.跳转ContactActivity获取联系人权限
            startActivity(new Intent(getApplicationContext(), ContactActivity.class));

        } else if (v.getId() == R.id.toDownload || v.getId() == R.id.toCamera) {
            Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
            if (v.getId() == R.id.toDownload) intent.putExtra("isDownload", true);//TODO 2.跳转页面后下载图片并显示
            else intent.putExtra("isDownload", false);//TODO 3.获取相机权限跳转TakePhotoActivity
            startActivity(intent);

        } else if (v.getId() == R.id.toReceive) {
            //TODO 4.发送广播的方法在MyApp.class中
            //如果该服务只在一个界面执行，由于OnReceiveCallback为静态必须重新赋值，需在onResume时再次调用方法
            StringUtils.show1Toast(getApplicationContext(), "顺风耳已启动~~");
            final Button toReceive = (Button) findViewById(R.id.toReceive);
            toReceive.setTextColor(Color.parseColor("#541E00"));

            MyReceiver.registerCallBack(new MyReceiver.OnReceiveCallback() {
                @Override
                public void onCheck(int number) {
                    toReceive.setText("我是顺风耳：" + number);
                }
            });

        } else if (v.getId() == R.id.toJSMutual) {
            startActivity(new Intent(getApplication(), WebViewActivity.class));

        }
    }
}
