package com.fengyang.myproject.fragment;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.fengyang.myproject.R;
import com.fengyang.myproject.activity.SettingActivity;
import com.fengyang.myproject.utils.PermissionUtils;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.StringUtils;
import com.fengyang.toollib.view.IOSScrollView;

/**
 * Created by wuhuihui on 2017/5/11.
 * TODO 子功能的主架构
 */
public class MineFragment extends Fragment {

    private static final String TAG = "MineFragment";
    private View content;//内容布局
    private boolean isOPent, isPermission;//手电筒打开标识
    private Camera camera;

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

        openLight();
    }

    private void openLight() {
        //手电筒
        final Button light = (Button) content.findViewById(R.id.light);
        PermissionUtils.checkCameraPermission(new PermissionUtils.OnCheckCallback() {
            @Override
            public void onCheck(boolean isSucess) {
                if (isPermission) {
                    if (isSucess) {
                        light.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                open();
                            }
                        });
                    } else {
                        StringUtils.show1Toast(getActivity(), "可能读取相机权限未打开，请检查后重试！");
                        isOPent = false;
                    }
                } else {
                    light.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (! isOPent) {
                                PermissionUtils.checkCameraPermission( new PermissionUtils.OnCheckCallback() {
                                    @Override
                                    public void onCheck(final boolean isSucess) {
                                        if (isSucess) {
                                            isPermission = false;
                                            open();
                                        } else {
                                            //权限获取失败后再次弹出系统框，将按钮的点击跳转标志设为true,保证用户点击“允许”后可直接跳转指定界面
                                            isPermission = true;
                                            PermissionUtils.notPermission(getActivity(), PermissionUtils.PERMISSIONS_CAMERA);
                                        }
                                    }
                                });
                            } else {
                                close();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 打开手电筒
     */
    private void open(){
        LogUtils.i(TAG, "openLight");
        isOPent = true;
        camera = PermissionUtils.camera;
        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview(); // 开始亮灯
    }

    /**
     * 关闭手电筒
     */
    private void close(){
        if (isOPent) {
            LogUtils.i(TAG, "closeLight");
            camera.stopPreview(); // 关掉亮灯
            camera.release(); // 关掉照相机
            isOPent = false;
        }
    }

}
