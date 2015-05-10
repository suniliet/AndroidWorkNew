package com.kns.model;

import java.io.Serializable;

public class MultiSelectVideoModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String uploadedid;
	private String videoname;
	private String videorealurl;
	private String updatedvideothumb;
	private String videothumburl0;
	private String videothumburl1;
	private String videothumburl2;
	private String videothumburl3;
	public boolean isSeleted = false;
	private String AdminApprovedFlag;
	
	public MultiSelectVideoModel(){
	
	}

	public MultiSelectVideoModel(String uploadedid, String videoname, String updatedvideothumb, String videorealurl, String videothumburl0, String videothumburl1,String videothumburl2, String videothumburl3, boolean isselect, String AdminApprovedFlag){
		this.videorealurl=videorealurl;
		this.updatedvideothumb=updatedvideothumb;
		this.videothumburl0=videothumburl0;
		this.videothumburl1=videothumburl1;
		this.videothumburl2=videothumburl2;
		this.videothumburl3=videothumburl3;
		this.isSeleted=isselect;
		this.uploadedid=uploadedid;
		this.AdminApprovedFlag=AdminApprovedFlag;
		this.videoname=videoname;
	}

	
	
	public String getVideoname() {
		return videoname;
	}

	public void setVideoname(String videoname) {
		this.videoname = videoname;
	}

	public String getAdminApprovedFlag() {
		return AdminApprovedFlag;
	}

	public void setAdminApprovedFlag(String adminApprovedFlag) {
		AdminApprovedFlag = adminApprovedFlag;
	}

	public String getUploadedid() {
		return uploadedid;
	}

	public void setUploadedid(String uploadedid) {
		this.uploadedid = uploadedid;
	}

	public boolean isSeleted() {
		return isSeleted;
	}

	public void setSeleted(boolean isSeleted) {
		this.isSeleted = isSeleted;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUpdatedvideothumb() {
		return updatedvideothumb;
	}

	public void setUpdatedvideothumb(String updatedvideothumb) {
		this.updatedvideothumb = updatedvideothumb;
	}

	public String getVideorealurl() {
		return videorealurl;
	}

	public String getVideothumburl0() {
		return videothumburl0;
	}

	public void setVideothumburl0(String videothumburl0) {
		this.videothumburl0 = videothumburl0;
	}

	public String getVideothumburl1() {
		return videothumburl1;
	}

	public void setVideothumburl1(String videothumburl1) {
		this.videothumburl1 = videothumburl1;
	}

	public String getVideothumburl2() {
		return videothumburl2;
	}

	public void setVideothumburl2(String videothumburl2) {
		this.videothumburl2 = videothumburl2;
	}

	public String getVideothumburl3() {
		return videothumburl3;
	}

	public void setVideothumburl3(String videothumburl3) {
		this.videothumburl3 = videothumburl3;
	}

	public void setVideorealurl(String videorealurl) {
		this.videorealurl = videorealurl;
	}
}
