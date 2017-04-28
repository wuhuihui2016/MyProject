package com.fengyang.music.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fengyang.music.model.Music;

/**
 * @Title: DBUtils   
 * @Description: TODO 管理音乐列表
 * @author wuhuihui
 * @date 2016年5月31日 下午4:22:26 
 */
public class DBUtils extends SQLiteOpenHelper {

	public DBUtils(Context context){
		super(context, "MusicRadio.db", null, 1);
	}

	@Override
	public synchronized void onCreate(SQLiteDatabase db) {

		//聊天记录表(联系人列表)
		db.execSQL("create table t_like(_id integer primary key,id integer,title varchar(100),album varchar(100),artist varchar(100),url varchar(100),duration varchar(100))");
	}

	@Override
	public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion > oldVersion){
			db.execSQL("drop table if exists t_like");
			onCreate(db);
		}
	}

	/** 
	 * @Title: createList 
	 * @Description: TODO 创建列表
	 * @param name  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月31日 下午4:31:09
	 */
	public synchronized void createList(String name){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("create table t_like(_id integer primary key,id integer, fromJid varchar(20),fromNick varchar(20),fromIcon varchar(100),time varchar(100),message varchar(100),type integer(4))");
		db.close();
	}

	/** 
	 * @Title: addMusic 
	 * @Description: TODO 加入收藏（收藏前判断是否已有）
	 * @param music  
	 * @return void
	 * @author wuhuihui  
	 * @date 2016年5月31日 下午5:02:29
	 */
	public synchronized void addMusic(Music music){
		if(! isLiked(music)){
			SQLiteDatabase db = getWritableDatabase();
			Cursor cursor = null;
			cursor = db.rawQuery("select * from t_like where title = ?",new String[]{music.getTitle()});
			db.execSQL("insert into t_like(id,title,album,artist,url,duration) values(?,?,?,?,?,?)",
					new Object[]{music.getId(),music.getTitle(), music.getAlbum(), music.getArtist(), music.getUrl(), music.getDuration()});
			cursor.close();
			db.close();
		}
	}

	/** 
	 * @Title: isLiked
	 * @Description: TODO 是否已被收藏
	 * @param music
	 * @return  
	 * @return boolean
	 * @author wuhuihui  
	 * @date 2016年5月31日 下午5:02:12
	 */
	public synchronized boolean isLiked(Music music){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = null;
		cursor = db.rawQuery("select * from t_like where title = ?",new String[]{music.getTitle()});
		if( cursor.moveToNext()){
			return true;
		}
		cursor.close();
		db.close();
		return false;
	}

	/** 
	* @Title: delMusic 
	* @Description: TODO 删除单个记录 
	* @param title  
	* @return void
	* @author wuhuihui  
	* @date 2016年5月31日 下午5:04:43
	*/
	public synchronized void delMusic(Music music){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from t_like where title = ?",new Object[]{music.getTitle()});
		db.close();
	}

	/** 
	* @Title: showCollector 
	* @Description: TODO 查看记录
	* @return  
	* @return List<Music>
	* @author wuhuihui  
	* @date 2016年5月31日 下午5:04:52
	*/
	public synchronized List<Music> getLikedList(){
		List<Music> msgList = new ArrayList<Music>();
		SQLiteDatabase db = getReadableDatabase(); 
		Cursor cursor = db.query("t_like", null, null, null, null, null, "id");
		Music music = null;
		while(cursor.moveToNext()){
			music = new Music();
			music.setId(cursor.getInt(cursor.getColumnIndex("id")));
			music.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			music.setAlbum(cursor.getString(cursor.getColumnIndex("album")));
			music.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
			music.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			music.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
			if (new File(music.getUrl()).exists()) msgList.add(music);
			else delMusic(music);
		}
		cursor.close();
		db.close();
		return msgList;
	}

}
