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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kns.adapter.MultiSelect_PictureAdapter;
import com.kns.adapter.SetAdapter;
import com.kns.model.MultiSelectPictureModel;
import com.kns.model.SetModel;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;
import com.sunil.selectmutiple.LoadSetActivity;
import com.sunil.selectmutiple.MyUploadActivity;
import com.sunil.selectmutiple.R;

public class MyPictureGragmentbackup  extends Fragment implements OnItemClickListener, OnClickListener, OnItemSelectedListener{
	  
	private final static String TAG="MyPictureFragment";
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	private GridView gridview=null;
	private ImageButton btn_addtoset;
	private ImageButton btn_delete;
	private Spinner spinner_select;
	List<MultiSelectPictureModel> list_images=new ArrayList<MultiSelectPictureModel>();
	TextView textviwe;
	private ProgressDialog prodialog_editset;
	private ProgressDialog prodialog_set;
	private ProgressDialog prodialog;
	private ProgressDialog prodialog1;
	private ProgressDialog prodialog2;
	private ProgressDialog prodialog3;
	private ProgressDialog prodialog_delete;
	private ProgressDialog prodeialog_add;
	private ProgressDialog prodialog_myupload;
	String imageourl="";
	String imagethumbourl="";
	private AlertDialog alert = null;
	AlertDialog dialog;
	EditText editcatname;
	String categoryname;
	String categoryid="";
	List<SetModel> catlist=new ArrayList<SetModel>();
	int setthumb=0;
	String imagecount;
	String approvflag="";
	String jsonresp="";
	MultiSelect_PictureAdapter adapter;
	ArrayList<MultiSelectPictureModel> selected=null;
	String array_select[]={"Select Multiple", "Checked All", "Unchecked All", "1-10", "1-20", "1-50", "1-100"};
	String selectedvalue="";
	String liveflag="";
	
