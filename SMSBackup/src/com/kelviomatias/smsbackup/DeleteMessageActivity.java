package com.kelviomatias.smsbackup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kelviomatias.smsbackup.model.Backup;
import com.kelviomatias.smsbackup.util.SmsUtil;

public class DeleteMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_message);
		setupActionBar();
		refreshBackupList();
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
		getMenuInflater().inflate(R.menu.delete_message, menu);
		return true;
	}

	private void refreshBackupList() {
		List<Backup> l = new ArrayList<Backup>();
		File backupDir = SmsUtil.getAppExternalStorageDir(this);
		File[] files = backupDir.listFiles();
		if (files != null) {

			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if (f.isFile() && f.getName().endsWith(".xml")) {
					l.add(new Backup(f));
				}
			}
			Backup[] items = new Backup[l.size()];
			for (int i = 0; i < items.length; i++) {
				items[i] = l.get(i);
			}
			BackupAdapter adapter = new BackupAdapter(this,
					R.layout.activity_restore_list_item, items);

			ListView list = (ListView) findViewById(R.id.activityDeleteListView);
			list.setAdapter(adapter);

		}

		if (l.isEmpty()) {

			findViewById(R.id.activityDeleteMessageEmptyTextView)
					.setVisibility(View.VISIBLE);

		} else {
			
			findViewById(R.id.activityDeleteMessageEmptyTextView)
					.setVisibility(View.INVISIBLE);
		}
	}

	class BackupAdapter extends ArrayAdapter<Backup> {
		private Activity myContext;

		private Backup[] datas;

		public BackupAdapter(Context context, int textViewResourceId,
				Backup[] objects) {
			super(context, textViewResourceId, objects);

			myContext = (Activity) context;
			datas = objects;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = myContext.getLayoutInflater();

			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.activity_restore_list_item, null);
			}

			TextView name = (TextView) convertView
					.findViewById(R.id.activityRestoreListItemBackupName);
			TextView createdAt = (TextView) convertView
					.findViewById(R.id.activityRestoreListItemBackupCreatedAt);
			Backup b = this.datas[position];

			name.setText(b.getName());
			createdAt.setText(java.text.DateFormat.getDateTimeInstance(
					java.text.DateFormat.FULL, java.text.DateFormat.FULL)
					.format(b.getCreatedAt()));
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO: Restore backup

					AlertDialog.Builder alert = new AlertDialog.Builder(
							DeleteMessageActivity.this);

					alert.setTitle(getString(R.string.app_name));
					alert.setMessage(getString(R.string.deleteBackup));

					alert.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();

									File f = datas[position].getFile();
									if (f.delete()) {
										refreshBackupList();
										showDeletedMessage(datas[position]);

									}

								}
							}).setNegativeButton(getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									dialog.dismiss();

								}
							});
					alert.show();

				}
			});
			return convertView;
		}
	}

	public void showDeletedMessage(final Backup backup) {
		AlertDialog.Builder alert = new AlertDialog.Builder(
				DeleteMessageActivity.this);

		alert.setTitle(getString(R.string.app_name));
		alert.setMessage(getString(R.string.backupDeleted));

		alert.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();

					}
				});
		alert.show();
	}
}
