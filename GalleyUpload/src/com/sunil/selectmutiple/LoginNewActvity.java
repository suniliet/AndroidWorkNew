package com.sunil.selectmutiple;

import java.io.IOException;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class LoginNewActvity extends Activity implements OnClickListener, OnTouchListener{

	private static final String TAG="LoginActivity";
	private Context context=null;
	private ImageButton btn_login=null;
	private ImageButton btn_back=null;
	private EditText edit_username=null;
	private EditText edit_pasw=null;
	private TextView txt_register=null;
	private ProgressDialog prodialog_affilate;
	
	private String username;
	private String password;
	private ProgressDialog prodialog=null;
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginnewscreen);
		context=this;
		
		edit_username=(EditText)findViewById(R.id.Editext_username);
		edit_pasw=(EditText)findViewById(R.id.editText_password);
		edit_pasw.setTypeface( Typeface.DEFAULT);
		txt_register=(TextView)findViewById(R.id.textView_register);
		txt_register.setOnTouchListener(this);
		
		btn_back=(ImageButton)findViewById(R.id.imageButton_back);
		btn_login=(ImageButton)findViewById(R.id.imageButton_login);
		
		btn_back.setOnClickListener(this);
		btn_login.setOnClickListener(this);
	}
	
	 private class LoginTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				//http://23.21.71.132/KNSGallery/web_service.php/?act=getmemID&ws=1&uname=sunil&psword=sunil123
				
				//String url=ImageConstant.BASEURL;
				Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String regid=Prefs.getString(ImageConstant.PROPERTY_REG_ID, "");
				
				String url=ImageConstant.BASEURL+"getmemID";
			
		
	                JSONObject obj1=new JSONObject();
					
					try {
						obj1.put("Username", username);
						obj1.put("UserPassword", password);
						obj1.put("Register_ID", regid);
						
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
				      //Log.v(TAG, "Json object is: "+obj1.toString());
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
					     
					         Log.v(TAG+".doInBackground", "json response code:" + responsecode);
					        if (responsecode==200) {
					        	
					        	String result = EntityUtils.toString(response.getEntity());   
							    //Log.v(TAG+".doInBackground", "Http response is:" + result);
							    ImageUtil.galleryLog(TAG, "Http response is:" + result);
							 /*   InputStream is = response.getEntity().getContent();
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
				
				prodialog.dismiss();
				Log.v(TAG, "onPostExecute called");
			//	Log.v(TAG, "Response is: "+resultString);
				  ImageUtil.galleryLog(TAG, "Response is: "+resultString);
				  
				if (resultString != null && !resultString.isEmpty()) {
					
					try{
						
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Success")) {
							
							String memberid=jsonobj.getString("MemberID");
							String username=jsonobj.getString("UserName");
							String affliatedid=jsonobj.getString("AffiliateID");
							
							  Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
							  SharedPreferences.Editor editor = Prefs.edit();
							  editor.putString(ImageConstant.USERNAME, username);
							  editor.putString(ImageConstant.MEMBERID, memberid);
							  editor.putString(ImageConstant.AFILIATEDID, affliatedid);
							  editor.commit();
							  
							  Intent intent=new Intent(LoginNewActvity.this, MainActivity.class);
							  startActivity(intent);
							  
							  finish();
							
							  
						}
						else if (status.trim().equalsIgnoreCase("Invalid Login Data")) {
							
							ImageUtil.showAlert(LoginNewActvity.this, "You are not an authenticated user.");
							
						}
						else if (status.trim().equalsIgnoreCase("Invalid Login Data /Wait for Admin Approval")) {
							
							ImageUtil.showAlert(LoginNewActvity.this, "You are not an authenticated user.");
						}
					    
					}catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				else{
					
					ImageUtil.showAlert(LoginNewActvity.this, getResources().getString(R.string.timeout));
				}

			}
		}

	@Override
	public void onClick(View arg0) {
		
		if (arg0==btn_back) {
			finish();
		}
		else if (btn_login==arg0) {
			
			username=edit_username.getText().toString().trim();
			password=edit_pasw.getText().toString().trim();
			
			if (username.length() < 1) {
				
				Toast.makeText(context, "Username is required.", Toast.LENGTH_LONG).show();
			}
			else if (password.length() < 1) {
				
				Toast.makeText(context, "Password is required.", Toast.LENGTH_LONG).show();
			}
			else{
				
				boolean isinternet=ImageUtil.isInternetOn(context);
				if (isinternet) {
					
					prodialog=ProgressDialog.show(context, "", "Authenticating...");
					LoginTask task=new LoginTask();
					task.execute();
				}
				else{
					ImageUtil.showAlert(LoginNewActvity.this, getResources().getString(R.string.internet_error));
				}
							
			}
			
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		 //  ImageUtil.showAlert(LoginNewActvity.this, "Please regsiter on website.");
		
		Intent intent=new Intent(LoginNewActvity.this, RegistrationActivity.class);
		startActivity(intent);
		/*
		boolean isinternet=ImageUtil.isInternetOn(context);
		if (isinternet) {
			prodialog_affilate=ProgressDialog.show(context, "", "Loading...");
			PartnerAffilatedTask task=new PartnerAffilatedTask();
			task.execute();
		}
		else{
			ImageUtil.showAlert(LoginNewActvity.this, "Internet Connection Error.");
		}*/
		
	/*	Intent intent=new Intent(LoginNewActvity.this, RegistrationActivity.class);
		startActivity(intent);
		*/
		return false;
	}

/*	 private class PartnerAffilatedTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				
				String url=ImageConstant.BASEURL+"getAffiliateID";
				
				JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("cat_ID", cat_id);
				
					
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
				
				prodialog_affilate.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				if (resultString != null ) {
					
					Intent intent=new Intent(LoginNewActvity.this, RegistrationActivity.class);
					intent.putExtra("JSONAPI", resultString);
					startActivity(intent);
					
				}
				else{
					ImageUtil.showAlert(LoginNewActvity.this, "Unable to connect to server.");
				}
			}
		}*/
}
