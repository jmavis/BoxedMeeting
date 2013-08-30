package com.JaredMavis.boxedmeeting;

import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.JaredMavis.Utils.Utils;

/**
 * This class is based off of google's number picker class
 * It will handle the timer display functionality. Meaning the up and down buttons and updating the center display to 
 * a correctly formated number
 */
public class TimerDisplay extends LinearLayout implements OnClickListener, OnLongClickListener {
    @SuppressWarnings("unused")
	private static final String TAG = "NumberPicker";
    private static final int DEFAULT_MAX = 60 * 1000 * 60;
    private static final int DEFAULT_MIN = 1 * 1000 * 60;
    private static final double MAX_MULTIPLE = .45;
    private static final double MULTIPLE_INCREMENT = .075;
    private double currentMultiple = 1;
    
    private final Handler mHandler;
    private final Runnable mRunnable = new Runnable() {
        public void run() {
        	if (currentMultiple > MAX_MULTIPLE){
        		currentMultiple -= MULTIPLE_INCREMENT;
        	}
        	if (mIncrement) {
                incCurrent();
            } else if (mDecrement) {
            	decCurrent();
            }
        	mHandler.postDelayed(this, (long) (mSpeed*currentMultiple));
        }
    };

    private TextView mText;
    protected long mStart;
    protected long mEnd;
    protected long msCurrent;
    protected long msPrevious;
    protected int mCurrent;
    protected int mPrevious;
    private long startingTime = -1;
    private long mSpeed = 300;
    private boolean mIncrement;
    private boolean mDecrement;

    public TimerDisplay(Context context) {
        this(context, null);
    }

    public TimerDisplay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerDisplay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.number_picker, this, true);
        mHandler = new Handler();
        mIncrementButton = (NumberPickerButton) findViewById(R.id.increment);
        mIncrementButton.setOnClickListener(this);
        mIncrementButton.setOnLongClickListener(this);
        mIncrementButton.setNumberPicker(this);
        mDecrementButton = (NumberPickerButton) findViewById(R.id.decrement);
        mDecrementButton.setOnClickListener(this);
        mDecrementButton.setOnLongClickListener(this);
        mDecrementButton.setNumberPicker(this);

        mText = (TextView) findViewById(R.id.timepicker_input);
        
        mStart = DEFAULT_MIN;
        mEnd = DEFAULT_MAX;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mIncrementButton.setEnabled(enabled);
        mDecrementButton.setEnabled(enabled);
    }

    /**
     * Will update the current value of the display to the given value in seconds
     * @param time in ms
     */
    public void setCurrent(long timeMS) {
        msCurrent = timeMS;
        updateView();
    }

    /**
     * The speed (in milliseconds) at which the numbers will scroll
     * when the the +/- buttons are longpressed. Default is 300ms.
     */
    public void setSpeed(long speed) {
        mSpeed = speed;
    }

    public void onClick(View v) {
        // now perform the increment/decrement
        if (R.id.increment == v.getId()) {
        	incCurrent();
        } else if (R.id.decrement == v.getId()) {
        	decCurrent();
        }
    }

    private String formatNumber(long value) {
    	return (String.format("%02d:%02d",
  			   TimeUnit.MILLISECONDS.toMinutes(value),
  			   TimeUnit.MILLISECONDS.toSeconds(value) -
  			   TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(value))));
    }
    
    private void incCurrent(){
        changeCurrent(msCurrent + (1000 * 60));
    }
    
    private void decCurrent(){
        changeCurrent(msCurrent - (1000 * 60));
    }

    protected void changeCurrent(long currentMS) {
        // Wrap around the values if we go past the start or end
        if (currentMS > mEnd) {
        	currentMS = mStart;
        } else if (currentMS < mStart) {
        	currentMS = mEnd;
        }
        msPrevious = msCurrent;
        msCurrent = currentMS;

        updateView();
    }

    protected void updateView() {
    	mText.setText(formatNumber(msCurrent));    
    }

    /**
     * We start the long click here but rely on the {@link NumberPickerButton}
     * to inform us when the long click has ended.
     */
    public boolean onLongClick(View v) {

        /* The text view may still have focus so clear it's focus which will
         * trigger the on focus changed and any typed values to be pulled.
         */
        currentMultiple = 1;
        if (R.id.increment == v.getId()) {
            mIncrement = true;
            mHandler.post(mRunnable);
        } else if (R.id.decrement == v.getId()) {
            mDecrement = true;
            mHandler.post(mRunnable);
        }
        return true;
    }

    public void cancelIncrement() {
        mIncrement = false;
        currentMultiple = 1;
        mHandler.removeCallbacks(mRunnable);
    }

    public void cancelDecrement() {
        mDecrement = false;
        currentMultiple = 1;
        mHandler.removeCallbacks(mRunnable);
    }

    private NumberPickerButton mIncrementButton;
    private NumberPickerButton mDecrementButton;

    /**
     * @return the current value.
     */
    public long getCurrent() {
        return msCurrent;
    }
    
    /**
     * Will remove the buttons from the display making it so this can only be updated by 
     * UpdateDisplay(timeLef)
     */
    public void LockDisplay(){
    	startingTime = getCurrent();
    	mIncrementButton.setVisibility(View.INVISIBLE);
    	mDecrementButton.setVisibility(View.INVISIBLE);
    	mIncrementButton.setEnabled(false);
    	mDecrementButton.setEnabled(false);
    	mText.setEnabled(false);
    }
    
    /**
     * Will re-enable the buttons to allow the time to be changed
     */
    public void UnLockDisplay(){
    	msCurrent = startingTime;
    	updateView();
    	mIncrementButton.setVisibility(View.VISIBLE);
    	mDecrementButton.setVisibility(View.VISIBLE);
    	mIncrementButton.setEnabled(true);
    	mDecrementButton.setEnabled(true);
    	mText.setEnabled(true);
    }

    public void UpdateDisplay(long timeLeft){
    	msCurrent = timeLeft;
    	updateView();
    }
    
    /**
     * @param max in minutes
     */
    public void setMaxTime(int max){
    	mEnd = max * 60 * 1000;
    }
    
    public void setStartTime(long time){
    	startingTime = time;
    }
    
    public void setDefaultStartTime(){
    	startingTime = Utils.defaultStartingTimeInMS(getContext());
    }
    
    public void setToLastStartTime(){
    	msCurrent = getStartingTime();
    	updateView();
    }
    
    public long getStartingTime(){
    	if (startingTime == -1){
    		setDefaultStartTime();
    	}
    	return startingTime;
    }
}
