package com.kns.model;

import com.j256.ormlite.field.DatabaseField;

public class ImageModel {
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String imageurl;
	
	public ImageModel() {
	
	}
	
	public ImageModel(String imageurl) {
		this.imageurl=imageurl;
	}
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}
	
}
