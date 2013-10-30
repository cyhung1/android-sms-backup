package com.kelviomatias.smsbackup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.kelviomatias.smsbackup.model.Data;
import com.kelviomatias.smsbackup.model.Sms;
import com.kelviomatias.smsbackup.util.SmsUtil;

public class BackupActivity extends Activity {

	private Button button;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backup);
		setupActionBar();

		findViewsById();

		button.setText(getString(R.string.backup_selected));
		
		this.refreshContactList();
		
		

	}
	
	private void refreshContactList() {
		
		findViewById(R.id.activityBackupListView).setVisibility(View.INVISIBLE);
		findViewById(R.id.activityBackupProgressBar).setVisibility(View.VISIBLE);
		findViewById(R.id.activityBackupEmptyContactsTextView).setVisibility(View.INVISIBLE);
		findViewById(R.id.activityBackupBackupButton).setEnabled(false);
		
		new AsyncTask<Void, Void, Map<String, List<Sms>>>() {

			@Override
			protected Map<String, List<Sms>> doInBackground(Void... params) {
				return getSmsMap();
			}

			@Override
			protected void onPostExecute(final Map<String, List<Sms>> result) {
				super.onPostExecute(result);
							
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
												
						
						if (result.keySet().isEmpty()) {
							findViewById(R.id.activityBackupEmptyContactsTextView).setVisibility(View.VISIBLE);
							findViewById(R.id.activityBackupListView).setVisibility(View.INVISIBLE);
							findViewById(R.id.activityBackupProgressBar).setVisibility(View.INVISIBLE);
							return;
						}
						findViewById(R.id.activityBackupBackupButton).setEnabled(true);
						
						Set<String> contactNumbers = new TreeSet<String>();

						for (String key : result.keySet()) {
							contactNumbers.add(key);
						}


						String[] data = new String[contactNumbers.size()];
						int i = 0;
						for (String contactNumber : contactNumbers) {
							data[i] = contactNumber;
							i++;
						}

						adapter = new ArrayAdapter<String>(BackupActivity.this,
								android.R.layout.simple_list_item_multiple_choice, data);
						listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
						listView.setAdapter(adapter);
						for (i = 0; i < contactNumbers.size(); i++) {
							listView.setItemChecked(i, true);
						}

						
						button.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								SparseBooleanArray checked = listView.getCheckedItemPositions();

								final ArrayList<String> selectedItems = new ArrayList<String>();
								for (int i = 0; i < checked.size(); i++) {
									// Item position in adapter
									int position = checked.keyAt(i);
									// Add sport if it is checked i.e.) == TRUE!
									if (checked.valueAt(i)) {
										selectedItems.add(adapter.getItem(position));
									}
								}
								//Grava arquivo de backup
								final Data d = new Data();
								for (String key : selectedItems) {
									d.getSmsList().addAll(result.get(key));
								}
								try {
									SmsUtil.backup(BackupActivity.this, d);
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						});
						findViewById(R.id.activityBackupListView).setVisibility(View.VISIBLE);
						findViewById(R.id.activityBackupProgressBar).setVisibility(View.INVISIBLE);
					}
				});
			}
			
		}.execute();
		
		

		
	}

	private void findViewsById() {
		listView = (ListView) findViewById(R.id.activityBackupListView);
		button = (Button) findViewById(R.id.activityBackupBackupButton);
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
		getMenuInflater().inflate(R.menu.backup, menu);
		return true;
	}

	public Map<String, List<Sms>> getSmsMap() {
		Map<String, List<Sms>> map = new HashMap<String, List<Sms>>();

		Data d = SmsUtil.readSmsConversationsFromPhone(BackupActivity.this);
		for (Sms s : d.getSmsList()) {

			String key = (s.getContactName() == null ? "?" : s.getContactName()) + " - " + s.getAddress();
			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<Sms>());
			}
			map.get(key).add(s);
		}
		
		return map;		
	}
}
