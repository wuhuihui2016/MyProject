package com.fengyang.myproject.tool.doc;
/**

 // TODO 1.A跳转B，B回传参数至A
A.class
点击事件：startActivityForResult(new Intent(getApplicationContext(), ChooseCarActivity.class), 101);
值回传：	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 101 && resultCode == 1){
			kind_id = data.getStringExtra("id");
			
		}
	}

B.class(intent放入参数)
Intent intent = new Intent();
intent.putExtra("id", "id");
setResult(1, intent);//第一个参数不能为RESULT_OK=-1，不能是小于0的数
finish();

 // TODO 2.获取手机版本及品牌信息
String handSetInfo=
		"手机型号:" + android.os.android.os.Build.MODEL + 
		",SDK版本:" + android.os.android.os.Build.VERSION.SDK + 
		",系统版本:" + android.os.android.os.Build.VERSION.RELEASE+
		",软件版本:"+getAppVersionName(MainActivity.this) +
		",手机品牌:"+ android.os.android.os.Build.BRAND; 

Build静态类的详细应用：http://www.2cto.com/kf/201503/379741.html
http://www.cnblogs.com/windamy/p/3927013.html

android.os.Build.BOARD // 主板  
android.os.Build.BRAND // android系统定制商  
android.os.Build.CPU_ABI // cpu指令集  
android.os.Build.DEVICE // 设备参数  
android.os.Build.DISPLAY // 显示屏参数  
android.os.Build.FINGERPRINT // 硬件名称  
android.os.Build.HOST  
android.os.Build.ID // 修订版本列表  
android.os.Build.MANUFACTURER // 硬件制造商  
android.os.Build.MODEL // 版本  
android.os.Build.PRODUCT // 手机制造商  
android.os.Build.TAGS // 描述build的标签  
android.os.Build.TIME  
android.os.Build.TYPE // builder类型  
android.os.Build.USER  
android.os.Build.VERSION.CODENAME  // 当前开发代号  
android.os.Build.VERSION.INCREMENTAL  // 源码控制版本号 
android.os.Build.VERSION.RELEASE  // 版本字符串   
android.os.Build.VERSION.SDK  // 版本号
android.os.Build.VERSION.SDK_INT


 // TODO 3.listView.setOnItemClickListener(new OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> parent, View view,int position, long id) {}  });

listView设置OnItemClick和OnItemLongClick事件的冲突解决办法：
设置OnItemClick后设置OnItemLongClick，但是
setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view,  
	                    final int position, long id) {
					
					return true;//必须返回true,否则会继续执行OnItemClick事件
//如果返回false那么click仍然会被调用。而且是先调用Long click，然后调用click。 
//如果返回true那么click就会被吃掉，click就不会再被调用了。
				} });

 // TODO 4.SQLite 数据库操作语句
//创建表
db.execSQL("create table t_user(_id integer primary key,userJid varchar(20),time varchar(100))");
//删除表
db.execSQL("drop table if exists t_user");
//查询表数据
SQLiteDatabase db = getWritableDatabase();
Cursor cursor = db.rawQuery("select * from t_user ",  null);
if(cursor != null) 
	if(cursor.moveToFirst()) user = cursor.getString(cursor.getColumnIndex("userJid"));
cursor.close();	
db.close;
//插入数据
db.execSQL("insert into t_chat(fromJid,toJid,icon,time,message,isRead) values(?,?,?,?,?,?)",new Object[]{from,to,icon,time,message,isRead});
//更新数据
db.execSQL("update t_chat set isRead = 1 where fromJid = ?", new String[]{from});

 // TODO 5.线程
 //停止任务
 if (timer != null && task != null) {
 timer.cancel(); timer = null;
 task.cancel();  task = null;
 }

 //开启服务状态轮询，每5秒获取一次
 timer = new Timer();
 task = new TimerTask() {
@Override
public void run() {
//TO DO SOMETHING
}
};
 timer.schedule(task, 0, 5000);//每5秒执行一次
 耗时操作不能在主线程进行，当主线程有太多任务执行时，需要开启线程执行其他任务，否则尽管是改变控件的状态也会延迟。
 new thread() {
@Override
public void run() {
//TO DO SOMETHING
}
}.start();

 // TODO 6.


 */






