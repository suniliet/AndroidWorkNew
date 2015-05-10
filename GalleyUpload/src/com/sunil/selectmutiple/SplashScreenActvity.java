package com.sunil.selectmutiple;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kns.db.DBHelper;
import com.kns.model.CategoryModel;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class SplashScreenActvity extends Activity implements OnClickListener{
	
	private static final String TAG="SplashScreenActvity";
	private Context context=null;
	
	private ProgressBar progressbar=null;
	private ImageButton btn_continue;
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
	
   // protected String SENDER_ID = "987884149445"; 
  //  protected String SENDER_ID = "611493961500";
    private GoogleCloudMessaging gcm =null;
    private String regid = null;
    //List<CategoryModel> list_category=new ArrayList<CategoryModel>();
    private DBHelper db;
    String version = "";
	int versioncode=0;
	private LinearLayout linear_app;
	private TextView textview_applink;
	private TextView textview_appversiontitle;
	private ProgressDialog downlaodapk_prodialog;
	private PowerManager.WakeLock mWakeLock;	
	String VersionURL="";
    Tracker tracker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		context=this;
		
		linear_app=(LinearLayout)findViewById(R.id.linearlayout_appupdate);
		textview_applink=(TextView)findViewById(R.id.TextView_applink);
		textview_appversiontitle=(TextView)findViewById(R.id.textView_versiontitle);
		btn_continue=(ImageButton)findViewById(R.id.imageButton_continue);
		btn_continue.setOnClickListener(this);
		
		linear_app.setVisibility(View.GONE);
		
		progressbar=(ProgressBar)findViewById(R.id.progressbar_load);
		progressbar.setVisibility(View.GONE);
		
		db=new DBHelper(context);
		
	
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			 version = pInfo.versionName;
			 versioncode=pInfo.versionCode;
			 Log.v(TAG, "version name: "+version);
			 Log.v(TAG, "version code: "+versioncode);
			 
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		

	/*	try {
			long installed = getPackageManager().getPackageInfo(getPackageName(), 0).firstInstallTime;
			 Log.v(TAG, "installed time: "+installed);
			 
			    tracker = ((UILApplication) getApplication()).getTracker(UILApplication.TrackerName.APP_TRACKER);
			    tracker.setScreenName("Installation Uploader");
			    tracker.send(new HitBuilders.AppViewBuilder().build());
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}*/
	
	
		
		boolean isinternethave=ImageUtil.isInternetOn(context);
		if (isinternethave) {
			
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
    		    new LoadVersionTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    		} else {
    		    new LoadVersionTask().execute();
    		}
			
			/*LoadVersionTask task=new LoadVersionTask();
			task.execute();*/
		}
		
		List<CategoryModel> list=db.GetCategoryData();
		if (list.size() > 0) {
			
		}
		else{
			
			boolean isinternet=ImageUtil.isInternetOn(context);
			if (isinternet) {
			
				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    		    new LoadCategoryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    		} else {
	    		    new LoadCategoryTask().execute();
	    		}
				/*LoadCategoryTask task=new LoadCategoryTask();
				task.execute();*/
			}
			else{

				Toast.makeText(SplashScreenActvity.this, getResources().getString(R.string.internet_error), Toast.LENGTH_LONG).show();
			}
			
		}
		
		   
	     if (checkPlayServices()) 
	     {
	            gcm = GoogleCloudMessaging.getInstance(context);
	            regid = getRegistrationId(context);
	           
             
	            if (regid.isEmpty() || regid.trim().equalsIgnoreCase(""))
	            {
	                registerInBackground();
	            }
	            else
	            {
	            	Log.v(TAG, " Registration ID is: "+regid);
	            }
	        } 
