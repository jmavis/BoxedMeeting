package com.JaredMavis.MeetingTimer;

import java.beans.PropertyChangeListener;

import android.util.Log;

// Will hold the time left in the countdown and will notify when the data changes
public class MeetingTimer {
	private String TAG = "MeetingTimer";
	
	public static final String TimerChange = "TimerChange";
	public static final String TimerFinish = "TimerFinish";
	private static final long updateInterval = 1000; // in ms
	
	private Boolean isRunning;
	MeetingCountDownTimer _timer;
	PropertyChangeListener _listener;

	public MeetingTimer(PropertyChangeListener listener) {
		_listener = listener;
		isRunning = false;
	}

	public void start(long timeToRun){
		Log.d(TAG, "start(" + Long.toString(timeToRun) + ")");
		isRunning = true;
		_timer = new MeetingCountDownTimer(_listener, timeToRun, updateInterval);
		_timer.start();
	}
	
	public void stop(){
		Log.d(TAG, "start()");
		isRunning = false;
		_timer.cancel();
	}
	
	public Boolean isRunning(){
		return (isRunning);
	}
}
