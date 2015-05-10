package com.sunil.selectmutiple;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.kns.adapter.GalleryVideoAdapter;
import com.kns.util.CustomMultiPartEntity;
import com.kns.util.CustomMultiPartEntity.ProgressListener;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;
import com.kns.web.WebHelper;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class GridShowVideoActivity extends Activity implements OnClickListener{
	
	public static final String ACTION_MULTIPLE_PICK = "video.ACTION_MULTIPLE_PICK";
	private static final String TAG="GridShowImageActivity";
	GridView gridGallery;
	Handler handler;
	GalleryVideoAdapter adapter;
	Button btnGalleryPick;
	ImageButton btnGalleryPickMul;
	ImageButton btn_uploadvideo;
	ImageButton btn_back=null;
	String action;
	ViewSwitcher viewSwitcher;
	ImageLoader imageLoader;
	Context context=null;
	
	private ProgressDialog prodialog=null;
	private ProgressDialog prodialog1=null;
	
	private EditText edit_username=null;
	private EditText edit_pasw=null;
	
	private String username;
	private String password;

	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	long totalsize;
	
	ArrayList<CustomVideoGallery> dataT = new ArrayList<CustomVideoGallery>();
	ArrayList<CustomVideoGallery> dataID = new ArrayList<CustomVideoGallery>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gridshowvideo);
		context=this;
		
		initImageLoader();
		
		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryVideoAdapter(GridShowVideoActivity.this, imageLoader);
		adapter.setMultiplePick(false);
		gridGallery.setAdapter(adapter);

		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		viewSwitcher.setDisplayedChild(1);

		btnGalleryPickMul = (ImageButton) findViewById(R.id.btnGalleryPickMul);
		btn_uploadvideo = (ImageButton) findViewById(R.id.button_videoupload);
		btn_back=(ImageButton) findViewById(R.id.imageButton_back);
		btn_back.setOnClickListener(this);
		btnGalleryPickMul.setOnClickListener(this);
		btn_uploadvideo.setOnClickListener(this);

	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).memoryCache(
				new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}
	
	
	@Override
	protected void onResume() {
		
		Intent intent=getIntent();
		if (intent !=null) {
			
			String[] all_path = intent.getStringArrayExtra("all_path");
			String[] all_id = intent.getStringArrayExtra("all_id");
			//ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
			
		if (all_id.length > 0) {
				
			dataT.clear();
			dataID.clear();
			
			for (String string : all_path) {
				CustomVideoGallery item = new CustomVideoGallery();
				item.sdcardPaththumbvideo = string;
				 //Log.v(TAG, "path is: "+string);
				 ImageUtil.galleryLog(TAG,"path is: "+string);
				dataT.add(item);
			}
			
			for (String string : all_id) {
				CustomVideoGallery item = new CustomVideoGallery();
				item.video_id = Integer.parseInt(string);
				 //Log.v(TAG, "id is: "+string);
				 ImageUtil.galleryLog(TAG,"id is: "+string);
				dataID.add(item);
			}

			//viewSwitcher.setDisplayedChild(0);
			adapter.addAll(dataID);
			
		}
			
		}
		
		super.onResume();
	}
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			adapter.clear();

			viewSwitcher.setDisplayedChild(1);
			String single_path = data.getStringExtra("single_path");
			imageLoader.displayImage("file://" + single_path, imgSinglePick);

		} else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
			String[] all_path = data.getStringArrayExtra("all_path");
			String[] all_id = data.getStringArrayExtra("all_id");
			//ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
			dataT.clear();
			dataID.clear();
			
			for (String string : all_path) {
				CustomVideoGallery item = new CustomVideoGallery();
				item.sdcardPaththumbvideo = string;
				 Log.v(TAG, "path is: "+string);
				dataT.add(item);
			}
			
			for (String string : all_id) {
				CustomVideoGallery item = new CustomVideoGallery();
				item.video_id = Integer.parseInt(string);
				 Log.v(TAG, "id is: "+string);
				dataID.add(item);
			}

			//viewSwitcher.setDisplayedChild(0);
			adapter.addAll(dataID);
		}
	}
*/
	@Override
	public void onClick(View v) {
		
		if (btnGalleryPickMul==v) {
			
		/*	Intent intent=new Intent(GridShowVideoActivity.this, VideoGalleryActvity.class);
			startActivity(intent);
			*/
			Intent i = new Intent(ACTION_MULTIPLE_PICK);
			startActivityForResult(i, 200);
			
			finish();
		}
		else if (btn_uploadvideo==v) {
			
			if (dataT.size() > 0) {
				
				//alertBox();
				
				  UploadingTask task=new UploadingTask();
				  task.execute();
		
			}
			else{
				
				ImageUtil.showAlert(GridShowVideoActivity.this, "No Video available for upload.");
			}
		}
		else if (btn_back==v) {
			 finish();
		}
	}
	
