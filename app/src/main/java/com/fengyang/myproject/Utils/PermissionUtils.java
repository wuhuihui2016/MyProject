package com.fengyang.myproject.Utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class PermissionUtils {

    /**
     * 判断通讯录权限获取成功与否
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
            if (e.toString().contains("requires android.permission")) {
                checkCallback.onCheck(false);
            }
        }

    }

    /**
     * 联系人权限获取回调
     */
    public interface OnCheckCallback {
        void onCheck(boolean isSucess);
    }

    /**
     * 判断通讯录权限获取成功与否
     * @param checkCallback
     */
    public static void checkSDcardPermission(Context context, final OnCheckCallback checkCallback) {
        try {
            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);
            if (cursor == null) checkCallback.onCheck(false);
            else checkCallback.onCheck(true);
            cursor.close();
        } catch (Exception e) {
            Log.i("Exception", e.toString());
            if (e.toString().contains("requires android.permission")) {
                checkCallback.onCheck(false);
            }
        }

    }

    /**
     * 权限获取失败时的操作
     */
    public static void notPermission(Activity activity, String[] permissions) {
        //申请弹出获取权限系统框
        ActivityCompat.requestPermissions(activity, permissions, 0);
    }
}
