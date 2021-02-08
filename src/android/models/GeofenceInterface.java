/**
 * Interface for geofences
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.models;

import android.location.Location;

interface GeofenceInterface {
	String getState();
	boolean isPointWithinGeofence(Location gps_point) throws Exception;
	void setState(String state);
}
