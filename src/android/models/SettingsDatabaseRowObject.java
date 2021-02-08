/**
 * Row in Settings Table
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.models;

import android.database.Cursor;

public class SettingsDatabaseRowObject {
	/**
	 * Key for setting
	 *
	 * @var String
	 */
	protected String key;

	/**
	 * Value for setting
	 *
	 * @var String
	 */
	protected String value;

	/**
	 * Constructor for object
	 *
	 * @return void
	 */
	public SettingsDatabaseRowObject() { }

	/**
	 * Constructor for object
	 *
	 * @param cursor		Cursor to build from
	 *
	 * @return void
	 */
	public SettingsDatabaseRowObject(Cursor cursor) {
		this.buildFromCursor(cursor);
	}

	/**
	 * Constructor for object
	 *
	 * @param key		Key to set
	 * @param value		Value to set
	 *
	 * @return void
	 */
	public SettingsDatabaseRowObject(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Build from a database cursor
	 *
	 * @param cursor		Cursor to build from
	 *
	 * @return void
	 */
	public void buildFromCursor(Cursor cursor) {
		this.key = cursor.getString(cursor.getColumnIndex("key"));
		this.value = cursor.getString(cursor.getColumnIndex("value"));
	}

	/**
	 * Return key
	 *
	 * @return String
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * Return value
	 *
	 * @return String
	 */
	public String getValue() {
		return this.value;
	}
}