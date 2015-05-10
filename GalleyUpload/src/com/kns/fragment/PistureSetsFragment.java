package com.kns.fragment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kns.adapter.CategoryAdapter;
import com.kns.adapter.PictureSetAdapter;
import com.kns.db.DBHelper;
import com.kns.model.CategoryModel;
import com.kns.model.RequestedSetModel;
import com.kns.model.SetModel;
import com.kns.util.FragmentLifecycle;
import com.kns.util.ImageConstant;
import com.kns.util.ImageUtil;
import com.sunil.selectmutiple.LoadSetActivity;
import com.sunil.selectmutiple.MyUploadActivity;
import com.sunil.selectmutiple.R;
import com.sunil.selectmutiple.SelectCoverPhotoActivity;
import com.sunil.selectmutiple.SeletctMultipleFile;

public class PistureSetsFragment extends Fragment implements OnClickListener, FragmentLifecycle{

	private final static String TAG="PistureSetsFragment";
	private static SharedPreferences Prefs = null;
	private static String prefname = "galleryPrefs";
	private GridView gridview=null;
	private TextView textviwe=null;
	private ImageButton btn_createnewset;
	List<SetModel> setlist=new ArrayList<SetModel>();
	
	EditText editcatname;
	String categoryname="";
	String categoryid="";
	int setthumb=0;
	String approveflag="";
	
	private ProgressDialog prodialog1;
	private ProgressDialog prodialog2;
	private ProgressDialog prodialog;
	private ProgressDialog prodialog_set;
	private ProgressDialog prodialog_addedit;
	private ProgressDialog prodialog_myupload;
	private ProgressDialog prodialog_viewcategory;
	private ProgressDialog prodialog_sendrequest;
	private ProgressDialog prodialog_cat;
	
	AlertDialog alert = null;
	String jsonresp="";
	private DBHelper db=null;
	List<CategoryModel> list;
	AlertDialog dialog;
	AlertDialog cat_dialog;
	ArrayList<CategoryModel> checked_category=null;
	String noofimages="";
	String catid1="";
	String catid2="";
	String catid3="";
	String liveflag ="";
	CategoryAdapter newadapter;
	
	 @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         
		 View view = inflater.inflate(R.layout.mypicture_sets, container, false);
         gridview=(GridView)view.findViewById(R.id.gridView_pictureset);
     
         textviwe= (TextView)view.findViewById(R.id.textView);
         btn_createnewset=(ImageButton)view.findViewById(R.id.imageButton_createnewset);
         btn_createnewset.setOnClickListener(this);
         
         gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				SetModel setmodel=setlist.get(arg2);
				categoryid=setmodel.getCatagoryid();
				categoryname=setmodel.getCatagoryname();
				String categoryurl=setmodel.getCatagorythumb();
				noofimages=setmodel.getNoofimages();
				approveflag=setmodel.getApprovalflag();
				String partnerid=setmodel.getPartnerid();
				liveflag = setmodel.getLiveFlag();
				
