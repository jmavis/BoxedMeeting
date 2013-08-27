package com.JaredMavis.boxedmeeting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.JaredMavis.Notifications.NotificationSender;
import com.JaredMavis.Utils.PreferenceHandler;
import com.JaredMavis.Utils.ViewScaler;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Main activity for app
 * Handles the interfacing between the timer and android os. 
 * @author Jared Mavis
 */
public class MainActivity extends TimerActivity {
	private String TAG = "MainActivity";
	private String timeToGoOffKey = "TimeToGoOff";
	private long _timeToGoOffFromLastSession; 
	private int defaultMeetingTime;
	
	/*
	 * When leaving the app we will stop the timer thread and will set alarms to go off at the right time
	 * as we may have our timer thread ended so we cancel and set and alarm for the time ending
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause(){
		super.onPause();
		if (_timer.isRunning()){
			long timeToGoOff = System.currentTimeMillis() + _timer.getMillisLeft();
			NotificationSender.setNotifications(_timer.getMillisLeft(), _shouldNotifyAtWarning, this);
			markTimeAlarmEnds(timeToGoOff);
			_timer.stop();	
		} else {
			markTimeAlarmEnds(-1); // TODO
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadPreferences();
		Log.d(TAG, "resuming. time from last session = " + _timeToGoOffFromLastSession);
		long currentTime = System.currentTimeMillis();
		if (_timeToGoOffFromLastSession != -1 && _timeToGoOffFromLastSession > currentTime){ //TODO
			UpdateTimerToLastSessionTime(_timeToGoOffFromLastSession, currentTime);
			markTimeAlarmEnds(-1);
		} else {
			Log.d(TAG, "Setting to default time at " + defaultMeetingTime);
			_display.setCurrent(defaultMeetingTime);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		NotificationSender.cancelNotificationsAndAlarms(this);
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
		if (!_hasScaled) {
			ViewScaler.scaleContents(_scalingContents, _rootLayout);
			_hasScaled = true;
		}
	}

	private void loadPreferences() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		int maxTime = sharedPrefs.getInt(getResources().getString(R.string.PrefKey_MaxMeetingTime), R.string.Value_DefaultMaxMeetingTimeInMins);
		
		_display.setMaxTime(maxTime);
		_shouldNotifyAtWarning = sharedPrefs.getBoolean("checkboxNotify", true); //TODO
		_timeToGoOffFromLastSession = sharedPrefs.getLong(timeToGoOffKey, -1);
		defaultMeetingTime = sharedPrefs.getInt("defaultMeetingTime", 15);
	}
	
	/**
	 * Will save the time when the alarm should go off
	 */
	private void markTimeAlarmEnds(long timeToGoOff){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putLong(this.getString(R.string.PrefKey_TimeToGoOff), timeToGoOff); 
		editor.apply();
	}

	/**
	 * Will determine the amount of time that we need to finish and will set the time to that
	 * and start the timer with that time
	 */
	private void UpdateTimerToLastSessionTime(long timeToGoOff, long currentTime){
		long msLeft = timeToGoOff - currentTime;
		_display.setCurrent((int) (msLeft / 1000));
		_timer.start(msLeft);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.options:
				Intent myIntent = new Intent(getBaseContext(), PreferenceHandler.class);
				startActivityForResult(myIntent, R.string.Value_PREFERENCESCREENREQUESTCODE);
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.string.Value_PREFERENCESCREENREQUESTCODE && resultCode == RESULT_OK) {
			loadPreferences();
		}
	}
}
