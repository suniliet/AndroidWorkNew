package com.kns.adapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kns.db.DBHelper;
import com.kns.model.VideoModel;
import com.kns.util.ImageUtil;
import com.loopj.android.image.SmartImage;
import com.loopj.android.image.SmartImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sunil.selectmutiple.CustomVideoGallery;
import com.sunil.selectmutiple.R;

public class GalleryVideoAdapter extends BaseAdapter{


	private String TAG="GalleryVideoAdapter";
	private Activity mContext;
	private LayoutInflater infalter;
	private ArrayList<CustomVideoGallery> data = new ArrayList<CustomVideoGallery>();
	//ImageLoader imageLoader;
	HashMap<Integer, Bitmap> cacheBitmap;
	private boolean isActionMultiplePick;
	private DBHelper db=null;
	
	int newpostion=0;
	View newview=null;
	ViewHolder holder;

	@SuppressLint("UseSparseArrays")
	public GalleryVideoAdapter(Activity c, ImageLoader imageLoader) {
		infalter = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = c;
		//this.imageLoader = imageLoader;
		cacheBitmap = new HashMap<Integer, Bitmap>();
	    db=new DBHelper(c);
		//initCacheBitmap();
		// clearCache();
	}
	/*
	private void initCacheBitmap() {
		
		for (int i = 0; i < data.size(); i++) {
			cacheBitmap.put(i, ThumbnailUtils.createVideoThumbnail(data.get(i).sdcardPaththumbvideo, MediaStore.Video.Thumbnails.MINI_KIND));

		}
		
	}*/

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public CustomVideoGallery getItem(int position) {
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

	public ArrayList<CustomVideoGallery> getSelected() {
		ArrayList<CustomVideoGallery> dataT = new ArrayList<CustomVideoGallery>();

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				dataT.add(data.get(i));
			}
		}

		return dataT;
	}

	public void addAll(ArrayList<CustomVideoGallery> arrayList) {

		try {
			this.data.clear();
			this.data.addAll(arrayList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}

	public void changeSelection(View v, int position) {
		
		newpostion=position;
		newview=v;
		String url= data.get(position).sdcardPathvideo;
		try {
			List<VideoModel> listimages=db.GetVideoId(url);
			if (listimages.size() > 0) {
				
				showAlert(mContext, "Do you want to upload this content again?");
				//Toast.makeText(mContext, "This Video already uploaded.", Toast.LENGTH_SHORT).show();
				// images already uploaded
				//((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(false);
			}
			else{
				if (data.get(position).isSeleted) {
					data.get(position).isSeleted = false;
				} else {
					data.get(position).isSeleted = true;
				}

				((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data.get(position).isSeleted);
			}
			}catch (SQLException e) {
				e.printStackTrace();
			}

		
	}

	
	public void showAlert(Activity activity, String message) {
		
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
				
				holder.imageuploaded.setVisibility(View.VISIBLE);
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

			convertView = infalter.inflate(R.layout.gallery_item_video, null);
			holder = new ViewHolder();
			holder.imgQueue = (SmartImageView) convertView.findViewById(R.id.imgQueue);

			holder.imgQueueMultiSelected = (ImageView) convertView
					.findViewById(R.id.imgQueueMultiSelected);
			holder.imageuploaded = (ImageView) convertView
					.findViewById(R.id.videoupload);

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
			
			
			//String path=data.get(position).sdcardPaththumbvideo;
			//Bitmap bitmap=cacheBitmap.get(position);
			//holder.imgQueue.setImageBitmap(cacheBitmap.get(position));
			
			
			//holder.imgQueue.setImageBitmap(data.get(position).bitmap);
			int video_id=data.get(position).video_id;
			//Log.v("Adapter", "video id is: "+video_id);
			 ImageUtil.galleryLog(TAG, "video id is: "+video_id);
			
			 VideoThumbnailImage thumb = new VideoThumbnailImage( video_id,  MediaStore.Video.Thumbnails.MICRO_KIND);

			 holder.imgQueue.setImage(thumb,R.drawable.ic_dwnloadthumb);

			
		/*	
			Bitmap bitmap=ThumbnailUtils.createVideoThumbnail(data.get(position).sdcardPaththumbvideo, MediaStore.Video.Thumbnails.MINI_KIND);
			//holder.imgQueue.setImageUrl(data.get(position).sdcardPaththumbvideo);
			if (bitmap==null) {
				holder.imgQueue.setImageResource(R.drawable.ic_media_video_poster);
				
			}
			else{
				holder.imgQueue.setImageBitmap(bitmap);
			}*/
			//imageLoader.
		/*	imageLoader.displayImage("file://" + data.get(position).sdcardPaththumbvideo,
					holder.imgQueue, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.imgQueue
									.setImageResource(R.drawable.no_media);
							super.onLoadingStarted(imageUri, view);
						}
					});*/

			if (isActionMultiplePick) {

				holder.imgQueueMultiSelected.setSelected(data.get(position).isSeleted);

			}
			
			
			String url= data.get(position).sdcardPathvideo;
			try {
				List<VideoModel> listvideo=db.GetVideoId(url);
				if (listvideo.size() > 0) {
					
					// images already uploaded
					//((ViewHolder) convertView.sett()).imguploaded.setVisibility(View.VISIBLE);
					holder.imageuploaded.setVisibility(View.VISIBLE);
					//holder.imgQueueMultiSelected.setVisibility(View.GONE);
					//holder.imgQueueMultiSelected.setSelected(false);
				
				}
				else{
					holder.imageuploaded.setVisibility(View.GONE);
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
		SmartImageView imgQueue;
		ImageView imgQueueMultiSelected;
		ImageView imageuploaded;
	}

/*	public void clearCache() {
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
	}
*/
	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}
	
	 public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
			    int reqHeight) {

			   Bitmap bm = null;
			   // First decode with inJustDecodeBounds=true to check dimensions
			   final BitmapFactory.Options options = new BitmapFactory.Options();
			   options.inJustDecodeBounds = true;
			   BitmapFactory.decodeFile(path, options);

			   // Calculate inSampleSize
			   options.inSampleSize = calculateInSampleSize(options, reqWidth,
			     reqHeight);

			   // Decode bitmap with inSampleSize set
			   options.inJustDecodeBounds = false;
			   bm = BitmapFactory.decodeFile(path, options);

			   return bm;
			  }

			  public int calculateInSampleSize(

			  BitmapFactory.Options options, int reqWidth, int reqHeight) {
			   // Raw height and width of image
			   final int height = options.outHeight;
			   final int width = options.outWidth;
			   int inSampleSize = 1;

			   if (height > reqHeight || width > reqWidth) {
			    if (width > height) {
			     inSampleSize = Math.round((float) height
			       / (float) reqHeight);
			    } else {
			     inSampleSize = Math.round((float) width / (float) reqWidth);
			    }
			   }

			   return inSampleSize;
			  }

			
		/*	  private static class ThumbnailBinder implements SimpleCursorAdapter.ViewBinder {
			    @Override
			    public boolean setViewValue(View v, Cursor c, int column) {
			      if (column == c.getColumnIndex(MediaStore.Video.Media._ID)) {
			        VideoThumbnailImage thumb = new VideoThumbnailImage( c.getInt(column),  MediaStore.Video.Thumbnails.MICRO_KIND);

			        ((SmartImageView)v).setImage(thumb,R.drawable.ic_media_video_poster);

			        return(true);
			      }

			      return(false);
			    }
			  }*/

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