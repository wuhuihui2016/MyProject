package com.fengyang.music.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengyang.music.R;

/**
* @Title: ImageTextButtonView   
* @Description: TODO 闹钟图标加倒计时信息的组合View
* @author wuhuihui
* @date 2016年6月3日 下午3:40:33 
*/
public class ImageTextButtonView extends RelativeLayout {

	private ImageView iv; 
	private TextView  tv; 

	public ImageTextButtonView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.imagetextbutton_view,this,true);
		iv = (ImageView) findViewById(R.id.iv); 
		tv = (TextView) findViewById(R.id.tv);
	}

	public ImageTextButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.imagetextbutton_view,this,true);
		iv = (ImageView) findViewById(R.id.iv); 
		tv = (TextView) findViewById(R.id.tv);
	}

	public void setDefaultImageResource(int resId) { 
		iv.setImageResource(resId); 
	} 

	public void setDefaultTextViewText(String text){
		tv.setText(text); 
	}

	/**
	 * @param resId
	 */
	public void setImageResource(int resId) { 
		iv.setImageResource(resId); 
	} 

	/**
	 * @param text
	 */
	public void setTextViewText(String text) {
		if(! TextUtils.isEmpty(text)){
			tv.setVisibility(View.VISIBLE);
			tv.setText(text); 
		}else{
			tv.setVisibility(View.GONE);
		}
	}

	/**
	 * @param color
	 */
	public void setTextColor(int color) { 
		tv.setTextColor(color); 
	} 

	/**
	 * 隐藏
	 * @param flag
	 */
	public void hideText(boolean flag) {
		if(flag){
			tv.setVisibility(View.GONE);
		}else{
			tv.setVisibility(View.VISIBLE);
		}
	}
}
