package com.kns.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kns.model.CustomRequestModel;
import com.sunil.selectmutiple.R;

public class CustomRequestAdapter extends BaseAdapter{

	private final List<CustomRequestModel> list;
	private final Activity context;
	private LayoutInflater mInflater=null;
	ArrayList<CustomRequestModel> list1 = new ArrayList<CustomRequestModel>();
	
	public CustomRequestAdapter(Activity context, List<CustomRequestModel> list) {
		// super(context, R.layout.listcheck, list);
		mInflater = context.getLayoutInflater();
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final ViewHolder holder;
		
		if (convertView == null ) {
			
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.customrequest_row, null);
			
			holder.txt_name= (TextView)convertView.findViewById(R.id.textView_custname);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		CustomRequestModel model=list.get(position);
		
		String consumerid=model.getConsumerID();
		String customname=model.getCustomName();
		String customammount=model.getCustomAmount();
		String customrequest=model.getCustomRequest();
		String approvalflag=model.getApproval_Flag();
		
		holder.txt_name.setText(customname);

	
		return convertView;
	}
	
	private static class ViewHolder {
		
		ImageView image;
		TextView txt_name;
		TextView txt_noofimage;
    }

}
