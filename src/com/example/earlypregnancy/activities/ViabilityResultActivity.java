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

public class ViabilityResultActivity extends Activity implements
		OnClickListener {

	private double result1, result2;
	private int resultId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.viability_output);

		Intent intent = getIntent();

		String name = intent.getStringExtra("NAME");
		double B13 = (double) (intent.getIntExtra("B13", -1));
		int B14 = intent.getIntExtra("B14", -1);
		int B15 = intent.getIntExtra("B15", -1);
		double B16 = (double) (intent.getFloatExtra("B16", -1));
		double B17 = (double) (intent.getFloatExtra("B17", -1));
		resultId = intent.getIntExtra("RESULT_ID", -1);

		ArrayList<Double> results = Datas.calculatorViability(B13, B14, B15,
				B16, B17);
		if (results != null && results.size() == 2) {

			result1 = results.get(0);
			result2 = results.get(1);

			((TextView) findViewById(R.id.viabilityOutput_result1_tv))
					.setText(result1 + "%");
			((TextView) findViewById(R.id.viabilityOutput_result2_tv))
					.setText(result2 + "%");

			PieChartView pieChartView = (PieChartView) findViewById(R.id.viabilityOutput_pieChartView);
			pieChartView.addItem(result1 + "%", (float) result1,
					Color.parseColor("#33BE00"));
			pieChartView.addItem(result2 + "%", (float) result2,
					Color.parseColor("#177CAC"));

			findViewById(R.id.viabilityOutput_back_iv).setOnClickListener(this);
			findViewById(R.id.viabilityOutput_result_iv).setOnClickListener(
					this);
			findViewById(R.id.viabilityOutput_mail_iv).setOnClickListener(this);
		} else {
			Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
			findViewById(R.id.viabilityOutput_back_iv).setOnClickListener(this);
			findViewById(R.id.viabilityOutput_result_iv).setOnClickListener(
					this);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.viabilityOutput_back_iv:
			finish();
			break;

		case R.id.viabilityOutput_result_iv:
			startActivity(new Intent(this, ResultActivity.class));
			break;

		case R.id.viabilityOutput_mail_iv:
			if (resultId >= 0) {
				Datas.sendMailForUser(this, resultId);
			}
			break;
		}
	}

}
