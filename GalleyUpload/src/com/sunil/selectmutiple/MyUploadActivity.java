package com.sunil.selectmutiple;

import java.io.IOException;
import java.util.List;

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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.kns.adapter.TabPagerAdapter;
import com.kns.model.CategoryModel;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class MyUploadActivity extends FragmentActivity{

	private final String TAG="MyUploadActivity";
	 ViewPager Tab;
	 TabPagerAdapter TabAdapter;
	 ActionBar actionBar;
	 private static SharedPreferences Prefs = null;
	 private static String prefname = "galleryPrefs";
	 private ProgressDialog prodialog_myupload;
	 private Context context;
	// Fragment fragment;
	 
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.myuploads);
		
		context=this;
		
		Bundle bundle=getIntent().getExtras();
		if (bundle != null) {
			  String jsonres=bundle.getString("JSONAPI");
			  Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
			  SharedPreferences.Editor editor = Prefs.edit();
			  editor.putString(ImageConstant.MYUPLOAD, jsonres);
			  editor.commit();
		}
		
		    TabAdapter = new TabPagerAdapter(getSupportFragmentManager(), MyUploadActivity.this);
	        Tab = (ViewPager)findViewById(R.id.pager);
	        Tab.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
	                    @Override
	                    public void onPageSelected(int position) {
	                    	
	                 //   Toast.makeText(context, "Tab postion is: "+position, Toast.LENGTH_LONG).show();	
	                      
	                      actionBar = getActionBar();
	                      
	                      actionBar.setSelectedNavigationItem(position);  
	                      if (position==2) {
	                    	  //Toast.makeText(context, "Picture sets require a minimum of 10 pictures to go live in the store.", Toast.LENGTH_LONG).show();
	                    	  String checkstatus=Prefs.getString(ImageConstant.CHECKBOXPICTURESTATUS_SET, "");
	                 		 if (checkstatus.equalsIgnoreCase("not checked") || checkstatus.equalsIgnoreCase("")) {
	                 			
	                 			 attentionBox();
	                 		}
	                    	 
	                      }
	                      
	                     }
	                });
	        Tab.setAdapter(TabAdapter);
	        actionBar = getActionBar();
	        actionBar.setTitle("My Uploads");
	        actionBar.setDisplayShowTitleEnabled(true);
	        actionBar.setDisplayShowHomeEnabled(true);
	        //Enable Tabs on Action Bar
	        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	        ActionBar.TabListener tabListener = new ActionBar.TabListener(){
	    
		@Override
		public void onTabReselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
			
		}
		@Override
		public void onTabSelected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
			
			Tab.setCurrentItem(tab.getPosition());
			//ft.replace(R.id.fragment_container, fragment);
		}
		@Override
		public void onTabUnselected(android.app.ActionBar.Tab tab, FragmentTransaction ft) {
			
		}};
	      //Add New Tab
	      actionBar.addTab(actionBar.newTab().setText("Pictures").setTabListener(tabListener));
	      actionBar.addTab(actionBar.newTab().setText("Videos").setTabListener(tabListener));
	      actionBar.addTab(actionBar.newTab().setText("Picture Sets").setTabListener(tabListener));
	      
	  	 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
         String videostatus=Prefs.getString(ImageConstant.UPDATEVIDEO, "");
         String picturestatus=Prefs.getString(ImageConstant.UPDATEPICTURE, ""); 
         String setstatus=Prefs.getString(ImageConstant.UPDATESET, "");
         if (videostatus.equalsIgnoreCase("1")) {
        	
        
        		 Tab.setCurrentItem(1);
 	        	 //Toast.makeText(getApplicationContext(), "if tab position is: "+1, Toast.LENGTH_LONG).show();

 	        	 Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
 				 SharedPreferences.Editor editor = Prefs.edit();
 				 editor.putString(ImageConstant.UPDATEVIDEO, "0");
 				 editor.commit();	
			
        	
         }
         else if (picturestatus.equalsIgnoreCase("1")) {
        	
        	     Tab.setCurrentItem(0);

	        	 Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
				 SharedPreferences.Editor editor = Prefs.edit();
				 editor.putString(ImageConstant.UPDATEPICTURE, "0");
				 editor.commit();	
		
		}
         else if (setstatus.equalsIgnoreCase("1")) {
			
             Tab.setCurrentItem(2);

        	 Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
			 SharedPreferences.Editor editor = Prefs.edit();
			 editor.putString(ImageConstant.UPDATESET, "0");
			 editor.commit();	
	
		}
         else{
        	 Tab.setCurrentItem(0);
        	 //Toast.makeText(getApplicationContext(), "else tab position is: "+0, Toast.LENGTH_LONG).show();
         }
	      
	      //actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic__home).setTabListener(tabListener));
	     
	    /*  actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_mypic).setTabListener(tabListener));
	      actionBar.addTab(actionBar.newTab().setIcon(R.drawable.ic_myvideo).setTabListener(tabListener));*/
	     
	    }
	
	@Override
	protected void onResume() {
		super.onResume();
		
		boolean isinternet=ImageUtil.isInternetOn(context);
		if (isinternet) {
			
		}
	}
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	  } 
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // action with ID action_refresh was selected
	    case R.id.action_refresh:
	   
	    	// here is the add the refresh option
	    	
	    	 Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
			 SharedPreferences.Editor editor = Prefs.edit();
			 editor.putString(ImageConstant.UPDATEVIDEO, "0");
			 editor.commit();	
	    	
			 finish();
	    	
	      break;
	      
	    case R.id.action_home:
	 	   
	    	// here is the add the refresh option
	    	boolean isinternet=ImageUtil.isInternetOn(context);
	    	if (isinternet) {
	    		prodialog_myupload=ProgressDialog.show(context, "", "Refreshing page...");
	    		MyUploadedTask task=new MyUploadedTask();
	    		task.execute();
			}
	    	else{
	    		ImageUtil.showAlert(MyUploadActivity.this, getResources().getString(R.string.internet_error));
	    	}
	    	
	    	
	      break;
	  
	    default:
	      break;
	    }

	    return true;
	  } 
	
	 private class MyUploadedTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
				
				Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
				//String url="http://picture-video-store.com/ws/?";
				
				String url=ImageConstant.BASEURL+"getIVUpdate";
				
				
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
				//   Log.v(TAG, "url is: "+url);
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
				        	
				        	response1 = result;  
				        	
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
				
				prodialog_myupload.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				ImageUtil.galleryLog(TAG, "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					Intent intent=new Intent(MyUploadActivity.this, MyUploadActivity.class);
					intent.putExtra("JSONAPI", resultString);
					startActivity(intent);
					
					finish();
				}
				else{
					ImageUtil.showAlert(MyUploadActivity.this, getResources().getString(R.string.timeout));
				}

			}
		}
	 
	  public void attentionBox(){
			
			AlertDialog.Builder adb=new AlertDialog.Builder(this);
		    LayoutInflater adbInflater = LayoutInflater.from(this);
		    View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
		    final CheckBox dontShowAgain = (CheckBox)eulaLayout.findViewById(R.id.skip);
		    adb.setView(eulaLayout);
		    adb.setTitle("Attention");
		    adb.setMessage("Picture sets require a minimum of 10 pictures to go live in the store.");
	    	adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	    	      public void onClick(DialogInterface dialog, int which) {
	    	    	
	    	    	  Prefs =getSharedPreferences(prefname, Context.MODE_PRIVATE);
	    	    	  if (dontShowAgain.isChecked())  {
	    	    
						 SharedPreferences.Editor editor = Prefs.edit();
						 editor.putString(ImageConstant.CHECKBOXPICTURESTATUS_SET, "checked");
						 editor.commit();	
						 
	    	    	  }
	    	    	  else{
	    	    		  
	 					 SharedPreferences.Editor editor = Prefs.edit();
	 					 editor.putString(ImageConstant.CHECKBOXPICTURESTATUS_SET, "not checked");
	 					 editor.commit();	
	    	    	  }
	    	    	  
	    	    	  return;  
	    	      } });
	 
	    	  adb.show();
		}
}
