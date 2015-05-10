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
import android.widget.TextView;

import com.kns.model.SetModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.sunil.selectmutiple.R;

public class PictureSetAdapter extends BaseAdapter {
	
	private static final String TAG="Partner_Image_Adapter";
	private LayoutInflater mInflater=null;
	private List<SetModel> setlist=null;
	private Context context=null;
	private  Bitmap bitmap=null;

	
public PictureSetAdapter(Activity context, List<SetModel> list){
		
		mInflater = context.getLayoutInflater();
		this.setlist=list;
		this.context=context;
		//imageLoader = MyVolleySingleton.getInstance(context).getImageLoader();
		
	
	}
	
	@Override
	public int getCount() {
		return setlist.size();
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
			convertView = mInflater.inflate(R.layout.pictureset_item_row, null);
			
			holder.image = (ImageView) convertView.findViewById(R.id.imgQueue);
			holder.setname = (TextView) convertView.findViewById(R.id.textView_setname);
			holder.approvestatus = (TextView) convertView.findViewById(R.id.textView_approve);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		//holder.image.setDefaultImageResId(R.drawable.no_image);
		SetModel setmodel=setlist.get(position);
		String categoryid=setmodel.getCatagoryid();
		String catagoryname=setmodel.getCatagoryname();
		String categoryurl=setmodel.getCatagorythumb();
		String noofimages=setmodel.getNoofimages();
		String liveflag=setmodel.getLiveFlag();
		
		if (liveflag.trim().equalsIgnoreCase("1")) {
			
			holder.approvestatus.setText("Live");
			
		}
		else{
			holder.approvestatus.setText("Pending");
		}
		
		holder.setname.setText(catagoryname);
		
		
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
		
		
		if (categoryurl!=null) {
			
			Picasso.with(context)
            .load(categoryurl)
           // .resize(150, 150)
           // .centerCrop()
            .placeholder(R.drawable.ic_dwnloadthumb)
            .error(R.drawable.no_image)
              .transform(transformation)
           // .into(holder.image);
			  .into(holder.image, new Callback() {
                  @Override
                  public void onSuccess() {
                     // holder.progressBar_picture.setVisibility(View.GONE);
                  }

                  @Override
                  public void onError() {
                     
                      //holder.progressBar_picture.setVisibility(View.GONE);
                  }
              });
			
		}
		else{
			holder.image.setImageResource(R.drawable.no_image);
		}
		
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView image;
		TextView setname;
		TextView approvestatus;
       
    }

}
