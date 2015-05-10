package com.kns.model;

import com.j256.ormlite.field.DatabaseField;

public class CategoryModel {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String cat_id;
	@DatabaseField
	private String cat_name;
	@DatabaseField
	private boolean isselected;
	
	public CategoryModel(){
		
	}

	public CategoryModel(String cat_id, String cat_name,boolean isselected){
		this.cat_id=cat_id;
		this.cat_name=cat_name;
		this.isselected=isselected;
	}

	public String getCat_id() {
		return cat_id;
	}

	public void setCat_id(String cat_id) {
		this.cat_id = cat_id;
	}

	public String getCat_name() {
		return cat_name;
	}

	public void setCat_name(String cat_name) {
		this.cat_name = cat_name;
	}

	public boolean isIsselected() {
		return isselected;
	}

	public void setIsselected(boolean isselected) {
		this.isselected = isselected;
	}
	

}
