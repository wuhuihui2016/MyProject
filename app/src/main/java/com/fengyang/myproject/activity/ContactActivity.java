package com.fengyang.myproject.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.fengyang.myproject.R;
import com.fengyang.myproject.utils.DialogUtils;
import com.fengyang.myproject.utils.PermissionUtils;
import com.fengyang.toollib.view.AutoListView;
import com.fengyang.toollib.base.BaseActivity;
import com.fengyang.toollib.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限打开后显示手机联系人
 */
public class ContactActivity extends BaseActivity {

    private Button contacts;
    private boolean isClicked = false;//按钮是否已点击标志

    private AutoListView lstv;
    private ArrayAdapter<String> adapter;
    private List<String> list = new ArrayList<String>();
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            List<String> result = (List<String>) msg.obj;
            switch (msg.what) {
                case AutoListView.REFRESH:
                    lstv.onRefreshComplete();
                    list.clear();
                    list.addAll(result);
                    break;
                case AutoListView.LOAD:
                    lstv.onLoadComplete();
                    list.addAll(result);
                    break;
            }
            lstv.setResultSize(result.size());
            adapter.notifyDataSetChanged();
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("联系人", R.layout.activity_contact);
        initView();

    }

    /**
     * 初始化界面
     */
    private void initView() {
        lstv = (AutoListView) findViewById(R.id.auto_listView);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list);
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                if (position <= list.size())
                    DialogUtils.showMsgDialog(ContactActivity.this, list.get(position - 1));
            }
        });
        lstv.setOnRefreshListener(new AutoListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(AutoListView.REFRESH);
            }
        });
        lstv.setOnLoadListener(new AutoListView.OnLoadListener() {
            @Override
            public void onLoad() {
                loadData(AutoListView.LOAD);
            }
        });
    }

    /**
     * 加载数据
     * @param what
     */
    private void loadData(final int what) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = handler.obtainMessage();
                msg.what = what;
                msg.obj = getData();
                handler.sendMessage(msg);
            }
        }).start();
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
                        lstv.setVisibility(View.VISIBLE);
                        loadData(AutoListView.REFRESH);
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
                                        lstv.setVisibility(View.VISIBLE);
                                        loadData(AutoListView.REFRESH);
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

    // 获取通讯录列表
    private List<String> getData() {
        List<String> result = new ArrayList<String>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);

        if (cursor != null) {
            for (cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()) {
                String phoneNum = cursor.getString(0) + ":" //姓名
                        + cursor.getString(1).replace(" ", "").replace("-", "").replace("+86", "");//号码（去除号码中的除数字以外的其他字符）
                result.add(phoneNum);
            }
            cursor.close();
        }

        return result;
    }

}
