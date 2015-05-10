package com.sunil.selectmutiple;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.GridView;

public class MyCustom_View_Activity extends Activity{
	
	private static final String TAG="MyCustom_View_Activity";
	private Context context=null;
	private GridView gridview;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_custom_content_view);
		
		context=this;
		
		gridview=(GridView)findViewById(R.id.gridGallery_mycustom);
		
		
	}

}
