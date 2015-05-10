package com.kns.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kns.model.FileDirectories;
import com.kns.util.ImageUtil;
import com.loopj.android.image.SmartImage;
import com.squareup.picasso.Picasso;
import com.sunil.selectmutiple.R;

public class FileDir_Adapter  extends BaseAdapter{

	private static final String TAG="CategoryAdapter";
	private LayoutInflater mInflater=null;
	private List<FileDirectories> filedir_list=null;
	private Context context=null;
	private  Bitmap bitmap=null;
	//ImageLoader imageLoader;
	
	public FileDir_Adapter(Activity context, List<FileDirectories> list){
		
		mInflater = context.getLayoutInflater();
		this.filedir_list=list;
		this.context=context;
		//imageLoader = MyVolleySingleton.getInstance(context).getImageLoader();
	}
	
	@Override
	public int getCount() {
		return filedir_list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null ) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.filedir_row, null);
			
			holder.image = (ImageView) convertView.findViewById(R.id.imgQueue_dir);
			holder.txt_name= (TextView)convertView.findViewById(R.id.textView_dirname);
			holder.textView_size= (TextView)convertView.findViewById(R.id.textView_size);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		FileDirectories model=filedir_list.get(position);
		String bucketid=model.getBucket_id();
		String bucketname=model.getBucket_name();
		String bucketprofile=model.getBucket_profile();
		String bucketsize=model.getBucket_size();
		//String thumburl=partnermodel.getVideothumburl();
		int videoid=model.getFileid();
		
		boolean isfile=model.isIsfile();
		if (isfile) {
			
		}
		else{
			String fileurl="file://" +bucketprofile;
			//Log.v(TAG, "Image Url is: "+fileurl);
			ImageUtil.galleryLog(TAG, "Image Url is: "+fileurl);
			if (fileurl!=null && !fileurl.isEmpty()) {
				
				//Log.v(TAG, "Image Url is: "+bucketprofile);
				
				Picasso.with(context)
	            .load(fileurl)
	            .resize(150, 150)
	            .centerCrop()
	            .placeholder(R.drawable.ic_dwnloadthumb)
	            .error(R.drawable.no_image)
	            .into(holder.image);
				
			}
			else{
				holder.image.setImageResource(R.drawable.no_image);
			}
		}
		
		holder.txt_name.setText(bucketname);
		holder.textView_size.setText("("+bucketsize+")");
		
	//	holder.txt_noofimage.setText(noofimages+"/100");
		
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView image;
		TextView txt_name;
		TextView textView_size;
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

}
