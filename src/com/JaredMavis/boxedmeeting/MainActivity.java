package com.JaredMavis.boxedmeeting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.JaredMavis.Notifications.NotificationSender;
import com.JaredMavis.Utils.PreferenceHandler;
import com.JaredMavis.Utils.Utils;
import com.JaredMavis.Utils.ViewScaler;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Main activity for app
 * Handles the interfacing between the timer and android os. 
 * @author Jared Mavis
 */
public class MainActivity extends TimerActivity {
	@SuppressWarnings("unused")
	private String TAG = "MainActivity";
	private String timeToGoOffKey = "TimeToGoOff";
	private long _timeToGoOffFromLastSession; 
	
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
			markTimeAlarmEnds(-1);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadPreferences();
		NotificationSender.cancelNotificationsAndAlarms(this);
		long currentTime = System.currentTimeMillis();
		if (_timeToGoOffFromLastSession != -1 && _timeToGoOffFromLastSession > currentTime){
			UpdateTimerToLastSessionTime(_timeToGoOffFromLastSession, currentTime);
			_display.setStartTime(Utils.defaultStartingTimeInMS(this));
			markTimeAlarmEnds(-1);
		} else {
			_display.setToLastStartTime();
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
		if (!_hasScaled) {
			ViewScaler.scaleContents(_scalingContents, _rootLayout);
			_hasScaled = true;
		}
	}

	private void loadPreferences() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		_shouldNotifyAtWarning = sharedPrefs.getBoolean("checkboxNotify", true);
		_timeToGoOffFromLastSession = sharedPrefs.getLong(timeToGoOffKey, -1);
	}
	
	/**
	 * Will save the time when the alarm should go off
	 */
	private void markTimeAlarmEnds(long timeToGoOff){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putLong(this.getString(R.string.PrefKey_TimeToGoOff), timeToGoOff); 
		editor.commit();
	}

	/**
	 * Will determine the amount of time that we need to finish and will set the time to that
	 * and start the timer with that time
	 */
	private void UpdateTimerToLastSessionTime(long timeToGoOff, long currentTime){
		long msLeft = timeToGoOff - currentTime;

		
		_display.setCurrent(msLeft);
		_display.setDefaultStartTime();
		super.startTimer();
		if (msLeft < Utils.warningTimeInMs(this)){
			_hasGaveWarningWarning = true;
		}
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
				startActivityForResult(myIntent, R.integer.Value_PREFERENCESCREENREQUESTCODE);
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.integer.Value_PREFERENCESCREENREQUESTCODE && resultCode == RESULT_OK) {
			loadPreferences();
		}
	}
}
