package com.kns.model;

import com.j256.ormlite.field.DatabaseField;

public class Pending_Uploadurl_model {
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String fileurl;
	@DatabaseField
	private String upload_type;
	
	public Pending_Uploadurl_model(){
		
	}

	public Pending_Uploadurl_model(String fileurl, String upload_type){
		this.fileurl=fileurl;
		this.upload_type=upload_type;
		
	}
	
	
	
	public String getUpload_type() {
		return upload_type;
	}

	public void setUpload_type(String upload_type) {
		this.upload_type = upload_type;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFileurl() {
		return fileurl;
	}
	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}
	
	
	
	
}
