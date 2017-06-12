package com.fengyang.process;


/**
 * Created by FengYang on 2016/11/18.
 */

public class Constant {
    //例子
    /*public static int KCLIENT = R.raw.kclient207; //ssl证书cer文件id
    public static int TCLIENT = R.raw.tclient207; //ssl证书cer文件id*/
    public static int KCLIENT = 100; //ssl证书cer文件id
    public static int TCLIENT = 100;
    public final static int TIME_OUT = 10000; //普通网络请求时间
    public final static int TIME_OUT_CHEBY = 1000 * 90; //重新获取token网络请求时间
}
