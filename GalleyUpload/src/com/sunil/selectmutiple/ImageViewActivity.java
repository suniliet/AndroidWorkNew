package com.sunil.selectmutiple;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.Tracker;
import com.kns.util.ImageUtil;
import com.kns.util.TouchImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class ImageViewActivity  extends Activity implements OnClickListener{
	
	private static final String TAG="ImageViewActivity";
	private Context context=null;
	private TouchImageView imageview=null;
	private ProgressBar progressbar;
	private ImageButton btn_download=null;
	private Button btn_back=null;
	
	
	private String image_url;
	
	private static final float MIN_ZOOM = 1.0f;
	private static final float MAX_ZOOM = 5.0f;

	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	Tracker tracker;
	String realimageurl="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageviewsdceen);
		context=this;
		
		
		imageview=(TouchImageView)findViewById(R.id.image_view);
		
		btn_back=(Button)findViewById(R.id.button_back);
		btn_back.setOnClickListener(this);
		imageview.setMaxZoom(4);
		/*imageview.setOnTouchListener(this);
		imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		*/
		Bundle bundle=getIntent().getExtras();
		if (bundle!=null) {
			image_url=bundle.getString("ImageUrl");
			realimageurl=bundle.getString("realurl");
		}
		
		//ImageLoader imageLoader = ImageLoader.getInstance();
		ImageUtil.galleryLog(TAG, "imageurl is: "+realimageurl);
		
	/*	Transformation transformation = new Transformation() {

            @Override public Bitmap transform(Bitmap source) {
                int targetWidth = imageview.getWidth();

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
        };*/

		if (realimageurl!=null && !realimageurl.isEmpty()) {
			
			Picasso.with(context)
	        .load(realimageurl)
	       .resize(300, 300)
	        .centerInside()
	       // .transform(transformation)
	        .placeholder(R.drawable.ic_dwnloadthumb)
	        .error(R.drawable.no_image)
	        .into(imageview);
		}
		else{
			
			imageview.setImageResource(R.drawable.no_image);
		}
		
		
			
	}
	


		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onClick(View v) {
			
			if (btn_back==v) {
				
				finish();
			}
			
		}
		


}
