/**
 * Row in Service Status Table
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.models;

import android.database.Cursor;

public class ServiceStatusDatabaseRowObject {
	/**
	 * Service
	 *
	 * @var String
	 */
	protected String service;

	/**
	 * Status
	 *
	 * @var String
	 */
	protected String status;

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
	public ServiceStatusDatabaseRowObject() { }

	/**
	 * Constructor for object
	 *
	 * @param cursor		Cursor to build from
	 *
	 * @return void
	 */
	public ServiceStatusDatabaseRowObject(Cursor cursor) {
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
		this.service = cursor.getString(cursor.getColumnIndex("service"));
		this.status = cursor.getString(cursor.getColumnIndex("status"));
		this.create_date = cursor.getString(cursor.getColumnIndex("create_date"));
	}

	/**
	 * Return servive
	 *
	 * @return String
	 */
	public String getService() {
		return this.service;
	}

	/**
	 * Return status
	 *
	 * @return String
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Return create_date
	 *
	 * @return String
	 */
	public String getCreateDate() {
		return this.create_date;
	}

	/**
	 * Set servive
	 *
	 * @return void
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * Set status
	 *
	 * @return void
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Set create_date
	 *
	 * @return void
	 */
	public void setCreateDate(String create_date) {
		this.create_date = create_date;
	}
}