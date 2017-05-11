package com.fengyang.myproject;

import android.app.Application;
import android.content.Intent;

import com.fengyang.myproject.receiver.MyReceiver;
import com.fengyang.toollib.utils.ContansUtils;
import com.fengyang.toollib.utils.CrashHandler;

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

        do2toReceive();//生成随机数，发送广播
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
    private void do2toReceive() {

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                //生成1-100之间的随机数（每5秒执行一次）
                int i = (int)(Math.random() * 100) + 1;
//                LogUtils.i("random", i + "");
                if (i % 5 == 0) {//如果是5的倍数，则发送广播
                    Intent intent = new Intent(MyReceiver.ACTION);
                    intent.putExtra("random", i);
                    sendBroadcast(intent);
                }
            }
        };
        timer.schedule(task, 0, 5000);//每5秒执行一次

    }

}
