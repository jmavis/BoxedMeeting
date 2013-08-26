package com.JaredMavis.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReciever extends BroadcastReceiver {
	private static final String TAG = "AlarmReciever";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onRecieve");
		
		int notifID = intent.getExtras().getInt("NotifID");
		String title = intent.getExtras().getString("Title");
		String text = intent.getExtras().getString("Text");
		
		Intent serviceIntent = new Intent(context, NotificationService.class);
		
		serviceIntent.putExtra("NotifID", notifID);
		serviceIntent.putExtra("Title", title); 
		serviceIntent.putExtra("Text", text);
		
	    context.startService(serviceIntent);
	}

}