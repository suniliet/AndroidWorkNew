package com.kns.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.kns.model.Custom_Image_Model;
import com.kns.util.ImageUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.sunil.selectmutiple.R;

public class Custom_Image_Adapter extends BaseAdapter{

	private static final String TAG="Custom_Image_Adapter";
	private LayoutInflater mInflater=null;
	private List<Custom_Image_Model> videolist=null;
	private Context context=null;
	private  Bitmap bitmap=null;
	private  Button btn_update;
	private boolean isActionMultiplePick;
	

	public Custom_Image_Adapter(Activity context, List<Custom_Image_Model> list){
		
		mInflater = context.getLayoutInflater();
		this.videolist=list;
		this.context=context;
		
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
	

	public void setMultiplePick(boolean isMultiplePick) {
		this.isActionMultiplePick = isMultiplePick;
	}

	public void selectAll(boolean selection) {
		for (int i = 0; i < videolist.size(); i++) {
			videolist.get(i).isSeleted = selection;

		}
		notifyDataSetChanged();
	}

	
	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < videolist.size(); i++) {
			if (!videolist.get(i).isSeleted) {
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;

		for (int i = 0; i < videolist.size(); i++) {
			if (videolist.get(i).isSeleted) {
				isAnySelected = true;
				break;
			} 
		}

		return isAnySelected;
	}
	
	public ArrayList<Custom_Image_Model> getSelected() {
		ArrayList<Custom_Image_Model> dataT = new ArrayList<Custom_Image_Model>();

		for (int i = 0; i < videolist.size(); i++) {
			if (videolist.get(i).isSeleted) {
				dataT.add(videolist.get(i));
			}
		}

		return dataT;
	}

	
	public void changeSelection(View v, int position) {

		if (videolist.get(position).isSeleted) {
			videolist.get(position).isSeleted = false;
		} else {
			videolist.get(position).isSeleted = true;
		}

		((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(videolist.get(position).isSeleted);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 final ViewHolder holder;
		if (convertView == null ) {
			holder = new ViewHolder();
			
			convertView = mInflater.inflate(R.layout.gallery_item, null);
		
			holder.imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);

			holder.imgQueueMultiSelected = (ImageView) convertView.findViewById(R.id.imgQueueMultiSelected);;
			
			if (isActionMultiplePick) {
				
				   holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
				   
				} else {
					
					holder.imgQueueMultiSelected.setVisibility(View.GONE);
				}

			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.imgQueue.setTag(position);
		
		Custom_Image_Model imagemodel=videolist.get(position);
		//String imageurl=imagemodel.getVideothumburl0();
		String videourl=imagemodel.getImagerealurl();
		String updatedvideothumb=imagemodel.getImagethumburl();
		//holder.image.setDefaultImageResId(R.drawable.no_video);
		
		//Log.v(TAG, "Updated thumb is: "+ updatedvideothumb);
		 ImageUtil.galleryLog(TAG, "Updated thumb is: "+ updatedvideothumb);
		 
		 Transformation transformation = new Transformation() {

	            @Override public Bitmap transform(Bitmap source) {
	                int targetWidth = holder.imgQueue.getWidth();

	                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
	                int targetHeight = (int) (targetWidth * aspectRatio);
	                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
	                if (result != source) {
	                    // Same bitmap is returned if sizes are the same
	                    source.recycle();
	                }
	                return result;
	            }

	            @Override public String key() {
	                return "transformation" + " desiredWidth";
	            }
	        };
		
		if (updatedvideothumb!=null && !updatedvideothumb.isEmpty()) {
			
			Picasso.with(context)
            .load(updatedvideothumb)
            //.resize(150, 150)
          //  .centerCrop()
             .transform(transformation)
            .placeholder(R.drawable.ic_dwnloadthumb)
            .error(R.drawable.ic_nothumb)
            .into(holder.imgQueue);
			
		}
		else{
			holder.imgQueue.setImageResource(R.drawable.no_image);
		}
		
		
		
		if (isActionMultiplePick) {

			holder.imgQueueMultiSelected.setSelected(videolist.get(position).isSeleted);

		}
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
    }
	
}
