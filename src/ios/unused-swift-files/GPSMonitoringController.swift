/**
 * Proxy for working with GPS Monitoring
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
import Foundation;
import CoreLocation;

class GPSMonitoringController: NSObject, CLLocationManagerDelegate {
	/**
	 * Construct the object
	 *
	 * @return void
	 */
	override init() {
		super.init();
		self.resetLocationManager();
	}

	/**
	 * Check whether location tracking is supported and enabled
	 *
	 * @return boolean
	 */
	private func checkLocationTrackingIsSupported() -> Bool {
		let has_gps_enabled = self.isLocationTrackingEnabled();
		if (!has_gps_enabled) {
			self.updateMonitoringStatus("gps not enabled");

		} else {
			self.updateMonitoringStatus("gps enabled");
		}

		return has_gps_enabled;
	}

	/**
	 * Return last location
	 *
	 * @return Data?
	 */
	func getLastLocation() -> Data? {
		if (Constants.last_position == nil) {
			return nil;
		}

		var data = Dictionary<String, Any>();
		data["latitude"] = Constants.last_position!.coordinate.latitude;
		data["longitude"] = Constants.last_position!.coordinate.longitude;

		return try! JSONSerialization.data(withJSONObject: data, options: JSONSerialization.WritingOptions.prettyPrinted);
	}

	/**
	 * Return monitoring status
	 *
	 * @return String
	 */
	func getMonitoringStatus() -> String {
		self.checkLocationTrackingIsSupported();
		return Constants.monitoring_status;
	}

	/**
	 * Return permission status
	 *
	 * @return String
	 */
	func getPermissionStatus() -> String {
		self.hasPermissionToLocation();
		return Constants.permission_status;
	}

	/**
	 * Return whether we have permission to location
	 *
	 * @return Bool
	 */
	func hasPermissionToLocation() -> Bool {
		self.requestLocationPermissions();

		let authorization_status = CLLocationManager.authorizationStatus();
		if (authorization_status == .notDetermined || authorization_status == .denied || authorization_status == .restricted) {
			self.updatePermissionStatus("not granted");
			return false;
		}

		var status = "granted";
		if (authorization_status == .authorizedAlways) {
			status += ", always";

		} else if (authorization_status == .authorizedWhenInUse) {
			status += ", always when in use";
		}

		self.updatePermissionStatus(status);
		return true;
	}

	/**
	 * Indicates if location services are enabled
	 *
	 * @return Bool
	 */
	func isLocationTrackingEnabled() -> Bool {
		return CLLocationManager.locationServicesEnabled();
	}

	/**
	 * Handle when we have a location update
	 *
	 * @param manager		Manager to use
	 * @param locations		Locations received
	 *
	 * @return void
	 */
	func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
		self.startTimer();

		var last_timestamp: Double? = nil;
		if (Constants.last_position != nil) {
			last_timestamp = Constants.last_position?.timestamp.timeIntervalSince1970;
		}

		let tracking_frequency_milliseconds = Constants.database_helpers.getUserSettingByKey(key: "tracking_frequency_milliseconds") as! Int;

		if (last_timestamp != nil) {
			print("GPSMonitoringController.locationManager: last_timestamp = " + String(last_timestamp!));
		} else {
			print("GPSMonitoringController.locationManager: last_timestamp = nil");
		}

		// Save the location in the database if it 0.9 * tracking_frequency_milliseconds seconds after the last update i.e. if frequency was 10, then 9 seconds after last update
		guard let location = locations.last else { return };
		if (last_timestamp == nil || (location.timestamp.timeIntervalSince1970 - last_timestamp!) >= (Double(tracking_frequency_milliseconds) / 1000 * 0.90)) {
			self.updateLocation(location);
		}
	}

	/**
	 * Handle when there is a location manager failure
	 *
	 * @param manager			Manager to use
	 * @param error			Error to handle
	 *
	 * @return void
	 */
	private func locationManager(manager: CLLocationManager, didFailWithError error: Error) {
		print(error.localizedDescription);
		self.startTimer();
	}

	/**
	 * Handle when authorization changes
	 *
	 * @param manager			Manager to use
	 * @param status			New status
	 *
	 * @return void
	 */
	private func locationManager(manager: CLLocationManager!, didChangeAuthorizationStatus status: CLAuthorizationStatus) {
		if (self.hasPermissionToLocation()) {
			self.start(callback: nil);
		} else {
			self.stop();
		}
	}

	/**
	 * Request location services
	 *
	 * @return void
	 */
	private func requestLocationPermissions() {
		Constants.location_manager.requestWhenInUseAuthorization();
		Constants.location_manager.requestAlwaysAuthorization();
	}

	/**
	 * Reset the location manager
	 *
	 * @return void
	 */
	private func resetLocationManager() {
		Constants.location_manager = CLLocationManager();
		Constants.location_manager.delegate = self;
		Constants.location_manager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters;
		Constants.location_manager.allowsBackgroundLocationUpdates = true;
		Constants.location_manager.pausesLocationUpdatesAutomatically = false;
		Constants.location_manager.activityType = CLActivityType.otherNavigation;
	}

	/**
	 * Restart the background task
	 *
	 * @return void
	 */
	private func restartBackgroundTask() {
		if (Constants.current_background_task_id != nil) {
			UIApplication.shared.endBackgroundTask(Constants.current_background_task_id!);
		}

		Constants.current_background_task_id = UIApplication.shared.beginBackgroundTask(expirationHandler: {});
	}

	/**
	 * Restart the timer
	 *
	 * @return void
	 */
	@objc func restartTimer() {
		if (Constants.timer == nil) {
			return;
		}

		Constants.timer?.invalidate();
		Constants.timer = nil;

		Constants.location_manager.startUpdatingLocation();
		Constants.location_manager.startMonitoringSignificantLocationChanges();
	}

	/**
	 * Send saved locations to server
	 *
	 * @return void
	 */
	private func sendLocationsToServer() {
		let locations_array = Constants.database_helpers.getLocations();

		var locations_data = [Dictionary<String, Any>]();
		for location_object in locations_array {
			var location_data: Dictionary<String, Any> = Dictionary<String, Any>();
			location_data["client_database_id"] = location_object.id;
			location_data["access_token"] = location_object.access_token;
			location_data["latitude"] = location_object.latitude;
			location_data["longitude"] = location_object.longitude;
			location_data["other_data"] = location_object.other_data;
			location_data["create_date"] = location_object.create_date;

			locations_data.append(location_data);
		}

		let access_token = Constants.database_helpers.getUserSettingByKey(key: "access_token") as! String;
		let data = ["coordinates": locations_data, "token": access_token] as [String : Any];

		var json_data:Data? = nil;
		do {
			json_data = try JSONSerialization.data(withJSONObject: data, options:[]);
		} catch {
			return;
		}

		let api_url = Constants.database_helpers.getUserSettingByKey(key: "api_url") as! String;
		let url = api_url + Constants.GPS_COORDINATES_API_URL;

		print("GPSMonitoringController.sendLocationsToServer url = " + url + ", num locations = " + String(locations_array.count));
		_ = AsyncAPITask.POSTJSON(url: url, data: json_data) {
		(response: HTTPURLResponse, json: NSDictionary?) in
			if (response.statusCode == 200) {
				if (json?.value(forKey: "ids_saved") == nil) {
					return;
				}

				let ids_saved: [String] = json?.value(forKey:"ids_saved") as! [String];
				Constants.database_helpers.removeLocations(ids_saved);
			}
		}
	}

	/**
	 * Start monitoring
	 *
	 * @return void
	 */
	func start(callback: ((Bool) -> Void)?) {
		if (self.hasPermissionToLocation()) {
			self.startTimer();

			print("GPSMonitoringController.start: Started Location Updates");
			if (callback != nil) {
				callback!(true);
			}

			return;
		}

		print("GPSMonitoringController.start: No permissions");
		if (callback != nil) {
			callback!(false);
		}
	}

	/**
	 * Start the timer
	 *
	 * @return void
	 */
	private func startTimer() {
		self.restartBackgroundTask();

		let tracking_frequency_milliseconds = Constants.database_helpers.getUserSettingByKey(key: "tracking_frequency_milliseconds") as! Int;

		Constants.location_manager.startUpdatingLocation();
		Constants.location_manager.startMonitoringSignificantLocationChanges();

		Constants.timer = Timer.scheduledTimer(
			timeInterval: TimeInterval(tracking_frequency_milliseconds / 1000),
			target: self,
			selector: #selector(self.restartTimer),
			userInfo: nil,
			repeats: false
		);
	}

	/**
	 * Stop monitoring
	 *
	 * @return void
	 */
	func stop() {
		if (!self.hasPermissionToLocation()) {
			return;
		}

		Constants.current_background_task_id = nil;
		Constants.timer?.invalidate();
		Constants.timer = nil;

		Constants.location_manager.stopUpdatingLocation();
		Constants.location_manager.stopMonitoringSignificantLocationChanges();

		print("GPSMonitoringController.stop: Stopped Location Updates");
	}

	/**
	 * Update monitoring status
	 *
	 * @param new_status		Status to set
	 *
	 * @return void
	 */
	private func updateMonitoringStatus(_ new_status: String) {
		Constants.monitoring_status = new_status;
	}

	/**
	 * Update permission status
	 *
	 * @param new_status		Status to set
	 *
	 * @return void
	 */
	private func updatePermissionStatus(_ new_status: String) {
		Constants.permission_status = new_status;
	}

	/**
	 * Update based on current location
	 *
	 * @param location		Location to set
	 *
	 * @return void
	 */
	func updateLocation(_ location: CLLocation) {
		let access_token = Constants.database_helpers.getUserSettingByKey(key: "access_token") as! String;

		Constants.last_position = location;
		Constants.database_helpers.saveLocation(access_token, location);

		let number_locations: Int = Constants.database_helpers.getLocationCount();
		if (number_locations >= Constants.NUM_LOCATIONS_BEFORE_API_REQUEST) {
			self.sendLocationsToServer();
		}

		print("GPSMonitoringController.updateLocation: Location: " + String(location.coordinate.latitude) + ", " + String(location.coordinate.longitude));
		print("GPSMonitoringController.updateLocation: number_locations: " + String(number_locations));
	}
}
