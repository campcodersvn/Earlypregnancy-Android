package com.example.earlypregnancy.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.example.earlypregnancy.R;
import com.example.earlypregnancy.services.Datas;
import com.example.earlypregnancy.services.SQLiteDatabaseManager;

public class HomeActivity extends Activity implements OnClickListener {

	private boolean isShowResult;
	private TextView tvResult;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.home);

		try {
			if (!SQLiteDatabaseManager.isOpen()) {
				SQLiteDatabaseManager.openDatabase(getApplicationContext(),
						getString(R.string.database_name));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		tvResult = (TextView) findViewById(R.id.home_result_tv);

		findViewById(R.id.home_start_bt).setOnClickListener(this);

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		try {
			Cursor cursor = SQLiteDatabaseManager.database.rawQuery(
					"SELECT id FROM result LIMIT 1", null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					isShowResult = true;
				} else {
					isShowResult = false;
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isShowResult) {
			tvResult.setOnClickListener(this);
		} else {
			tvResult.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			Cursor cursor = SQLiteDatabaseManager.database.rawQuery(
					"SELECT id FROM result LIMIT 1", null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					isShowResult = true;
				} else {
					isShowResult = false;
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isShowResult) {
			tvResult.setOnClickListener(this);
		} else {
			tvResult.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_start_bt:
			if (!Datas.isDisclaimer) {
				startActivity(new Intent(this, DisclaimerActivity.class));
				finish();
			} else {
				startActivity(new Intent(this, SelectModelActivity.class));
				finish();
			}
			break;

		case R.id.home_result_tv:
			if (isShowResult) {
				startActivity(new Intent(this, ResultActivity.class));
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		try {
			SQLiteDatabaseManager.closeDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onBackPressed();
	}
}
