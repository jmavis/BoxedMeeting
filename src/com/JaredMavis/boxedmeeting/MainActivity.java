package com.JaredMavis.boxedmeeting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Formatter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.JaredMavis.MeetingTimer.MeetingTimer;

public class MainActivity extends Activity implements PropertyChangeListener, OnClickListener {
	private String TAG = "MainActivity";
	private int STARTTIME = 15;
	
	private Button _startStopButton;
	private TimerDisplay _display;
	private MeetingTimer _timer;
	private int _meetingTime;
	private long _warningNotificationTime = 5 * 60 * 1000; // the time when a quick warning buzz should be given
	private boolean _hasGaveWarning = false;
	private Vibrator _vibrator;
	private long[] meetingEndVibrationPattern = {0, 200, 200,200};
	private long[] meetingWarningNotificationPattern = {0,200,200};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}
	
	private void init(){
		bindViews();
		_timer = new MeetingTimer(getBaseContext(), this);
		_meetingTime = STARTTIME;
		_display.setCurrent(_meetingTime);
		_display.SetSize(getWindowManager().getDefaultDisplay(), .65, .1, .2);
		_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	private void bindViews(){
		_display = (TimerDisplay) findViewById(R.id.timerDisplay);
		_startStopButton = (Button) findViewById(R.id.startStopButton);
		_startStopButton.setOnClickListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		Log.d(TAG, event.getPropertyName());
		if (event.getPropertyName().equals(this.getString(R.string.Value_TimerUpdate))){
			long newTime = (Long) event.getNewValue();
			updateDisplay(newTime);
			if (!_hasGaveWarning && newTime <= _warningNotificationTime && _meetingTime >= 5){
				_vibrator.vibrate(meetingWarningNotificationPattern, -1);
				_hasGaveWarning = true;
			}
		} else if (event.getPropertyName().equals(this.getString(R.string.Value_TimerFinished))){
			onFinish();
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.startStopButton: {
				onButtonClick();
				break;
			}
		}
	}
	
	private void onButtonClick(){
		if (_timer.isRunning()){
			onStopClick();
		} else {
			onStartClick();
		}
	}
	
	private void onStartClick(){
		_startStopButton.setText(this.getString(R.string.Stop));
		_timer.start(getMeetingTimeInMillis());
		updateDisplayToCurrent();
		_display.LockDisplay();
	}
	
	private void onStopClick(){
		_startStopButton.setText(this.getString(R.string.Start));
		_timer.stop();
		_display.UnLockDisplay();
	}
	
	private void onFinish(){
		updateDisplay(0);
		_startStopButton.setText(this.getString(R.string.Start));
		_timer.stop();
		_vibrator.vibrate(meetingEndVibrationPattern, -1);
		_display.UnLockDisplay();
	}
	
	private long getMeetingTimeInMillis(){
		return (_display.getCurrent() * 1000 * 60);
	}
	
	private void updateDisplayToCurrent(){
		updateDisplay(getMeetingTimeInMillis());
	}
	
	private void updateDisplay(long timeLeft){
		_display.UpdateDisplay(timeLeft);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
