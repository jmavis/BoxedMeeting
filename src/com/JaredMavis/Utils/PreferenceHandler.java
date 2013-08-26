package com.JaredMavis.Utils;

import com.JaredMavis.boxedmeeting.MainActivity;
import com.JaredMavis.boxedmeeting.R;
import com.JaredMavis.boxedmeeting.R.xml;
import com.google.analytics.tracking.android.EasyTracker;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PreferenceHandler extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Get the custom preference
		Preference customPref = (Preference) findPreference("saveAndReturn");
		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {
						Toast.makeText(getBaseContext(), "Your settings have been saved", Toast.LENGTH_LONG).show();
						
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

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { MenuInflater
	 * inflater = getMenuInflater(); inflater.inflate(R.menu.menu, menu); return
	 * true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case 0: startActivity(new Intent(this,
	 * OptionsPage.class)); return true; } return false; }
	 */
}