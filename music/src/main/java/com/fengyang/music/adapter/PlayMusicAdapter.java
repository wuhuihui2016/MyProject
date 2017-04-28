package com.fengyang.music.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengyang.music.R;
import com.fengyang.music.model.Music;
/**
* @Title: PlayMusicAdapter   
* @Description: TODO 播放界面的列表适配器，简要显示
* @author wuhuihui
* @date 2016年5月18日 下午2:53:41 
*/
public class PlayMusicAdapter extends BaseAdapter {

	List<Music> list;
	Context context;
	Music music;

	public PlayMusicAdapter(List<Music> list,Context context) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.play_item_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.song = (TextView) convertView.findViewById(R.id.song);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		music = list.get(position);
		viewHolder.song.setText(music.getTitle() + "---" + music.getArtist());
		return convertView;
	}

	class ViewHolder{
		TextView song;
	}

}
