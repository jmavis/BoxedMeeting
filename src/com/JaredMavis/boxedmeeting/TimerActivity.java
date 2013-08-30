package com.JaredMavis.boxedmeeting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.JaredMavis.MeetingTimer.MeetingTimer;
import com.JaredMavis.Utils.Utils;

/**
 * Handles the timer and display functionality of the activity 
 * @author Jared Mavis
 *
 */
public class TimerActivity extends Activity implements PropertyChangeListener,  OnClickListener{
	@SuppressWarnings("unused")
	private String TAG = "TimerActivity";

	protected static boolean _shouldNotifyAtWarning;
	protected static boolean _hasScaled;
	protected boolean _hasGaveWarningWarning;
	
	// Views
	protected Button _startStopButton;
	protected TimerDisplay _display;
	protected MeetingTimer _timer;
	protected FrameLayout _rootLayout;
	protected RelativeLayout _scalingContents;
	
	private String[] _defaultTimes;
	protected Resources _resources;
	
	private Vibrator _tone;
	private long[] _tonePatternEnd = {0, 200, 300, 300, 300, 300};
	private long[] _tonePatternWarning = {0, 200, 300, 300, 300};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}
	
	private void init(){
		bindViews();
		_timer = new MeetingTimer(getBaseContext(), this);
		_hasScaled = false;
		_resources = getResources();
		_tone = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		_defaultTimes = _resources.getStringArray(R.array.Array_DefaultTimes);
    }

	/**
	 * Retrieve the view references from the layout
	 */
	private void bindViews(){
		_display = (TimerDisplay) findViewById(R.id.timerDisplay);
		_startStopButton = (Button) findViewById(R.id.startStopButton);
		_startStopButton.setOnClickListener(this);
		_rootLayout = (FrameLayout) findViewById(R.id.rootLayout);
		_scalingContents = (RelativeLayout) findViewById(R.id.contents);
	}

	/**
	 * Alerted when the running timer ticks or finishes
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(this.getString(R.string.Value_TimerUpdate))){
			long currentTime = (Long) event.getNewValue();
			updateDisplay(currentTime);
			if (shouldPlayWarningSound(currentTime)){
				playNotification(_tonePatternWarning);
				_hasGaveWarningWarning = true;
			}
		} else if (event.getPropertyName().equals(this.getString(R.string.Value_TimerFinished))){
			onTimerFinish();
		}
	}
	
	@Override
	/**
	 * Called by activity whenever a view is clicked and will call appropriate onClicks
	 */
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
	
	/**
	 * Will call start or stop clicks depending on if the timer is running or not
	 */
	private void onButtonClick(){
		if (_timer.isRunning()){
			onStopClick();
		} else {
			startTimer();
		}
	}
	
	protected void startTimer(){
		_startStopButton.setText(this.getString(R.string.Stop));
		_timer.start(_display.getCurrent());
		_display.LockDisplay();
		_hasGaveWarningWarning = false;
	}
	
	private void onStopClick(){
		_startStopButton.setText(this.getString(R.string.Start));
		_timer.stop();
		_display.UnLockDisplay();
	}
	
	private void onTimerFinish(){
		updateDisplay(0);
		_startStopButton.setText(this.getString(R.string.Start));
		_timer.stop();
		playNotification(_tonePatternEnd);
		_display.UnLockDisplay();
	}
	
	protected long getMeetingTimeInMillis(){
		return (_display.getCurrent() * 1000 * 60);
	}
	
	private void updateDisplay(long timeLeft){
		_display.UpdateDisplay(timeLeft);
	}
	
	/**
	 * @param currentTime in ms
	 * @return true if the warning sound should be played at the current time
	 */
	private boolean shouldPlayWarningSound(long currentTime){
		return (_shouldNotifyAtWarning && 
				!_hasGaveWarningWarning && 
				currentTime < Utils.warningTimeInMs(this) && 
				_display.getStartingTime() > Utils.warningTimeInMs(this));
	}
	
	private void showDefaultChoicesDialog(){
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Time");
		builder.setItems(_defaultTimes , new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				_display.setCurrent(Integer.valueOf(_defaultTimes[which])*60*1000);
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
	
	private void playNotification(long[] pattern){
		_tone.vibrate(pattern, -1);
	}
}
