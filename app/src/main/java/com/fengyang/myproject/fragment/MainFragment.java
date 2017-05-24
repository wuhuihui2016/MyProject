package com.fengyang.myproject.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.fengyang.music.activity.MusicActivity;
import com.fengyang.myproject.R;
import com.fengyang.myproject.activity.ContactActivity;
import com.fengyang.myproject.activity.ImageActivity;
import com.fengyang.myproject.activity.TextActivity;
import com.fengyang.myproject.activity.WebViewActivity;
import com.fengyang.myproject.receiver.MyReceiver;
import com.fengyang.myproject.utils.DialogUtils;
import com.fengyang.toollib.utils.FileUtils;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.StringUtils;
import com.fengyang.toollib.utils.SystemUtils;
import com.fengyang.toollib.view.IOSScrollView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by wuhuihui on 2017/5/11.
 * TODO 子功能的主架构
 *   TODO 1.跳转ContactActivity获取联系人权限
 *   TODO 2.跳转页面后获取SDcard权限下载图片并显示
 *   TODO 3.跳转TakePhotoActivity获取相机权限
 *   TODO 4.发送广播的方法在MyApp.class中
 *   TODO 5.JS交互
 *   TODO 6.文字游戏
 *   TODO 7.懒猫音乐
 *   TODO 8.截屏
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private View content;//内容布局

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        content = inflater.inflate(R.layout.fragment_main, container, false);

        initView();

        return content;
    }

    /**
     * 初始化控件
     */
    private void initView() {

        //当页面滑动到中间位置，导致看不到顶部时显示到达顶部的按钮
        final IOSScrollView scrollView = (IOSScrollView) content.findViewById(R.id.scrollView);
        final ImageButton top_btn = (ImageButton) content.findViewById(R.id.top_btn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > 500) {
                        top_btn.setVisibility(View.VISIBLE);
                        top_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scrollView.fullScroll(ScrollView.FOCUS_UP);
                            }
                        });
                    } else {
                        top_btn.setVisibility(View.GONE);
                    }
                }
            });
        }

        //TODO 1.跳转ContactActivity获取联系人权限
        content.findViewById(R.id.toContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ContactActivity.class));
            }
        });

        //TODO 2.跳转页面后下载图片并显示
        content.findViewById(R.id.toDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra("isDownload", true);
                startActivity(intent);
            }
        });

        //TODO 3.获取相机权限跳转TakePhotoActivity
        content.findViewById(R.id.toCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra("isDownload", false);
                startActivity(intent);
            }
        });

        //TODO 4.发送广播的方法在MyApp.class中
        content.findViewById(R.id.toReceive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemUtils.isNotFastClick()) {
                    final Button toReceive = (Button) content.findViewById(R.id.toReceive);
                    String msg = null;
                    if (MyReceiver.randomStarted)  msg = "停止顺风耳！";
                    else msg = "启动顺风耳！";
                    //如果该服务只在一个界面执行，由于OnReceiveCallback为静态必须重新赋值，需在onResume时再次调用方法
                    DialogUtils.showMsgDialog(getActivity(), "", msg, new DialogUtils.DialogListener() {
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                            //启动或停止随机取数
                            Intent intent = new Intent(MyReceiver.ACTION);
                            intent.putExtra("start", ! MyReceiver.randomStarted);
                            getActivity().sendBroadcast(intent);

                            if (MyReceiver.randomStarted) {
                                StringUtils.show2Toast(getActivity(), "顺风耳已停止~~");
                                toReceive.setText("侧耳倾听");
                                toReceive.setTextColor(getResources().getColor(R.color.black));
                            } else {
                                StringUtils.show2Toast(getActivity(), "顺风耳已启动~~");
                                toReceive.setTextColor(getResources().getColor(R.color.app_color));
                                MyReceiver.registerCallBackNum(new MyReceiver.OnReceiveCallback_Num() {
                                    @Override
                                    public void onCheck(int number) {
                                        toReceive.setText("侧耳倾听：" + number);
                                    }
                                });
                            }
                        }
                    }, new DialogUtils.DialogListener() {
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);
                        }
                    });

                }
            }
        });

        //TODO 5.JS交互
        content.findViewById(R.id.toJSMutual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WebViewActivity.class));
            }
        });

        //TODO 6.文字游戏
        content.findViewById(R.id.toWordGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TextActivity.class));
            }
        });
        //TODO 7.懒猫音乐
        content.findViewById(R.id.music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MusicActivity.class));
            }
        });

        //TODO 8.截屏
        content.findViewById(R.id.screen_shot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String filePath = FileUtils.imagePath + "screenshot"
                            + StringUtils.formatDate4fileName() + ".png";

                    File file = new File(filePath);
                    FileOutputStream os = new FileOutputStream(file);
                    //APP内部截屏最好不包含状态栏，如果含状态栏也是出现留白现象
                    Bitmap bmp = SystemUtils.snapShotWithoutStatusBar(getActivity());//不包含状态栏
//                    Bitmap bmp = SystemUtils.snapShotWithStatusBar(getActivity());//包含状态栏
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                    if (file.exists()) DialogUtils.showMsgDialog(getActivity(),"截屏成功！", "保存在" + filePath);
                } catch (Exception e) {LogUtils.i("Exception", e.toString());}

            }
        });
    }

}
