package com.kns.model;

import java.io.Serializable;

public class MultiSelectPictureModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String uploadedid;
	private String imagerealurl;
	private String imagethumburl;
	private String AdminApprovedFlag;
	public boolean isSeleted = false;
	
	public MultiSelectPictureModel() {
		
	}

	public MultiSelectPictureModel(String uploadedid, String imagerealurl, String imagethumburl, boolean isselect, String AdminApprovedFlag) {
		
		this.imagerealurl=imagerealurl;
		this.imagethumburl=imagethumburl;
		this.isSeleted=isselect;
		this.uploadedid=uploadedid;
		this.AdminApprovedFlag=AdminApprovedFlag;
	}

	
	public String getAdminApprovedFlag() {
		return AdminApprovedFlag;
	}

	public void setAdminApprovedFlag(String adminApprovedFlag) {
		AdminApprovedFlag = adminApprovedFlag;
	}

	public boolean isSeleted() {
		return isSeleted;
	}

	public void setSeleted(boolean isSeleted) {
		this.isSeleted = isSeleted;
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

	public String getUploadedid() {
		return uploadedid;
	}

	public void setUploadedid(String uploadedid) {
		this.uploadedid = uploadedid;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
