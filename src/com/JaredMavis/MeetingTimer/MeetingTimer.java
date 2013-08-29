package com.JaredMavis.MeetingTimer;

import java.beans.PropertyChangeListener;

import com.JaredMavis.boxedmeeting.R;

import android.content.Context;

/**
 *  Will hold the time left in the count down and will notify when the data changes
 * @author Jared Mavis
 */
public class MeetingTimer {
	@SuppressWarnings("unused")
	private String TAG = "MeetingTimer";
	
	private Boolean _isRunning;
	MeetingCountDownTimer _timer;
	PropertyChangeListener _listener;
	Context _context;

	public MeetingTimer(Context context, PropertyChangeListener listener) {
		_listener = listener;
		_isRunning = false;
		_context = context;
	}
	
	public long getMillisLeft(){
		return (_timer.getMillisLeft());
	}

	public void start(long timeToRun){
		_isRunning = true;
		_timer = new MeetingCountDownTimer(_context, 
										   _listener, 
										   timeToRun, 
										   _context.getResources().getInteger(R.integer.Value_TimerUpdateIntervalInMs));
		_timer.start();
	}
	
	public void stop(){
		_isRunning = false;
		_timer.cancel();
	}
	
	public Boolean isRunning(){
		return (_isRunning);
	}
}
