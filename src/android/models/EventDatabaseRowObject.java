/**
 * Row in Events Table
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class EventDatabaseRowObject {
	/**
	 * Id of row
	 *
	 * @var String
	 */
	protected String id;

	/**
	 * Access token for user
	 *
	 * @var String
	 */
	protected String access_token;

	/**
	 * Service
	 *
	 * @var String
	 */
	protected String service;

	/**
	 * Action
	 *
	 * @var String
	 */
	protected String action;

	/**
	 * Objects
	 *
	 * @var JSONObject
	 */
	protected JSONObject objects;

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
	public EventDatabaseRowObject() { }

	/**
	 * Constructor for object
	 *
	 * @param cursor		Cursor to build from
	 *
	 * @return void
	 */
	public EventDatabaseRowObject(Cursor cursor) {
		try {
			this.buildFromCursor(cursor);

		} catch (JSONException error) {
			// Do Nothing
		}
	}

	/**
	 * Build from a database cursor
	 *
	 * @param cursor		Cursor to build from
	 *
	 * @return void
	 *
	 * @throws JSONException
	 */
	public void buildFromCursor(Cursor cursor) throws JSONException {
		this.id = cursor.getString(cursor.getColumnIndex("id"));
		this.access_token = cursor.getString(cursor.getColumnIndex("access_token"));
		this.service = cursor.getString(cursor.getColumnIndex("service"));
		this.action = cursor.getString(cursor.getColumnIndex("action"));
		this.create_date = cursor.getString(cursor.getColumnIndex("create_date"));

		this.objects = new JSONObject(cursor.getString(cursor.getColumnIndex("objects")));
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
		data.put("service", this.service);
		data.put("action", this.action);
		data.put("objects", this.objects);
		data.put("create_date", this.create_date);

		return data;
	}
}