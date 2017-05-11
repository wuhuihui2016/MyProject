package com.fengyang.music.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.fengyang.music.R;
import com.fengyang.music.adapter.SkinAdapter;
import com.fengyang.music.utils.MusicUtils;
import com.fengyang.toollib.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
* @Title: AlterSkinActivity   
* @Description: TODO 更换皮肤
* @author wuhuihui
* @date 2016年6月3日 下午2:01:30 
*/
public class AlterSkinActivity extends BaseActivity {

	private Button defaultSkin;
	private GridView gridView;
	private List<Drawable> list;
	private SkinAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView("更换皮肤", R.layout.activity_skin);


		gridView = (GridView) findViewById(R.id.gridView);
		adapter = new SkinAdapter(getDrawables(), getApplicationContext());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelected(gridView, position);
				MusicUtils.setSkin(position + 1);
				getWindow().setBackgroundDrawableResource(MusicUtils.getDrawableSkin());
			}
		});
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		defaultSkin = (Button) findViewById(R.id.defaultSkin);
		defaultSkin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MusicUtils.setSkin(0);
				if (MusicUtils.isNight) {
					getWindow().setBackgroundDrawableResource(android.R.color.black);
				} else {
					getWindow().setBackgroundDrawableResource(android.R.color.white);
				}
			}
		});
		if (MusicUtils.getSkin() != 0) {
			 adapter.setSelected(gridView, MusicUtils.getSkin() - 1);
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


}
