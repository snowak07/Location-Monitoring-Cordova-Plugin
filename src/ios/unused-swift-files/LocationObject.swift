/**
 * Object for location
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
import Foundation;

class LocationObject: NSObject, NSCoding {
	/**
	 * Identifier
	 *
	 * @var String
	 */
	var id: String = "";

	/**
	 * Access token of user that had location
	 *
	 * @var String
	 */
	var access_token: String = "";

	/**
	 * Latitude
	 *
	 * @var String
	 */
	var latitude: String = "";

	/**
	 * Longitude
	 *
	 * @var String
	 */
	var longitude: String = "";

	/**
	 * Other data for the location
	 *
	 * @var String
	 */
	var other_data: String = "";

	/**
	 * Date the location was collected
	 *
	 * @var String
	 */
	var create_date: String = "";

	/**
	 * Construct the object
	 *
	 * @return void
	 */
	override init() { }

	/**
	 * Construct the object from a decoder
	 *
	 * @param decoder		Decoder to use
	 *
	 * @return void
	 */
	required init(coder decoder: NSCoder) {
		if let id = decoder.decodeObject(forKey: "id") as? String {
			self.id = id;
		}

		if let access_token = decoder.decodeObject(forKey: "access_token") as? String {
			self.access_token = access_token;
		}

		if let latitude = decoder.decodeObject(forKey: "latitude") as? String {
			self.latitude = latitude;
		}

		if let longitude = decoder.decodeObject(forKey: "longitude") as? String {
			self.longitude = longitude;
		}

		if let other_data = decoder.decodeObject(forKey: "other_data") as? String {
			self.other_data = other_data;
		}

		if let create_date = decoder.decodeObject(forKey: "create_date") as? String {
			self.create_date = create_date;
		}
	}

	/**
	 * Encode the object
	 *
	 * @param aCoder		Code to encode
	 *
	 * @return void
	 */
	func encode(with aCoder: NSCoder) {
		aCoder.encode(self.id, forKey: "id");
		aCoder.encode(self.access_token, forKey: "access_token");
		aCoder.encode(self.latitude, forKey: "latitude");
		aCoder.encode(self.longitude, forKey: "longitude");
		aCoder.encode(self.other_data, forKey: "other_data");
		aCoder.encode(self.create_date, forKey: "create_date");
	}

	/**
	 * Build from Data object
	 *
	 * @param data		Data to build from
	 *
	 * @return Data
	 */
	func buildFromData(data: Data) {
		let location_object = NSKeyedUnarchiver.unarchiveObject(with: data) as! LocationObject;

		self.id = location_object.id;
		self.access_token = location_object.access_token;
		self.latitude = location_object.latitude;
		self.longitude = location_object.longitude;
		self.other_data = location_object.other_data;
		self.create_date = location_object.create_date;
	}

	/**
	 * Return as data
	 *
	 * @return Data
	 */
	func returnAsData() -> Data {
		let location_object = LocationObject();

		location_object.id = self.id;
		location_object.access_token = self.access_token;
		location_object.latitude = self.latitude;
		location_object.longitude = self.longitude;
		location_object.other_data = self.other_data;
		location_object.create_date = self.create_date;

		return NSKeyedArchiver.archivedData(withRootObject: location_object);
	}
}