/*		
		Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
		final String username=Prefs.getString(ImageConstant.USERNAME, "");
		final String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				if (username.equalsIgnoreCase("") && memberid.equalsIgnoreCase("")) {
					Intent intent=new Intent(SplashScreenActvity.this, LoginNewActvity.class);
					startActivity(intent);
					finish();
				}
				else{
					
					Intent intent=new Intent(SplashScreenActvity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
				
				//progressbar.setVisibility(View.GONE);
				
			}
		}, 3000);*/
		
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	 private boolean checkPlayServices() {
	        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	        if (resultCode != ConnectionResult.SUCCESS) {
	            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
	            } else {
	                Log.d(TAG, "This device is not supported - Google Play Services.");
	                Toast.makeText(context, "This device is not supported - Google Play Services.", Toast.LENGTH_LONG).show();
	                //finish();
	            }
	            return false;
	        }
	        return true;
	 }
	 
	 @SuppressWarnings("unchecked")
	private void registerInBackground() {    
		 new AsyncTask() {
	     @ Override
	      protected Object doInBackground(Object... params) 
	      {
	           String msg = "";
	           try 
	           {
	                if (gcm == null) 
	                {
	                         gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(ImageConstant.SENDERID);          
	                Log.v(TAG, "########################################");
	               // Log.v(TAG, "Current Device's Registration ID is: "+regid);  
	                ImageUtil.galleryLog(TAG,"Current Device's Registration ID is: "+regid);
	                 Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
					 SharedPreferences.Editor editor = Prefs.edit();
					 editor.putString(ImageConstant.PROPERTY_REG_ID, regid);
					 editor.commit();
	           } 
	           catch (IOException ex) 
	           {
	               msg = "Error :" + ex.getMessage();
	           }
	           return null;
	      }     
	     protected void onPostExecute(Object result) 
	      { 
	    	 //to do here
	    };
	    	 
	   }.execute();
	 }
	 
	 @SuppressLint("NewApi")
	private String getRegistrationId(Context context) 
	 {
	    Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
	    String registrationId = Prefs.getString(ImageConstant.PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	       // Log.d(TAG, "Registration ID not found.");
	        ImageUtil.galleryLog(TAG,"Registration ID not found.");
	        return "";
	    }
	    int registeredVersion = Prefs.getInt(ImageConstant.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        // Log.d(TAG, "App version changed.");
	         ImageUtil.galleryLog(TAG, "App version changed.");
	         return "";
	     }
	     return registrationId;
	 }

	 private static int getAppVersion(Context context) 
	 {
	      try 
	      {
	          PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	          return packageInfo.versionCode;
	       } 
	       catch (NameNotFoundException e) 
	       {
	             throw new RuntimeException("Could not get package name: " + e);
	       }
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
				
				   HttpClient httpclient = new DefaultHttpClient();
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
				      //  Log.v(TAG+".doInBackground", "Http response is:" + result);
				      //  Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				        
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
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
			
				//list_category.clear();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				ImageUtil.galleryLog(TAG,"Response is: "+resultString);
				if (resultString != null) {
					
                    try {
						JSONArray jsonarray=new JSONArray(resultString);
						for (int i = 0; i < jsonarray.length(); i++) {
							
							JSONObject jsonobj=jsonarray.getJSONObject(i);
							String cat_id=jsonobj.getString("CategoryID");
							String cat_name=jsonobj.getString("CategoryName");
							CategoryModel model=new CategoryModel(cat_id, cat_name, false);
							//list_category.add(model);
							db.addCategory(model);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
				
                  
				}

			}
		}
	    
	    
  private class LoadVersionTask extends AsyncTask<String, Void, String> {
			
	    	String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				//String url=GalleryConstant.BASEURL;
				
				String url=ImageConstant.BASEURL+"getUploadVersiondetails";
			
				
				 try {
						HttpClient httpclient = new DefaultHttpClient();
						//Log.v(TAG, "url is: "+url);
						ImageUtil.galleryLog(TAG,"url is: "+url);
						HttpPost  httppost = new HttpPost(url);

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
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
			
				//list_category.clear();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				  ImageUtil.galleryLog(TAG,"Response is: "+resultString);
				if (resultString != null) {
					
                    try {
						
                    	JSONObject jsonobj=new JSONObject(resultString);
                    	String status=jsonobj.getString("Status");
                    	if (status.trim().equalsIgnoreCase("Success")) {
							
                    		String versionapp=jsonobj.getString("UploadVersionDetails");
                    		VersionURL=jsonobj.getString("VersionURL");
                    		String versiontitle=jsonobj.getString("VersionTitle");
                    		
                    		if (versionapp != null && !versionapp.isEmpty()) {
								int versionco=Integer.parseInt(versionapp);
								if (versioncode < versionco) {
									
									Log.v(TAG, "inside the if condition");
									
									linear_app.setVisibility(View.VISIBLE);
									textview_applink.setText(VersionURL);
									textview_appversiontitle.setText(versiontitle);
									
									boolean isinternet=ImageUtil.isInternetOn(context);
									
									if (isinternet) {
										
										downlaodapk_prodialog = new ProgressDialog(SplashScreenActvity.this);
										downlaodapk_prodialog.setTitle("Upgrading App");
										downlaodapk_prodialog.setMessage("You are upgrading this application. The old version will be replaced with the new version. All your data will be saved");
										downlaodapk_prodialog.setIndeterminate(true);
										downlaodapk_prodialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
										downlaodapk_prodialog.setCancelable(true);
										downlaodapk_prodialog.show();
										
										PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
								        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
								        mWakeLock.acquire();
										
										final DownlaodApkTask task=new DownlaodApkTask();
										if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
											task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							    		} else {
							    			task.execute();
							    		}
										
										downlaodapk_prodialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
										    @Override
										    public void onCancel(DialogInterface dialog) {
										    	task.cancel(true);
										    }
										});
									}
									else{
										ImageUtil.showAlert(SplashScreenActvity.this, getResources().getString(R.string.internet_error));
									}
									
								}
								else{
									
									Log.v(TAG, "inside the else condition");
									linear_app.setVisibility(View.GONE);
								}
								
							}
                    		
						}
						
						
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
				
                  
				}

			}
		}


