package com.fengyang.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fengyang.music.R;
import com.fengyang.music.service.PlayService;
import com.fengyang.music.utils.MusicUtils;
import com.fengyang.toollib.utils.LogUtils;

/**
 * @Title: SettingActivity  
 * @Description: TODO 设置选项界面
 * @author wuhuihui
 * @date 2016年5月27日 下午3:47:12 
 */
public class SettingActivity extends MusicBaseActivity{

	private RelativeLayout search, alterSkin, sao, isLock, isShaker, isNight, isSleep;
	private CheckBox lockCheck, shakerCheck, nightCheck, sleepCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView("设置", R.layout.activity_setting);

		search = (RelativeLayout) findViewById(R.id.search);
		alterSkin = (RelativeLayout) findViewById(R.id.alterSkin);
		sao = (RelativeLayout) findViewById(R.id.sao);
		isLock = (RelativeLayout) findViewById(R.id.isLock);
		lockCheck = (CheckBox) findViewById(R.id.lockCheck);
		isShaker = (RelativeLayout) findViewById(R.id.isShaker);
		shakerCheck = (CheckBox) findViewById(R.id.shakerCheck);
		isNight = (RelativeLayout) findViewById(R.id.isNight);
		nightCheck = (CheckBox) findViewById(R.id.nightCheck);
		isSleep = (RelativeLayout) findViewById(R.id.isSleep);
		sleepCheck = (CheckBox) findViewById(R.id.sleepCheck);

		//继承自父类的OnClickListener，加上才有点击效果（？？）
		search.setOnClickListener(this);
		alterSkin.setOnClickListener(this);
		sao.setOnClickListener(this);
		isLock.setOnClickListener(this);
		isShaker.setOnClickListener(this);
		isNight.setOnClickListener(this);
		isSleep.setOnClickListener(this);
		Button clearCache = (Button) findViewById(R.id.clearCache);
		clearCache.setOnClickListener(this);
		Button exit = (Button) findViewById(R.id.exit);
		exit.setOnClickListener(this);
	}

	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.search) {//搜索本地音乐
			startActivity(new Intent(getApplicationContext(), SearchActivity.class));

		} else if (v.getId() == R.id.alterSkin) {//更改皮肤
			startActivity(new Intent(getApplicationContext(), AlterSkinActivity.class));
			
		} else if (v.getId() == R.id.sao) {//扫一扫
//			startActivity(new Intent(getApplicationContext(), CaptureActivity.class));

		} else if (v.getId() == R.id.isLock) {//设置锁屏（默认有锁屏）
			if (MusicUtils.getIsLock()) {
				lockCheck.setButtonDrawable(R.drawable.checkbox_off);
				MusicUtils.setIsLock(false);
			} else { 
				lockCheck.setButtonDrawable(R.drawable.checkbox_on);
				MusicUtils.setIsLock(true);
			}
			startActivity(new Intent(getApplicationContext(), LockActivity.class));
			
		} else if (v.getId() == R.id.isShaker) {//摇一摇切歌
			if (MusicUtils.getIsShaked()) {
				MusicUtils.setIsShaker(false);
				shakerCheck.setBottom(R.drawable.checkbox_off);
			} else {
				MusicUtils.setIsShaker(true);
				shakerCheck.setBottom(R.drawable.checkbox_on);
			}
			
		} else if (v.getId() == R.id.isNight) {//设置夜间模式
			if (MusicUtils.isNight) {
				nightCheck.setButtonDrawable(R.drawable.checkbox_off);
				MusicUtils.isNight = false;
				if (MusicUtils.getSkin() == 0) {
					getWindow().setBackgroundDrawableResource(android.R.color.white);
				} else {
					getWindow().setBackgroundDrawableResource(MusicUtils.getDrawableSkin());
				}
			} else { 
				nightCheck.setButtonDrawable(R.drawable.checkbox_on);
				MusicUtils.isNight = true;
				getWindow().setBackgroundDrawableResource(android.R.color.black);
			}

		} else if (v.getId() == R.id.isSleep) {//设置睡眠时间
			startActivity(new Intent(getApplicationContext(), SetTimeActivity.class));
 
		} else if (v.getId() == R.id.clearCache) {
			LogUtils.i(TAG, "清除缓存！");
			MusicUtils.clearCache();
			Toast.makeText(getApplicationContext(), "清除缓存！", Toast.LENGTH_SHORT).show();

		} else if (v.getId() == R.id.exit) {
			//弹出退出APP对话框
			MusicUtils.startService(getApplicationContext(), PlayService.ACTION_TOEXIT);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		//锁屏
		if (MusicUtils.getIsLock()) lockCheck.setButtonDrawable(R.drawable.checkbox_on);
		else lockCheck.setButtonDrawable(R.drawable.checkbox_off);
		
		//摇一摇
		if (MusicUtils.getIsShaked())  shakerCheck.setButtonDrawable(R.drawable.checkbox_on);
		else shakerCheck.setButtonDrawable(R.drawable.checkbox_off);
		
		//夜间模式
		if (MusicUtils.isNight)  nightCheck.setButtonDrawable(R.drawable.checkbox_on);
		else nightCheck.setButtonDrawable(R.drawable.checkbox_off);
		
		//睡眠定时
		if (MusicUtils.setTime && MusicUtils.lastTime != 0)  sleepCheck.setButtonDrawable(R.drawable.checkbox_on);
		else sleepCheck.setButtonDrawable(R.drawable.checkbox_off);
		
	}
}
