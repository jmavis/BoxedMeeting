package com.JaredMavis.boxedmeeting;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.JaredMavis.Utils.AlarmReciever;
import com.JaredMavis.Utils.NotificationService;
import com.JaredMavis.Utils.PreferenceHandler;
import com.JaredMavis.Utils.Utils;
import com.JaredMavis.Utils.ViewScaler;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Main activity for app
 * 
 * @author Jared Mavis
 */
public class MainActivity extends TimerActivity {
	private String TAG = "MainActivity";
	private int PREFERENCESCREENREQUESTCODE = 1;
	private String timeToGoOffKey = "TimeToGoOff";
	private long timeToGoOffFromLastSession; 
	
	private void SetNotifications(long time){
		long timeToGoOff = System.currentTimeMillis() + time;
		if (time > Utils.WaitTime() && SHOULDNOTIFYFIVEMINS) {
			SetNotification("5 Minutes Left", "", timeToGoOff - Utils.WaitTime(), R.string.Value_Meeting5MinNotificationID);
		} 
		
		SetNotification("Time is up", "Time is up", timeToGoOff, R.string.Value_MeetingEndNotificationID);
	}
	
	private void SetNotification(String title, String text, long time, int id) {
        //---PendingIntent to launch activity when the alarm triggers---
        Intent i = new Intent(this, AlarmReciever.class);

        //---assign an ID of 1---
        i.putExtra("NotifID", id);
        i.putExtra("Title", title); 
        i.putExtra("Text", text);

        AlarmManager mAlarmManager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, i,0);
        
		mAlarmManager.set(AlarmManager.RTC, time, pendingIntent);
	}
	
	// will cancel all alarms with the same pending intent that goes to this app and any notifications that are showing
	void CancelNotification() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(getBaseContext(), NotificationService.class);

        PendingIntent displayIntent = PendingIntent.getActivity(getBaseContext(), 0, i, 0);

		alarmManager.cancel(displayIntent); 
		
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
	}

	/*
	 * When leaving the app we will stop the timer thread and will set alarms to go off at the right time
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG, "on pause");
		if (_timer.isRunning()){
			long timeToGoOff = System.currentTimeMillis() + _timer.getMillisLeft();
			SetNotifications(_timer.getMillisLeft());
			markTimeLeftApp(timeToGoOff);
			_timer.stop();	
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadPreferences();
		Log.d(TAG, "resuming. time from last session = " + timeToGoOffFromLastSession);
		if (timeToGoOffFromLastSession != -1){
			UpdateTimerToLastSessionTime(timeToGoOffFromLastSession);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Log.d(TAG, "Window focus change");
		if (!hasScaled) {
			ViewScaler.scaleContents(_scalingContents, _rootLayout);
			hasScaled = true;
		}
	}

	private void loadPreferences() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		int maxTime = Integer.parseInt((sharedPrefs.getString("maxMeetingTime",
				"60")));
		
		_display.setMaxTime(maxTime);
		SHOULDNOTIFYFIVEMINS = sharedPrefs.getBoolean("checkboxNotify", true);
		timeToGoOffFromLastSession = sharedPrefs.getLong(timeToGoOffKey, -1);
	}
	
	/*
	 * Will save the time left on the timer when we leave the app
	 */
	private void markTimeLeftApp(long timeToGoOff){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putLong("TimeToGoOff", timeToGoOff);
		editor.apply();
	}

	private void UpdateTimerToLastSessionTime(long timeToGoOff){
		long currentTime = System.currentTimeMillis();
		if (currentTime > timeToGoOff) return;
		int timeLeft = (int) ((timeToGoOff - currentTime) / 1000);
		Log.d(TAG, "Updaing time to last with time " + timeLeft);
		_display.setCurrent(timeLeft);
		_timer.start(getMeetingTimeInMillis());
		markTimeLeftApp(-1);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.options:
			// Go to options page
			Intent myIntent = new Intent(getBaseContext(),
					PreferenceHandler.class);

			startActivityForResult(myIntent, PREFERENCESCREENREQUESTCODE);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PREFERENCESCREENREQUESTCODE) {
			if (resultCode == RESULT_OK) {
				loadPreferences();
			}
		}
	}
}
