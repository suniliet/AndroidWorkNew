package com.kns.fragment;

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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kns.adapter.CategoryAdapter;
import com.kns.adapter.Muti_SelectVideoAdapter;
import com.kns.adapter.Update_Thumb_Adapter;
import com.kns.db.DBHelper;
import com.kns.model.CategoryModel;
import com.kns.model.MultiSelectVideoModel;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;
import com.kns.util.NothingSelectedSpinnerAdapter;
import com.sunil.selectmutiple.MyUploadActivity;
import com.sunil.selectmutiple.R;

public class MyVideoFragment extends Fragment implements OnItemClickListener, OnClickListener, OnItemSelectedListener {

	private final static String TAG="MyVideoFragment";
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	private GridView gridview=null;
	List<MultiSelectVideoModel> list_video=new ArrayList<MultiSelectVideoModel>();
	TextView textviwe;
	private ProgressDialog prodialog_myupload;
	private ProgressDialog prodoalog_cat;
	private ProgressDialog prodialog;
	private ProgressDialog prodialog1;
	private ProgressDialog prodialog_addedit;
	private ProgressDialog prodialog_delete;
	private ProgressDialog prodialog_addvideo;
	private ProgressDialog prodialog_cat;
	private ProgressDialog prodialog_videothumb;
	
	private ImageButton btn_addeditcate;
	private ImageButton btn_delete;
	private Spinner spinner_select;
	private EditText editvideoname;
	
	String videourl;
	String thumburl;
	String deletevideourl="";
	String deletethumburl="";
	AlertDialog dialog;
	private AlertDialog alert = null;
	List<CategoryModel> list_category=new ArrayList<CategoryModel>();
	Muti_SelectVideoAdapter adapter=null;
	ArrayList<MultiSelectVideoModel> selected=null;
	List<CategoryModel> list;
	ArrayList<CategoryModel> checked_category=null;
	private DBHelper db;
	String uploadedid="";
	
	String videoname="";
	private String videoid="";
	
	 CategoryAdapter newadapter;
	
	List<String> list_thumb=new ArrayList<String>();
	
