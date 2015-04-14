package com.example.earlypregnancy.customview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.earlypregnancy.R;
import com.example.earlypregnancy.activities.DisclaimerActivity;
import com.example.earlypregnancy.activities.SelectModelActivity;
import com.example.earlypregnancy.entity.Result;
import com.example.earlypregnancy.services.Datas;
import com.example.earlypregnancy.services.SQLiteDatabaseManager;

@SuppressLint({ "InflateParams", "SimpleDateFormat" })
public class ResultAdapter extends ArrayAdapter<Result> {

	private ArrayList<Result> results;
	private SimpleDateFormat dateFormat;
	private Activity activity;

	public ResultAdapter(Activity activity, int textViewResourceId,
			ArrayList<Result> results) {
		super(activity, textViewResourceId, results);

		this.activity = activity;
		this.results = results;
		dateFormat = new SimpleDateFormat("MMMM d, yyyy - hh:mmaa");
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_result, null);
		}

		TextView tvInfor = (TextView) convertView
				.findViewById(R.id.itemResult_infor_tv);

		final Result result = results.get(position);

		((TextView) convertView.findViewById(R.id.resultItem_name_tv))
				.setText("Record Name " + result.getName());

		tvInfor.setText("Record " + result.getName() + " - "
				+ (result.getModel() == 1 ? "M6" : "Viability") + "\n"
				+ dateFormat.format(new Date(result.getTime())));

		convertView.findViewById(R.id.resultItem_add_iv).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Datas.name = result.getName();
						Intent intent = new Intent(ResultAdapter.this.activity,
								Datas.isDisclaimer ? SelectModelActivity.class
										: DisclaimerActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						ResultAdapter.this.activity.startActivity(intent);
						ResultAdapter.this.activity.finish();
					}
				});

		convertView.findViewById(R.id.resultItem_sub_iv).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							if (!SQLiteDatabaseManager.isOpen()) {
								SQLiteDatabaseManager.openDatabase(
										activity,
										activity.getString(R.string.database_name));
							}
							SQLiteDatabaseManager.database.delete(result
									.getModel() == 1 ? "pul_m6" : "viability",
									"id=?", new String[] { result.getId_model()
											+ "" });
							SQLiteDatabaseManager.database.delete("result",
									"id=?",
									new String[] { result.getId() + "" });
							results.remove(position);
							notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		convertView.findViewById(R.id.resultItem_mail_iv).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Datas.sendMailForUser(activity, result.getId());
					}
				});

		return convertView;
	}
}
