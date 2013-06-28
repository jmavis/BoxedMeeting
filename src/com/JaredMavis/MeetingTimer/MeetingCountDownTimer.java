package com.JaredMavis.MeetingTimer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.JaredMavis.boxedmeeting.R;

import android.os.CountDownTimer;
import android.util.Log;

public class MeetingCountDownTimer extends CountDownTimer{
	private String TAG = "MeetingCountDownTimer";
	
	PropertyChangeListener _listener;
	long _timeLeft;
	
	public MeetingCountDownTimer(PropertyChangeListener listener, long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		_listener = listener; 
		_timeLeft = millisInFuture;
	}

	@Override
	public void onFinish() {
		Log.d(TAG, "onFinish()");
		_listener.propertyChange(new PropertyChangeEvent(this, Integer.toString(R.string.Value_TimerFinished), 0, _timeLeft));
	}

	@Override
	public void onTick(long millisUntilFinished) {
		Log.d(TAG, "onTick(" + Long.toString(millisUntilFinished) + ")");
		_listener.propertyChange(new PropertyChangeEvent(this, Integer.toString(R.string.Value_TimerUpdate), _timeLeft, millisUntilFinished));
		_timeLeft = millisUntilFinished;
	}
}
