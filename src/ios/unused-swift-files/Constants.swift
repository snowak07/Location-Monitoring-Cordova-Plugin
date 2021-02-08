/**
 * Constants within the plugin
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
import Foundation;
import CoreLocation;
import UIKit;

class Constants {
	/**
	 * API URL to use when saving the GPS coordinates
	 *
	 * @var String
	 */
	public static let GPS_COORDINATES_API_URL: String = "gps-coordinates/save-coordinates";

	/**
	 * Number of locations to save before saving to api request
	 *
	 * @var Int
	 */
	public static let NUM_LOCATIONS_BEFORE_API_REQUEST: Int = 5;

	/**
	 * Current background task id
	 *
	 * @var UIBackgroundTaskIdentifier?
	 */
	public static var current_background_task_id: UIBackgroundTaskIdentifier?;

	/**
	 * Database helpers
	 *
	 * @var DatabaseHelpers
	 */
	public static var database_helpers = DatabaseHelpers();

	/**
	 * GPS Monitoring Controller
	 *
	 * @var GPSMonitoringController
	 */
	public static var gps_monitoring_controller: GPSMonitoringController = GPSMonitoringController();

	/**
	 * Last location collected
	 *
	 * @var CLLocation?
	 */
	public static var last_position: CLLocation? = nil;

	/**
	 * Location Manager
	 *
	 * @var CLLocationManager!
	 */
	public static var location_manager: CLLocationManager! = nil;

	/**
	 * Status of monitoring
	 *
	 * @var String
	 */
	public static var monitoring_status: String = "";

	/**
	 * Status of permission
	 *
	 * @var String
	 */
	public static var permission_status: String = "";

	/**
	 * Timer object used for limiting how often we sample location
	 *
	 * @var Timer?
	 */
	public static var timer: Timer? = nil;
}
