package com.kns.model;

import com.j256.ormlite.field.DatabaseField;

public class PnedingUpload_model {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String upload_type;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Pending_Uploadurl_model model;
	
	public PnedingUpload_model(){
		
	}

	public PnedingUpload_model(String upload_type, Pending_Uploadurl_model model){
		this.upload_type=upload_type;
		this.model=model;
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUpload_type() {
		return upload_type;
	}
	public void setUpload_type(String upload_type) {
		this.upload_type = upload_type;
	}
	public Pending_Uploadurl_model getModel() {
		return model;
	}
	public void setModel(Pending_Uploadurl_model model) {
		this.model = model;
	}

	
}
