package com.fengyang.myproject;

import android.app.Application;
import android.content.Intent;

import com.fengyang.myproject.receiver.MyReceiver;
import com.fengyang.process.RequestManager;
import com.fengyang.toollib.utils.ContansUtils;
import com.fengyang.toollib.utils.CrashHandler;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.SystemUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class MyApp extends Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        CrashHandler.getInstance().init(getApplicationContext());//程序崩溃日志输出保存

        ContansUtils.setPres(this);//设置存储空间，获取编辑器

        RequestManager.init(this);//网路请求框架初始化

        // TODO 生成随机数，发送广播（依赖点击事件的触发）
        do2toReceive();
    }

    /**
     * 获取application实例
     */
    public static Application getInstance() {
        return instance;
    }

    /**
     * 生成随机数，发送广播
     */
    Timer timer = null;
    private void do2toReceive() {
        MyReceiver.registerCallBack(new MyReceiver.OnReceiveCallback() {
            @Override
            public void onCheck(boolean isStart) {
                if (isStart) {
                    timer = new Timer();
                    TimerTask task = new TimerTask() {

                        @Override
                        public void run() {
                            //生成1-100之间的随机数（每5秒执行一次）
                            int i = (int)(Math.random() * 100) + 1;
                            LogUtils.i("random", i + "");
                            if (i % 2 == 0) {//如果是2的倍数，则发送广播
                                Intent intent = new Intent(MyReceiver.ACTION);
                                intent.putExtra("random", i);
                                sendBroadcast(intent);
                            }
                        }
                    };
                    timer.schedule(task, 0, 5000);//每5秒执行一次
                } else {
                    SystemUtils.stopTimer(timer);
                }
            }
        });
    }

}
