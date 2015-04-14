package com.example.earlypregnancy.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.earlypregnancy.R;
import com.example.earlypregnancy.customview.PieChartView;
import com.example.earlypregnancy.services.Datas;

public class PulM6ResultActivity extends Activity implements OnClickListener {

	private String name;
	private double result1, result2, result3;
	private int resultId;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.pul_m6_output);

		// final PieChart pie = (PieChart) this.findViewById(R.id.Pie);

		PieChartView pieChartView = (PieChartView) findViewById(R.id.pulM6Output_pieChartView);

		Intent intent = getIntent();
		name = intent.getStringExtra("NAME");
		int B15 = intent.getIntExtra("B15", -1);
		double B13 = (double) (intent.getIntExtra("B13", -1));
		double B14 = (double) (intent.getIntExtra("B14", -1));
		double B16 = (double) (intent.getIntExtra("B16", -1));
		resultId = intent.getIntExtra("RESULT_ID", -1);

		ArrayList<Double> results = Datas.calculatorPulM6(B13, B14, B15, B16);

		if (results != null && results.size() == 3) {

			result1 = results.get(0);
			result2 = results.get(1);
			result3 = results.get(2);

			// pie.addItem(result1 + "%", (float) result1,
			// Color.parseColor("#F90F01"));
			// pie.addItem(result2 + "%", (float) result2,
			// Color.parseColor("#177CAC"));
			// pie.addItem(result3 + "%", (float) result3,
			// Color.parseColor("#33BE00"));

			pieChartView.addItem(result1 + "%", (float) result1,
					Color.parseColor("#F90F01"));
			pieChartView.addItem(result2 + "%", (float) result2,
					Color.parseColor("#177CAC"));
			pieChartView.addItem(result3 + "%", (float) result3,
					Color.parseColor("#33BE00"));

			((TextView) findViewById(R.id.pulM6Output_result1_tv))
					.setText(result1 + "%");
			((TextView) findViewById(R.id.pulM6Output_result2_tv))
					.setText(result2 + "%");
			((TextView) findViewById(R.id.pulM6Output_result3_tv))
					.setText(result3 + "%");

			TextView tvResult4 = (TextView) findViewById(R.id.pulM6Output_result4_tv);
			if (result1 >= 5D) {
				tvResult4
						.setText("Patient in high risk group for ectopic pregnancy");
				tvResult4.invalidate();
			} else if (result1 >= 0 && result1 < 5) {
				tvResult4
						.setText("Patient in low risk group for ectopic pregnancy");
				tvResult4.setTextColor(Color.parseColor("#218500"));
				tvResult4.invalidate();
			} else {
				findViewById(R.id.pulM6Output_inter_tv)
						.setVisibility(View.GONE);
				tvResult4.setVisibility(View.GONE);
			}

			findViewById(R.id.pulM6Output_result_iv).setOnClickListener(this);
			findViewById(R.id.pulM6Output_back_iv).setOnClickListener(this);
			findViewById(R.id.pulM6Output_mail_iv).setOnClickListener(this);

		} else {
			Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
			findViewById(R.id.pulM6Output_result_iv).setOnClickListener(this);
			findViewById(R.id.pulM6Output_back_iv).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pulM6Output_result_iv:
			startActivity(new Intent(this, ResultActivity.class));
			break;

		case R.id.pulM6Output_back_iv:
			finish();
			break;

		case R.id.pulM6Output_mail_iv:
			if (resultId >= 0) {
				Datas.sendMailForUser(this, resultId);
			}
			break;
		}
	}

}
