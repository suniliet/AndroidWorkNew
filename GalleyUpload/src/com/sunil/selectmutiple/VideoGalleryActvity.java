package com.sunil.selectmutiple;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;

import com.kns.adapter.GalleryVideoAdapter;
import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class VideoGalleryActvity extends Activity implements OnClickListener{

	public static final String ACTION_MULTIPLE_PICK = "video.ACTION_MULTIPLE_PICK";
	private static final String TAG="VideoGalleryActvity";
	private GridView gridGallery;
	private Handler handler;
	private GalleryVideoAdapter adapter;
	private Button btnGalleryOk;
    private Context context=null;
	String action;
	private ImageLoader imageLoader;
	private Button btn_upload=null;
	public final static String APP_PATH_SD_CARD = "/VideoUpload";
	final static int Video_RESULT = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custome_videogallery);
		
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		btn_upload = (Button)findViewById(R.id.button_takevideo);
		btn_upload.setOnClickListener(this);
	
		
		
		String[] from=
		        { MediaStore.Video.Media.TITLE, MediaStore.Video.Media._ID };
		    int[] to= { android.R.id.text1, R.id.thumbnail };
		    SimpleCursorAdapter adapter= new SimpleCursorAdapter(this, R.layout.row, null, from, to, 0);

		    adapter.setViewBinder(new ThumbnailBinder());
		    gridGallery.setAdapter(adapter);
	}
	
	
	 private static class ThumbnailBinder implements
     SimpleCursorAdapter.ViewBinder {
   @Override
   public boolean setViewValue(View v, Cursor c, int column) {
     if (column == c.getColumnIndex(MediaStore.Video.Media._ID)) {
       VideoThumbnailImage thumb=
           new VideoThumbnailImage(
                                   c.getInt(column),
                                   MediaStore.Video.Thumbnails.MICRO_KIND);

       ((SmartImageView)v).setImage(thumb,R.drawable.ic_media_video_poster);

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

@Override
public void onClick(View v) {
	
}

}
