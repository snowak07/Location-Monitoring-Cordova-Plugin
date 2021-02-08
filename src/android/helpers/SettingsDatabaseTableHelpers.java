/**
 * Settings Database Table Helpers Object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import edu.wisc.chess.plugins.chesslocationmonitoring.models.SettingsDatabaseRowObject;

public class SettingsDatabaseTableHelpers {
	/**
	 * Database helpers
	 *
	 * @var DatabaseHelpers
	 */
	DatabaseHelpers database_helpers = null;

	/**
	 * Constructor for the database
	 *
	 * @param context		Context to operate in
	 *
	 * @return void
	 */
	public SettingsDatabaseTableHelpers(Context context) {
		this.database_helpers = new DatabaseHelpers(context);
	}

	/**
	 * Delete settings from database
	 *
	 * @return void
	 */
	public void deleteSettings() {
		SQLiteDatabase database = this.database_helpers.getWritableDatabase();

		String query = ""
			+ "DELETE FROM "
			+ 	this.database_helpers.SETTINGS_DATABASE_TABLE;

		try {
			database.execSQL(query);
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Get settings by key
	 *
	 * @param key			Key to retrieve value for
	 *
	 * @return String
	 */
	public String getSettingByKey(String key) {
		SQLiteDatabase database = this.database_helpers.getReadableDatabase();

		String query = ""
			+ " SELECT * "
			+ " FROM "
			+		this.database_helpers.SETTINGS_DATABASE_TABLE
			+ " WHERE key = ? ";

		String[] args = new String[] { key };

		String value = "";
		try {
			Cursor cursor = database.rawQuery(query, args);
			while (cursor.moveToNext()) {
				value = cursor.getString(cursor.getColumnIndex("value"));
			}

			cursor.close();
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}

		return value;
	}

	/**
	 * Return settings in a HashMap
	 *
	 * @return void
	 */
	public HashMap<String, String> getSettings() {
		SQLiteDatabase database = this.database_helpers.getReadableDatabase();

		HashMap<String, String> results = new HashMap<>();
		String query = ""
			+ " SELECT * "
			+ " FROM "
			+	this.database_helpers.SETTINGS_DATABASE_TABLE;

		try {
			Cursor cursor = database.rawQuery(query, null);
			while (cursor.moveToNext()) {
				results.put(
					cursor.getString(cursor.getColumnIndex("key")),
					cursor.getString(cursor.getColumnIndex("value"))
				);
			}

			cursor.close();
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}

		return results;
	}

	/**
	 * Insert a setting
	 *
	 * @param key			Key of setting
	 * @param value			Value of setting
	 *
	 * @return void
	 */
	public void insertSetting(String key, String value) {
		SQLiteDatabase database = this.database_helpers.getWritableDatabase();

		String query1 = ""
			+ " DELETE FROM "
			+	this.database_helpers.SETTINGS_DATABASE_TABLE
			+ " WHERE key = ?; ";

		String[] args1 = new String[] { key };

		String query2 = ""
			+ " INSERT INTO "
			+ 	this.database_helpers.SETTINGS_DATABASE_TABLE
			+ "		(key, value) "
			+ " VALUES "
			+ "		(?, ?) ";

		String[] args2 = new String[] { key, value };

		try {
			database.execSQL(query1, args1);
			database.execSQL(query2, args2);
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Save a hashmap of settings a setting
	 *
	 * @param settings    Settings to save
	 *
	 * @return void
	 */
	public void saveSettings(HashMap<String, String> settings) {
		Object[] keys = settings.keySet().toArray();
		int i = 0;
		while (i < keys.length) {
			String key = keys[i].toString();
			String value = settings.get(key);

			this.insertSetting(key, value);
			i = i + 1;
		}
	}
}
