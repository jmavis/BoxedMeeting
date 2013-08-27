package com.JaredMavis.Utils;

import android.content.Context;

import com.JaredMavis.boxedmeeting.R;

public class Utils {
	public static long warningTimeInMs(Context context){
		return (60 * 1000 * context.getResources().getInteger(R.string.Value_DefultWarningTimeInMins));
	}
	
	public static int warningTimeInMins(Context context){
		return (context.getResources().getInteger(R.string.Value_DefultWarningTimeInMins));
	}
	
}
