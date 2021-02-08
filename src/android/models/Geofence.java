/**
 * Geofence super class
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.models;

import android.location.Location;

public class Geofence implements GeofenceInterface {
	/**
	 * Identifier for geofence
	 *
	 * @var String
	 */
	protected String id = "";

	/**
	 * State of the geofence
	 *
	 * @var String
	 */
	protected String state = "";

	/**
	 * Construct geofence
	 *
	 * @param id Identifier for geofence
	 *
	 * @return void
	 */
	public Geofence(String id) {
		this.id = id;
	}

	/**
	 * Return geofence state
	 *
	 * @return String
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * Check if point is within the geofence
	 *
	 * @param gps_point Point to check
	 *
	 * @return boolean
	 */
	public boolean isPointWithinGeofence(Location gps_point) throws Exception {
		throw new Exception("`isPointWithinGeofence` must be overwritten in subclass");
	}

	/**
	 * Set geofence state
	 *
	 * @param state State to set
	 *
	 * @return void
	 */
	public void setState(String state) {
		this.state = state;
	}
}
