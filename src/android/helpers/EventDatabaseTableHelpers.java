/**
 * GPSDatabaseTableHelpers Object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.wisc.chess.plugins.chesslocationmonitoring.models.EventDatabaseRowObject;

public class EventDatabaseTableHelpers {
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
	public EventDatabaseTableHelpers(Context context) {
		this.database_helpers = new DatabaseHelpers(context);
	}

	/**
	 * Delete events by their id
	 *
	 * @param ids		List of ids to delete
	 *
	 * @return void
	 */
	public void deleteEventsById(JSONArray ids) throws JSONException {
		SQLiteDatabase database = this.database_helpers.getWritableDatabase();

		StringBuilder clause = new StringBuilder();
		ArrayList<String> args = new ArrayList<>();

		int i = 0;
		while (i < ids.length()) {
			String id = Double.toString(Double.parseDouble(ids.get(i).toString()));
			args.add(id);
			clause.append(" OR id = ? ");
			i = i + 1;
		}

		if (args.size() == 0) {
			return;
		}

		String query = ""
			+ "DELETE FROM "
			+ 	this.database_helpers.EVENTS_DATABASE_TABLE
			+ " WHERE 0 = 1 " + clause.toString();

		try {
			database.execSQL(query, args.toArray());
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Return the specified number of events ordered by create date
	 *
	 * @param n			Number to return
	 *
	 * @return void
	 */
	public ArrayList<EventDatabaseRowObject> getFirstNEvents(int n) {
		SQLiteDatabase database = this.database_helpers.getReadableDatabase();

		ArrayList<EventDatabaseRowObject> results = new ArrayList<>();
		String query = ""
			+ " SELECT * "
			+ " FROM "
			+		this.database_helpers.EVENTS_DATABASE_TABLE
			+ " ORDER BY create_date DESC "
			+ " LIMIT " + Integer.toString(n);

		try {
			Cursor cursor = database.rawQuery(query, null);
			while (cursor.moveToNext()) {
				results.add(new EventDatabaseRowObject(cursor));
			}
			cursor.close();
			database.close();

		} catch (Exception error) {
			error.printStackTrace();
		}

		return results;
	}

	/**
	 * Return the number of events
	 *
	 * @return int
	 */
	public long getEventCount() {
		SQLiteDatabase database = this.database_helpers.getReadableDatabase();

		long count = 0;
		String query = "SELECT COUNT(*) FROM " + this.database_helpers.EVENTS_DATABASE_TABLE;

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
	 * Insert event
	 *
	 * @param access_token		Access token of user
	 * @param service			Service user is in
	 * @param action			Action taken by user
	 * @param objects			Objects being acted on
	 *
	 * @return void
	 */
	public void insertEvent(String access_token, String service, String action, JSONObject objects) {
		String objects_string = "";

		if (objects != null) {
			objects_string = objects.toString();
		}

		if (objects_string.isEmpty()) {
			objects_string = "";
		}

		SQLiteDatabase database = this.database_helpers.getWritableDatabase();

		String unix_time_string = Helpers.getUnixTimeAsString();
		Helpers.log("New event time: " + unix_time_string);

		String query1 = ""
			+ " DELETE FROM "
			+	this.database_helpers.EVENTS_DATABASE_TABLE
			+ " WHERE id = ?";

		String[] args1 = new String[] { unix_time_string };

		String query2 = ""
			+ "INSERT INTO "
			+ 	this.database_helpers.EVENTS_DATABASE_TABLE
			+ "		(id, access_token, service, action, objects, create_date) "
			+ " VALUES "
			+ "		(?, ?, ?, ?, ?, ?) ";

		String[] args2 = new String[] {
			unix_time_string,
			access_token,
			service,
			action,
			objects_string,
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
