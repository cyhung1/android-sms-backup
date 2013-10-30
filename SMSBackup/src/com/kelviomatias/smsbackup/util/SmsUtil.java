package com.kelviomatias.smsbackup.util;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.EditText;

import com.kelviomatias.smsbackup.BackupActivity;
import com.kelviomatias.smsbackup.MainActivity;
import com.kelviomatias.smsbackup.R;
import com.kelviomatias.smsbackup.SettingsActivity;
import com.kelviomatias.smsbackup.model.Data;
import com.kelviomatias.smsbackup.model.Sms;

public class SmsUtil {

	private static final Uri SENT_MSGS_CONTENT_PROVIDER = Uri
			.parse("content://sms/sent");
	private static final Uri INBOX_MSGS_CONTET_PROVIDER = Uri
			.parse("content://sms/inbox");

	public static void startSmsActivityWithNumber(Context context, String number) {

		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts(
				"sms", number, null)));
	}

	public static Data readSmsConversationsFromPhone(Context context,
			List<String> bodyContentList) {
		Data data = new Data();

		Map<String, String> contactNameMap = new HashMap<String, String>();

		// INBOX
		Uri uriSMSURI = INBOX_MSGS_CONTET_PROVIDER;
		Cursor c = context.getContentResolver().query(uriSMSURI, null, null,
				null, null);

		// Read the sms data and store it in the list
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {

				String body = c.getString(c.getColumnIndexOrThrow("body"))
						.toString().toLowerCase();

				for (String bodyContent : bodyContentList) {

					if (body.contains(bodyContent.toLowerCase())) {

						Sms s = getSms(context, contactNameMap, c);

						data.getSmsList().add(s);
						break;
					}

				}

				c.moveToNext();
			}
		}
		c.close();

		// OUTBOX
		uriSMSURI = SENT_MSGS_CONTENT_PROVIDER;
		c = context.getContentResolver().query(uriSMSURI, null, null, null,
				null);

		// Read the sms data and store it in the list
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {

				String body = c.getString(c.getColumnIndexOrThrow("body"))
						.toString().toLowerCase();

				for (String bodyContent : bodyContentList) {

					if (body.contains(bodyContent.toLowerCase())) {

						Sms s = getSms(context, contactNameMap, c);

						data.getSmsList().add(s);
						break;
					}

				}

				c.moveToNext();
			}
		}
		c.close();
		return data;
	}

	public static Data readSmsConversationsFromPhone(Context context) {

		Map<String, String> contactNameMap = new HashMap<String, String>();

		Data data = new Data();
		Uri uriSMSURI = INBOX_MSGS_CONTET_PROVIDER;
		final String[] projection = new String[] { Sms._ID, Sms.PROTOCOL,
				Sms.ADDRESS, Sms.DATE, Sms.TYPE, Sms.SUBJECT, Sms.BODY,
				Sms.SERVICE_CENTER, Sms.READ, Sms.STATUS, Sms.LOCKED,
				Sms.DATE_SENT,
				// Sms.CONTACT_NAME,
				Sms.THREAD_ID, Sms.DELIVERY_DATE };

		Cursor c = context.getContentResolver().query(uriSMSURI, projection,
				null, null, null);

		// Read the sms data and store it in the list
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {

				Sms s = getSms(context, contactNameMap, c);

				data.getSmsList().add(s);

				c.moveToNext();
			}
		}
		c.close();

		uriSMSURI = SENT_MSGS_CONTENT_PROVIDER;
		c = context.getContentResolver().query(uriSMSURI, projection, null,
				null, null);

		// Read the sms data and store it in the list
		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {

				Sms s = getSms(context, contactNameMap, c);

				data.getSmsList().add(s);

				c.moveToNext();
			}
		}
		c.close();

		return data;
	}

	private static Sms getSms(Context context, Map<String, String> contactMap,
			Cursor c) {
		Sms s = new Sms();
		String address = c.getString(c.getColumnIndexOrThrow("address"))
				.toString();
		s.setAddress(address);

		if (!contactMap.containsKey(address)) {
			// GET CONTACT NAME
			Uri lookupUri = Uri.withAppendedPath(
					PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
			Cursor contactCursor = context
					.getContentResolver()
					.query(lookupUri,
							new String[] {

							Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.Data.DISPLAY_NAME_PRIMARY
									: ContactsContract.Data.DISPLAY_NAME },
							null, null, null);
			String displayName = null;
			try {

				contactCursor.moveToFirst();
				displayName = contactCursor.getString(0);

			} catch (Exception e) {

				displayName = null;

			} finally {
				contactCursor.close();
			}
			contactMap.put(address, displayName);
		}

		s.setContactName(contactMap.get(address));

		s.setDate(c.getLong(c.getColumnIndexOrThrow("date")));
		s.setDateSent(c.getLong(c.getColumnIndexOrThrow("date_sent")));
		s.setProtocol(c.getInt(c.getColumnIndexOrThrow("protocol")));
		s.setRead(c.getString(c.getColumnIndexOrThrow("read")));
		// s.setScToa(c.getString(c.getColumnIndexOrThrow("sc_toa")));
		// s.setToa(c.getString(c.getColumnIndexOrThrow("toa")));
		s.setStatus(c.getString(c.getColumnIndexOrThrow("status")));
		s.setType(c.getInt(c.getColumnIndexOrThrow("type")));

		s.setSubject(c.getString(c.getColumnIndexOrThrow("subject")));
		s.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
		s.setServiceCenter(c.getString(c
				.getColumnIndexOrThrow("service_center")));
		s.setLocked(c.getString(c.getColumnIndexOrThrow("locked")));
		s.setDeliveryDate(c.getString(c.getColumnIndexOrThrow("delivery_date")));
		s.setThreadId(c.getString(c.getColumnIndexOrThrow(Sms.THREAD_ID)));

		return s;
	}

	public static Data readSmsFromBackupFile(File file, List<String> bodyContent)
			throws Exception {

		Data d = new Persister().read(Data.class, file);
		sms: for (int i = 0; i < d.getSmsList().size(); i++) {

			for (String c : bodyContent) {

				if (d.getSmsList().get(i).getBody().toLowerCase()
						.contains(c.toLowerCase())) {
					continue sms;
				}

			}
			d.getSmsList().remove(i);

		}
		return d;
	}

	public static Data readSmsFromBackupFile(File file) throws Exception {

		return new Persister().read(Data.class, file);
	}

	public static void backup(final Context context, final Data data)
			throws Exception {

		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle(context.getString(R.string.app_name));
		alert.setMessage(context
				.getString(R.string.please_insert_the_backup_filename));

		// Set an EditText view to get user input
		final EditText input = new EditText(context);

		File f = SmsUtil.getBackupFile(context);

		input.setText(SmsUtil.getBackupName(f));
		alert.setView(input);

		alert.setPositiveButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						// Progress dialog
						dialog.dismiss();
						final ProgressDialog progressBar = new ProgressDialog(
								context);
						progressBar.setCancelable(false);
						progressBar.setMessage(context
								.getString(R.string.loading));
						progressBar
								.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						// progressBar.setProgress(0);
						// progressBar.setMax(data.getSmsList().size());
						progressBar.setIndeterminate(true);
						progressBar.show();

						new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... params) {

								try {
									File out = getBackupFile(context, input
											.getText().toString());
									if (!out.exists()) {
										out.mkdirs();
										out.delete();
										out.createNewFile();
									}
									new Persister().write(data, out);

								} catch (Exception e) {
									e.printStackTrace();
								}

								return null;

							}

							@Override
							protected void onPostExecute(Void result) {
								super.onPostExecute(result);

								progressBar.cancel();

								AlertDialog.Builder alert = new AlertDialog.Builder(
										context);

								alert.setTitle(context
										.getString(R.string.app_name));
								alert.setMessage(context
										.getString(R.string.backed_up_successfully));

								alert.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {

												dialog.dismiss();

											}
										});
								alert.show();

							}

						}.execute();

					}
				});

		alert.setNegativeButton(context.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						dialog.dismiss();
					}
				});

		alert.show();

	}

	public static String getBackupName(File file) {
		return file.getName().substring(0, file.getName().lastIndexOf("."));
	}

	public static File getBackupFile(Context context, String backupName) {
		return new File(getAppExternalStorageDir(context), backupName + ".xml");
	}

	public static File getAppExternalStorageDir(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return new File(Environment.getExternalStorageDirectory(),
				sp.getString(SettingsActivity.BACKUP_DIRECTORY_KEY,
						SettingsActivity.DEFAULT_BACKUP_DIRECTORY));
	}

	public static File getBackupFile(Context context) {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);

		return new File(getAppExternalStorageDir(context), "sms-" + year
				+ month + day + hour + minute + second + ".xml");
	}

	public static void restore(final Activity context, final Data data) {

		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// create and display a new ProgressBarDialog
				final ProgressDialog progressBar = new ProgressDialog(context);
				progressBar.setCancelable(false);
				progressBar.setMessage(context.getString(R.string.restoring));
				progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressBar.setProgress(0);
				progressBar.setMax(data.getSmsList().size());
				progressBar.show();

				new AsyncTask<Void, Integer, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						int cont = 0;

						for (Sms sms : data.getSmsList()) {
							addSmsMessage(context, sms);
							cont++;
							this.publishProgress(cont);
						}

						return null;
					}

					@Override
					protected void onProgressUpdate(Integer... values) {
						super.onProgressUpdate(values);
						final int value = values[0];
						context.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								progressBar.setProgress(value);

							}
						});
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						context.runOnUiThread(new Runnable() {

							public void run() {
								progressBar.cancel();
								AlertDialog.Builder alert = new AlertDialog.Builder(
										context);

								alert.setTitle(context
										.getString(R.string.app_name));
								alert.setMessage(context
										.getString(R.string.restored_with_success));
								alert.setPositiveButton(
										context.getString(R.string.ok),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
											}
										});
								alert.show();
							}

						});
					}

				}.execute();
			}
		});

	}

	public static void addSmsMessage(Context context, Sms sms) {

		ContentValues smsContent = getContentValues(sms);
		ContentResolver contentResolver = context.getContentResolver();

		if (sms.getType() == Sms.SMS_INBOX) {

			Uri uri = contentResolver.insert(INBOX_MSGS_CONTET_PROVIDER,
					smsContent);
			

		} else if (sms.getType() == Sms.SMS_OUTBOX) {

			contentResolver.insert(SENT_MSGS_CONTENT_PROVIDER, smsContent);
		}

	}

	public static ContentValues getContentValues(Sms sms) {
		ContentValues smsValues = new ContentValues();
		smsValues.put(Sms.ADDRESS, sms.getAddress());
		// smsValues.put(Sms.APP_ID, sms.getAppId());
		smsValues.put(Sms.BODY, sms.getBody());
		// smsValues.put(Sms.CONTACT_NAME, sms.getContactName());
		smsValues.put(Sms.DATE, sms.getDate());
		smsValues.put(Sms.DATE_SENT, sms.getDateSent());
		smsValues.put(Sms.DELIVERY_DATE, sms.getDeliveryDate());
		smsValues.put(Sms.LOCKED, sms.getLocked());
		smsValues.put(Sms.PROTOCOL, sms.getProtocol());
		smsValues.put(Sms.READ, sms.getRead());
		smsValues.put(Sms.SERVICE_CENTER, sms.getServiceCenter());
		smsValues.put(Sms.SUBJECT, sms.getSubject());
		smsValues.put(Sms.TYPE, sms.getType());
		smsValues.put(Sms.THREAD_ID, sms.getThreadId());
		// smsValues.put(Sms.TOA, sms.getToa());
		// smsValues.put(Sms.SC_TOA, sms.getStatus());
		smsValues.put(Sms.STATUS, sms.getStatus());
		return smsValues;
	}

	public static void showDeletePhoneSmsWizard(final Activity context) {

		String[] items = new String[] {
				context.getString(R.string.backup_messages),
				context.getString(R.string.delete_messages),
				context.getString(R.string.cancel) };

		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		ab.setTitle(context.getString(R.string.app_name) + " - " + context.getString(R.string.options));
		
		//ab.setMessage(context.getString(R.string.backup_sms_messages_before_delete));
		
		ab.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int choice) {

				switch (choice) {
				case 0: // BACKUP 
					
					context.startActivity(new Intent().setClass(context, BackupActivity.class));
					
					break;
				case 1: // DELETE
					deletePhoneMessages(context);
					break;
				default: // Cancel
					break;
				}

			}

		});
		ab.show();
	}
		

	
	public static void deletePhoneMessages(final Activity context) {
		
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setIndeterminate(true);
		pd.setCancelable(false);
		pd.setTitle(context.getString(R.string.app_name));
		pd.setMessage(context.getString(R.string.deleting));
		pd.show();
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				//context.getContentResolver().delete(INBOX_MSGS_CONTET_PROVIDER, null, null);
				//context.getContentResolver().delete(SENT_MSGS_CONTENT_PROVIDER, null, null);
				
				context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {

						pd.cancel();
						AlertDialog.Builder alert = new AlertDialog.Builder(context);

						alert.setTitle(context.getString(R.string.app_name));
						alert.setMessage(context
								.getString(R.string.messages_successfully_deleted));
						alert.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						alert.show();
						
					}
				});
				
				return null;
			}
			
		}.execute();
				
	}
	/**
	 * //imports
	 * 
	 * public class SentSmsLogger extends Service {
	 * 
	 * @Override public void onStart(Intent intent, int startId) {
	 *           addMessageToSentIfPossible(intent); stopSelf(); }
	 * 
	 *           private void addMessageToSentIfPossible(Intent intent) { if
	 *           (intent != null) { String telNumber =
	 *           intent.getStringExtra("telNumber"); String messageBody =
	 *           intent.getStringExtra("messageBody"); if (telNumber != null &&
	 *           messageBody != null) { addMessageToSent(telNumber,
	 *           messageBody); } } }
	 * 
	 *           private void addMessageToSent(String telNumber, String
	 *           messageBody) { ContentValues sentSms = new ContentValues();
	 *           sentSms.put(TELEPHON_NUMBER_FIELD_NAME, telNumber);
	 *           sentSms.put(MESSAGE_BODY_FIELD_NAME, messageBody);
	 * 
	 *           ContentResolver contentResolver = getContentResolver();
	 *           contentResolver.insert(SENT_MSGS_CONTET_PROVIDER, sentSms); }
	 * @Override public IBinder onBind(Intent intent) { return null; }
	 * 
	 *           }
	 */

}
