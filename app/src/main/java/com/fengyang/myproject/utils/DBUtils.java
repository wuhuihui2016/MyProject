package com.fengyang.myproject.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fengyang.myproject.model.User;
import com.fengyang.toollib.utils.LogUtils;
import com.fengyang.toollib.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengyangtech
 * 数据库工具类
 * 2017年4月28日
 */

public class DBUtils extends SQLiteOpenHelper {
	
	private static String TAG = "DBUtils";

	public DBUtils(Context context){
		super(context, "user.db", null, 1);
		LogUtils.i(TAG, "保存用户-->" + "user.db");
	}

	@Override
	public synchronized void onCreate(SQLiteDatabase db) {

		//用户登录记录表(昵称，密码，登录时间）
		db.execSQL("create table t_user(_id integer primary key,name varchar(20),pwd varchar(20),time varchar(20))");

	}

	@Override
	public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion > oldVersion){
			db.execSQL("drop table if exists t_user");
			onCreate(db);
		}
	}

	//插入/更新用户登录记录表
	public synchronized void newUser(String name, String pwd){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from t_user where name = ?",new String[]{name});
		if(cursor.moveToNext()){//如果数据库中已有同名用户，则更新信息
			db.execSQL("update t_user set pwd=? where name=?", new Object[]{pwd, name});
		} else {//否则插入新用户
			db.execSQL("insert into t_user(name,pwd,time) values(?,?,?)",
					new Object[]{name, pwd, StringUtils.formatDate()});
		}
		cursor.close();
		db.close();
	}

	/**
	 * 查找某用户是否存在
	 * @param name
	 * @return
     */
	public synchronized boolean userExists(String name) {
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from t_user where name = ?",new String[]{name});
		if(cursor.moveToNext()) return true;
		return false;
	}

	/**
	 * 查找某用户
	 * @param name
	 * @return
     */
	public synchronized User getUser(String name) {
		User user = null;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from t_user where name = ?",new String[]{name});
		if(cursor.moveToNext())
			user = new User(cursor.getString(cursor.getColumnIndex("name")),
				cursor.getString(cursor.getColumnIndex("pwd")),
				cursor.getString(cursor.getColumnIndex("time")));
		return user;
	}

	//查看所有登录过的用户
	public synchronized List<User> showUsers(){
		List<User> users = new ArrayList<User>();
		SQLiteDatabase db = getReadableDatabase(); 
		Cursor cursor = db.rawQuery("select * from t_user",null);
		while(cursor.moveToNext()){
			User user = new User(cursor.getString(cursor.getColumnIndex("name")), 
					cursor.getString(cursor.getColumnIndex("pwd")),
					cursor.getString(cursor.getColumnIndex("time")));
			users.add(user);
		}
		cursor.close();
		db.close();
		return users;
	}

	//删除单个用户登录记录
	public synchronized void delUser(String name){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from t_user where name = ?",new Object[]{name});
		db.close();
	}

	//清除聊天记录(单聊)
	public synchronized void clearUsers(){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from t_user");
		db.close();
	}

}
