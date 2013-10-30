package com.kelviomatias.smsbackup;

import com.kelviomatias.smsbackup.util.SmsUtil;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private GridView gridView;
	private static final int BACKUP_BLOCK_POSITION = 0;
	private static final int RESTORE_BLOCK_POSITION = 1;
	private static final int VIEW_SMS_BLOCK_POSITION = 2;
	private static final int VIEW_BACKUP_SMS_BLOCK_POSITION = 3;
	
	private static final int SEARCH_BLOCK_POSITION = 4;
	private static final int DELETE_BACKUP_BLOCK_POSITION = 5;
	//private static final int DELETE_SMS_BLOCK_POSITION = 6;
	private static final int APPS_BLOCK_POSITION = 6;
	private static final int SHARE_BLOCK_POSITION = 7;
	private static final int RATE_BLOCK_POSITION = 8;
	private static final int SETTINGS_BLOCK_POSITION = 9;
	//private static final int HELP_BLOCK_POSITION = 11;
	private static final int ABOUT_BLOCK_POSITION = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		gridView = (GridView) findViewById(R.id.activityMainGridView);

		Item[] items = new Item[11];

		items[BACKUP_BLOCK_POSITION] = new Item(BackupActivity.class, getString(R.string.backup),
				R.drawable.backup, getResources().getColor(
						R.color.activity_backup_background));
		items[RESTORE_BLOCK_POSITION] = new Item(RestoreActivity.class, getString(R.string.restore),
				R.drawable.restore, getResources().getColor(
						R.color.activity_restore_background));
		items[VIEW_SMS_BLOCK_POSITION] = new Item(ViewMessageActivity.class,
				getString(R.string.view), R.drawable.view, getResources()
						.getColor(R.color.activity_view_background));
		items[VIEW_BACKUP_SMS_BLOCK_POSITION] = new Item(ViewMessageActivity.class,
				getString(R.string.view_backup_sms), R.drawable.view, getResources()
						.getColor(R.color.activity_help_background));
		
		items[SEARCH_BLOCK_POSITION] = new Item(SearchActivity.class, getString(R.string.search),
				R.drawable.search, getResources().getColor(
						R.color.activity_search_background));
		items[DELETE_BACKUP_BLOCK_POSITION] = new Item(DeleteMessageActivity.class,
				getString(R.string.delete_backup), R.drawable.delete, getResources()
						.getColor(R.color.activity_delete_background));
		//items[DELETE_SMS_BLOCK_POSITION] = new Item(DeleteMessageActivity.class,
		//		getString(R.string.delete_sms), R.drawable.delete, getResources()
		//				.getColor(R.color.activity_delete_sms_background));
		
		items[APPS_BLOCK_POSITION] = new Item(ShareActivity.class, getString(R.string.apps),
				R.drawable.apps, getResources().getColor(
						R.color.activity_backup_background));
		
		items[SHARE_BLOCK_POSITION] = new Item(ShareActivity.class, getString(R.string.share),
				R.drawable.share, getResources().getColor(
						R.color.activity_share_background));
		
		items[RATE_BLOCK_POSITION] = new Item(AboutActivity.class,
				getString(R.string.rate), R.drawable.star, getResources()
						.getColor(R.color.activity_rate_background));
		
		items[ABOUT_BLOCK_POSITION] = new Item(AboutActivity.class,
						getString(R.string.about), R.drawable.info, getResources()
								.getColor(R.color.activity_info_background));
				
		
		items[SETTINGS_BLOCK_POSITION] = new Item(SettingsActivity.class,
				getString(R.string.settings), R.drawable.settings,
				getResources().getColor(R.color.activity_settings_background));
		
		//items[HELP_BLOCK_POSITION] = new Item(HelpActivity.class, getString(R.string.help),
		//		R.drawable.help, getResources().getColor(
		//				R.color.activity_help_background));
		
		CustomItemAdapter adapter = new CustomItemAdapter(this,
				R.layout.item_list_item, items);

		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						((TextView) v).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}
	

	class Item {

		private Class activityClass;

		private String label;

		private int iconResource;

		private int backgroudColor;

		public Item() {

		}

		public Item(Class activityClass, String label, int iconResource,
				int backgroudColor) {
			super();
			this.activityClass = activityClass;
			this.label = label;
			this.iconResource = iconResource;
			this.backgroudColor = backgroudColor;
		}

		public int getBackgroudColor() {
			return backgroudColor;
		}

		public void setBackgroudColor(int backgroudColor) {
			this.backgroudColor = backgroudColor;
		}

		public int getIconResource() {
			return iconResource;
		}

		public void setIconResource(int iconResource) {
			this.iconResource = iconResource;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Class getActivityClass() {
			return activityClass;
		}

		public void setActivityClass(Class activityClass) {
			this.activityClass = activityClass;
		}

	}

	class CustomItemAdapter extends ArrayAdapter<Item> {

		private Activity myContext;

		private Item[] datas;

		public CustomItemAdapter(Context context, int textViewResourceId,
				Item[] objects) {
			super(context, textViewResourceId, objects);

			myContext = (Activity) context;
			datas = objects;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = myContext.getLayoutInflater();

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_list_item, null);
			}

			Button icon = (Button) convertView
					.findViewById(R.id.itemListItemButton);
			icon.setBackgroundColor(datas[position].backgroudColor);
			icon.setText(datas[position].getLabel());
			icon.setCompoundDrawablesWithIntrinsicBounds(0,
					datas[position].iconResource, 0, 0);

			icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (position == VIEW_SMS_BLOCK_POSITION) {
						
						Intent i = new Intent();
						i.setClass(MainActivity.this, SearchActivity.class);
						i.putExtra(SearchActivity.SEARCH_MODE, SearchActivity.MODE_PHONE);
						startActivity(i);
						
					} else if (position == VIEW_BACKUP_SMS_BLOCK_POSITION) {
						
						Intent i = new Intent();
						i.setClass(MainActivity.this, SearchActivity.class);
						i.putExtra(SearchActivity.SEARCH_MODE, SearchActivity.MODE_BACKUP);
						startActivity(i);
						
					} else if (position == SHARE_BLOCK_POSITION) { // SHARE

						Intent share = new Intent(
								android.content.Intent.ACTION_SEND);
						share.setType("text/plain");
						share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

						// Add data to the intent, the receiving app will
						// decide
						// what to do with it.
						share.putExtra(Intent.EXTRA_SUBJECT,
								getString(R.string.app_name));
						share.putExtra(Intent.EXTRA_TEXT,
								getString(R.string.appShareDescription));

						startActivity(Intent.createChooser(share,
								getString(R.string.select_share_method)));

					} else if(position == RATE_BLOCK_POSITION) { //Rate
						
						Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName());
						Intent i = new Intent(Intent.ACTION_VIEW, uri);
						try {
							startActivity(i);							
						} catch (ActivityNotFoundException e) {
							Toast.makeText(MainActivity.this, getString(R.string.google_play_not_installed), Toast.LENGTH_LONG).show();
						}
						
						
						
					} /*else if (position == DELETE_SMS_BLOCK_POSITION) {
						
						SmsUtil.showDeletePhoneSmsWizard(MainActivity.this);
						
					} */ else if (position == APPS_BLOCK_POSITION) {
						
						Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=Kelvio+Matias");
						Intent i = new Intent(Intent.ACTION_VIEW, uri);
						try {
							startActivity(i);							
						} catch (ActivityNotFoundException e) {
							Toast.makeText(MainActivity.this, getString(R.string.google_play_not_installed), Toast.LENGTH_LONG).show();
						}
						
					} else {
						Intent i = new Intent();
						i.setClass(MainActivity.this,
								datas[position].activityClass);
						startActivity(i);
					}

				}
			});

			return convertView;
		}

	}

}