	String array_select[]={"Select Multiple", "Select All", "UnSelect All", "1-5", "1-10", "1-20"};
	String selectedvalue="";
	
	
	 @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.myvideouploaded, container, false);
         gridview=(GridView)view.findViewById(R.id.gridView_video);
         //gridview.setOnItemLongClickListener(this);
        // 
         textviwe= (TextView)view.findViewById(R.id.textView);
         btn_addeditcate=(ImageButton)view.findViewById(R.id.button_addeditcat);
         btn_delete=(ImageButton)view.findViewById(R.id.button_delete);
         spinner_select= (Spinner)view.findViewById(R.id.spinner_selectvideo);
         spinner_select.setOnItemSelectedListener(this);
         
         btn_addeditcate.setOnClickListener(this);
         btn_delete.setOnClickListener(this);
         
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, array_select);
 		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 		spinner_select.setAdapter(new NothingSelectedSpinnerAdapter(dataAdapter,
	            R.layout.contact_spinner_row_nothing_selected, getActivity()));
 		//spinner_select.setAdapter(dataAdapter);
         
         gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				
				/*if (selectedvalue.trim().equalsIgnoreCase("Unchecked All")) {
					
					Toast.makeText(getActivity(), "Please select multiple option to checked the file.", Toast.LENGTH_LONG).show();
				}
				else{*/
				adapter.changeSelection(v, position);
				//}
				
			}
		});
        
         gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
				// list_thumb.clear();
					
				 MultiSelectVideoModel model=list_video.get(arg2);
				 videourl=model.getVideorealurl();
				 videoid=model.getUploadedid();
				 String updatedthumb=model.getUpdatedvideothumb();
				 String thumburl=model.getVideothumburl0();
				 String thumburl1=model.getVideothumburl1();
				 String thumburl2=model.getVideothumburl2();
				 String thumburl3=model.getVideothumburl3();
				 
				 deletevideourl=model.getVideorealurl();
				 deletethumburl=model.getUpdatedvideothumb();
				 uploadedid=model.getUploadedid();
					
				 
			/*	// list_thumb.add(updatedthumb);
				 list_thumb.add(thumburl);
				 list_thumb.add(thumburl1);
				 list_thumb.add(thumburl2);
				 list_thumb.add(thumburl3);
				//Toast.makeText(getActivity(), "Item click", Toast.LENGTH_LONG).show();
				 */
				 showDialogItemClick();
				
				return false;
			}
		});
       
         return view;
    }
	 
	     @Override
	    public void onActivityCreated(Bundle savedInstanceState)
	    {
	        super.onActivityCreated(savedInstanceState);
	        Activity activity = getActivity();
	        db=new DBHelper(getActivity());
	        Prefs = activity.getSharedPreferences(prefname, Context.MODE_PRIVATE);
	        
	         String jsonresp=Prefs.getString(ImageConstant.MYUPLOAD, "");
	         if (jsonresp.equalsIgnoreCase("")) {
	 			
	 		}
	         else{
	         	
	         	// parse the json here
	        	 	if (jsonresp != null) {
	    				
	    				try {
	    					
	    					
	    					JSONArray jsonarray=new JSONArray(jsonresp);
	    					for (int i = 0; i < jsonarray.length(); i++) {
	    						JSONObject obj=jsonarray.getJSONObject(i);
	    						String status=obj.getString("Status");
	    						String partnerid=obj.getString("PartnerID");
	    						
	    						JSONArray array=obj.getJSONArray("Video");
	    						for (int j = 0; j < array.length(); j++) {
	    							
	    							JSONObject obj1=array.getJSONObject(j);
	    							String videourl=obj1.getString("RealVideo");
	    							String uploadedid=obj1.getString("UploadID");
	    							String AdminApprovedFlag=obj1.getString("AdminApprovedFlag");
	    							String videoname=obj1.getString("VideoName");
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
	    							
	    							
	    							//Log.v(TAG, "Thumb Video url: "+updatedvideothumb);
	    							 ImageUtil.galleryLog(TAG, "Thumb Video url: "+updatedvideothumb);
	    							MultiSelectVideoModel model=new MultiSelectVideoModel(uploadedid, videoname, updatedvideothumb, videourl, videothumburl0, videothumburl1, videothumburl2, videothumburl3, false, AdminApprovedFlag);
	    							list_video.add(model);
	    						}
	    					
	    					
	    				/*	
	    					JSONObject jsonobj=new JSONObject(jsonresp);
	    					String status=jsonobj.getString("Status");
	    					if (status.trim().equalsIgnoreCase("Partner Details")) {
	    				
	    						JSONArray jsonarray2=jsonobj.getJSONArray("Video");
	    						for (int i = 0; i < jsonarray2.length(); i++) {
	    							
	    							JSONObject obj=jsonarray2.getJSONObject(i);
	    							String videourl=obj.getString("RealVideo");
	    							
	    							// here I am checking that if key available or not.
	    							String updatedvideothumb="";
	    							boolean isupdatedvideothumb=obj.isNull("UpdateThumbVideo");
	    							if (isupdatedvideothumb) {
										
									}
	    							else{
	    								updatedvideothumb=obj.getString("UpdateThumbVideo");
	    							}
	    							
	    							String videothumburl0="";
	    							boolean isvideothumburl0=obj.isNull("ThumbVideo-0");
	    							if (isvideothumburl0) {
										
									}
	    							else{
	    								videothumburl0=obj.getString("ThumbVideo-0");
	    							}
	    							
	    							String videothumburl1="";
	    							boolean isvideothumburl1=obj.isNull("ThumbVideo-1");
	    							if (isvideothumburl1) {
										
									}
	    							else{
	    								videothumburl1=obj.getString("ThumbVideo-1");
	    							}
	    							
	    							String videothumburl2="";
	    							boolean isvideothumburl2=obj.isNull("ThumbVideo-2");
	    							if (isvideothumburl2) {
										
									}
	    							else{
	    								videothumburl2=obj.getString("ThumbVideo-2");
	    							}
	    							
	    							String videothumburl3="";
	    							boolean isvideothumburl3=obj.isNull("ThumbVideo-3");
	    							if (isvideothumburl3) {
										
									}
	    							else{
	    								videothumburl3=obj.getString("ThumbVideo-3");
	    							}
	    							
	    							
	    							//String videothumburl0=obj.getString("ThumbVideo-0");
	    							//String videothumburl1=obj.getString("ThumbVideo-1");
	    							//String videothumburl2=obj.getString("ThumbVideo-2");
	    							//String videothumburl3=obj.getString("ThumbVideo-3");
	    							
	    							PartnerVideo_Model model=new PartnerVideo_Model(updatedvideothumb, videourl, videothumburl0, videothumburl1, videothumburl2, videothumburl3);
	    							list_video.add(model);
	    						}*/
	    					  }
	    					
	    				} catch (JSONException e) {
	    					e.printStackTrace();
	    				}
	    				if (list_video.size() > 0) {
	    					textviwe.setVisibility(View.GONE);
	    				}
	    				else{
	    					textviwe.setText(getActivity().getResources().getString(R.string.not_yet_uploaded));
	    				}
	    				adapter=new Muti_SelectVideoAdapter(getActivity(), list_video);
	    				adapter.setMultiplePick(true);
	    				gridview.setAdapter(adapter);
	    				
	    			}
	         }
	        
		        
	    	//Toast.makeText(getActivity(), "onActivityCreated", Toast.LENGTH_LONG).show();
	    }

	    @Override
	    public void onAttach(Activity activity)
	    {
	        super.onAttach(activity);
	        //Toast.makeText(getActivity(), "onAttach", Toast.LENGTH_LONG).show();
	    }

	    @Override
	    public void onStart()
	    {
	        super.onStart();
	       // Toast.makeText(getActivity(), "onStart", Toast.LENGTH_LONG).show();
	        
	    }

	    @Override
	    public void onResume()
	    {
	        super.onResume();
	        //Toast.makeText(getActivity(), "onResume", Toast.LENGTH_LONG).show();
	    }
	
