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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kns.adapter.Partner_Image_Adapter1;
import com.kns.model.PartnerImage_Model;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class LoadSetActivity extends Activity implements OnItemClickListener, OnClickListener{
	
	private final static String TAG="LoadSetActivity";
	private Context context=null;
	private TextView txt_name;
	private TextView txt_setname;
	private GridView gridview_set;
	private ProgressDialog progressdialog;
	private ProgressDialog prodialog3_uudate;
	private ProgressDialog prodialog_set;
	List<PartnerImage_Model> list_image=new ArrayList<PartnerImage_Model>();
	String partnerid="";
	String categoryid="";
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	String imagethumb="";
	String imagereal="";
	private ImageButton btn_back;
	AlertDialog alert;
	int setthumb=0;
	String approveflag="";
	String uploadedid="";
	String categoryname="";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadsetscreen);
		
		context=this;
		
		txt_name=(TextView)findViewById(R.id.textView_set);
		txt_setname=(TextView)findViewById(R.id.textView_setnameload);
		gridview_set=(GridView)findViewById(R.id.gridView_setpic);
		btn_back=(ImageButton)findViewById(R.id.imageButton_back);
		btn_back.setOnClickListener(this);
		
		Bundle bundle=getIntent().getExtras();
		if (bundle != null) {
			
			String jsonpai=bundle.getString("JSONAPI");
			categoryname=bundle.getString("categoryname");
			approveflag=bundle.getString("approveflag");
			txt_setname.setText(categoryname);
			//Log.v(TAG, "aprovedflag is: "+approveflag);
			ImageUtil.galleryLog(TAG, "aprovedflag is: "+approveflag);
			
			try {
				if (jsonpai != null && !jsonpai.isEmpty()) {
					
					 
					        JSONArray jsonaaray=new JSONArray(jsonpai);
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
					        			String UploadID=jsonobj1.getString("UploadID");
					        			
					        			
					        			PartnerImage_Model model=new PartnerImage_Model(UploadID, realurlurl, thumburl);
					        			list_image.add(model);
									}
								}
					        	
							}
							
					} 
					else{
						ImageUtil.showAlert(LoadSetActivity.this, "Unable to connect to server. Please try again.");
					  }
				}
				catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
				}
			
			Partner_Image_Adapter1 adapter=new Partner_Image_Adapter1(LoadSetActivity.this, list_image);
			gridview_set.setAdapter(adapter);
		}
		
		if (list_image.size() > 0) {
			txt_name.setVisibility(View.GONE);
			
		}else{
			txt_name.setVisibility(View.VISIBLE);
			txt_name.setText("This set does not have any picture.");
		}
	
		gridview_set.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	
		PartnerImage_Model model=list_image.get(arg2);
		 imagethumb=model.getImagethumburl();
		 imagereal=model.getImagerealurl();
		 uploadedid=model.getUploadedid();
		
		 
		 showDialogItemClick();
		 
		//showAlert(LoadSetActivity.this, "Are you sure want to delete this content?");
	}
	
	private void showDialogItemClick() {
		
		try {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose");

			final CharSequence[] choiceList = { "Expand", "Select as a cover photo for this set", "Delete"};
			int selected = -1; 

			builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int itemNO) {

							if (itemNO == 0) {
								
								setthumb=0;
							
								Intent intent=new Intent(LoadSetActivity.this, ImageViewActivity.class);
								intent.putExtra("ImageUrl", imagethumb);
								intent.putExtra("realurl", imagereal);
								startActivity(intent);
								
								alert.dismiss();
							
							} else if (itemNO == 1) {
								
								setthumb=1;
								
								boolean isinternet=ImageUtil.isInternetOn(context);
								if (isinternet) {
									
									prodialog3_uudate=ProgressDialog.show(LoadSetActivity.this, "", "Updating cover photo..");
									
									if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
						    		    new ResetThumbTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						    		} else {
						    		    new ResetThumbTask().execute();
						    		}
									
								/*	ResetThumbTask task=new ResetThumbTask();
									
									task.execute();*/
								}
								else{
									ImageUtil.showAlert(LoadSetActivity.this, getResources().getString(R.string.internet_error));
								}
								
								
								alert.dismiss();

							}
							else if (itemNO == 2) {
								
								// do stuff here for use image
								setthumb=2;
							
								
								if (approveflag.equalsIgnoreCase("1")) {
									
									ImageUtil.showAlert(LoadSetActivity.this, "This Set already live. User can't edit this set");
								}
								else{
									showAlert(LoadSetActivity.this, "Are you sure want to delete this content?");
									
								}
								
								alert.dismiss();

							}
						
						}
					}).setCancelable(false);
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			alert = builder.create();
			alert.show();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void showAlert(Activity activity, String message) {
		
	
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		//builder.setCustomTitle(title); 
		builder.setMessage(message);
		
		builder.setCancelable(false);
		builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				
				

			}

		});
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				

				// do here
				
				boolean isinternet=ImageUtil.isInternetOn(context);
				if (isinternet) {
					
					progressdialog=ProgressDialog.show(LoadSetActivity.this, "", "Deleting Content..");
					
					if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
		    		    new DeleteContentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		    		} else {
		    		    new DeleteContentTask().execute();
		    		}
					
					/*DeleteContentTask task=new DeleteContentTask();
					task.execute();*/
				}
			
				else{
					
					ImageUtil.showAlert(LoadSetActivity.this, getResources().getString(R.string.internet_error));
					
				}
			}

		});

		AlertDialog alert = builder.create();
		alert.show();
		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}

	

	 private class DeleteContentTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
				
				Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
				String url=ImageConstant.BASEURL+"DeleteImageContent";
				
				
               JSONObject obj1=new JSONObject();
				
				try {
					
					obj1.put("MemberID", memberid);
					obj1.put("totalcount", "1");
					obj1.put("Source_fileUploadID1", uploadedid);
					
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      //Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG,"Json object is: "+obj1.toString());
				
			      HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
				  
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				 //  Log.v(TAG, "url is: "+url);
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
				        // Log.v(TAG+".doInBackground", "json response code:" + responsecode);
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
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
				   
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				progressdialog.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				 ImageUtil.galleryLog(TAG,"Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
				
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Content deleted successfully")) {
							
							showAlertDelete(LoadSetActivity.this, status);
							
						
						}
						else{
							ImageUtil.showAlert(LoadSetActivity.this, status);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else{
					ImageUtil.showAlert(LoadSetActivity.this, getResources().getString(R.string.timeout));
				}

			}
		}
	 
	 public void showAlertDelete(Activity activity, String message) {
		
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			//builder.setCustomTitle(title); 
			builder.setMessage(message);
			
			builder.setCancelable(false);
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					
					boolean isinternet=ImageUtil.isInternetOn(context);
					if (isinternet) {
						
						prodialog_set=ProgressDialog.show(context, "", "Loading set..");
						

						if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    		    new LoadSetTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    		} else {
			    		    new LoadSetTask().execute();
			    		}
						
						/*LoadSetTask task=new LoadSetTask();
						task.execute();*/
					}
					else{
						ImageUtil.showAlert(LoadSetActivity.this, getResources().getString(R.string.internet_error));
					}
					
				/*	finish();
					
					Intent intent=new Intent(LoadSetActivity.this, LoadSetActivity.class);
					intent.putExtra("JSONAPI", resultString);
					intent.putExtra("categoryname", categoryname);
					intent.putExtra("approveflag", approveflag);
					startActivity(intent);*/

				}

			});

			AlertDialog alert = builder.create();
			alert.show();
			TextView messageText = (TextView) alert.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		}
	 
		private class LoadSetTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				String url=ImageConstant.BASEURL+"getsetImages";
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("cat_id", categoryid);
					
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
				         
				         ImageUtil.galleryLog(TAG+".doInBackground", "Http response is:" + result);
				         ImageUtil.galleryLog(TAG+".doInBackground", "json response code:" + responsecode);
				         
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
				
				prodialog_set.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				
				if (resultString != null && !resultString.isEmpty()) {
					
					finish();
					
					Intent intent=new Intent(LoadSetActivity.this, LoadSetActivity.class);
					intent.putExtra("JSONAPI", resultString);
					intent.putExtra("categoryname", categoryname);
					intent.putExtra("approveflag", approveflag);
					startActivity(intent);
					 
							
					} 
					else{
						ImageUtil.showAlert(LoadSetActivity.this, getResources().getString(R.string.timeout));
					  }
				
			}
		}

	@Override
	public void onClick(View arg0) {
		
		finish();
		
	}
	
	private class ResetThumbTask extends AsyncTask<String, Void, String> {
		String response1 = "";
		
		@Override
		protected String doInBackground(String... urls) {
		
			//http://picture-video-store.com/ws/?ws=1&act=updaterepcatimage&MemberID=1&origfilename=KIMG0019.JPG&cat_id=2
			Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
		/*	String splitvideourl[]=imagereal.split("/");
			String origfilename=splitvideourl[splitvideourl.length-1];
			
			String splitvideothumburl[]=imagethumb.split("/");
			String thumbfilename=splitvideothumburl[splitvideothumburl.length-1];
			Log.v(TAG, "origvideofilename is: "+origfilename);
			Log.v(TAG, "thumbfilename is: "+thumbfilename);*/
						
			//String url=ImageConstant.BASEURLMYUPLOAD;
			
			
			String url=ImageConstant.BASEURL+"updaterepcatimage";
			ImageUtil.galleryLog(TAG, "Image url for thumb: "+imagereal);
			
            JSONObject obj1=new JSONObject();
			
			try {
				obj1.put("MemberID", memberid);
				obj1.put("origfilename", imagereal);
				obj1.put("cat_id", categoryid);
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		      //Log.v(TAG, "Json object is: "+obj1.toString());
		      ImageUtil.galleryLog(TAG,"Json object is: "+obj1.toString());
			
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
			
			prodialog3_uudate.dismiss();
			Log.v(TAG, "onPostExecute called");
			//Log.v(TAG, "Response is: "+resultString);
			 ImageUtil.galleryLog(TAG, "Response is: "+resultString);
			try {
			if (resultString != null && !resultString.isEmpty()) {
				
				
				/*	JSONArray jsonarray=new JSONArray(resultString);
					for (int i = 0; i < jsonarray.length(); i++) {
						*/
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Already Added inside Reset")) {
						
							ImageUtil.showAlert(LoadSetActivity.this, "Already added this picture in a set");
							//break;
						}
						else if (status.trim().equalsIgnoreCase("Represent Set Image Updated Successfully")) {
							
							//ImageUtil.showAlert(LoadSetActivity.this, status);
							//break;
							
							showAlertupdate(LoadSetActivity.this, "Update Successful");
						}
					//}
				} 
				else{
					ImageUtil.showAlert(LoadSetActivity.this, getResources().getString(R.string.timeout));
				  }
			}
			catch (JSONException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	
	  public void showAlertupdate(Activity activity, String message) {
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(message);
			
			builder.setCancelable(false);
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();

					// Load Myupload and update the shared preference
					

					 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
					 SharedPreferences.Editor editor = Prefs.edit();
					 editor.putString(ImageConstant.UPDATECOVERPHOTO, "1");
					 editor.commit();	
					 
					
					finish();
				}

			});

			AlertDialog alert = builder.create();
			alert.show();
			TextView messageText = (TextView) alert.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		}
}
