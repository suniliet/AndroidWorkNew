package com.sunil.selectmutiple;

import java.io.IOException;
import java.util.ArrayList;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kns.adapter.Custom_Image_Adapter;
import com.kns.model.Custom_Image_Model;
import com.kns.model.PartnerImage_Model;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class SeletctMultipleFile extends Activity implements OnClickListener{
	
	private static final String TAG="SeletctMultipleFile";
	private Context context=null;
	private ImageButton btn_add;
	private GridView gridview_selectmultiple;
	//private GridView gridview_viewset;
	List<Custom_Image_Model> list_images=new ArrayList<Custom_Image_Model>();
	Custom_Image_Adapter adapter=null;
	private ImageButton btn_okay;
	private ImageButton btn_back;
	ArrayList<Custom_Image_Model> selected=null;
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	private ProgressDialog prodeialog_add;
	List<Custom_Image_Model> list;
	String imageurl="";
	String imagethmb="";
	String setid="";
	List<PartnerImage_Model> list_images_set=new ArrayList<PartnerImage_Model>();
	String partnerid="";
	String categoryid="";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.addmultipleimage);
	        context=this;
	        
	        btn_add=(ImageButton)findViewById(R.id.imageButton_add);
	        gridview_selectmultiple=(GridView)findViewById(R.id.gridView_selectimage);
	        btn_back=(ImageButton)findViewById(R.id.imageButton_backmulti);
	       // gridview_viewset=(GridView)findViewById(R.id.gridView_viewser);
	     //   findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
	        
	        btn_add.setOnClickListener(this);
	        btn_back.setOnClickListener(this);
	        
	    	Bundle bundle=getIntent().getExtras();
			if (bundle != null) {
				
				String jsonresponse=bundle.getString("JSONAPI");
				setid=bundle.getString("setid");
				Log.v(TAG, "set id is: "+setid);
				try {
					list_images.clear();
					JSONArray jsonarray=new JSONArray(jsonresponse);
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject obj=jsonarray.getJSONObject(i);
						String status=obj.getString("Status");
						String partnerid=obj.getString("PartnerID");
						
						JSONArray array=obj.getJSONArray("Image");
						for (int j = 0; j < array.length(); j++) {
							
							JSONObject obj1=array.getJSONObject(j);
							String imageurl=obj1.getString("RealImage");
							String imagethumburl=obj1.getString("ThumbImage");
							
							Custom_Image_Model model=new Custom_Image_Model(imageurl, imagethumburl, false);
							list_images.add(model);
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}  
				
				adapter=new Custom_Image_Adapter(SeletctMultipleFile.this, list_images);
				gridview_selectmultiple.setAdapter(adapter);
				adapter.setMultiplePick(true);
			}
			
			gridview_selectmultiple.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
					
					adapter.changeSelection(v, position);
				}
			});
			
		/*	LoadSetTask task=new LoadSetTask();
			task.execute();*/
		
	}

	@Override
	public void onClick(View v) {
		
		if (btn_add==v) {
			//here to call service to add pics
			
			list=adapter.getSelected();
			
			boolean isinternet=ImageUtil.isInternetOn(getApplicationContext());
			if (isinternet) {
				
				prodeialog_add=ProgressDialog.show(context, "", "Adding files..");
				
				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    		    new AddImageMultiple().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    		} else {
	    		    new AddImageMultiple().execute();
	    		}
				
				/*AddImageMultiple task=new AddImageMultiple();
				task.execute();*/
			} 
			else{
				ImageUtil.showAlert(SeletctMultipleFile.this, getResources().getString(R.string.internet_error));
			}
			
			
		}
		else if (btn_back==v) {
			finish();
		}
		
	}
	
	
	private class AddImageMultiple extends AsyncTask<String, Void, String> {
		String response1 = "";
		
		@Override
		protected String doInBackground(String... urls) {
		
			Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
			String url=ImageConstant.BASEURL+"AddMultipleImageSet";
			
            JSONObject obj1=new JSONObject();
            
			try {
				obj1.put("MemberID", memberid);
				obj1.put("cat_id", setid);
				obj1.put("totalcount", list.size());
				for (int i = 1; i <= list.size(); i++) {
					
					Custom_Image_Model model=list.get(i-1);
					String imageurl=model.getImagerealurl();
					String thumburl=model.getImagethumburl();
					obj1.put("Source_fileName"+i, imageurl);
					obj1.put("Source_thumbfileName"+i, thumburl);
				}
				
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		     // Log.v(TAG, "Json object is: "+obj1.toString());
		  	ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
			
			   HttpClient httpclient = new DefaultHttpClient();
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
			//Log.v(TAG, "Response is: "+resultString);
			ImageUtil.galleryLog(TAG,"Response is: "+resultString);
			
			if (resultString != null && !resultString.isEmpty()) {
				
			try{
				
			      JSONObject jsonobj=new JSONObject(resultString);
			      String status=jsonobj.getString("Status");
			      if (status.trim().equalsIgnoreCase("Success")) {
			    	  
			    	  //"Status":"Success","PartnerID":"1","SetId":"2","ImageCount":10
			    	  showAlert(SeletctMultipleFile.this, "Files added successfully");
				}
					
			}catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	  }
	}
	
	public  void showAlert(Activity activity, String message) {
		
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message);
		
		builder.setCancelable(false);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();

				// here you can update the upload task
				
				 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				 SharedPreferences.Editor editor = Prefs.edit();
				 editor.putString(ImageConstant.ADDMOREPIC, "1");
				 editor.commit();	
				 
				
				finish();
				
				
			}

		});

		AlertDialog alert = builder.create();
		alert.show();
		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}
	
	
	
	
