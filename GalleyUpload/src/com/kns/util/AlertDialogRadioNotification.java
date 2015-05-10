package com.kns.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class AlertDialogRadioNotification extends DialogFragment{
	
	public String[] notificationpref = new String[]{"Notify me as soon as custom content requests are made.","Notify me once a day (Recommended).","Notify me once a week.", "Never notify me.", "Check Custom Request"};
    
	AlertPositiveListener alertPositiveListener;
    public interface AlertPositiveListener {
        public void onPositiveClick(int position);
    }
 
    /** This is a callback method executed when this fragment is attached to an activity.
    *  This function ensures that, the hosting activity implements the interface AlertPositiveListener
    * */
    @SuppressLint("NewApi")
	public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try{
            alertPositiveListener = (AlertPositiveListener) activity;
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
 
        /** Getting the arguments passed to this fragment */
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Custom Request Notifications");
        b.setSingleChoiceItems(notificationpref, position, null);
        b.setPositiveButton("Cancel",null);
        b.setNegativeButton("OK", positiveListener);
        AlertDialog d = b.create();
 
        /** Return the alert dialog window */
        return d;
    }
}