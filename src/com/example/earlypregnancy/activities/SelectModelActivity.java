package com.example.earlypregnancy.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.example.earlypregnancy.R;

public class SelectModelActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.select_model);

		findViewById(R.id.selectModel_back_iv).setOnClickListener(this);
		findViewById(R.id.selectModel_pulM6_bt).setOnClickListener(this);
		findViewById(R.id.selectModel_viability_bt).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selectModel_back_iv:
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;

		case R.id.selectModel_pulM6_bt:
			startActivity(new Intent(this, PulM6Activity.class));
			finish();
			break;

		case R.id.selectModel_viability_bt:
			startActivity(new Intent(this, ViabilityActivity.class));
			finish();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		Intent intent = new Intent(this, HomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

}
