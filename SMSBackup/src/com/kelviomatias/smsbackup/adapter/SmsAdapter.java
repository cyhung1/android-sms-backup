package com.kelviomatias.smsbackup.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kelviomatias.smsbackup.R;
import com.kelviomatias.smsbackup.model.Data;
import com.kelviomatias.smsbackup.model.Sms;
import com.kelviomatias.smsbackup.util.SmsUtil;

public class SmsAdapter extends ArrayAdapter<Sms> {
	private Activity myContext;

	private Sms[] datas;

	public SmsAdapter(Context context, int textViewResourceId, Sms[] objects) {
		super(context, textViewResourceId, objects);

		myContext = (Activity) context;
		datas = objects;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = myContext.getLayoutInflater();

		
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.activity_search_result_list_item, null);
		}

		TextView contactName = (TextView) convertView
				.findViewById(R.id.activitySearchResultListViewContactName);
		TextView smsDate = (TextView) convertView
				.findViewById(R.id.activitySearchResultListItemSmsDate);
		TextView smsText = (TextView) convertView
				.findViewById(R.id.activitySearchResultListItemSmsText);

		Sms s = datas[position];
		if (s.getContactName() == null || s.getContactName().equals("")) {
			contactName.setText(getContext().getString(R.string.unknown));
		} else {
			contactName.setText(s.getContactName());
		}

		smsDate.setText(java.text.DateFormat.getDateTimeInstance(
				java.text.DateFormat.FULL, java.text.DateFormat.FULL).format(
				s.getDate()));
		smsText.setText(s.getBody());

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String[] items = new String[] {
						getContext().getString(R.string.backup_message),
						getContext().getString(R.string.view),
						getContext().getString(R.string.share),
						getContext().getString(R.string.copy_for_transfer_area),
						getContext().getString(R.string.mark_number),
						getContext().getString(R.string.send_new_message) };
				AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
				ab.setTitle(getContext().getString(R.string.app_name));
				ab.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int choice) {

						switch (choice) {
						case 0: // BACKUP
							Data d = new Data();
							d.getSmsList().add(datas[position]);
							doBackup(d);
							break;
						case 1: // VIEW
							SmsUtil.startSmsActivityWithNumber(getContext(),
									datas[position].getAddress());
							break;
						case 2: // SHARE
							shareSms(datas[position]);
							break;
						case 3: // COPY FOR TRANSFER AREA
							copyToClipboard(datas[position]);
							break;
						case 4: // MARK NUMBER
							markNumber(datas[position]);
							break;
						case 5: // SEND NEW
							SmsUtil.startSmsActivityWithNumber(getContext(),
									datas[position].getAddress());
							break;
						default:
							break;
						}

					}

					private void shareSms(Sms sms) {
						Intent share = new Intent(
								android.content.Intent.ACTION_SEND);
						share.setType("text/plain");
						share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

						// Add data to the intent, the receiving app will
						// decide
						// what to do with it.
						share.putExtra(Intent.EXTRA_SUBJECT, sms.getSubject());
						share.putExtra(Intent.EXTRA_TEXT, sms.getBody());

						getContext().startActivity(
								Intent.createChooser(
										share,
										getContext().getString(
												R.string.select_share_method)));
					}

					private void doBackup(Data data) {
						try {
							SmsUtil.backup(getContext(), data);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					private void copyToClipboard(Sms sms) {
						if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
							android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getContext()
									.getSystemService(Context.CLIPBOARD_SERVICE);
							clipboard.setText(sms.getBody());
						} else {
							copyToClipboardApi11(sms);
						}
						Toast.makeText(
								getContext(),
								getContext()
										.getString(
												R.string.sms_body_copied_for_transfer_area),
								Toast.LENGTH_SHORT).show();
					}

					@TargetApi(Build.VERSION_CODES.HONEYCOMB)
					private void copyToClipboardApi11(Sms sms) {
						android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext()
								.getSystemService(Context.CLIPBOARD_SERVICE);
						android.content.ClipData clip = android.content.ClipData
								.newPlainText("Copied Text", sms.getBody());
						clipboard.setPrimaryClip(clip);
					}

					private void markNumber(Sms sms) {
						Intent intent = new Intent(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:" + sms.getAddress()));
						getContext().startActivity(intent);
					}
				});
				ab.show();

			}
		});

		return convertView;
	}
}
