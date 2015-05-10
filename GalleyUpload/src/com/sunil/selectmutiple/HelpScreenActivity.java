package com.sunil.selectmutiple;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class HelpScreenActivity extends Activity implements OnClickListener{
	
	private static final String TAG="HelpScreenActivity";
	private Context context=null;
	private WebView webview_help=null;
	private ProgressDialog progressBar;
	private Button btn_help;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helpscreen);
		context=this;
		
		webview_help=(WebView)findViewById(R.id.webView_help);
		btn_help=(Button)findViewById(R.id.button_help_back);
		btn_help.setOnClickListener(this);
		
		WebSettings settings = webview_help.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		webview_help.setWebChromeClient(new WebChromeClient());
		webview_help.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		progressBar = ProgressDialog.show(HelpScreenActivity.this, "","Loading...");
		progressBar.setCancelable(true);
		
		webview_help.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				//Log.i(TAG, "Processing webview url click...");
				ImageUtil.galleryLog(TAG, "Processing webview url click...");
				view.loadUrl(url);
				return true;
			}

			public void onPageFinished(WebView view, String url) {
				//Log.i(TAG, "Finished loading URL: " + url);
				ImageUtil.galleryLog(TAG, "Finished loading URL: " + url);
				if (progressBar.isShowing()) {
					progressBar.dismiss();
				}
			}

			@SuppressWarnings("deprecation")
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				//Log.e(TAG, "Error: " + description);
				ImageUtil.galleryLog(TAG, "Error: " + description);
				Toast.makeText(HelpScreenActivity.this, "Oh no! " + description,Toast.LENGTH_SHORT).show();
				alertDialog.setTitle("Error");
				alertDialog.setMessage(description);
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								return;
							}
						});
				alertDialog.show();
			}
		});
		webview_help.loadUrl(ImageConstant.HELPURL);  // loading url here
	
	}

	@Override
	public void onClick(View arg0) {
		
		if (btn_help==arg0) {
			finish();
		}
	}

}
