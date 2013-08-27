package com.JaredMavis.Utils;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import com.JaredMavis.boxedmeeting.R;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Handles the changing of the preferences found in res/xml/preferences
 * 
 * @author Jared Mavis
 * 
 */
public class PreferenceHandler extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Get the custom preference
		Preference customPref = (Preference) findPreference("saveAndReturn");
		customPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						Toast.makeText(getBaseContext(),
								"Your settings have been saved",
								Toast.LENGTH_LONG).show();

						finish();
						return true;
					}

				});
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
}