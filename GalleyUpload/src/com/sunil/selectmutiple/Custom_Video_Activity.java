package com.sunil.selectmutiple;

import java.io.IOException;
import java.util.ArrayList;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kns.adapter.Custom_Video_Adapter;
import com.kns.model.Custom_Video_Model;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class Custom_Video_Activity extends Activity implements OnClickListener{
	
	private static final String TAG="Custom_Image_Activity";
	private Context context=null;
	private GridView gridGallery;
	List<Custom_Video_Model> list_video=new ArrayList<Custom_Video_Model>();
	private ImageButton btn_back=null;
	Custom_Video_Adapter adapter=null;
	private String cunsumerid="";
	private ImageButton btn_okay;
	ArrayList<Custom_Video_Model> selected=null;
	String videourl="";
	String videothumburl="";
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	private ProgressDialog prodeialog_add;
	private String RequestFormID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ccustom_video_screen);
		context=this;
		
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		btn_back=(ImageButton)findViewById(R.id.imageButton_back);
		btn_okay=(ImageButton)findViewById(R.id.btnGalleryOk);
		btn_back.setOnClickListener(this);
		btn_okay.setOnClickListener(this);
		
		Bundle bundle=getIntent().getExtras();
		if (bundle != null) {
			
			String jsonresponse=bundle.getString("JSONAPI");
			cunsumerid=bundle.getString("cunsumerid");
			RequestFormID=bundle.getString("RequestFormID");
			try {
				
				JSONArray jsonarray=new JSONArray(jsonresponse);
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject obj=jsonarray.getJSONObject(i);
					String status=obj.getString("Status");
					String partnerid=obj.getString("PartnerID");
					
					JSONArray array=obj.getJSONArray("Video");
					for (int j = 0; j < array.length(); j++) {
						
						JSONObject obj1=array.getJSONObject(j);
						String videourl=obj1.getString("RealVideo");
						//String AdminApprovedFlag=obj1.getString("AdminApprovedFlag");
						String updatedvideothumb="";
						boolean isupdatedvideothumb=obj1.isNull("UpdateThumbVideo");
						if (isupdatedvideothumb) {
							
						}
						else{
							updatedvideothumb=obj1.getString("UpdateThumbVideo");
						}
						String videothumburl0="";
						boolean isvideothumburl0=obj1.isNull("ThumbVideo-0");
						if (isvideothumburl0) {
							
						}
						else{
							videothumburl0=obj1.getString("ThumbVideo-0");
						}
						
						String videothumburl1="";
						boolean isvideothumburl1=obj1.isNull("ThumbVideo-1");
						if (isvideothumburl1) {
							
						}
						else{
							videothumburl1=obj1.getString("ThumbVideo-1");
						}
						
						String videothumburl2="";
						boolean isvideothumburl2=obj1.isNull("ThumbVideo-2");
						if (isvideothumburl2) {
							
						}
						else{
							videothumburl2=obj1.getString("ThumbVideo-2");
						}
						
						String videothumburl3="";
						boolean isvideothumburl3=obj1.isNull("ThumbVideo-3");
						if (isvideothumburl3) {
							
						}
						else{
							videothumburl3=obj1.getString("ThumbVideo-3");
						}
						
						
						// if admin approval flag 1 only you can add this files in the list
						
					/*	if (AdminApprovedFlag.trim().equalsIgnoreCase("1")) {
							
						}
						else{
						
							ImageUtil.galleryLog(TAG, "Thumb Video url: "+updatedvideothumb);
							Custom_Video_Model model=new Custom_Video_Model(videourl, updatedvideothumb, false);
							list_video.add(model);
						}*/
						
						//Log.v(TAG, "Thumb Video url: "+updatedvideothumb);
						ImageUtil.galleryLog(TAG, "Thumb Video url: "+updatedvideothumb);
						Custom_Video_Model model=new Custom_Video_Model(videourl, updatedvideothumb, false);
						list_video.add(model);
					}
				
				  }
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (list_video.size() > 0) {
				
			}
			else{
				
				Toast.makeText(context, "You don't have any files to add. Please upload the files first.", Toast.LENGTH_LONG).show();
			}
			
			adapter=new Custom_Video_Adapter(Custom_Video_Activity.this, list_video);
			gridGallery.setAdapter(adapter);
		}
		
		findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
		gridGallery.setOnItemClickListener(mItemMulClickListener);
		adapter.setMultiplePick(true);
	}

	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			adapter.changeSelection(v, position);

		}
	};
	
	@Override
	public void onClick(View arg0) {
		
		if (btn_back==arg0) {
		
			finish();
		}
		else if (btn_okay==arg0) {
			
			 selected = adapter.getSelected();
			
			if (selected.size() > 0) {
				
				/*for (int i = 0; i < selected.size(); i++) {
					
					Custom_Video_Model model=selected.get(i);
					 videourl=model.getVideorealurl();
					 videothumburl=model.getUpdatedvideothumb();
					
				}*/
				
				boolean isinternet=ImageUtil.isInternetOn(context);
				if (isinternet) {
					
					prodeialog_add=ProgressDialog.show(context, "", "Adding the video..");
					AddImageTask task=new AddImageTask();
					task.execute();
					
				}else{
					ImageUtil.showAlert(Custom_Video_Activity.this, getResources().getString(R.string.internet_error));
				}
				
			}
			else{
				
				Toast.makeText(context, "Please select checkbox to add video", Toast.LENGTH_LONG).show();
				
			  }
			
	    	}
		
		}


	private class AddImageTask extends AsyncTask<String, Void, String> {
		String response1 = "";
		
		@Override
		protected String doInBackground(String... urls) {
		
			Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
	//		/http://picture-video-store.com/ws/?ws=1&act=Listofcategory&MemberID=1			
			//String url=ImageConstant.BASEURLMYUPLOAD;
			
			
			String url=ImageConstant.BASEURL+"AddCustomRequest_UploadDetails";
			
	        JSONObject obj1=new JSONObject();
			
			try {
				obj1.put("MemberID", memberid);
				obj1.put("consumer_id", cunsumerid);
				obj1.put("RequestFormID", RequestFormID);
				obj1.put("totalcount", selected.size());
				for (int i = 1; i <= selected.size(); i++) {
					
					 Custom_Video_Model model=selected.get(i-1);
					 videourl=model.getVideorealurl();
					 videothumburl=model.getUpdatedvideothumb();
					 obj1.put("Source_fileName"+i, videourl);
					 obj1.put("Source_thumbfileName"+i, videothumburl);
				}
				obj1.put("Source_filetype", "video");
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		     // Log.v(TAG, "Json object is: "+obj1.toString());
		  	ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());

			  HttpParams httpParameters = new BasicHttpParams();
		      HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
		      HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
		      
			   HttpClient httpclient = new DefaultHttpClient(httpParameters);
			   //Log.v(TAG, "url is: "+url);
			   ImageUtil.galleryLog(TAG,  "url is: "+url);
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
			         //Log.v(TAG+".doInBackground", "json response code:" + responsecode);
			         ImageUtil.galleryLog(TAG,  "Http response is:" + result);
			         ImageUtil.galleryLog(TAG,   "json response code:" + responsecode);
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
			
			prodeialog_add.dismiss();
			Log.v(TAG, "onPostExecute called");
			
		try {
			if (resultString != null && !resultString.isEmpty()) {
				
				JSONObject jsonobj=new JSONObject(resultString);
				String status=jsonobj.getString("Status");
				if (status.trim().equalsIgnoreCase("Success")) {
					
					showAlert(Custom_Video_Activity.this, "Video Added Successfully.");
				}
				else{
					
					ImageUtil.showAlert(Custom_Video_Activity.this, "Same video already exist.");
				}
			} 
			else{
				
				ImageUtil.showAlert(Custom_Video_Activity.this, getResources().getString(R.string.timeout));
			 }
			}
			catch (JSONException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
			}
		}
	}
	
	public void showAlert(Activity activity, String message) {
		
	
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message);
		
		builder.setCancelable(false);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
	
				finish();
			}
	
		});
	
		AlertDialog alert = builder.create();
		alert.show();
		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}

}
