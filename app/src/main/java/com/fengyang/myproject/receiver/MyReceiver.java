package com.fengyang.myproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class MyReceiver extends BroadcastReceiver {

    public static String ACTION = "com.example.fengyangtech.myexample.receiver";
    public static OnReceiveCallback callback;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent != null) {
                if (intent.getAction().equals(ACTION)) {
                    int i = intent.getIntExtra("random", 0);
                    callback = getICallBack();
                    if (callback != null) {
                        callback.onCheck(i);
                    }
                }
            }
        } catch (Exception e) {}
    }


    public interface OnReceiveCallback {
        void onCheck(int i);
    }


    /**
     * 注册回调
     * @return
     */
    public static void registerCallBack(OnReceiveCallback receiveCallback){
        callback = receiveCallback;
    }

    /**
     * 调用
     * @return
     */
    private static OnReceiveCallback getICallBack(){
        return callback;
    }
}
