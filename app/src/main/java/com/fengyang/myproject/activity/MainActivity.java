package com.fengyang.myproject.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.fengyang.myproject.R;
import com.fengyang.myproject.fragment.MainFragment;
import com.fengyang.myproject.fragment.MineFragment;
import com.fengyang.myproject.utils.PermissionUtils;
import com.fengyang.toollib.base.BaseActivity;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private Fragment frag_main, frag_mine;//首页,我的
    private int frag_index = 0;//当前加载fragment标志位
    private TextView shouye_title, wode_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shouye_title = (TextView) findViewById(R.id.shouye_title);
        wode_title = (TextView) findViewById(R.id.wode_title);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //检测权限后显示界面
        PermissionUtils.checkSDcardPermission(MainActivity.this, new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isSucess) {
                    selectTab(1);
                } else {
                    PermissionUtils.notPermission(MainActivity.this, PermissionUtils.PERMISSIONS_STORAGE);
                    StringUtils.show1Toast(context, "可能读取SDCard权限未打开，请检查后重试！");
                }
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_page: selectTab(1); break;
            case R.id.mine:      selectTab(2); break;
        }
    }

    /**
     * 首页切换模块
     * @param index
     */
    private void selectTab(int index){
        try {
            manager = getSupportFragmentManager();
            transaction = manager.beginTransaction();
            hideFragment(transaction);
            switch (index) {
                case 1://首页
                    setTitle("懒猫");
                    shouye_title.setTextColor(Color.RED);
                    wode_title.setTextColor(Color.BLACK);
                    if (frag_index == 1) {
                        transaction.remove(frag_main);
                        transaction.commit();
                        frag_main = null;//制空
                        frag_index = 0;//标志复位
                        selectTab(1);//重新加载
                        return;
                    } else {
                        if (frag_main == null) {
                            frag_main = new MainFragment();
                            fragments.add(frag_main);
                        }
                        if(! frag_main.isAdded()){
                            transaction.add(R.id.main_context, frag_main);
                        }
                        transaction.show(frag_main);
                    }
                    break;

                case 2://我的
                    setTitle("我的");
                    shouye_title.setTextColor(Color.BLACK);
                    wode_title.setTextColor(Color.RED);
                    if (frag_index == 2) {
                        transaction.remove(frag_mine);
                        transaction.commit();
                        frag_mine = null;
                        frag_index = 0;
                        selectTab(2);
                        return;
                    } else  {
                        if (frag_mine == null) {
                            frag_mine = new MineFragment();
                            fragments.add(frag_mine);
                        }
                        if(! frag_mine.isAdded()){
                            transaction.add(R.id.main_context, frag_mine);
                        }
                        transaction.show(frag_mine);
                    }

                    break;

            }
            transaction.commit();
            frag_index = index;//当前加载页标志
        } catch (Exception e) {
            LogUtils.i("Exception", e.toString());
        }
    }

    /**
     * 隐藏fragments
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction){
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isAdded() && fragment.isVisible()){
                transaction.hide(fragment);
            }
        }
    }

}