	@Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.mypictureuploaded, container, false);
      gridview=(GridView)view.findViewById(R.id.gridView_pic);
      textviwe= (TextView)view.findViewById(R.id.textView);
      btn_addtoset=(ImageButton)view.findViewById(R.id.button_addtoset);
      btn_delete=(ImageButton)view.findViewById(R.id.button_deletepic);
      spinner_select= (Spinner)view.findViewById(R.id.spinner_select);
      spinner_select.setOnItemSelectedListener(this);
      btn_addtoset.setOnClickListener(this);
      btn_delete.setOnClickListener(this);
      gridview.setOnItemClickListener(this);
      
  	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, array_select);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_select.setAdapter(dataAdapter);
      
      Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
      jsonresp=Prefs.getString(ImageConstant.MYUPLOAD, "");
      
      String checkstatus=Prefs.getString(ImageConstant.CHECKBOXPICTURESTATUS, "");
		 if (checkstatus.equalsIgnoreCase("not checked") || checkstatus.equalsIgnoreCase("")) {
			
			 attentionBox();
		}
		 
      if (jsonresp.equalsIgnoreCase("")) {
			// if the service have no any upload
		}
      else{
      	
      	// parse the json here
      	if (jsonresp != null) {
				
				try {
					list_images.clear();
					JSONArray jsonarray=new JSONArray(jsonresp);
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject obj=jsonarray.getJSONObject(i);
						String status=obj.getString("Status");
						String partnerid=obj.getString("PartnerID");
						
						JSONArray array=obj.getJSONArray("Image");
						for (int j = 0; j < array.length(); j++) {
							
							JSONObject obj1=array.getJSONObject(j);
							
							String uploadedid=obj1.getString("UploadID");
							String imageurl=obj1.getString("RealImage");
							String imagethumburl=obj1.getString("ThumbImage");
							String AdminApprovedFlag=obj1.getString("AdminApprovedFlag");
							
							MultiSelectPictureModel model=new MultiSelectPictureModel(uploadedid, imageurl, imagethumburl, false, AdminApprovedFlag);
							list_images.add(model);
						}
					}
					
					
			/*		JSONObject jsonobj=new JSONObject(jsonresp);
					String status=jsonobj.getString("Status");
					if (status.trim().equalsIgnoreCase("Partner Details")) {
						
						JSONArray jsonarray=jsonobj.getJSONArray("Image");
						for (int i = 0; i < jsonarray.length(); i++) {
							
							JSONObject obj=jsonarray.getJSONObject(i);
							String imageurl=obj.getString("RealImage");
							String imagethumburl=obj.getString("ThumbImage");
							
							PartnerImage_Model model=new PartnerImage_Model(imageurl, imagethumburl);
							list_images.add(model);
						}
					
					  }*/
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				if (list_images.size() > 0) {
					textviwe.setVisibility(View.GONE);
				}
				else{
					textviwe.setText("Upload pictures. Then create a picture set to sell.");
				}
				adapter=new MultiSelect_PictureAdapter(getActivity(), list_images);
				adapter.setMultiplePick(true);
				gridview.setAdapter(adapter);
				
			}
      }
      
     
      return view;
   }
	
	  public void attentionBox(){
			
			AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
		    LayoutInflater adbInflater = LayoutInflater.from(getActivity());
		    View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
		    final CheckBox dontShowAgain = (CheckBox)eulaLayout.findViewById(R.id.skip);
		    adb.setView(eulaLayout);
		    adb.setTitle("Attention");
		    adb.setMessage("You currently have pictures that are not assigned to a set. Only pictures grouped into sets can go live in the store.");
	    	adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
	    	      public void onClick(DialogInterface dialog, int which) {
	    	 
	    	    	  Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
	    	    	  if (dontShowAgain.isChecked())  {
	    	    
						 SharedPreferences.Editor editor = Prefs.edit();
						 editor.putString(ImageConstant.CHECKBOXPICTURESTATUS, "checked");
						 editor.commit();	
						 
	    	    	  }
	    	    	  else{
	    	    		  
	 					 SharedPreferences.Editor editor = Prefs.edit();
	 					 editor.putString(ImageConstant.CHECKBOXPICTURESTATUS, "not checked");
	 					 editor.commit();	
	    	    	  }
	    	    	  
	    	    	  return;  
	    	      } });
	 
	    	  adb.show();
		}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		
		if (selectedvalue.trim().equalsIgnoreCase("Unchecked All")) {
			
			Toast.makeText(getActivity(), "Please select multiple option to checked the file.", Toast.LENGTH_LONG).show();
		}
		else{
			MultiSelectPictureModel model=list_images.get(position);
			String adminapproveflag=model.getAdminApprovedFlag();
			Log.v(TAG, "onItemClick approved: "+adminapproveflag);
			adapter.changeSelection(v, position, adminapproveflag);
		}
		
		
		
	/*	
		MultiSelectPictureModel model=list_images.get(arg2);
		imageourl=model.getImagerealurl();
		imagethumbourl=model.getImagethumburl();
		
		showDialogItemClick();*/
		
		
	}
	
	public void showAlertDeleteSet(Activity activity, String message) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message);
		
		builder.setCancelable(false);
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
             // do here you want
				
			}

		});
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();

				//here you can send the alert to admin for delete the video
				boolean isinternet=ImageUtil.isInternetOn(getActivity());
				if (isinternet) {
					
					prodialog=ProgressDialog.show(getActivity(), "", "Sending request to delete set...");
					DeleteSetTask task=new DeleteSetTask();
					task.execute();
				}
				else{
					ImageUtil.showAlert(getActivity(), "No Internet Connection");
				}
			   
			}

		});

		AlertDialog alert = builder.create();
		alert.show();
		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}
	
	private class DeleteSetTask extends AsyncTask<String, Void, String> {
		String response1 = "";
		
		@Override
		protected String doInBackground(String... urls) {
		
			Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
			
			String url=ImageConstant.BASEURL+"DeleteSetName";
		
	
              JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("set_id", categoryid);
					
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      Log.v(TAG, "Json object is: "+obj1.toString());
				

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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		 return response1;
	}

		@Override
		protected void onPostExecute(String resultString) {
			
			prodialog.dismiss();
			Log.v(TAG, "onPostExecute called");
			Log.v(TAG, "Response is: "+resultString);
			if (resultString != null && !resultString.isEmpty()) {
				
				try {
					JSONObject jsonobj=new JSONObject(resultString);
					String status=jsonobj.getString("Status");
					if (status.trim().equalsIgnoreCase("Set Will be delete after Admin Approval")) {
					
						ImageUtil.showAlert(getActivity(), "Set Will be delete after Admin Approval");
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
		builder.setMessage(message);
		
		builder.setCancelable(false);
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
             // do here you want
				
			}

		});
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();

				//here you can send the alert to admin for delete the video
				boolean isinternet=ImageUtil.isInternetOn(getActivity());
				if (isinternet) {
					
					prodialog=ProgressDialog.show(getActivity(), "", "Sending request to delete...");
					DeleteContentTask task=new DeleteContentTask();
					task.execute();
				}
				else{
					ImageUtil.showAlert(getActivity(), "No Internet Connection");
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
		
			Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
			String splitvideourl[]=imageourl.split("/");
			String origvideofilename=splitvideourl[splitvideourl.length-1];
			
			String splitvideothumburl[]=imagethumbourl.split("/");
			String thumbfilename=splitvideothumburl[splitvideothumburl.length-1];
			Log.v(TAG, "origvideofilename is: "+origvideofilename);
			Log.v(TAG, "thumbfilename is: "+thumbfilename);
			
			//http://picture-video-store.com/ws/?ws=1&act=DeleteContent&MemberID=1&
			//thumbfilename=userid1/videos/thumbimage/Enjoy_001.jpg&origvideofilename=userid1/videos/Enjoy.3gp
			
			//String url=ImageConstant.BASEURLMYUPLOAD;
			
			
			String url=ImageConstant.BASEURL+"DeleteImageContent";
		
	
              JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("thumbfilename", thumbfilename);
					obj1.put("origvideofilename", origvideofilename);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      Log.v(TAG, "Json object is: "+obj1.toString());
			      

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
			
			prodialog.dismiss();
			Log.v(TAG, "onPostExecute called");
			Log.v(TAG, "Response is: "+resultString);
			if (resultString != null && !resultString.isEmpty()) {
				
				try {
					JSONObject jsonobj=new JSONObject(resultString);
					String status=jsonobj.getString("Status");
					if (status.trim().equalsIgnoreCase("Content Will be delete after Admin Approval")) {
					
						ImageUtil.showAlert(getActivity(), "Your request to delete this file is pending approval from admin");
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
	
	private void showDialogItemClick() {
		
		try {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Choose");

			final CharSequence[] choiceList = { "Delete Image", "Use Image to Represent a Set", "Add Image to a Set", "View/Edit Set" };
			int selected = -1; 

			builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int itemNO) {

							if (itemNO == 0) {
							
								// do stuff here for delete the image
								showAlert(getActivity(), "Are you sure you want to delete the image");
								alert.dismiss();
							
							} else if (itemNO == 1) {
								
								setthumb=1;
								// do stuff here for use image
								
							/*	boolean isinternet=ImageUtil.isInternetOn(getActivity());
								if (isinternet) {
									
									prodialog1=ProgressDialog.show(getActivity(), "", "Adding in a set..");
									ResetThumbTask task=new ResetThumbTask();
									task.execute();
								}
								else{
									ImageUtil.showAlert(getActivity(), "No Internet Connection");
								}*/
								
								boolean isinternet=ImageUtil.isInternetOn(getActivity());
								if (isinternet) {
									
									
									prodialog1=ProgressDialog.show(getActivity(), "", "Loading..");
									CategoryTask task=new CategoryTask();
									task.execute();
								}
								else{
									ImageUtil.showAlert(getActivity(), "No Internet Connection");
								}
								
								alert.dismiss();

							}
							else if (itemNO == 2) {
								
								// do stuff here for use image
								setthumb=2;
								boolean isinternet=ImageUtil.isInternetOn(getActivity());
								if (isinternet) {
									
									prodialog1=ProgressDialog.show(getActivity(), "", "Loading..");
									CategoryTask task=new CategoryTask();
									task.execute();
								}
								else{
									ImageUtil.showAlert(getActivity(), "No Internet Connection");
								}
								
								alert.dismiss();

							}
							else if (itemNO == 3) {
								
								setthumb=3;
								
								boolean isinternet=ImageUtil.isInternetOn(getActivity());
								if (isinternet) {
									
									
									prodialog1=ProgressDialog.show(getActivity(), "", "Loading..");
									CategoryTask task=new CategoryTask();
									task.execute();
								}
								else{
									ImageUtil.showAlert(getActivity(), "No Internet Connection");
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
	
/*	private class AddedInsetTask extends AsyncTask<String, Void, String> {
		String response1 = "";
		
		@Override
		protected String doInBackground(String... urls) {
		
			//http://picture-video-store.com/ws/?ws=1&act=AddRepSet&MemberID=1&origfilename=Jellyfish.jpg&resetvalue=1&cat_id=1
			Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
			String splitvideourl[]=imageourl.split("/");
			String originalfile=splitvideourl[splitvideourl.length-1];
			
			String splitvideothumburl[]=imagethumbourl.split("/");
			String thumbfilename=splitvideothumburl[splitvideothumburl.length-1];
			Log.v(TAG, "originalfile is: "+originalfile);
			Log.v(TAG, "thumbfilename is: "+thumbfilename);
						
			//String url=ImageConstant.BASEURLMYUPLOAD;
			
			    String url=ImageConstant.BASEURL+"AddRepSet";
	
              JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("origfilename", originalfile);
					obj1.put("thumbfilename", thumbfilename);
					obj1.put("resetvalue", "1");
					obj1.put("cat_id", categoryid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      Log.v(TAG, "Json object is: "+obj1.toString());
			      
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
				        	
						    InputStream is = response.getEntity().getContent();
						    WebHelper webHelper = new WebHelper();
						    response1 = webHelper.convertStreamToString(is);
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		 return response1;
	}

		@Override
		protected void onPostExecute(String resultString) {
			
			prodialog1.dismiss();
			Log.v(TAG, "onPostExecute called");
			Log.v(TAG, "Response is: "+resultString);
			try {
			if (resultString != null && !resultString.isEmpty()) {
				
				
					JSONArray jsonarray=new JSONArray(resultString);
					for (int i = 0; i < jsonarray.length(); i++) {
						
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("This Image already Represent under this Category")) {
						
							ImageUtil.showAlert(getActivity(), "Already added this picture in this set");
							//break;
						}
						else if (status.trim().equalsIgnoreCase("Represent Set Successfully")) {
							
							ImageUtil.showAlert(getActivity(), "Added successfully in set");
							//break;
						}
					//}
				} 
				else{
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
				  }
			}
			catch (JSONException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
		}
	}*/
	
	private class CategoryTask extends AsyncTask<String, Void, String> {
		String response1 = "";
		
		@Override
		protected String doInBackground(String... urls) {
		
			Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
//			/http://picture-video-store.com/ws/?ws=1&act=Listofcategory&MemberID=1			
			//String url=ImageConstant.BASEURLMYUPLOAD;
			
			
			String url=ImageConstant.BASEURL+"Listofcategory";
			
          JSONObject obj1=new JSONObject();
			
			try {
				obj1.put("MemberID", memberid);
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		      Log.v(TAG, "Json object is: "+obj1.toString());
			
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
			        	
					   /* InputStream is = response.getEntity().getContent();
					    WebHelper webHelper = new WebHelper();
					    response1 = webHelper.convertStreamToString(is);*/
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
			catlist.clear();
			try {
			if (resultString != null && !resultString.isEmpty()) {
				
					JSONArray jsonarray=new JSONArray(resultString);
					for (int i = 0; i < jsonarray.length(); i++) {
						
						JSONObject jsonobj=jsonarray.getJSONObject(i);
						String status=jsonobj.getString("Status");
						
						if (status.trim().equalsIgnoreCase("This Partner Dont Have any Category")) {
							Log.v(TAG, status);
						}
						else{
							
							String partnerid=jsonobj.getString("PartnerID");
							String categoryid=jsonobj.getString("CategoryID");
							String catagoryname=jsonobj.getString("CategoryName");
							String imagecount=jsonobj.getString("ImageCount");
							String imagethumb=jsonobj.getString("CategoryImage");
							String Approvalflag=jsonobj.getString("Approvalflag");
							String LiveFlag=jsonobj.getString("LiveFlag");
							
							
							SetModel model=new SetModel(partnerid, categoryid, catagoryname, imagethumb, imagecount, Approvalflag, LiveFlag);
							catlist.add(model);
						}
					
					}
					
					SetAdapter adapter=new SetAdapter(getActivity(), catlist);
					ShowListAlert(adapter);
				} 
				else{
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
				  }
			}
			catch (JSONException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
			}
		}
	}
	
	 public void ShowListAlert(SetAdapter adapter){
			
		    AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
		    Toast.makeText(getActivity(), "Picture sets require a minimum of 10 pictures to go live in the store.", Toast.LENGTH_LONG).show();
		  /*  
		    if (setthumb==1) {
		    	 myDialog.setTitle("Select Set to Update Thumb");
			}
		    else if(setthumb==2){
		    	 myDialog.setTitle("Select Set to Add Picture");
		    }
		    else if (setthumb==3) {
		    	myDialog.setTitle("Select Set to View");
			}*/
		 //   myDialog.setTitle("Select Catagory"); 
		    
		
		    myDialog.setTitle("Select Set to Add Picture");
		    final TextView textview=new TextView(getActivity());
	        final ListView listview=new ListView(getActivity());
	        listview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	      
	        LinearLayout layout = new LinearLayout(getActivity());
	        layout.setOrientation(LinearLayout.VERTICAL);		
	        layout.addView(textview);
	        layout.addView(listview);
	        myDialog.setView(layout);
	        listview.setAdapter(adapter);
	        listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					
					// add the images inside 
					 SetModel model=catlist.get(arg2);
				     categoryid=model.getCatagoryid();
				     imagecount=model.getNoofimages();
				     categoryname=model.getCatagoryname();
				     approvflag=model.getApprovalflag();
				     liveflag=model.getLiveFlag();
				     
				     
				  	 if (liveflag.equalsIgnoreCase("1")) {
							
			    		 ImageUtil.showAlert(getActivity(), "This Set in live. You can not edit/add in this set");
					}
			    	 else{
			    		 
			    		 if (imagecount.trim().equalsIgnoreCase("100")) {
								
				    		 ImageUtil.showAlert(getActivity(), "Set had already completed with maximum pictures.");
						  }
				    	 else{

					    		boolean isinternet=ImageUtil.isInternetOn(getActivity());
								if (isinternet) {
								//	jsonresp
									
									prodeialog_add=ProgressDialog.show(getActivity(), "", "Adding files..");
									AddImageMultiple task=new AddImageMultiple();
									task.execute();
									
								/*	Intent intent=new Intent(getActivity(), SeletctMultipleFile.class);
									intent.putExtra("JSONAPI", jsonresp);
									intent.putExtra("setid", categoryid);
									startActivity(intent);*/
									
								}
								else{
									ImageUtil.showAlert(getActivity(), "Internet Connection Error");
								}
								
				    	 }
			    		 
			    		 dialog.dismiss();
			    	 }
				     
	/*				
				     if (setthumb==1) {
						
				    		boolean isinternet=ImageUtil.isInternetOn(getActivity());
							if (isinternet) {
								
								prodialog3=ProgressDialog.show(getActivity(), "", "updating thumb..");
								ResetThumbTask task=new ResetThumbTask();
								
								task.execute();
							}
							else{
								ImageUtil.showAlert(getActivity(), "No Internet Connection");
							}
							
							dialog.dismiss();
					     }
				     else if (setthumb==2) {
				    	 
				    	 if (approvflag.equalsIgnoreCase("1")) {
							
				    		 ImageUtil.showAlert(getActivity(), "This Set in live. You can not edit");
						}
				    	 else{
				    		 
				    		 if (imagecount.trim().equalsIgnoreCase("100")) {
									
					    		 ImageUtil.showAlert(getActivity(), "Set had already completed with maximum pictures.");
							  }
					    	 else{

						    		boolean isinternet=ImageUtil.isInternetOn(getActivity());
									if (isinternet) {
									//	jsonresp
										
										Intent intent=new Intent(getActivity(), SeletctMultipleFile.class);
										intent.putExtra("JSONAPI", jsonresp);
										intent.putExtra("setid", categoryid);
										startActivity(intent);
										
									}
									else{
										ImageUtil.showAlert(getActivity(), "Internet Connection Error");
									}
									
					    	 }
				    	 }
				    	 
				  
						
							dialog.dismiss();
					}
				     
				     else if (setthumb==3) {
				    	 
							showDialogItemEditDelete();

					   }*/
				     
				  }
			});
	       
	        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	 
	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            dialog.dismiss();
	                        }
	                    });
	        
	        myDialog.setPositiveButton("Create Set", new DialogInterface.OnClickListener() {
	       	 
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  
                  ShowAlertNewCatagory();
                  
              }
          });
	 
	        dialog= myDialog.show();
	         
	 	}
	 
/*	 
		private void showDialogItemEditDelete() {
			
			try {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Choose");

				final CharSequence[] choiceList = { "Delete Set", "Edit Set Name", "View Set"};
				int selected = -1; 

				builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int itemNO) {

								if (itemNO == 0) {
								
									 if (liveflag.equalsIgnoreCase("1")) {
											
							    		 ImageUtil.showAlert(getActivity(), "This Set in live. You can not edit/add in this set");
									}else{
										
										
										showAlertDeleteSet(getActivity(), "Are you sure you want to delete set?");
										alert.dismiss();
									}
									
								
								} else if (itemNO == 1) {
									
									//edit set name
									 if (liveflag.equalsIgnoreCase("1")) {
											
							    		 ImageUtil.showAlert(getActivity(), "This Set in live. You can not edit/add in this set");
									}else{
										
										ShowAlertEditSet();
										
										alert.dismiss();
									}
										
								

								}
								else if (itemNO == 2) {
									
									alert.dismiss();
									

						    		boolean isinternet=ImageUtil.isInternetOn(getActivity());
									if (isinternet) {
										
										prodialog_set=ProgressDialog.show(getActivity(), "", "Loading set..");
										LoadSetTask task=new LoadSetTask();
										task.execute();
									}
									else{
										ImageUtil.showAlert(getActivity(), "No Internet Connection");
									}

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
		}*/
		
		
		private class AddImageMultiple extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				String url=ImageConstant.BASEURL+"AddMultipleImageSet";
				
	            JSONObject obj1=new JSONObject();
	            
	            selected=adapter.getSelected();
	            
				try {
					obj1.put("MemberID", memberid);
					obj1.put("cat_id", categoryid);
					obj1.put("totalcount", selected.size());
					for (int i = 1; i <= selected.size(); i++) {
						
						MultiSelectPictureModel model=selected.get(i-1);
						String imageurl=model.getImagerealurl();
						String thumburl=model.getImagethumburl();
						obj1.put("Source_fileName"+i, imageurl);
						obj1.put("Source_thumbfileName"+i, thumburl);
					}
					
					
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
				
				prodeialog_add.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				
				if (resultString != null && !resultString.isEmpty()) {
					
				try{
					
				      JSONObject jsonobj=new JSONObject(resultString);
				      String status=jsonobj.getString("Status");
				      if (status.trim().equalsIgnoreCase("Success")) {
				    	  
				    	  //"Status":"Success","PartnerID":"1","SetId":"2","ImageCount":10
				    	  showAlertAddFile(getActivity(), "Files added successfully");
					   }
				      else{
				    	  ImageUtil.showAlert(getActivity(), status);
				      }
						
				}catch (JSONException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		  }
		}
		
		
		public void showAlertAddFile(Activity activity, String message) {
	
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
						MyUploadedTask task=new MyUploadedTask();
						task.execute();
					} 
					else{
						
						Toast.makeText(getActivity(), "Internet Connection Error.", Toast.LENGTH_LONG).show();
					}
				}

			});

			AlertDialog alert = builder.create();
			alert.show();
			TextView messageText = (TextView) alert.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
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
					
				      Log.v(TAG, "Json object is: "+obj1.toString());
				      
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
					Log.v(TAG, "Response is: "+resultString);
					if (resultString != null && !resultString.isEmpty()) {
						
						Intent intent=new Intent(getActivity(), MyUploadActivity.class);
						intent.putExtra("JSONAPI", resultString);
						startActivity(intent);
						
						 Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
						 SharedPreferences.Editor editor = Prefs.edit();
						 editor.putString(ImageConstant.UPDATEPICTURE, "1");
						 editor.commit();	
						 
						
						getActivity().finish();
					}
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					}

				}
			}
	
