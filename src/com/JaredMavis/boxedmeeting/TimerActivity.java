package com.JaredMavis.boxedmeeting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.JaredMavis.MeetingTimer.MeetingTimer;
import com.JaredMavis.Utils.Utils;

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
	private boolean _hasGaveWarning = false;
	final ToneGenerator _tone = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
	private String[] defaultTimes;
	
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
		defaultTimes = getResources().getStringArray(R.array.Array_DefaultTimes);
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
			if (SHOULDNOTIFYFIVEMINS && !_hasGaveWarning && newTime <= Utils.WaitTime() && _meetingTime >= 5){
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
