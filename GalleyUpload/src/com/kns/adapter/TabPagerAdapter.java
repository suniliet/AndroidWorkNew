package com.kns.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

import com.kns.fragment.MyPictureFragment;
import com.kns.fragment.MyVideoFragment;
import com.kns.fragment.PistureSetsFragment;
public class TabPagerAdapter extends FragmentStatePagerAdapter  {
	
	 private static SharedPreferences Prefs = null;
	 private static String prefname = "galleryPrefs";
	Activity act;
	long baseId = 0;
    public TabPagerAdapter(FragmentManager fm, Activity activity) {
    super(fm);
    act=activity;
   // private TextProvider mProvider;
  
    // TODO Auto-generated constructor stub
  }
  @Override
  public Fragment getItem(int i) {
	//  Toast.makeText(act, "Tab is: "+i, Toast.LENGTH_LONG).show();
	  //Toast.makeText(act, "baseId is: "+baseId, Toast.LENGTH_LONG).show();
    switch (i) {
        case 0:
        /*	 
        	 Prefs = act.getSharedPreferences(prefname, Context.MODE_PRIVATE);
 	         String videostatus=Prefs.getString(ImageConstant.UPDATEVIDEO, "");
 	         if (videostatus.equalsIgnoreCase("") || videostatus.equalsIgnoreCase("0")) {
 	        	
 	        	 return new MyPictureFragment();
			}
 	         else{
 	        	  return new MyVideoFragment();
 	         }*/
            //Fragement for Picture Tab
            return new MyPictureFragment();
            
        case 1:
           //Fragment for video Tab
        	
            return new MyVideoFragment();
            
        case 2:
            //Fragment for video Tab
        	 //  Toast.makeText(act, "Picture sets require a minimum of 10 pictures to go live in the store.", Toast.LENGTH_LONG).show();
             return new PistureSetsFragment();
        default:
        	return null;
       
        }
   // return null;
    
  }
  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return 3; //No of Tabs
  }
  
  /*@Override
  public long getItemId(int position) {
      // give an ID different from position when position has been changed
      return baseId + position;
  }*/

  @Override
  public int getItemPosition(Object object){
      return TabPagerAdapter.POSITION_NONE;
  }
  
  public void notifyChangeInPosition(int n) {
      // shift the ID returned by getItemId outside the range of all previous fragments
      baseId += getCount() + n;
  }
 }