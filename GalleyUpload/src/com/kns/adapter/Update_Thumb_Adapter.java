package com.kns.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kns.util.ImageUtil;
import com.squareup.picasso.Picasso;
import com.sunil.selectmutiple.R;

public class Update_Thumb_Adapter extends BaseAdapter{

	private static final String TAG="Search_Partner_Adapter";
	private LayoutInflater mInflater=null;
	private List<String> partnerlist=null;
	private Context context=null;
	private  Bitmap bitmap=null;
	//ImageLoader imageLoader;
	
	public Update_Thumb_Adapter(Activity context, List<String> list){
		
		mInflater = context.getLayoutInflater();
		this.partnerlist=list;
		this.context=context;
		//imageLoader = MyVolleySingleton.getInstance(context).getImageLoader();
	}
	
	@Override
	public int getCount() {
		return partnerlist.size();
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
			convertView = mInflater.inflate(R.layout.list_item_row, null);
			
			holder.image = (ImageView) convertView.findViewById(R.id.image_view);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String thumburl=partnerlist.get(position);
		//String thumburl=partnermodel.getVideothumburl();
		
		if (thumburl!=null) {
			
			//Log.v(TAG, "Image Url is: "+thumburl);
			ImageUtil.galleryLog(TAG, "Image Url is: "+thumburl);
			
			Picasso.with(context)
            .load(thumburl)
            //.resize(150, 150)
           // .centerCrop()
            .placeholder(R.drawable.ic_thumb_generate)
            .error(R.drawable.ic_nothumb)
            .into(holder.image);
			
		}
		else{
			holder.image.setImageResource(R.drawable.no_video);
		}
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView image;
    }

}