/*	public  String postFile(ArrayList<CustomVideoGallery> dataT2, String custom_id) throws Exception {

		Log.v(TAG, "file size is: "+dataT2.size());
	    HttpClient client = new DefaultHttpClient();
	    HttpPost post = new HttpPost("http://23.21.71.132/KNSGallery/video_upload.php");
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
	    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

	    for (int i = 1; i <= dataT2.size(); i++) {
	    	CustomVideoGallery gallery=dataT2.get(i-1);
	    	String filename=gallery.sdcardPaththumbvideo;
	    	Log.v(TAG, "file name: "+filename);
	    	File file = new File(filename);
		    FileBody fb = new FileBody(file);
		    builder.addPart("file"+i, fb);  
		}
	    final File file = new File(fileName);
	    FileBody fb = new FileBody(file);

	   // builder.addPart("file", fb);  
	    
	 
	    
	    builder.addTextBody("member_id", custom_id);
	    builder.addTextBody("count", String.valueOf(dataT2.size()));
	    
	    final HttpEntity yourEntity = builder.build();

	    class ProgressiveEntity implements HttpEntity {
	        @Override
	        public void consumeContent() throws IOException {
	            yourEntity.consumeContent();                
	        }
	        @Override
	        public InputStream getContent() throws IOException,
	                IllegalStateException {
	            return yourEntity.getContent();
	        }
	        @Override
	        public Header getContentEncoding() {             
	            return yourEntity.getContentEncoding();
	        }
	        @Override
	        public long getContentLength() {
	        	totalsize=yourEntity.getContentLength();
	            return totalsize;
	        }
	        @Override
	        public Header getContentType() {
	            return yourEntity.getContentType();
	        }
	        @Override
	        public boolean isChunked() {             
	            return yourEntity.isChunked();
	        }
	        @Override
	        public boolean isRepeatable() {
	            return yourEntity.isRepeatable();
	        }
	        @Override
	        public boolean isStreaming() {             
	            return yourEntity.isStreaming();
	        } // CONSIDER put a _real_ delegator into here!

	        @Override
	        public void writeTo(OutputStream outstream) throws IOException {

	            class ProxyOutputStream extends FilterOutputStream {

	                public ProxyOutputStream(OutputStream proxy) {
	                    super(proxy);    
	                }
	                public void write(int idx) throws IOException {
	                    out.write(idx);
	                }
	                public void write(byte[] bts) throws IOException {
	                    out.write(bts);
	                }
	                public void write(byte[] bts, int st, int end) throws IOException {
	                	
	                	
	                    out.write(bts, st, end);
	                }
	                public void flush() throws IOException {
	                    out.flush();
	                }
	                public void close() throws IOException {
	                    out.close();
	                }
	            } // CONSIDER import this class (and risk more Jar File Hell)

	            class ProgressiveOutputStream extends ProxyOutputStream {
	            
	                public ProgressiveOutputStream(OutputStream proxy) {
	                    super(proxy);
	                    
	                }
	                public void write(byte[] bts, int st, int end) throws IOException {

	                    // FIXME  Put your progress bar stuff here!
	             
	                	  
	                    out.write(bts, st, end);
	                }
	            }

	            yourEntity.writeTo(new ProgressiveOutputStream(outstream));
	        }

	    };
	    ProgressiveEntity myEntity = new ProgressiveEntity();

	    post.setEntity(myEntity);
	    HttpResponse response = client.execute(post);        

	    return getContent(response);

	} */
	
	public  String getContent(HttpResponse response) throws IOException {
	    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    String body = "";
	    String content = "";

	    while ((body = rd.readLine()) != null) 
	    {
	        content += body + "\n";
	    }
	    return content.trim();
	}
	
	 private class UploadingTask extends AsyncTask<String, Integer, String> {
		
			
		   @Override
		   protected void onPreExecute() {
				
						prodialog1 = new ProgressDialog(context);
						prodialog1.setTitle("In progress...");
						prodialog1.setMessage("Uploading Video...");
						prodialog1.setCanceledOnTouchOutside(false);
						//prodialog1.setIcon(R.drawable.upload);
						//prodialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						//prodialog1.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progress));
						prodialog1.setCancelable(true);
						prodialog1.setButton(DialogInterface.BUTTON_NEGATIVE, "Upload In Background", new DialogInterface.OnClickListener() {
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
						        dialog.dismiss();
						    }
						});
						prodialog1.show();
				}	
				 
			
			@Override
			protected String doInBackground(String... urls) {
				String response1 = "";
		    	//http://23.21.71.132/KNSGallery/web_service.php?act=getmemID&ws=1&devID=123&andID=234
				
				Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				Log.v(TAG, "member id is:"+ memberid);
				Log.v(TAG, "count  is:"+ dataT.size());
				try {
					
					String url=ImageConstant.BASEURL+"video_upload.php";
					//"http://23.21.71.132/KNSGallery/video_upload.php"
					//Log.v(TAG, "file size is: "+dataT.size());
					 ImageUtil.galleryLog(TAG,"file size is: "+dataT.size());
				    HttpClient client = new DefaultHttpClient();
				    HttpPost post = new HttpPost(url);
				    MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
				    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

				    for (int i = 1; i <= dataT.size(); i++) {
				    	CustomVideoGallery gallery=dataT.get(i-1);
				    	String filename=gallery.sdcardPaththumbvideo;
				    	//Log.v(TAG, "file name: "+filename);
				    	 ImageUtil.galleryLog(TAG,"file name: "+filename);
				    	
				    	File file = new File(filename);
					    FileBody fb = new FileBody(file);
					    builder.addPart("file"+i, fb);  
					}
				 
				    builder.addTextBody("member_id", memberid);
				    builder.addTextBody("count", String.valueOf(dataT.size()));
				    
				    final HttpEntity yourEntity = builder.build();
					
				    CustomMultiPartEntity entity=new CustomMultiPartEntity(yourEntity, new ProgressListener() {
						
						@Override
						public void transferred(long num) {
						
							publishProgress((int) ((num / (float) totalsize) * 100));
						}
					});
				    totalsize = entity.getContentLength();
				    post.setEntity(entity);
				    HttpResponse response = client.execute(post);        
				    response1= getContent(response);
				   
					// response=postFile(dataT, memberid);
				//	Log.v(TAG, "response is: "+ response);
					} catch (Exception e) {
						e.printStackTrace();
					}
				return response1;
			  
		}
			
			@Override
			protected void onProgressUpdate(Integer... progress) {
		
				prodialog1.setProgress((int) (progress[0]));
			}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog1.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				 ImageUtil.galleryLog(TAG,"Response is: "+resultString);
				if (resultString != null) {
					
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String resp=jsonobj.getString("Success");
						//ImageUtil.showAlert(GridShowVideoActivity.this, resp);
						Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
			    	catch (Exception e) {
					e.printStackTrace();
				}
					
				}
				else{
					ImageUtil.showAlert(GridShowVideoActivity.this, "Problem in Internet Processing. Please try again.");
				}

			}
		}
	 
	 

	/* private void alertBox() {

			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.login, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);
			edit_username=(EditText)promptsView.findViewById(R.id.editText_username);
			edit_pasw=(EditText)promptsView.findViewById(R.id.editText_pass);
			
			// set dialog message
			alertDialogBuilder.setCancelable(false).setPositiveButton("Login",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
			
			alertDialogBuilder.setCancelable(false).setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
						}
					});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			Button theButton = alertDialog
					.getButton(DialogInterface.BUTTON_POSITIVE);
			theButton.setOnClickListener(new CustomListener(alertDialog));

		}

	 class CustomListener implements View.OnClickListener {
			private final Dialog dialog;

			public CustomListener(Dialog dialog) {
				this.dialog = dialog;
			}

			@Override
			public void onClick(View v) {

			
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
						dialog.dismiss();
						
						prodialog=ProgressDialog.show(context, "", "Authenticating...");
						LoginTask task=new LoginTask();
						task.execute();
						
					}
					else{
						ImageUtil.showAlert(GridShowVideoActivity.this, "Internet Connection Error.");
					}
				}
			}
		}
	 
	 
	 private class LoginTask extends AsyncTask<String, Void, String> {
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
					
					try{
						
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.equalsIgnoreCase("Success")) {
							
							String memberid=jsonobj.getString("MemberID");
							String username=jsonobj.getString("UserName");
							
							  Prefs = context.getSharedPreferences(prefname, Context.MODE_PRIVATE);
							  SharedPreferences.Editor editor = Prefs.edit();
							  editor.putString(ImageConstant.USERNAME, username);
							  editor.putString(ImageConstant.MEMBERID, memberid);
							  editor.commit();
							  
							 // prodialog=ProgressDialog.show(context, "", "Uploading file..");
							  UploadingTask task=new UploadingTask();
							  task.execute();
							  
						}
						else if (status.equalsIgnoreCase("Invalid Login Data")) {
							
							ImageUtil.showAlert(GridShowVideoActivity.this, "You are not an authenticated user. Please register on website.");
							
						}
					  
					}catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				else{
					
					ImageUtil.showAlert(GridShowVideoActivity.this, "Problem in Internet Processing.Please try again.");
				}

			}
		}*/
	 
}
