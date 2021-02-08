/**
 * GPSDatabaseTableHelpers Object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.wisc.chess.plugins.chesslocationmonitoring.models.GPSDatabaseRowObject;

public class GPSDatabaseTableHelpers {
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
	public GPSDatabaseTableHelpers(Context context) {
		this.database_helpers = new DatabaseHelpers(context);
	}

	/**
	 * Delete locations by their id
	 *
	 * @param ids		List of ids to delete
	 *
	 * @return void
	 */
	public void deleteLocationsById(JSONArray ids) throws JSONException {
		SQLiteDatabase database = this.database_helpers.getWritableDatabase();

		StringBuilder clause = new StringBuilder();
		ArrayList<String> args = new ArrayList<>();

		int i = 0;
		while (i < ids.length()) {
			String id = ids.get(i).toString();
			args.add(id);
			clause.append(" OR id = ? ");
			i = i + 1;
		}

		if (args.size() == 0) {
			return;
		}

		String query = ""
			+ "DELETE FROM "
			+ 	this.database_helpers.GPS_COORDINATES_DATABASE_TABLE
			+ " WHERE 0 = 1 " + clause.toString();

		try {
			database.execSQL(query, args.toArray());
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Return the specified number of locations ordered by create date
	 *
	 * @param n			Number to return
	 *
	 * @return void
	 */
	public ArrayList<GPSDatabaseRowObject> getFirstNLocations(int n) {
		SQLiteDatabase database = this.database_helpers.getReadableDatabase();

		ArrayList<GPSDatabaseRowObject> results = new ArrayList<>();
		String query = ""
			+ " SELECT * "
			+ " FROM "
			+		this.database_helpers.GPS_COORDINATES_DATABASE_TABLE
			+ " ORDER BY create_date ASC "
			+ " LIMIT " + Integer.toString(n);

		try {
			Cursor cursor = database.rawQuery(query, null);
			while (cursor.moveToNext()) {
				results.add(new GPSDatabaseRowObject(cursor));
			}
			cursor.close();
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}

		return results;
	}

	/**
	 * Return the specified number of locations ordered by create date
	 *
	 * @param n			Number to return
	 *
	 * @return void
	 */
	public ArrayList<GPSDatabaseRowObject> getLastNLocations(int n) {
		SQLiteDatabase database = this.database_helpers.getReadableDatabase();

		ArrayList<GPSDatabaseRowObject> results = new ArrayList<>();
		String query = ""
			+ " SELECT * "
			+ " FROM "
			+		this.database_helpers.GPS_COORDINATES_DATABASE_TABLE
			+ " ORDER BY create_date DESC "
			+ " LIMIT " + Integer.toString(n);

		try {
			Cursor cursor = database.rawQuery(query, null);
			while (cursor.moveToNext()) {
				results.add(new GPSDatabaseRowObject(cursor));
			}
			cursor.close();
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}

		return results;
	}

	/**
	 * Return last location time
	 *
	 * @return String
	 */
	public String getLastLocationTime() {
		SQLiteDatabase database = this.database_helpers.getReadableDatabase();

		String last_location_time = "0";
		String query = "SELECT create_date FROM " + this.database_helpers.GPS_COORDINATES_DATABASE_TABLE + " ORDER BY create_date DESC LIMIT 1";

		try {
			Cursor cursor = database.rawQuery(query, null);
			if (cursor.moveToFirst() || cursor.getCount() > 0) {
				last_location_time = cursor.getString(0);
			}

			cursor.close();

		} catch (Exception error) {
			error.printStackTrace();
		}

		database.close();

		return last_location_time;
	}

	/**
	 * Return the number of locations
	 *
	 * @return long
	 */
	public long getLocationCount() {
		SQLiteDatabase database = this.database_helpers.getReadableDatabase();

		long count = 0;
		String query = "SELECT COUNT(*) FROM " + this.database_helpers.GPS_COORDINATES_DATABASE_TABLE;

		try {
			Cursor cursor = database.rawQuery(query, null);
			cursor.moveToFirst();
			count = cursor.getLong(0);
			cursor.close();
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}

		return count;
	}

	/**
	 * Insert location into the database
	 *
	 * @NOTE
	 * 		other_data: Contains the following encoded fields to save on database space:
	 * 			hacc: Horizontal accuracy in meters
	 * 			spd: Speed in meters
	 * 			idim: Is app in Device Idle Mode (0 = false, 1 = true, -1 = not known)
	 * 			ii: Is app interactive (0 = false, 1 = true)
	 * 			ipsm: Is device in Power Saving mode (0 = false, 1 = true)
	 * 			iibo: Is app ignoring battery optimizations (0 = false, 1 = true, -1 = not known)
	 * 			ic: Is device charging (0 = false, 1 = true, -1 = not known)
	 *
	 * 		For more information about "hacc" and "spd", see https://developer.android.com/reference/android/location/Location
	 * 		For more information about "idim", "ii", "ipsm", and "iibo", see https://developer.android.com/reference/android/os/PowerManager
	 * 		For more information about "ic", see https://developer.android.com/reference/android/os/BatteryManager
	 *
	 * @param context			Context we are operating in
	 * @param access_token		Access token of user
	 * @param location			Location to save
	 *
	 * @return void
	 */
	public void insertLocation(Context context, String access_token, Location location) {
		String package_name = context.getPackageName();
		String latitude = Double.valueOf(location.getLatitude()).toString();
		String longitude = Double.valueOf(location.getLongitude()).toString();
		String other_data = "";

		double accuracy = location.getAccuracy();
		accuracy = Math.round(accuracy * 100) / 100.0;

		double speed = location.getSpeed();
		speed = Math.round(speed * 100) / 100.0;

		try {
			JSONObject other_data_object = new JSONObject();

			other_data_object.put("hacc", accuracy);
			other_data_object.put("spd", speed);
			other_data = other_data_object.toString();

			PowerManager power_manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			BatteryManager battery_manager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

			int is_interactive = (power_manager.isInteractive()) ? 1 : 0;
			int is_power_save_mode = (power_manager.isPowerSaveMode()) ? 1 : 0;

			int is_charging = -1;
			int is_ignoring_battery_optimizations = -1;
			int is_device_idle_mode = -1;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				is_charging = (battery_manager.isCharging()) ? 1 : 0;
				is_ignoring_battery_optimizations = (power_manager.isIgnoringBatteryOptimizations(package_name)) ? 1 : 0;
				is_device_idle_mode = (power_manager.isDeviceIdleMode()) ? 1 : 0;
			}

			other_data_object.put("idim", is_device_idle_mode);
			other_data_object.put("ii", is_interactive);
			other_data_object.put("ipsm", is_power_save_mode);
			other_data_object.put("iibo", is_ignoring_battery_optimizations);
			other_data_object.put("ic", is_charging);

			other_data = other_data_object.toString();

		} catch (Exception error) {
			error.printStackTrace();
		}

		SQLiteDatabase database = this.database_helpers.getWritableDatabase();

		String unix_time_string = Helpers.getUnixTimeAsString();
		Helpers.log("GPSDatabaseTableHelpers.insertLocation: New location time: " + unix_time_string);

		String query1 = ""
			+ " DELETE FROM "
			+	this.database_helpers.EVENTS_DATABASE_TABLE
			+ " WHERE id = ?";

		String[] args1 = new String[] { unix_time_string };

		String query2 = ""
			+ "INSERT INTO "
			+ 	this.database_helpers.GPS_COORDINATES_DATABASE_TABLE
			+ "		(id, access_token, latitude, longitude, other_data, create_date) "
			+ " VALUES "
			+ "		(?, ?, ?, ?, ?, ?) ";

		String[] args2 = new String[] {
			unix_time_string,
			access_token,
			latitude,
			longitude,
			other_data,
			unix_time_string
		};

		try {
			database.execSQL(query1, args1);
			database.execSQL(query2, args2);
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
