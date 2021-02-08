/**
 * GPSDatabaseService Object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelpers extends SQLiteOpenHelper {
	/**
	 * Name of database
	 *
	 * @var String
	 */
	public static final String DATABASE_NAME = "app_database";

	/**
	 * Version of database
	 *
	 * @var int
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * Database table for events
	 *
	 * @var String
	 */
	public final String EVENTS_DATABASE_TABLE = "events";

	/**
	 * Database table for gps coordinates
	 *
	 * @var String
	 */
	public final String GPS_COORDINATES_DATABASE_TABLE = "gps_coordinates";

	/**
	 * Database table for settings
	 *
	 * @var String
	 */
	public final String SETTINGS_DATABASE_TABLE = "settings";

	/**
	 * Database table for service status
	 *
	 * @var String
	 */
	public final String SERVICE_STATUS_DATABASE_TABLE = "service_status";

	/**
	 * Constructor
	 *
	 * @param context		Context to operate in
	 *
	 * @return void
	 */
	public DatabaseHelpers(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Handle when creating database
	 *
	 * @param database		Database to create
	 *
	 * @return void
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		try {
			database.execSQL(""
				+ "CREATE TABLE IF NOT EXISTS "
				+ 		this.GPS_COORDINATES_DATABASE_TABLE
				+ "("
				+ "		id CHAR(200) PRIMARY KEY NOT NULL, "
				+ "		access_token CHAR(200), "
				+ "		latitude CHAR(50) NOT NULL, "
				+ "		longitude CHAR(50) NOT NULL, "
				+ "		other_data TEXT, "
				+ "		create_date CHAR(200) NOT NULL"
				+ ")");
		} catch (Exception error) {
			error.printStackTrace();
		}

		try {
			database.execSQL(""
				+ "CREATE TABLE IF NOT EXISTS "
				+ 		this.SETTINGS_DATABASE_TABLE
				+ "("
				+ "		key CHAR(200) PRIMARY KEY NOT NULL, "
				+ "		value TEXT "
				+ ")");
		} catch (Exception error) {
			error.printStackTrace();
		}

		try {
			database.execSQL(""
				+ "CREATE TABLE IF NOT EXISTS "
				+ 		this.SERVICE_STATUS_DATABASE_TABLE
				+ "("
				+ "		service CHAR(200) PRIMARY KEY NOT NULL, "
				+ "		status TEXT, "
				+ "		create_date CHAR(200) NOT NULL "
				+ ")");
		} catch (Exception error) {
			error.printStackTrace();
		}

		try {
			database.execSQL(""
				+ "CREATE TABLE IF NOT EXISTS "
				+ 		this.EVENTS_DATABASE_TABLE
				+ "("
				+ "		id CHAR(200) PRIMARY KEY NOT NULL, "
				+ "		access_token CHAR(200) NOT NULL, "
				+ "		service TEXT NOT NULL, "
				+ "		action TEXT NOT NULL, "
				+ "		objects TEXT, "
				+ "		create_date CHAR(200) NOT NULL "
				+ ")");
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Handle when upgrading database
	 *
	 * @param database				Database to create
	 * @param old_version			Old version
	 * @param new_version			New version
	 *
	 * @return void
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int old_version, int new_version) {
		if (new_version == 1) {
			this.onCreate(database);
		}

		this.onCreate(database);
	}
}
