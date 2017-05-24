package com.fengyang.myproject.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fengyang.myproject.R;

/**
 * Created by wuhuihui on 2017/5/11.
 * TODO 子功能的主架构
 *   TODO
 */
public class MineFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private View content;//内容布局
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        content = inflater.inflate(R.layout.fragment_mine, container, false);
        return content;

    }

}
