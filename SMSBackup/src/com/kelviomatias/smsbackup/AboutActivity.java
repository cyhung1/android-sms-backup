package com.kelviomatias.smsbackup;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.view.Menu;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setupActionBar();
		
		
		String version = null;
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (Exception e) {
			
		}
		
		
		((TextView) findViewById(R.id.activityAboutTitle)).setText(getString(R.string.app_name) + (version != null  ? " v" + version : ""));
		((TextView) findViewById(R.id.activityAboutDescriptionText)).setText(getString(R.string.appDescription));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
}
