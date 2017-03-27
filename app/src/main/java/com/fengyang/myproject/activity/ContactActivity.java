package com.fengyang.myproject.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;

import com.fengyang.myproject.R;

/**
 * 权限打开后显示手机联系人
 */
public class ContactActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        getContact();
    }

    /**
     * 通讯录权限获取
     */
    private void getContact() {
        try {
            TextView text = (TextView) findViewById(R.id.text);
            String phone_nums = "";
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);

            if (cursor != null) {
                for (cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()) {
                    String phoneNum = cursor.getString(1).replace(" ", "").replace("-", "");//去除号码中的除数字以外的其他字符
                    phone_nums += phoneNum + ",";
                }
                if(phone_nums.endsWith(",")) phone_nums = phone_nums.substring(0, phone_nums.length() - 1);
                text.setText("手机联系人：\n" + phone_nums);
                cursor.close();
            }
        } catch (Exception e) {}

    }


}
