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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.kns.adapter.Partner_Image_Adapter;
import com.kns.adapter.Partner_Video_Adapter;
import com.kns.model.PartnerImage_Model;
import com.kns.model.PartnerVideo_Model;
import com.kns.util.ExpandableTextView;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class RequestActivity extends Activity implements OnCheckedChangeListener, OnClickListener{
	
	private final String TAG="RequestActivity"; 
	private Context context=null;
	private TextView txt_name;
	private TextView txt_offer;
	private ExpandableTextView txt_description;
	private CheckBox checkbox_status;
	private CheckBox checkbox_complete;
	private CheckBox checkbox_delete;
	private ImageButton btn_reqsubmit;
	private ImageButton btn_back;
	private ImageButton btn_addimage;
	private ImageButton btn_addvideo;
	private HorizontalListView listview_image ;
	private HorizontalListView listview_video;
	private LinearLayout layout_hide;
	
	String cunsumerid="";
	String partnerid="";
	String consumername="";
	String consumerrequest="";
	String consumerammount="";
	String arrpovalflag="";
	String RequestFormID="";
	String completeflag="";
	String deleteflag="";
	
    private ProgressDialog pro_image;
    private ProgressDialog pro_video;
    private ProgressDialog pro_req_submit;
    private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	List<PartnerImage_Model> list_image=new ArrayList<PartnerImage_Model>();
	List<PartnerVideo_Model> list_video=new ArrayList<PartnerVideo_Model>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customerequestscreen);
		
		context=this;
		
		listview_image = (HorizontalListView) findViewById(R.id.listview_image);
		listview_video = (HorizontalListView) findViewById(R.id.listview_video);
		layout_hide=(LinearLayout)findViewById(R.id.linear_hide);
		
		txt_name=(TextView)findViewById(R.id.textView_name);
		txt_offer=(TextView)findViewById(R.id.textView_offer);
		txt_description=(ExpandableTextView)findViewById(R.id.textview_desc);
		checkbox_status=(CheckBox)findViewById(R.id.checkBox_status);
		checkbox_complete=(CheckBox)findViewById(R.id.checkBox_completed);
		checkbox_delete=(CheckBox)findViewById(R.id.checkBox_deleted);
		btn_back=(ImageButton)findViewById(R.id.imageButton_back);
		btn_addimage=(ImageButton)findViewById(R.id.button_addimage);
		btn_addvideo=(ImageButton)findViewById(R.id.button_addvideo);
		btn_reqsubmit=(ImageButton)findViewById(R.id.button_reqsubmit);
		btn_back.setOnClickListener(this);
		btn_addimage.setOnClickListener(this);
		btn_addvideo.setOnClickListener(this);
		btn_reqsubmit.setOnClickListener(this);
	
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			
			 cunsumerid=bundle.getString("cunsumerid");
			 partnerid=bundle.getString("partnerid");
			 consumername=bundle.getString("consumername");
			 consumerrequest=bundle.getString("consumerrequest");
			 consumerammount=bundle.getString("consumerammount");
			 arrpovalflag=bundle.getString("arrpovalflag");
			 RequestFormID=bundle.getString("RequestFormID");
			
			 txt_name.setText(consumername);
			 txt_offer.setText(consumerammount);
			 txt_description.setText(consumerrequest);
			
		}
		
		checkbox_status.setOnCheckedChangeListener(this);
		checkbox_complete.setOnCheckedChangeListener(this);
		checkbox_delete.setOnCheckedChangeListener(this);
		checkbox_complete.setVisibility(View.GONE);
		checkbox_delete.setVisibility(View.GONE);
		layout_hide.setVisibility(View.GONE);
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		if (checkbox_status.isChecked()) {
			
			layout_hide.setVisibility(View.VISIBLE);
			checkbox_complete.setVisibility(View.VISIBLE);
			checkbox_delete.setVisibility(View.VISIBLE);
		}
		else{
			
			layout_hide.setVisibility(View.GONE);
			checkbox_complete.setVisibility(View.GONE);
			checkbox_delete.setVisibility(View.GONE);
		}
		
		if (checkbox_complete.isChecked()){
			completeflag="1";
			//Toast.makeText(context, "Please submit the request for complete.", Toast.LENGTH_LONG).show();
		}else{
			completeflag="0";
		}
		 if (checkbox_complete.isChecked()) {
			deleteflag="1";
		}else{
			deleteflag="0";
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		boolean isinternet=ImageUtil.isInternetOn(context);
		if (isinternet) {
			
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
    		    new ViewCustomeContent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    		} else {
    		    new ViewCustomeContent().execute();
    		}
			/*ViewCustomeContent task=new ViewCustomeContent();
			task.execute();*/
		}
		else{
			Toast.makeText(context, getResources().getString(R.string.internet_error), Toast.LENGTH_LONG).show();
		}
		
	}


	@Override
	public void onClick(View v) {
		
		if (btn_back==v) {
			
			finish();
		}
		else if (btn_addimage==v) {
			
			boolean isinternet=ImageUtil.isInternetOn(context);
			
			if (isinternet) {
				pro_image=ProgressDialog.show(context, "", "Loading..");
				
				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    		    new MyUploadsTaskImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    		} else {
	    		    new MyUploadsTaskImage().execute();
	    		}
				
				/*MyUploadsTaskImage task=new MyUploadsTaskImage();
				task.execute();*/
			}
			else{
				ImageUtil.showAlert(RequestActivity.this, getResources().getString(R.string.internet_error));
			}
			
		}
		else if (btn_addvideo==v) {
			
			boolean isinternet=ImageUtil.isInternetOn(context);
			if (isinternet) {
				pro_video=ProgressDialog.show(context, "", "Loading..");
				
				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    		    new MyUploadsTaskVideo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    		} else {
	    		    new MyUploadsTaskVideo().execute();
	    		}
				
				/*MyUploadsTaskVideo task=new MyUploadsTaskVideo();
				task.execute();*/
			}
			else{
				ImageUtil.showAlert(RequestActivity.this, getResources().getString(R.string.internet_error));
			}
			
		}
		
		else if (btn_reqsubmit==v) {
			
			if (checkbox_complete.isChecked() || checkbox_delete.isChecked()) {
				
				if (list_image.size() > 0 || list_video.size() >0) {
					
					boolean isinternet=ImageUtil.isInternetOn(context);
					if (isinternet) {
						
						pro_req_submit=ProgressDialog.show(context, "", "Sending...");
						
						if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    		    new SubmitRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    		} else {
			    		    new SubmitRequestTask().execute();
			    		}
						
					/*	SubmitRequestTask task=new SubmitRequestTask();
						task.execute();*/
						
					}else{
						ImageUtil.showAlert(RequestActivity.this, getResources().getString(R.string.internet_error));
					}
				}
				else {
					ImageUtil.showAlert(RequestActivity.this, "Please add custom content.");
				}
				
				
			}
			else{
				
				ImageUtil.showAlert(RequestActivity.this, "Please check the status any complete or delete");
			}
			
		}
		
	}
	
	  private class MyUploadsTaskImage extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
				
				Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				//http://picture-video-store.com/ws/?ws=1&act=getIV&MemberID=1
				
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
				
				pro_image.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				 ImageUtil.galleryLog(TAG, "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					Intent intent=new Intent(RequestActivity.this, Custom_Image_Activity.class);
					intent.putExtra("JSONAPI", resultString);
					intent.putExtra("cunsumerid", cunsumerid);
					intent.putExtra("RequestFormID", RequestFormID);
					startActivity(intent);
				}
				else{
					ImageUtil.showAlert(RequestActivity.this, getResources().getString(R.string.timeout));
				}

			}
		}
	  
	  
	  private class MyUploadsTaskVideo extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
				
				Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				//http://picture-video-store.com/ws/?ws=1&act=getIV&MemberID=1
				
				String url=ImageConstant.BASEURL+"getIVUpdate";
		
                JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      //Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG,  "Json object is: "+obj1.toString());
				
			      HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
				  
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				  // Log.v(TAG, "url is: "+url);
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
				    	//Log.v(TAG+".doInBackground", "Http response is:" + result);
				        // Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         
				         ImageUtil.galleryLog(TAG,  "Http response is:" + result);
				         ImageUtil.galleryLog(TAG,  "json response code:" + responsecode);
				         
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
				
				pro_video.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				ImageUtil.galleryLog(TAG, "Response is: "+resultString);
				  
				if (resultString != null && !resultString.isEmpty()) {
					
					Intent intent=new Intent(RequestActivity.this, Custom_Video_Activity.class);
					intent.putExtra("JSONAPI", resultString);
					intent.putExtra("cunsumerid", cunsumerid);
					intent.putExtra("RequestFormID", RequestFormID);
					startActivity(intent);
				}
				else{
					ImageUtil.showAlert(RequestActivity.this, getResources().getString(R.string.timeout));
				}

			}
		}
	  
	  private class ViewCustomeContent extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
				
				Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				//http://picture-video-store.com/ws/?ws=1&act=getIV&MemberID=1
				
				String url=ImageConstant.BASEURL+"getCustomRequest_Uploaddetails";
				
                JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
				    obj1.put("consumer_id", cunsumerid);
				    obj1.put("RequestFormID", RequestFormID);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      //Log.v(TAG, "Json object is: "+obj1.toString());
			  	ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
				
				   HttpClient httpclient = new DefaultHttpClient();
				  // Log.v(TAG, "url is: "+url);
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
				    	//Log.v(TAG+".doInBackground", "Http response is:" + result);
				        // Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         
				         ImageUtil.galleryLog(TAG,  "Http response is:" + result);
				         ImageUtil.galleryLog(TAG,  "json response code:" + responsecode);
				         
				        if (responsecode==200) {
				        	
				        	response1 = result;  
				        	
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
				//Log.v(TAG, "Response is: "+resultString);
				  ImageUtil.galleryLog(TAG,  "Response is: "+resultString);
				if (resultString != null) {
					list_image.clear();
					list_video.clear();
					try {
						JSONArray jsnarray=new JSONArray(resultString);
						for (int i = 0; i < jsnarray.length(); i++) {
							
							JSONObject jsonob=jsnarray.getJSONObject(i);
							boolean isstatus=jsonob.isNull("Status");
							if (!isstatus) {
								
								String getstatus=jsonob.getString("Status");
								//Toast.makeText(context, getstatus, Toast.LENGTH_LONG).show();
								//ImageUtil.showAlert(RequestActivity.this, getstatus);
							}
							else{
								
								JSONArray jsonarray=jsonob.getJSONArray("CustomRequestUploadDetails");
								for (int j = 0; j < jsonarray.length(); j++) {
									
									JSONObject obj=jsonarray.getJSONObject(j);
									String status=obj.getString("Status");
									if (status.trim().equalsIgnoreCase("Success")) {
										
										String uploadid=obj.getString("UploadID");
										String PartnerID=obj.getString("PartnerID");
										String ConsumerID=obj.getString("ConsumerID");
										String SourceFileName=obj.getString("SourceFileName");
										String sourceThumbFileName=obj.getString("sourceThumbFileName");
										String SourceFileType=obj.getString("SourceFileType");
										String ApprovalFlag=obj.getString("ApprovalFlag");
										
										if (SourceFileType.trim().equalsIgnoreCase("image")) {
											PartnerImage_Model model=new PartnerImage_Model("", SourceFileName, sourceThumbFileName);
											list_image.add(model);
										}
										else{
											PartnerVideo_Model model=new PartnerVideo_Model(sourceThumbFileName, SourceFileName, "", "", "", "");
											list_video.add(model);
										}
										
									}
								}
							}
						
							
						}
					
						
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					//Log.v(TAG, "List of images: "+list_image.size());
					 ImageUtil.galleryLog(TAG,  "List of images: "+list_image.size());
					Partner_Image_Adapter adapter=new Partner_Image_Adapter(RequestActivity.this, list_image);
					listview_image.setAdapter(adapter);
					
					Partner_Video_Adapter adapter1=new Partner_Video_Adapter(RequestActivity.this, list_video);
					listview_video.setAdapter(adapter1);
				}

			}
		}

	  private class SubmitRequestTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
				
				Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				//http://picture-video-store.com/ws/?ws=1&act=getIV&MemberID=1
				
				String url=ImageConstant.BASEURL+"UpdatecustomRequestStatus";
				
                JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("RequestFormID", RequestFormID);
					obj1.put("Conumser_ID", cunsumerid);
					obj1.put("Request_Status_Flag", completeflag);
					obj1.put("Request_Delete_Flag", deleteflag);
					
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
				   ImageUtil.galleryLog(TAG,   "url is: "+url);
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
				       //  Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         ImageUtil.galleryLog(TAG,    "Http response is:" + result);
				         ImageUtil.galleryLog(TAG,   "json response code:" + responsecode);
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
				
				pro_req_submit.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				ImageUtil.galleryLog(TAG,    "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
				
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Custom Request Status updated Successfully")) {
							//ImageUtil.showAlert(RequestActivity.this, "Request sent to Admin to update.");
							showAlert(RequestActivity.this, "Request sent to Admin to update.");
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
				else{
					ImageUtil.showAlert(RequestActivity.this, getResources().getString(R.string.timeout));
				}

			}
		}
	  
	  public void showAlert(Activity activity, String message) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			//builder.setCustomTitle(title); 
			builder.setMessage(message);
			
			builder.setCancelable(false);
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();

					finish();
					
				}

			});

			AlertDialog alert = builder.create();
			alert.show();
			TextView messageText = (TextView) alert.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		}
	  
}
