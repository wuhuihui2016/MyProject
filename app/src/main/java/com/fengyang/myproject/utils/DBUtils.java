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

		//用户登录记录表
		db.execSQL("create table t_user(_id integer primary key,name varchar(20),age integer,time varchar(100),jobdesc varchar(100),info varchar(100))");

	}

	@Override
	public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion > oldVersion){
			db.execSQL("drop table if exists t_user");
			onCreate(db);
		}
	}

	//插入用户登录记录表(去重后再存入)
	public synchronized void newUser(String name, String age, String jobdesc, String info){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from t_user where name = ?",new String[]{name});
		if(! cursor.moveToNext()){
			db.execSQL("insert into t_user(name,age,time,jobdesc,message) values(?,?,?,?,?)",
					new Object[]{name, age, jobdesc, StringUtils.formatDate(), info});
		}
		cursor.close();
		db.close();
	}

	//查看所有登录过的用户
	public synchronized List<User> showUsers(){
		List<User> users = new ArrayList<User>();
		SQLiteDatabase db = getReadableDatabase(); 
		Cursor cursor = db.rawQuery("select * from t_user",null);
		while(cursor.moveToNext()){
			User user = new User(cursor.getString(cursor.getColumnIndex("name")), 
					cursor.getString(cursor.getColumnIndex("age")),
					cursor.getString(cursor.getColumnIndex("time")),
					cursor.getString(cursor.getColumnIndex("jobdesc")),
					cursor.getString(cursor.getColumnIndex("info")));
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
