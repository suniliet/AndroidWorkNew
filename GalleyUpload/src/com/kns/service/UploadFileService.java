package com.kns.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.kns.adapter.GalleryAdapter;
import com.kns.db.DBHelper;
import com.kns.model.FileDirectories;
import com.kns.model.ImageModel;
import com.kns.util.CustomMultiPartEntity;
import com.kns.util.CustomMultiPartEntity.ProgressListener;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;
import com.sunil.selectmutiple.ImageDiectoriesActivity;
import com.sunil.selectmutiple.R;


public class UploadFileService extends IntentService{

	private static final String TAG="UploadFileService";
	private DBHelper db=null;
	private ProgressDialog prodialog1;
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	private int totalsize=0;
	//private ImageLoader imageLoader;
	private GalleryAdapter adapter;
	private long totalsizeimage=0;
	public static final int UPDATE_PROGRESS = 200;
	ResultReceiver receiver = null;
	String allpath[];
	String selectedfilename[];
	NotificationManager notificationManager;
	private NotificationManager mNotifyManager;
	private Builder mBuilder;
	private SharedPreferences preferenceManager;
	String Upload_ID = "Upload_ID";
	int Notofication_Image_ID = 0;
	
	
	//private PowerManager.WakeLock mWakeLock; 
	//private WifiManager.WifiLock  wifiLock;
	int currentId;
	
