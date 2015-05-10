package com.kns.model;

public class Custom_Image_Model {

	private String imagerealurl;
	private String imagethumburl;
	public boolean isSeleted = false;
	
	public Custom_Image_Model(){
		
	}
	
	public Custom_Image_Model(String imagerealurl, String imagethumburl, boolean  isSeleted){
		this.imagerealurl=imagerealurl;
		this.imagethumburl=imagethumburl;
		this.isSeleted=isSeleted;
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

	public boolean isSeleted() {
		return isSeleted;
	}

	public void setSeleted(boolean isSeleted) {
		this.isSeleted = isSeleted;
	}
	
	
}