/*	public void showAlertupdate(Activity activity, String message) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message);
		
		builder.setCancelable(false);
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();

			}

		});
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();

				//here you can send the alert to admin for delete the video
				
			
			}

		});

		AlertDialog alert = builder.create();
		alert.show();
		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}*/

	public void showAlertDelete(Activity activity, String message) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message);
		
		builder.setCancelable(false);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();

				boolean ispendingall=false;
				selected = adapter.getSelected();
				for (int i = 0; i < selected.size(); i++) {
					
					MultiSelectVideoModel model=selected.get(i);
					String admiapproved=model.getAdminApprovedFlag();
					if (admiapproved.trim().equalsIgnoreCase("1")) {
						
					
						ispendingall=true;
						break;
						
					}
					else{
						
					}
				}
				
				if (ispendingall) {
					
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.select_onepending_todelete));
				}
				else{
					
					//here you can send the alert to admin for delete the video
					boolean isinternet=ImageUtil.isInternetOn(getActivity());
					if (isinternet) {
						
						prodialog_delete=ProgressDialog.show(getActivity(), "", "Deleting...");
						/*DeleteMultipleVideo task=new DeleteMultipleVideo();
						task.execute();*/
						
						if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    		    new DeleteMultipleVideo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    		} else {
			    		    new DeleteMultipleVideo().execute();
			    		}
					}
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
					}
				}

				
			}

		});
		builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				
			
			    
			}

		});

		AlertDialog alert = builder.create();
		alert.show();
		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}
	
	 private class UpdateTumb extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				//http://picture-video-store.com/ws/?ws=1&act=UpdateVideoThumb&MemberID=1&thumbfilename=MOV_0226_001.jpg&origvideofilename=MOV_0226.mp4
				//http://picture-video-store.com/ws/?ws=1&act=fetchWatchlist&consumer_id=1
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
			/*	String splitvideourl[]=videourl.split("/");
				String origvideofilename=splitvideourl[splitvideourl.length-1];
				
				String splitvideothumburl[]=thumburl.split("/");
				String thumbfilename=splitvideothumburl[splitvideothumburl.length-1];
				Log.v(TAG, "origvideofilename is: "+origvideofilename);
				Log.v(TAG, "thumbfilename is: "+thumbfilename);
				*/
				//String url=ImageConstant.BASEURLMYUPLOAD;
				
				String url=ImageConstant.BASEURL+"UpdateVideoThumb";
				
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("thumbfilename", thumburl);
					obj1.put("origvideofilename", videourl);
					
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
				      //   Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         
				         ImageUtil.galleryLog(TAG, "Http response is:" + result);
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
				
				prodialog.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				ImageUtil.galleryLog(TAG,   "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Thumb Video file updated Successfully")) {
							
						/*	Toast.makeText(getActivity(), "Thumbnail updated successfully.", Toast.LENGTH_LONG).show();
							Intent intent=new Intent(getActivity(), MainActivity.class);
							startActivity(intent);*/
							
							// here refresh the the video area.
							showAlert(getActivity(), getActivity().getResources().getString(R.string.thumb_updated));
							
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else{
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
				}
			}
		}
	 

	public void showAlert(Activity activity, String message) {
			
		
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			//builder.setCustomTitle(title); 
			builder.setMessage(message);
			
			builder.setCancelable(false);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					
					// do here to refresh the video tab
				
					
					boolean isinternet=ImageUtil.isInternetOn(getActivity());
					if (isinternet) {

						prodialog_myupload=ProgressDialog.show(getActivity(), "", "Loading...");
						/*MyUploadedTask task=new MyUploadedTask();
						task.execute();*/
						
						if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    		    new MyUploadedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    		} else {
			    		    new MyUploadedTask().execute();
			    		}
					} 
					else{
						
						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.internet_error), Toast.LENGTH_LONG).show();
					}
					
					
				}

			});

			AlertDialog alert = builder.create();
			alert.show();
			TextView messageText = (TextView) alert.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
	
		}

	 public void ShowListAlert(Update_Thumb_Adapter adapter){
			
		    AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
		    myDialog.setTitle("Video Thumbnails"); 
	        final ListView listview=new ListView(getActivity());
	        listview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	      
	        LinearLayout layout = new LinearLayout(getActivity());
	        layout.setOrientation(LinearLayout.VERTICAL);					       
	        layout.addView(listview);
	        myDialog.setView(layout);
	        listview.setAdapter(adapter);
	        listview.setOnItemClickListener(this);
	       
	        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	 
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            dialog.dismiss();
	                        }
	                    });
	 
	        dialog= myDialog.show();
	         
	 	}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	
				thumburl=list_thumb.get(arg2);
				dialog.dismiss();
				boolean isinternet=ImageUtil.isInternetOn(getActivity());
				if (isinternet) {
					
					prodialog=ProgressDialog.show(getActivity(), "", "Updating thumb...");
					/*UpdateTumb task=new UpdateTumb();
					task.execute();*/
					
					if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
		    		    new UpdateTumb().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		    		} else {
		    		    new UpdateTumb().execute();
		    		}
				}
				else{
					ImageUtil.showAlert(getActivity(),getActivity().getResources().getString(R.string.internet_error));
				}
			
				//showDialogItemClick();
		}
		
