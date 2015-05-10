package com.kns.adapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kns.db.DBHelper;
import com.kns.model.ImageModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.sunil.selectmutiple.CustomGallery;
import com.sunil.selectmutiple.R;

public class GalleryAdapter extends BaseAdapter{

	private final static String TAG="GalleryAdapter";
	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();
	//ImageLoader imageLoader;
    
	private boolean isActionMultiplePick;
	DBHelper db=null;
	int newpostion=0;
	View newview=null;
	ViewHolder holder;

	public GalleryAdapter(Context c, ImageLoader imageLoader) {
		infalter = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = c;
		//this.imageLoader = imageLoader;
		db=new DBHelper(c);
		// clearCache();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public CustomGallery getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setMultiplePick(boolean isMultiplePick) {
		this.isActionMultiplePick = isMultiplePick;
	}

	public void selectAll(boolean selection) {
		for (int i = 0; i < data.size(); i++) {
			data.get(i).isSeleted = selection;

		}
		notifyDataSetChanged();
	}

	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).isSeleted) {
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				isAnySelected = true;
				break;
			} 
		}

		return isAnySelected;
	}

	public ArrayList<CustomGallery> getSelected() {
		ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				dataT.add(data.get(i));
			}
		}

		return dataT;
	}

	public void addAll(ArrayList<CustomGallery> files) {

		try {
			this.data.clear();
			this.data.addAll(files);

		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}

	public void changeSelection(View v, int position) {
		Log.v(TAG, "changeSelection called");
		newpostion=position;
		newview=v;
		String url= data.get(position).sdcardPath;
		try {
			List<ImageModel> listimages=db.GetImageUrl(url);
			if (listimages.size() > 0) {
				
				showAlert(mContext, "Do you want to upload this content again?");
				//Toast.makeText(mContext, "This images already uploaded", Toast.LENGTH_SHORT).show();
				// images already uploaded
				
				//((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(false);
			}
			else{
				

				if (data.get(position).isSeleted) {
				
					data.get(position).isSeleted = false;
					Log.v(TAG, "not selected");
				} else {
					
					data.get(position).isSeleted = true;
					Log.v(TAG, "  selected");
				}

				((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data.get(position).isSeleted);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public void showAlert(Context activity, String message) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(message);
		
		builder.setCancelable(false);
		builder.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				
				((ViewHolder) newview.getTag()).imgQueueMultiSelected.setSelected(false);
				data.get(newpostion).isSeleted = false;
			}

		});
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				
				Log.v(TAG, "Click yes");
				
				holder.imguploaded.setVisibility(View.VISIBLE);
				holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
			
			
				if (data.get(newpostion).isSeleted) {
				
				/*	data.get(newpostion).isSeleted = false;
					Log.v(TAG, "not selected");*/
				} else {
					
					data.get(newpostion).isSeleted = true;
					Log.v(TAG, "  selected");
					
				}
				
				holder.imgQueueMultiSelected.setSelected(data.get(newpostion).isSeleted);

				((ViewHolder) newview.getTag()).imgQueueMultiSelected.setSelected(data.get(newpostion).isSeleted);
			
			}

		});

		AlertDialog alert = builder.create();
		alert.show();
		TextView messageText = (TextView) alert.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//final ViewHolder holder;
		if (convertView == null) {

			convertView = infalter.inflate(R.layout.gallery_item, null);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);

			holder.imgQueueMultiSelected = (ImageView) convertView
					.findViewById(R.id.imgQueueMultiSelected);
			holder.imguploaded=(ImageView)convertView.findViewById(R.id.imguploaded);
			// here we can check the which images already uploaded.
		
			if (isActionMultiplePick) {
				
			   holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
			   
			} else {
				
				holder.imgQueueMultiSelected.setVisibility(View.GONE);
			}

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.imgQueue.setTag(position);

		try {
			
			String fileurl="file://" + data.get(position).sdcardPath;


		/*	ImageUtil.galleryLog(TAG, "imagefiles :"+"file://" + data.get(position).sdcardPath);
			imageLoader.displayImage("file://" + data.get(position).sdcardPath,
					holder.imgQueue, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.imgQueue
									.setImageResource(R.drawable.ic_dwnloadthumb);
							super.onLoadingStarted(imageUri, view);
						}
					});*/

			
			if (fileurl!=null && !fileurl.isEmpty()) {
				
				//Log.v(TAG, "Image Url is: "+bucketprofile);
				
				Picasso.with(mContext)
	            .load(fileurl)
	            .resize(150, 150)
	            .centerCrop()
	            .placeholder(R.drawable.ic_dwnloadthumb)
	            .error(R.drawable.no_image)
	            .into(holder.imgQueue);
				
			}
			else{
				holder.imgQueue.setImageResource(R.drawable.no_image);
			}
			
			
			
			if (isActionMultiplePick) {

				holder.imgQueueMultiSelected.setSelected(data.get(position).isSeleted);

			}
			
			String url= data.get(position).sdcardPath;
			try {
				List<ImageModel> listimages=db.GetImageUrl(url);
				if (listimages.size() > 0) {
					
					// images already uploaded
					//((ViewHolder) convertView.sett()).imguploaded.setVisibility(View.VISIBLE);
					holder.imguploaded.setVisibility(View.VISIBLE);
					//holder.imgQueueMultiSelected.setVisibility(View.GONE);
					//holder.imgQueueMultiSelected.setSelected(false);
				
				}
				else{
					holder.imguploaded.setVisibility(View.GONE);
					holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}



		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
		ImageView imguploaded;
	}

	/*public void clearCache() {
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
	}*/

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}
}
