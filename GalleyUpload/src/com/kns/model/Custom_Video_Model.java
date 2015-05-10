package com.kns.model;

public class Custom_Video_Model {

	private String videorealurl;
	private String updatedvideothumb;
	public boolean isSeleted = false;
	
	public Custom_Video_Model(){
		
	}
	
	public Custom_Video_Model(String videorealurl, String updatedvideothumb, boolean isSeleted){
		this.videorealurl=videorealurl;
		this.updatedvideothumb=updatedvideothumb;
		this.isSeleted=isSeleted;
	}

	public String getVideorealurl() {
		return videorealurl;
	}

	public void setVideorealurl(String videorealurl) {
		this.videorealurl = videorealurl;
	}

	public String getUpdatedvideothumb() {
		return updatedvideothumb;
	}

	public void setUpdatedvideothumb(String updatedvideothumb) {
		this.updatedvideothumb = updatedvideothumb;
	}

	public boolean isSeleted() {
		return isSeleted;
	}

	public void setSeleted(boolean isSeleted) {
		this.isSeleted = isSeleted;
	}
	
	

}
