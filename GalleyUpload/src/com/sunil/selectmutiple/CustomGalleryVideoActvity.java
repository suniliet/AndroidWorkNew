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

import android.annotation.SuppressLint;
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
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.kns.adapter.CategoryAdapter;
import com.kns.adapter.GalleryVideoAdapter;
import com.kns.db.DBHelper;
import com.kns.model.CategoryModel;
import com.kns.model.Pending_Uploadurl_model;
import com.kns.model.VideoModel;
import com.kns.service.UploadFileService;
import com.kns.service.UploadVideoService;
import com.kns.util.CustomMultiPartEntity;
import com.kns.util.CustomMultiPartEntity.ProgressListener;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;
import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sunil.selectmutiple.CustomGalleryImageActivity.DownloadReceiver;

public class CustomGalleryVideoActvity extends Activity implements OnClickListener{

	public static final String ACTION_MULTIPLE_PICK = "video.ACTION_MULTIPLE_PICK";
	private static final String TAG="CustomGalleryVideoActvity";
	private GridView gridGallery;
	private Handler handler;
	private GalleryVideoAdapter adapter;
	private ImageButton btnGalleryOk;
	private ImageButton btn_back;
    private Context context=null;
	String action;
	private ImageLoader imageLoader;
	private ImageButton btn_upload=null;
	public final static String APP_PATH_SD_CARD = "/VideoUpload";
	final static int Video_RESULT = 0;
	private int totalsize=0;
	
	private ProgressDialog prodialog=null;
	private ProgressDialog prodialog1=null;
	ArrayList<CustomVideoGallery> dataT = new ArrayList<CustomVideoGallery>();
	//ArrayList<CustomVideoGallery> dataID = new ArrayList<CustomVideoGallery>();
	

	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	long totalsizevideo;
	private DBHelper db=null;
	List<CategoryModel> list;
	AlertDialog dialog;
	List<CategoryModel> list_category=new ArrayList<CategoryModel>();
	ArrayList<CategoryModel> checked_category=null;
	