/*	private class LoadSetTask extends AsyncTask<String, Void, String> {
		String response1 = "";
		
		@Override
		protected String doInBackground(String... urls) {
		
			Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
			String url=ImageConstant.BASEURL+"getsetImages";
			
            JSONObject obj1=new JSONObject();
			
			try {
				obj1.put("MemberID", memberid);
				obj1.put("cat_id", setid);
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		      Log.v(TAG, "Json object is: "+obj1.toString());
			
			   HttpClient httpclient = new DefaultHttpClient();
			   Log.v(TAG, "url is: "+url);
			   HttpPost  httppost = new HttpPost(url);
			 
			   try {

				   StringEntity se = new StringEntity(obj1.toString());
				   httppost.setEntity(se);  
				   httppost.setHeader("Accept", "application/json");
				   httppost.setHeader("Content-type", "application/json");

			        HttpResponse response = httpclient.execute(httppost);
			        int responsecode=response.getStatusLine().getStatusCode();
			        String result = EntityUtils.toString(response.getEntity());   
			        Log.v(TAG+".doInBackground", "Http response is:" + result);
			         Log.v(TAG+".doInBackground", "json response code:" + responsecode);
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
			
		
			Log.v(TAG, "onPostExecute called");
			Log.v(TAG, "Response is: "+resultString);
			
			if (resultString != null && !resultString.isEmpty()) {
				
				list_images_set.clear();
				try{
					
				
		        JSONArray jsonaaray=new JSONArray(resultString);
		        for (int i = 0; i < jsonaaray.length(); i++) {
		        	
		        	JSONObject jsonobj=jsonaaray.getJSONObject(i);
		        	String status=jsonobj.getString("Status");
		        	if (status.trim().equalsIgnoreCase("Represent Set Images List")) {
						
		        		 partnerid=jsonobj.getString("PartnerID");
		        		 categoryid=jsonobj.getString("CatId");
		        		
		        		
		        		JSONArray jsonarray1=jsonobj.getJSONArray("SetImages");
		        		for (int j = 0; j < jsonarray1.length(); j++) {
							
		        			JSONObject jsonobj1=jsonarray1.getJSONObject(j);
		        			String thumburl=jsonobj1.getString("Resetthumbfilename");
		        			String realurlurl=jsonobj1.getString("Resetoriginalfilename");
		        			
		        			PartnerImage_Model model=new PartnerImage_Model(realurlurl, thumburl);
		        			list_images_set.add(model);
						}
					}
		        	
				}
		        
		        Partner_Image_Adapter adapter=new Partner_Image_Adapter(SeletctMultipleFile.this, list_images_set);
				gridview_viewset.setAdapter(adapter);
				 
					
			}catch (JSONException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	  }
	}*/
}
