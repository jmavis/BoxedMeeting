package com.JaredMavis.boxedmeeting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.JaredMavis.MeetingTimer.MeetingTimer;

public class MainActivity extends Activity implements PropertyChangeListener, OnClickListener {
	private String TAG = "MainActivity";
	private int STARTTIME = 15;
	
	TextView _timeDisplay;
	Button _startStopButton;
	RelativeLayout _backgroundLayout;
	MeetingTimer _timer;
	int _meetingTime;
	
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
		updateDisplayToCurrent();
	}
	
	private void bindViews(){
		_backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);
		_timeDisplay = (TextView) findViewById(R.id.displayText);
		_timeDisplay.setOnClickListener(this);
		_startStopButton = (Button) findViewById(R.id.startStopButton);
		_startStopButton.setOnClickListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		Log.d(TAG, event.getPropertyName());
		if (event.getPropertyName().equals(this.getString(R.string.Value_TimerUpdate))){
			updateDisplay((Long) event.getNewValue());
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
			case R.id.displayText:{
				onDisplayClick();
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
	}
	
	private void onStopClick(){
		_startStopButton.setText(this.getString(R.string.Start));
		_timer.stop();
	}
	
	private void onDisplayClick(){
		Log.d(TAG, "onDisplayClick()");
		popUpTimeInput();
	}
	
	private void popUpTimeInput(){
		final Dialog timePickerDialog = new Dialog(this);
		timePickerDialog.setTitle("");
		timePickerDialog.setContentView(R.layout.timer_set_dialog);

	    final NumberPicker numberPicker = (NumberPicker) timePickerDialog.findViewById(R.id.numberPicker1);
	    numberPicker.setRange(1, 60);
	    numberPicker.setCurrent(_meetingTime);

	    
	    Button acceptButton = (Button) timePickerDialog.findViewById(R.id.acceptButton);
	    acceptButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_meetingTime = numberPicker.getCurrent();
				updateDisplayToCurrent();
				timePickerDialog.hide();
			}
		});
	    
	    timePickerDialog.show();
	}

	
	private void onFinish(){
		_startStopButton.setText(this.getString(R.string.Start));
		_timer.stop();
	}
	
	private long getMeetingTimeInMillis(){
		return (_meetingTime * 1000 * 60);
	}
	
	private void updateDisplayToCurrent(){
		updateDisplay(getMeetingTimeInMillis());
	}
	
	private void updateDisplay(long timeLeft){
		String text = String.format("%d:%02d",
			   TimeUnit.MILLISECONDS.toMinutes(timeLeft),
			   TimeUnit.MILLISECONDS.toSeconds(timeLeft) -
			   TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft))
			);
		_timeDisplay.setText(text);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