	private SharedPreferences preferenceManager;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custome_videogallery);
		context=this;
		db=new DBHelper(getApplicationContext());
		
		 preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
		
		/*action = getIntent().getAction();
		if (action == null) {
			finish();
		}*/
		
		initImageLoader();
		
		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		btn_upload = (ImageButton)findViewById(R.id.button_takevideo);
		btn_back=(ImageButton)findViewById(R.id.imageButton_back);
		btn_back.setOnClickListener(this);
		btn_upload.setOnClickListener(this);
	
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryVideoAdapter(CustomGalleryVideoActvity.this, imageLoader);
	
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
				true, true);
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
			/*PartnerApprovedTask task1=new PartnerApprovedTask();
			task1.execute();*/
		}
		
	
	/*	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
		        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,  Uri.parse("file://" + Environment.getExternalStorageDirectory()));
		        this.sendBroadcast(mediaScanIntent);
		}
		else
		{
		       sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		}*/
		
		
		//context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()))); 

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	
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

		}
	}
	
	View.OnClickListener mOkClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<CustomVideoGallery> selected = adapter.getSelected();

			String[] allPath = new String[selected.size()];
			String[] allid = new String[selected.size()];
			String listofselectefilename[]=new String[selected.size()];
			
			if (allPath.length < 1) {
				
				//Toast.makeText(context, "Please Select atleast one video.", Toast.LENGTH_LONG).show();
				ImageUtil.showAlert(CustomGalleryVideoActvity.this, "Please select at least one video.");
			}
			else{
				
				if (allPath.length > 10) {
					
					//Toast.makeText(context, "Please Select max 2 video for upload.", Toast.LENGTH_LONG).show();
					
					ImageUtil.showAlert(CustomGalleryVideoActvity.this, "Please select maximum ten videos.");
				}
				else {
					
					dataT.clear();
					totalsize=0;
					for (int i = 0; i < allPath.length; i++) {
						allPath[i] = selected.get(i).sdcardPathvideo;
						
						CustomVideoGallery item = new CustomVideoGallery();
						item.sdcardPathvideo = allPath[i] ;
						dataT.add(item);
						
						allid[i] = String.valueOf(selected.get(i).video_id);
						listofselectefilename[i]=selected.get(i).sdcardPathvideo;
						File filenew = new File(selected.get(i).sdcardPathvideo);
						totalsize += Integer.parseInt(String.valueOf(filenew.length()/1024));
					}
					
					//Log.v(TAG, "File size is: "+totalsize/1024+"Mb");
					ImageUtil.galleryLog(TAG,"File size is: "+totalsize/1024+"Mb");
					//Toast.makeText(context, "File size is: "+totalsize/1024+"Mb", Toast.LENGTH_LONG).show();
					if ((totalsize/1024) > 2000) {
						
						ImageUtil.showAlert(CustomGalleryVideoActvity.this, "Your selected file(s) exceed the max upload limit. Please select fewer files or a smaller file.");
						//ImageUtil.showAlert(CustomGalleryVideoActvity.this, "Total file size is "+totalsize/1024 +" Mb."+" Upload only support upto 500 Mb.");
					}
					else{
					
						
						Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
						String isapproved=Prefs.getString(ImageConstant.PARTNERAPPROVED, "");
						if (isapproved.trim().equalsIgnoreCase("0")) {
							
							boolean isinternet=ImageUtil.isInternetOn(context);
							if (isinternet) {
								
								
								prodialog1 = new ProgressDialog(context);
								prodialog1.setTitle("In progress...");
								prodialog1.setMessage("Uploading Videos...");
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
								/*prodialog1.setButton(DialogInterface.BUTTON_POSITIVE, "Stop Upload", new DialogInterface.OnClickListener() {
								    @Override
								    public void onClick(DialogInterface dialog, int which) {
								    	
								    	dialog.dismiss();
								    	Intent intent = new Intent(CustomGalleryVideoActvity.this, UploadVideoService.class);
							    		stopService(intent);
								    }
								});*/
								prodialog1.show();
								Bundle b=new Bundle();
								b.putStringArray("stringarray", allPath);
								b.putStringArray("selectedfilename", listofselectefilename);
					    		Intent intent = new Intent(CustomGalleryVideoActvity.this, UploadVideoService.class);
					    		intent.putExtras(b);
					    		intent.putExtra("receiver", new DownloadReceiver(new Handler()));
					    		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					    		startService(intent);
					    		
					    		if (totalsize/1024 > 500) {
									
					    			ImageUtil.galleryLog(TAG, "yes size exceeded than 500");
								}
					    		else{
					    			
					    			for (int j = 0; j < listofselectefilename.length; j++) {
										
						    			String fileurl=listofselectefilename[j];
						    			Pending_Uploadurl_model model=new Pending_Uploadurl_model(fileurl, "video");
						    			db.addPendingUploaddata(model);
						    			ImageUtil.galleryLog(TAG, "added in db");
									}
						    		
					    		}
					    		
					    		
					    		
					    		onResume();
								
								/*if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
					    		    new UploadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					    		} else {
					    		    new UploadingTask().execute();
					    		}*/
								 
								/*UploadingTask task=new UploadingTask();
								task.execute();*/
							}
							else{
								ImageUtil.showAlert(CustomGalleryVideoActvity.this, getResources().getString(R.string.internet_error));
							}
							
						}
						else{
							ImageUtil.showAlert(CustomGalleryVideoActvity.this, "You can start making content. However, your account needs to be approved before uploads are allowed.");
						}
						
						
						
				
				    	/*list=db.GetCategoryData();
				    	CategoryAdapter adapter=new CategoryAdapter(CustomGalleryVideoActvity.this, list);
		                ShowListAlertCategory(adapter);*/
						
		             /*
						boolean isinternet=ImageUtil.isInternetOn(context);
						if (isinternet) {
							 
							UploadingTask task=new UploadingTask();
							task.execute();
						}
						else{
							ImageUtil.showAlert(CustomGalleryVideoActvity.this, "Internet Connection Error.");
						}*/
						
						//Toast.makeText(context, "You selected videos. "+allPath.length, Toast.LENGTH_LONG).show();
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
	

	private ArrayList<CustomVideoGallery> getGalleryPhotos() {
		ArrayList<CustomVideoGallery> galleryList = new ArrayList<CustomVideoGallery>();


		 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
		 String bucketid=Prefs.getString(ImageConstant.BUCKETID, "");
		
		 
		 try {
			 final String[] projection = { MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns._ID };
		    final String selection = MediaStore.Video.VideoColumns.BUCKET_ID + " = ?";
		    final String[] selectionArgs = { bucketid };
			final String orderBy = MediaStore.Video.VideoColumns.DATE_TAKEN +" ASC, " + VideoColumns._ID+ " ASC";
			
			Uri videosuri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		    final Cursor cursor = context.getContentResolver().query(videosuri, 
		            projection, 
		            selection, 
		            selectionArgs, 
		            orderBy);
		  
			if (cursor != null && cursor.getCount() > 0) {
				
				 if (cursor.moveToFirst()) {
					 
				       // final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				        do {
				        	CustomVideoGallery item = new CustomVideoGallery();
				        	int video_id=cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
							int dataColumnIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
							int dataColumnIndexthumb = cursor.getColumnIndex( MediaStore.Video.Thumbnails.DATA );
						
				            final String data = cursor.getString(dataColumnIndex);
				            item.sdcardPaththumbvideo = cursor.getString(dataColumnIndexthumb);
				            item.sdcardPathvideo = data;
				            item.video_id=video_id;
							galleryList.add(item);
				            
				        } while (cursor.moveToNext());
				    }
			}
			else{
				
				Toast.makeText(context, "Doesn't exist any files in this diretory.", Toast.LENGTH_LONG).show();
			}
		    
		   
		    cursor.close();
		    
		   }catch (Exception e) {
				e.printStackTrace();
			}
	/*	
	 try {
	 
	  final String[] columns = { MediaStore.Video.VideoColumns.DATA,
					MediaStore.Video.VideoColumns._ID
					MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
		            MediaStore.Video.Media.DATE_TAKEN};
		
			//final String orderBy = MediaStore.Video.Media._ID;
			final String orderBy = MediaStore.Video.VideoColumns.DATE_TAKEN;
			Uri videosuri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
			//Uri videosuri=	MediaStore.Video.getContentUri("external");
			Cursor imagecursor = getContentResolver().query(videosuri, columns, null, null, orderBy);
		

			if (imagecursor != null && imagecursor.getCount() > 0) {

				while (imagecursor.moveToNext()) {
					CustomVideoGallery item = new CustomVideoGallery();
					int video_id=imagecursor.getInt(imagecursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
					int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
					int dataColumnIndexthumb = imagecursor.getColumnIndex( MediaStore.Video.Thumbnails.DATA );
				
					 String path=imagecursor.getString(dataColumnIndex);
					 String paththumb=imagecursor.getString(dataColumnIndexthumb);
					 //Log.v(TAG, "video thumb path is: "+paththumb);
					// Log.v(TAG, "video  path is: "+path);
					 ImageUtil.galleryLog(TAG,"video thumb path is: "+paththumb);
					 ImageUtil.galleryLog(TAG,"video  path is: "+path);
					
					item.sdcardPathvideo = imagecursor.getString(dataColumnIndex);
					item.sdcardPaththumbvideo = imagecursor.getString(dataColumnIndexthumb);
					item.video_id=video_id;
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

	@Override
	public void onClick(View v) {
		
		if (v==btn_upload) {
			
			Log.v(TAG, "image_camera called");
			/*String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
		    Uri uriSavedvideo=null;
			
		            File dir = new File(fullPath);
		            if (!dir.exists()) {
		                dir.mkdirs();
		            }
		            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		            String currentDateandTime = sdf.format(new Date()).replace(" ","");
		            OutputStream fOut = null;
		            File file = new File(fullPath, currentDateandTime+".mp4");
		            try {
						file.createNewFile();
						fOut = new FileOutputStream(file);
						fOut.flush();
						fOut.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		            uriSavedvideo = Uri.fromFile(file);
		            String videoPath= uriSavedvideo.getPath();
		            Log.v(TAG, "Video path is: " + videoPath);*/
		            // 100 means no compression, the lower you go, the stronger the compression
		           // bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			    //    Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
					//Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
					//intent.putExtra(MediaStore.EXTRA_OUTPUT, videoPath);
				//	startActivityForResult(intent, Video_RESULT);
					
					 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
					 String checkstatus=Prefs.getString(ImageConstant.CHECKBOXSTATUS, "");
					 if (checkstatus.equalsIgnoreCase("not checked") || checkstatus.equalsIgnoreCase("")) {
						
						 attentionBox();
					}
					 else{
						 
							boolean iscamera=hasCamera();
							if (iscamera) {
							
								Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
								startActivityForResult(intent, Video_RESULT);
							}
							else{
								ImageUtil.showAlert(CustomGalleryVideoActvity.this, "Your device doesn't have camera feature.");
							}
					 }
				
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
            // Video captured and saved to fileUri specified in the Intent
           /* Toast.makeText(this, "Video saved to:\n" +
            		resultData.getData(), Toast.LENGTH_LONG).show();*/
            //Toast.makeText(this, "Video saved successfully.", Toast.LENGTH_LONG).show();

        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the video capture
        	Log.v(TAG, "cancelled he video capture");
        } else {
        	Log.v(TAG, "Video capture faile");
            // Video capture failed, advise user
        }
		
	/*	try {
		
		//String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
		if (resultData != null) {
		    Uri videoUri = resultData.getData();
		    Log.v(TAG, "Video Uri is: "+videoUri);
			image_camera.setImageBitmap(bitmapimage);
			image_camera.setScaleType(ScaleType.CENTER_CROP);
			
			
		} 

		} catch (Exception ex) {
		ex.printStackTrace();
		}*/
}

	
	  private static class ThumbnailBinder implements SimpleCursorAdapter.ViewBinder {
		    @Override
		    public boolean setViewValue(View v, Cursor c, int column) {
		      if (column == c.getColumnIndex(MediaStore.Video.Media._ID)) {
		        VideoThumbnailImage thumb = new VideoThumbnailImage( c.getInt(column),  MediaStore.Video.Thumbnails.MICRO_KIND);

		        ((SmartImageView)v).setImage(thumb,R.drawable.no_media);

		        return(true);
		      }

		      return(false);
		    }
		  }

		  private static class VideoThumbnailImage implements SmartImage {
		    private int videoId;
		    private int thumbnailKind;

		    VideoThumbnailImage(int videoId, int thumbnailKind) {
		      this.videoId=videoId;
		      this.thumbnailKind=thumbnailKind;
		    }

		    @Override
		    public Bitmap getBitmap(Context ctxt) {
		      return(MediaStore.Video.Thumbnails.getThumbnail(ctxt.getContentResolver(),
		                                                      videoId,
		                                                      thumbnailKind,
		                                                      null));
		    }
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
						//Log.v(TAG, "Ur is: "+url);
						//Log.v(TAG, "file size is: "+dataT.size());
						 ImageUtil.galleryLog(TAG,"Ur is: "+url);
						 ImageUtil.galleryLog(TAG,"file size is: "+dataT.size());
					    HttpClient client = new DefaultHttpClient();
					    HttpPost post = new HttpPost(url);
					    MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
					    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

					    for (int i = 1; i <= dataT.size(); i++) {
					    	CustomVideoGallery gallery=dataT.get(i-1);
					    	String filename=gallery.sdcardPathvideo;
					    	//Log.v(TAG, "file name: "+filename);
					    	ImageUtil.galleryLog(TAG, "file name: "+filename);
					    	
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
							
								publishProgress((int) ((num / (float) totalsizevideo) * 100));
							}
						});
					    totalsizevideo = entity.getContentLength();
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

				@SuppressWarnings("deprecation")
				@Override
				protected void onPostExecute(String resultString) {
					
					prodialog1.dismiss();
					Log.v(TAG, "onPostExecute called");
					Log.v(TAG, "Response is: "+resultString);
					if (resultString != null && !resultString.isEmpty()) {
						
						try {
							JSONObject jsonobj=new JSONObject(resultString);
							String resp=jsonobj.getString("Success");
							
							if (resp.equalsIgnoreCase("Successfully Uploaded Video File")) {
								
								Toast.makeText(getApplicationContext(), "Upload Complete!", Toast.LENGTH_LONG).show();
								
								for (int i = 0; i < dataT.size(); i++) {
									
									CustomVideoGallery gallery=dataT.get(i);
									String url=gallery.sdcardPathvideo;
									VideoModel model=new VideoModel(url);
									db.addVideo(model);
								}
								
								
								
								onResume();
								
								 NotificationManager notificationManager = (NotificationManager)getApplicationContext()
								            .getSystemService(Context.NOTIFICATION_SERVICE);
								    @SuppressWarnings("deprecation")
									Notification notification = new Notification(android.R.drawable.
										      stat_notify_more, "Uploading video successfully.", System.currentTimeMillis());

								    Intent notificationIntent = new Intent(getApplicationContext(), CustomGalleryVideoActvity.class);

								    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

								    PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
								            notificationIntent, 0);

								    notification.setLatestEventInfo(getApplicationContext(), "Gallery Upload", "Video uploaded successfully.", intent);
								    notification.flags |= Notification.FLAG_AUTO_CANCEL;
								    notificationManager.notify(0, notification);
							}
							else{
								Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_LONG).show();
							}
							
							//ImageUtil.showAlert(GridShowVideoActivity.this, resp);
						
						} catch (JSONException e) {
							e.printStackTrace();
						}
				    	catch (Exception e) {
						e.printStackTrace();
					}
						
					}
					else{
						ImageUtil.showAlert(CustomGalleryVideoActvity.this, "Problem in Internet Processing. Please try again.");
					}

				}
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
		
		  private boolean hasCamera() {
			    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			        return true;
			    } else {
			        return false;
			    }
			}
		  
		  public  void attentionBox(){
				
				AlertDialog.Builder adb=new AlertDialog.Builder(CustomGalleryVideoActvity.this);
			    LayoutInflater adbInflater = LayoutInflater.from(CustomGalleryVideoActvity.this);
			    View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
			    final CheckBox dontShowAgain = (CheckBox)eulaLayout.findViewById(R.id.skip);
			    adb.setView(eulaLayout);
			    adb.setTitle("Attention");
			    adb.setMessage("Videos should be 15 minutes or less. The max video upload size is 2000 MB.");
		    	adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
		    	      public void onClick(DialogInterface dialog, int which) {
		    	 
		    	    	  if (dontShowAgain.isChecked())  {
		    	    
		    	    		 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
							 SharedPreferences.Editor editor = Prefs.edit();
							 editor.putString(ImageConstant.CHECKBOXSTATUS, "checked");
							 editor.commit();	
							 
		    	    	  }
		    	    	  else{
		    	    		  

		     	    		 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
		 					 SharedPreferences.Editor editor = Prefs.edit();
		 					 editor.putString(ImageConstant.CHECKBOXSTATUS, "not checked");
		 					 editor.commit();	
		    	    	  }
		    	  			
		    	    		
							boolean iscamera=hasCamera();
							if (iscamera) {
							
								Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
								startActivityForResult(intent, Video_RESULT);
							}
							else{
								ImageUtil.showAlert(CustomGalleryVideoActvity.this, "Your device doesn't have camera feature.");
							}
		    	    	  
		    	    	  return;  
		    	      } });
		 
		    	  adb.show();
		    	
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
			    
			       
			        myDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			 
			                        @Override
			                        public void onClick(DialogInterface dialog, int which) {
			                            dialog.dismiss();
			                            
			                            if(adapter!=null){
											
			 							   checked_category=adapter.getChecked();
			 							  // Toast.makeText(getApplicationContext(), "Checked size is: "+checked_category.size(), Toast.LENGTH_LONG).show();			   
			 						   }
			 						 

			 							boolean isinternet=ImageUtil.isInternetOn(context);
			 							if (isinternet) {
			 								 
			 								UploadingTask task=new UploadingTask();
			 								task.execute();
			 							}
			 							else{
			 								ImageUtil.showAlert(CustomGalleryVideoActvity.this, "Internet Connection Error.");
			 							}
			 		                 
			                        }
			                    });
			        
			        myDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			       	 
		             @Override
		             public void onClick(DialogInterface dialog, int which) {
		                 dialog.dismiss();
		                 
		                 
						
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
					            }
					           
					        }
					    }
				}
			  
			  public int progressupdateNotoficatio(int updatestatus){
					
				  switch (updatestatus) {
				case 100:
					getNotification(updatestatus);
					break;
			
				default:
					break;
				}
				  
				  return updatestatus;
				  
			  }
			  
			 
			  @SuppressLint("NewApi")
			public void getNotification(int update){
				  
				  NotificationManager notificationManager = (NotificationManager)getApplicationContext()
				            .getSystemService(Context.NOTIFICATION_SERVICE);
				  Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext());
				
				  notificationBuilder.setOngoing(true)
                  .setContentTitle("")
                  .setContentText("Uploaded "+update+"%")
                  .setProgress(100, update, false);

				//Send the notification:
				Notification notification = notificationBuilder.build();
				
				notificationManager.notify(0, notification);
								  
			/*	  Notification notification = new Notification(android.R.drawable.
					      stat_notify_more, "Uploaded "+update+"%", System.currentTimeMillis());

			    Intent notificationIntent = new Intent(getApplicationContext(), ImageDiectoriesActivity.class);

			    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

			    PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

			    notification.setLatestEventInfo(getApplicationContext(), "", "Uploaded "+update+"%", intent);
			    notification.flags |= Notification.FLAG_AUTO_CANCEL;
			    notificationManager.notify(0, notification);*/
			
			  }

}