/*		private class DeleteVideosTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				//http://picture-video-store.com/ws/?ws=1&act=UpdateVideoThumb&MemberID=1&thumbfilename=MOV_0226_001.jpg&origvideofilename=MOV_0226.mp4
				//http://picture-video-store.com/ws/?ws=1&act=fetchWatchlist&consumer_id=1
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				String splitvideourl[]=deletevideourl.split("/");
				String origvideofilename=splitvideourl[splitvideourl.length-1];
				
				String splitvideothumburl[]=deletethumburl.split("/");
				String thumbfilename=splitvideothumburl[splitvideothumburl.length-1];
				Log.v(TAG, "origvideofilename is: "+origvideofilename);
				Log.v(TAG, "thumbfilename is: "+thumbfilename);
				
				//http://picture-video-store.com/ws/?ws=1&act=DeleteContent&MemberID=1&
				//thumbfilename=userid1/videos/thumbimage/Enjoy_001.jpg&origvideofilename=userid1/videos/Enjoy.3gp
				
				//String url=ImageConstant.BASEURLMYUPLOAD;
				
				
				String url=ImageConstant.BASEURL+"DeletevideoContent";
				
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("thumbfilename", thumbfilename);
					obj1.put("origvideofilename", origvideofilename);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      //Log.v(TAG, "Json object is: "+obj1.toString());
				
				  HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
				  
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
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
				
				prodialog1.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Content Will be delete after Admin Approval")) {
							
							ImageUtil.showAlert(getActivity(), "Your request to delete this file is pending approval from admin.");
							
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else{
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
				}
			}
		}*/
		
		private void showDialogItemClick() {
			
			try {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Choose");

				final CharSequence[] choiceList = { "Select Thumbnail", "Add Video Name"};
				int selected = -1; 

				builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int itemNO) {

								if (itemNO == 0) {
								
									// do stuff here for update the video thumbnail
									//showAlertupdate(getActivity(), "Are you sure want to update the thumbnail ?");
									prodialog_videothumb=ProgressDialog.show(getActivity(), "", "Loading..");
									
									if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
						    		    new LoadVideoThumbTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						    		} else {
						    		    new LoadVideoThumbTask().execute();
						    		}
								/*	
									LoadVideoThumbTask task=new LoadVideoThumbTask();
									task.execute();*/
									
								/*	Update_Thumb_Adapter adapter=new Update_Thumb_Adapter(getActivity(), list_thumb);
									ShowListAlert(adapter);*/
								   
									alert.dismiss();
								
								}else if (itemNO == 1) {
									
									ShowAlertAddVideoName();
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
		
		 public void ShowAlertAddVideoName(){
				
			    LayoutInflater li = LayoutInflater.from(getActivity());
				View promptsView = li.inflate(R.layout.addvideoname, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
				editvideoname = (EditText) promptsView.findViewById(R.id.editText_catname);
				
				// set dialog message
				alertDialogBuilder.setCancelable(false).setPositiveButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});
				
				alertDialogBuilder.setCancelable(false).setNegativeButton("Add Video Name",
						new DialogInterface.OnClickListener() {
						
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
				Button theButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				theButton.setOnClickListener(new CustomListenerMessage(alertDialog));
			  
	         }
		 
			class CustomListenerMessage implements View.OnClickListener {
				private final Dialog dialog;

				public CustomListenerMessage(Dialog dialog) {
					this.dialog = dialog;
				}

				@Override
				public void onClick(View v) {

					 videoname  = editvideoname.getText().toString().trim();
					if (videoname.length() < 1) {
						
						Toast.makeText(getActivity(), "Video Name is required.", Toast.LENGTH_LONG).show();
					} 
					else if (videoname.length() > 18) {
						
						Toast.makeText(getActivity(), "Video Name should not exceed 18 character.", Toast.LENGTH_LONG).show();
					}
					else {
						   
						boolean isinternet=ImageUtil.isInternetOn(getActivity());
						if (isinternet) {
							//do here what
						
								prodialog_addvideo=ProgressDialog.show(getActivity(), "", "Adding Video Name..");
								/*AddVideoNameTask task=new AddVideoNameTask();
								task.execute();*/
								
								if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
					    		    new AddVideoNameTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					    		} else {
					    		    new AddVideoNameTask().execute();
					    		}
								
							}
							else{
								ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
							}
							
							dialog.dismiss();
						}
						
			    	}
		    	}
			
		
		 private class MyUploadedTask extends AsyncTask<String, Void, String> {
				String response1 = "";
				
				@Override
				protected String doInBackground(String... urls) {
					
					Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
					String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
					//String url="http://picture-video-store.com/ws/?";
					
					String url=ImageConstant.BASEURL+"getIVUpdate";
					
					
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
						
						Intent intent=new Intent(getActivity(), MyUploadActivity.class);
						intent.putExtra("JSONAPI", resultString);
						startActivity(intent);
						
						 Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
						 SharedPreferences.Editor editor = Prefs.edit();
						 editor.putString(ImageConstant.UPDATEVIDEO, "1");
						 editor.commit();	
						 
						
						getActivity().finish();
					}
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					}

				}
			}
		 
		 
		 private class AddVideoNameTask extends AsyncTask<String, Void, String> {
				String response1 = "";
				
				@Override
				protected String doInBackground(String... urls) {
				
					Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
					String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
					
					//String url=GalleryConstant.BASEURL;
					
					String url=ImageConstant.BASEURL+"AddVideoName";
						
					JSONObject obj1=new JSONObject();
					
					try {
						obj1.put("MemberID", memberid);
						obj1.put("videoname", videoname);
						obj1.put("uploadID", videoid);
						
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
				       //Log.v(TAG, "Json object is: "+obj1.toString());
				   	ImageUtil.galleryLog(TAG,"Json object is: "+obj1.toString());
					
					   HttpClient httpclient = new DefaultHttpClient();
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
					       // Log.v(TAG+".doInBackground", "Http response is:" + result);
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
					
					prodialog_addvideo.dismiss();
				
					Log.v(TAG, "onPostExecute called");
					//Log.v(TAG, "Response is: "+resultString);
					ImageUtil.galleryLog(TAG,"Response is: "+resultString);
					if (resultString != null && !resultString.isEmpty()) {
						
						try {
							JSONObject jsonobj=new JSONObject(resultString);
							String status=jsonobj.getString("Status");
							if (status.trim().equalsIgnoreCase("Video Name updated successfully")) {
								showAlertUpdate(getActivity(), getActivity().getResources().getString(R.string.video_name_added));
								
							}
							else if (status.trim().equalsIgnoreCase("This video Name already Exist")) {
								ImageUtil.showAlert(getActivity(), status);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
	                   
					}

				}
			}
		 
		 public void showAlertUpdate(Activity activity, String message) {
				
		
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage(message);
				
				builder.setCancelable(false);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();

						boolean isinternet=ImageUtil.isInternetOn(getActivity());
						if (isinternet) {

							prodialog_myupload=ProgressDialog.show(getActivity(), "", "Loading...");
							/*MyUploadedTask task=new MyUploadedTask();
							task.execute();*/
							
							if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				    		    new MyUploadedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				    		} else {
				    		    new MyUploadedTask().execute();
				    		}
						} 
						else{
							
							Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.internet_error), Toast.LENGTH_LONG).show();
						}
					}

				});

				AlertDialog alert = builder.create();
				alert.show();
				TextView messageText = (TextView) alert.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
			}
		 
