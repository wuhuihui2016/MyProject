package com.fengyang.music.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.fengyang.music.R;
import com.fengyang.music.utils.ContansUtils;

/**
 * @Title: SkinAdapter   
 * @Description: TODO 皮肤显示适配器
 * @author wuhuihui
 * @date 2016年5月20日 下午3:50:16 
 */
public class SkinAdapter extends BaseAdapter {

	List<Drawable> list;
	Context context;
	Drawable drawable;

	public SkinAdapter(List<Drawable> list,Context context) {
		super();
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void setSelected(GridView gridView, int position) {
		for (int i = 0; i < gridView.getChildCount(); i ++) {
			View view = gridView.getChildAt(i);
			ViewHolder holder = (ViewHolder) view.getTag();
			if (position == i) {
				holder.selected.setVisibility(View.VISIBLE);
			} else {
				holder.selected.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.skin_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.skin = (ImageView) convertView.findViewById(R.id.skin);
			viewHolder.selected = (ImageView) convertView.findViewById(R.id.selected);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		drawable = list.get(position);
		viewHolder.skin.setImageDrawable(drawable);
		if (ContansUtils.getSkin() == (position + 1)) {
			viewHolder.selected.setVisibility(View.VISIBLE);
		} else {
			viewHolder.selected.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	class ViewHolder{
		ImageView skin, selected;
	}

}
