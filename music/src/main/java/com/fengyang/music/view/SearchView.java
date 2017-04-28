package com.fengyang.music.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.fengyang.music.R;

/**
* @Title: SearchView   
* @Description: TODO 自定义搜索框
* @author wuhuihui
* @date 2016年6月3日 下午3:40:59 
*/
public class SearchView extends RelativeLayout {

	private EditText edit; 
	private ImageButton clear, img; 

	public SearchView(Context context) {
		super(context);
	}

	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.search_view, this, true);
		edit = (EditText) findViewById(R.id.edit); 
		clear = (ImageButton) findViewById(R.id.clear);
		img = (ImageButton) findViewById(R.id.img);
	}

	//设置输入hint
	public void setHint(String hint) { 
		edit.setHint(hint);
	} 
	//设置输入字体大小
	public void setEditTextSize(float size) { 
		edit.setTextSize(size);
	} 
	//设置输入字体颜色
	public void setEditTextColor(int color) { 
		edit.setTextColor(color);
	} 
	//设置输入内容
	public void setText(String text) {
		edit.setText(text);
		edit.setSelection(text.length());//设置光标位置
	} 
	//获取输入内容
	public String getText() {
		return edit.getText().toString();
	} 

	public void setOnFinishListener(final OnClickListener listener,
			final OnClickListener clearListener) {
		edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() != 0){
					clear.setVisibility(View.VISIBLE);
					clear.setOnClickListener(clearListener);
					if(s.length() > 0){
						img.setVisibility(View.VISIBLE);
						img.setOnClickListener(listener);
					}
				}else{
					clear.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {}
		});

	}



}

