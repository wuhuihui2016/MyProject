package com.fengyang.myproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.fengyang.myproject.R;
import com.fengyang.myproject.Utils.PermissionUtils;
import com.fengyang.myproject.Utils.StringUtils;
import com.fengyang.myproject.receiver.MyReceiver;

/**
 * 子功能的主架构
 * 1.判断读取通讯录权限，获取权限后跳转页面ContactActivity
 */
public class MainActivity extends BaseActivity {

    private boolean isClicked = false;//按钮是否已点击标志
    private ImageButton toContactActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO 3.跳转页面后下载图片并显示
        do3Download();
        //TODO 5.JS交互
        do5JSMutual();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO 1.获取联系人权限并跳转指定ContactActivity
        do1Contact();//申请弹出获取联系人权限系统框后用户会选择允许或拒绝，弹出框消失，消失后会再次调用onResume方法

        //TODO 2.发送广播的方法在MyApp.class中
        do2Receiver();//由于OnReceiveCallback为静态必须重新赋值，需在onResume时再次调用方法


    }


    /**
     * 获取联系人并跳转ContactActivity
     */
    private void do1Contact() {
        //申请弹出获取联系人权限系统框后用户会选择允许或拒绝，弹出框消失，消失后会再次调用onResume方法
        toContactActivity = (ImageButton) findViewById(R.id.toContactActivity);
        PermissionUtils.checkContactsPermission(getApplicationContext(), new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isClicked) {
                    if (isSucess) {
                        //权限获取成功后，如果跳转按钮已点击则直接跳转指定界面，并将标志还原
                        isClicked = false;
                        startActivity(new Intent(getApplicationContext(), ContactActivity.class));
                    } else {//也要考虑某些手机（比如vivo，oppo）自动禁止权限的问题
                        StringUtils.show1Toast(context, "可能读取通讯录权限未打开，请检查后重试！");
                    }
                } else {//设置按钮的点击事件，进一步判断权限的获取或跳转指定界面
                    toContactActivity.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionUtils.checkContactsPermission(getApplicationContext(), new PermissionUtils.OnCheckCallback() {
                                @Override
                                public void onCheck(final boolean isSucess) {
                                    if (isSucess) {
                                        isClicked = false;
                                        startActivity(new Intent(getApplicationContext(), ContactActivity.class));
                                    } else {
                                        //权限获取失败后再次弹出系统框，将按钮的点击跳转标志设为true,保证用户点击“允许”后可直接跳转指定界面
                                        isClicked = true;
                                        PermissionUtils.notPermission(MainActivity.this, PermissionUtils.PERMISSIONS_READ_CONTACTS);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    /**
     * 接收数据并显示数据
     */
    private void do2Receiver() {
        MyReceiver.registerCallBack(new MyReceiver.OnReceiveCallback() {
            @Override
            public void onCheck(int i) {
                if (i != 0) {
                    Button toReceive = (Button) findViewById(R.id.toReceive);
                    toReceive.setText("我是顺风耳：" + i);
                }
            }
        });
    }


    /**
     * 跳转页面后下载图片并显示
     */
    private void do3Download() {
        ImageButton toDownload = (ImageButton) findViewById(R.id.toDownload);
        toDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ImageActivity.class));
            }
        });

    }

    /**
     * JS交互
     */
    private void do5JSMutual() {
        Button toJSMutual = (Button) findViewById(R.id.toJSMutual);
        toJSMutual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), WebViewActivity.class));
            }
        });
    }


}
