package com.JaredMavis.boxedmeeting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.JaredMavis.MeetingTimer.MeetingTimer;

public class MainActivity extends Activity implements PropertyChangeListener, OnClickListener {
	private String TAG = "MainActivity";
	
	EditText _timeDisplay;
	Button _startStopButton;
	MeetingTimer timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}
	
	private void init(){
		bindViews();
		timer = new MeetingTimer(this);
		
	}
	
	private void bindViews(){
		_timeDisplay = (EditText) findViewById(R.id.editText1);
		_startStopButton = (Button) findViewById(R.id.button1);
		_startStopButton.setOnClickListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		Log.d(TAG, event.getPropertyName());
		if (event.getPropertyName().equals(MeetingTimer.TimerChange)){
			updateDisplay((Long) event.getNewValue());
		} else if (event.getPropertyName().equals(MeetingTimer.TimerFinish)){
			
		}
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.button1: {
				onButtonClick();
				break;
			}
		}
	}
	
	private void onButtonClick(){
		if (timer.isRunning()){
			onStopClick();
		} else {
			onStartClick();
		}
	}
	
	private void onStartClick(){
		timer.start(120000);
	}
	
	private void onStopClick(){
		timer.stop();
	}
	
	private void updateDisplay(long timeLeft){
		String text = String.format("%d:%d",
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
