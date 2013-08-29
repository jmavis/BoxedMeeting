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
			setNotification(Utils.warningTimeInMins(context) + context.getString(R.string.String_WarningNotificationTitle), 
							"", 
							timeToGoOff - Utils.warningTimeInMs(context), 
							context.getResources().getInteger(R.integer.Value_Meeting5MinNotificationID), 
							context);
		} 
		
		setNotification(context.getString(R.string.String_EndNotificationTitle), 
				        context.getString(R.string.String_EndNotificationText), 
				        timeToGoOff, 
				        context.getResources().getInteger(R.integer.Value_MeetingEndNotificationID), 
				        context);
	}
	
	static private void setNotification(String title, String text, long time, int id, Context context) {
        //---PendingIntent to launch activity when the alarm triggers---
        Intent i = new Intent(context, AlarmReciever.class);

        Resources resources = context.getResources();
        
        i.putExtra(resources.getString(R.string.Key_NotificaitonID), id);
        i.putExtra(resources.getString(R.string.Key_NotificaitionTitle), title); 
        i.putExtra(resources.getString(R.string.Key_NotificaitionText), text);

        AlarmManager mAlarmManager = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));
        
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, i, 0);
        
		mAlarmManager.set(AlarmManager.RTC, time, pendingIntent);
	}
	
	// will cancel all alarms with the same pending intent that goes to this app and any notifications that are showing
	static public void cancelNotificationsAndAlarms(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, NotificationService.class);
        
        int endingID = context.getResources().getInteger(R.integer.Value_MeetingEndNotificationID);
        int warningID = context.getResources().getInteger(R.integer.Value_Meeting5MinNotificationID);

        PendingIntent displayIntentForEndingAlarm = PendingIntent.getActivity(context, endingID, i, 0);
        PendingIntent displayIntentForWarningAlarm = PendingIntent.getActivity(context, warningID, i, 0);

        // cancels all pending intents that we made with the different ids
		alarmManager.cancel(displayIntentForEndingAlarm);  
		alarmManager.cancel(displayIntentForWarningAlarm); 
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(endingID);
		notificationManager.cancel(warningID);
	}
}
