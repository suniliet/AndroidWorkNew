package com.sunil.selectmutiple;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kns.util.ImageUtil;

public class LoginActivity extends Activity implements OnClickListener{
	
	private static final String TAG="LoginActivity";
	private Context context=null;
	
	private Button btn_login=null;
	private Button btn_cancel=null;
	private EditText edit_username=null;
	private EditText edit_pasw=null;
	
	private String username;
	private String password;
	private ProgressDialog prodialog=null;
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		context=this;
		
		edit_username=(EditText)findViewById(R.id.editText_username);
		edit_pasw=(EditText)findViewById(R.id.editText_pass);
		edit_pasw.setTypeface( Typeface.DEFAULT);
		/*btn_cancel=(Button)findViewById(R.id.button_cancel);
		btn_login=(Button)findViewById(R.id.button_login);
		
		btn_cancel.setOnClickListener(this);
		btn_login.setOnClickListener(this);*/
	}
	@Override
	public void onClick(View v) {
		
		if (btn_cancel==v) {
			
			finish();
		}
		else if (btn_login==v) {
			
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
					
				/*	prodialog=ProgressDialog.show(context, "", "Authenticating...");
					LoginTask task=new LoginTask();
					task.execute();*/
				}
				else{
					ImageUtil.showAlert(LoginActivity.this, getResources().getString(R.string.internet_error));
				}
							
			}
			
		}
	}

/*	 private class LoginTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				//http://23.21.71.132/KNSGallery/web_service.php/?act=getmemID&ws=1&uname=sunil&psword=sunil123
				
				String url=ImageConstant.BASEURL;
				
				StringBuilder sb=new StringBuilder();
				sb.append(url);
				sb.append("&act=getmemID");
				sb.append("&ws=1");
				sb.append("&uname="+username);
				sb.append("&psword="+password);
				
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
				
				prodialog.dismiss();
				Log.v(TAG, "onPostExecute called");
				Log.v(TAG, "Response is: "+resultString);
				if (resultString != null) {
					
				    Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
				    SharedPreferences.Editor editor = Prefs.edit();
					editor.putString(ImageConstant.USERNAME, username);
					editor.putString(ImageConstant.PASSWORD, password);
					editor.commit();
					
				}

			}
		}
*/
	 
}
