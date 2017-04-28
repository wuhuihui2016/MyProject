package com.fengyang.music.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fengyang.music.R;

/**
 * @Title: Base_SetActivity   
 * @Description: TODO 所有设置界面的Activity的基类，处理共同的操作，如:加载中间布局
 * @author wuhuihui  
 * @date 2016年5月6日 下午4:40:24
 */
public class Base_SetActivity extends BaseActivity implements OnClickListener {

	protected FrameLayout frameLayout;//中间位置的显示其他控件的布局
	protected View contentView;
	protected ImageButton btn_back, btn_setting;

	@SuppressLint("InlinedApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_set);

		btn_setting = (ImageButton) findViewById(R.id.btn_setting);
		btn_setting.setVisibility(View.GONE);
		frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
	}

	/** 
	 * @Title: setTitle 
	 * @Description: TODO 设置title_bar的标题文字
	 * @param activity
	 * @param titleStr  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:40:24
	 */
	public void setTitle(Activity activity, String titleStr){
		tvTitle = (TextView) activity.findViewById(R.id.tv_title);
		tvTitle.setText(titleStr);
	}

	/** 
	 * @Title: setTitle 
	 * @Description: TODO 设置title_bar的标题文字
	 * @param activity
	 * @param titleResourceId  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:40:24
	 */
	public void setTitle(Activity activity, int titleResourceId){
		setTitle(activity, getString(titleResourceId));
	}

	/** 
	 * @Title: setView 
	 * @Description: TODO 设置中间布局控件
	 * @param v  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月6日 下午4:40:24
	 */
	public void setView(View v) {
		frameLayout.removeAllViews();
		frameLayout.addView(v);
		v.setOnTouchListener(this);//向右滑动手势
	}

	public void onClick(View v) {
		if(v.getId() == R.id.btn_back){
			finish();
		}
	}
	
}
