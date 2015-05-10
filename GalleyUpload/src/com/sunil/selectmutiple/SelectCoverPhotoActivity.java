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
import android.widget.Toast;

import com.kns.adapter.Partner_Image_Adapter1;
import com.kns.model.PartnerImage_Model;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class SelectCoverPhotoActivity extends Activity implements OnClickListener, OnItemClickListener{
	
	private final static String TAG="SelectCoverPhotoActivity";
	private Context context=null;
	private ImageButton btn_back;
	//private Button btn_setphoto;
	private GridView gridview;
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	private ProgressDialog prodialog3_uudate;
	String jsonresp="";
	List<PartnerImage_Model> list_images=new ArrayList<PartnerImage_Model>();
	String imageurl="";
	String thumburl="";
	String setid="";
	private String partnerid="";
	private String categoryid="";
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectcoverphoto);
		
		context=this;
		
		btn_back=(ImageButton)findViewById(R.id.imageButton_backmulti);
		//btn_setphoto=(Button)findViewById(R.id.imageButton_add);
		gridview=(GridView)findViewById(R.id.gridView_selectimage);
		gridview.setOnItemClickListener(this);
		btn_back.setOnClickListener(this);
		//btn_setphoto.setOnClickListener(this);
        		
        		Bundle bundle=getIntent().getExtras();
    			if (bundle != null) {
    				
    				String jsonresponse=bundle.getString("JSONAPI");
    				setid=bundle.getString("setid");
    				//Log.v(TAG, "set id is: "+setid);
    				ImageUtil.galleryLog(TAG,   "set id is: "+setid);
				
				try {
					
					
					if (jsonresponse != null && !jsonresponse.isEmpty()) {
						
						        list_images.clear();
						        JSONArray jsonaaray=new JSONArray(jsonresponse);
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
						        			list_images.add(model);
						        			//list_image.add(model);
										}
									}
						        	
								}
								
						} 
						else{
							ImageUtil.showAlert(SelectCoverPhotoActivity.this, "Unable to connect to server. Please try again.");
						  }
					
					
				/*	JSONArray jsonarray=new JSONArray(jsonresponse);
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject obj=jsonarray.getJSONObject(i);
						String status=obj.getString("Status");
						String partnerid=obj.getString("PartnerID");
						
						JSONArray array=obj.getJSONArray("Image");
						for (int j = 0; j < array.length(); j++) {
							
							JSONObject obj1=array.getJSONObject(j);
							String imageurl=obj1.getString("RealImage");
							String imagethumburl=obj1.getString("ThumbImage");
							
							PartnerImage_Model model=new PartnerImage_Model("", imageurl, imagethumburl);
							
							//MultiSelectPictureModel model=new MultiSelectPictureModel(imageurl, imagethumburl, false);
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
					//textviwe.setVisibility(View.GONE);
				}
				else{
					//textviwe.setText("Not yet uploaded any picture.");
					Toast.makeText(SelectCoverPhotoActivity.this, "Not yet added any picture in this set to make cover photo.", Toast.LENGTH_LONG).show();
				}
				Partner_Image_Adapter1 adapter=new Partner_Image_Adapter1(SelectCoverPhotoActivity.this, list_images);
				gridview.setAdapter(adapter);
				
			}
		
	}

	@Override
	public void onClick(View arg0) {
		
		if (btn_back==arg0) {
			
			finish();
			
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		PartnerImage_Model model=list_images.get(arg2);
		 imageurl=model.getImagerealurl();
		 thumburl=model.getImagethumburl();
		
		 boolean isinternet=ImageUtil.isInternetOn(context);
			if (isinternet) {
				
				prodialog3_uudate=ProgressDialog.show(SelectCoverPhotoActivity.this, "", "Updating cover photo..");
				
				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    		    new ResetThumbTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	    		} else {
	    		    new ResetThumbTask().execute();
	    		}
				/*ResetThumbTask task=new ResetThumbTask();
				
				task.execute();*/
			}
			else{
				ImageUtil.showAlert(SelectCoverPhotoActivity.this, getResources().getString(R.string.internet_error));
			}
			
		
	}
	
	private class ResetThumbTask extends AsyncTask<String, Void, String> {
		String response1 = "";
		
		@Override
		protected String doInBackground(String... urls) {
		
			//http://picture-video-store.com/ws/?ws=1&act=updaterepcatimage&MemberID=1&origfilename=KIMG0019.JPG&cat_id=2
			Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
			
			/*String splitvideourl[]=imageurl.split("/");
			String origfilename=splitvideourl[splitvideourl.length-1];
			
			String splitvideothumburl[]=thumburl.split("/");
			String thumbfilename=splitvideothumburl[splitvideothumburl.length-1];
			Log.v(TAG, "origvideofilename is: "+origfilename);
			Log.v(TAG, "thumbfilename is: "+thumbfilename);*/
						
			//String url=ImageConstant.BASEURLMYUPLOAD;
			
			
			String url=ImageConstant.BASEURL+"updaterepcatimage";
			ImageUtil.galleryLog(TAG, "Image url for thumb: "+imageurl);
			
            JSONObject obj1=new JSONObject();
			
			try {
				obj1.put("MemberID", memberid);
				obj1.put("origfilename", imageurl);
				obj1.put("cat_id", setid);
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		     // Log.v(TAG, "Json object is: "+obj1.toString());
		      ImageUtil.galleryLog(TAG,   "Json object is: "+obj1.toString());
			
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
			       //  Log.v(TAG+".doInBackground", "json response code:" + responsecode);
			         
			         ImageUtil.galleryLog(TAG, "Http response is:" + result);
			         ImageUtil.galleryLog(TAG,  "json response code:" + responsecode);
			         
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
			ImageUtil.galleryLog(TAG,  "Response is: "+resultString);
			try {
			if (resultString != null && !resultString.isEmpty()) {
				
			
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Already Added inside Reset")) {
						
							ImageUtil.showAlert(SelectCoverPhotoActivity.this, "Already added this picture in a set");
							//break;
						}
						else if (status.trim().equalsIgnoreCase("Represent Set Image Updated Successfully")) {
							
							showAlert(SelectCoverPhotoActivity.this, "Update Successful");
							//break;
						}
					//}
				} 
				else{
					ImageUtil.showAlert(SelectCoverPhotoActivity.this, getResources().getString(R.string.timeout));
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