				if (liveflag.trim().equalsIgnoreCase("1")) {
					
					showDialogItemClickLive();
				}
				else{
					showDialogItemClick();
				}
				
				
			}
		});
        
       
         return view;
    }
	 
	 @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//prodialog1=ProgressDialog.show(getActivity(), "", "Loading..");
		     db=new DBHelper(getActivity());
		  	 Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
	         jsonresp=Prefs.getString(ImageConstant.MYUPLOAD, "");
	       //  Toast.makeText(getActivity(), "Picture sets require a minimum of 10 pictures to go live in the store.", Toast.LENGTH_LONG).show();
	         
	         //Log.v(TAG, "jsonresponse :"+jsonresp);
	         ImageUtil.galleryLog(TAG, "jsonresponse :"+jsonresp);
	         if (jsonresp.equalsIgnoreCase("")) {
	 			
	 		   }
	         else{
	         	
	        	 if (jsonresp != null) {
	        		 setlist.clear();
	        		 try {
	        		 
	        			JSONArray jsonarray=new JSONArray(jsonresp);
    					for (int i = 0; i < jsonarray.length(); i++) {
    						JSONObject obj=jsonarray.getJSONObject(i);
    						String status=obj.getString("Status");
    						String partnerid=obj.getString("PartnerID");
    						
    						JSONArray array=obj.getJSONArray("PictureSets");
    						for (int j = 0; j < array.length(); j++) {
    							
    							JSONObject obj1=array.getJSONObject(j);
    							boolean isstatus=obj1.isNull("Status");
    							if (!isstatus) {
									String ststusupdate=obj1.getString("Status");
									if (ststusupdate.trim().equalsIgnoreCase("This Partner Dont Have any sets")) {
										
										//Log.v(TAG, "This partner donot have any set");
										ImageUtil.galleryLog(TAG,"This partner donot have any set");
									}
									else{
										
										String partner_id=obj1.getString("PartnerID");
										String categoryid=obj1.getString("SetID");
										String catagoryname=obj1.getString("SetName");
										String imagecount=obj1.getString("ImageCount");
										String imageset=obj1.getString("SetImage");
										String imagethumb=obj1.getString("ThumbImage");
										String Approvalflag=obj1.getString("Approvalflag");
										String LiveFlag=obj1.getString("LiveFlag");
										
										SetModel model=new SetModel(partner_id, categoryid, catagoryname, imagethumb, imagecount, Approvalflag, LiveFlag);
										setlist.add(model);
									}
								}
    							else{
    								
    								String partner_id=obj1.getString("PartnerID");
    								String categoryid=obj1.getString("SetID");
    								String catagoryname=obj1.getString("SetName");
    								String imagecount=obj1.getString("ImageCount");
    								String realsetthumb=obj1.getString("SetImage");
    								String imagethumb=obj1.getString("ThumbImage");
    								String Approvalflag=obj1.getString("Approvalflag");
    								String LiveFlag=obj1.getString("LiveFlag");
    								
    								
    								SetModel model=new SetModel(partner_id, categoryid, catagoryname, imagethumb, imagecount, Approvalflag, LiveFlag);
    								setlist.add(model);
    							}
    							
    						}
    					}
    					
    					
						PictureSetAdapter adapter=new PictureSetAdapter(getActivity(), setlist);
						gridview.setAdapter(adapter);
						
	        	     }	
	        	 catch (JSONException e) {
 					e.printStackTrace();
	        	 }
	        }
	        	 
	       //Log.v(TAG, "set list size is :"+setlist.size());
	       ImageUtil.galleryLog(TAG, "set list size is :"+setlist.size());
	       if (setlist.size() < 1) {
			
	    	   textviwe.setText("Not yet created any sets.");
	    	   textviwe.setVisibility(View.VISIBLE);
		    }
	       else{
	    	   textviwe.setVisibility(View.GONE);
	       }
	     }
	}
	 
	 @Override
	 public void onPauseFragment() {
	     Log.i(TAG, "onPauseFragment()");
	     Toast.makeText(getActivity(), "onPauseFragment():" + TAG, Toast.LENGTH_SHORT).show(); 
	 }

	 @Override
	 public void onResumeFragment() {
	     Log.i(TAG, "onResumeFragment()");
	     Toast.makeText(getActivity(), "onResumeFragment():" + TAG, Toast.LENGTH_SHORT).show(); 
	     
	
	 }
	 
	 @Override
	public void onResume() {
		
		super.onResume();
		
		 Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
         String addmorepic=Prefs.getString(ImageConstant.ADDMOREPIC, "");
         String updatecover=Prefs.getString(ImageConstant.UPDATECOVERPHOTO, "");
         if (addmorepic.equalsIgnoreCase("1") || updatecover.equalsIgnoreCase("1")) {
			
        	 boolean isinternet=ImageUtil.isInternetOn(getActivity());
				if (isinternet) {
					
					prodialog_myupload=ProgressDialog.show(getActivity(), "", "Loading..");
					/*MyUploadedUpdateTask task=new MyUploadedUpdateTask();
					task.execute();*/
					
					if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    		    new MyUploadedUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    		} else {
			    		    new MyUploadedUpdateTask().execute();
			    		}
				}
				else{
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
				}
		}
	}
	 
		@Override
		public void onClick(View v) {
		
			  ShowAlertNewCatagory();
			
		}
		
		
		 public void ShowAlertNewCatagory(){
				
			    LayoutInflater li = LayoutInflater.from(getActivity());
				View promptsView = li.inflate(R.layout.newcataogy, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
				editcatname = (EditText) promptsView.findViewById(R.id.editText_catname);
				
				// set dialog message
				alertDialogBuilder.setCancelable(false).setPositiveButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});
				
				alertDialogBuilder.setCancelable(false).setNegativeButton("Create Set",
						new DialogInterface.OnClickListener() {
						
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
				Button theButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				theButton.setOnClickListener(new CustomListenerMessage(alertDialog));
			  
	         }
		 
			class CustomListenerMessage implements View.OnClickListener {
				private final Dialog dialogmy;

				public CustomListenerMessage(Dialog dialog) {
					this.dialogmy = dialog;
				}

				@Override
				public void onClick(View v) {

					categoryname = editcatname.getText().toString().trim();
					if (categoryname.length() < 1) {
						
						Toast.makeText(getActivity(), "Set name is required.", Toast.LENGTH_LONG).show();
					} 
					
					else if (categoryname.length() > 18) {
						
						Toast.makeText(getActivity(), "Set name should not exceed 18 characters.", Toast.LENGTH_LONG).show();
					} 
					else {
						   
						boolean isinternet=ImageUtil.isInternetOn(getActivity());
						if (isinternet) {
							//do here what
						
								prodialog2=ProgressDialog.show(getActivity(), "", "Creating..");
								/*NewCategoryTask task=new NewCategoryTask();
								task.execute();*/
								
								if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
					    		    new NewCategoryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					    		} else {
					    		    new NewCategoryTask().execute();
					    		}
							}
							else{
								ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
							}
							
						dialogmy.dismiss();
						}
						
			    	}
		    	}
			
		
		private class NewCategoryTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
