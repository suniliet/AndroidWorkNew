package com.kns.model;

import com.j256.ormlite.field.DatabaseField;

public class RequestedSetModel {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String setid;
	@DatabaseField
	private String checkstatus;
	
	public RequestedSetModel(){
		
	}
	
	public RequestedSetModel(String setid, String checkstatus){
		this.setid=setid;
		this.checkstatus=checkstatus;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public String getCheckstatus() {
		return checkstatus;
	}

	public void setCheckstatus(String checkstatus) {
		this.checkstatus = checkstatus;
	}
	
	
}