/*	 
	public void ShowAlertEditSet(){
				
			    LayoutInflater li = LayoutInflater.from(getActivity());
				View promptsView = li.inflate(R.layout.newcataogy, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
				editcatname = (EditText) promptsView.findViewById(R.id.editText_catname);
				editcatname.setText(categoryname);
				// set dialog message
				alertDialogBuilder.setCancelable(false).setPositiveButton("Update Set",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});
				
				alertDialogBuilder.setCancelable(false).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
						
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
				Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				theButton.setOnClickListener(new CustomListenerUpdate(alertDialog));
			  
	         }
	
	
	class CustomListenerUpdate implements View.OnClickListener {
		private final Dialog dialog1;

		public CustomListenerUpdate(Dialog dialog) {
			this.dialog1 = dialog;
		}

		@Override
		public void onClick(View v) {

			categoryname = editcatname.getText().toString().trim();
			if (categoryname.length() < 1) {
				
				Toast.makeText(getActivity(), "Set name is required.", Toast.LENGTH_LONG).show();
			} 
			else {
				   
				boolean isinternet=ImageUtil.isInternetOn(getActivity());
				if (isinternet) {
					
					prodialog_editset=ProgressDialog.show(getActivity(), "", "Loading set..");
					EditSetTask task=new EditSetTask();
					task.execute();
				}
				else{
					ImageUtil.showAlert(getActivity(), "No Internet Connection");
				}
			
					dialog1.dismiss();
					dialog.dismiss();
				}
				
	    	}
  	}
	*/
		
	 public void ShowAlertNewCatagory(){
			
		    LayoutInflater li = LayoutInflater.from(getActivity());
			View promptsView = li.inflate(R.layout.newcataogy, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);
			editcatname = (EditText) promptsView.findViewById(R.id.editText_catname);
			
			// set dialog message
			alertDialogBuilder.setCancelable(false).setPositiveButton("Create Set",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
			
			alertDialogBuilder.setCancelable(false).setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
					
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			theButton.setOnClickListener(new CustomListenerMessage(alertDialog));
		  
       }
	 
		class CustomListenerMessage implements View.OnClickListener {
			private final Dialog dialog;

			public CustomListenerMessage(Dialog dialog) {
				this.dialog = dialog;
			}

			@Override
			public void onClick(View v) {

				categoryname = editcatname.getText().toString().trim();
				if (categoryname.length() < 1) {
					
					Toast.makeText(getActivity(), "Set name is required.", Toast.LENGTH_LONG).show();
				} 
				else if (categoryname.length() > 18) {
					
					Toast.makeText(getActivity(), "Set name is required.", Toast.LENGTH_LONG).show();
				}
				else {
					   
					boolean isinternet=ImageUtil.isInternetOn(getActivity());
					if (isinternet) {
						//do here what
					
							prodialog2=ProgressDialog.show(getActivity(), "", "Creating and loading..");
							NewCategoryTask task=new NewCategoryTask();
							task.execute();
						}
						else{
							ImageUtil.showAlert(getActivity(), "No Internet Connection");
						}
						
						dialog.dismiss();
					}
					
		    	}
	    	}
		
		
		
		
/*		private class EditSetTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				String url=ImageConstant.BASEURL+"UpdateSetName";
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("cat_id", categoryid);
					obj1.put("cat_name", categoryname);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      Log.v(TAG, "Json object is: "+obj1.toString());
			      
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
				
				prodialog_editset.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				
				if (resultString != null && !resultString.isEmpty()) {
					
					// here update the result set
					
				try {
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Updated Successfully")) {
							
							ImageUtil.showAlert(getActivity(), "Updated Successfully");
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
		
		private class LoadSetTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				String url=ImageConstant.BASEURL+"getsetImages";
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("cat_id", categoryid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      Log.v(TAG, "Json object is: "+obj1.toString());
				
			      
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
				
				prodialog_set.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				
				if (resultString != null && !resultString.isEmpty()) {
					
					
					Intent intent=new Intent(getActivity(), LoadSetActivity.class);
					intent.putExtra("JSONAPI", resultString);
					startActivity(intent);
					 
							
					} 
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					  }
				
			}
		}
		
		
		private class NewCategoryTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
//				/http://picture-video-store.com/ws/?ws=1&act=Listofcategory&MemberID=1			
				//String url=ImageConstant.BASEURLMYUPLOAD;
				
				String url=ImageConstant.BASEURL+"Addcategory";
				
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("catname", categoryname);
					obj1.put("setthumbFile", "");
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      Log.v(TAG, "Json object is: "+obj1.toString());
				
			      
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
				        	
						   /* InputStream is = response.getEntity().getContent();
						    WebHelper webHelper = new WebHelper();
						    response1 = webHelper.convertStreamToString(is);*/
				        	
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
				
				prodialog2.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				catlist.clear();
				try {
				if (resultString != null && !resultString.isEmpty()) {
					
					 
							JSONObject jsonobj=new JSONObject(resultString);
							String status=jsonobj.getString("Status");
							if (status.trim().equalsIgnoreCase("Category Added  Successfully")) {
								
								categoryid=jsonobj.getString("CategoryID");
								
								showAlertSetCreat(getActivity(), "Set Created Successfully.");
								
								/*String partnerid=jsonobj.getString("PartnerID");
								String categoryid=jsonobj.getString("CategoryID");
								String catagoryname=jsonobj.getString("CategoryName");
								String imagecount=jsonobj.getString("ImageCount");
								String categoryimage=jsonobj.getString("CategoryImage");
								
								CatagoryModel model=new CatagoryModel(partnerid, categoryid, catagoryname, categoryimage, imagecount);
								catlist.add(model);
								CategoryAdapter adapter=new CategoryAdapter(getActivity(), catlist);
								Log.v(TAG, "list of size category: "+catlist.size());
								ShowListAlert(adapter);*/
							}
							else if (status.trim().equalsIgnoreCase("Category already Exist")) {
								ImageUtil.showAlert(getActivity(), "Same Set name already exist.");
							}
					
					} 
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					  }
				}
				catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
				}
			}
		}
		
		public  void showAlertSetCreat(Activity activity, String message) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			//builder.setCustomTitle(title); 
			builder.setMessage(message);
			
			builder.setCancelable(false);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					
					boolean isinternet=ImageUtil.isInternetOn(getActivity());
					if (isinternet) {
					//	jsonresp
						
						prodeialog_add=ProgressDialog.show(getActivity(), "", "Adding files..");
						AddImageMultiple task=new AddImageMultiple();
						task.execute();
						
					}
					else{
						ImageUtil.showAlert(getActivity(), "Internet Connection Error");
					}
					

				}

			});

			AlertDialog alert = builder.create();
			alert.show();
			TextView messageText = (TextView) alert.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		}
		
