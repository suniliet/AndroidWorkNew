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
import android.widget.TextView;

import com.kns.model.SetModel;
import com.kns.util.ImageUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.sunil.selectmutiple.R;

public class SetAdapter extends BaseAdapter{

	private static final String TAG="CategoryAdapter";
	private LayoutInflater mInflater=null;
	private List<SetModel> catagorylist=null;
	private Context context=null;
	private  Bitmap bitmap=null;
	//ImageLoader imageLoader;
	
	public SetAdapter(Activity context, List<SetModel> list){
		
		mInflater = context.getLayoutInflater();
		this.catagorylist=list;
		this.context=context;
		//imageLoader = MyVolleySingleton.getInstance(context).getImageLoader();
	}
	
	@Override
	public int getCount() {
		return catagorylist.size();
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
			convertView = mInflater.inflate(R.layout.catagoryitemrow, null);
			
			holder.image = (ImageView) convertView.findViewById(R.id.image_view);
			holder.txt_name= (TextView)convertView.findViewById(R.id.textView_catname);
			holder.txt_noofimage= (TextView)convertView.findViewById(R.id.textView_noofimage);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		SetModel model=catagorylist.get(position);
		String categoryid=model.getCatagoryid();
		String catagoryname=model.getCatagoryname();
		String categoryurl=model.getCatagorythumb();
		String noofimages=model.getNoofimages();
		//String thumburl=partnermodel.getVideothumburl();
		holder.txt_name.setText(catagoryname);
		holder.txt_noofimage.setText(noofimages+"/100");
		
	/*	
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
		*/
		
		if (categoryurl!=null && !categoryurl.isEmpty()) {
			
			//Log.v(TAG, "Image Url is: "+categoryurl);
			ImageUtil.galleryLog(TAG, "Image Url is: "+categoryurl);
			
			Picasso.with(context)
            .load(categoryurl)
            .resize(150, 150)
            .centerInside()
           // .transform(transformation)
            .placeholder(R.drawable.ic_dwnloadthumb)
            .error(R.drawable.no_image)
            .into(holder.image);
			
		}
		else{
			holder.image.setImageResource(R.drawable.no_image);
		}
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView image;
		TextView txt_name;
		TextView txt_noofimage;
    }
	
}
