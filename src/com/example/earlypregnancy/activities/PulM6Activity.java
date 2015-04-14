package com.example.earlypregnancy.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.earlypregnancy.R;
import com.example.earlypregnancy.services.Datas;
import com.example.earlypregnancy.services.SQLiteDatabaseManager;

@SuppressLint("InflateParams")
public class PulM6Activity extends Activity implements OnClickListener {

	private TextView tvYes, tvNo;
	private EditText et1, et2, et3, et5;

	private int B15;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.pul_m6_input);

		tvYes = (TextView) findViewById(R.id.pulM6Input_yes_tv);
		tvNo = (TextView) findViewById(R.id.pulM6Input_no_tv);
		et1 = (EditText) findViewById(R.id.pulM6Input_et1);
		et2 = (EditText) findViewById(R.id.pulM6Input_et2);
		et3 = (EditText) findViewById(R.id.pulM6Input_et3);
		et5 = (EditText) findViewById(R.id.pulM6Input_et5);

		tvYes.setOnClickListener(this);
		tvNo.setOnClickListener(this);
		findViewById(R.id.pulM6Input_clearData_bt).setOnClickListener(this);
		findViewById(R.id.pulM6Input_result_bt).setOnClickListener(this);
		findViewById(R.id.pulM6Input_back_iv).setOnClickListener(this);

		if (Datas.name != null && Datas.name.length() > 0) {
			et1.setText(Datas.name);
			et2.requestFocus();
		} else {
			et1.requestFocus();
		}
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		startActivity(new Intent(this, SelectModelActivity.class));
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pulM6Input_yes_tv:
			if (B15 != 1) {
				B15 = 1;
				tvYes.setBackgroundResource(R.drawable.circle_yellow);
				tvNo.setBackgroundResource(R.drawable.circle_blue);
			}
			break;

		case R.id.pulM6Input_no_tv:
			if (B15 != 2) {
				B15 = 2;
				tvNo.setBackgroundResource(R.drawable.circle_yellow);
				tvYes.setBackgroundResource(R.drawable.circle_blue);
			}
			break;

		case R.id.pulM6Input_clearData_bt:
			et1.setText("");
			et2.setText("");
			et3.setText("");
			et5.setText("");
			B15 = 0;
			tvYes.setBackgroundResource(R.drawable.circle_blue);
			tvNo.setBackgroundResource(R.drawable.circle_blue);
			break;

		case R.id.pulM6Input_result_bt:
			checkInfor();
			break;

		case R.id.pulM6Input_back_iv:
			startActivity(new Intent(this, SelectModelActivity.class));
			finish();
			break;
		}
	}

	private void checkInfor() {

		String name = et1.getText().toString().trim();
		if (name == null || name.length() <= 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Enter a Record Name please");
			builder.setPositiveButton("RETRY",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							et1.requestFocus();
						}
					});
			builder.show();
			return;
		}

		int B13 = -1, B14 = -1, B16 = -1;

		try {
			B13 = Integer.parseInt(et2.getText().toString());
		} catch (Exception e) {
			B13 = 0;
			e.printStackTrace();
		}
		if (B13 < 26 || B13 > 50000) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Initial serum hCG is outside the suggested range of application. If initial hCG is <=25 we assume a failed pul");
			builder.setPositiveButton("RETRY",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							et2.requestFocus();
						}
					});
			builder.show();
			return;
		}

		try {
			B14 = Integer.parseInt(et3.getText().toString());
		} catch (Exception e) {
			B14 = -1;
			e.printStackTrace();
		}
		if (B14 < 0 || B14 > 50000) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("48 hCG level is outside the suggested range of application");
			builder.setPositiveButton("RETRY",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							et3.requestFocus();
						}
					});
			builder.show();
			return;
		}

		if (B15 == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Please answer YES or NO on the question \"Did patient receive progesterone treatment?\"");
			builder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							final Dialog dialogChoose = new Dialog(
									PulM6Activity.this);
							dialogChoose
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							View view = LayoutInflater.from(PulM6Activity.this)
									.inflate(R.layout.dialog_yes_no, null);
							((TextView) view
									.findViewById(R.id.dialogYesNo_msg_tv))
									.setText("Did patient receive progesterone treatment?");
							dialogChoose
									.addContentView(
											view,
											new RelativeLayout.LayoutParams(
													RelativeLayout.LayoutParams.WRAP_CONTENT,
													RelativeLayout.LayoutParams.WRAP_CONTENT));
							final TextView tvyes = (TextView) view
									.findViewById(R.id.dialogYesNo_yes_tv);
							tvyes.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									B15 = 1;
									tvyes.setBackgroundResource(R.drawable.circle_yellow);
									tvYes.setBackgroundResource(R.drawable.circle_yellow);
									tvNo.setBackgroundResource(R.drawable.circle_blue);
									dialogChoose.dismiss();
								}
							});
							final TextView tvno = (TextView) view
									.findViewById(R.id.dialogYesNo_no_tv);
							tvno.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									B15 = 2;
									tvno.setBackgroundResource(R.drawable.circle_yellow);
									tvNo.setBackgroundResource(R.drawable.circle_yellow);
									tvYes.setBackgroundResource(R.drawable.circle_blue);
									dialogChoose.dismiss();
								}
							});
							dialogChoose.show();

						}
					});
			builder.show();
			return;
		}

		try {
			String b16 = et5.getText().toString();
			if (b16 != null && b16.length() > 0) {
				B16 = Integer.parseInt(b16);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (B16 > 200) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Intial serum progesterone level is outside the suggested range of application");
			builder.setPositiveButton("RETRY",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							et5.requestFocus();
						}
					});
			builder.show();
			return;
		}

		int resultId = -1;
		try {
			if (!SQLiteDatabaseManager.isOpen()) {
				SQLiteDatabaseManager.openDatabase(getApplicationContext(),
						getString(R.string.database_name));
			}

			ContentValues values = new ContentValues();
			values.put("value1", B13);
			values.put("value2", B14);
			values.put("value3", B15);
			values.put("value4", B16);
			SQLiteDatabaseManager.database.insert("pul_m6", null, values);

			Cursor cursor = SQLiteDatabaseManager.database.rawQuery(
					"SELECT id FROM pul_m6 ORDER BY id DESC LIMIT 1", null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					values = new ContentValues();
					values.put("name", name);
					values.put("model", 1);
					values.put("time",
							String.valueOf(System.currentTimeMillis()));
					values.put("id_model", cursor.getInt(0));
					SQLiteDatabaseManager.database.insert("result", null,
							values);

					Cursor cursorResult = SQLiteDatabaseManager.database
							.rawQuery(
									"SELECT id FROM result WHERE id_model=? ORDER BY id DESC LIMIT 1",
									new String[] { cursor.getInt(0) + "" });
					if (cursorResult != null) {
						if (cursorResult.moveToFirst()) {
							resultId = cursorResult.getInt(0);
						}
						cursorResult.close();
					}
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Intent intent = new Intent(this, PulM6ResultActivity.class);
		intent.putExtra("NAME", name);
		intent.putExtra("B15", B15);
		intent.putExtra("B13", B13);
		intent.putExtra("B14", B14);
		intent.putExtra("B16", B16);
		intent.putExtra("RESULT_ID", resultId);

		startActivity(intent);

	}
}
