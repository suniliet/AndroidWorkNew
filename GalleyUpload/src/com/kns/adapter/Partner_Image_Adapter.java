package com.kns.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.kns.model.PartnerImage_Model;
import com.kns.util.MyVolleySingleton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.sunil.selectmutiple.R;

public class Partner_Image_Adapter extends BaseAdapter{

	private static final String TAG="Partner_Image_Adapter";
	private LayoutInflater mInflater=null;
	private List<PartnerImage_Model> imagelist=null;
	private Context context=null;
	private  Bitmap bitmap=null;
	ImageLoader imageLoader;
	
	public Partner_Image_Adapter(Activity context, List<PartnerImage_Model> list){
		
		mInflater = context.getLayoutInflater();
		this.imagelist=list;
		this.context=context;  
		//imageLoader = MyVolleySingleton.getInstance(context).getImageLoader();
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null ) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.image_view_row, null);
			
			holder.image = (ImageView) convertView.findViewById(R.id.image_view);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		//holder.image.setDefaultImageResId(R.drawable.no_image);
		PartnerImage_Model imagemodel=imagelist.get(position);
		String imageurl=imagemodel.getImagerealurl();
		String imageurlthumb=imagemodel.getImagethumburl();
		
		
		Transformation transformation = new Transformation() {

            @Override public Bitmap transform(Bitmap source) {
                int targetWidth = holder.image.getWidth();

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
	
		
		if (imageurlthumb!=null && !imageurlthumb.isEmpty()) {
			
			Picasso.with(context)
            .load(imageurlthumb)
           // .resize(150, 150)
          //  .centerCrop()
            .transform(transformation)
            .placeholder(R.drawable.ic_dwnloadthumb)
            .error(R.drawable.no_image)
            .into(holder.image);
			
			
			/*holder.image.setErrorImageResId(R.drawable.no_image);
			//holder.image.setAdjustViewBounds(true);
			holder.image.setScaleType(ScaleType.CENTER);
			holder.image.setImageUrl(imageurl, imageLoader);
			*/
		/*	ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(imageurl,
					holder.image, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.image.setImageResource(R.drawable.no_image);
							super.onLoadingStarted(imageUri, view);
						}
					});*/
		}
		else{
			holder.image.setImageResource(R.drawable.no_image);
		}
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView image;
       
    }
	
}