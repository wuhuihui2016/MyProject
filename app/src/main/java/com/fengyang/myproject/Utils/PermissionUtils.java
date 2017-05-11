package com.fengyang.myproject.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.fengyang.toollib.utils.FileUtils;
import com.fengyang.toollib.utils.StringUtils;

import java.io.File;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class PermissionUtils {

    public static int REQUESTCODE = 0;
    public static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String[] PERMISSIONS_READ_CONTACTS = {Manifest.permission.READ_CONTACTS};
    public static String[] PERMISSIONS_CAMERA = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    /**
     * 判断通讯录权限获取成功与否
     * 失败后本方法不调用系统弹出框
     * @param checkCallback
     */
    public static void checkContactsPermission(Context context, final OnCheckCallback checkCallback) {
        try {
            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);
            if (cursor == null) checkCallback.onCheck(false);
            else checkCallback.onCheck(true);
            cursor.close();
        } catch (Exception e) {
            Log.i("Exception", e.toString());
            if (e.toString().contains("permission")) {
                checkCallback.onCheck(false);
            }
        }

    }


    /**
     * 判断SDcard权限获取成功与否
     * 失败后本方法不调用系统弹出框
     * @param activity
     * @param checkCallback
     */
    public static void checkSDcardPermission(Activity activity, OnCheckCallback checkCallback) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            checkCallback.onCheck(false);
        } else {
            checkCallback.onCheck(true);
        }
    }

    /**
     * 判断相机权限获取成功与否
     * 失败后本方法不调用系统弹出框
     * @param activity
     * @param checkCallback
     */
    public static void checkCameraPermission(Activity activity, OnCheckCallback checkCallback) {
        try {//权限获取异常处理
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){   //如果可用
                startCamera(activity);
                checkCallback.onCheck(true);
            } else {
                StringUtils.show1Toast(activity, "SDCard不可用!");
            }
        } catch (Exception e) {
            if (e.toString().contains("permission")) {
                checkCallback.onCheck(false);
            }
        }
    }

    /**
     * 跳转系统相机
     * @param activity
     */
    public static void startCamera(Activity activity) {
        File file = new File(FileUtils.getDirFile(FileUtils.imagePath), "camera.jpg");
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        activity.startActivityForResult(intent, REQUESTCODE);
    }

    /**
     * 权限获取回调
     */
    public interface OnCheckCallback {
        void onCheck(boolean isSucess);
    }

    /**
     * 权限获取失败时的操作
     */
    public static void notPermission(Activity activity, String[] permissions) {
        //申请弹出获取权限系统框
        ActivityCompat.requestPermissions(activity, permissions, REQUESTCODE);
    }
}