/*		private class ResetThumbTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				//http://picture-video-store.com/ws/?ws=1&act=updaterepcatimage&MemberID=1&origfilename=KIMG0019.JPG&cat_id=2
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				String splitvideourl[]=imageourl.split("/");
				String origfilename=splitvideourl[splitvideourl.length-1];
				
				String splitvideothumburl[]=imagethumbourl.split("/");
				String thumbfilename=splitvideothumburl[splitvideothumburl.length-1];
				Log.v(TAG, "origvideofilename is: "+origfilename);
				Log.v(TAG, "thumbfilename is: "+thumbfilename);
							
				//String url=ImageConstant.BASEURLMYUPLOAD;
				
				
				String url=ImageConstant.BASEURL+"updaterepcatimage";
				
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("origfilename", origfilename);
					obj1.put("cat_id", categoryid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      Log.v(TAG, "Json object is: "+obj1.toString());
				
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
				        	
						    InputStream is = response.getEntity().getContent();
						    WebHelper webHelper = new WebHelper();
						    response1 = webHelper.convertStreamToString(is);
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
				
				prodialog3.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				try {
				if (resultString != null && !resultString.isEmpty()) {
					
					
						JSONArray jsonarray=new JSONArray(resultString);
						for (int i = 0; i < jsonarray.length(); i++) {
							
							JSONObject jsonobj=new JSONObject(resultString);
							String status=jsonobj.getString("Status");
							if (status.trim().equalsIgnoreCase("Already Added inside Reset")) {
							
								ImageUtil.showAlert(getActivity(), "Already added this picture in a set");
								//break;
							}
							else if (status.trim().equalsIgnoreCase("Represent Set Image Updated Successfully")) {
								
								ImageUtil.showAlert(getActivity(), status);
								//break;
							}
						//}
					} 
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					  }
				}
				catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
			}
		}*/

		@Override
		public void onClick(View v) {
			
			if (btn_addtoset==v) {
				
				selected = adapter.getSelected();
				if (selected.size() > 0) {
					
					boolean isinternet=ImageUtil.isInternetOn(getActivity());
					if (isinternet) {
						
						prodialog1=ProgressDialog.show(getActivity(), "", "Loading..");
						CategoryTask task=new CategoryTask();
						task.execute();
					}
					else{
						ImageUtil.showAlert(getActivity(), "No Internet Connection");
					}
					
				}
				else{
					
					ImageUtil.showAlert(getActivity(), "Please select atleast one file.");
				}
				
				
				
			}
			else if (btn_delete==v) {
				
				selected = adapter.getSelected();
				if (selected.size() > 0) {
					
					showAlertDelete(getActivity(), "Are you sure you want to delete the images?");
					
				
				}
				else{
					
					ImageUtil.showAlert(getActivity(), "Please select atleast one file.");
				}
			}			
		}
		
		public void showAlertDelete(Activity activity, String message) {
			
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
					boolean isinternet=ImageUtil.isInternetOn(getActivity());
					if (isinternet) {
						
						prodialog_delete=ProgressDialog.show(getActivity(), "", "Sending request to delete files..");
						DeleteMultipleImages task=new DeleteMultipleImages();
						task.execute();
					}
					else{
						ImageUtil.showAlert(getActivity(), "No Internet Connection");
					}
					
				}

			});

			AlertDialog alert = builder.create();
			alert.show();
			TextView messageText = (TextView) alert.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		}
		
		private class DeleteMultipleImages extends AsyncTask<String, Void, String> {
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
						     
							MultiSelectPictureModel model=selected.get(i-1);
							String uploadedid=model.getUploadedid();
							obj1.put("Source_fileUploadID"+i, uploadedid);
						}
						
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
				      Log.v(TAG, "Json object is: "+obj1.toString());
					

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
				Log.v(TAG, "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Content Will be delete after Admin Approval")) {
							
							ImageUtil.showAlert(getActivity(), status);
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



		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
			selectedvalue=array_select[arg2];
			if (selectedvalue.trim().equalsIgnoreCase("Checked All")) {
				adapter.selectAll(true);
			}
			else if (selectedvalue.trim().equalsIgnoreCase("Unchecked All")) {
				adapter.unckeckedAll(false);
			}
			else if (selectedvalue.trim().equalsIgnoreCase("Select Multiple")) {
				adapter.unckeckedAll(false);
			}
			else if (selectedvalue.trim().equalsIgnoreCase("1-10")) {
				adapter.unckeckedAll(false);
				adapter.selectTencheck(true);
			}
			
			else if (selectedvalue.trim().equalsIgnoreCase("1-20")) {
				adapter.unckeckedAll(false);
				adapter.selectTwentycheck(true);
			}
			
			else if (selectedvalue.trim().equalsIgnoreCase("1-50")) {
				adapter.unckeckedAll(false);
				adapter.selectFiftycheck(true);
			}
			else if (selectedvalue.trim().equalsIgnoreCase("1-100")) {
				adapter.unckeckedAll(false);
				adapter.selectHundredcheck(true);
			}
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
			
		}

}
