package com.kns.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.kns.adapter.GalleryAdapter;
import com.kns.db.DBHelper;
import com.kns.model.VideoModel;
import com.kns.util.CustomMultiPartEntity;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;
import com.kns.util.CustomMultiPartEntity.ProgressListener;
import com.sunil.selectmutiple.CustomGallery;
import com.sunil.selectmutiple.R;
import com.sunil.selectmutiple.VideoDirectoryActivity;

public class UploadVideoServicePending extends IntentService{

	private static final String TAG="UploadVideoService";
	private DBHelper db=null;
	private ProgressDialog prodialog1;
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
	private int totalsize=0;
	//private ImageLoader imageLoader;
	private GalleryAdapter adapter;
	private long totalsizeimage=0;
	public static final int UPDATE_PROGRESS = 200;
	ResultReceiver receiver = null;
	String allpath[];
	String selectedfilename[];
	int currentId;
	int Notofication_Video_ID = 3;
	
	private NotificationManager mNotifyManager;
	private Builder mBuilder;
	private SharedPreferences preferenceManager;
	String Upload_ID = "Upload_ID";
	
	public UploadVideoServicePending() {
		super("name");
		Log.v(TAG, "UploadVideoServicePending called");
		//Log.v(TAG, "AutoUploadServiceNotes called");
	   // ePodUtil.epodLog(context, TAG,"AutoUploadServiceNotes called");
	}
	public UploadVideoServicePending(String name) {
		super(name);
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "UploadVideoServicePending onCreate called");
		db=new DBHelper(getApplicationContext());
	}
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.v(TAG, "onHandleIntent called");
		
		//receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
		
		/// preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
		
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
			
			NotificationManager notificationManager = (NotificationManager)getApplicationContext()
		            .getSystemService(Context.NOTIFICATION_SERVICE);
		    @SuppressWarnings("deprecation")
			Notification notification = new Notification(android.R.drawable.
				      stat_notify_more, "Internet Error!", System.currentTimeMillis());

		    Intent notificationIntent = new Intent(getApplicationContext(), VideoDirectoryActivity.class);

		    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		    PendingIntent intent1 = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

		    notification.setLatestEventInfo(getApplicationContext(), "", "Internet Error!", intent1);
		    notification.flags |= Notification.FLAG_AUTO_CANCEL;
		    notificationManager.notify(0, notification);
		    
		    mBuilder.setProgress(0, 0, false);
			mNotifyManager.notify(Notofication_Video_ID, mBuilder.build());
			
			stopSelf();
		}
		
		
	}
	

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
			   
		   	String response1="";
			   ImageUtil.galleryLog(TAG, "doupload called");
			   Prefs = getApplicationContext().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				ImageUtil.galleryLog(TAG, "memberid: "+memberid);
				
				try {
					
					String url=ImageConstant.BASEURL+"video_upload.php";
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
					    
					    Log.v(TAG, "count is: "+allpath.length);
					    builder.addTextBody("member_id", memberid);
					    builder.addTextBody("count", String.valueOf(allpath.length));
					    
					    final HttpEntity yourEntity = builder.build();
				
					    CustomMultiPartEntity entity=new CustomMultiPartEntity(yourEntity, new ProgressListener() {
							
							@Override
							public void transferred(long num) {
							
								 int update=(int) ((num / (float) totalsizeimage) * 100);
								 
								// Bundle resultData = new Bundle();
					          //   resultData.putInt("progress" ,update);
					         //    resultData.putInt("currentId" ,currentId);
					            // if (update < 100) {
					            	 // receiver.send(UPDATE_PROGRESS, resultData);
					            	  
					            	  mBuilder.setProgress(100, update, false);
					     			  mNotifyManager.notify(Notofication_Video_ID, mBuilder.build());
							//	}
								
								 ImageUtil.galleryLog(TAG, "publish progress :"+update);
							}

						
						});
					    totalsizeimage = entity.getContentLength();
					   // Log.v(TAG, "total size is: "+totalsize);
					    ImageUtil.galleryLog(TAG, "total size is: "+totalsize);
					    post.setEntity(entity);
					    HttpResponse response = client.execute(post);        
					    response1= getContent(response);
					
					
					    if (response1 != null && !response1.isEmpty()) {
							
							//Bundle resultData = new Bundle();
							//resultData.putInt("progress" ,100);
							//receiver.send(UPDATE_PROGRESS, resultData);
							
							mBuilder.setProgress(0, 0, false);
							mNotifyManager.notify(Notofication_Video_ID, mBuilder.build());
							
							try {
								JSONObject jsonobj=new JSONObject(response1);
								String resp=jsonobj.getString("Success");
								
								if (resp.equalsIgnoreCase("Successfully Uploaded Video File")) {
									//Toast.makeText(getApplicationContext(), "Upload Complete!", Toast.LENGTH_LONG).show();
									
									for (int i = 0; i < allpath.length; i++) {
										
										String url2=selectedfilename[i];
										VideoModel model=new VideoModel(url2);
										db.addVideo(model);
										
										db.DeletePendingRow(url2);
										
									}
									
								
									  NotificationManager notificationManager = (NotificationManager)getApplicationContext()
									            .getSystemService(Context.NOTIFICATION_SERVICE);
									    @SuppressWarnings("deprecation")
										Notification notification = new Notification(android.R.drawable.
											      stat_notify_more, "Upload Complete!", System.currentTimeMillis());

									    Intent notificationIntent = new Intent(getApplicationContext(), VideoDirectoryActivity.class);

									    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

									    PendingIntent intent1 = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

									    notification.setLatestEventInfo(getApplicationContext(), "Gallery Upload", "Upload Complete!", intent1);
									    notification.flags |= Notification.FLAG_AUTO_CANCEL;
									    notificationManager.notify(Notofication_Video_ID, notification);
									  
								}
								
						else{
							//Toast.makeText(getApplicationContext(), "Unable to connect to server, Please try again.", Toast.LENGTH_LONG).show();
						}
					    
					    
					
					} 
						catch (JSONException e) {
							e.printStackTrace();
						}catch (Exception e) {
						 e.printStackTrace();
					}
				
			
		        }
					    
						
				} 
				
				catch (UnknownHostException e) {
					e.printStackTrace();
					ImageUtil.galleryLog(TAG, "excep: "+e);
					
					 NotificationManager notificationManager = (NotificationManager)getApplicationContext()
					            .getSystemService(Context.NOTIFICATION_SERVICE);
					    @SuppressWarnings("deprecation")
						Notification notification = new Notification(android.R.drawable.
							      stat_notify_more, "Upload Failed!", System.currentTimeMillis());

					    Intent notificationIntent = new Intent(getApplicationContext(), VideoDirectoryActivity.class);

					    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

					    PendingIntent intent1 = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

					    notification.setLatestEventInfo(getApplicationContext(), "Gallery Upload", "Upload Failed! Internet Connection Error.", intent1);
					    notification.flags |= Notification.FLAG_AUTO_CANCEL;
					    notificationManager.notify(Notofication_Video_ID, notification);
					    
					    stopSelf();
					 
				}
				catch (Exception e) {
					e.printStackTrace();
				}
	   }

}
