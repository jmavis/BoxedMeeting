package com.JaredMavis.boxedmeeting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.JaredMavis.MeetingTimer.MeetingTimer;

public class MainActivity extends Activity implements PropertyChangeListener, OnClickListener {
	private String TAG = "MainActivity";
	
	TextView _timeDisplay;
	Button _startStopButton;
	RelativeLayout _backgroundLayout;
	MeetingTimer _timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}
	
	private void init(){
		bindViews();
		_timer = new MeetingTimer(this);
		
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
		if (event.getPropertyName().equals(Integer.toString(R.string.Value_TimerUpdate))){
			updateDisplay((Long) event.getNewValue());
		} else if (event.getPropertyName().equals(Integer.toString(R.string.Value_TimerFinished))){
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
		long time = Long.decode(_timeDisplay.getText().toString());
		time *= 1000 * 60; // the user enters minutes so change to seconds
		_timer.start(10000);
	}
	
	private void onDisplayClick(){
		Log.d(TAG, "onDisplayClick()");
		popUpTimeInput();
	}
	
	private void popUpTimeInput(){
		final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
	    helpBuilder.setTitle("");

	    LayoutInflater inflater = getLayoutInflater();
	    final View checkboxLayout = inflater.inflate(R.layout.timer_set_dialog, null);
	    helpBuilder.setView(checkboxLayout);

	    final AlertDialog helpDialog = helpBuilder.create();
	    helpDialog.show();
	}
	
	private void onStopClick(){
		_startStopButton.setText(Integer.toString(R.string.Start));
		_timer.stop();
	}
	
	private void onFinish(){
		_startStopButton.setText(Integer.toString(R.string.Stop));
		_timer.stop();
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
