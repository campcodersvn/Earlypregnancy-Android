package com.example.earlypregnancy.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseManager {

	private static final String DATABASE_PATH_FINAL = "/data/data/<package>/databases/";
	private static String DATABASE_NAME;
	private static String DATABASE_PATH;

	private static Context context;

	public static SQLiteDatabase database;

	public static boolean openDatabase(Context context, String databaseName) {
		try {
			if (database == null
					|| (database != null && database.isOpen() == false)) {

				SQLiteDatabaseManager.context = context;
				DATABASE_NAME = databaseName;
				DATABASE_PATH = DATABASE_PATH_FINAL.replace("<package>",
						SQLiteDatabaseManager.context.getPackageName());
				String databasePath = DATABASE_PATH + DATABASE_NAME;
				// Log.e(CLASS_NAME, "DATABSE: " + databasePath);

				try {
					database = SQLiteDatabase.openDatabase(databasePath, null,
							SQLiteDatabase.OPEN_READWRITE);
					// Log.e(CLASS_NAME, "DATABASE OPENED");
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (copyDatabase(databasePath)) {
					try {
						database = SQLiteDatabase.openDatabase(databasePath,
								null, SQLiteDatabase.OPEN_READWRITE);
						// Log.e(CLASS_NAME, "DATABASE TRY OPENED");
						return true;
					} catch (Exception ex) {
						ex.printStackTrace();
						return false;
					}
				}
			} else if (database != null && database.isOpen()) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	// reload db
	public static void reinitDb() {
		String databasePath = DATABASE_PATH + DATABASE_NAME;
		copyDatabase(databasePath);
	}

	public static boolean isOpen() {
		try {
			if (database != null) {
				return database.isOpen();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static void closeDatabase() {
		try {
			if (isOpen()) {
				database.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void deleteDatabase() {
		try {
			closeDatabase();
			context.deleteDatabase(DATABASE_NAME);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static boolean copyDatabase(String outFileName) {

		try {
			SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(context,
					outFileName, null, 1) {

				@Override
				public void onUpgrade(SQLiteDatabase db, int oldVersion,
						int newVersion) {
				}

				@Override
				public void onCreate(SQLiteDatabase db) {
				}
			};

			sqLiteOpenHelper.getReadableDatabase();

			InputStream inputStream = context.getAssets().open(DATABASE_NAME);
			OutputStream outputStream = new FileOutputStream(outFileName);

			byte[] buffer = new byte[2048];
			int length;

			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}

			outputStream.flush();
			inputStream.close();
			outputStream.close();
			sqLiteOpenHelper.close();
			// Log.e(CLASS_NAME, "DATABASE FINISH COPY");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
