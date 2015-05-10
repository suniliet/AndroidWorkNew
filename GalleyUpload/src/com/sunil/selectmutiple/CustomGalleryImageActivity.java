package com.sunil.selectmutiple;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kns.adapter.CategoryAdapter;
import com.kns.adapter.GalleryAdapter;
import com.kns.db.DBHelper;
import com.kns.model.CategoryModel;
import com.kns.model.ImageModel;
import com.kns.model.Pending_Uploadurl_model;
import com.kns.model.PnedingUpload_model;
import com.kns.service.UploadFileService;
import com.kns.util.CustomMultiPartEntity;
import com.kns.util.CustomMultiPartEntity.ProgressListener;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class CustomGalleryImageActivity extends Activity implements OnClickListener, OnSeekBarChangeListener{
	
	public static final String ACTION_MULTIPLE_PICK = "ACTION_MULTIPLE_PICK";
	private static final String TAG="CustomGalleryActivity";
	private GridView gridGallery;
	private Handler handler;
	private GalleryAdapter adapter;
	private ImageButton btnGalleryOk;
    private Context context=null;
	private String action;
	private ImageLoader imageLoader;
	private ImageButton btn_takepic;
	private ImageButton btn_back;
	public final static String APP_PATH_SD_CARD = "/ImageUpload";
	final static int CAMERA_RESULT = 0;
	public static File CurrentFile = null;
	public ArrayList<String> GalleryList = new ArrayList<String>();
	private int totalsize=0;
	private long totalsizeimage=0;
	private TextView textseekbar=null;
	int seekbarprogress=0;
	private String username;
	private String password;

	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	private ProgressDialog prodialog=null;
	private ProgressDialog prodialog1=null;
	ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
	private DBHelper db=null;
	NotificationManager NM;
	private ProgressDialog prodoalog_cat;
	List<CategoryModel> list;
	AlertDialog dialog;
	List<CategoryModel> list_category=new ArrayList<CategoryModel>();
	ArrayList<CategoryModel> checked_category=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_gallery);
		context=this;
		db=new DBHelper(getApplicationContext());
	/*	action = getIntent().getAction();
		if (action == null) {
			finish();
		}*/
		initImageLoader();
		
		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		btn_takepic= (ImageButton) findViewById(R.id.button_takepic);
		btn_back= (ImageButton) findViewById(R.id.imageButton_back);
		btn_back.setOnClickListener(this);
		btn_takepic.setOnClickListener(this);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(CustomGalleryImageActivity.this, imageLoader);
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, true, true);
		gridGallery.setOnScrollListener(listener);

		//if (action.equalsIgnoreCase(ACTION_MULTIPLE_PICK)) {

			findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
			adapter.setMultiplePick(true);

	//	} 

		gridGallery.setAdapter(adapter);
		
		btnGalleryOk = (ImageButton) findViewById(R.id.btnGalleryOk);
		btnGalleryOk.setOnClickListener(mOkClickListener);

		boolean isinternet=ImageUtil.isInternetOn(context);
		if (isinternet) {
			
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
    		    new PartnerApprovedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    		} else {
    		    new PartnerApprovedTask().execute();
    		}
			/*
			PartnerApprovedTask task1=new PartnerApprovedTask();
			task1.execute();*/
		}
		
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
		        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,  Uri.parse("file://" + Environment.getExternalStorageDirectory()));
		        this.sendBroadcast(mediaScanIntent);
		}
		else
		{
		       sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		} */
		
		//getbucketname();
		
		
		/*File dir = new File(Environment.getExternalStorageDirectory()+"/");    
		String[] fileNames = dir.list(new FilenameFilter() { 
		  public boolean accept (File dir, String name) {
		      if (new File(dir,name).isDirectory())
		         return false;
		    
		      return ((name.toLowerCase().endsWith(".png")) || (name.toLowerCase().endsWith(".jpg")));
		  }
		});
		
		Toast.makeText(context, "no of folder is: "+fileNames.length, Toast.LENGTH_LONG).show();
		
		for(String bitmapFileName : fileNames) {
		  Bitmap bmp = BitmapFactory.decodeFile(dir.getPath() + "/" + bitmapFileName);
		  // do something with bitmap
		}*/
		
	}
	
	@Override
	protected void onResume() {
		
		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter.addAll(getGalleryPhotos());
						//checkImageStatus();
					}
				});
				Looper.loop();
			};

		}.start();

		
		
		super.onResume();
	}
	
	private void initImageLoader() {
		try {
			String CACHE_DIR = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/.temp_tmp";
			new File(CACHE_DIR).mkdirs();

			File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(),
					CACHE_DIR);

			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
			ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
					getBaseContext())
					.defaultDisplayImageOptions(defaultOptions)
					.discCache(new UnlimitedDiscCache(cacheDir))
					.memoryCache(new WeakMemoryCache());

			ImageLoaderConfiguration config = builder.build();
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*private void checkImageStatus() {  
		if (adapter.isEmpty()) {
			imgNoMedia.setVisibility(View.VISIBLE);
		} else {
			imgNoMedia.setVisibility(View.GONE);
		}
	}
