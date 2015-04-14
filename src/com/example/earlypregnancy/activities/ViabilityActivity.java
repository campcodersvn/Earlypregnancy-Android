package com.example.earlypregnancy.activities;

import java.util.ArrayList;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.earlypregnancy.R;
import com.example.earlypregnancy.services.Datas;
import com.example.earlypregnancy.services.SQLiteDatabaseManager;

@SuppressLint("InflateParams")
public class ViabilityActivity extends Activity implements OnClickListener {

	private TextView tvYes, tvNo;
	private EditText et1, et2, et5, et6;
	private Spinner spinner;

	private int B15;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.viability_input);

		tvYes = (TextView) findViewById(R.id.viabilityInput_yes_tv);
		tvNo = (TextView) findViewById(R.id.viabilityInput_no_tv);
		et1 = (EditText) findViewById(R.id.viabilityInput_et1);
		et2 = (EditText) findViewById(R.id.viabilityInput_et2);
		et5 = (EditText) findViewById(R.id.viabilityInput_et5);
		et6 = (EditText) findViewById(R.id.viabilityInput_et6);
		spinner = (Spinner) findViewById(R.id.viabilityInput_spinner);

		findViewById(R.id.viabilityInput_clearData_bt).setOnClickListener(this);
		findViewById(R.id.viabilityInput_result_bt).setOnClickListener(this);
		findViewById(R.id.viabilityInput_back_iv).setOnClickListener(this);
		tvYes.setOnClickListener(this);
		tvNo.setOnClickListener(this);

		ArrayList<String> items = new ArrayList<String>();
		items.add(" No bleeding");
		items.add(" Light bleeding");
		items.add(" Moderate bleeding");
		items.add(" Soaked sanitary towel");
		items.add(" Clots or flooding");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.item_spinner, items);
		spinner.setAdapter(adapter);

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
		case R.id.viabilityInput_clearData_bt:
			et1.setText("");
			et2.setText("");
			et5.setText("");
			et6.setText("");
			spinner.setSelection(0, true);
			B15 = 0;
			tvYes.setBackgroundResource(R.drawable.circle_blue);
			tvNo.setBackgroundResource(R.drawable.circle_blue);
			break;

		case R.id.viabilityInput_result_bt:
			checkInfor();
			break;

		case R.id.viabilityInput_yes_tv:
			if (B15 != 1) {
				B15 = 1;
				tvYes.setBackgroundResource(R.drawable.circle_yellow);
				tvNo.setBackgroundResource(R.drawable.circle_blue);
			}
			break;

		case R.id.viabilityInput_no_tv:
			if (B15 != 2) {
				B15 = 2;
				tvNo.setBackgroundResource(R.drawable.circle_yellow);
				tvYes.setBackgroundResource(R.drawable.circle_blue);
			}
			break;

		case R.id.viabilityInput_back_iv:
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

		int B13 = -1;
		float B16 = -1;
		float B17 = -1;

		try {
			B13 = Integer.parseInt(et2.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (B13 < 15 || B13 > 50) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Maternal age is outside the suggested range of application");
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

		int B14 = spinner.getSelectedItemPosition();

		if (B15 == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Please answer YES or NO on the question \"Presence of fetal heartbeat?\"");
			builder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							final Dialog dialogChoose = new Dialog(
									ViabilityActivity.this);
							dialogChoose
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							View view = LayoutInflater.from(
									ViabilityActivity.this).inflate(
									R.layout.dialog_yes_no, null);
							((TextView) view
									.findViewById(R.id.dialogYesNo_msg_tv))
									.setText("Presence of fetal heartbeat?");
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
			B16 = Float.parseFloat(et5.getText().toString().replace(",", "."));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (B16 < 0 || B16 > 99.9F) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Mean gestational sac size is outside the suggested range of application");
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

		try {
			B17 = Float.parseFloat(et6.getText().toString().replace(",", "."));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (B17 < 0 || B17 > 9.9F) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Mean yolk sac diameter is outside the suggested range of application");
			builder.setPositiveButton("RETRY",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							et6.requestFocus();
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
			values.put("value5", B17);
			SQLiteDatabaseManager.database.insert("viability", null, values);

			Cursor cursor = SQLiteDatabaseManager.database.rawQuery(
					"SELECT id FROM viability ORDER BY id DESC LIMIT 1", null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					values = new ContentValues();
					values.put("name", name);
					values.put("model", 2);
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

		Intent intent = new Intent(this, ViabilityResultActivity.class);
		intent.putExtra("NAME", name);
		intent.putExtra("B13", B13);
		intent.putExtra("B14", B14);
		intent.putExtra("B15", B15);
		intent.putExtra("B16", B16);
		intent.putExtra("B17", B17);
		intent.putExtra("RESULT_ID", resultId);

		startActivity(intent);
	}
}
