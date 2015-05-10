package com.sunil.selectmutiple;

import java.util.HashMap;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class UILApplication extends Application{
	
	private static final String TAG = "UILApplication";
	//private static final String PROPERTY_ID = "UA-46196759-2";
	private static final String PROPERTY_ID = "UA-50771208-3";
	public static int GENERAL_TRACKER = 0;
	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
		}
	

	public UILApplication() {
	super();
	}
	
	synchronized Tracker getTracker(TrackerName trackerId) {
	if (!mTrackers.containsKey(trackerId)) {
	
	GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
	Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
	: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(PROPERTY_ID)
	: analytics.newTracker(R.xml.ecommerce_tracker);
	mTrackers.put(trackerId, t);
	
	}
	return mTrackers.get(trackerId);
	}
	
	@Override
	public void onCreate() {

		super.onCreate();

	}


}
