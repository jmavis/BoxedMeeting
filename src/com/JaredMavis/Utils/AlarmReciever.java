package com.JaredMavis.Utils;

import com.JaredMavis.Notifications.NotificationService;
import com.JaredMavis.boxedmeeting.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

public class AlarmReciever extends BroadcastReceiver {
	@SuppressWarnings("unused")
	private static final String TAG = "AlarmReciever";
	
	/**
	 * Called when an alarm goes off. It will read in and transfer the notification data to the service and then start it up.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Resources resources = context.getResources();
		
		int notifID = intent.getExtras().getInt(resources.getString(R.string.Key_NotificaitonID));
		String title = intent.getExtras().getString(resources.getString(R.string.Key_NotificaitionTitle));
		String text = intent.getExtras().getString(resources.getString(R.string.Key_NotificaitionText));
		
		Intent serviceIntent = new Intent(context, NotificationService.class);
		
		serviceIntent.putExtra(resources.getString(R.string.Key_NotificaitonID), notifID);
		serviceIntent.putExtra(resources.getString(R.string.Key_NotificaitionTitle), title); 
		serviceIntent.putExtra(resources.getString(R.string.Key_NotificaitionText), text);
		
	    context.startService(serviceIntent);
	}
}