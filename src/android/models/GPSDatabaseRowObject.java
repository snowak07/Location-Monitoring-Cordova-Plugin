/**
 * Row in GPS Database Table
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.models;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

public class GPSDatabaseRowObject {
	/**
	 * Latitude of point
	 *
	 * @var String
	 */
	protected String id;

	/**
	 * Access token of user
	 *
	 * @var String
	 */
	protected String access_token;

	/**
	 * Latitude of point
	 *
	 * @var String
	 */
	protected String latitude;

	/**
	 * Longitude of point
	 *
	 * @var String
	 */
	protected String longitude;

	/**
	 * Other data to save
	 *
	 * @var String
	 */
	protected String other_data;

	/**
	 * Create date
	 *
	 * @var String
	 */
	protected String create_date;

	/**
	 * Constructor for object
	 *
	 * @return void
	 */
	public GPSDatabaseRowObject() { }

	/**
	 * Constructor for object
	 *
	 * @param cursor		Cursor to build from
	 *
	 * @return void
	 */
	public GPSDatabaseRowObject(Cursor cursor) {
		this.buildFromCursor(cursor);
	}

	/**
	 * Build from a database cursor
	 *
	 * @param cursor		Cursor to build from
	 *
	 * @return void
	 */
	public void buildFromCursor(Cursor cursor) {
		this.id = cursor.getString(cursor.getColumnIndex("id"));
		this.access_token = cursor.getString(cursor.getColumnIndex("access_token"));
		this.latitude = cursor.getString(cursor.getColumnIndex("latitude"));
		this.longitude = cursor.getString(cursor.getColumnIndex("longitude"));
		this.other_data = cursor.getString(cursor.getColumnIndex("other_data"));
		this.create_date = cursor.getString(cursor.getColumnIndex("create_date"));
	}

	/**
	 * Return latitude
	 *
	 * @return String
	 */
	public String getLatitude() {
		return this.latitude;
	}

	/**
	 * Return longitude
	 *
	 * @return String
	 */
	public String getLongitude() {
		return this.longitude;
	}

	/**
	 * Return as a JSON object
	 *
	 * @return JSONObject
	 *
	 * @throws JSONException
	 */
	public JSONObject returnAsJSONObject() throws JSONException {
		JSONObject data = new JSONObject();

		data.put("client_database_id", this.id);
		data.put("access_token", this.access_token);
		data.put("latitude", this.latitude);
		data.put("longitude", this.longitude);
		data.put("other_data", this.other_data);
		data.put("create_date", this.create_date);

		return data;
	}
}
