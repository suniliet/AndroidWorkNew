package com.sunil.selectmutiple;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kns.adapter.CustomRequestAdapter;
import com.kns.db.DBHelper;
import com.kns.model.CategoryModel;
import com.kns.model.CustomRequestModel;
import com.kns.util.AlertDialogRadioCustomPrefer;
import com.kns.util.AlertDialogRadioCustomPrefer.AlertPositiveListenerCustom;
import com.kns.util.AlertDialogRadioNotification;
import com.kns.util.AlertDialogRadioNotification.AlertPositiveListener;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class MainActivity extends Activity implements OnClickListener, AlertPositiveListener, AlertPositiveListenerCustom{

	private final static String TAG="MainActivity";
	private ImageButton btn_custom_gallery;
	private ImageButton btn_custom_gallery_video;
	private ImageButton btn_myuploads;
	private ImageButton btn_notification;
	private ImageButton btn_back=null;
	private ImageButton btn_refresh=null;
	private ImageButton btn_viewsale=null;
	private ImageButton btn_help=null;
	private ImageButton btn_myprefrence=null;
	private Button btn_request;
	private ImageButton btn_overflow=null;
	private Context context=null;
	private AlertDialog alert = null;
	private AlertDialog alert1 = null;
	private ProgressDialog prodialog_notify;
	private ProgressDialog prodialog_custreq;
	private ProgressDialog prodialog_custreqcheck;
	private ProgressDialog prodialog_cat;
	private ProgressDialog prodialog_editprofile;

	private ProgressBar progressbar=null;
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	final static int CAMERA_RESULT = 0;
	final static int VIDEO_RESULT = 1;
	public static final String ACTION_MULTIPLE_PICK = "ACTION_MULTIPLE_PICK";
	//public static final String ACTION_MULTIPLE_PICK1 = "ACTION_MULTIPLE_PICK";
	public static final String ACTION_MULTIPLE_PICK1 = "video.ACTION_MULTIPLE_PICK";
	private ProgressDialog prodialog;
	private ProgressDialog prodialog_partapprov;
	int positionnotofication=0;
	int positionmyprefernece=0;
	int prefernceclick=0;
	List<CustomRequestModel> list_custom=new ArrayList<CustomRequestModel>();
	DBHelper db;
	private int numberofreq=0;
	
	///////////
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
     
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
  
    private Uri fileUri; // file url to store image/video
     //////////////////
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context=this;
		
		db=new DBHelper(context);
		
		btn_custom_gallery=(ImageButton)findViewById(R.id.button_customgallery);
		btn_custom_gallery_video=(ImageButton)findViewById(R.id.button_gelleryvideo);
	    btn_back= (ImageButton)findViewById(R.id.imageButton_back);
	    btn_myuploads= (ImageButton)findViewById(R.id.button_myupload);
	    btn_request=(Button)findViewById(R.id.button_request);
	    btn_notification=(ImageButton)findViewById(R.id.imageButton_notofication);
	    btn_viewsale=(ImageButton)findViewById(R.id.imageButton_viewsale);
	    btn_help=(ImageButton)findViewById(R.id.imageButton_help);
	    btn_refresh=(ImageButton)findViewById(R.id.imageButton_refresh);
	    btn_refresh.setOnClickListener(this);

	    btn_overflow = (ImageButton) findViewById(R.id.imageButton_overflow);
	    btn_myprefrence= (ImageButton) findViewById(R.id.imageButton_prefrenece);
	    
	    btn_myprefrence.setOnClickListener(this);
	    btn_overflow.setOnClickListener(this);
	    btn_back.setOnClickListener(this);
	    btn_myuploads.setOnClickListener(this);
		btn_custom_gallery.setOnClickListener(this);
		btn_custom_gallery_video.setOnClickListener(this);
		btn_request.setOnClickListener(this);
		btn_notification.setOnClickListener(this);
		btn_viewsale.setOnClickListener(this);
		btn_help.setOnClickListener(this);
		
		btn_request.setVisibility(View.GONE);
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		boolean isinternet=ImageUtil.isInternetOn(context);
		if (isinternet) {
			
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
    		    new PartnerApprovedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    		} else {
    		    new PartnerApprovedTask().execute();
    		}
			
			
			/*PartnerApprovedTask task1=new PartnerApprovedTask();
			task1.execute();*/
		}
		
		
		if (isinternet) {
			
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
    		    new CustomRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    		} else {
    		    new CustomRequestTask().execute();
    		}
			
			/*CustomRequestTask task=new CustomRequestTask();
			task.execute();*/
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
		if (v==btn_custom_gallery) {
			/*Intent intent=new Intent(MainActivity.this, GridShowImageActivity.class);
			startActivity(intent);*/
			boolean iscamera=hasCamera();
			if (iscamera) {
				showDialogButtonClick();
			}
			else{
				ImageUtil.showAlert(MainActivity.this, "Your device doesn't have camera feature.");
			}
			
		}
		else if (v==btn_custom_gallery_video) {
			
			/*Intent intent=new Intent(MainActivity.this, GridShowVideoActivity.class);
			  startActivity(intent);*/
			//this is handle to work for picture/video recording.
			
			 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
			 String checkstatus=Prefs.getString(ImageConstant.CHECKBOXSTATUS, "");
			 if (checkstatus.equalsIgnoreCase("not checked") || checkstatus.equalsIgnoreCase("")) {
				
				 attentionBox();
			}
			 else{
				 
				 //recordVideo();
					
					boolean iscamera=hasCamera();
					if (iscamera) {
						//showDialogButtonClickTake();
						//Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
						Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
						//intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10); //10 sec
						//intent.putExtra("android.intent.extra.sizeLimit", 12*1048*1048);  //12 MB  
						//intent.putExtra(android.provider.MediaStore.EXTRA_SIZE_LIMIT, 1*1024L);  //12 MB  
						startActivityForResult(intent, CAMERA_RESULT);
					}
					else{
						ImageUtil.showAlert(MainActivity.this, "Your device doesn't have camera feature.");
					}
			 }
		
			
		}
		else if (btn_back==v) {
			finish();
		}
		else if (btn_overflow==v) {
			
			// View menuItemView = findViewById(R.id.btn_overflow);
			 PopupMenu popup = new PopupMenu(MainActivity.this, btn_overflow);   
	         popup.getMenuInflater().inflate(R.menu.overflowmenu, popup.getMenu()); 
	         
	         popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
	       
				@Override
				public boolean onMenuItemClick(android.view.MenuItem item) {
					if(item.getItemId()==R.id.logout){
						
					 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
					 SharedPreferences.Editor editor = Prefs.edit();
					 editor.putString(ImageConstant.USERNAME, "");
					 editor.putString(ImageConstant.MEMBERID, "");
					 editor.putString(ImageConstant.CHECKBOXSTATUS, "");
					 editor.commit();	
					 
					  Intent intent2=new Intent(MainActivity.this, LoginNewActvity.class);
					  intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					  startActivity(intent2);
					  
			           return true;
					}
					
					if(item.getItemId()==R.id.editprofile){
						
						boolean isinternet=ImageUtil.isInternetOn(context);
						if (isinternet) {
							
							prodialog_editprofile=ProgressDialog.show(context, "", "Loading..");
							
							if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				    		    new EditProfleTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				    		} else {
				    		    new EditProfleTask().execute();
				    		}
							
							/*EditProfleTask task=new EditProfleTask();
							task.execute();*/
							
						}else{
							ImageUtil.showAlert(MainActivity.this, getResources().getString(R.string.internet_error));
						}
						
				           return true;
						}
					return false;
				
	            }
				
	         });  
	  
	          popup.show();//showing popup menu         	    
	      
            
		}
		
		else if (btn_myuploads==v) {
			
			boolean isinternet=ImageUtil.isInternetOn(context);
			if (isinternet) {
				prodialog=ProgressDialog.show(context, "", "Loading...");
				
				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    		    new MyUploadedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    		} else {
	    		    new MyUploadedTask().execute();
	    		}
				/*MyUploadedTask task=new MyUploadedTask();
				task.execute();*/
			}
			else{
				ImageUtil.showAlert(MainActivity.this, getResources().getString(R.string.internet_error));
			}
			
			/*Intent intent=new Intent(MainActivity.this, MyUploadActivity.class);
			startActivity(intent);*/
		}
		
		else if (btn_request==v) {
			
			
			CustomRequestAdapter adapter=new CustomRequestAdapter(MainActivity.this, list_custom);
			ShowListAlertCustomRequest(adapter);
			
			
		}
		
		else if (btn_notification==v) {
			
			prefernceclick=0;
			
			Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
 			String notifivalue=Prefs.getString(ImageConstant.NOTIFICATIONPREFERENCE, "");
			
			 FragmentManager manager = getFragmentManager();
			 AlertDialogRadioNotification alert = new AlertDialogRadioNotification();
             Bundle b  = new Bundle();
             
             try{
	             if (notifivalue.trim().equalsIgnoreCase("") || notifivalue.isEmpty()) {
	            	 b.putInt("position", positionnotofication);
				}
	             else{
	            	 b.putInt("position", Integer.valueOf(notifivalue));
	             }
             }catch (Exception e) {
            	 e.printStackTrace();
			}
             
             alert.setArguments(b);
             alert.show(manager, "alert_dialog_radio");
		}
		
		else if (btn_refresh==v) {
			

			boolean isinternet=ImageUtil.isInternetOn(context);
			if (isinternet) {
			
				prodialog_cat=ProgressDialog.show(context, "", "Refreshing Category...");
				/*LoadCategoryTask task=new LoadCategoryTask();
				task.execute();
				*/
				
				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    		    new LoadCategoryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    		} else {
	    		    new LoadCategoryTask().execute();
	    		}
			}
			else{

				Toast.makeText(MainActivity.this, getResources().getString(R.string.internet_error), Toast.LENGTH_LONG).show();
			}
		}
		
		else if (btn_myprefrence == v) {
			
			prefernceclick=1;
			
			 FragmentManager manager = getFragmentManager();
			 AlertDialogRadioCustomPrefer alert = new AlertDialogRadioCustomPrefer();
             Bundle b  = new Bundle();
             
            Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
  			String mypreferncevalue=Prefs.getString(ImageConstant.MYPREFERENCE, "");
             try{
	             if (mypreferncevalue.trim().equalsIgnoreCase("") || mypreferncevalue.isEmpty()) {
	            	 b.putInt("position", positionmyprefernece);
				}
	             else{
	            	 b.putInt("position", Integer.valueOf(mypreferncevalue));
	             }
             }catch (Exception e) {
            	 e.printStackTrace();
			}
             
             alert.setArguments(b);
             alert.show(manager, "alert_dialog_radio");
		}
		
		else if (btn_help==v) {
			
			Intent intent=new Intent(MainActivity.this, HelpScreenActivity.class);
			startActivity(intent);
		}
		else if (btn_viewsale==v) {
			
			Intent intent=new Intent(MainActivity.this, ViewSaleActivity.class);
			startActivity(intent);
		}
	}
	
	
	@Override
	public void onPositiveClick(int position) {
		
	
			if (prefernceclick==0) {
				
				
				this.positionnotofication = position;
				
		        //Toast.makeText(context, "Position is"+NotificationArray.code[this.positionnotofication], Toast.LENGTH_LONG).show();
				String notifivalue="";
				 Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
				 notifivalue=Prefs.getString(ImageConstant.NOTIFICATIONPREFERENCE, "");
		       // Log.v(TAG, "Position notofication is: "+positionnotofication);
		        ImageUtil.galleryLog(TAG, "Position notofication is: "+positionnotofication);
		      
				if (notifivalue.equalsIgnoreCase("")) {
					
					if (position==4) {
						// here you call to check 
						prodialog_custreqcheck=ProgressDialog.show(context, "", "Checking custom request..");
						/*CustomRequestCheckTask task=new CustomRequestCheckTask();
						task.execute();
						*/
						
						if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    		    new CustomRequestCheckTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    		} else {
			    		    new CustomRequestCheckTask().execute();
			    		}
					}
					else {

						   boolean isinternet=ImageUtil.isInternetOn(context);
							if (isinternet) {
								
								prodialog_notify=ProgressDialog.show(context, "", "Saving preference..");
								/*SavingPreferencesTask task=new SavingPreferencesTask();
								task.execute();*/
								
								if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
					    		    new SavingPreferencesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					    		} else {
					    		    new SavingPreferencesTask().execute();
					    		}
								
							}
							else{
								ImageUtil.showAlert(MainActivity.this, getResources().getString(R.string.internet_error));
							}
					}
				}
				else if (notifivalue.trim().equalsIgnoreCase(String.valueOf(positionnotofication))) {
					
					 //Toast.makeText(context, NotificationArray.code[this.positionnotofication]+" Saved.", Toast.LENGTH_LONG).show();
				}
				else{
					
					if (position==4) {
						
						// here you call to check 
						prodialog_custreqcheck=ProgressDialog.show(context, "", "Checking custom request..");
						/*CustomRequestCheckTask task=new CustomRequestCheckTask();
						task.execute();*/
						
						if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    		    new CustomRequestCheckTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    		} else {
			    		    new CustomRequestCheckTask().execute();
			    		}
					}
					else{
						
						boolean isinternet=ImageUtil.isInternetOn(context);
						if (isinternet) {
							prodialog_notify=ProgressDialog.show(context, "", "Saving preference..");
							/*SavingPreferencesTask task=new SavingPreferencesTask();
							task.execute();*/
							
							if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				    		    new SavingPreferencesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				    		} else {
				    		    new SavingPreferencesTask().execute();
				    		}
						}
						else{
							ImageUtil.showAlert(MainActivity.this, getResources().getString(R.string.internet_error));
						}
					}
					
				}
		   
			}
		
			else if (prefernceclick==1) {
				
				this.positionmyprefernece = position;
				Log.v(TAG, "Position prefernces is: "+positionmyprefernece);
				
		        Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String notifivalue=Prefs.getString(ImageConstant.MYPREFERENCE, "");
				if (notifivalue.equalsIgnoreCase("")) {
				
					 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
					 SharedPreferences.Editor editor = Prefs.edit();
					 editor.putString(ImageConstant.MYPREFERENCE, "0");
					 editor.commit();	
				}
				else if (notifivalue.trim().equalsIgnoreCase(String.valueOf(positionmyprefernece))) {
					
					 //Toast.makeText(context, NotificationArray.code[this.positionnotofication]+" Saved.", Toast.LENGTH_LONG).show();
				}
				else{
					
					 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
					 SharedPreferences.Editor editor = Prefs.edit();
					 editor.putString(ImageConstant.MYPREFERENCE, String.valueOf(positionmyprefernece));
					 editor.commit();	
					 
					 Toast.makeText(context, "Checking the custom request..", Toast.LENGTH_LONG).show();
					 CustomRequestTask task=new CustomRequestTask();
					 task.execute();
				}
			}
		
		
	}
	
	 private class EditProfleTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				//http://picture-video-store.com/ws/?&ws=1&act=UpdateNotification&consumer_id=4&notificate_value=0
				Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				//String url=GalleryConstant.BASEURL;
				
				String url=ImageConstant.BASEURL+"ViewPartnerDetails";
				
				JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("partner_Id", memberid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      //Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG,  "Json object is: "+obj1.toString());
				
			      HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
			
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				   //Log.v(TAG, "url is: "+url);
				   ImageUtil.galleryLog(TAG, "url is: "+url);
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				        String result = EntityUtils.toString(response.getEntity());   
				       // Log.v(TAG+".doInBackground", "Http response is:" + result);
				       // Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				        
				        ImageUtil.galleryLog(TAG, "Http response is:" + result);
				        ImageUtil.galleryLog(TAG, "json response code:" + responsecode);
				        
				        if (responsecode==200) {
				        
				        	response1=result;
				
				        }
				        
				        else{
				        	response1="";
				        }				        
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog_editprofile.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				ImageUtil.galleryLog(TAG,"Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty() ) {
					
						Intent intent=new Intent(MainActivity.this, EditProfilePictureActivity.class);
						intent.putExtra("JSONAPI", resultString);
						startActivity(intent);
						
				}
				else{
					ImageUtil.showAlert(MainActivity.this, getResources().getString(R.string.timeout));
				}
			}
		}
	
	 private class SavingPreferencesTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				//http://picture-video-store.com/ws/?&ws=1&act=UpdateNotification&consumer_id=4&notificate_value=0
				Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				//String url=GalleryConstant.BASEURL;
				
				String url=ImageConstant.BASEURL+"UpdatePartnerNotification";
				
				JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("notificate_value", positionnotofication);
				
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			    //  Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG,"Json object is: "+obj1.toString());
				
			      HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
			
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				  // Log.v(TAG, "url is: "+url);
				   ImageUtil.galleryLog(TAG,"url is: "+url);
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				        String result = EntityUtils.toString(response.getEntity());   
				       // Log.v(TAG+".doInBackground", "Http response is:" + result);
				       // Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				        
				        ImageUtil.galleryLog(TAG,"Http response is:" + result);
				        ImageUtil.galleryLog(TAG,"json response code:" + responsecode);
				        
				        if (responsecode==200) {
				        
				        	response1=result;
				
				        }
				        
				        else{
				        	response1="";
				        }				        
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog_notify.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty() ) {
					
					try{
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Notification Value updated Successfully")) {
							
							 Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
							 SharedPreferences.Editor editor = Prefs.edit();
							 editor.putString(ImageConstant.NOTIFICATIONPREFERENCE, String.valueOf(positionnotofication));
							 editor.commit();
							 
							 ImageUtil.showAlert(MainActivity.this, "Your Custom Request Notifications Have Been Updated");
						}
						
					}catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				else{
					ImageUtil.showAlert(MainActivity.this, getResources().getString(R.string.timeout));
				}
			}
		}
	
	private void showDialogButtonClick() {
		try {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Choose");

			final CharSequence[] choiceList = { "Gallery Images", "Gallery Videos" };
			int selected = -1; 

			builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int itemNO) {

							if (itemNO == 0) {
								
								/*Intent i = new Intent(ACTION_MULTIPLE_PICK);
								startActivityForResult(i, 200);*/
								
								Intent intent=new Intent(MainActivity.this, ImageDiectoriesActivity.class);
								startActivity(intent);
								alert.dismiss();
							  
							} else if (itemNO == 1) {
								
								Intent intent=new Intent(MainActivity.this, VideoDirectoryActivity.class);
								startActivity(intent);
							/*	
								Intent i = new Intent(ACTION_MULTIPLE_PICK1);
								startActivityForResult(i, 200);
								*/
								
								alert.dismiss();

							}

						}
					}).setCancelable(false);
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							alert.dismiss();
						}
					});

			alert = builder.create();
			alert.show();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
