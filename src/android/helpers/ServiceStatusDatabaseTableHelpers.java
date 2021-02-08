/**
 * Service Status Database Table Helpers Object
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

import edu.wisc.chess.plugins.chesslocationmonitoring.models.ServiceStatusDatabaseRowObject;

public class ServiceStatusDatabaseTableHelpers {
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
	public ServiceStatusDatabaseTableHelpers(Context context) {
		this.database_helpers = new DatabaseHelpers(context);
	}

	/**
	 * Delete status from database
	 *
	 * @param service		Service to delete
	 *
	 * @return void
	 */
	public void deleteStatusByService(String service) {
		SQLiteDatabase database = this.database_helpers.getWritableDatabase();

		String query = ""
			+ " DELETE FROM "
			+ 		this.database_helpers.SERVICE_STATUS_DATABASE_TABLE
			+ " WHERE service = ?";

		String[] args = new String[] { service };

		try {
			database.execSQL(query, args);
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Get status by service
	 *
	 * @param service			Service to retrieve status for
	 *
	 * @return ServiceStatusDatabaseRowObject
	 */
	public ServiceStatusDatabaseRowObject getByService(String service) {
		SQLiteDatabase database = this.database_helpers.getReadableDatabase();

		String query = ""
			+ " SELECT * "
			+ " FROM "
			+		this.database_helpers.SERVICE_STATUS_DATABASE_TABLE
			+ " WHERE service = ? ";

		String[] args = new String[] { service };

		ServiceStatusDatabaseRowObject status = null;
		try {
			Cursor cursor = database.rawQuery(query, args);
			while (cursor.moveToNext()) {
				status = new ServiceStatusDatabaseRowObject(cursor);
			}

			cursor.close();
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}

		return status;
	}

	/**
	 * Insert a service status
	 *
	 * @param service		Service name
	 * @param status		Status
	 *
	 * @return void
	 */
	public void insertServiceStatus(String service, String status) {
		SQLiteDatabase database = this.database_helpers.getWritableDatabase();

		String unix_time_string = Helpers.getUnixTimeAsString();
		Helpers.log("New event time: " + unix_time_string);

		String query1 = ""
			+ " DELETE FROM "
			+	this.database_helpers.SERVICE_STATUS_DATABASE_TABLE
			+ " WHERE service = ?; ";

		String[] args1 = new String[] { service };

		String query2 = ""
			+ " INSERT INTO "
			+ 	this.database_helpers.SERVICE_STATUS_DATABASE_TABLE
			+ "		(service, status, create_date) "
			+ " VALUES "
			+ "		(?, ?, ?) ";

		String[] args2 = new String[] { service, status, unix_time_string };

		try {
			database.execSQL(query1, args1);
			database.execSQL(query2, args2);
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
