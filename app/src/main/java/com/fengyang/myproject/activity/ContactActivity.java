package com.fengyang.myproject.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fengyang.myproject.R;
import com.fengyang.myproject.utils.PermissionUtils;
import com.fengyang.myproject.utils.StringUtils;

/**
 * 权限打开后显示手机联系人
 */
public class ContactActivity extends BaseActivity {

    private Button contacts;
    private boolean isClicked = false;//按钮是否已点击标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("联系人", R.layout.activity_contact);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getContact();//申请弹出获取联系人权限系统框后用户会选择允许或拒绝，弹出框消失，消失后会再次调用onResume方法
    }

    /**
     * 通讯录权限获取
     */
    private void getContact() {//设置按钮的点击事件，进一步判断权限的获取或跳转指定界面
        //申请弹出获取联系人权限系统框后用户会选择允许或拒绝，弹出框消失，消失后会再次调用onResume方法
        contacts = (Button) findViewById(R.id.contacts);
        PermissionUtils.checkContactsPermission(ContactActivity.this, new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isClicked) {
                    if (isSucess) {
                        //权限获取成功后，如果跳转按钮已点击则直接跳转指定界面，并将标志还原
                        isClicked = false;
                        getData();
                    } else {//也要考虑某些手机（比如vivo，oppo）自动禁止权限的问题
                        StringUtils.show1Toast(context, "可能读取通讯录权限未打开，请检查后重试！");
                    }
                } else {//设置按钮的点击事件，进一步判断权限的获取或跳转指定界面
                    contacts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionUtils.checkContactsPermission(ContactActivity.this, new PermissionUtils.OnCheckCallback() {
                                @Override
                                public void onCheck(final boolean isSucess) {
                                    if (isSucess) {
                                        isClicked = false;
                                        getData();
                                    } else {
                                        //权限获取失败后再次弹出系统框，将按钮的点击跳转标志设为true,保证用户点击“允许”后可直接跳转指定界面
                                        isClicked = true;
                                        PermissionUtils.notPermission(ContactActivity.this, PermissionUtils.PERMISSIONS_READ_CONTACTS);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取数据并显示
     */
    private void getData() {
        try {
            TextView text = (TextView) findViewById(R.id.text);
            String phone_nums = "";
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);

            if (cursor != null) {
                for (cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()) {
                    String phoneNum = cursor.getString(0) + ":" //姓名
                            + cursor.getString(1).replace(" ", "").replace("-", "").replace("+86", "");//号码（去除号码中的除数字以外的其他字符）
                    phone_nums += phoneNum + "\n";
                }
                if(phone_nums.endsWith(",")) phone_nums = phone_nums.substring(0, phone_nums.length() - 1);
                text.setText("手机联系人：\n" + phone_nums);
                cursor.close();
            }
        } catch (Exception e) {}
    }

}
