package com.fengyang.myproject.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fengyang.music.activity.MusicActivity;
import com.fengyang.myproject.R;
import com.fengyang.myproject.receiver.MyReceiver;
import com.fengyang.myproject.utils.DialogUtils;
import com.fengyang.myproject.utils.PermissionUtils;
import com.fengyang.toollib.base.BaseActivity;
import com.fengyang.toollib.utils.StringUtils;

/**
 * TODO 子功能的主架构
 *   TODO 1.跳转ContactActivity获取联系人权限
 *   TODO 2.跳转页面后获取SDcard权限下载图片并显示
 *   TODO 3.跳转TakePhotoActivity获取相机权限
 *   TODO 4.发送广播的方法在MyApp.class中
 *   TODO 5.JS交互
 *   TODO 6.文字游戏
 *   TODO 7.懒猫音乐
 */
public class MainActivity extends BaseActivity {

    private boolean isClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            DialogUtils.showMsgDialog(MainActivity.this, "", "顺风耳已启动~~", new DialogUtils.DialogListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    StringUtils.show2Toast(getApplicationContext(), "顺风耳已启动~~");
                }
            }, new DialogUtils.DialogListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                }
            });
            final Button toReceive = (Button) findViewById(R.id.toReceive);
            toReceive.setTextColor(Color.parseColor("#541E00"));

            MyReceiver.registerCallBack(new MyReceiver.OnReceiveCallback() {
                @Override
                public void onCheck(int number) {
                    toReceive.setText("我是顺风耳：" + number);
                }
            });

        } else if (v.getId() == R.id.toJSMutual) {
            //TODO 5.JS交互
            startActivity(new Intent(getApplication(), WebViewActivity.class));

        } else if (v.getId() == R.id.toWordGame) {
            //TODO 6.文字游戏
            startActivity(new Intent(getApplication(), TextActivity.class));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toMusic();
    }

    /**
     *  TODO 7.懒猫音乐
     *  需打开文件权限才可进入音乐
     * 注意:避免用户拒绝访问权限时出现无限循环的系统框弹出
     */
    private void toMusic() {
        final Button music_btn = (Button) findViewById(R.id.music);
        PermissionUtils.checkSDcardPermission(MainActivity.this, new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isClicked) {
                    if (isSucess) {
                        isClicked = false;
                        startActivity(new Intent(getApplication(), MusicActivity.class));
                    } else {//也要考虑某些手机（比如vivo，oppo）自动禁止权限的问题
                        StringUtils.show1Toast(context, "可能读取SDCard权限未打开，请检查后重试！");
                    }
                } else {//设置按钮的点击事件，进一步判断权限的获取或跳转指定界面
                    music_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionUtils.checkSDcardPermission(MainActivity.this, new PermissionUtils.OnCheckCallback() {
                                @Override
                                public void onCheck(final boolean isSucess) {
                                    if (isSucess) {
                                        isClicked = false;
                                        startActivity(new Intent(getApplication(), MusicActivity.class));
                                    } else {
                                        //权限获取失败后再次弹出系统框，将按钮的点击跳转标志设为true,保证用户点击“允许”后可直接跳转指定界面
                                        isClicked = true;
                                        PermissionUtils.notPermission(MainActivity.this, PermissionUtils.PERMISSIONS_STORAGE);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }

}
