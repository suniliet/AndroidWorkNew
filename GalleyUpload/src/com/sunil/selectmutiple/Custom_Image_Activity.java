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

import com.kns.adapter.Custom_Image_Adapter;
import com.kns.model.Custom_Image_Model;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class Custom_Image_Activity extends Activity implements OnClickListener{

	private static final String TAG="Custom_Image_Activity";
	private Context context=null;
	private GridView gridGallery;
	List<Custom_Image_Model> list_images=new ArrayList<Custom_Image_Model>();
	Custom_Image_Adapter adapter=null;
	private ImageButton btn_okay;
	private ImageButton btn_back;
	ArrayList<Custom_Image_Model> selected=null;
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	private ProgressDialog prodeialog_add;
	private String cunsumerid="";
	String imageurl="";
	String imagethmb="";
	String RequestFormID="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_image_screen);
		context=this;
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		btn_okay=(ImageButton)findViewById(R.id.btnGalleryOk);
		btn_back=(ImageButton)findViewById(R.id.imageButton_back);
		btn_okay.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		
		Bundle bundle=getIntent().getExtras();
		if (bundle != null) {
			
			String jsonresponse=bundle.getString("JSONAPI");
			cunsumerid=bundle.getString("cunsumerid");
			RequestFormID=bundle.getString("RequestFormID");
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
			
			if (list_images.size() > 0) {
				
			}
			else{
				
				Toast.makeText(context, "You don't have any files to add. Please upload the files first.", Toast.LENGTH_LONG).show();
			}
			
			adapter=new Custom_Image_Adapter(Custom_Image_Activity.this, list_images);
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
	public void onClick(View v) {
		
		if (btn_back==v) {
			finish();
		}
		else if (btn_okay==v) {
			
			selected = adapter.getSelected();
			
			if (selected.size() > 0) {
				
				
				
				boolean isinternet=ImageUtil.isInternetOn(context);
				if (isinternet) {
					
					prodeialog_add=ProgressDialog.show(context, "", "Adding the images..");
					AddImageTask task=new AddImageTask();
					task.execute();
					
				}else{
					ImageUtil.showAlert(Custom_Image_Activity.this, getResources().getString(R.string.internet_error));
				}
				
			}
			else{
				
				Toast.makeText(context, "Please select checkbox to add image", Toast.LENGTH_LONG).show();
				
			}
			
			
		}
		
	}
	
	private class AddImageTask extends AsyncTask<String, Void, String> {
		String response1 = "";
		
		@Override
		protected String doInBackground(String... urls) {
		
			Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
			String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
//			/http://picture-video-store.com/ws/?ws=1&act=Listofcategory&MemberID=1			
			//String url=ImageConstant.BASEURLMYUPLOAD;
			
			
			String url=ImageConstant.BASEURL+"AddCustomRequest_UploadDetails";
			
            JSONObject obj1=new JSONObject();
			
			try {
				obj1.put("MemberID", memberid);
				obj1.put("consumer_id", cunsumerid);
				obj1.put("RequestFormID", RequestFormID);
				obj1.put("totalcount", selected.size());
				for (int i = 1; i <= selected.size(); i++) {
					
					Custom_Image_Model model=selected.get(i-1);
					 imageurl=model.getImagerealurl();
					 imagethmb=model.getImagethumburl();
					 obj1.put("Source_fileName"+i, imageurl);
					 obj1.put("Source_thumbfileName"+i, imagethmb);
				}
				
				obj1.put("Source_filetype", "image");
				
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
			       //  Log.v(TAG+".doInBackground", "json response code:" + responsecode);
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
			
			prodeialog_add.dismiss();
			Log.v(TAG, "onPostExecute called");
			
		try {
			
			if (resultString != null && !resultString.isEmpty()) {
				
				JSONObject jsonobj=new JSONObject(resultString);
				String status=jsonobj.getString("Status");
				if (status.trim().equalsIgnoreCase("success")) {
					
					showAlert(Custom_Image_Activity.this, "Image Added Successfully.");
				}
				else{
					
					ImageUtil.showAlert(Custom_Image_Activity.this, "Same picture already exist.");
				}
			} 
			else{
				
				ImageUtil.showAlert(Custom_Image_Activity.this, getResources().getString(R.string.timeout));
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
