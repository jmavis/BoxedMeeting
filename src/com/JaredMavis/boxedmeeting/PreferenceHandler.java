package com.JaredMavis.boxedmeeting;

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
						
						Intent myIntent = new Intent(getBaseContext(), MainActivity.class);

						startActivity(myIntent);
						return true;
					}

				});
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