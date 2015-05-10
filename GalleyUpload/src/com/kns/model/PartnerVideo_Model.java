package com.kns.model;

public class PartnerVideo_Model {

	private String videorealurl;
	private String updatedvideothumb;
	private String videothumburl0;
	private String videothumburl1;
	private String videothumburl2;
	private String videothumburl3;
	
	public PartnerVideo_Model(){
	
	}

	public PartnerVideo_Model(String updatedvideothumb, String videorealurl, String videothumburl0, String videothumburl1,String videothumburl2, String videothumburl3){
		this.videorealurl=videorealurl;
		this.updatedvideothumb=updatedvideothumb;
		this.videothumburl0=videothumburl0;
		this.videothumburl1=videothumburl1;
		this.videothumburl2=videothumburl2;
		this.videothumburl3=videothumburl3;
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
