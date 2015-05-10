package com.kns.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kns.model.PartnerVideo_Model;
import com.kns.util.ImageUtil;
import com.squareup.picasso.Picasso;
import com.sunil.selectmutiple.R;



public class Partner_Video_Adapter extends BaseAdapter{

	private static final String TAG="Partner_Video_Adapter";
	private LayoutInflater mInflater=null;
	private List<PartnerVideo_Model> videolist=null;
	private Context context=null;
	private  Bitmap bitmap=null;
	private  Button btn_update;
	//private ImageLoader imageLoader;
	
	public Partner_Video_Adapter(Activity context, List<PartnerVideo_Model> list){
		
		mInflater = context.getLayoutInflater();
		this.videolist=list;
		this.context=context;
		//imageLoader = MyVolleySingleton.getInstance(context).getImageLoader();
		
		//doption=new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.no_video).showImageOnFail(R.drawable.ic_error).showStubImage(R.drawable.ic_stub).cacheInMemory(true).cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(20)).build();
		
	}
	
	@Override
	public int getCount() {
		return videolist.size();
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
			convertView = mInflater.inflate(R.layout.video_item_row, null);
			
			holder.image = (ImageView) convertView.findViewById(R.id.image_view);
			//holder.btn_update = (Button) convertView.findViewById(R.id.button_update);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		PartnerVideo_Model imagemodel=videolist.get(position);
		//String imageurl=imagemodel.getVideothumburl0();
		String videourl=imagemodel.getVideorealurl();
		String updatedvideothumb=imagemodel.getUpdatedvideothumb();
		//holder.image.setDefaultImageResId(R.drawable.no_video);
		
		//Log.v(TAG, "Updated thumb is: "+ updatedvideothumb);
		ImageUtil.galleryLog(TAG, "Updated thumb is: "+ updatedvideothumb);
		
		if (updatedvideothumb!=null) {
			
			Picasso.with(context)
            .load(updatedvideothumb)
            //.resize(150, 150)
            //.centerCrop()
            .placeholder(R.drawable.ic_dwnloadthumb)
            .error(R.drawable.ic_nothumb)
            .into(holder.image);
			
		/*	holder.image.setErrorImageResId(R.drawable.no_video);
			holder.image.setAdjustViewBounds(true);
			holder.image.setImageUrl(imageurl, imageLoader);*/
			
			/*imageLoader.displayImage(imageurl,
					holder.image, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.image.setImageResource(R.drawable.no_video);
							super.onLoadingStarted(imageUri, view);
						}
					});*/
		}
		else{
			holder.image.setImageResource(R.drawable.no_image);
		}
		/*holder.btn_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Toast.makeText(context, "Onclicked", Toast.LENGTH_SHORT).show();
			}
		});*/

		return convertView;
	}
	
	private static class ViewHolder {
		ImageView image;
        Button btn_update;
    }
	
	/*public void clearCache() {
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
	}
*/
	/*public void clear() {
		videolist.clear();
		notifyDataSetChanged();
	}*/
	

}