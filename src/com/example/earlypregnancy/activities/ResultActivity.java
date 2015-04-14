package com.example.earlypregnancy.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.earlypregnancy.R;
import com.example.earlypregnancy.customview.ResultAdapter;
import com.example.earlypregnancy.entity.Result;
import com.example.earlypregnancy.services.Datas;
import com.example.earlypregnancy.services.SQLiteDatabaseManager;

public class ResultActivity extends Activity implements OnItemClickListener,
		OnClickListener {

	private ArrayList<Result> results;

	private ListView lvResult;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.result);

		lvResult = (ListView) findViewById(R.id.result_lv);

		findViewById(R.id.result_home_iv).setOnClickListener(this);
		findViewById(R.id.result_add_iv).setOnClickListener(this);

		try {
			if (!SQLiteDatabaseManager.isOpen()) {
				SQLiteDatabaseManager.openDatabase(getApplicationContext(),
						getString(R.string.database_name));
			}
			Cursor cursor = SQLiteDatabaseManager.database.rawQuery(
					"SELECT * FROM result ORDER BY id DESC LIMIT 100", null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					results = new ArrayList<Result>(cursor.getCount());
					do {
						Result result = new Result();
						result.setId(cursor.getInt(0));
						result.setName(cursor.getString(1));
						result.setModel(cursor.getInt(2));
						try {
							result.setTime(Long.parseLong(cursor.getString(3)));
						} catch (Exception e) {
							e.printStackTrace();
						}
						result.setId_model(cursor.getInt(4));
						results.add(result);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (results != null && !results.isEmpty()) {
			ResultAdapter resultAdapter = new ResultAdapter(this,
					R.layout.item_result, results);
			lvResult.setAdapter(resultAdapter);
			lvResult.setOnItemClickListener(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		Intent intent = null;
		switch (results.get(position).getModel()) {
		case 1:// pul m6

			try {
				if (!SQLiteDatabaseManager.isOpen()) {
					SQLiteDatabaseManager.openDatabase(getApplicationContext(),
							getString(R.string.database_name));
				}
				Cursor cursor = SQLiteDatabaseManager.database.rawQuery(
						"SELECT * FROM pul_m6 WHERE id=?",
						new String[] { String.valueOf(results.get(position)
								.getId_model()) });
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						intent = new Intent(this, PulM6ResultActivity.class);
						intent.putExtra("NAME", results.get(position).getName());
						intent.putExtra("B15", cursor.getInt(3));
						intent.putExtra("B13", cursor.getInt(1));
						intent.putExtra("B14", cursor.getInt(2));
						intent.putExtra("B16", cursor.getInt(4));
						intent.putExtra("RESULT_ID", results.get(position)
								.getId());
					}
					cursor.close();
				}
			} catch (Exception e) {
				intent = null;
				e.printStackTrace();
			}
			if (intent != null) {
				startActivity(intent);
			}
			break;

		default: // viability
			try {
				if (!SQLiteDatabaseManager.isOpen()) {
					SQLiteDatabaseManager.openDatabase(getApplicationContext(),
							getString(R.string.database_name));
				}
				Cursor cursor = SQLiteDatabaseManager.database.rawQuery(
						"SELECT * FROM viability WHERE id=?",
						new String[] { String.valueOf(results.get(position)
								.getId_model()) });
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						intent = new Intent(this, ViabilityResultActivity.class);
						intent.putExtra("NAME", results.get(position).getName());
						intent.putExtra("B13", cursor.getInt(1));
						intent.putExtra("B14", cursor.getInt(2));
						intent.putExtra("B15", cursor.getInt(3));
						intent.putExtra("B16", cursor.getFloat(4));
						intent.putExtra("B17", cursor.getFloat(5));
						intent.putExtra("RESULT_ID", results.get(position)
								.getId());
					}
					cursor.close();
				}
			} catch (Exception e) {
				intent = null;
				e.printStackTrace();
			}
			if (intent != null) {
				startActivity(intent);
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {

		Intent intent = null;
		switch (v.getId()) {
		case R.id.result_home_iv:
			intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;

		case R.id.result_add_iv:
			intent = new Intent(this,
					Datas.isDisclaimer ? SelectModelActivity.class
							: DisclaimerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		}
	}
}
