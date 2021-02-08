/**
 * GPS Point Object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.models;

 public class GPSPoint {
	/**
	 * Latitude of point
	 *
	 * @var double
	 */
	protected double latitude;

	/**
	 * Longitude of point
	 *
	 * @var double
	 */
	protected double longitude;

	/**
	 * Constructor for object
	 *
	 * @param latitude			Latitude of point
	 * @param longitude			Longitude of point
	 *
	 * @return void
	 */
	public GPSPoint(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
}