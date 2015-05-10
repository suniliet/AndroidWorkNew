package com.kns.receiver;

import java.sql.SQLException;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.kns.db.DBHelper;
import com.kns.model.Pending_Uploadurl_model;
import com.kns.service.UploadFileServicePending;
import com.kns.service.UploadVideoServicePending;
import com.kns.util.ImageUtil;

public class UpdateReceiver extends BroadcastReceiver{

	private static final String TAG="UpdateReceiver";
	private DBHelper db;
	@Override
	public void onReceive(Context context, Intent arg1) {
		
		db=new DBHelper(context);
		if(arg1.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			
			 boolean isinternet=ImageUtil.isInternetOn(context);
			  if (isinternet) {
				
				  ImageUtil.galleryLog(TAG, "Internet Connected");
				  
				  try {
					
					List<Pending_Uploadurl_model> list=db.getPendinguploadType("image");
					ImageUtil.galleryLog(TAG, "list image size is "+list.size());
					if (list.size() > 0) {
						
						
						if (list.size() <= 20 ) {
							
							String[] stringArray=new String[list.size()];
							for (int i = 0; i < list.size(); i++) {
								
								Pending_Uploadurl_model model=list.get(i);
								stringArray[i] = model.getFileurl();
							}
							 //stringArray = list.toArray(new String[list.size()]);
							 	Bundle b=new Bundle();
								b.putStringArray("stringarray", stringArray);
								b.putStringArray("selectedfilename", stringArray);
					    		Intent intent = new Intent(context, UploadFileServicePending.class);
					    		intent.putExtras(b);
					    		context.startService(intent);
						}else{
							
							String[] stringArray = new String[list.size()];
							for (int i = 0; i < list.size(); i++) {
								
								Pending_Uploadurl_model model=list.get(i);
								stringArray[i] = model.getFileurl();
							}
							String[] stringArraynew = new String[20];
							for (int i = 0; i < 20; i++) {
								stringArraynew[i]=stringArray[i]; 
							}
							
							Bundle b=new Bundle();
							b.putStringArray("stringarray", stringArraynew);
							b.putStringArray("selectedfilename", stringArraynew);
				    		Intent intent = new Intent(context, UploadFileServicePending.class);
				    		intent.putExtras(b);
				    		context.startService(intent);
							
							
						}
						
						
					}
					
							List<Pending_Uploadurl_model> listvideo=db.getPendinguploadType("video");
							ImageUtil.galleryLog(TAG, "listvideo size is "+listvideo.size());
							if (listvideo.size() > 0) {
								
								
								if (listvideo.size() == 1 ) {
									
										String[] stringArray=new String[listvideo.size()];
										for (int i = 0; i < listvideo.size(); i++) {
											
											Pending_Uploadurl_model model=listvideo.get(i);
											stringArray[i] = model.getFileurl();
										}
									    //stringArray = listvideo.toArray(stringArray);
									 	Bundle b=new Bundle();
										b.putStringArray("stringarray", stringArray);
										b.putStringArray("selectedfilename", stringArray);
							    		Intent intent = new Intent(context, UploadVideoServicePending.class);
							    		intent.putExtras(b);
							    		context.startService(intent);
								}else{
									String[] stringArray=new String[listvideo.size()];
									for (int i = 0; i < listvideo.size(); i++) {
										
										Pending_Uploadurl_model model=listvideo.get(i);
										stringArray[i] = model.getFileurl();
									}
									String[] stringArraynew = new String[1];
									for (int i = 0; i < 1; i++) {
										stringArraynew[i]=stringArray[i]; 
									}
									
									Bundle b=new Bundle();
									b.putStringArray("stringarray", stringArraynew);
									b.putStringArray("selectedfilename", stringArraynew);
						    		Intent intent = new Intent(context, UploadVideoServicePending.class);
						    		intent.putExtras(b);
						    		context.startService(intent);
									
						    		
				
								}
								
								
							}
					
					
				} catch (SQLException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
			 }
			  else{
				  ImageUtil.galleryLog(TAG, "Itnernet not present");
			  }
	    	}
		else{
			 ImageUtil.galleryLog(TAG, "No action");
		  }
		 
		}
}
