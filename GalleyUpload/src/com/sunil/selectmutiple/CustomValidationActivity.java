package com.sunil.selectmutiple;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;
import com.kns.web.WebHelper;

public class CustomValidationActivity extends Activity implements OnClickListener{
	
	private final static String TAG="CustomValidationActivity";
	private Context context=null;
	private Button btn_customid;
	private ProgressDialog dialog=null;
	String IMEI_deviceId="";
	String m_androidId ="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.validation);
		context=this;
		
		btn_customid=(Button)findViewById(R.id.button_customid);
		btn_customid.setOnClickListener(this);
		
		TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		IMEI_deviceId = TelephonyMgr.getDeviceId();
		m_androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		
		Log.v(TAG, "IMEI number is: "+IMEI_deviceId);
		Log.v(TAG, "Android id: "+m_androidId);
		
		
	}
	@Override
	public void onClick(View arg0) {
		
		if (arg0==btn_customid) {
			
			boolean isinternet=ImageUtil.isInternetOn(context);
			if (isinternet) {
				
			/*	dialog=ProgressDialog.show(context, "", "validating...");
				ValidationTask task=new ValidationTask();
				task.execute();*/
			}
			else{
				
				ImageUtil.showAlert(CustomValidationActivity.this, getResources().getString(R.string.internet_error));
				
			}
		}
	}
	
/*	 private class ValidationTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
		    	//http://23.21.71.132/KNSGallery/web_service.php?act=getmemID&ws=1&devID=123&andID=234
				
				String url=ImageConstant.BASEURL;
				
				StringBuilder sb=new StringBuilder();
				sb.append(url);
				sb.append("&act=getmemID");
				sb.append("&ws="+"1");
				sb.append("&devID="+IMEI_deviceId);
				sb.append("&andID="+m_androidId);
				
				String httpurl=sb.toString().replace(" ", "%20");
				Log.v(TAG, "url is: "+httpurl);
			
			    HttpClient httpclient = new DefaultHttpClient();
			    HttpPost  httppost = new HttpPost(httpurl);
				try {
				
					 HttpResponse response = httpclient.execute(httppost);
					 InputStream is = response.getEntity().getContent();
				     WebHelper webHelper = new WebHelper();
					 response1 = webHelper.convertStreamToString(is);
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				dialog.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				if (resultString != null) {
					
					
					
				}

			}
		}

	 */

}