/*		 private class LoadCategoryTask extends AsyncTask<String, Void, String> {
				String response1 = "";
				
				@Override
				protected String doInBackground(String... urls) {
				
					Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
					String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
					
					//String url=GalleryConstant.BASEURL;
					
					String url=ImageConstant.BASEURL+"getCategoryList";
						
					JSONObject obj1=new JSONObject();
					
					try {
						obj1.put("MemberID", memberid);
						
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
					
					prodoalog_cat.dismiss();
					list_category.clear();
					Log.v(TAG, "onPostExecute called");
					Log.v(TAG, "Response is: "+resultString);
					if (resultString != null) {
						
	                    try {
							JSONArray jsonarray=new JSONArray(resultString);
							for (int i = 0; i < jsonarray.length(); i++) {
								
								JSONObject jsonobj=jsonarray.getJSONObject(i);
								String cat_id=jsonobj.getString("CategoryID");
								String cat_name=jsonobj.getString("CategoryName");
								CategoryModel model=new CategoryModel(cat_id, cat_name, false);
								list_category.add(model);
							}
							
						} catch (JSONException e) {
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
					
	                    CategoryAdapter adapter=new CategoryAdapter(getActivity(), list_category, "0", "0", "0");
	                    ShowListAlertCategory(adapter);
					}

				}
			}*/
		 
		 public void ShowListAlertCategory(CategoryAdapter adapter){
				
			    AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
			    
			    myDialog.setTitle("Category List");
		        final ListView listview=new ListView(getActivity());
		        listview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		      
		        LinearLayout layout = new LinearLayout(getActivity());
		        layout.setOrientation(LinearLayout.VERTICAL);		
		        layout.addView(listview);
		        myDialog.setView(layout);
		        listview.setAdapter(adapter);
		        listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						
						// add the images inside 
						 CategoryModel model=list_category.get(arg2);
					    
				    }
		        
		        });
		       
		        myDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
		 
		                        @Override
		                        public void onClick(DialogInterface dialog, int which) {
		                            dialog.dismiss();
		                        }
		                    });
		        
		        myDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
		       	 
	             @Override
	             public void onClick(DialogInterface dialog, int which) {
	                 dialog.dismiss();
	                 
	             }
	         });
		 
		   dialog= myDialog.show();
		         
		 }

		@Override
		public void onClick(View v) {
		
			if (btn_addeditcate==v) {
				
				selected = adapter.getSelected();
				if (selected.size() > 0) {
					
					boolean isinternet=ImageUtil.isInternetOn(getActivity());
					if (isinternet) {
						
						// load the added the category for a set
						
						prodialog_cat=ProgressDialog.show(getActivity(), "", "Loading...");
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
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
					}
					
					
					/*// call to load the category
					list=db.GetCategoryData();
			    	CategoryAdapter adapter=new CategoryAdapter(getActivity(), list, "0", "0", "0");
	                ShowListAlertCategoryAll(adapter);*/
				}
				else{
					
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.select_one_File));
				}
				
				
			} else if (btn_delete==v) {
				
				selected = adapter.getSelected();
				if (selected.size() > 0) {
					
					showAlertDelete(getActivity(), "Are you sure you want to delete the videos?");
				}
				else{
					
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.select_one_File));
				}
				
			}
			
		}
		
		 public void ShowListAlertCategoryAll(final CategoryAdapter adapter){
				
			    AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
			    newadapter=adapter;
			    myDialog.setTitle("Select Category List");
		        final ListView listview=new ListView(getActivity());
		        listview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		      
		        LinearLayout layout = new LinearLayout(getActivity());
		        layout.setOrientation(LinearLayout.VERTICAL);		
		        layout.addView(listview);
		        myDialog.setView(layout);
		        listview.setAdapter(adapter);
		       
		        myDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
		 
		                        @Override
		                        public void onClick(DialogInterface dialog, int which) {
		                           
		                            
		                            
		       				
		       					
		                        }
		                    });
		        
		        myDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
		       	 
	             @Override
	             public void onClick(DialogInterface dialog, int which) {
	                 //dialog.dismiss();
	                 
	               
	             }
	         });
		 
		        AlertDialog alertDialog= myDialog.create();
			     alertDialog.show();
			     Button theButton = alertDialog
							.getButton(DialogInterface.BUTTON_NEGATIVE);
					theButton.setOnClickListener(new CustomListener(alertDialog));    
		         
		 }
		 
		 class CustomListener implements View.OnClickListener {
				private final Dialog dialog;

				public CustomListener(Dialog dialog) {
					this.dialog = dialog;
				}

				@Override
				public void onClick(View v) {

					 if(newadapter!=null){
							
 						   checked_category=newadapter.getChecked();
 						  // Toast.makeText(getApplicationContext(), "Checked size is: "+checked_category.size(), Toast.LENGTH_LONG).show();			   
 					   }
 					 
 					 if (checked_category.size() > 0) {
 						 
 						 dialog.dismiss();
						
 						 boolean isinternet=ImageUtil.isInternetOn(getActivity());
     						if (isinternet) {
     							
     							prodialog_addedit=ProgressDialog.show(getActivity(), "", "Loading..");
     							/*AddEditCategory task=new AddEditCategory();
     							task.execute();*/
     							
     							if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
     				    		    new AddEditCategory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
     				    		} else {
     				    		    new AddEditCategory().execute();
     				    		}
     						}
     						else{
     							ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
     						}
					}
 					 else{
 						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.check_atleast_onecat), Toast.LENGTH_LONG).show();
 					 }
				
				}
			}
		 
			private class AddEditCategory extends AsyncTask<String, Void, String> {
				String response1 = "";
				
				@Override
				protected String doInBackground(String... urls) {
				
					Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
					String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
					
					
					String url=ImageConstant.BASEURL+"AddUnderCategoryContentsdetails";
				
			
				    	selected = adapter.getSelected();
		                JSONObject obj1=new JSONObject();
						
						try {
							obj1.put("MemberID", memberid);
							obj1.put("totalcount", selected.size());
							for (int i = 1; i <= selected.size(); i++) {
							     
								MultiSelectVideoModel model=selected.get(i-1);
								String uploadedid=model.getUploadedid();
								obj1.put("Source_fileUploadID"+i, uploadedid);
							}
							
							
							  if ( checked_category.size() == 0) {
									
								    obj1.put("cat_id1", "0");
									obj1.put("cat_id2", "0");
									obj1.put("cat_id3", "0");
								}
							    else if (checked_category.size() == 1) {
									
							    	for (int i = 1; i <= checked_category.size(); i++) {
							    		CategoryModel model=checked_category.get(i-1);
							    		String catid=model.getCat_id();
							    		obj1.put("cat_id"+i, catid);
									}
							    	obj1.put("cat_id2", "0");
							    	obj1.put("cat_id3", "0");
								}
							    else if (checked_category.size() == 2) {
									
							    	for (int i = 1; i <= checked_category.size(); i++) {
							    		CategoryModel model=checked_category.get(i-1);
							    		String catid=model.getCat_id();
							    		obj1.put("cat_id"+i, catid);
									}
							    	obj1.put("cat_id3", "0");
								}
							    else if (checked_category.size() == 3) {
									
							    	for (int i = 1; i <= checked_category.size(); i++) {
							    		CategoryModel model=checked_category.get(i-1);
							    		String catid=model.getCat_id();
							    		//Log.v(TAG, "cat_id"+i+" "+catid);
							    		ImageUtil.galleryLog(TAG,"cat_id"+i+" "+catid);
							    		obj1.put("cat_id"+i, catid);
									}
							    	 
								}
							   
							
						
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
						
					     // Log.v(TAG, "Json object is: "+obj1.toString());	
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
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				 return response1;
			}

				@Override
				protected void onPostExecute(String resultString) {
					
					prodialog_addedit.dismiss();
					Log.v(TAG, "onPostExecute called");
					//Log.v(TAG, "Response is: "+resultString);
					ImageUtil.galleryLog(TAG,  "Response is: "+resultString);
					if (resultString != null && !resultString.isEmpty()) {
						
						try {
							JSONObject jsonobj=new JSONObject(resultString);
							String status=jsonobj.getString("Status");
							if (status.trim().equalsIgnoreCase("Success")) {
								
								ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.cat_added));
							}
							
							
						} catch (JSONException e) {
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
					
					}
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					}
				}
			}
			
			
			 
				private class DeleteMultipleVideo extends AsyncTask<String, Void, String> {
					String response1 = "";
					
					@Override
					protected String doInBackground(String... urls) {
					
						Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
						String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
						
						
						String url=ImageConstant.BASEURL+"DeleteImageContent";
					
				
					    	selected = adapter.getSelected();
			                JSONObject obj1=new JSONObject();
							
							try {
								obj1.put("MemberID", memberid);
								obj1.put("totalcount", selected.size());
								for (int i = 1; i <= selected.size(); i++) {
								     
									MultiSelectVideoModel model=selected.get(i-1);
									String uploadedid=model.getUploadedid();
									obj1.put("Source_fileUploadID"+i, uploadedid);
								}
								
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
							
						     // Log.v(TAG, "Json object is: "+obj1.toString());
						  	ImageUtil.galleryLog(TAG,  "Json object is: "+obj1.toString());
							

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
							      //  Log.v(TAG+".doInBackground", "Http response is:" + result);
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
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					 return response1;
				}

					@Override
					protected void onPostExecute(String resultString) {
						
						prodialog_delete.dismiss();
						Log.v(TAG, "onPostExecute called");
						//Log.v(TAG, "Response is: "+resultString);
						ImageUtil.galleryLog(TAG,"Response is: "+resultString);
						if (resultString != null && !resultString.isEmpty()) {
							
							try {
								JSONObject jsonobj=new JSONObject(resultString);
								String status=jsonobj.getString("Status");
								if (status.trim().equalsIgnoreCase("Content deleted successfully")) {
									
									showAlertDeletecontent(getActivity(), status);
								
						
								}
								else{
									ImageUtil.showAlert(getActivity(), status);
								}
								
							} catch (JSONException e) {
								e.printStackTrace();
							}catch (Exception e) {
								e.printStackTrace();
							}
						
						}
						else{
							ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
						}
					}
				}
				
				public void showAlertDeletecontent(Activity activity, String message) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
					//builder.setCustomTitle(title); 
					builder.setMessage(message);
					
					builder.setCancelable(false);
					builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							
							
								boolean isinternet=ImageUtil.isInternetOn(getActivity());
								if (isinternet) {

									prodialog_myupload=ProgressDialog.show(getActivity(), "", "Loading...");
									
									if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
						    		    new MyUploadedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						    		} else {
						    		    new MyUploadedTask().execute();
						    		}
									
									/*MyUploadedTask task=new MyUploadedTask();
									task.execute();*/
								} 
								else{
									
									Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.internet_error), Toast.LENGTH_LONG).show();
								}
								

						}

					});

					AlertDialog alert = builder.create();
					alert.show();
					TextView messageText = (TextView) alert.findViewById(android.R.id.message);
					messageText.setGravity(Gravity.CENTER);
				}


				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
					selectedvalue=array_select[arg2];
					if (selectedvalue.trim().equalsIgnoreCase("Select All")) {
						adapter.selectAll(true);
					}
					else if (selectedvalue.trim().equalsIgnoreCase("UnSelect All")) {
						adapter.unckeckedAll(false);
					}
					else if (selectedvalue.trim().equalsIgnoreCase("Checked Multiple")) {
						adapter.unckeckedAll(false);
					}
					else if (selectedvalue.trim().equalsIgnoreCase("1-5")) {
						adapter.unckeckedAll(false);
						adapter.selectFivecheck(true);
					}
					
					else if (selectedvalue.trim().equalsIgnoreCase("1-10")) {
						adapter.unckeckedAll(false);
						adapter.selectTencheck(true);
					}
					
					else if (selectedvalue.trim().equalsIgnoreCase("1-20")) {
						adapter.unckeckedAll(false);
						adapter.selectTwentycheck(true);
					}
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
				
				
				  private class LoadCategoryTask extends AsyncTask<String, Void, String> {
						
				    	String response1 = "";
						
						@Override
						protected String doInBackground(String... urls) {
						
							Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
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
								
									}
									//Toast.makeText(context, "Refresh Complete.", Toast.LENGTH_LONG).show();
									
								} catch (JSONException e) {
									e.printStackTrace();
								}catch (Exception e) {
									e.printStackTrace();
								}
							
			               
			                  // here load data
			                
			               // call to load the category
								list=db.GetCategoryData();
						    	CategoryAdapter adapter=new CategoryAdapter(getActivity(), list, "0", "0", "0");
				                ShowListAlertCategoryAll(adapter);
							}
							else{
								ImageUtil.showAlert(getActivity(), getResources().getString(R.string.timeout));
							}

						}
					}

				  private class LoadVideoThumbTask extends AsyncTask<String, Void, String> {
						
				    	String response1 = "";
						
						@Override
						protected String doInBackground(String... urls) {
						
							Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
							String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
						
							String url=ImageConstant.BASEURL+"getThumbVideoImage";
								
							JSONObject obj1=new JSONObject();
							
							try {
								obj1.put("MemberID", memberid);
								obj1.put("UploadId", videoid);
								
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
							
						
							prodialog_videothumb.dismiss();
							Log.v(TAG, "onPostExecute called");
							//Log.v(TAG, "Response is: "+resultString);
							 ImageUtil.galleryLog(TAG, "Response is: "+resultString);
							if (resultString != null  && !resultString.isEmpty()) {
								list_thumb.clear();
								try {
									JSONArray jsonvideoaray=new JSONArray(resultString);
									JSONObject jsonobj=jsonvideoaray.getJSONObject(0);
									JSONArray jsonayyay=jsonobj.getJSONArray("Video");
									for (int i = 0; i < jsonayyay.length(); i++) {
										
										JSONObject jsonobjvideo=jsonayyay.getJSONObject(i);
										String thumbvideo=jsonobjvideo.getString("ThumbVideo-"+i);
										list_thumb.add(thumbvideo);
										
									}
									
									Update_Thumb_Adapter adapter=new Update_Thumb_Adapter(getActivity(), list_thumb);
									ShowListAlert(adapter);
									
								} catch (JSONException e) {
									e.printStackTrace();
								}catch (Exception e) {
									
								}
							}
							else{
								ImageUtil.showAlert(getActivity(), getResources().getString(R.string.timeout));
							}

						}
					}
}
