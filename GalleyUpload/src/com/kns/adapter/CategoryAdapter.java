package com.kns.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kns.model.CategoryModel;
import com.kns.util.ImageUtil;
import com.sunil.selectmutiple.R;

public class CategoryAdapter extends BaseAdapter{
	
	private final List<CategoryModel> list;
	private final Activity context;
	private LayoutInflater mInflater=null;
	ArrayList<CategoryModel> list1 = new ArrayList<CategoryModel>();
	private static final String TAG="CategoryAdapter";
	String catid1; 
	String catid2; 
	String catid3;
	private int numberOfCheckboxesChecked;
	CheckBox	checkbox =null;
	private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
	
	public CategoryAdapter(Activity context, List<CategoryModel> list, String catid1, String catid2, String catid3) {
		// super(context, R.layout.listcheck, list);
		mInflater = context.getLayoutInflater();
		this.context = context;
		this.list = list;
		this.catid1=catid1;
		this.catid2=catid2;
		this.catid3=catid3;
		
		  
        if (catid1.trim().equalsIgnoreCase("0") && catid2.trim().equalsIgnoreCase("0") && catid3.trim().equalsIgnoreCase("0")) {
			  
        	for (int i = 0; i < this.getCount(); i++) {
		        itemChecked.add(i, false); // initializes all items value with false
		    }
		  }
		  else{
			  
			  for (int i = 0; i < this.getCount(); i++) {
				
				  CategoryModel model=list.get(i);
				  if (model.getCat_id().trim().equalsIgnoreCase(catid1) || model.getCat_id().trim().equalsIgnoreCase(catid2) ||model.getCat_id().trim().equalsIgnoreCase(catid3)) {
					  itemChecked.add(i, true);
				}
				  else{
					  itemChecked.add(i, false);
				  }
			}
			
			  
		  }
		 
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
	
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	
		 
		 View view = convertView;
		 if (view == null) {
			 view = mInflater.inflate(R.layout.cate_list_item, parent, false);
			}

		  CategoryModel p = getProduct(position);
		  TextView	text = (TextView) view.findViewById(R.id.textView_custname);
	    	checkbox = (CheckBox) view.findViewById(R.id.checkBox1);
		 /// checkbox.setOnCheckedChangeListener(myCheckChangList);
		  checkbox.setTag(position);
		
		  
		  String catid=p.getCat_id();
		  
		  checkbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox) arg0.findViewById(R.id.checkBox1);
				 getProduct((Integer) arg0.getTag()).setIsselected(cb.isChecked()) ;
					
				 int numberOfCheckboxesChecked=0;

				 for (int i = 0; i < itemChecked.size(); i++) {
					boolean istrue=itemChecked.get(i);
					if (istrue) {
						numberOfCheckboxesChecked++;
					}
				}
				 
				 if (numberOfCheckboxesChecked>=3) {
					  cb.setChecked(false);
					  itemChecked.set(position, false);
					  getProduct((Integer) arg0.getTag()).setIsselected(false) ;
					  Toast.makeText(context, "Please select max 3 Categories.", Toast.LENGTH_LONG).show();
				}
				 else{
					 
					 if (cb.isChecked()) {
			                itemChecked.set(position, true);
			                // do some operations here
			            } else if (!cb.isChecked()) {
			                itemChecked.set(position, false);
			                // do some operations here
			            }
				 }
				 
