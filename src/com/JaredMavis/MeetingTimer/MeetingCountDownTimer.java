package com.JaredMavis.MeetingTimer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import android.content.Context;
import android.os.CountDownTimer;
import com.JaredMavis.boxedmeeting.R;

/**
 * Used to count down to a given number at a given interval and will alert a listener with either a Value_TimerUpdate or Value_TimerFinished
 * @author Jared Mavis
 *
 */
public class MeetingCountDownTimer extends CountDownTimer {
	@SuppressWarnings("unused")
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
		_listener.propertyChange(new PropertyChangeEvent(this, _context.getString(R.string.Value_TimerFinished), 0, _timeLeft));
		this.cancel();
	}

	@Override
	public void onTick(long millisUntilFinished) {
		_listener.propertyChange(new PropertyChangeEvent(this, _context.getString(R.string.Value_TimerUpdate), _timeLeft, millisUntilFinished));
		_timeLeft = millisUntilFinished;
	}
}
