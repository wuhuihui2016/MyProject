package com.fengyang.myproject.utils;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class StringUtils {

    /**
     * 字符串转成int
     * @param str 字符串
     * @return str为null或者转换出错返回-1，其他返回正常值
     */
    public static int parseStr2Int(String str) {
        if (str == null) {
            return -1;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 字符串转成浮点数
     * @param str 字符串
     * @return str为null或者转换出错返回-1，其他返回正常值
     */
    public static float parseStr2Float(String str) {
        if (str == null) {
            return -1;
        }
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 判断字符串是否是合法的16进制
     * @author: Xue Wenchao
     * @param str
     * @return: boolean
     */
    public static boolean isHexString(String str) {
        if (str == null) {
            return false;
        }
        return Pattern.matches("^[0-9a-fA-F]++$", str);
    }

    /**
     * 字符串转成Long
     * @param str 字符串
     * @return  str为null或者转换出错返回-1，其他返回正常值
     */
    public static long parseStr2Long(String str) {
        if (str == null) {
            return -1;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 计算字符个数，一个汉字算两个
     * @param s 字符串
     * @return	字符格式
     */
    public static int countWord(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        int n = s.length(), a = 0, b = 0;
        int len = 0;
        char c;
        for (int i = 0; i < n; i++) {
            c = s.charAt(i);
            if (Character.isSpaceChar(c)) {
                ++b;
            } else if (isAscii(c)) {
                ++a;
            } else {
                ++len;
            }
        }
        return len + (int) Math.ceil((a + b) / 2.0);
    }

    /**
     *
     * @param c 判断字符是否是ascii码
     * @return
     */
    public static boolean isAscii(char c) {
        return c <= 0x7f;
    }

    /**
     * 验证是否是手机号
     * @param mobile
     * @return
     */
    public static boolean isPhone(String mobile){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }
    /**
     *  1.长度至少为6位
     2.密码不能是重复的
     3.密码不能是连续的数字组合
     * @param password
     * @return true为符合规则,false为不符合规则
     */
    public static boolean checkpassword(String password){
        boolean isComplex = false;
        boolean isNumber = true;
        char[] ma = password.toCharArray();
        if (ma.length < 6 || ma.length > 28) {
//            System.out.println("密码长度为6-28位!");
            return isComplex;
        }else {
            //判断是否不包含数字  48-57对应0-9
            for (int i = 0; i < ma.length; i++) {
                if (ma[i] < 48 || ma[i] > 57) {
                    isNumber = false;
                }
            }
            if (isNumber) {
                boolean isOne = true;
                boolean isSameNumber = true;
                //123456, 12345678这样的不行
                for (int i = 0; i < ma.length - 1; i++) {
                    if (ma[i+1] - ma[i] != 1) {
                        isOne = false;
                        break;
                    }
                }
                // 000000, 111111这样的不行
                for (int i = 0; i < ma.length - 1; i++) {
                    if (ma[i+1] - ma[i] != 0) {
                        isSameNumber = false;
                        break;
                    }
                }
                if (!isSameNumber && !isOne) {
                    isComplex = true;
                }else{
                    isComplex = false;
                }
            }else {
                isComplex = true;
            }
            return isComplex;
        }
    }

    /**
     * 时间格式
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 页面消息提示(短)
     * @param context
     * @param message
     */
    public static void show1Toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 页面消息提示（长）
     * @param context
     * @param message
     */
    public static void show2Toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
