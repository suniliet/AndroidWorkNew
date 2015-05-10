package com.kns.model;

import com.j256.ormlite.field.DatabaseField;

public class VideoModel {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String videoeurl;
	
	public VideoModel(){
		
	}
	
	public VideoModel(String videourl){
		this.videoeurl=videourl;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getVideoeurl() {
		return videoeurl;
	}
	public void setVideoeurl(String videoeurl) {
		this.videoeurl = videoeurl;
	}
	
	
}