@Override
public void onClick(View v) {
	
	if (btn_continue==v) {
		
		
		Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
		final String username=Prefs.getString(ImageConstant.USERNAME, "");
		final String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
		
				
				if (username.equalsIgnoreCase("") && memberid.equalsIgnoreCase("")) {
					Intent intent=new Intent(SplashScreenActvity.this, LoginNewActvity.class);
					startActivity(intent);
					finish();
				}
				else{
					
					Intent intent=new Intent(SplashScreenActvity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			
	}
}

private class DownlaodApkTask extends AsyncTask<String, Integer, String> {
	 
	  String path =Environment.getExternalStorageDirectory()+"/ApkFile/GalleryUpload/";
	  
		@Override
		protected String doInBackground(String... urls) {
			
			//String urlpath="http://api.androidhive.info/progressdialog/hive.jpg";VersionURL
				String urlpath=VersionURL;
			    try {
			        URL url = new URL(urlpath);
			        URLConnection connection = url.openConnection();
			        connection.connect();

			        int fileLength = connection.getContentLength();

			        File file = new File(path);
	                file.mkdirs();
	                File outputFile = new File(file, "GalleryUpload.apk");
	                
	                if(outputFile.exists()) {
	                	outputFile.delete();
	                }
	             
			        InputStream input = new BufferedInputStream(url.openStream());
			        OutputStream output = new FileOutputStream(outputFile);

			        byte data[] = new byte[1024];
			        long total = 0;
			        int count;
			        while ((count = input.read(data)) != -1) {
			            total += count;
			            publishProgress((int) (total * 100 / fileLength));
			            output.write(data, 0, count);
			        }

			        output.flush();
			        output.close();
			        input.close();
			    } catch (Exception e) {
			        Log.e("YourApp", "Well that didn't work out so well...");
			        Log.e("YourApp", e.getMessage());
			    }
			    
			    return path;
		
	 }
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
	        // if we get here, length is known, now set indeterminate to false
			downlaodapk_prodialog.setIndeterminate(false);
			downlaodapk_prodialog.setMax(100);
			downlaodapk_prodialog.setProgress(progress[0]);
			
		}
		
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String resultString) {
			mWakeLock.release();
			downlaodapk_prodialog.dismiss();
			Log.v(TAG, "onPostExecute called");
			
			if (resultString != null) {
				
				Intent i = new Intent();
			    i.setAction(Intent.ACTION_VIEW);
			    i.setDataAndType(Uri.fromFile(new File(path+"GalleryUpload.apk")), "application/vnd.android.package-archive" );
			    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    Log.d("Lofting", "About to install new .apk");
			    startActivity(i);
			}
			else{
				
			}
		}
	}

}
