package com.fengyang.myproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fengyang.toollib.utils.LogUtils;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";
    public static String ACTION = "com.example.fengyang.myproject.listener";
    public static boolean randomStarted = false;
    public static OnReceiveCallback callback;
    public static OnReceiveCallback_Num callback_num;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent != null) {
                if (intent.getAction().equals(ACTION)) {
                   if (intent.hasExtra("start")) {
                       boolean isStart = intent.getBooleanExtra("start", false);
                       randomStarted = isStart;
                       if (! randomStarted) callback_num = null;
                       callback = getICallBack();
                       if (callback != null) {
                           callback.onCheck(isStart);
                       }
                   } else {
                       int number = intent.getIntExtra("random", 0);
                       callback_num = getCallback_num();
                       LogUtils.i("onReceive", "" + number + "--" + callback_num.toString());
                       if (callback_num != null) {
                           callback_num.onCheck(number);
                       }
                   }

                }
            }
        } catch (Exception e) {
            LogUtils.i(TAG, e.toString());
        }
    }

    /**
     * 启动停止服务回调操作
     */
    public interface OnReceiveCallback {
        void onCheck(boolean isStart);
    }

    /**
     * 启动服务后回显数字
     */
    public interface OnReceiveCallback_Num {
        void onCheck(int number);
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

    /**
     * 注册回调
     * @return
     */
    public static void registerCallBackNum(OnReceiveCallback_Num receiveCallback_num){
        callback_num = receiveCallback_num;
    }

    /**
     * 调用
     * @return
     */
    private static OnReceiveCallback_Num getCallback_num(){
        return callback_num;
    }
}
