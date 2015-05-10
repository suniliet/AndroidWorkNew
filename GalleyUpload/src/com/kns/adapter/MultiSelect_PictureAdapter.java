package com.kns.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kns.model.MultiSelectPictureModel;
import com.kns.util.ImageUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.sunil.selectmutiple.R;

public class MultiSelect_PictureAdapter extends BaseAdapter{

	private static final String TAG="Muti_SelectVideoAdapter";
	private LayoutInflater mInflater=null;
	private List<MultiSelectPictureModel> imagelist=null;
	private Context context=null;
	private  Bitmap bitmap=null;
	private  Button btn_update;
	private boolean isActionMultiplePick;
	String adminapproved="";
	String uploadedid="";
	

	public MultiSelect_PictureAdapter(Activity context, List<MultiSelectPictureModel> list){
		
		mInflater = context.getLayoutInflater();
		this.imagelist=list;
		this.context=context;
		
	}
	
	@Override
	public int getCount() {
		return imagelist.size();
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
		for (int i = 0; i < imagelist.size(); i++) {
			
			if (imagelist.get(i).getAdminApprovedFlag().trim().equalsIgnoreCase("0")) {
				imagelist.get(i).isSeleted = false;
			}else{
				imagelist.get(i).isSeleted = selection;
			}
			

		}
		notifyDataSetChanged();
	}
	
	public void selectTencheck(boolean selection) {
		if (imagelist.size() >= 10) {
			
			for (int i = 0; i < 10; i++) {
				
				if (imagelist.get(i).getAdminApprovedFlag().trim().equalsIgnoreCase("0")) {
					imagelist.get(i).isSeleted = false;
				}else{
				imagelist.get(i).isSeleted = selection;
				}

			}
			notifyDataSetChanged();
		}
		else{
			
			Toast.makeText(context, "There are less than 10 pictures", Toast.LENGTH_LONG).show();
		}
		
	}
	
	public void selectTwentycheck(boolean selection) {
		if (imagelist.size() >= 20) {
			
			for (int i = 0; i < 20; i++) {
				if (imagelist.get(i).getAdminApprovedFlag().trim().equalsIgnoreCase("0")) {
					imagelist.get(i).isSeleted = false;
				}else{
				imagelist.get(i).isSeleted = selection;
				}

			}
			notifyDataSetChanged();
		}
		else{
			
			Toast.makeText(context, "There are less than 20 pictures", Toast.LENGTH_LONG).show();
		}
		
	}
	
	public void selectFiftycheck(boolean selection) {
		if (imagelist.size() >= 50) {
			
			for (int i = 0; i < 50; i++) {
				if (imagelist.get(i).getAdminApprovedFlag().trim().equalsIgnoreCase("0")) {
					imagelist.get(i).isSeleted = false;
				}else{
				imagelist.get(i).isSeleted = selection;
				}

			}
			notifyDataSetChanged();
		}
		else{
			
			Toast.makeText(context, "There are less than 50 pictures", Toast.LENGTH_LONG).show();
		}
		
	}
	
	public void selectHundredcheck(boolean selection) {
		if (imagelist.size() >= 100) {
			
			for (int i = 0; i < 100; i++) {
				if (imagelist.get(i).getAdminApprovedFlag().trim().equalsIgnoreCase("0")) {
					imagelist.get(i).isSeleted = false;
				}else{
				imagelist.get(i).isSeleted = selection;
				}

			}
			notifyDataSetChanged();
		}
		else{
			
			Toast.makeText(context, "There are less than 100 pictures", Toast.LENGTH_LONG).show();
		}
		
	}