*/
	View.OnClickListener mOkClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<CustomGallery> selected = adapter.getSelected();

			String[] allPath = new String[selected.size()];
			String listofselectefilename[]=new String[selected.size()];
			
			if (allPath.length < 1) {
				
				 ImageUtil.showAlert(CustomGalleryImageActivity.this, "Please select at least one image.");
				//Toast.makeText(context, "Please Select atleast one picture.", Toast.LENGTH_LONG).show();
			}
			else{
				
				if (allPath.length > 100) {
					
					//Toast.makeText(context, "Please Select max ten pictures for upload.", Toast.LENGTH_LONG).show();
				      ImageUtil.showAlert(CustomGalleryImageActivity.this, "Please select maximum 100 images.");
				}
				else {
					
					totalsize=0;
					
				
					dataT.clear();
					for (int i = 0; i < allPath.length; i++) {
						allPath[i] = selected.get(i).sdcardPath;
						CustomGallery item = new CustomGallery();
						item.sdcardPath = allPath[i];  
						dataT.add(item);
						listofselectefilename[i]=selected.get(i).sdcardPath;
						File filenew = new File(selected.get(i).sdcardPath);
						totalsize += Integer.parseInt(String.valueOf(filenew.length()/1024));
					}
					
					//Toast.makeText(context, "File size is: "+totalsize/1024, Toast.LENGTH_LONG).show();
					if ((totalsize/1024) > 2000) {
						
						ImageUtil.showAlert(CustomGalleryImageActivity.this, "Your selected file(s) exceed the max upload limit. Please select fewer files or a smaller file.");
						//ImageUtil.showAlert(CustomGalleryImageActivity.this, "Total file size is "+totalsize/1024 +" Mb."+" Upload only support upto 500 Mb.");
					}
					else{
				
						 ImageUtil.galleryLog(TAG,  "selected images: "+allPath.length);
						 
						Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
						String isapproved=Prefs.getString(ImageConstant.PARTNERAPPROVED, "");
						if (isapproved.trim().equalsIgnoreCase("0")) {
							
							
					    	boolean isinternet=ImageUtil.isInternetOn(context);
					    	if (isinternet) {
					    		
					    		prodialog1 = new ProgressDialog(context);
								prodialog1.setTitle("In progress...");
								prodialog1.setMessage("Uploading Images...");
								//prodialog1.setIcon(R.drawable.upload);
								prodialog1.setCanceledOnTouchOutside(false);
								prodialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								prodialog1.setProgressDrawable(getResources().getDrawable(R.drawable.custom_progress));
								prodialog1.setCancelable(true);
								prodialog1.setButton(DialogInterface.BUTTON_NEGATIVE, "Upload In Background", new DialogInterface.OnClickListener() {
								    @Override
								    public void onClick(DialogInterface dialog, int which) {
								        dialog.dismiss();
								    }
								});
								/*prodialog1.setButton(DialogInterface.BUTTON_POSITIVE, "Upload In Background", new DialogInterface.OnClickListener() {
								    @Override
								    public void onClick(DialogInterface dialog, int which) {
								    	
								    	dialog.dismiss();
								    	Intent intent = new Intent(CustomGalleryImageActivity.this, UploadFileService.class);
							    		stopService(intent);
								    }
								});*/
								prodialog1.show();
								Bundle b=new Bundle();
								b.putStringArray("stringarray", allPath);
								b.putStringArray("selectedfilename", listofselectefilename);
					    		Intent intent = new Intent(CustomGalleryImageActivity.this, UploadFileService.class);
					    		intent.putExtras(b);
					    		intent.putExtra("receiver", new DownloadReceiver(new Handler()));
					    		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					    		startService(intent);
					    		
					    		for (int j = 0; j < listofselectefilename.length; j++) {
									
					    			String fileurl=listofselectefilename[j];
					    			Pending_Uploadurl_model model=new Pending_Uploadurl_model(fileurl, "image");
					    			db.addPendingUploaddata(model);
								}
					    		
					    		
					    		onResume();
					    		
					    	/*	UploadingTask task=new UploadingTask();
								 task.execute();*/
							}
					    	else{
					    		ImageUtil.showAlert(CustomGalleryImageActivity.this, getResources().getString(R.string.internet_error));
					    	}
							
						}
						else{
							ImageUtil.showAlert(CustomGalleryImageActivity.this, "You can start making content. However, your account needs to be approved before uploads are allowed.");
						}
						
					}
					
				}
				
			}
			
			
		}
	};
	
	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			adapter.changeSelection(v, position);

		}
	};
	

	private ArrayList<CustomGallery> getGalleryPhotos() {
		ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();
		
		 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
		 String bucketid=Prefs.getString(ImageConstant.BUCKETID, "");
		 
		 try {
			 final String[] projection = { MediaStore.Images.Media.DATA };
		    final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
		    final String[] selectionArgs = { bucketid };
		   
		    final String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN+ " ASC, " + ImageColumns._ID+ " ASC";
		    final Cursor cursor = context.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, 
		            projection, 
		            selection, 
		            selectionArgs, 
		            orderBy);
		  //  ArrayList<String> result = new ArrayList<String>(cursor.getCount());
		    
			if (cursor != null && cursor.getCount() > 0) {
				
				 if (cursor.moveToFirst()) {
					 
				        final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				        do {
				        	CustomGallery item = new CustomGallery();
				            final String data = cursor.getString(dataColumn);
				            item.sdcardPath = data;

							galleryList.add(item);
				            
				           // result.add(data);
				        } while (cursor.moveToNext());
				    }
			}
			else{
				
				Toast.makeText(context, "Doesn't exist any files in this diretory.", Toast.LENGTH_LONG).show();
			}
		    
		   
		    cursor.close();
		 
		 } catch (Exception e) {
				e.printStackTrace();
			}

	/*	try {
			final String[] columns = { MediaStore.Images.ImageColumns.DATA,
					MediaStore.Images.ImageColumns._ID, 
					MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
		            MediaStore.Images.ImageColumns.DATE_TAKEN};
		//	final String orderBy = MediaStore.Images.Media._ID;
			final String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN;
		    Uri imagesuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		   // Uri imagesuri1 = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
			Cursor merCur = getContentResolver().query(imagesuri, columns, null, null, orderBy);
			//Cursor imagecursor1 = getContentResolver().query(imagesuri1, columns, null, null, orderBy);
			//Cursor merCur = new MergeCursor(new Cursor[]{imagecursor,imagecursor1});

			//Toast.makeText(context, "Length of cursor is: "+merCur.getCount(), Toast.LENGTH_LONG).show();
			
			if (merCur != null && merCur.getCount() > 0) {

				while (merCur.moveToNext()) {
					CustomGallery item = new CustomGallery();

					int dataColumnIndex = merCur
							.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
					
					 String path=merCur.getString(dataColumnIndex);
					
					item.sdcardPath = merCur.getString(dataColumnIndex);

					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		// show newest photo at beginning of the list
		Collections.reverse(galleryList);
		return galleryList;
	}
	
/*	 private void customalertdialog(){
		 
		   LayoutInflater li = LayoutInflater.from(context);
		   View promptsView = li.inflate(R.layout.custom_seekbar, null);
		   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		   alertDialogBuilder.setView(promptsView);
		   SeekBar seekbar=(SeekBar)promptsView.findViewById(R.id.SeekBar_compress);
		   seekbarprogress=100;
			seekbar.setProgress(seekbarprogress);
			seekbar.setOnSeekBarChangeListener(this);
			textseekbar = (TextView) promptsView.findViewById(R.id.textView_seedbarValue);
			textseekbar.setText(100+" %");	
		   
		   alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				
			    	//dialog.dismiss();
			    	
			    	//here provide option to select category
			    	
			    	boolean isinternet=ImageUtil.isInternetOn(context);
			    	if (isinternet) {
			    		
			    		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    		    new UploadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    		} else {
			    		    new UploadingTask().execute();
			    		}
			    		UploadingTask task=new UploadingTask();
						 task.execute();
					}
			    	else{
			    		ImageUtil.showAlert(CustomGalleryImageActivity.this, "Internet Connection Error");
			    	}
			    	
			    	 
			    	
			    	list=db.GetCategoryData();
			    	CategoryAdapter adapter=new CategoryAdapter(CustomGalleryImageActivity.this, list);
	                ShowListAlertCategory(adapter);
					//ad
					UploadingTask task=new UploadingTask();
					task.execute();
			    }
			  });
		   
		   AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		   
	 }
		   */

	@Override
	public void onClick(View v) {
	
		if (v==btn_takepic) {
			//GalleryList.clear();
			Log.v(TAG, "image_camera called");
			/*String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
		    Uri uriSavedImage=null;
			
		            File dir = new File(fullPath);
		            if (!dir.exists()) {
		                dir.mkdirs();
		            }
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		            String currentDateandTime = sdf.format(new Date()).replace(" ","");
		            OutputStream fOut = null;
		            CurrentFile = new File(fullPath, currentDateandTime+".jpg");
		            try {
		            	CurrentFile.createNewFile();
						fOut = new FileOutputStream(CurrentFile);
						fOut.flush();
						fOut.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		            uriSavedImage = Uri.fromFile(CurrentFile);
		            String imagePath= uriSavedImage.getPath();
		            Log.v(TAG, "Image path is: " + imagePath);*/
		            // 100 means no compression, the lower you go, the stronger the compression
		           // bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		      
			        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
					//Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					//intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
					startActivityForResult(intent, CAMERA_RESULT);
		}
		
		else if (v==btn_back) {
			finish();
		}
		
	}
	
		@Override
		protected void onActivityResult(int requestCode, int resultCode,Intent resultData) {
			super.onActivityResult(requestCode, resultCode, resultData);
			Log.v(TAG + ".onActivityResult", "onActivityResult");
			
			  if (resultCode == RESULT_OK) {
		            // Image captured and saved to fileUri specified in the Intent
		           // Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
		        	//Toast.makeText(this, "Saved successfully.", Toast.LENGTH_LONG).show();
		        } else if (resultCode == RESULT_CANCELED) {
		        	Log.v(TAG, "cancelled the image capture");
		        } else {    
		        	Log.v(TAG, "Image capture failed");
		        }
			
}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
			seekbarprogress=progress;
			textseekbar.setText(seekbarprogress+" %");	
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
		
		 private class UploadingTask extends AsyncTask<String, Integer, String> {
				
			   @Override
			protected void onPreExecute() {
			

					prodialog1 = new ProgressDialog(context);
					prodialog1.setTitle("In progress...");
					prodialog1.setMessage("Uploading Images...");
					//prodialog1.setIcon(R.drawable.upload);
					prodialog1.setCanceledOnTouchOutside(false);
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
					
					try {
						
						   String url=ImageConstant.BASEURL+"image_fileupload.php";
						   Log.v(TAG, "url is: "+url);
						   HttpClient client = new DefaultHttpClient();
						    HttpPost post = new HttpPost(url);
						    MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
						    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
						   // Log.v(TAG, "SeekBar values is: "+seekbarprogress);
						    
						    for (int i = 1; i <= dataT.size(); i++) {
						    	CustomGallery gallery=dataT.get(i-1);
						    	String filename=gallery.sdcardPath;
						    	Log.v(TAG, "file name: "+filename);
						    	
						    	File file = new File(filename);
							    FileBody fb = new FileBody(file);
							    builder.addPart("file"+i, fb);
						    	
				
						    /*	if (seekbarprogress >= 80) {
									
							    	File file = new File(filename);
								    FileBody fb = new FileBody(file);
								    builder.addPart("file"+i, fb);  
								}
						    	else{
						    		
						    		Bitmap bmp=getwidthheight(filename, seekbarprogress);
						    		if (bmp !=null) {
										
						    			//Bitmap bmp=decodeSampledBitmapFromPath(filename, 750, 500);
								    	//Log.v(TAG, "Bitmap width is: "+bmp.getWidth());
								        //Log.v(TAG, "Bitmap height is: "+bmp.getHeight());
								    	//Bitmap bmp = BitmapFactory.decodeFile(filename);
								    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
								    	bmp.compress(CompressFormat.JPEG, seekbarprogress, bos);
								    	ContentBody foto = new ByteArrayBody(bos.toByteArray(), filename);
									    builder.addPart("file"+i, foto); 
									    
									    bmp.recycle();
									    bos.flush();
							    	}
						    		else{
						    			Log.v(TAG, "Getting bitmap is null");
						    		  }
									}*/
							    
							}
						    
							
						
						    
						    builder.addTextBody("member_id", memberid);
						    builder.addTextBody("count", String.valueOf(dataT.size()));
						    
						    final HttpEntity yourEntity = builder.build();
					
						    CustomMultiPartEntity entity=new CustomMultiPartEntity(yourEntity, new ProgressListener() {
								
								@Override
								public void transferred(long num) {
								
									publishProgress((int) ((num / (float) totalsizeimage) * 100));
									//Log.v(TAG, "publish progress :"+totalsizeimage);
									 ImageUtil.galleryLog(TAG, "publish progress :"+totalsizeimage);
								}

							
							});
						    totalsizeimage = entity.getContentLength();
						   // Log.v(TAG, "total size is: "+totalsize);
						    ImageUtil.galleryLog(TAG, "total size is: "+totalsize);
						    post.setEntity(entity);
						    HttpResponse response = client.execute(post);        
						    response1= getContent(response);
						
						// response=postFile(dataT, memberid);
						 
						Log.v(TAG, "response is: "+ response1);
						} catch (Exception e) {
							e.printStackTrace();
						}
				 return response1;
			}
				
				@Override
				protected void onProgressUpdate(Integer... progress) {
					prodialog1.setProgress((progress[0]));
					//Log.v(TAG, "onprogressupdate :"+progress[0]);
					 ImageUtil.galleryLog(TAG,"onprogressupdate :"+progress[0]);
				}
				
				
				@SuppressWarnings("deprecation")
				@Override
				protected void onPostExecute(String resultString) {
					
					prodialog1.dismiss();
					Log.v(TAG, "onPostExecute called");
					//Log.v(TAG, "Response is: "+resultString);
					ImageUtil.galleryLog(TAG,"Response is: "+resultString);
					if (resultString != null && !resultString.isEmpty()) {
						
						try {
							JSONObject jsonobj=new JSONObject(resultString);
							String resp=jsonobj.getString("Success");
							
							if (resp.equalsIgnoreCase("Successfully Uploaded the Image File")) {
								Toast.makeText(getApplicationContext(), "Upload Complete!", Toast.LENGTH_LONG).show();
								for (int i = 0; i < dataT.size(); i++) {
									
									CustomGallery gallery=dataT.get(i);
									String url=gallery.sdcardPath;
									ImageModel model=new ImageModel(url);
									db.addImage(model);
									
								}
								
								onResume();
								
								
								 NotificationManager notificationManager = (NotificationManager)getApplicationContext()
								            .getSystemService(Context.NOTIFICATION_SERVICE);
								    Notification notification = new Notification(android.R.drawable.
										      stat_notify_more, "Upload Complete!", System.currentTimeMillis());

								    Intent notificationIntent = new Intent(getApplicationContext(), CustomGalleryImageActivity.class);

								    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

								    PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

								    notification.setLatestEventInfo(getApplicationContext(), "Gallery Upload", "Upload Complete!", intent);
								    notification.flags |= Notification.FLAG_AUTO_CANCEL;
								    notificationManager.notify(0, notification);
								
							}
							else{
								Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
							}
							
							//ImageUtil.showAlert(GridShowImageActivity.this, resp);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
				    	catch (Exception e) {
						e.printStackTrace();
					}
						
					}
					else{
						Toast.makeText(getApplicationContext(), "Unable to connect to server, Please try again.", Toast.LENGTH_LONG).show();
					}

				}
			}
		 public int getseekbarValue(int seekbarvalue){
				
				if (seekbarvalue < 20 && seekbarvalue >= 0) {
					Log.v(TAG, "return 1");
					return 1;
				}
				else if (seekbarvalue < 40 && seekbarvalue >= 20) {
					Log.v(TAG, "return 2");
					return 2;
				}
				
				else if (seekbarvalue < 60 && seekbarvalue >= 40) {
					Log.v(TAG, "return 3");
				     return 3;	
				}
				else if (seekbarvalue < 80 && seekbarvalue >= 60) {
					Log.v(TAG, "return 4");
					return 4;
				}
				
				else if (seekbarvalue <= 100 && seekbarvalue >= 80) {
					Log.v(TAG, "return 5");
					return 5;
				}
				else{
					return 5;
				}
			}   
			
			 
			public Bitmap getwidthheight(String filename, int seekbarvalue){
				Bitmap bmp=null;
				int getvalue=getseekbarValue(seekbarvalue);
				switch (getvalue) {
				case 1:
					Log.v(TAG, "case 1");
				    bmp=decodeSampledBitmapFromPath(filename, 350, 300);
				  
					break;

				case 2:
					Log.v(TAG, "case 2");
				    bmp=decodeSampledBitmapFromPath(filename, 400, 350);
				  
					break;
					
				case 3:
					Log.v(TAG, "case 3");
				    bmp=decodeSampledBitmapFromPath(filename, 450, 400);
				 
					break;
					
				case 4:
					Log.v(TAG, "case 4");
				    bmp=decodeSampledBitmapFromPath(filename, 550, 450); 
				 
					break;	
					
			   case 5:
					Log.v(TAG, "case 5");
				    bmp=decodeSampledBitmapFromPath(filename, 550, 450);
				  
					break;	
					
				default:
					Log.v(TAG, "case default");
					bmp=decodeSampledBitmapFromPath(filename, 550, 450);
					break;
				}
				  return bmp;  
			}
			
			public Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
				 
		        final BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inJustDecodeBounds = true;
		        BitmapFactory.decodeFile(path, options);
		    
		        Log.v(TAG, "before compression width: "+options.outWidth);
		        Log.v(TAG, "before compression heigth: "+options.outHeight);
		        
		        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		 
		        // Decode bitmap with inSampleSize set
		        options.inJustDecodeBounds = false;
		        Bitmap bmp = BitmapFactory.decodeFile(path, options);
		        return bmp;
		        }
		 
		    public static int calculateInSampleSize(BitmapFactory.Options options,
		            int reqWidth, int reqHeight) {
		 
		        final int height = options.outHeight;
		        final int width = options.outWidth;
		        int inSampleSize = 1;
		 
		        if (height > reqHeight || width > reqWidth) {
		            if (width > height) {
		                inSampleSize = Math.round((float) height / (float) reqHeight);
		            } else {
		                inSampleSize = Math.round((float) width / (float) reqWidth);
		             }
		         }
		         return inSampleSize;
		        }
		    
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
			
		    
		  
		 
		 public void ShowListAlertCategory(final CategoryAdapter adapter){
				
			    AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
			    
			    myDialog.setTitle("Select Category List");
		        final ListView listview=new ListView(this);
		        listview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		      
		        LinearLayout layout = new LinearLayout(this);
		        layout.setOrientation(LinearLayout.VERTICAL);		
		        layout.addView(listview);
		        myDialog.setView(layout);
		        listview.setAdapter(adapter);
		      /*  listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						
						// add the images inside 
						// CategoryModel model=list.get(arg2);
						 //get the selected category
						
						 
							
				    }
		        
		        });*/
		       
		        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		 
		                        @Override
		                        public void onClick(DialogInterface dialog, int which) {
		                            dialog.dismiss();
		                        }
		                    });
		        
		        myDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		       	 
	             @Override
	             public void onClick(DialogInterface dialog, int which) {
	                 dialog.dismiss();
	                 
	                 
					 if(adapter!=null){
							
						   checked_category=adapter.getChecked();
						  // Toast.makeText(getApplicationContext(), "Checked size is: "+checked_category.size(), Toast.LENGTH_LONG).show();			   
					   }
					 
	                 UploadingTask task=new UploadingTask();
					 task.execute();
	                 
	             }
	         });
		 
		   dialog= myDialog.show();
		         
		 }
		 
		  private class PartnerApprovedTask extends AsyncTask<String, Void, String> {
				
		    	String response1 = "";
				
				@Override
				protected String doInBackground(String... urls) {
				
					Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
					String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
					
					//String url=GalleryConstant.BASEURL;
					
					String url=ImageConstant.BASEURL+"getpartnerflagdetails";
						
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
					
				
					//prodialog_partapprov.dismiss();
					Log.v(TAG, "onPostExecute called");
					Log.v(TAG, "Response is: "+resultString);
					if (resultString != null && !resultString.isEmpty()) {
						
						try{
							
							JSONArray jsonarray=new JSONArray(resultString);
							for (int i = 0; i < jsonarray.length(); i++) {
								
								JSONObject jsonobj=jsonarray.getJSONObject(i);
								String memberid=jsonobj.getString("MemberID");
								String status=jsonobj.getString("Status");
								
								 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
			 					 SharedPreferences.Editor editor = Prefs.edit();
			 					 editor.putString(ImageConstant.PARTNERAPPROVED, status);
			 					 editor.commit();	
								
							}
						
							
						} catch (JSONException e) {
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
					
	                  
					}
				}	
				
		  }
		  
		  public void getbucketname(){
			  // which image properties are we querying
			    String[] PROJECTION_BUCKET = {
			            ImageColumns.BUCKET_ID,
			            ImageColumns.BUCKET_DISPLAY_NAME,
			            ImageColumns.DATE_TAKEN,
			            ImageColumns.DATA,
			            ImageColumns.SIZE};
			    // We want to order the albums by reverse chronological order. We abuse the
			    // "WHERE" parameter to insert a "GROUP BY" clause into the SQL statement.
			    // The template for "WHERE" parameter is like:
			    //    SELECT ... FROM ... WHERE (%s)
			    // and we make it look like:
			    //    SELECT ... FROM ... WHERE (1) GROUP BY 1,(2)
			    // The "(1)" means true. The "1,(2)" means the first two columns specified
			    // after SELECT. Note that because there is a ")" in the template, we use
			    // "(2" to match it.
			    String BUCKET_GROUP_BY ="1) GROUP BY 1,(2";
			    String BUCKET_ORDER_BY = "MAX(datetaken) DESC";

			    // Get the base URI for the People table in the Contacts content provider.
			    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

			    Cursor cur = getContentResolver().query(
			            images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);

			    Log.i("ListingImages"," query count=" + cur.getCount());
			    Toast.makeText(context, "query count=" + cur.getCount(), Toast.LENGTH_LONG).show();

			    if (cur.moveToFirst()) {
			        String bucket;
			        String date;
			        String data;
			        int datasize;
			        int bucketColumn = cur.getColumnIndex(
			                MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

			        int dateColumn = cur.getColumnIndex(
			                MediaStore.Images.Media.DATE_TAKEN);
			        int dataColumn = cur.getColumnIndex(
			                MediaStore.Images.Media.DATA);
			        int numberofdata = cur.getColumnIndex(
			                MediaStore.Images.Media.SIZE);

			        do {
			            // Get the field values
			            bucket = cur.getString(bucketColumn);
			            date = cur.getString(dateColumn);
			            data = cur.getString(dataColumn);
			            datasize = cur.getInt(numberofdata);

			            // Do something with the values.
			            Log.i("ListingImages", " bucket=" + bucket 
			                    + "  date_taken=" + date
			                    + "  _data=" + data+ "  _datasize=" + datasize);
			        } while (cur.moveToNext());
			    }
		  }

		  public class DownloadReceiver extends ResultReceiver{

				public DownloadReceiver(Handler handler) {
					super(handler);
					// TODO Auto-generated constructor stub
				}

					@Override
				    protected void onReceiveResult(int resultCode, Bundle resultData) {
				        super.onReceiveResult(resultCode, resultData);
				        if (resultCode == UploadFileService.UPDATE_PROGRESS) {
				            int progress = resultData.getInt("progress");
				            prodialog1.setProgress(progress);
				            if (progress == 200) {
				            	prodialog1.dismiss();
				            	
				            	//finish();
				            }
				        }
				    }
			}
		  
		 
}
