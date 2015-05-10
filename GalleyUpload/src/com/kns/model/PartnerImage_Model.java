package com.kns.model;

public class PartnerImage_Model {
	
	private String uploadedid;
	private String imagerealurl;
	private String imagethumburl;
	
	public PartnerImage_Model() {
		
	}

	public PartnerImage_Model(String uploadedid, String imagerealurl, String imagethumburl) {
		
		this.imagerealurl=imagerealurl;
		this.imagethumburl=imagethumburl;
		this.uploadedid=uploadedid;
	}
	
	

	public String getUploadedid() {
		return uploadedid;
	}

	public void setUploadedid(String uploadedid) {
		this.uploadedid = uploadedid;
	}

	public String getImagerealurl() {
		return imagerealurl;
	}

	public void setImagerealurl(String imagerealurl) {
		this.imagerealurl = imagerealurl;
	}

	public String getImagethumburl() {
		return imagethumburl;
	}

	public void setImagethumburl(String imagethumburl) {
		this.imagethumburl = imagethumburl;
	}
	
	
}