				/* List<CategoryModel> list=getChecked();
					Toast.makeText(context, "size is: "+list.size(), Toast.LENGTH_LONG).show();*/
		            
			}
		});
		
		  checkbox.setChecked(itemChecked.get(position));
		  
		 /* Log.v(TAG, "Cat id is: "+catid);
		  Log.v(TAG, "Cat id1 is: "+catid1);
		  Log.v(TAG, "Cat id2 is: "+catid2);
		  Log.v(TAG, "Cat id3 is: "+catid3);*/
		  /*ImageUtil.galleryLog(TAG, "Cat id is: "+catid);
		  ImageUtil.galleryLog(TAG,"Cat id1 is: "+catid1);
		  ImageUtil.galleryLog(TAG, "Cat id2 is: "+catid2);
		  ImageUtil.galleryLog(TAG, "Cat id3 is: "+catid3)*/;
		  
		/*  if (catid1.trim().equalsIgnoreCase("0") && catid2.trim().equalsIgnoreCase("0") && catid3.trim().equalsIgnoreCase("0")) {
			  
			  checkbox.setChecked(p.isIsselected());
		  }
		  else{
			  
			  if (catid.trim().equalsIgnoreCase(catid1)) {
				  
				  checkbox.setChecked(true);
				 // getProduct((Integer) checkbox.getTag()).setIsselected(true) ;
				 
			   }
			  else if (catid.trim().equalsIgnoreCase(catid2)) {
				
				  checkbox.setChecked(true);
				 // getProduct((Integer) checkbox.getTag()).setIsselected(true) ;
			  }
			  else if (catid.trim().equalsIgnoreCase(catid3)) {
				  
				  checkbox.setChecked(true);
				 // getProduct((Integer) checkbox.getTag()).setIsselected(true) ;
				}
			  else{
				  checkbox.setChecked(false);
			  }
		  }*/
		  
		  text.setText(p.getCat_name());
		
		
		 // checkbox.setChecked(p.isIsselected());
		
		return view;
	}

	CategoryModel getProduct(int position) {
			return ((CategoryModel) getItem(position));
		}
	 
		public ArrayList<CategoryModel> getChecked() {
			ArrayList<CategoryModel> list1 = new ArrayList<CategoryModel>();
			for (CategoryModel details : list) {
				if (details.isIsselected())
					list1.add(details);
			}
			return list1;
		}
	

		OnCheckedChangeListener myCheckChangList = new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ArrayList<CategoryModel> list1=getChecked();
				//CategoryModel model = (CategoryModel) buttonView.getTag();
				//Log.v(TAG, "checked size is: "+list1.size());
				  //ImageUtil.galleryLog(TAG, "checked size is: "+list1.size());
				
				/*if (list1.size() <= 3) {
					
					 buttonView.setChecked(false);
					 //getProduct((Integer) buttonView.getTag()).setIsselected(isChecked) ;
					 Toast.makeText(context, "Please select max 3 Categories.", Toast.LENGTH_LONG).show();
				}*/
				
				/* if (numberOfCheckboxesChecked >= 3) {
					// checkbox.setChecked(false);
					 buttonView.setChecked(false);
					 Toast.makeText(context, "Please select max 3 Categories.", Toast.LENGTH_LONG).show();
			        } else {
			            // the checkbox either got unchecked
			            // or there are less than 2 other checkboxes checked
			            // change your counter accordingly
			            if (isChecked) {
			                numberOfCheckboxesChecked++;
			            } else {
			                numberOfCheckboxesChecked--;
			            }

			            // now everything is fine and you can do whatever
			            // checking the checkbox should do here
			        }
				 getProduct((Integer) buttonView.getTag()).setIsselected(isChecked) ;*/
				
				
				 if(numberOfCheckboxesChecked == 3 && isChecked){
					 buttonView.setChecked(false);
				 	Toast.makeText(context, "Please select max 3 Categories.", Toast.LENGTH_LONG).show();
		           }else if(isChecked){

		        	   numberOfCheckboxesChecked++;
		                
		            }else if(!isChecked){
		            	numberOfCheckboxesChecked--;
		        }
				 getProduct((Integer) buttonView.getTag()).setIsselected(isChecked) ;
				
				/* if(isChecked)
                 {
					 numberOfCheckboxesChecked++;
                 }
                 else if(!isChecked)
                 {
                	 numberOfCheckboxesChecked--;
                 }
                 if(numberOfCheckboxesChecked >=4)// it will allow 3 checkboxes only
                 {
                	 Toast.makeText(context, "Please select max 3 Categories.", Toast.LENGTH_LONG).show();
                     buttonView.setChecked(false);
                     numberOfCheckboxesChecked--;
                    // getProduct((Integer) buttonView.getTag()).setIsselected(false) ;
                 }
                 else
                 {
                	 //model.setIsselected(isChecked);
                	 getProduct((Integer) buttonView.getTag()).setIsselected(isChecked) ;
                	 
                 }*/
               
				/*if (isChecked) {
					
					if (list1.size() >= 3) {
						
						 buttonView.setChecked(false);
						 //getProduct((Integer) buttonView.getTag()).setIsselected(isChecked) ;
						
						 Toast.makeText(context, "Please select max 3 Categories.", Toast.LENGTH_LONG).show();
					}
					else{
					
						getProduct((Integer) buttonView.getTag()).setIsselected(isChecked) ;
						
					}
					
				
				}
				else{
					getProduct((Integer) buttonView.getTag()).setIsselected(isChecked) ;
				}
				*/
			}
		};

}