	public UploadFileService() {
		super("name");
		Log.v(TAG, "UploadVideoService called");
		//Log.v(TAG, "AutoUploadServiceNotes called");
	   // ePodUtil.epodLog(context, TAG,"AutoUploadServiceNotes called");
	}
	public UploadFileService(String name) {
		super(name);
		
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		db=new DBHelper(getApplicationContext());
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	
		ImageUtil.galleryLog(TAG, "UploadFileService onCreate called");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		
		return null; 
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onHandleIntent(Intent intent) {
		
		ImageUtil.galleryLog(TAG, "onHandleIntent onStartCommand called");
		
		// preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
		
		receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
		allpath=intent.getExtras().getStringArray("stringarray");
		selectedfilename=intent.getExtras().getStringArray("selectedfilename");
		
		Log.v(TAG, "selected files: "+allpath.length);
		Log.v(TAG, "selected list files name: "+selectedfilename.length);
		
		totalsize=0;
		
		for (int i = 0; i < allpath.length; i++) {
			File filenew = new File(selectedfilename[i]);
			totalsize += Integer.parseInt(String.valueOf(filenew.length()/1024));
		}
		

		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(getApplicationContext());
		mBuilder.setContentTitle("Upload")
				.setContentText("upload in progress")
				.setSmallIcon(R.drawable.ic_upload_icon);
		
		boolean isinternet=ImageUtil.isInternetOn(getApplicationContext());
    	if (isinternet) {
    		
    		doUpload();
    	}
    	else{
    		/*NotificationManager notificationManager = (NotificationManager)getApplicationContext()
		            .getSystemService(Context.NOTIFICATION_SERVICE);*/
		    @SuppressWarnings("deprecation")
			Notification notification = new Notification(android.R.drawable.
				      stat_notify_more, "Internet Error!", System.currentTimeMillis());

		    Intent notificationIntent = new Intent(getApplicationContext(), FileDirectories.class);

		    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		    PendingIntent intent1 = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

		    notification.setLatestEventInfo(getApplicationContext(), "", "Internet Error!", intent1);
		    notification.flags |= Notification.FLAG_AUTO_CANCEL;
		    notificationManager.notify(0, notification);
		    
		    mBuilder.setProgress(0, 0, false);
			mNotifyManager.notify(Notofication_Image_ID, mBuilder.build());
			
			stopSelf();
    	}
	}
	
/*	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		currentId=startId;
		ImageUtil.galleryLog(TAG, "UploadFileService onStartCommand called");
		
		 preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
		
		receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
		allpath=intent.getExtras().getStringArray("stringarray");
		selectedfilename=intent.getExtras().getStringArray("selectedfilename");
		
		Log.v(TAG, "selected files: "+allpath.length);
		Log.v(TAG, "selected list files name: "+selectedfilename.length);
		
		totalsize=0;
		
		for (int i = 0; i < allpath.length; i++) {
			File filenew = new File(selectedfilename[i]);
			totalsize += Integer.parseInt(String.valueOf(filenew.length()/1024));
		}
		
		  Runnable r = new Runnable() {
	            public void run() {

	                   
	                    synchronized (this) {
	                        try {

	                            //uploadFile(sharedPref.getString(i + "", ""));
	                        	
	                        	doUpload();

	                        } catch (Exception e) {
	                       }

	                    }
	                    // }
	                    Log.i(TAG, "Service running " + currentId);
	             
	                stopSelf();
	            }
	        };

	        Thread t = new Thread(r);
	        t.start();
	       return Service.START_NOT_STICKY;
		
		
        
		boolean isinternet=ImageUtil.isInternetOn(getApplicationContext());
    	if (isinternet) {
    		
    		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
    		    new UploadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    		} else {
    		    new UploadingTask().execute();
    		}
    		
    		 Editor PrefEdit = preferenceManager.edit();
			    PrefEdit.putInt(Upload_ID, currentId);
			    PrefEdit.commit();
    		
    		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mBuilder = new NotificationCompat.Builder(getApplicationContext());
			mBuilder.setContentTitle("Upload")
					.setContentText("upload in progress")
					.setSmallIcon(R.drawable.ic_upload_icon);
    	}
		
	     return Service.START_NOT_STICKY;
	}	*/
	
/*	 private class UploadingTask extends AsyncTask<String, Integer, String> {
			
			@Override
			protected String doInBackground(String... urls) {
				String response1 = "";
		    	//http://23.21.71.132/KNSGallery/web_service.php?act=getmemID&ws=1&devID=123&andID=234
				
				Prefs = getApplicationContext().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				ImageUtil.galleryLog(TAG, "memberid: "+memberid);
				
				try {
					
					   String url=ImageConstant.BASEURL+"image_fileupload.php";
					   Log.v(TAG, "url is: "+url);
					   HttpClient client = new DefaultHttpClient();
					    HttpPost post = new HttpPost(url);
					    MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
					    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
					   // Log.v(TAG, "SeekBar values is: "+seekbarprogress);
					    
					    for (int i = 1; i <= allpath.length; i++) { 
					    	
					    	String filename=selectedfilename[i-1];
					    	Log.v(TAG, "file name: "+filename);
					    	
					    	File file = new File(filename);
						    FileBody fb = new FileBody(file);
						    builder.addPart("file"+i, fb);
					    }
					    
					  
					    
					    builder.addTextBody("member_id", memberid);
					    builder.addTextBody("count", String.valueOf(allpath.length));
					    
					    final HttpEntity yourEntity = builder.build();
				
					    CustomMultiPartEntity entity=new CustomMultiPartEntity(yourEntity, new ProgressListener() {
							
							@Override
							public void transferred(long num) {
							
								publishProgress((int) ((num / (float) totalsizeimage) * 100));
								//Log.v(TAG, "publish progress :"+totalsizeimage);
								// ImageUtil.galleryLog(TAG, "publish progress :"+totalsizeimage);
								
								
							}

						
						});
					    totalsizeimage = entity.getContentLength();
					   // Log.v(TAG, "total size is: "+totalsize);
					    ImageUtil.galleryLog(TAG, "total size is: "+totalsize);
					    post.setEntity(entity);
					    HttpResponse response = client.execute(post);  
					    int code=response.getStatusLine().getStatusCode();
					    Log.v(TAG, "response code is: "+ code);
					    response1= getContent(response);
					
					// response=postFile(dataT, memberid);
					 
					Log.v(TAG, "response is: "+ response1);
					
					}catch (UnknownHostException e) {
						e.printStackTrace();
						ImageUtil.galleryLog(TAG, "excep: "+e);
						 Notification notification = new Notification(android.R.drawable.
							      stat_notify_more, "Upload Failed!", System.currentTimeMillis());

					    Intent notificationIntent = new Intent(getApplicationContext(), ImageDiectoriesActivity.class);

					    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

					    PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

					    notification.setLatestEventInfo(getApplicationContext(), "Gallery Upload", "Upload Failed! Internet Connection Error.", intent);
					    notification.flags |= Notification.FLAG_AUTO_CANCEL;
					    notificationManager.notify(currentId, notification);
					
					    stopSelf(currentId);
					    
					} catch (Exception e) {
						e.printStackTrace();
					}
			 return response1;
		}
			
			@Override
			protected void onProgressUpdate(Integer... progress) {
				//prodialog1.setProgress((progress[0]));
				//Log.v(TAG, "onprogressupdate :"+progress[0]);
				 Bundle resultData = new Bundle();
				 int update=progress[0];
	             resultData.putInt("progress" ,update);
	             if (update < 100) {
	            	  receiver.send(UPDATE_PROGRESS, resultData);
	            	  
	            	  mBuilder.setProgress(100, update, false);
	      			  mNotifyManager.notify(currentId, mBuilder.build());
				}
	             
	             
				 ImageUtil.galleryLog(TAG,"onprogressupdate :"+progress[0]);
			}
			
			
			@SuppressWarnings("deprecation")
			@Override
			protected void onPostExecute(String resultString) {
				
				//prodialog1.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				//mWakeLock.release();
				//wifiLock.release();
				ImageUtil.galleryLog(TAG,"Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					Bundle resultData = new Bundle();
					resultData.putInt("progress" ,100);
					receiver.send(UPDATE_PROGRESS, resultData);
					
					mBuilder.setProgress(0, 0, false);
					mNotifyManager.notify(currentId, mBuilder.build());
					
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String resp=jsonobj.getString("Success");
						
						if (resp.equalsIgnoreCase("Successfully Uploaded the Image File")) {
							Toast.makeText(getApplicationContext(), "Upload Complete!", Toast.LENGTH_LONG).show();
							
							for (int i = 0; i < allpath.length; i++) {
								
								String url=selectedfilename[i];
								ImageModel model=new ImageModel(url);
								db.addImage(model);
								
							}
							
							 NotificationManager notificationManager = (NotificationManager)getApplicationContext()
							            .getSystemService(Context.NOTIFICATION_SERVICE);
							    Notification notification = new Notification(android.R.drawable.
									      stat_notify_more, "Upload Complete!", System.currentTimeMillis());

							    Intent notificationIntent = new Intent(getApplicationContext(), ImageDiectoriesActivity.class);

							    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

							    PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

							    notification.setLatestEventInfo(getApplicationContext(), "Gallery Upload", "Upload Complete!", intent);
							    notification.flags |= Notification.FLAG_AUTO_CANCEL;
							    notificationManager.notify(currentId, notification);
							
							    stopSelf(currentId);
						}
						else{
							Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
						}
						
						//ImageUtil.showAlert(GridShowImageActivity.this, resp);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
			    	catch (Exception e) {
					e.printStackTrace();
				}
					
				}
				else{
					Toast.makeText(getApplicationContext(), "Unable to connect to server, Please try again.", Toast.LENGTH_LONG).show();
				}

			}
		}*/

	 
	   public  String getContent(HttpResponse response) throws IOException {
		    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    String body = "";
		    String content = "";

		    while ((body = rd.readLine()) != null) 
		    {
		        content += body + "\n";
		    }
		    return content.trim();
		}
	

	  @SuppressWarnings("deprecation")
	public void doUpload(){
		   
		   ImageUtil.galleryLog(TAG, "doupload called");
		   String response1="";;
		   Prefs = getApplicationContext().getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
			ImageUtil.galleryLog(TAG, "memberid: "+memberid);
			
			try {
				
				   String url=ImageConstant.BASEURL+"image_fileupload.php";
				   Log.v(TAG, "url is: "+url);
				   HttpClient client = new DefaultHttpClient();
				    HttpPost post = new HttpPost(url);
				    MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
				    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				   // Log.v(TAG, "SeekBar values is: "+seekbarprogress);
				    
				    for (int i = 1; i <= allpath.length; i++) { 
				    	
				    	String filename=selectedfilename[i-1];
				    	Log.v(TAG, "file name: "+filename);
				    	
				    	File file = new File(filename);
					    FileBody fb = new FileBody(file);
					    builder.addPart("file"+i, fb);
				    }
				    
				  
				    
				    builder.addTextBody("member_id", memberid);
				    builder.addTextBody("count", String.valueOf(allpath.length));
				    
				    final HttpEntity yourEntity = builder.build();
			
				    CustomMultiPartEntity entity=new CustomMultiPartEntity(yourEntity, new ProgressListener() {
						
						@Override
						public void transferred(long num) {
						
							//publishProgress((int) ((num / (float) totalsizeimage) * 100));
							Bundle resultData = new Bundle();
							 int update=(int) ((num / (float) totalsizeimage) * 100);
							 ImageUtil.galleryLog(TAG, "Progress Update: "+update);
				             resultData.putInt("progress" ,update);
				             
				             receiver.send(UPDATE_PROGRESS, resultData);
			            	  mBuilder.setProgress(100, update, false);
			     			  mNotifyManager.notify(Notofication_Image_ID, mBuilder.build());
				             
				             
				             /*if (update < 100) {
				            	  receiver.send(UPDATE_PROGRESS, resultData);

				            	  mBuilder.setProgress(100, update, false);
				     			  mNotifyManager.notify(Notofication_Image_ID, mBuilder.build());
							}*/
						
						}

					
					});
				    totalsizeimage = entity.getContentLength();
				   // Log.v(TAG, "total size is: "+totalsize);
				   // ImageUtil.galleryLog(TAG, "total size is: "+totalsize);
				    post.setEntity(entity);
				    HttpResponse response = client.execute(post);        
				    response1= getContent(response);
				
				Log.v(TAG, "response is: "+ response1);
				
		
			
			if (response1 != null && !response1.isEmpty()) {
				
				Bundle resultData = new Bundle();
				resultData.putInt("progress" , 200);
				receiver.send(UPDATE_PROGRESS, resultData);
				
				mBuilder.setProgress(0, 0, false);
				mNotifyManager.notify(Notofication_Image_ID, mBuilder.build());
				
				try {
					JSONObject jsonobj=new JSONObject(response1);
					String resp=jsonobj.getString("Success");
					
					if (resp.equalsIgnoreCase("Successfully Uploaded the Image File")) {
						//Toast.makeText(getApplicationContext(), "Upload Complete!", Toast.LENGTH_LONG).show();
						
						for (int i = 0; i < allpath.length; i++) {
							
							String url2=selectedfilename[i];
							ImageModel model=new ImageModel(url2);
							db.addImage(model);
							
							db.DeletePendingRow(url2);
							
						}
						
						 NotificationManager notificationManager = (NotificationManager)getApplicationContext()
						            .getSystemService(Context.NOTIFICATION_SERVICE);
						    @SuppressWarnings("deprecation")
							Notification notification = new Notification(android.R.drawable.
								      stat_notify_more, "Upload Complete!", System.currentTimeMillis());

						    Intent notificationIntent = new Intent(getApplicationContext(), ImageDiectoriesActivity.class);

						    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

						    PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

						    notification.setLatestEventInfo(getApplicationContext(), "Gallery Upload", "Upload Complete!", intent);
						   // notification.flags |= Notification.FLAG_AUTO_CANCEL;
						    notificationManager.notify(Notofication_Image_ID, notification);
						
					}
					else{
						ImageUtil.galleryLog(TAG, "resp: "+resp);
						//Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
					}
					
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	catch (Exception e) {
				e.printStackTrace();
			}
				
			}
			else{
				//Toast.makeText(getApplicationContext(), "Unable to connect to server, Please try again.", Toast.LENGTH_LONG).show();
			}
			} catch (UnknownHostException e) {
				e.printStackTrace();
				ImageUtil.galleryLog(TAG, "excep: "+e);
				
				 NotificationManager notificationManager = (NotificationManager)getApplicationContext()
				            .getSystemService(Context.NOTIFICATION_SERVICE);
				    @SuppressWarnings("deprecation")
					Notification notification = new Notification(android.R.drawable.
						      stat_notify_more, "Upload Failed!", System.currentTimeMillis());

				    Intent notificationIntent = new Intent(getApplicationContext(), FileDirectories.class);

				    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

				    PendingIntent intent1 = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

				    notification.setLatestEventInfo(getApplicationContext(), "Gallery Upload", "Upload Failed! Internet Connection Error.", intent1);
				    notification.flags |= Notification.FLAG_AUTO_CANCEL;
				    notificationManager.notify(Notofication_Image_ID, notification);
				    
				   // mBuilder.setProgress(0, 0, false);
					//mNotifyManager.notify(0, mBuilder.build());
				    
				    stopSelf();
				 
			}catch (Exception e) {
				e.printStackTrace();
			}
	   }
	
}
