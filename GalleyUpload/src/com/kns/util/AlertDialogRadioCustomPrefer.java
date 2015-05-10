package com.kns.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class AlertDialogRadioCustomPrefer extends DialogFragment{

public String[] mypreferncepref = new String[]{"Enable","Disable"};
    
    AlertPositiveListenerCustom alertPositiveListener;
    public interface AlertPositiveListenerCustom {
        public void onPositiveClick(int position);
    }
 
    /** This is a callback method executed when this fragment is attached to an activity.
    *  This function ensures that, the hosting activity implements the interface AlertPositiveListener
    * */
    @SuppressLint("NewApi")
	public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try{
            alertPositiveListener = (AlertPositiveListenerCustom) activity;
        }catch(ClassCastException e){
            // The hosting activity does not implemented the interface AlertPositiveListener
            throw new ClassCastException(activity.toString() + " must implement AlertPositiveListener");
        }
    }
 
    OnClickListener positiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog)dialog;
            int position = alert.getListView().getCheckedItemPosition();
            alertPositiveListener.onPositiveClick(position);
        }
    };
 
    /** This is a callback method which will be executed
     *  on creating this fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	TextView title = new TextView(getActivity());
		title.setText("Would you like to receive requests for custom content from customers? You can increase your revenue by creating custom pictures and videos.");
		title.setPadding(5, 10, 5, 10);
		title.setGravity(Gravity.CENTER);
		//title.setTextColor(Color.WHITE);
		title.setTextSize(18);
 
        /** Getting the arguments passed to this fragment */
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        //b.setTitle("My Preference");
        b.setCustomTitle(title);
      //  b.setMessage("Would you like to receive requests for custom content from customers? You can increase your revenue by creating custom pictures and videos.");
        b.setSingleChoiceItems(mypreferncepref, position, null);
        b.setPositiveButton("Cancel",null);
        b.setNegativeButton("OK", positiveListener);
        AlertDialog d = b.create();
 
        /** Return the alert dialog window */
        return d;
    }
}
