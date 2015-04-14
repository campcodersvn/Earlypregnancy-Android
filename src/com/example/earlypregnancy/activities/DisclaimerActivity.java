package com.example.earlypregnancy.activities;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.example.earlypregnancy.R;
import com.example.earlypregnancy.services.Datas;

public class DisclaimerActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.disclaimer);

		findViewById(R.id.disclaimer_back_iv).setOnClickListener(this);
		findViewById(R.id.disclaimer_read_bt).setOnClickListener(this);
		((TextView) findViewById(R.id.disclaimer_infor_tv)).setText(Datas
				.readFileStringAssets(this, "disclaimer.txt"));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.disclaimer_back_iv:
			Datas.name = "";
			startActivity(new Intent(this, HomeActivity.class));
			finish();
			break;

		case R.id.disclaimer_read_bt:
			Datas.isDisclaimer = true;
			startActivity(new Intent(this, SelectModelActivity.class));
			finish();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		Datas.name = "";
		startActivity(new Intent(this, HomeActivity.class));
		finish();
		// super.onBackPressed();
	}

}
