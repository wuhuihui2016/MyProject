package com.fengyang.myproject.activity;

import android.os.Bundle;

import com.fengyang.myproject.R;
import com.fengyang.toollib.base.BaseActivity;

/**
 * 权限打开后显示手机联系人
 */
public class LoginActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("登录", R.layout.activity_login);

    }

}