/*	
	private void showDialogButtonClickTake() {
		
		try {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Choose");

			final CharSequence[] choiceList = { "Take Image", "Take Video" };
			int selected = -1; 

			builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int itemNO) {

							if (itemNO == 0) {  
								
								Intent intent=new Intent(MainActivity.this, GridShowImageActivity.class);
								startActivity(intent);
								
								//MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
								//Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
								Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
								startActivityForResult(intent, CAMERA_RESULT);
								
								alert1.dismiss();
							

							} else if (itemNO == 1) {
								
								
								Intent intent=new Intent(MainActivity.this, GridShowVideoActivity.class);
								
								startActivity(intent);
								
								Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
								startActivityForResult(intent, VIDEO_RESULT);
								
								alert1.dismiss();

							}

						}
					}).setCancelable(false);
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							alert1.dismiss();
						}
					});

			alert1 = builder.create();
			alert1.show();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/


	private boolean hasCamera() {
	    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        return true;
	    } else {
	        return false;
	    }
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    //if (requestCode == CAMERA_RESULT) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
	           // Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
	        	//Toast.makeText(this, "Image saved successfully.", Toast.LENGTH_LONG).show();
	        	
	        	startActivityForResult(data, CAMERA_RESULT);
	        	
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        	Log.v(TAG, "cancelled the image capture");
	        } else {
	            // Image capture failed, advise user
	        	Log.v(TAG, "Image capture failed");
	        }
	   // }

	   // if (requestCode == VIDEO_RESULT) {
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	          //  Toast.makeText(this, "Video saved to:\n" +
	                    // data.getData(), Toast.LENGTH_LONG).show();
	            Toast.makeText(this, "Video saved successfully.", Toast.LENGTH_LONG).show();

	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the video capture
	        	Log.v(TAG, "cancelled he video capture");
	        } else {
	        	Log.v(TAG, "Video capture faile");
	            // Video capture failed, advise user
	        }
	    }
	//}
	
	  public void attentionBox(){
			
			AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
		    LayoutInflater adbInflater = LayoutInflater.from(MainActivity.this);
		    View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
		    final CheckBox dontShowAgain = (CheckBox)eulaLayout.findViewById(R.id.skip);
		    adb.setView(eulaLayout);
		    adb.setTitle("Attention");
		    adb.setMessage("Videos should be 15 minutes or less. The max video upload size is 2000 MB.");
	    	adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
	    	      public void onClick(DialogInterface dialog, int which) {
	    	 
	    	    	  if (dontShowAgain.isChecked())  {
	    	    
	    	    		 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
						 SharedPreferences.Editor editor = Prefs.edit();
						 editor.putString(ImageConstant.CHECKBOXSTATUS, "checked");
						 editor.commit();	
						 
	    	    	  }
	    	    	  else{
	    	    		  

	     	    		 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
	 					 SharedPreferences.Editor editor = Prefs.edit();
	 					 editor.putString(ImageConstant.CHECKBOXSTATUS, "not checked");
	 					 editor.commit();	
	    	    	  }
	    	  			
	    	    		
						boolean iscamera=hasCamera();
						if (iscamera) {
						
							Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
							startActivityForResult(intent, CAMERA_RESULT);
						}
						else{
							ImageUtil.showAlert(MainActivity.this, "Your device doesn't have camera feature.");
						}
	    	    	  
	    	    	  return;  
	    	      } });
	 
	    	  adb.show();
		}
	  
	  private class MyUploadedTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
				
				Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				//http://picture-video-store.com/ws/?ws=1&act=getIV&MemberID=1
				
				//String url=ImageConstant.BASEURLMYUPLOAD;
				//String url="http://picture-video-store.com/ws/?";
				
				String url=ImageConstant.BASEURL+"getIVUpdate";
				
				
                JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      //Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG,"Json object is: "+obj1.toString());
			      

				  HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
								
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				  // Log.v(TAG, "url is: "+url);
				   ImageUtil.galleryLog(TAG,"url is: "+url);
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				    	String result = EntityUtils.toString(response.getEntity());  
				    	//Log.v(TAG+".doInBackground", "Http response is:" + result);
				         //Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         
				         ImageUtil.galleryLog(TAG,"Http response is:" + result);
				         ImageUtil.galleryLog(TAG,"json response code:" + responsecode);
				         
				        if (responsecode==200) {
				        	
				        	response1 = result;  
				        	
				        }
				        
				        else{
				        	response1="";
				        }				       
			
			
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				 e.printStackTrace();
				Log.v(TAG, "Connection time out");
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
				   
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				 ImageUtil.galleryLog(TAG,"Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					Intent intent=new Intent(MainActivity.this, MyUploadActivity.class);
					intent.putExtra("JSONAPI", resultString);
					startActivity(intent);
				}
				else{
					
					ImageUtil.showAlert(MainActivity.this, getResources().getString(R.string.timeout));
				}

			}
		}
	  
	  private class CustomRequestTask extends AsyncTask<String, Void, String> {
			String response1 = "";
		
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
				String url=ImageConstant.BASEURL+"viewCustomNotificationdetails";
				
				JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
				
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			     // Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG,"Json object is: "+obj1.toString());
			      
				  HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
								
				
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				   //Log.v(TAG, "url is: "+url);
				   ImageUtil.galleryLog(TAG,"url is: "+url);
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				        String result = EntityUtils.toString(response.getEntity());   
				        //Log.v(TAG+".doInBackground", "Http response is:" + result);
				        //Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				        
				        ImageUtil.galleryLog(TAG,"Http response is:" + result);
				        ImageUtil.galleryLog(TAG,"json response code:" + responsecode);
				        
				        if (responsecode==200) {
				        	 
				        	response1=result;
				
				        }
				        
				        else{
				        	response1="";
				        }				       
	
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				 e.printStackTrace();
				Log.v(TAG, "Connection time out");
			}
			catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
				   
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				//prodialog_custreq.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				list_custom.clear();
				numberofreq=0;
				if (resultString != null && !resultString.isEmpty()) {
					
					try {
						
						JSONArray jsonarray=new JSONArray(resultString);
						/*if (jsonarray.length() > 1) {
							btn_request.setVisibility(View.VISIBLE);
						}
						else{
							btn_request.setVisibility(View.GONE);
						}*/
						for (int i = 0; i < jsonarray.length(); i++) {
							
							JSONObject jsonobj=jsonarray.getJSONObject(i);
							String partnerid=jsonobj.getString("PartnerID");
							
							JSONArray isjsonarray=jsonobj.optJSONArray("NotificationRequestDetails");
							if (isjsonarray==null) {
								
							}
							else{
								
								JSONArray jsonarray2=jsonobj.getJSONArray("NotificationRequestDetails");
								
								/*	if (jsonarray2.length() > 0) {
										
										btn_request.setVisibility(View.VISIBLE);
										btn_request.setText("You have "+jsonarray2.length()+" request");
									}*/
									
									for (int j = 0; j < jsonarray2.length(); j++) {
										
										JSONObject jsonobj2=jsonarray2.getJSONObject(j);
										String RequestFormID=jsonobj2.getString("RequestFormID");
										String ConumserID=jsonobj2.getString("ConsumerID");
										String CustomRequest=jsonobj2.getString("CustomRequest");
										String ConsumerName=jsonobj2.getString("ConsumerName");
										String CustomAmount=jsonobj2.getString("CustomAmount");
										String ApprovalFlag=jsonobj2.getString("ApprovalFlag");
										String RequestStatusflag=jsonobj2.getString("RequestStatusflag");
										
										if (ApprovalFlag.equalsIgnoreCase("1") && RequestStatusflag.equalsIgnoreCase("0")) {
											
											numberofreq++;
											CustomRequestModel model=new CustomRequestModel(partnerid, ConumserID, ConsumerName, CustomRequest, CustomAmount, ApprovalFlag, RequestFormID);
											list_custom.add(model);
											
										}else{
											//Log.v(TAG, "not approved even");
											ImageUtil.galleryLog(TAG,"not approved even");
										}
										
										
									}
									
									//Log.v(TAG, "numberofreq is: "+numberofreq);
									ImageUtil.galleryLog(TAG,"numberofreq is: "+numberofreq);
									if (numberofreq > 0) {
										
										//sdfads
										
									    Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
										String notifivalue=Prefs.getString(ImageConstant.MYPREFERENCE, "");
									//	Log.v(TAG, "notifivalue is: "+notifivalue);
										ImageUtil.galleryLog(TAG,"notifivalue is: "+notifivalue);
										
										if (notifivalue.trim().equalsIgnoreCase("0") || notifivalue.trim().equalsIgnoreCase("")) {
											
											btn_request.setVisibility(View.VISIBLE);
											btn_request.setText("You have "+numberofreq+" request");
										}
										else{
											
											//Log.v(TAG, "Preference value is not enabled.");
											ImageUtil.galleryLog(TAG,"Preference value is not enabled.");
											
										}
										
									}
									else{
										
										btn_request.setVisibility(View.GONE);
										//Toast.makeText(context, "You have no any custom request by user.", Toast.LENGTH_LONG).show();
									}
							}
							
						
						}
					
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				else{
					ImageUtil.showAlert(MainActivity.this, getResources().getString(R.string.timeout));
				}

			}
		}

		
	  private class CustomRequestCheckTask extends AsyncTask<String, Void, String> {
			String response1 = "";
		
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
				String url=ImageConstant.BASEURL+"viewCustomNotificationdetails";
				
				JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
				
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			     // Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
			      
				  HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
								
				
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				  // Log.v(TAG, "url is: "+url);
				   ImageUtil.galleryLog(TAG, "url is: "+url);
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				        String result = EntityUtils.toString(response.getEntity());   
				        //Log.v(TAG+".doInBackground", "Http response is:" + result);
				       // Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				        
				        ImageUtil.galleryLog(TAG, "Http response is:" + result);
				        ImageUtil.galleryLog(TAG, "json response code:" + responsecode);
				        
				        if (responsecode==200) {
				        	 
				        	response1=result;
				
				        }
				        
				        else{
				        	response1="";
				        }				       
	
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				 e.printStackTrace();
				Log.v(TAG, "Connection time out");
			}
			catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
				   
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog_custreqcheck.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				list_custom.clear();
				numberofreq=0;
				if (resultString != null && !resultString.isEmpty()) {
					
					try {
						
						JSONArray jsonarray=new JSONArray(resultString);
						/*if (jsonarray.length() > 1) {
							btn_request.setVisibility(View.VISIBLE);
						}
						else{
							btn_request.setVisibility(View.GONE);
						}*/
						for (int i = 0; i < jsonarray.length(); i++) {
							
							JSONObject jsonobj=jsonarray.getJSONObject(i);
							String partnerid=jsonobj.getString("PartnerID");
							
							JSONArray isjsonarray=jsonobj.optJSONArray("NotificationRequestDetails");
							if (isjsonarray==null) {
								Toast.makeText(context, "You haven't any custom request by user.", Toast.LENGTH_LONG).show();
							}
							else{
								
								JSONArray jsonarray2=jsonobj.getJSONArray("NotificationRequestDetails");
								
								/*	if (jsonarray2.length() > 0) {
										
										btn_request.setVisibility(View.VISIBLE);
										btn_request.setText("You have "+jsonarray2.length()+" request");
									}*/
									
									for (int j = 0; j < jsonarray2.length(); j++) {
										
										JSONObject jsonobj2=jsonarray2.getJSONObject(j);
										String RequestFormID=jsonobj2.getString("RequestFormID");
										String ConumserID=jsonobj2.getString("ConsumerID");
										String CustomRequest=jsonobj2.getString("CustomRequest");
										String ConsumerName=jsonobj2.getString("ConsumerName");
										String CustomAmount=jsonobj2.getString("CustomAmount");
										String ApprovalFlag=jsonobj2.getString("ApprovalFlag");
										String RequestStatusflag=jsonobj2.getString("RequestStatusflag");
										
										if (ApprovalFlag.equalsIgnoreCase("1") && RequestStatusflag.equalsIgnoreCase("0")) {
											
											numberofreq++;
											CustomRequestModel model=new CustomRequestModel(partnerid, ConumserID, ConsumerName, CustomRequest, CustomAmount, ApprovalFlag, RequestFormID);
											list_custom.add(model);
											
										}else{
											Log.v(TAG, "not approved even");
										}
										
									}
									
									Log.v(TAG, "numberofreq is: "+numberofreq);
								
									if (numberofreq > 0) {
										
										Toast.makeText(context, "You have "+numberofreq+" custom request by user.", Toast.LENGTH_LONG).show();
										//sdfads
										
									    Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
										String notifivalue=Prefs.getString(ImageConstant.MYPREFERENCE, "");
										Log.v(TAG, "notifivalue is: "+notifivalue);
										
										if (notifivalue.trim().equalsIgnoreCase("0") || notifivalue.trim().equalsIgnoreCase("")) {
											
											btn_request.setVisibility(View.VISIBLE);
											btn_request.setText("You have "+numberofreq+" request");
										}
										else{
											
											//Log.v(TAG, "Preference value is not enabled.");
											ImageUtil.galleryLog(TAG, "Preference value is not enabled.");
											
										}
										
									}
									else{
										
										btn_request.setVisibility(View.GONE);
										Toast.makeText(context, "You haven't any custom request by user.", Toast.LENGTH_LONG).show();
									}
							}
							
						
						}
					
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				else{
					ImageUtil.showAlert(MainActivity.this, getResources().getString(R.string.timeout));
				}

			}
		}
	  
		 public void ShowListAlertCustomRequest(CustomRequestAdapter adapter){
				
			    AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
			    
			    myDialog.setTitle("Custom Request List");
		        final ListView listview=new ListView(this);
		        listview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		      
		        LinearLayout layout = new LinearLayout(this);
		        layout.setOrientation(LinearLayout.VERTICAL);		
		        layout.addView(listview);
		        myDialog.setView(layout);
		        listview.setAdapter(adapter);
		        listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						
						// add the images inside 
						CustomRequestModel model=list_custom.get(arg2);
						String cunsumerid=model.getConsumerID();
						String partnerid=model.getPartnerID();
						String consumername=model.getCustomName();
						String consumerrequest=model.getCustomRequest();
						String consumerammount=model.getCustomAmount();
						String arrpovalflag=model.getApproval_Flag();
						String RequestFormID=model.getRequestFormID();
						
						Intent intent=new Intent(MainActivity.this, RequestActivity.class);
						intent.putExtra("cunsumerid", cunsumerid);
						intent.putExtra("cunsumerid", cunsumerid);
						intent.putExtra("partnerid", partnerid);
						intent.putExtra("consumername", consumername);
						intent.putExtra("consumerrequest", consumerrequest);
						intent.putExtra("consumerammount", consumerammount);
						intent.putExtra("arrpovalflag", arrpovalflag);
						intent.putExtra("RequestFormID", RequestFormID);
						startActivity(intent);
					    
						
				    }
		        
		        });
		       
		        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		 
		                        @Override
		                        public void onClick(DialogInterface dialog, int which) {
		                            dialog.dismiss();
		                        }
		                    });
		        
		        /*myDialog.setPositiveButton("Create Set", new DialogInterface.OnClickListener() {
		       	 
	          @Override
	          public void onClick(DialogInterface dialog, int which) {
	              dialog.dismiss();
	              
	          }
	      });
		 */
		    myDialog.show();
		         
		 }
		 
		  private class LoadCategoryTask extends AsyncTask<String, Void, String> {
				
		    	String response1 = "";
				
				@Override
				protected String doInBackground(String... urls) {
				
					Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
					String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
					
					//String url=GalleryConstant.BASEURL;
					
					String url=ImageConstant.BASEURL+"getCategoryList";
						
					JSONObject obj1=new JSONObject();
					
					try {
						obj1.put("MemberID", memberid);
						
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
				       //Log.v(TAG, "Json object is: "+obj1.toString());
				       ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
				       
				 	  HttpParams httpParameters = new BasicHttpParams();
					  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
					  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
									
					
					   HttpClient httpclient = new DefaultHttpClient(httpParameters);
					 //  Log.v(TAG, "url is: "+url);
					   ImageUtil.galleryLog(TAG, "url is: "+url);
					   HttpPost  httppost = new HttpPost(url);
					 
					   try {

						   StringEntity se = new StringEntity(obj1.toString());
						   httppost.setEntity(se);  
						   httppost.setHeader("Accept", "application/json");
						   httppost.setHeader("Content-type", "application/json");

					        HttpResponse response = httpclient.execute(httppost);
					        int responsecode=response.getStatusLine().getStatusCode();
					        String result = EntityUtils.toString(response.getEntity());   
					       /// Log.v(TAG+".doInBackground", "Http response is:" + result);
					       // Log.v(TAG+".doInBackground", "json response code:" + responsecode);
					        
					        ImageUtil.galleryLog(TAG, "Http response is:" + result);
					        ImageUtil.galleryLog(TAG, "json response code:" + responsecode);
					        
					        if (responsecode==200) {
					      
					        	response1=result;
					
					        }
					        
					        else{
					        	
					        	response1="";
					        }				       
					
					
					
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (ConnectTimeoutException e) {
					e.printStackTrace();
					Log.v(TAG, "Connection time out");
				}
				 catch (IOException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
				 return response1;
			}

				@Override
				protected void onPostExecute(String resultString) {
					
				
					prodialog_cat.dismiss();
					Log.v(TAG, "onPostExecute called");
					//Log.v(TAG, "Response is: "+resultString);
					 ImageUtil.galleryLog(TAG, "Response is: "+resultString);
					if (resultString != null  && !resultString.isEmpty()) {
						db.DeleteCategory();
	                    try {
							JSONArray jsonarray=new JSONArray(resultString);
							for (int i = 0; i < jsonarray.length(); i++) {
								
								JSONObject jsonobj=jsonarray.getJSONObject(i);
								String cat_id=jsonobj.getString("CategoryID");
								String cat_name=jsonobj.getString("CategoryName");
								CategoryModel model=new CategoryModel(cat_id, cat_name, false);
								//list_category.add(model);
								List<CategoryModel> list=db.GetCategoryData();
								db.addCategory(model);
								
							/*	if (list.size() > 0) {
									
									db.addCategory(model);
									Toast.makeText(context, "Category Updated.", Toast.LENGTH_LONG).show();
								}else{
									
								}*/
								
								
							}
							Toast.makeText(context, "Refresh Complete.", Toast.LENGTH_LONG).show();
							
						} catch (JSONException e) {
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
					
	                  
					}
					else{
						ImageUtil.showAlert(MainActivity.this, getResources().getString(R.string.timeout));
					}

				}
			}
		  
		  
		  private class PartnerApprovedTask extends AsyncTask<String, Void, String> {
				
		    	String response1 = "";
				
				@Override
				protected String doInBackground(String... urls) {
				
					Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
					String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
					
					//String url=GalleryConstant.BASEURL;
					
					String url=ImageConstant.BASEURL+"getpartnerflagdetails";
						
					JSONObject obj1=new JSONObject();
					
					try {
						obj1.put("MemberID", memberid);
						
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
				       //Log.v(TAG, "Json object is: "+obj1.toString());
				       ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
				       
			       
				 	  HttpParams httpParameters = new BasicHttpParams();
					  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
					  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
				
					   HttpClient httpclient = new DefaultHttpClient(httpParameters);
					  // Log.v(TAG, "url is: "+url);
					   ImageUtil.galleryLog(TAG, "url is: "+url);
					   HttpPost  httppost = new HttpPost(url);
					 
					   try {

						   StringEntity se = new StringEntity(obj1.toString());
						   httppost.setEntity(se);  
						   httppost.setHeader("Accept", "application/json");
						   httppost.setHeader("Content-type", "application/json");

					        HttpResponse response = httpclient.execute(httppost);
					        int responsecode=response.getStatusLine().getStatusCode();
					        String result = EntityUtils.toString(response.getEntity());   
					      //  Log.v(TAG+".doInBackground", "Http response is:" + result);
					        //Log.v(TAG+".doInBackground", "json response code:" + responsecode);
					        
					        ImageUtil.galleryLog(TAG, "Http response is:" + result);
					        ImageUtil.galleryLog(TAG, "json response code:" + responsecode);
					        
					        if (responsecode==200) {
					      
					        	response1=result;
					
					        }
					        
					        else{
					        	
					        	response1="";
					        }				       
					
					
					
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				}catch (ConnectTimeoutException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
				 return response1;
			}

				@Override
				protected void onPostExecute(String resultString) {
					
				
					//prodialog_partapprov.dismiss();
					Log.v(TAG, "onPostExecute called");
					//Log.v(TAG, "Response is: "+resultString);
					 ImageUtil.galleryLog(TAG, "Response is: "+resultString);
					if (resultString != null && !resultString.isEmpty()) {
						
						try{
							
							JSONArray jsonarray=new JSONArray(resultString);
							for (int i = 0; i < jsonarray.length(); i++) {
								
								JSONObject jsonobj=jsonarray.getJSONObject(i);
								String memberid=jsonobj.getString("MemberID");
								String status=jsonobj.getString("Status");
								
								 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
			 					 SharedPreferences.Editor editor = Prefs.edit();
			 					 editor.putString(ImageConstant.PARTNERAPPROVED, status);
			 					 editor.commit();	
								
							}
						
							
						} catch (JSONException e) {
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
					
	                  
					}
				}	
				
		  }
		  
			 
			 private void recordVideo() {
			        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			  
			        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
			  
			        // set video quality
			        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			  
			        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
			                                                            // name
			  
			        // start the video capture Intent
			        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
			    }

			 public Uri getOutputMediaFileUri(int type) {
			        return Uri.fromFile(getOutputMediaFile(type));
			    }
			  
			    /**
			     * returning image / video
			     */
			    private static File getOutputMediaFile(int type) {
			  
			        // External sdcard location
			        File mediaStorageDir = new File(
			                Environment
			                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
			                        "Tonza Files");
			  
			        // Create the storage directory if it does not exist
			        if (!mediaStorageDir.exists()) {
			            if (!mediaStorageDir.mkdirs()) {
			                Log.d(TAG, "Oops! Failed create "
			                        + "Tonza Files" + " directory");
			                return null;
			            }
			        }
			  
			        // Create a media file name
			        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
			                Locale.getDefault()).format(new Date());
			        File mediaFile;
			        if (type == MEDIA_TYPE_IMAGE) {
			            mediaFile = new File(mediaStorageDir.getPath() + File.separator
			                    + "IMG_" + timeStamp + ".jpg");
			        } else if (type == MEDIA_TYPE_VIDEO) {
			            mediaFile = new File(mediaStorageDir.getPath() + File.separator
			                    + "VID_" + timeStamp + ".mp4");
			        } else {
			            return null;
			        }
			  
			        return mediaFile;
			    }
}
