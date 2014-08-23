package com.tekpub.player;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences_about);
		
		findPreference("tos").setOnPreferenceClickListener(urlClick);
		findPreference("privacy").setOnPreferenceClickListener(urlClick);
		
		// Set Version Number in Preference Screen
		try {
			findPreference("version").setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			// Couldn't find the version for some reason. 
			e.printStackTrace();
		}
		
	}
	
	Preference.OnPreferenceClickListener urlClick = new Preference.OnPreferenceClickListener() {

		public boolean onPreferenceClick(Preference preference) {
			Intent i = null;
			if (preference.getKey().equals("tos"))
				i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tekpub.com/terms"));
			if (preference.getKey().equals("privacy"))
				i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tekpub.com/terms"));

			if (i != null)
				startActivity(i);
			return false;
		}
	};

}
