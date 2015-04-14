package com.example.earlypregnancy.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;

import com.example.earlypregnancy.R;

public class Datas {

	public static final int ACTIVITY_HOME = 0;
	public static final int ACTIVITY_DISCLAIMER = 1;
	public static final int ACTIVITY_SELECT_MODEL = 2;
	public static final int ACTIVITY_PUL_M6_INPUT = 3;
	public static final int ACTIVITY_PUL_M6_OUTPUT = 4;
	public static final int ACTIVITY_VIABILITY_INPUT = 5;
	public static final int ACTIVITY_VIABILITY_OUTPUT = 6;
	public static final int ACTIVITY_RESULT = 7;

	public static final String IS_READ_AND_ACCPET_DISCLAIMER = "IS_READ_AND_ACCPET_DISCLAIMER";

	public static boolean isDisclaimer;

	public static String name;

	public static int ACTIVITY_BEFORE = ACTIVITY_HOME;

	@SuppressLint("SimpleDateFormat")
	public static void sendMailForUser(Activity activity, int resultId) {
		try {
			if (!SQLiteDatabaseManager.isOpen()) {
				SQLiteDatabaseManager.openDatabase(activity,
						activity.getString(R.string.database_name));
			}
			Cursor cursor = SQLiteDatabaseManager.database.rawQuery(
					"SELECT * FROM result WHERE id=?", new String[] { resultId
							+ "" });
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					String name = cursor.getString(1);
					int model = cursor.getInt(2);
					String time = (new SimpleDateFormat(
							"MMMM d, yyyy - hh:mmaa")).format(new Date(cursor
							.getLong(3)));
					int id_model = cursor.getInt(4);
					String title = null;
					if (model == 1) {
						title = "Pul M6 - " + time;
						Cursor cursorModel = SQLiteDatabaseManager.database
								.rawQuery("SELECT * FROM pul_m6 WHERE id=?",
										new String[] { id_model + "" });
						if (cursorModel != null) {
							if (cursorModel.moveToFirst()) {
								ArrayList<Double> results = calculatorPulM6(
										(double) cursorModel.getInt(1),
										(double) cursorModel.getInt(2),
										cursorModel.getInt(3),
										(double) cursorModel.getInt(4));
								if (results != null && results.size() == 3) {
									StringBuffer buffer = new StringBuffer();
									// html
									buffer.append("<html><body>");
									// name
									buffer.append(
											"<br /><font color=\"#6600ff\" size=\"6\"><b>")
											.append(name)
											.append("</b></font><br /><br />");
									// model + time
									buffer.append(
											"<font color=\"#444444\" size=\"5\"><b>")
											.append(title)
											.append("</b></font><br /><br />");
									// row 1
									buffer.append("<font color=\"#F90F00\" size=\"5\">Risk of ectopic pregnancy : </font>");
									buffer.append("<font color=\"#F90F00\" size=\"5\">"
											+ results.get(0)
											+ "%</font><br /><br />");
									// row 2
									buffer.append("<font color=\"#1A7CB1\" size=\"5\">Risk of failure PUL : </font>");
									buffer.append("<font color=\"#1A7CB1\" size=\"5\">"
											+ results.get(1)
											+ "%</font><br /><br />");
									// row 3
									buffer.append("<font color=\"#218500\" size=\"5\">Chance of introuterine pregnancy : </font>");
									buffer.append("<font color=\"#218500\" size=\"5\">"
											+ results.get(2)
											+ "%</font><br /> <br />");
									// row 4
									buffer.append("<font color=\"#515252\" size=\"5\"><b>Interpretation: </b></font>");
									if (results.get(0) >= 5) {
										buffer.append("<font color=\"#F91200\" size=\"5\"> Patient in high risk group for ectopic pregnancy</font><br />");
									} else if (results.get(0) >= 0
											&& results.get(0) < 5) {
										buffer.append("<font color=\"#218500\" size=\"5\"> Patient in low risk group for ectopic pregnancy</font><br />");
									}
									// colose html
									buffer.append("</body></html>");
									sendMail(activity,
											Html.fromHtml(buffer.toString()));
									// sendMail(activity,
									// Html.fromHtml(readFileStringAssets(
									// activity, "test.txt")));
								}
							}
						}
					} else if (model == 2) {
						title = "Viability - " + time;
						Cursor cursorModel = SQLiteDatabaseManager.database
								.rawQuery("SELECT * FROM viability WHERE id=?",
										new String[] { id_model + "" });
						if (cursorModel != null) {
							if (cursorModel.moveToFirst()) {
								ArrayList<Double> results = calculatorViability(
										(double) cursorModel.getInt(1),
										cursorModel.getInt(2),
										cursorModel.getInt(3),
										(double) cursorModel.getFloat(4),
										(double) cursorModel.getFloat(5));
								if (results != null && results.size() == 2) {
									StringBuffer buffer = new StringBuffer();
									// html
									buffer.append("<html><body>");
									// name
									buffer.append(
											"<br /><font color=\"#6600ff\" size=\"6\"><b>")
											.append(name)
											.append("</b></font><br /><br />");
									// model + time
									buffer.append(
											"<font color=\"#444444\" size=\"5\"><b>")
											.append(title)
											.append("</b></font><br /><br />");
									// row 1
									buffer.append("<font color=\"#218500\" size=\"5\">Chance of viable pregnancy : </font>");
									buffer.append("<font color=\"#218500\" size=\"5\">"
											+ results.get(0)
											+ "%</font><br /><br />");
									// row 2
									buffer.append("<font color=\"#1A7CB1\" size=\"5\">Risk of miscarriage : </font>");
									buffer.append("<font color=\"#1A7CB1\" size=\"5\">"
											+ results.get(1)
											+ "%</font><br /><br />");
									// colose html
									buffer.append("</body></html>");
									sendMail(activity,
											Html.fromHtml(buffer.toString()));
								}
							}
						}
						cursorModel.close();
					}
				}
				cursor.close();
			}
		} catch (Exception e) {

		}
	}

	public static ArrayList<Double> calculatorPulM6(double B13, double B14,
			int B15, double B16) {
		if (B15 != -1 && B13 != -1 && B14 != -1) {

			double result1, result2, result3;

			if (B15 == 1 || B16 < 0) {

				result1 = 1D / (1D + Math.exp(2.5506D - 0.4242D * Math.log(B13)
						- 2.9502D * Math.log(B14 / B13) + 2.1765D
						* Math.log(B14 / B13) * Math.log(B14 / B13)) + Math
						.exp(-3.2842D + 0.4072D * Math.log(B13) + 1.9238D
								* Math.log(B14 / B13) + 2.8952D
								* Math.log(B14 / B13) * Math.log(B14 / B13)));

				result2 = Math.exp(2.5506D - 0.4242D * Math.log(B13) - 2.9502D
						* Math.log(B14 / B13) + 2.1765D * Math.log(B14 / B13)
						* Math.log(B14 / B13))
						/ (1D + Math.exp(2.5506D - 0.4242D * Math.log(B13)
								- 2.9502D * Math.log(B14 / B13) + 2.1765D
								* Math.log(B14 / B13) * Math.log(B14 / B13)) + Math
									.exp(-3.2842D + 0.4072D * Math.log(B13)
											+ 1.9238D * Math.log(B14 / B13)
											+ 2.8952D * Math.log(B14 / B13)
											* Math.log(B14 / B13)));

				result3 = Math.exp(-3.2842D + 0.4072D * Math.log(B13) + 1.9238D
						* Math.log(B14 / B13) + 2.8952D * Math.log(B14 / B13)
						* Math.log(B14 / B13))
						/ (1D + Math.exp(2.5506D - 0.4242D * Math.log(B13)
								- 2.9502D * Math.log(B14 / B13) + 2.1765D
								* Math.log(B14 / B13) * Math.log(B14 / B13)) + Math
									.exp(-3.2842D + 0.4072D * Math.log(B13)
											+ 1.9238D * Math.log(B14 / B13)
											+ 2.8952D * Math.log(B14 / B13)
											* Math.log(B14 / B13)));
			} else {

				result1 = 1D / (1D + Math.exp(3.3265D - 0.3477D * Math.log(B13)
						- 0.4501D * Math.log(B16) - 5.6713D
						* Math.log(B14 / B13) + 1.0781D * Math.log(B14 / B13)
						* Math.log(B14 / B13) + 1.0529D * Math.log(B14 / B13)
						* Math.log(B16)) + Math.exp(-5.0661D + 0.3813D
						* Math.log(B13) + 0.5452D * Math.log(B16) - 5.2825D
						* Math.log(B14 / B13) + 1.3498D * Math.log(B14 / B13)
						* Math.log(B14 / B13) + 2.1392D * Math.log(B14 / B13)
						* Math.log(B16)));

				result2 = Math.exp(3.3265D - 0.3477D * Math.log(B13) - 0.4501D
						* Math.log(B16) - 5.6713D * Math.log(B14 / B13)
						+ 1.0781D * Math.log(B14 / B13) * Math.log(B14 / B13)
						+ 1.0529D * Math.log(B14 / B13) * Math.log(B16))
						/ (1D + Math
								.exp(3.3265D - 0.3477D * Math.log(B13)
										- 0.4501D * Math.log(B16) - 5.6713D
										* Math.log(B14 / B13) + 1.0781D
										* Math.log(B14 / B13)
										* Math.log(B14 / B13) + 1.0529D
										* Math.log(B14 / B13) * Math.log(B16)) + Math
								.exp(-5.0661D + 0.3813D * Math.log(B13)
										+ 0.5452D * Math.log(B16) - 5.2825D
										* Math.log(B14 / B13) + 1.3498D
										* Math.log(B14 / B13)
										* Math.log(B14 / B13) + 2.1392D
										* Math.log(B14 / B13) * Math.log(B16)));

				result3 = Math.exp(-5.0661D + 0.3813D * Math.log(B13) + 0.5452D
						* Math.log(B16) - 5.2825D * Math.log(B14 / B13)
						+ 1.3498D * Math.log(B14 / B13) * Math.log(B14 / B13)
						+ 2.1392D * Math.log(B14 / B13) * Math.log(B16))
						/ (1D + Math
								.exp(3.3265D - 0.3477D * Math.log(B13)
										- 0.4501D * Math.log(B16) - 5.6713D
										* Math.log(B14 / B13) + 1.0781D
										* Math.log(B14 / B13)
										* Math.log(B14 / B13) + 1.0529D
										* Math.log(B14 / B13) * Math.log(B16)) + Math
								.exp(-5.0661D + 0.3813D * Math.log(B13)
										+ 0.5452D * Math.log(B16) - 5.2825D
										* Math.log(B14 / B13) + 1.3498D
										* Math.log(B14 / B13)
										* Math.log(B14 / B13) + 2.1392D
										* Math.log(B14 / B13) * Math.log(B16)));
			}

			// Log.e("PUL M6", name + " " + B13 + " " + B14 + " " + B15 + " "
			// + B16 + " " + result1 + " " + result2 + " " + result3);

			try {
				result1 = Double.valueOf(new DecimalFormat("##.#").format(
						result1 * 100D).replaceAll(",", "."));
				result2 = Double.valueOf(new DecimalFormat("##.#").format(
						result2 * 100D).replaceAll(",", "."));
				result3 = Double.valueOf(new DecimalFormat("##.#").format(
						result3 * 100D).replaceAll(",", "."));

				ArrayList<Double> results = new ArrayList<Double>(3);
				results.add(result1);
				results.add(result2);
				results.add(result3);
				return results;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static ArrayList<Double> calculatorViability(double B13, int B14,
			int B15, double B16, double B17) {

		if (B13 != -1 && B14 != -1 && B15 != -1 && B16 != -1 && B17 != -1) {

			double E13 = -0.1D;
			if (B13 != 0) {
				E13 = -2 * ((B13 >= 35 && B13 <= 39) ? 1 : 0) - 4
						* (B13 >= 40 ? 1 : 0);
			} else {
				E13 = 0D;
			}

			double E14 = -0.1D;
			if (B14 != 0) {
				E14 = -3 * (B14 == 1 ? 1 : 0) - 5 * (B14 == 2 ? 1 : 0) - 8
						* (B14 == 3 ? 1 : 0) - 10 * (B14 == 4 ? 1 : 0);
			} else {
				E14 = 0D;
			}

			double E15 = -0.1D;
			if (B15 != 0) {
				E15 = -4 * (B15 == 1 ? 1 : 0);
			} else {
				E15 = 0D;
			}

			double E16 = -0.1D;
			if (B15 != 0 && B16 != 0) {
				E16 = 1 * ((B15 == 2 && B16 > 0 && B16 < 10) ? 1 : 0) - 3
						* ((B15 == 2 && B16 >= 15 && B16 < 20) ? 1 : 0) - 8
						* ((B15 == 2 && B16 >= 20 && B16 < 25) ? 1 : 0) - 14
						* ((B15 == 2 && B16 >= 25 && B16 < 30) ? 1 : 0) - 22
						* ((B15 == 2 && B16 >= 30 && B16 < 35) ? 1 : 0) - 32
						* ((B15 == 2 && B16 >= 35 && B16 < 40) ? 1 : 0) - 44
						* ((B15 == 2 && B16 >= 40 && B16 < 45) ? 1 : 0) - 57
						* ((B15 == 2 && B16 >= 45 && B16 < 50) ? 1 : 0) - 86
						* ((B15 == 2 && B16 >= 50) ? 1 : 0) + 1
						* ((B15 == 1 && B16 > 0 && B16 < 5) ? 1 : 0) + 3
						* ((B15 == 1 && B16 >= 5 && B16 < 10) ? 1 : 0) + 5
						* ((B15 == 1 && B16 >= 10 && B16 < 15) ? 1 : 0) + 7
						* ((B15 == 1 && B16 >= 15 && B16 < 20) ? 1 : 0) + 10
						* ((B15 == 1 && B16 >= 20 && B16 < 25) ? 1 : 0) + 12
						* ((B15 == 1 && B16 >= 25 && B16 < 30) ? 1 : 0) + 15
						* ((B15 == 1 && B16 >= 30 && B16 < 35) ? 1 : 0) + 17
						* ((B15 == 1 && B16 >= 35 && B16 < 40) ? 1 : 0) + 20
						* ((B15 == 1 && B16 >= 40 && B16 < 45) ? 1 : 0) + 22
						* ((B15 == 1 && B16 >= 45 && B16 < 50) ? 1 : 0) + 28
						* ((B15 == 1 && B16 >= 50) ? 1 : 0);
			} else {
				E16 = 0D;
			}

			double E17 = -0.1D;
			if (B17 != 0) {
				E17 = -3 * (B17 < 1 ? 1 : 0) - 2
						* ((B17 >= 1 && B17 < 2) ? 1 : 0) - 1
						* ((B17 >= 2 && B17 < 3) ? 1 : 0) - 1
						* ((B17 >= 5 && B17 < 6) ? 1 : 0) - 2
						* ((B17 >= 6 && B17 < 7) ? 1 : 0) - 4
						* (B17 >= 7 ? 1 : 0);
			} else {
				E17 = 0D;
			}

			double E18 = 0D;
			if (E13 != -0.1D && E14 != -0.1D && E15 != -0.1D && E16 != -0.1D
					&& E17 != -0.1D) {
				E18 = E13 + E14 + E15 + E16 + E17;
			}

			double result1 = 1D / (1D + Math
					.exp(-(1.549042D + 0.39048875D * E18)));
			double result2 = 1D - result1;

			try {
				result1 = Double.valueOf(new DecimalFormat("##.#").format(
						result1 * 100D).replaceAll(",", "."));
				result2 = Double.valueOf(new DecimalFormat("##.#").format(
						result2 * 100D).replaceAll(",", "."));
				ArrayList<Double> results = new ArrayList<Double>(2);
				results.add(result1);
				results.add(result2);
				return results;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static String readFileStringAssets(Context context, String fileName) {
		StringBuffer stringBuffer = new StringBuffer();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(context
					.getAssets().open(fileName, Context.MODE_WORLD_READABLE),
					"UTF-8"));
			String line = null;
			do {
				line = bufferedReader.readLine();
				if (line != null) {
					stringBuffer.append(line).append("\n");
				} else {
					break;
				}
			} while (true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stringBuffer.toString();
	}

	private static void sendMail(Activity activity, Spanned mailSpanned) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/html");
		// i.putExtra(Intent.EXTRA_STREAM, Uri.parse("mailto"));
		// i.putExtra(Intent.EXTRA_EMAIL,
		// new String[] { ANDROID_SUPPORT_EMAIL });
		i.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
		i.putExtra(Intent.EXTRA_TEXT, mailSpanned);

		activity.startActivity(createEmailOnlyChooserIntent(activity, i,
				"Send email"));
	}

	private static Intent createEmailOnlyChooserIntent(Context context,
			Intent intent, CharSequence chooserTitle) {
		Stack<Intent> intents = new Stack<Intent>();
		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
				"info@domain.com", null));
		List<ResolveInfo> activities = context.getPackageManager()
				.queryIntentActivities(i, 0);

		for (ResolveInfo ri : activities) {
			Intent target = new Intent(intent);
			target.setPackage(ri.activityInfo.packageName);
			intents.add(target);
		}

		if (!intents.isEmpty()) {
			Intent chooserIntent = Intent.createChooser(intents.remove(0),
					chooserTitle);
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					intents.toArray(new Parcelable[intents.size()]));

			return chooserIntent;
		} else {
			return Intent.createChooser(intent, chooserTitle);
		}
	}
}
