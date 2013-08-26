package com.JaredMavis.boxedmeeting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.JaredMavis.MeetingTimer.MeetingTimer;
import com.JaredMavis.Utils.AlarmReciever;
import com.JaredMavis.Utils.NotificationService;

public class TimerActivity extends Activity implements PropertyChangeListener,  OnClickListener{
	private static int STARTTIME = 15;
	private String TAG = "TimerActivity";
	protected boolean SHOULDNOTIFYFIVEMINS;

	protected static boolean hasScaled = false;
	protected Button _startStopButton;
	protected TimerDisplay _display;
	protected MeetingTimer _timer;
	
	protected FrameLayout _rootLayout;
	protected RelativeLayout _scalingContents;
	
	private int _meetingTime;
	private long _warningNotificationTime = 5 * 60 * 1000; // the time when a quick warning buzz should be given
	private boolean _hasGaveWarning = false;
	final ToneGenerator _tone = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
	private String[] defaultTimes = {"5","15","30","45","60"};
	
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
		hasScaled = false;
    }

	private void bindViews(){
		_display = (TimerDisplay) findViewById(R.id.timerDisplay);
		_startStopButton = (Button) findViewById(R.id.startStopButton);
		_startStopButton.setOnClickListener(this);
		_rootLayout = (FrameLayout) findViewById(R.id.rootLayout);
		_scalingContents = (RelativeLayout) findViewById(R.id.contents);
	}

	public void propertyChange(PropertyChangeEvent event) {
		Log.d(TAG, event.getPropertyName());
		if (event.getPropertyName().equals(this.getString(R.string.Value_TimerUpdate))){
			long newTime = (Long) event.getNewValue();
			updateDisplay(newTime);
			if (SHOULDNOTIFYFIVEMINS && !_hasGaveWarning && newTime <= _warningNotificationTime && _meetingTime >= 5){
				_tone.startTone(ToneGenerator.TONE_PROP_BEEP);
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
			case R.id.timepicker_input:{
				showDefaultChoicesDialog();
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
		_meetingTime = _display.getCurrent();
		updateDisplayToCurrent();
		_display.LockDisplay();
		_hasGaveWarning = false;
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
		_tone.startTone(ToneGenerator.TONE_PROP_BEEP2);
		_display.UnLockDisplay();
	}
	
	protected long getMeetingTimeInMillis(){
		return (_display.getCurrent() * 1000 * 60);
	}
	
	private void updateDisplayToCurrent(){
		updateDisplay(getMeetingTimeInMillis());
	}
	
	private void updateDisplay(long timeLeft){
		_display.UpdateDisplay(timeLeft);
	}
	
	private void showDefaultChoicesDialog(){
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Time");
		builder.setItems(defaultTimes , new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				_display.setCurrent(Integer.valueOf(defaultTimes[which]));
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
}
