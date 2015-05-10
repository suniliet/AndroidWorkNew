package com.kns.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

public class ImageUtil {
	
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	
	public static void showAlert(Activity activity, String message) {
		
		/*TextView title = new TextView(activity);
		title.setText("Gallery Upload");
		title.setPadding(10, 10, 10, 10);
		title.setGravity(Gravity.CENTER);
		//title.setTextColor(Color.WHITE);
		title.setTextSize(20);*/
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		//builder.setCustomTitle(title); 
		builder.setMessage(message);
		
		builder.setCancelable(false);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();

			}

		});

		AlertDialog alert = builder.create();
		alert.show();
		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}
	
	public static boolean isInternetOn(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// test for connection
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			Log.v("Util", "Internet is working");
			// txt_status.setText("Internet is working");
			return true;
		} else {
			// txt_status.setText("Internet Connection Not Present");
			Log.v("Util", "Internet Connection Not Present");
			return false;
		}
	}
	
	/*public static void checkNetworkConnection(Context context) {
	    ConnectivityManager connMgr =
	        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
	    if (activeInfo != null && activeInfo.isConnected()) {
	        wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
	        mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
	        if(wifiConnected) {
	            Log.i(TAG, "WIFI connected");
	        } else if (mobileConnected){
	            Log.i(TAG, "Mobile Connected");
	        }
	    } else {
	        Log.i(TAG,"Neither Mobile nor WIFi connected.");
	    }
	  }
*/
	
	public static void galleryLog( String tag, String message){
		
	     if (false) {
			
	    	 Log.v(tag, message);
		}
		
	}
	

}
