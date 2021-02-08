/**
 * Helpers for saving in the database
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
import Foundation;
import CoreLocation;

class DatabaseHelpers {
	/**
	 * Key for location
	 *
	 * @var String
	 */
	private let LOCATION_USER_DEFAULTS_KEY: String = "gps_coordinates";

	/**
	 * Return number of locations in database
	 *
	 * @param position		Position to save
	 *
	 * @return Int
	 */
	func getLocationCount() -> Int {
		return self.getLocations().count;
	}

	/**
	 * Return locations from database
	 *
	 * @return [LocationObject]
	 */
	func getLocations() -> [LocationObject] {
		let db_locations_array = UserDefaults.standard.array(forKey: self.LOCATION_USER_DEFAULTS_KEY);
		if (db_locations_array == nil) {
			return [];
		}

		var locations_array:[LocationObject] = [];
		for temp_location_object in db_locations_array! {
			let location_object = LocationObject();
			location_object.buildFromData(data: temp_location_object as! Data);
			locations_array.append(location_object);
		}

		return locations_array;
	}

	/**
	 * Return user setting by key
	 *
	 * @param key		Key to return
	 *
	 * @return AnyObject?
	 */
	func getUserSettingByKey(key: String) -> AnyObject? {
		return UserDefaults.standard.object(forKey: key) as AnyObject?;
	}

	/**
	 * Convert an array of location objects to the type to be saved
	 *
	 * @param locations_array			Location array to convert
	 *
	 * @return NSArray
	 */
	private func mapLocationObjectArrayToDatabaseArray(_ locations_array: [LocationObject]) -> NSArray {
		var db_locations_array: NSArray = [];
		for temp_location_object in locations_array {
			db_locations_array = db_locations_array.adding(temp_location_object.returnAsData()) as NSArray;
		}

		return db_locations_array;
	}

	/**
	 * Remove locations from database
	 *
	 * @param location_ids		Ids to remove
	 *
	 * @return void
	 */
	func removeLocations(_ location_ids: [String]) {
		var locations_array = self.getLocations();

		for id in location_ids {
			var i = locations_array.count - 1;
			while i >= 0 {
				if (id == locations_array[i].id) {
					locations_array.remove(at: i);
				}

				i = i - 1;
			}
		}

		let db_locations_array: NSArray = self.mapLocationObjectArrayToDatabaseArray(locations_array);

		UserDefaults.standard.set(db_locations_array, forKey: self.LOCATION_USER_DEFAULTS_KEY);
		UserDefaults.standard.synchronize();

		print("DatabaseHelpers.removeLocations: num locations in database = " + String(self.getLocationCount()));
	}

	/**
	 * Save location in database
	 *
	 * @param access_token		Token to save
	 * @param location			Position to save
	 *
	 * @return void
	 */
	func saveLocation(_ access_token: String, _ location: CLLocation) {
		let location_object: LocationObject = LocationObject();

		location_object.id = String(location.timestamp.timeIntervalSince1970);
		location_object.access_token = access_token;
		location_object.latitude = String(location.coordinate.latitude);
		location_object.longitude = String(location.coordinate.longitude);
		location_object.other_data = "";
		location_object.create_date = String(location.timestamp.timeIntervalSince1970);

		var locations_array = self.getLocations();
		locations_array.append(location_object);

		let db_locations_array: NSArray = self.mapLocationObjectArrayToDatabaseArray(locations_array);

		UserDefaults.standard.set(db_locations_array, forKey: self.LOCATION_USER_DEFAULTS_KEY);
		UserDefaults.standard.synchronize();

		print("DatabaseHelpers.saveLocation: num locations in database = " + String(self.getLocationCount()));
	}

	/**
	 * Save user settings
	 *
	 * @param api_url							Url for api
	 * @param access_token						User's access token
	 * @param tracking_frequency_milliseconds		Number of milliseconds between getting location updates
	 *
	 * @return void
	 */
	func saveUserSettings(api_url: String, access_token: String, tracking_frequency_milliseconds: Int) {
		let database = UserDefaults.standard;

		database.set(api_url, forKey: "api_url");
		database.set(access_token, forKey: "access_token");
		database.set(tracking_frequency_milliseconds, forKey: "tracking_frequency_milliseconds");

		database.synchronize();
	}
}