//				/http://picture-video-store.com/ws/?ws=1&act=Listofcategory&MemberID=1			
				//String url=ImageConstant.BASEURLMYUPLOAD;
				
				String url=ImageConstant.BASEURL+"Addcategory";
				
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("catname", categoryname);
					obj1.put("setthumbFile", "");
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			     // Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
			      
			      HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
			      
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				 //  Log.v(TAG, "url is: "+url);
				   ImageUtil.galleryLog(TAG, "url is: "+url);
				      
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				        String result = EntityUtils.toString(response.getEntity());   
				        //Log.v(TAG+".doInBackground", "Http response is:" + result);
				         //Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         
				         ImageUtil.galleryLog(TAG, "Http response is:" + result);
				         ImageUtil.galleryLog(TAG, "json response code:" + responsecode);
				         
				        if (responsecode==200) {
				        	
				        	response1=result;
				
				        }
				        
				        else{
				        	response1="";
				        }				       
			
			
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog2.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				  ImageUtil.galleryLog(TAG, "Response is: "+resultString);
				  
				try {
				if (resultString != null && !resultString.isEmpty()) {
					
					 
							JSONObject jsonobj=new JSONObject(resultString);
							String status=jsonobj.getString("Status");
							if (status.trim().equalsIgnoreCase("Category Added  Successfully")) {
								
								showAlertAdded(getActivity(), getActivity().getResources().getString(R.string.set_created));
								
							}
							else if (status.trim().equalsIgnoreCase("Category already Exist")) {
								ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.same_set));
							}
					
					} 
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					  }
				}
				catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
				}
			}
		}
		
		
		public void showAlertAdded(Activity activity, String message) {
		
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(message);
			
			builder.setCancelable(false);
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					
					boolean isinternet=ImageUtil.isInternetOn(getActivity());
					if (isinternet) {
						prodialog_myupload=ProgressDialog.show(getActivity(), "", "Loading..");
						/*MyUploadedUpdateTask task=new MyUploadedUpdateTask();
						task.execute();*/
						
						if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    		    new MyUploadedUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    		} else {
			    		    new MyUploadedUpdateTask().execute();
			    		}
					}
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
					}
					

				}

			});

			AlertDialog alert = builder.create();
			alert.show();
			TextView messageText = (TextView) alert.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		}
	
	private void showDialogItemClickLive() {
			
			try {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Choose");

				final CharSequence[] choiceList = { "View", "Add/Edit Category"};
				int selected = -1; 

				builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int itemNO) {

								if (itemNO == 0) {

						    		boolean isinternet=ImageUtil.isInternetOn(getActivity());
									if (isinternet) {
										
										prodialog_set=ProgressDialog.show(getActivity(), "", "Loading set..");
									/*	LoadSetTask task=new LoadSetTask();
										task.execute();*/
										
										if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
							    		    new LoadSetTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							    		} else {
							    		    new LoadSetTask().execute();
							    		}
									}
									else{
										ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
									}
									
									alert.dismiss();
								
								} else if (itemNO == 1) {
									
									setthumb=3;
									alert.dismiss();
									
									
									boolean isinternet=ImageUtil.isInternetOn(getActivity());
									if (isinternet) {
										
										//fwfwef
										// load the added the category for a set
										
										prodialog_cat=ProgressDialog.show(getActivity(), "", "Loading...");
										/*LoadCategoryTask task=new LoadCategoryTask();
										task.execute();
										*/

										if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
							    		    new LoadCategoryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							    		} else {
							    		    new LoadCategoryTask().execute();
							    		}
										
										/*prodialog_viewcategory=ProgressDialog.show(getActivity(), "", "Loading");
										ViewCategoryTaskSet task=new ViewCategoryTaskSet();
										task.execute();*/
										
									}
									else{
										ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
									}
									
									/*
									boolean isinternet=ImageUtil.isInternetOn(getActivity());
									if (isinternet) {
										
										// load the added the category for a set
										prodialog_viewcategory=ProgressDialog.show(getActivity(), "", "Loading");
										ViewCategoryTaskSet task=new ViewCategoryTaskSet();
										task.execute();
										
									}
									else{
										ImageUtil.showAlert(getActivity(), "Internet Connection Error");
									}
								*/
									alert.dismiss();

								}
							
								
							}
						}).setCancelable(false);
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});

				alert = builder.create();
				alert.show();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	 
		
		private void showDialogItemClick() {
			
			try {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Choose");

				final CharSequence[] choiceList = { "View", "Add more photos", "Select a cover photo for this set", "Add/Edit Category", "Delete", "Submit for Approval & Sell this Set"};
				int selected = -1; 

				builder.setSingleChoiceItems(choiceList, selected, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int itemNO) {

								if (itemNO == 0) {

						    		boolean isinternet=ImageUtil.isInternetOn(getActivity());
									if (isinternet) {
										
										prodialog_set=ProgressDialog.show(getActivity(), "", "Loading set..");
										/*LoadSetTask task=new LoadSetTask();
										task.execute();*/
										
										if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
							    		    new LoadSetTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							    		} else {
							    		    new LoadSetTask().execute();
							    		}
									}
									else{
										ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
									}
									alert.dismiss();
								
								} else if (itemNO == 1) {
									
									setthumb=1;
									
									List<RequestedSetModel> list=null;
									try {
										list = db.getRequestsetId(categoryid);
									} catch (SQLException e) {
										e.printStackTrace();
									}
									
										
								
									if (liveflag.trim().equalsIgnoreCase("1")) {
										
										ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.set_already_live));
									}
									
									else if (noofimages.trim().equalsIgnoreCase("100")) {
										
										ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.set_already_live));
									}
									else if (list.size() > 0) {
										
										ImageUtil.showAlert(getActivity(),  getActivity().getResources().getString(R.string.already_req_forapproval));
									}
									else {
										
										// Here you can load the myupload files
										
										boolean isinternet=ImageUtil.isInternetOn(getActivity());
										if (isinternet) {
											
											prodialog_myupload=ProgressDialog.show(getActivity(), "", "Loading...");
											/*MyUploadedTask task=new MyUploadedTask();
											task.execute();*/
											
											if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
								    		    new MyUploadedTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
								    		} else {
								    		    new MyUploadedTask().execute();
								    		}
										}
										else{
											ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
										}
										
									}
									
									
									alert.dismiss();

								}
								else if (itemNO == 2) {
									
									// add edit category
									

									List<RequestedSetModel> list=null;
									try {
										list = db.getRequestsetId(categoryid);
									} catch (SQLException e) {
										e.printStackTrace();
									}
									if (list.size() > 0) {
										
										ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.already_req_forapproval));
									}
									else{
										
										boolean isinternet=ImageUtil.isInternetOn(getActivity());
										if (isinternet) {
											/*prodialog_myupload=ProgressDialog.show(getActivity(), "", "Loading...");
											MyUploadedTask task=new MyUploadedTask();
											task.execute();*/
											
											prodialog_set=ProgressDialog.show(getActivity(), "", "Loading set..");
											/*LoadPictureSetTask task=new LoadPictureSetTask();
											task.execute();
											*/

											if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
								    		    new LoadPictureSetTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
								    		} else {
								    		    new LoadPictureSetTask().execute();
								    		}
										}
										else{
											ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
										}
									}
									
									setthumb=2;
									alert.dismiss();
								
									

								}
								else if (itemNO == 3) {
									
									setthumb=3;
									alert.dismiss();
									
									boolean isinternet=ImageUtil.isInternetOn(getActivity());
									if (isinternet) {
										
										//fwfwef
										// load the added the category for a set
										
										prodialog_cat=ProgressDialog.show(getActivity(), "", "Loading...");
										/*LoadCategoryTask task=new LoadCategoryTask();
										task.execute();
										*/
										if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
							    		    new LoadCategoryTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							    		} else {
							    		    new LoadCategoryTask().execute();
							    		}
										
										/*prodialog_viewcategory=ProgressDialog.show(getActivity(), "", "Loading");
										ViewCategoryTaskSet task=new ViewCategoryTaskSet();
										task.execute();*/
										
									}
									else{
										ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
									}
								
								}
								
								else if (itemNO == 4) {
									
									setthumb=4;
									alert.dismiss();
									
									List<RequestedSetModel> list=null;
									try {
										list = db.getRequestsetId(categoryid);
									} catch (SQLException e) {
										e.printStackTrace();
									}
									
									
									if (liveflag.equalsIgnoreCase("1")) {
											
							    		 ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.set_already_live));
									}
									else if (list.size() > 0) {
										ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.already_req_forapproval));
									}
										
									else{
										
										
										showAlertDeleteSet(getActivity(), "Are you sure you want to delete set?");
										
									}
									
								}
								
								else if (itemNO == 5) {
									
									setthumb=5;
									alert.dismiss();
									
									List<RequestedSetModel> list=null;
									try {
										list = db.getRequestsetId(categoryid);
									} catch (SQLException e) {
										e.printStackTrace();
									}
									
									
									if (liveflag.equalsIgnoreCase("1")) {
											
							    		 ImageUtil.showAlert(getActivity(),  getActivity().getResources().getString(R.string.set_already_live));
									}else if (list.size() > 0) {
										ImageUtil.showAlert(getActivity(),  getActivity().getResources().getString(R.string.already_req_forapproval));
									}
									
									else{
										
										// here you can make a request for approval to do.
										boolean isinternet=ImageUtil.isInternetOn(getActivity());
										if (isinternet) {
											
											prodialog_sendrequest=ProgressDialog.show(getActivity(), "", "Sending approval request...");
											/*ArrovalRequestTask task= new ArrovalRequestTask();
											task.execute();*/
											
											if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
								    		    new ArrovalRequestTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
								    		} else {
								    		    new ArrovalRequestTask().execute();
								    		}
											
										}else{
											ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
										}
										
										
										//showAlertDeleteSet(getActivity(), "Are you sure you want to delete set?");
										
									}
									
								}
								
							}
						}).setCancelable(false);
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});

				alert = builder.create();
				alert.show();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	 
		
		private class ViewCategoryTaskSet extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
				
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				//http://picture-video-store.com/ws/?ws=1&act=getIV&MemberID=1
				
				//String url=ImageConstant.BASEURLMYUPLOAD;
				//String url="http://picture-video-store.com/ws/?";
				
				String url=ImageConstant.BASEURL+"FetchCategoryDetails";
				
				
                JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("PartnerID", memberid);
					obj1.put("setID", categoryid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      //Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
			      

				  HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
								
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				   //Log.v(TAG, "url is: "+url);
				     ImageUtil.galleryLog(TAG,  "url is: "+url);
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				    	String result = EntityUtils.toString(response.getEntity());  
				    	//Log.v(TAG+".doInBackground", "Http response is:" + result);
				         //Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         ImageUtil.galleryLog(TAG,  "Http response is:" + result);
				         ImageUtil.galleryLog(TAG,  "json response code:" + responsecode);
				        if (responsecode==200) {
				        	
				        	response1 = result;  
				        	
				        }
				        
				        else{
				        	response1="";
				        }				       
			
			
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				 e.printStackTrace();
				Log.v(TAG, "Connection time out");
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
				   
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog_viewcategory.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				ImageUtil.galleryLog(TAG,  "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Success")) {
							
							String setid=jsonobj.getString("SetID");
							 catid1=jsonobj.getString("CatID1");
							 catid2=jsonobj.getString("CatID2");
							 catid3=jsonobj.getString("CatID3");
							
						}
						else if (status.trim().equalsIgnoreCase("This SetID Not yet add Any Category")) {
							
							String setid=jsonobj.getString("SetID");
							 catid1="0";
							 catid2="0";
							 catid3="0";
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					list=db.GetCategoryData();
			    	CategoryAdapter adapter=new CategoryAdapter(getActivity(), list, catid1, catid2, catid3);
	                ShowListAlertCategory(adapter);
				}
				else{
					
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
				}

			}
		}
		
		private class MyUploadedTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
				
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				//http://picture-video-store.com/ws/?ws=1&act=getIV&MemberID=1
				
				//String url=ImageConstant.BASEURLMYUPLOAD;
				//String url="http://picture-video-store.com/ws/?";
				
				String url=ImageConstant.BASEURL+"getIVUpdate";
				
				
                JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			     // Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
			      

				  HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
								
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				   //Log.v(TAG, "url is: "+url);
				   ImageUtil.galleryLog(TAG, "url is: "+url);
				      
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				    	String result = EntityUtils.toString(response.getEntity());  
				    	//Log.v(TAG+".doInBackground", "Http response is:" + result);
				         //Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         
				         ImageUtil.galleryLog(TAG,  "Http response is:" + result);
				         ImageUtil.galleryLog(TAG, "json response code:" + responsecode);
				         
				        if (responsecode==200) {
				        	
				        	response1 = result;  
				        	
				        }
				        
				        else{
				        	response1="";
				        }				       
			
			
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				 e.printStackTrace();
				Log.v(TAG, "Connection time out");
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
				   
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog_myupload.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				  ImageUtil.galleryLog(TAG, "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					  Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
					  SharedPreferences.Editor editor = Prefs.edit();
					  editor.putString(ImageConstant.MYUPLOAD, resultString);
					  editor.commit();
					  
					  if (setthumb==1) {
						
						  Intent intent=new Intent(getActivity(), SeletctMultipleFile.class);
							intent.putExtra("JSONAPI", resultString);
							intent.putExtra("setid", categoryid);
							startActivity(intent);
					
				    	}
					  else if (setthumb==2) {
						
						    Intent intent=new Intent(getActivity(), SelectCoverPhotoActivity.class);
							intent.putExtra("JSONAPI", resultString);
							intent.putExtra("setid", categoryid);
							startActivity(intent);
					}
						
				}
				else{
					
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
				}

			}
		}

		public void showAlertDeleteSet(Activity activity, String message) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(message);
			
			builder.setCancelable(false);
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
	               // do here you want
					
					//here you can send the alert to admin for delete the video
					boolean isinternet=ImageUtil.isInternetOn(getActivity());
					if (isinternet) {
						
						
						
						prodialog=ProgressDialog.show(getActivity(), "", "Deleting...");
						
						if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			    		    new DeleteSetTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    		} else {
			    		    new DeleteSetTask().execute();
			    		}
						/*DeleteSetTask task=new DeleteSetTask();
						task.execute();*/
					}
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
					}
					
				}

			});
			builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
				@TargetApi(Build.VERSION_CODES.GINGERBREAD)
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				   
				}

			});

			AlertDialog alert = builder.create();
			alert.show();
			TextView messageText = (TextView) alert.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		}
		
		
		private class AddEditCategory extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				
				String url=ImageConstant.BASEURL+"InsertCategoryDetails";
			
		
	                JSONObject obj1=new JSONObject();
					
					try {
						obj1.put("PartnerID", memberid);
						obj1.put("setID", categoryid);
						obj1.put("uploadID", "");
						
						  if ( checked_category.size() == 0) {
								
							    obj1.put("catID1", "0");
								obj1.put("catID2", "0");
								obj1.put("catID3", "0");
							}
						    else if (checked_category.size() == 1) {
								
						    	for (int i = 1; i <= checked_category.size(); i++) {
						    		CategoryModel model=checked_category.get(i-1);
						    		String catid=model.getCat_id();
						    		obj1.put("catID"+i, catid);
								}
						    	obj1.put("catID2", "0");
						    	obj1.put("catID3", "0");
							}
						    else if (checked_category.size() == 2) {
								
						    	for (int i = 1; i <= checked_category.size(); i++) {
						    		CategoryModel model=checked_category.get(i-1);
						    		String catid=model.getCat_id();
						    		obj1.put("catID"+i, catid);
								}
						    	obj1.put("catID3", "0");
							}
						    else if (checked_category.size() == 3) {
								
						    	for (int i = 1; i <= checked_category.size(); i++) {
						    		CategoryModel model=checked_category.get(i-1);
						    		String catid=model.getCat_id();
						    		Log.v(TAG, "Cat id"+i+" "+catid);
						    		obj1.put("catID"+i, catid);
								}
						    	 
							}
						   
						
					
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
				      //Log.v(TAG, "Json object is: "+obj1.toString());
				      ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());

				  HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
					   
				  HttpClient httpclient = new DefaultHttpClient(httpParameters);
					   //Log.v(TAG, "url is: "+url);
					   ImageUtil.galleryLog(TAG, "url is: "+url);
					   HttpPost  httppost = new HttpPost(url);
					 
					   try {

						   StringEntity se = new StringEntity(obj1.toString());
						   httppost.setEntity(se);  
						   httppost.setHeader("Accept", "application/json");
						   httppost.setHeader("Content-type", "application/json");

					        HttpResponse response = httpclient.execute(httppost);
					        int responsecode=response.getStatusLine().getStatusCode();
					        String result = EntityUtils.toString(response.getEntity());   
					        //Log.v(TAG+".doInBackground", "Http response is:" + result);
					        // Log.v(TAG+".doInBackground", "json response code:" + responsecode);
					         
					         ImageUtil.galleryLog(TAG, "Http response is:" + result);
					         ImageUtil.galleryLog(TAG,  "json response code:" + responsecode);
					         
					        if (responsecode==200) {
					        	
					        	response1=result;
					        }
					        
					        else{
					        	response1="";
					        }				       
				
				
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog_addedit.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				 ImageUtil.galleryLog(TAG,  "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("Inserted Successfully") || status.trim().equalsIgnoreCase("Success")) {
							
							ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.added_cat));
						}
						
						
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
				
				}
				else{
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
				}
			}
		}
		
		private class DeleteSetTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				
				String url=ImageConstant.BASEURL+"DeleteSetName";
			
		
	                JSONObject obj1=new JSONObject();
					
					try {
						obj1.put("MemberID", memberid);
						obj1.put("set_id", categoryid);
						
						
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
				     // Log.v(TAG, "Json object is: "+obj1.toString());
				      ImageUtil.galleryLog(TAG,  "Json object is: "+obj1.toString());
				      
				  HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
					   
				  HttpClient httpclient = new DefaultHttpClient(httpParameters);
					 //  Log.v(TAG, "url is: "+url);
					   ImageUtil.galleryLog(TAG, "url is: "+url);
					   HttpPost  httppost = new HttpPost(url);
					 
					   try {

						   StringEntity se = new StringEntity(obj1.toString());
						   httppost.setEntity(se);  
						   httppost.setHeader("Accept", "application/json");
						   httppost.setHeader("Content-type", "application/json");

					        HttpResponse response = httpclient.execute(httppost);
					        int responsecode=response.getStatusLine().getStatusCode();
					        String result = EntityUtils.toString(response.getEntity());   
					        //Log.v(TAG+".doInBackground", "Http response is:" + result);
					         //Log.v(TAG+".doInBackground", "json response code:" + responsecode);
					         
					         ImageUtil.galleryLog(TAG, "Http response is:" + result);
					         ImageUtil.galleryLog(TAG, "json response code:" + responsecode);
					         
					        if (responsecode==200) {
					        	
					        	response1=result;
					        }
					        
					        else{
					        	response1="";
					        }				       
				
				
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				 ImageUtil.galleryLog(TAG, "Response is: "+resultString);
				if (resultString != null && !resultString.isEmpty()) {
					
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("set was deleted successfully")) {
						
							showAlert(getActivity(), getActivity().getResources().getString(R.string.set_deleted));
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
				
				}
				else{
					ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
				}
			}
		}
		
		public void showAlert(Activity activity, String message) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(activity); 
			builder.setMessage(message);
			
			builder.setCancelable(false);
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();


					boolean isinternet=ImageUtil.isInternetOn(getActivity());
					if (isinternet) {
						prodialog_myupload=ProgressDialog.show(getActivity(), "", "Loading..");
						MyUploadedUpdateTask task=new MyUploadedUpdateTask();
						task.execute();
					}
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
					}
					
				}

			});

			AlertDialog alert = builder.create();
			alert.show();
			TextView messageText = (TextView) alert.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		}
		
		
		private class ArrovalRequestTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				String url=ImageConstant.BASEURL+"SetApprovedRequest";
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("partnerId", memberid);
					obj1.put("setId", categoryid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			      //Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG,  "Json object is: "+obj1.toString());
				
			      
			      HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
			      
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				   //Log.v(TAG, "url is: "+url);
				   ImageUtil.galleryLog(TAG,  "url is: "+url);
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				        String result = EntityUtils.toString(response.getEntity());   
				      //  Log.v(TAG+".doInBackground", "Http response is:" + result);
				        // Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         
				         ImageUtil.galleryLog(TAG, "Http response is:" + result);
				         ImageUtil.galleryLog(TAG,  "json response code:" + responsecode);
				         
				        if (responsecode==200) {
				      
				        	response1=result;
				
				        }
				        
				        else{
				        	response1="";
				        }				       
			
		
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog_sendrequest.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				ImageUtil.galleryLog(TAG, "Response is: "+resultString);
				
				if (resultString != null && !resultString.isEmpty()) {
					
					try {
						JSONObject jsonobj=new JSONObject(resultString);
						String status=jsonobj.getString("Status");
						if (status.trim().equalsIgnoreCase("request send successfully")) {
							
							String setid=jsonobj.getString("SetId");
							
							List<RequestedSetModel> list=db.getRequestsetId(setid);
							if (list.size() > 0) {
								String setidstored="";
								String checkesstatus="";
								for (int i = 0; i < list.size(); i++) {
									
									RequestedSetModel model=list.get(i);
									 setidstored=model.getSetid();
									 checkesstatus=model.getCheckstatus();
								}
								
								if (checkesstatus.trim().equalsIgnoreCase("checked")) {
									
									ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.not_edit_set));
								}
								else{
									
									ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.staff_review));
								}
								
							}else{
								
								attentionBox("Request sent successfully. Our staff will review your content before it appears in the store.", setid);
							}
							
							
							
							//ImageUtil.showAlert(getActivity(), "Request sent successfully. Our staff will review your content before it appears in the store.");
						}
						else if (status.trim().equalsIgnoreCase("already requested")) {
							
							String setid=jsonobj.getString("SetId");
							List<RequestedSetModel> list=db.getRequestsetId(setid);
							if (list.size() > 0) {
								String setidstored="";
								String checkesstatus="";
								for (int i = 0; i < list.size(); i++) {
									
									RequestedSetModel model=list.get(i);
									 setidstored=model.getSetid();
									 checkesstatus=model.getCheckstatus();
								}
								
								if (checkesstatus.trim().equalsIgnoreCase("checked")) {
									
									ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.not_edit_set));
								}
								else{
									
									ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.staff_review));
								}
								
							}else{
								
								attentionBox("You already Requested for approval. Our staff will review your content before it appears in the store.", setid);
							}
							
							
							//ImageUtil.showAlert(getActivity(), "Your request still in pending. Our staff will review your content before it appears in the store.");
						}
						else if (status.trim().equalsIgnoreCase("approved by admin")) {
							String setid=jsonobj.getString("SetId");
							List<RequestedSetModel> list=db.getRequestsetId(setid);
							if (list.size() > 0) {
								String setidstored="";
								String checkesstatus="";
								for (int i = 0; i < list.size(); i++) {
									
									RequestedSetModel model=list.get(i);
									 setidstored=model.getSetid();
									 checkesstatus=model.getCheckstatus();
								}
								
								if (checkesstatus.trim().equalsIgnoreCase("checked")) {
									
									ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.not_edit_set));
								}
								else{
									
									ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.staff_review));
								}
								
							}else{
								
								attentionBox("Set has been approved by Admin", setid);
							}
							
							//ImageUtil.showAlert(getActivity(), "Set has been approved by Admin.");
						}
						
						} catch (JSONException e) {
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
							
					} 
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					  }
				
			}
		}
		
		  public void attentionBox(String message, final String setid){
				
				AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
			    LayoutInflater adbInflater = LayoutInflater.from(getActivity());
			    View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
			    final CheckBox dontShowAgain = (CheckBox)eulaLayout.findViewById(R.id.skip);
			    adb.setView(eulaLayout);
			    adb.setTitle("Attention");
			    adb.setMessage(message);
		    	adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
		    	      public void onClick(DialogInterface dialog, int which) {
		    	 
		    	    	  if (dontShowAgain.isChecked())  {
		    	    
		    	    		 Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
							 SharedPreferences.Editor editor = Prefs.edit();
							 editor.putString(ImageConstant.REQUESTCHECHBOX, "checked");
							// editor.putString(ImageConstant.REQUESTEDSETID, setid);
							 editor.commit();	
							 
							 RequestedSetModel setmodel=new RequestedSetModel(setid, "checked");
							 db.addRequestSet(setmodel);
							 
		    	    	  }
		    	    	  else{
		    	    		  
		     	    		 Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
		 					 SharedPreferences.Editor editor = Prefs.edit();
		 					 editor.putString(ImageConstant.REQUESTCHECHBOX, "not checked");
		 					// editor.putString(ImageConstant.REQUESTEDSETID, setid);
		 					 editor.commit();	
		 					 
		 					 RequestedSetModel setmodel=new RequestedSetModel(setid, "unchecked");
							 db.addRequestSet(setmodel);
		    	    	  }
		    	  			
		    	      } });
		 
		    	  adb.show();
			}
		  
		
		
		private class LoadSetTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				String url=ImageConstant.BASEURL+"getsetImages";
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("cat_id", categoryid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			    //  Log.v(TAG, "Json object is: "+obj1.toString());
			  	ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
			      
			      HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
			      
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				//   Log.v(TAG, "url is: "+url);
				   ImageUtil.galleryLog(TAG, "url is: "+url);
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				        String result = EntityUtils.toString(response.getEntity());   
				        //Log.v(TAG+".doInBackground", "Http response is:" + result);
				         //Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         
				         ImageUtil.galleryLog(TAG,  "Http response is:" + result);
				         ImageUtil.galleryLog(TAG, "json response code:" + responsecode);
				         
				        if (responsecode==200) {
				      
				        	response1=result;
				
				        }
				        
				        else{
				        	response1="";
				        }				       
			
		
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog_set.dismiss();
				Log.v(TAG, "onPostExecute called");
			//	Log.v(TAG, "Response is: "+resultString);
				 ImageUtil.galleryLog(TAG, "Response is: "+resultString);
				
				if (resultString != null && !resultString.isEmpty()) {
					
					
					Intent intent=new Intent(getActivity(), LoadSetActivity.class);
					intent.putExtra("JSONAPI", resultString);
					intent.putExtra("categoryname", categoryname);
					intent.putExtra("approveflag", liveflag);
					startActivity(intent);
					 
					} 
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					  }
				
			}
		}
		
		private class LoadPictureSetTask extends AsyncTask<String, Void, String> {
			String response1 = "";
			
			@Override
			protected String doInBackground(String... urls) {
			
				Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
				String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
				String url=ImageConstant.BASEURL+"getsetImages";
				
	            JSONObject obj1=new JSONObject();
				
				try {
					obj1.put("MemberID", memberid);
					obj1.put("cat_id", categoryid);
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
			     // Log.v(TAG, "Json object is: "+obj1.toString());
			      ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
				
			      
			      HttpParams httpParameters = new BasicHttpParams();
				  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
				  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
			      
				   HttpClient httpclient = new DefaultHttpClient(httpParameters);
				  // Log.v(TAG, "url is: "+url);
				   ImageUtil.galleryLog(TAG, "url is: "+url);
				   HttpPost  httppost = new HttpPost(url);
				 
				   try {

					   StringEntity se = new StringEntity(obj1.toString());
					   httppost.setEntity(se);  
					   httppost.setHeader("Accept", "application/json");
					   httppost.setHeader("Content-type", "application/json");

				        HttpResponse response = httpclient.execute(httppost);
				        int responsecode=response.getStatusLine().getStatusCode();
				        String result = EntityUtils.toString(response.getEntity());   
				       // Log.v(TAG+".doInBackground", "Http response is:" + result);
				       //  Log.v(TAG+".doInBackground", "json response code:" + responsecode);
				         
				         ImageUtil.galleryLog(TAG, "Http response is:" + result);
				         ImageUtil.galleryLog(TAG, "json response code:" + responsecode);
				         
				        if (responsecode==200) {
				      
				        	response1=result;
				
				        }
				        
				        else{
				        	response1="";
				        }				       
			
		
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}catch (ConnectTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			 return response1;
		}

			@Override
			protected void onPostExecute(String resultString) {
				
				prodialog_set.dismiss();
				Log.v(TAG, "onPostExecute called");
				//Log.v(TAG, "Response is: "+resultString);
				ImageUtil.galleryLog(TAG,  "Response is: "+resultString);
				
				if (resultString != null && !resultString.isEmpty()) {
					
					
					Intent intent=new Intent(getActivity(), SelectCoverPhotoActivity.class);
					intent.putExtra("JSONAPI", resultString);
					intent.putExtra("JSONAPI", resultString);
					intent.putExtra("setid", categoryid);
					startActivity(intent);
					 
							
					} 
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					  }
				
			}
		}
		
		 public void ShowListAlertCategory(final CategoryAdapter adapter){
				
			    AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
			    newadapter=adapter;
			    myDialog.setTitle("Select Category List");
		        final ListView listview=new ListView(getActivity());
		        listview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		      
		        LinearLayout layout = new LinearLayout(getActivity());
		        layout.setOrientation(LinearLayout.VERTICAL);		
		        layout.addView(listview);
		        myDialog.setView(layout);
		        listview.setAdapter(adapter);
		        
		      /*  listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						
						// add the images inside 
						// CategoryModel model=list.get(arg2);
						 //get the selected category
						
						 
							
				    }
		        
		        });*/
		        myDialog.setCancelable(false);
		        myDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
		 
		                        @Override
		                        public void onClick(DialogInterface dialog, int which) {
		                          
		                        	
		                          
		     					
		                        }
		                    });
		        myDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
		       	 
	             @Override
	             public void onClick(DialogInterface dialog, int which) {
	            	 //cat_dialog.dismiss();
	                 
	             }
	         });
		
		     AlertDialog alertDialog= myDialog.create();
		     alertDialog.show();
		         
		     Button theButton = alertDialog
						.getButton(DialogInterface.BUTTON_NEGATIVE);
				theButton.setOnClickListener(new CustomListener(alertDialog));
		 }
		 
		 
		 class CustomListener implements View.OnClickListener {
				private final Dialog dialog;

				public CustomListener(Dialog dialog) {
					this.dialog = dialog;
				}

				@Override
				public void onClick(View v) {

				
					  if(newadapter!=null){
							
						   checked_category=newadapter.getChecked();
						  // Toast.makeText(getApplicationContext(), "Checked size is: "+checked_category.size(), Toast.LENGTH_LONG).show();			   
					   }
                       
                       if (checked_category.size() > 0) {
							
                    	   dialog.dismiss();
                       	
                       	 boolean isinternet=ImageUtil.isInternetOn(getActivity());
	     						if (isinternet) {
	     							
	     							prodialog_addedit=ProgressDialog.show(getActivity(), "", "Loading..");
	     							
	     							if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	     				    		    new AddEditCategory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	     				    		} else {
	     				    		    new AddEditCategory().execute();
	     				    		}
	     							
	     							/*AddEditCategory task=new AddEditCategory();
	     							task.execute();*/
	     						}
	     						else{
	     							ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
	     						}
						}
                       else{
                       	
                       	//cat_dialog.show();
                       	//ImageUtil.showAlert(getActivity(), "Please select atleast one category.");
                       	Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.check_atleast_onecat), Toast.LENGTH_LONG).show();
                       }
					 
				}
			}
		 
		 private class MyUploadedUpdateTask extends AsyncTask<String, Void, String> {
				String response1 = "";
				
				@Override
				protected String doInBackground(String... urls) {
					
					Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
					String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
				
					//String url="http://picture-video-store.com/ws/?";
					
					String url=ImageConstant.BASEURL+"getIVUpdate";
					
					
	                JSONObject obj1=new JSONObject();
					
					try {
						obj1.put("MemberID", memberid);
						
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
				     // Log.v(TAG, "Json object is: "+obj1.toString());
				  	ImageUtil.galleryLog(TAG,  "Json object is: "+obj1.toString());
				      
				      HttpParams httpParameters = new BasicHttpParams();
					  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
					  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
					
					   HttpClient httpclient = new DefaultHttpClient(httpParameters);
					   //Log.v(TAG, "url is: "+url);
					   ImageUtil.galleryLog(TAG,  "url is: "+url);
					   HttpPost  httppost = new HttpPost(url);
					 
					   try {

						   StringEntity se = new StringEntity(obj1.toString());
						   httppost.setEntity(se);  
						   httppost.setHeader("Accept", "application/json");
						   httppost.setHeader("Content-type", "application/json");

					        HttpResponse response = httpclient.execute(httppost);
					        int responsecode=response.getStatusLine().getStatusCode();
					    	String result = EntityUtils.toString(response.getEntity());  
					    	//Log.v(TAG+".doInBackground", "Http response is:" + result);
					      //   Log.v(TAG+".doInBackground", "json response code:" + responsecode);
					         
					         ImageUtil.galleryLog(TAG,  "Http response is:" + result);
					         ImageUtil.galleryLog(TAG,  "json response code:" + responsecode);
					         
					        if (responsecode==200) {
					        	
					        	response1 = result;  
					        	
					        }
					        
					        else{
					        	response1="";
					        }				       
				
					
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				}catch (ConnectTimeoutException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
					   
				 return response1;
			}

				@Override
				protected void onPostExecute(String resultString) {
					
					prodialog_myupload.dismiss();
					Log.v(TAG, "onPostExecute called");
					//Log.v(TAG, "Response is: "+resultString);
					  ImageUtil.galleryLog(TAG,   "Response is: "+resultString);
					
					if (resultString != null && !resultString.isEmpty()) {
						
						Intent intent=new Intent(getActivity(), MyUploadActivity.class);
						intent.putExtra("JSONAPI", resultString);
						startActivity(intent);
						
						 Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
						 SharedPreferences.Editor editor = Prefs.edit();
						 editor.putString(ImageConstant.UPDATESET, "1");
						 editor.putString(ImageConstant.ADDMOREPIC, "0");
						 editor.putString(ImageConstant.UPDATECOVERPHOTO, "0");
						 editor.commit();	
						 
						
						 getActivity().finish();
					}
					else{
						ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.timeout));
					}

				}
			}
		 
		 
		  private class LoadCategoryTask extends AsyncTask<String, Void, String> {
				
		    	String response1 = "";
				
				@Override
				protected String doInBackground(String... urls) {
				
					Prefs = getActivity().getSharedPreferences(prefname, Context.MODE_PRIVATE);
					String memberid=Prefs.getString(ImageConstant.MEMBERID, "");
					
					//String url=GalleryConstant.BASEURL;
					
					String url=ImageConstant.BASEURL+"getCategoryList";
						
					JSONObject obj1=new JSONObject();
					
					try {
						obj1.put("MemberID", memberid);
						
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
				       //Log.v(TAG, "Json object is: "+obj1.toString());
				       ImageUtil.galleryLog(TAG, "Json object is: "+obj1.toString());
				       
				 	  HttpParams httpParameters = new BasicHttpParams();
					  HttpConnectionParams.setConnectionTimeout(httpParameters, ImageConstant.timeoutConnection);
					  HttpConnectionParams.setSoTimeout(httpParameters, ImageConstant.timeoutSocket);
									
					
					   HttpClient httpclient = new DefaultHttpClient(httpParameters);
					 //  Log.v(TAG, "url is: "+url);
					   ImageUtil.galleryLog(TAG, "url is: "+url);
					   HttpPost  httppost = new HttpPost(url);
					 
					   try {

						   StringEntity se = new StringEntity(obj1.toString());
						   httppost.setEntity(se);  
						   httppost.setHeader("Accept", "application/json");
						   httppost.setHeader("Content-type", "application/json");

					        HttpResponse response = httpclient.execute(httppost);
					        int responsecode=response.getStatusLine().getStatusCode();
					        String result = EntityUtils.toString(response.getEntity());   
					       /// Log.v(TAG+".doInBackground", "Http response is:" + result);
					       // Log.v(TAG+".doInBackground", "json response code:" + responsecode);
					        
					        ImageUtil.galleryLog(TAG, "Http response is:" + result);
					        ImageUtil.galleryLog(TAG, "json response code:" + responsecode);
					        
					        if (responsecode==200) {
					      
					        	response1=result;
					
					        }
					        
					        else{
					        	
					        	response1="";
					        }				       
					
					
					
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (ConnectTimeoutException e) {
					e.printStackTrace();
					Log.v(TAG, "Connection time out");
				}
				 catch (IOException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
				 return response1;
			}

				@Override
				protected void onPostExecute(String resultString) {
					
				
					prodialog_cat.dismiss();
					Log.v(TAG, "onPostExecute called");
					//Log.v(TAG, "Response is: "+resultString);
					 ImageUtil.galleryLog(TAG, "Response is: "+resultString);
					if (resultString != null  && !resultString.isEmpty()) {
						db.DeleteCategory();
	                  try {
							JSONArray jsonarray=new JSONArray(resultString);
							for (int i = 0; i < jsonarray.length(); i++) {
								
								JSONObject jsonobj=jsonarray.getJSONObject(i);
								String cat_id=jsonobj.getString("CategoryID");
								String cat_name=jsonobj.getString("CategoryName");
								CategoryModel model=new CategoryModel(cat_id, cat_name, false);
								//list_category.add(model);
								List<CategoryModel> list=db.GetCategoryData();
								db.addCategory(model);
						
							}
							//Toast.makeText(context, "Refresh Complete.", Toast.LENGTH_LONG).show();
							
						} catch (JSONException e) {
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
					
	                  boolean isinternet=ImageUtil.isInternetOn(getActivity());
	                  if (isinternet) {
	                	   
	                	  prodialog_viewcategory=ProgressDialog.show(getActivity(), "", "Loading..");
							
	                	  if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
				    		    new ViewCategoryTaskSet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				    		} else {
				    		    new ViewCategoryTaskSet().execute();
				    		}
	                	  /*ViewCategoryTaskSet task=new ViewCategoryTaskSet();
							task.execute();*/
							
				 	}
	                  else{
							ImageUtil.showAlert(getActivity(), getActivity().getResources().getString(R.string.internet_error));
						}
	                
					}
					else{
						ImageUtil.showAlert(getActivity(), getResources().getString(R.string.timeout));
					}

				}
			}
		 
		 
}
