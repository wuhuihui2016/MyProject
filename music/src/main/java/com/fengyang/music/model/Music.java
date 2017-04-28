package com.fengyang.music.model;

import java.io.Serializable;

/**
* @Title: Music   
* @Description: TODO 音乐实体类
* @author wuhuihui
* @date 2016年5月6日 下午4:27:17 
*/
public class Music implements Serializable {
	
	/**   
	* @Description: TODO 
	* @author wuhuihui  
	* @date 2016年5月3日 下午3:03:57 
	*/
	private static final long serialVersionUID = 1L;
	private int id;//音乐编号
	private String title;//音乐标题
	private String album;//音乐的专辑名
	private String artist;//音乐的歌手名
	private String url;//音乐文件的路径
	private int duration;//音乐的总播放时长
	private int progress;//记录播放进度
	
	public static int mode_circle = 0, mode_order = 1, mode_random = 2, mode_single = 3;
	
	public Music() {}
	
	public Music(int id, String tilte, String album, String artist, String url,
			int duration) {
		super();
		this.id = id;
		this.title = tilte;
		this.album = album;
		this.artist = artist;
		this.url = url;
		this.duration = duration;
	}

	public Music(int id, String title, String album, String artist, String url,
			int duration, int progress) {
		super();
		this.id = id;
		this.title = title;
		this.album = album;
		this.artist = artist;
		this.url = url;
		this.duration = duration;
		this.progress = progress;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	public String toString() {
		return "Music [id=" + id + ", title=" + title + ", album=" + album
				+ ", artist=" + artist + ", url=" + url + ", duration="
				+ duration + ", progress=" + progress + "]";
	}
	
}
