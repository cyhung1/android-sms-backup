package com.kelviomatias.smsbackup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;

import com.kelviomatias.smsbackup.model.Data;
import com.kelviomatias.smsbackup.model.Sms;
import com.kelviomatias.smsbackup.util.SmsUtil;

public class SearchActivity extends Activity {

	private static final int REQUEST_CODE = 1234;

	private LocalActivityManager mLocalActivityManager;

	public static final String SEARCH_MODE = "SEARCH_MODE";
	
	public static final int MODE_PHONE = 0;
	
	public static final int MODE_BACKUP = 1;
	
	public static final int MODE_ALL = 2;
	
	private int mode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupActionBar();

		this.mode = this.getIntent().getIntExtra(SEARCH_MODE, MODE_ALL);
		
		List searchParams = (List) this.getIntent().getSerializableExtra(ArrayList.class.getName());
		
		
		// Disable button if no recognition service is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		Button speakButton = ((Button) findViewById(R.id.activitySearchTalkButton));
		if (activities.size() == 0) {
			speakButton.setEnabled(false);
		} else {
			speakButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startVoiceRecognitionActivity();
				}
			});
		}

		((Button) findViewById(R.id.activitySearchTypeButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						showSearchDialog();

					}
				});

		findViewById(R.id.activitySearchProgressBar).setVisibility(
				View.INVISIBLE);
		findViewById(R.id.activitySearchTabhost).setVisibility(View.INVISIBLE);
		findViewById(R.id.activitySearchNoResults).setVisibility(View.INVISIBLE);

		if (searchParams != null) {
			List<String> l = new ArrayList<String>(searchParams.size());
			for (Object param : searchParams) {
				l.add(param.toString());
			}
			this.doSearch(l);
			
		} else if (mode != MODE_ALL) {
			List<String> l = new ArrayList<String>();
			this.doSearch(l);
		}
		
	}

	/**
	 * Fire an intent to start the voice recognition activity.
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.tell_the_content_of_a_message));
		startActivityForResult(intent, REQUEST_CODE);
	}

	private void showSearchDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(SearchActivity.this);

		alert.setTitle(getString(R.string.app_name));
		alert.setMessage(getString(R.string.please_type_the_message_content));

		// Set an EditText view to get user input
		final EditText input = new EditText(SearchActivity.this);

		alert.setView(input);

		alert.setPositiveButton(getString(R.string.search),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String text = input.getText().toString();
						if (text == null || text.equals("")) {
							return;
						}
						ArrayList<String> l = new ArrayList<String>();
						l.add(text);
						makeSearch(l);
						
					}
				});

		alert.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						dialog.dismiss();
					}
				});

		alert.show();
	}

	private void makeSearch(ArrayList<String> params) {
		startActivity(new Intent()
		.setClass(SearchActivity.this, SearchActivity.class)
		.putExtra(SearchActivity.SEARCH_MODE, mode)
		.putExtra(ArrayList.class.getName(), params));
		finish();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			// Populate the wordsList with the String values the recognition
			// engine thought it heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			// wordsList.setAdapter(new ArrayAdapter<String>(this,
			// android.R.layout.simple_list_item_1, matches));
			makeSearch(matches);			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void doSearch(final List<String> args) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				findViewById(R.id.activitySearchProgressBar).setVisibility(
						View.VISIBLE);
				findViewById(R.id.activitySearchTabhost).setVisibility(View.INVISIBLE);
				findViewById(R.id.activitySearchNoResults).setVisibility(View.INVISIBLE);
			}
		});

		// TODO: Async search
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				final Map<String, List<Sms>> results = getSearchResults(args);

				
				
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						if (results.keySet().isEmpty()) {
							findViewById(R.id.activitySearchNoResults).setVisibility(View.VISIBLE);
							findViewById(R.id.activitySearchProgressBar).setVisibility(
									View.INVISIBLE);
							return;
						}
						
						mLocalActivityManager = new LocalActivityManager(
								SearchActivity.this, false);

						TabHost tabHost = (TabHost) findViewById(R.id.activitySearchTabhost);
						tabHost.setVisibility(View.VISIBLE);
						tabHost.setup(mLocalActivityManager);
						mLocalActivityManager.dispatchCreate(new Bundle());
						TabHost.TabSpec spec;

						String phoneKey = getString(R.string.phone);
						
						if (results.containsKey(phoneKey)) {
							spec = tabHost
									.newTabSpec(phoneKey)
									.setIndicator(
											phoneKey,
											getResources().getDrawable(
													R.drawable.ic_launcher))
									.setContent(
											new Intent().setClass(
													SearchActivity.this,
													SearchResultActivity.class)
													.putExtra(java.util.ArrayList.class.getName(), (ArrayList) results.get(phoneKey)));
							tabHost.addTab(spec);
						}
						
						for (String key : results.keySet()) {
							
							if (key.equals(phoneKey)) {
								continue;
							}
							
							spec = tabHost
									.newTabSpec(key)
									.setIndicator(
											key,
											getResources().getDrawable(
													R.drawable.ic_launcher))
									.setContent(
											new Intent().setClass(
													SearchActivity.this,
													SearchResultActivity.class)
													.putExtra(java.util.ArrayList.class.getName(), (ArrayList) results.get(key)));
							tabHost.addTab(spec);
						}

						final HorizontalScrollView h = (HorizontalScrollView) findViewById(R.id.hScrollView);

						h.post(new Runnable() {

							@Override
							public void run() {

								h.postDelayed(new Runnable() {

									@Override
									public void run() {
										h.fullScroll(HorizontalScrollView.FOCUS_RIGHT);

										h.postDelayed(new Runnable() {

											@Override
											public void run() {
												h.fullScroll(HorizontalScrollView.FOCUS_LEFT);
											}
										}, 1500);

									}
								}, 1000);

							}
						});
						findViewById(R.id.activitySearchProgressBar)
								.setVisibility(View.INVISIBLE);
						findViewById(android.R.id.tabs).setVisibility(
								View.VISIBLE);
					}

				});

				return null;
			}

		}.execute();

	}

	private Map<String, List<Sms>> getSearchResults(List<String> args) {
		Map<String, List<Sms>> results = new HashMap<String, List<Sms>>();

		Data d = null;
		if (mode == MODE_ALL || mode == MODE_PHONE) {
			//Phone
			d = SmsUtil.readSmsConversationsFromPhone(SearchActivity.this, args);
			
			if (!d.getSmsList().isEmpty()) {
				
				String key = getString(R.string.phone); 
				results.put(key, new ArrayList<Sms>());
				results.get(key).addAll(d.getSmsList());			
				
			}
		}
		
		if (mode == MODE_ALL || mode == MODE_BACKUP) {
			//Backup
			File backupDir = new File(Environment.getExternalStorageDirectory(), "SMSBackup");
			File[] files = backupDir.listFiles();
			if (files != null) {
				
				for (File f : files) {
					
					if (f.getName().endsWith(".xml")) {
						
						String key = f.getName().substring(0,  f.getName().lastIndexOf("."));
						
						try {
							
							d = SmsUtil.readSmsFromBackupFile(f, args);						
							
							if (!d.getSmsList().isEmpty()) {
								
								results.put(key, d.getSmsList());
								
							}
							
							
						} catch (Exception e) {						
							
						}
						
					}
					
				}
				
			}
		}
		
		
		return results;
	}
}
