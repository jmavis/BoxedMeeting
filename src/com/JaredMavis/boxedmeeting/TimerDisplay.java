package com.JaredMavis.boxedmeeting;

import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * This class is based off of google's number picker class
 */
public class TimerDisplay extends LinearLayout implements OnClickListener, OnLongClickListener {
    private static final String TAG = "NumberPicker";
    private static final int DEFAULT_MAX = 60;
    private static final int DEFAULT_MIN = 1;
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
                changeCurrent(mCurrent + 1);
            } else if (mDecrement) {
                changeCurrent(mCurrent - 1);
            }
        	mHandler.postDelayed(this, (long) (mSpeed*currentMultiple));
        }
    };

    private EditText mText;
    protected int mStart;
    protected int mEnd;
    protected long msCurrent;
    protected int mCurrent;
    protected int mPrevious;
    private long mSpeed = 300;

    private boolean mIncrement;
    private boolean mDecrement;
    private boolean isCountingDown;

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

        mText = (EditText) findViewById(R.id.timepicker_input);
        mText.setFocusable(false);

        mStart = DEFAULT_MIN;
        mEnd = DEFAULT_MAX;
        isCountingDown = false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mIncrementButton.setEnabled(enabled);
        mDecrementButton.setEnabled(enabled);
        mText.setEnabled(enabled);
    }

    public void setCurrent(int current) {
        mCurrent = current;
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
            changeCurrent(mCurrent + 1);
        } else if (R.id.decrement == v.getId()) {
            changeCurrent(mCurrent - 1);
        }
    }

    private String formatNumber(long value) {
    	return (String.format("%02d:%02d",
  			   TimeUnit.MILLISECONDS.toMinutes(value),
  			   TimeUnit.MILLISECONDS.toSeconds(value) -
  			   TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(value))));
    }

    protected void changeCurrent(int current) {
        // Wrap around the values if we go past the start or end
        if (current > mEnd) {
            current = mStart;
        } else if (current < mStart) {
            current = mEnd;
        }
        mPrevious = mCurrent;
        mCurrent = current;

        updateView();
    }

    protected void updateView() {
        /* If we don't have displayed values then use the
         * current number else find the correct value in the
         * displayed values for the current number.
         */
    	if (!isCountingDown){
    		mText.setText(formatNumber(mCurrent*60*1000));
    	} else {
    		mText.setText(formatNumber(msCurrent));
    	}       
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
    public int getCurrent() {
        return mCurrent;
    }
    
    /**
     * Will remove the buttons from the display making it so this can only be updated by 
     * UpdateDisplay(timeLef)
     */
    public void LockDisplay(){
    	isCountingDown = true;
    	mIncrementButton.setVisibility(View.INVISIBLE);
    	mDecrementButton.setVisibility(View.INVISIBLE);
    	mIncrementButton.setEnabled(false);
    	mDecrementButton.setEnabled(false);
    }
    
    /**
     * Will re-enable the buttons to allow the time to be changed
     */
    public void UnLockDisplay(){
    	isCountingDown = false;
    	mCurrent = (int) Math.ceil(msCurrent/60/1000); // will truncate any seconds left
    	updateView();
    	mIncrementButton.setVisibility(View.VISIBLE);
    	mDecrementButton.setVisibility(View.VISIBLE);
    	mIncrementButton.setEnabled(true);
    	mDecrementButton.setEnabled(true);
    }

    public void UpdateDisplay(long timeLeft){
    	msCurrent = timeLeft;
    	updateView();
    }


	
	
    
    public void setMaxTime(int max){
    	mEnd = max;
    }
}
