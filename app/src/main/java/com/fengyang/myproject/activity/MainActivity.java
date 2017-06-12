package com.fengyang.myproject.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengyang.callback.ICallBack;
import com.fengyang.myproject.R;
import com.fengyang.myproject.fragment.MainFragment;
import com.fengyang.myproject.fragment.MineFragment;
import com.fengyang.myproject.utils.DialogUtils;
import com.fengyang.myproject.utils.PermissionUtils;
import com.fengyang.process.HttpVolleyUtils;
import com.fengyang.process.RequestParams;
import com.fengyang.toollib.base.BaseActivity;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.StringUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private Fragment frag_main, frag_mine;//首页,我的
    private int frag_index = 0;//当前加载fragment标志位
    private TextView shouye_title, wode_title;
    private boolean canShow = false;//tabBar动画显示标志(仅在界面重新激活时为true,动画效果才实现)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testHttp();
    }

    /**
     * 测试网络请求框架
     */
    private void testHttp() {
        String URL = "http://beta.mobi.che-by.com:7050/appversion-getAppVersion";
        RequestParams params = new RequestParams();
        params.addParameter("currentVersion", "4.1");
        params.addParameter("type", "android");

        HttpVolleyUtils httpUtils = new HttpVolleyUtils();
        httpUtils.sendGETRequest(getApplicationContext(), URL, params, new ICallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                DialogUtils.showMsgDialog(MainActivity.this, "测试请求结果" + jsonObject.toString());
            }

            @Override
            public void onFailure() {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        shouye_title = (TextView) findViewById(R.id.shouye_title);
        wode_title = (TextView) findViewById(R.id.wode_title);

        //检测权限后显示界面
        PermissionUtils.checkSDcardPermission(MainActivity.this, new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isSucess) {
                    if (frag_index == 0) selectTab(1);//默认加载首页
                    else selectTab(frag_index);//如果已加载“我的”，则返回时加载“我的”
                } else {
                    PermissionUtils.notPermission(MainActivity.this, PermissionUtils.PERMISSIONS_STORAGE);
                    StringUtils.show1Toast(context, "可能读取SDCard权限未打开，请检查后重试！");
                }
            }
        });

        isShow(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //底部TAB显示动画
        canShow = true;
        isShow(false);

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
    private void selectTab(int index) {
        try {
            manager = getSupportFragmentManager();
            transaction = manager.beginTransaction();
            hideFragment(transaction);
            switch (index) {
                case 1://首页
                    setTitle("懒猫");
                    shouye_title.setTextColor(getResources().getColor(R.color.app_color));
                    wode_title.setTextColor(getResources().getColor(R.color.gray));
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
                    wode_title.setTextColor(getResources().getColor(R.color.app_color));
                    shouye_title.setTextColor(getResources().getColor(R.color.gray));
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

    /**
     * TabBar显示隐藏动画控制
     * @param isShow
     */
    private void isShow (boolean isShow) {
        if (canShow) {
            LinearLayout app_buttom = (LinearLayout) findViewById(R.id.app_buttom);

            Animation animation;
            LayoutAnimationController controller;
            if (isShow)  animation = AnimationUtils.loadAnimation(this, R.anim.tabbar_show);
            else animation = AnimationUtils.loadAnimation(this, R.anim.tabbar_hidden);
            controller = new LayoutAnimationController(animation);

            app_buttom.setLayoutAnimation(controller);// 设置动画
        }

    }

}
