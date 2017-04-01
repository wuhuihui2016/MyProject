package com.fengyang.myproject;

import android.app.Application;
import android.content.Intent;

import com.fengyang.myproject.utils.ContansUtils;
import com.fengyang.myproject.utils.CrashHandler;
import com.fengyang.myproject.utils.LogUtils;
import com.fengyang.myproject.receiver.MyReceiver;

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
        do2toReceive();//生成随机数，发送广播
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
                int i = (int)(Math.random() * 100);
                if (i % 5 == 0) {//如果是5的倍数，则发送广播
                    Intent intent = new Intent(MyReceiver.ACTION);
                    intent.putExtra("random", i);
                    sendBroadcast(intent);
                    LogUtils.i("random", i + "");
                }
            }
        };
        timer.schedule(task, 0, 5000);//每5秒执行一次

    }

    /**
     * 保存键值对
     */
    public void setValue(String key, String value) {
        ContansUtils.put(instance, key, value);
    }

    /**
     * 以键获取值
     * @return
     */
    public Object getValue(String key) {
        return (Object) ContansUtils.get(instance, key, "");
    }

}
