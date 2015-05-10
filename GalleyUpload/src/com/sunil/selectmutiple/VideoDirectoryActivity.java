package com.sunil.selectmutiple;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;

import com.kns.adapter.FileDir_Adapter;
import com.kns.adapter.FileDir_Video_Adapter;
import com.kns.model.FileDirectories;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;

public class VideoDirectoryActivity extends Activity implements OnItemClickListener, OnClickListener{
	
	private static final String TAG="VideoDirectoryActivity";
	private Context context=null;
	public static final String ACTION_MULTIPLE_PICK = "video.ACTION_MULTIPLE_PICK";
	
	private GridView gridview_videodir;
	private ImageButton btn_back;
	List<FileDirectories> list_dir=new ArrayList<FileDirectories>();
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videodirectories);
		
		context=this;
		
		gridview_videodir=(GridView)findViewById(R.id.gridGallery_videodir);
		btn_back=(ImageButton)findViewById(R.id.imageButton_back);
		btn_back.setOnClickListener(this);
		
		
		gridview_videodir.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		list_dir=getbucketname();
		
		FileDir_Video_Adapter adapter=new FileDir_Video_Adapter(VideoDirectoryActivity.this, list_dir);
		gridview_videodir.setAdapter(adapter);
	}
	
	  public List<FileDirectories> getbucketname(){
		  
		  list_dir.clear();
		  
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
		    Uri images = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

		    Cursor cur = getContentResolver().query(
		            images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);

		   // Log.i("ListingImages"," query count=" + cur.getCount());
		    ImageUtil.galleryLog(TAG," query count=" + cur.getCount());
		   // Toast.makeText(context, "query count=" + cur.getCount(), Toast.LENGTH_LONG).show();

		    if (cur.moveToFirst()) {
		        String bucketname;
		        String bucketdate;
		        String data;
		        String bucketsize;
		        String bucketid;
		        
		        int bucketidcolumn = cur.getColumnIndex(
		                MediaStore.Video.Media.BUCKET_ID);

		        int bucketColumn = cur.getColumnIndex(
		                MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

		        int dateColumn = cur.getColumnIndex(
		                MediaStore.Video.Media.DATE_TAKEN);
		        int dataColumn = cur.getColumnIndex(
		                MediaStore.Video.Media.DATA);
		        int numberofdata = cur.getColumnIndex(
		                MediaStore.Video.Media.SIZE);

		        do {
		            // Get the field values
		        	bucketid = cur.getString(bucketidcolumn);
		        	bucketname = cur.getString(bucketColumn);
		        	bucketdate = cur.getString(dateColumn);
		            data = cur.getString(dataColumn);
		            bucketsize = cur.getString(numberofdata);
		            
		           int videocount= videoCountByAlbum(bucketid);
		           int videoid= getvideoid(bucketid);
		            
		            FileDirectories model=new FileDirectories(bucketid, bucketname, bucketdate, data, String.valueOf(videocount), videoid, true);
		            list_dir.add(model);
		            // Do something with the values.
		           /* Log.i("Listingvideos"," bucketid=" + bucketid + " bucket=" + bucketname 
		                    + "  date_taken=" + bucketdate
		                    + "  _data=" + data+ "  _datasize=" + bucketsize);
		            */
		      	  ImageUtil.galleryLog(TAG," bucketid=" + bucketid + " bucket=" + bucketname 
		                    + "  date_taken=" + bucketdate
		                    + "  _data=" + data+ "  _datasize=" + bucketsize);
		        } while (cur.moveToNext());
		    }
		    
		    return list_dir;
	  }
	  
	  
		private int videoCountByAlbum(String bucketid) {

			try {
				final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
				String searchParams = null;
				searchParams = "BUCKET_ID = \"" + bucketid + "\"";

				// final String[] columns = { MediaStore.Video.Media.DATA,
				// MediaStore.Video.Media._ID };
				Cursor mVideoCursor = getContentResolver().query(
						MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
						searchParams, null, orderBy + " DESC");

				if (mVideoCursor.getCount() > 0) {

					return mVideoCursor.getCount();
				}
				mVideoCursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return 0;

		}
		
		private int getvideoid(String bucketid) {

			try {
				final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
				String searchParams = null;
				searchParams = "BUCKET_ID = \"" + bucketid + "\"";

				// final String[] columns = { MediaStore.Video.Media.DATA,
				// MediaStore.Video.Media._ID };
				Cursor mVideoCursor = getContentResolver().query(
						MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
						searchParams, null, orderBy + " DESC");

				if (mVideoCursor.getCount() > 0) {

					 if (mVideoCursor.moveToFirst()) {
						 
						 //int dataColumnIndexthumb = mVideoCursor.getColumnIndex( MediaStore.Video.Thumbnails.DATA );
						 int video_id=mVideoCursor.getInt(mVideoCursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
						// String path=mVideoCursor.getString(dataColumnIndexthumb);
						 return video_id;
					 }
					
				}
				
				mVideoCursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return 0;

		}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		FileDirectories model=list_dir.get(arg2);
		String bucketid=model.getBucket_id();
		
		//List<String> listimages=getBucketImages(context, bucketid);
		//Toast.makeText(context, "No of images is: "+listimages.size(), Toast.LENGTH_LONG).show();
		
		 Prefs = getSharedPreferences(prefname, Context.MODE_PRIVATE);
		 SharedPreferences.Editor editor = Prefs.edit();
		 editor.putString(ImageConstant.BUCKETID, bucketid);
		 editor.commit();	
		
		 Intent i=new Intent(VideoDirectoryActivity.this, CustomGalleryVideoActvity.class);
		 startActivity(i);
		  
		 
		/*Intent i = new Intent(ACTION_MULTIPLE_PICK);
		startActivityForResult(i, 200);*/
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		finish();
	}

}
