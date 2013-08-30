package com.JaredMavis.Utils;

import android.content.Context;
import com.JaredMavis.boxedmeeting.R;

public class Utils {
	@SuppressWarnings("unused")
	private static final String TAG = "Utils";
	
	public static long warningTimeInMs(Context context){
		return (60 * 1000 * context.getResources().getInteger(R.integer.Value_DefultWarningTimeInMins));
	}
	
	public static int warningTimeInMins(Context context){
		return (context.getResources().getInteger(R.integer.Value_DefultWarningTimeInMins));
	}
	
	public static long defaultStartingTimeInMS(Context context){
		return (60 * 1000 * context.getResources().getInteger(R.integer.Value_TimerDefaultStartingTimeInMin));
	}
}
