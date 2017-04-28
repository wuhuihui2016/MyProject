package com.fengyang.music.adapter;

import java.util.List;

import com.fengyang.music.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
* @Title: SettingAdapter   
* @Description: TODO 睡眠时间显示适配器
* @author wuhuihui
* @date 2016年5月18日 下午2:54:12 
*/
public class TimeAdapter extends BaseAdapter {

	List<String> list;
	Context context;
	String info;
	int i;

	public TimeAdapter(List<String> list, Context context, int i) {
		super();
		this.list = list;
		this.context = context;
		this.i = i;
	}
	
	public void setSelected(ListView listView, int position) {
		for (int i = 0; i < listView.getChildCount(); i ++) {
			View view = listView.getChildAt(i);
			ViewHolder holder = (ViewHolder) view.getTag();
			if (position == i) {
				holder.selected.setVisibility(View.VISIBLE);
			} else {
				holder.selected.setVisibility(View.GONE);
			}
		}
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.settime_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.txt = (TextView) convertView.findViewById(R.id.txt);
			viewHolder.selected = (ImageView) convertView.findViewById(R.id.selected);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		info = list.get(position);
		viewHolder.txt.setText(info);
		
		if (i == position) {
			viewHolder.selected.setVisibility(View.VISIBLE);
		} else {
			viewHolder.selected.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	class ViewHolder{
		TextView txt;
		ImageView selected;
	}

}
