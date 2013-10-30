package com.kelviomatias.smsbackup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.kelviomatias.smsbackup.adapter.SmsAdapter;
import com.kelviomatias.smsbackup.model.Sms;

public class SearchResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);

		List l = (List) this.getIntent().getSerializableExtra(
				ArrayList.class.getName());

		this.refreshListView(l);

	}

	private void refreshListView(List l) {
		Sms[] datas = new Sms[l.size()];
		for (int i = 0; i < datas.length; i++) {
			datas[i] = (Sms) l.get(i);
		}
		SmsAdapter adapter = new SmsAdapter(this,
				R.layout.activity_search_result_list_item, datas);

		ListView list = (ListView) findViewById(R.id.activitySearchResultListView);
		list.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
		return true;
	}

}
