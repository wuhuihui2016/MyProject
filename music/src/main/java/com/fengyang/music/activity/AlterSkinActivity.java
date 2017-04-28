package com.fengyang.music.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.fengyang.music.R;
import com.fengyang.music.adapter.SkinAdapter;
import com.fengyang.music.utils.ContansUtils;

/**
* @Title: AlterSkinActivity   
* @Description: TODO 更换皮肤
* @author wuhuihui
* @date 2016年6月3日 下午2:01:30 
*/
public class AlterSkinActivity extends Base_SetActivity{

	private Button defaultSkin;
	private GridView gridView;
	private List<Drawable> list;
	private SkinAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(this, "更换皮肤");
		contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_skin, null);
		setView(contentView);

		//右滑监听
		RelativeLayout layout = (RelativeLayout) contentView.findViewById(R.id.layout);
		layout.setOnTouchListener(this);

		gridView = (GridView) contentView.findViewById(R.id.gridView);
		adapter = new SkinAdapter(getDrawables(), getApplicationContext());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelected(gridView, position);
				ContansUtils.setSkin(getApplicationContext(), position + 1);
				getWindow().setBackgroundDrawableResource(ContansUtils.getDrawableSkin());
			}
		});
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		defaultSkin = (Button) findViewById(R.id.defaultSkin);
		defaultSkin.setOnClickListener(this);
		if (ContansUtils.getSkin() != 0) {
			 adapter.setSelected(gridView, ContansUtils.getSkin() - 1);
		}
	}

	/** 
	 * @Title: getDrawables 
	 * @Description: TODO 皮肤资源
	 * @return  
	 * @return List<Drawable>
	 * @author wuhuihui  
	 * @date 2016年5月20日 下午5:33:04
	 */
	private List<Drawable> getDrawables() {
		list = new ArrayList<Drawable>();
		list.add(getResources().getDrawable(R.drawable.skin1));
		list.add(getResources().getDrawable(R.drawable.skin2));
		list.add(getResources().getDrawable(R.drawable.skin3));
		list.add(getResources().getDrawable(R.drawable.skin4));
		list.add(getResources().getDrawable(R.drawable.skin5));
		list.add(getResources().getDrawable(R.drawable.skin6));
		list.add(getResources().getDrawable(R.drawable.skin7));
		list.add(getResources().getDrawable(R.drawable.skin8));
		list.add(getResources().getDrawable(R.drawable.skin9));
		return list;
	}

	@SuppressLint("NewApi") @Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.defaultSkin) {
			ContansUtils.setSkin(getApplicationContext(), 0);
			if (ContansUtils.isNight) {
				getWindow().setBackgroundDrawableResource(android.R.color.black);
			} else {
				getWindow().setBackgroundDrawableResource(android.R.color.white);
			}
		}
	}

}
