package com.fengyang.myproject.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fengyang.myproject.R;
import com.fengyang.myproject.activity.LoginActivity;
import com.fengyang.myproject.activity.SettingActivity;
import com.fengyang.toollib.utils.ContansUtils;
import com.fengyang.toollib.view.IOSScrollView;

/**
 * Created by wuhuihui on 2017/5/11.
 * TODO 子功能的主架构
 */
public class MineFragment extends Fragment {

    private static final String TAG = "MineFragment";
    private View content;//内容布局

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        content = inflater.inflate(R.layout.fragment_mine, container, false);

        initView();

        return content;

    }

    /**
     * 初始化控件
     */
    private void initView() {

        //当页面滑动到中间位置，导致看不到顶部时显示到达顶部的按钮
        final IOSScrollView scrollView = (IOSScrollView) content.findViewById(R.id.scrollView);
        final ImageButton top_btn = (ImageButton) content.findViewById(R.id.top_btn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > 500) {
                        top_btn.setVisibility(View.VISIBLE);
                        top_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scrollView.fullScroll(ScrollView.FOCUS_UP);
                            }
                        });
                    } else {
                        top_btn.setVisibility(View.GONE);
                    }
                }
            });
        }

        //设置界面
        content.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        getLoginInfo();
    }

    /**
     * 用户登录/用户信息
     */
    private void getLoginInfo() {
        ImageView user_icon = (ImageView) content.findViewById(R.id.user_icon);
        TextView user_name = (TextView) content.findViewById(R.id.user_name);
        String name = (String) ContansUtils.get("name", "");
        if (! TextUtils.isEmpty(name)) {//登录状态
            user_icon.setImageResource(R.mipmap.app_icon);
            user_name.setText(name);
        } else {
            user_icon.setImageResource(R.drawable.user);
            user_name.setText("请登录");
        }

        content.findViewById(R.id.login_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }

}
