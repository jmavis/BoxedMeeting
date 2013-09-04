package com.JaredMavis.Notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import com.JaredMavis.Utils.AlarmReciever;
import com.JaredMavis.Utils.Utils;
import com.JaredMavis.boxedmeeting.R;

public class NotificationSender {
	static public void setNotifications(long time, boolean shouldNotifyAtWarningTime, Context context){
		long timeToGoOff = System.currentTimeMillis() + time;
		if (shouldNotifyAtWarningTime && time > Utils.warningTimeInMs(context)) {
			setNotification(getWarningIntent(context), context, timeToGoOff - Utils.warningTimeInMs(context));
		} 
		
		setNotification(getEndingIntent(context), context, timeToGoOff);
	}
	
	static private void setNotification(PendingIntent pendingIntent, Context context, long time) {
        AlarmManager mAlarmManager = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));
        
		mAlarmManager.set(AlarmManager.RTC, time, pendingIntent);
	}
	
	// will cancel all alarms with the same pending intent that goes to this app and any notifications that are showing
	static public void cancelNotificationsAndAlarms(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent displayIntentForEndingAlarm = getEndingIntent(context);
        PendingIntent displayIntentForWarningAlarm = getWarningIntent(context);

        // cancels all pending intents that we made with the different ids
		alarmManager.cancel(displayIntentForEndingAlarm);  
		alarmManager.cancel(displayIntentForWarningAlarm); 
		
		cancelNotifications(context);
	}
	
	static private PendingIntent getBaseIntent(String title, String text, int id, Context context){
		//---PendingIntent to launch activity when the alarm triggers---
        Intent i = new Intent(context, AlarmReciever.class);

        Resources resources = context.getResources();
        
        i.putExtra(resources.getString(R.string.Key_NotificaitonID), id);
        i.putExtra(resources.getString(R.string.Key_NotificaitionTitle), title); 
        i.putExtra(resources.getString(R.string.Key_NotificaitionText), text);
        
	    return (PendingIntent.getBroadcast(context, id, i, 0));
	}
	
	static private PendingIntent getWarningIntent(Context context){
		return (getBaseIntent(Integer.toString(Utils.warningTimeInMins(context)) + " " + context.getString(R.string.String_WarningNotificationTitle),
				context.getString(R.string.String_WarningNotificationText), 
				context.getResources().getInteger(R.integer.Value_Meeting5MinNotificationID), 
				context));
	}
	
	static private PendingIntent getEndingIntent(Context context){
		return (getBaseIntent(context.getString(R.string.String_EndNotificationTitle), 
		        context.getString(R.string.String_EndNotificationText),
		        context.getResources().getInteger(R.integer.Value_MeetingEndNotificationID), 
		        context));
	}
	
	static public void cancelNotifications(Context context){
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}
}
