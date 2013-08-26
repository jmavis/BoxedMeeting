package com.JaredMavis.MeetingTimer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.JaredMavis.boxedmeeting.R;

public class MeetingCountDownTimer extends CountDownTimer{
	private String TAG = "MeetingCountDownTimer";
	
	PropertyChangeListener _listener;
	Context _context;
	long _timeLeft; // in ms
	
	public MeetingCountDownTimer(Context context, PropertyChangeListener listener, long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		_listener = listener; 
		_timeLeft = millisInFuture;
		_context = context;
	}
	
	public long getMillisLeft(){
		return (_timeLeft);
	}

	@Override
	public void onFinish() {
		//Log.d(TAG, "onFinish()");
		_listener.propertyChange(new PropertyChangeEvent(this, _context.getString(R.string.Value_TimerFinished), 0, _timeLeft));
	}

	@Override
	public void onTick(long millisUntilFinished) {
		Log.d(TAG, "onTick(" + Long.toString(millisUntilFinished) + ")");
		_listener.propertyChange(new PropertyChangeEvent(this, _context.getString(R.string.Value_TimerUpdate), _timeLeft, millisUntilFinished));
		_timeLeft = millisUntilFinished;
	}
}
