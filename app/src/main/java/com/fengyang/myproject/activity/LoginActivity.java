package com.fengyang.myproject.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.fengyang.myproject.MyApp;
import com.fengyang.myproject.R;
import com.fengyang.myproject.utils.DialogUtils;
import com.fengyang.toollib.base.BaseActivity;
import com.fengyang.toollib.utils.ContansUtils;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.StringUtils;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity {

    private boolean isLogined = false;

    private String user_name;
    private EditText nameEdt, pwdEdt, pwdEdt2;
    private Button login_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MyApp.getIsLogined()) {
            setContentView("修改密码", R.layout.activity_login);
            findViewById(R.id.pwd2_layout).setVisibility(View.VISIBLE);
            Button login_in = (Button) findViewById(R.id.login_in);
            login_in.setText("提  交");
            isLogined = true;
            user_name = (String) ContansUtils.get("name", "");
        } else {
            setContentView("登录", R.layout.activity_login);
        }

        initView();

        commit();
    }

    /**
     * 初始化数据
     */
    private void initView() {
        nameEdt = (EditText) findViewById(R.id.username);
        if (isLogined) {
            nameEdt.setText(user_name);
            nameEdt.setEnabled(false);
        } else {
            //用户名输入
            final ImageView clear_name = (ImageView) findViewById(R.id.clear_name);
            nameEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        clear_name.setVisibility(View.VISIBLE);
                        clear_name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nameEdt.setText("");
                            }
                        });
                    } else clear_name.setVisibility(View.GONE);
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });
        }

        //密码输入
        pwdEdt = (EditText) findViewById(R.id.password);
        final ImageView clear_pwd = (ImageView) findViewById(R.id.clear_pwd);
        final CheckBox seenCheck = (CheckBox) findViewById(R.id.seenCheck);
        seenCheck.setChecked(false);
        pwdEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clear_pwd.setVisibility(View.VISIBLE);
                    clear_pwd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pwdEdt.setText("");
                        }
                    });
                } else clear_pwd.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //重复密码输入
        pwdEdt2 = (EditText) findViewById(R.id.password2);
        final ImageView clear_pwd2 = (ImageView) findViewById(R.id.clear_pwd2);
        pwdEdt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clear_pwd2.setVisibility(View.VISIBLE);
                    clear_pwd2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pwdEdt2.setText("");
                        }
                    });

                } else clear_pwd.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


        //密文明文显示
        seenCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //设置EditText文本为可见的
                    pwdEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    pwdEdt2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //设置EditText文本为隐藏的
                    pwdEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    pwdEdt2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        login_in = (Button) findViewById(R.id.login_in);
    }

    /**
     * 提交数据（更新或新增）
     */
    private void commit() {
        //提交用户名密码登录
        login_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final String name = nameEdt.getText().toString().trim().replace(" ", "");
                    String pwd = pwdEdt.getText().toString().trim().replace(" ", "");
                    if (isLogined) {
                        String pwd2 = pwdEdt2.getText().toString().trim().replace(" ", "");
                        if (pwd.equals(pwd2)) {
                            MyApp.utils.newUser(name, pwd);
                            if (MyApp.utils.userExists(name)) {
                                StringUtils.show1Toast(activity, "密码修改成功！");
                                finish();
                            } else {
                                StringUtils.show1Toast(activity, "密码修改失败！");
                            }
                        } else StringUtils.show1Toast(getApplicationContext(), "请确认密码一致！");
                    } else {
                        if (name.length() > 0 && pwd.length() > 0) {
                            if (MyApp.utils.userExists(name)) {
                                if (! pwd.equals(MyApp.utils.getUser(name).getPwd())) {
                                    DialogUtils.showMsgDialog(activity, "登录提示",
                                            "密码不正确，登录失败！\n是否修改密码",
                                            new DialogUtils.DialogListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    super.onClick(v);
                                                    pwdError(name);
                                                }
                                            }, new DialogUtils.DialogListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    super.onClick(v);
                                                }
                                            });
                                } else {
                                    MyApp.utils.newUser(name, pwd);
                                    ContansUtils.put("name", name);
                                    StringUtils.show1Toast(activity, "登录成功！");
                                    finish();
                                }
                            } else {
                                MyApp.utils.newUser(name, pwd);
                                if (MyApp.utils.userExists(name)) {
                                    ContansUtils.put("name", name);//保存信息
                                    StringUtils.show1Toast(activity, "登录成功！");
                                    finish();
                                } else {
                                    StringUtils.show1Toast(activity, "登录失败！");
                                }
                            }
                        } else StringUtils.show1Toast(getApplicationContext(), "请输入完整信息！");
                    }
                } catch (Exception e){
                    LogUtils.i(TAG + "--Exception", e.toString());
                }
            }
        });
    }

    /**
     * 登录时密码输入错误重复输入相同密码即修改密码并登录
     * @param name
     */
    private void pwdError(final String name) {

        pwdEdt.setText("");
        findViewById(R.id.pwd2_layout).setVisibility(View.VISIBLE);

        login_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = pwdEdt.getText().toString().trim().replace(" ", "");
                String pwd2 = pwdEdt2.getText().toString().trim().replace(" ", "");
                if (pwd.length() > 0 && pwd.equals(pwd2)) {
                    MyApp.utils.newUser(name, pwd);
                    if (MyApp.utils.userExists(name)) {
                        ContansUtils.put("name", name);
                        StringUtils.show1Toast(activity, "密码修改成功！自动登录!");
                        finish();
                    } else {
                        StringUtils.show1Toast(activity, "密码修改失败！");
                    }
                } else StringUtils.show1Toast(getApplicationContext(), "请确认密码一致！");
            }
        });
    }

}