	public void unckeckedAll(boolean selection) {
		for (int i = 0; i < imagelist.size(); i++) {
			imagelist.get(i).isSeleted = selection;

		}
		notifyDataSetChanged();
	}

	
	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < imagelist.size(); i++) {
			if (!imagelist.get(i).isSeleted) {
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;

		for (int i = 0; i < imagelist.size(); i++) {
			if (imagelist.get(i).isSeleted) {
				isAnySelected = true;
				break;
			} 
		}

		return isAnySelected;
	}
	
	public ArrayList<MultiSelectPictureModel> getSelected() {
		ArrayList<MultiSelectPictureModel> dataT = new ArrayList<MultiSelectPictureModel>();

		for (int i = 0; i < imagelist.size(); i++) {
			if (imagelist.get(i).isSeleted) {
				dataT.add(imagelist.get(i));
			}
		}

		return dataT;
	}

	
	public void changeSelection(View v, int position, String adminapproveflag) {
		
		//Log.v(TAG, "item get in chnageselection approv: "+adminapproveflag);
		//Log.v(TAG, "Getview approv in selection: "+adminapproved);
		
		 ImageUtil.galleryLog(TAG, "item get in chnageselection approv: "+adminapproveflag);
		 ImageUtil.galleryLog(TAG, "Getview approv in selection: "+adminapproved);

		if (adminapproveflag.trim().equalsIgnoreCase("0")) {
			
			Toast.makeText(context, "This Picture is not approved by Admin yet.", Toast.LENGTH_SHORT).show();
			((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(false);
		}
		else{
			
			if (imagelist.get(position).isSeleted) {
				imagelist.get(position).isSeleted = false;
			} else {
				imagelist.get(position).isSeleted = true;
			}

			((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(imagelist.get(position).isSeleted);
		}
		
		
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
		
		MultiSelectPictureModel imagemodel=imagelist.get(position);
		//String imageurl=imagemodel.getVideothumburl0();
		String videourl=imagemodel.getImagerealurl();
		String updatedimagethumb=imagemodel.getImagethumburl();
		adminapproved=imagemodel.getAdminApprovedFlag();
		uploadedid=imagemodel.getUploadedid();
		//Log.v(TAG, "Getview approv: "+adminapproved);
		ImageUtil.galleryLog(TAG, "Getview approv: "+adminapproved);
		
		//holder.image.setDefaultImageResId(R.drawable.no_video);
		
		//Log.v(TAG, "Updated thumb is: "+ updatedimagethumb);
		ImageUtil.galleryLog(TAG, "Updated thumb is: "+ updatedimagethumb);
		
		
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
        
      /*  //for this required background thread
		try {
			Bitmap	image = Picasso.with(context).load(videourl).get();
			int width = image.getWidth();
	        int height = image.getHeight();
	        ImageUtil.galleryLog(TAG, "width is: "+width);
            ImageUtil.galleryLog(TAG, "height is: "+height);
		} catch (IOException e) {
			e.printStackTrace();
		}
        */
		
		if (updatedimagethumb!=null && !updatedimagethumb.isEmpty()) {
			
			Picasso.with(context)
            .load(updatedimagethumb)
            .resize(150, 150)
            .centerInside()
    
            //.transform(transformation)
            .placeholder(R.drawable.ic_dwnloadthumb)
            .error(R.drawable.ic_nothumb)
           
            .into(holder.imgQueue);
            
        /*    .into(new Target() {
				
				@Override
				public void onPrepareLoad(Drawable arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
					// TODO Auto-generated method stub
					int width = bitmap.getWidth();
	                int height = bitmap.getHeight();
	                ImageUtil.galleryLog(TAG, "width is: "+width);
	                ImageUtil.galleryLog(TAG, "height is: "+height);
	                holder.imgQueue.setImageBitmap(bitmap);
				}
				
				@Override
				public void onBitmapFailed(Drawable arg0) {
					// TODO Auto-generated method stub
					
				}
			});*/
			
		}
		else{
			holder.imgQueue.setImageResource(R.drawable.no_image);
		}
		
		
		
		if (isActionMultiplePick) {

			holder.imgQueueMultiSelected.setSelected(imagelist.get(position).isSeleted);

		}
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
    }
}