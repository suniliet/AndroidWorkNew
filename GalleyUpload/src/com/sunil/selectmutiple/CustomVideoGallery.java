package com.sunil.selectmutiple;

import android.graphics.Bitmap;

public class CustomVideoGallery {

	public String sdcardPathvideo;
	public String sdcardPaththumbvideo;
	public boolean isSeleted = false;
	public Bitmap bitmap;
	public int video_id;
	
	public CustomVideoGallery(){
		
	}
	
	public CustomVideoGallery(String sdcardpath, String sdcardthumb, boolean isselected, Bitmap bitmap, int videoid){
		this.sdcardPathvideo=sdcardpath;
		this.sdcardPaththumbvideo=sdcardthumb;
		this.isSeleted=isselected;
		this.bitmap=bitmap;
		this.video_id=videoid;
	}

	public String getSdcardPathvideo() {
		return sdcardPathvideo;
	}

	public void setSdcardPathvideo(String sdcardPathvideo) {
		this.sdcardPathvideo = sdcardPathvideo;
	}

	public String getSdcardPaththumbvideo() {
		return sdcardPaththumbvideo;
	}

	public void setSdcardPaththumbvideo(String sdcardPaththumbvideo) {
		this.sdcardPaththumbvideo = sdcardPaththumbvideo;
	}

	public boolean isSeleted() {
		return isSeleted;
	}

	public void setSeleted(boolean isSeleted) {
		this.isSeleted = isSeleted;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public int getVideo_id() {
		return video_id;
	}

	public void setVideo_id(int video_id) {
		this.video_id = video_id;
	}
	
	
	
}
