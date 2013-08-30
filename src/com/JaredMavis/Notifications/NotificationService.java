package com.JaredMavis.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.JaredMavis.boxedmeeting.MainActivity;
import com.JaredMavis.boxedmeeting.R;

/**
 * Ran when a alarm has gone off. Will create a notification in the background then destroy itself
 * @author Jared Mavis
 *
 */
public class NotificationService extends Service {
	private static final String TAG = "NotificationService";
	private WakeLock mWakeLock;

	/**
	 * * Simply return null, since our Service will not be communicating with *
	 * any other components. It just does its work silently.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * * This is where we initialize. We call this when onStart/onStartCommand
	 * is * called by the system. We won't do anything with the intent here, and
	 * you * probably won't, either.
	 */
	private void handleIntent(Intent intent) {
		Log.d(TAG, "handle intent");

		// obtain the wake lock
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mWakeLock.acquire();

		// check the global background data setting
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		if (!cm.getBackgroundDataSetting()) {
			stopSelf();
			return;
		}

		// do the actual work, in a separate thread
		new PollTask().execute(intent);
	}

	private class PollTask extends AsyncTask<Intent, Void, Void> {
		@Override
		protected Void doInBackground(Intent... params) {
			Intent intent = params[0];
			Resources resources = getBaseContext().getResources();
			int notifID = intent.getExtras().getInt(resources.getString(R.string.Key_NotificaitonID));
			String title = intent.getExtras().getString(resources.getString(R.string.Key_NotificaitionTitle));
			String text = intent.getExtras().getString(resources.getString(R.string.Key_NotificaitionText));

			
			Intent i = new Intent(getBaseContext(), MainActivity.class);
			i.putExtra(resources.getString(R.string.Key_NotificaitonID), notifID);

			PendingIntent detailsIntent = PendingIntent.getActivity(
					getBaseContext(), 0, i, 0);

			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			Notification notif = new Notification(R.drawable.ic_launcher,
					title, System.currentTimeMillis());

			CharSequence from = title;
			CharSequence message = text;
			notif.setLatestEventInfo(getBaseContext(), from, message,
					detailsIntent);
			
			Log.d(TAG, "making a notification with title '" + title + "' and text = '" + message + "'");

			notif.vibrate = new long[] { 0, 250, 100, 500 };

			NotificationSender.cancelNotifications(getBaseContext()); // cancel any previous notifications before we send a new one
			nm.notify(notifID, notif);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			stopSelf();
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		handleIntent(intent);
	}

	@Override
	/** * This is called on 2.0+ (API level 5 or higher). Returning * START_NOT_STICKY tells the system to not restart the service if it is * killed because of poor resource (memory/cpu) conditions. */
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		handleIntent(intent);
		return START_NOT_STICKY;
	}

	@Override
	/** * In onDestroy() we release our wake lock. This ensures that whenever the * Service stops (killed for resources, stopSelf() called, etc.), the wake * lock will be released. */
	public void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
	}
}