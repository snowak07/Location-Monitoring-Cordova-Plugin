/**
 * Circular geofence class
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.models;

import android.location.Location;

import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.Helpers;

public class CircularGeofence extends Geofence {
	/**
	 * Center of geofence
	 *
	 * @var GPSPoint
	 */
	GPSPoint center;

	/**
	 * Radius of geofence (in meters)
	 *
	 * @var double
	 */
	double radius;

	/**
	 * Construct geofence
	 *
	 * @param id          Identifier for geofence
	 * @param center      Center of geofence
	 * @param radius      Radius of geofence (in meters)
	 *
	 * @return void
	 */
	public CircularGeofence(String id, GPSPoint center, double radius) {
		super(id);

		this.center = center;
		this.radius = radius;
	}

	/**
	 * Check if point is within the geofence
	 *
	 * @param gps_point Point to check
	 *
	 * @return boolean
	 */
	public boolean isPointWithinGeofence(Location gps_point) {
		if (this.radius <= 0) {
			return false;
		}

		float[] results = new float[3];
		Location.distanceBetween(gps_point.getLatitude(), gps_point.getLongitude(), center.latitude, center.longitude, results);

		if (results[0] < this.radius) {
			return true;
		}

		return false;
	}
}
