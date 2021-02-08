/**
 * Helpers for saving in the database
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "DatabaseHelpers.h"
#import "LocationObject.h"
#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@implementation DatabaseHelpers
/**
 * Key for location
 *
 * @var NSString*
 */
static NSString* _LOCATION_USER_DEFAULTS_KEY = @"gps_coordinates";

/**
 * Return LOCATION_USER_DEFAULTS_KEY value
 *
 * @return int
 */
+ (NSString*) LOCATION_USER_DEFAULTS_KEY {
	return _LOCATION_USER_DEFAULTS_KEY;
}

/**
 * Set the LOCATION_USER_DEFAULTS_KEY value
 *
 * @param LOCATION_USER_DEFAULTS_KEY		Value to set
 *
 * @return void
 */
+ (void) setLOCATION_USER_DEFAULTS_KEY: (NSString*) LOCATION_USER_DEFAULTS_KEY {
	_LOCATION_USER_DEFAULTS_KEY = LOCATION_USER_DEFAULTS_KEY;
}

/**
 * Return number of locations in database
 *
 * @return long
 */
- (long) getLocationCount {
	return [self getLocations].count;
}

/**
 * Return locations from database
 *
 * @return NSArray*
 */
- (NSArray*) getLocations {
	NSUserDefaults* database = [NSUserDefaults standardUserDefaults];

	NSArray* db_locations_array = [database objectForKey: DatabaseHelpers.LOCATION_USER_DEFAULTS_KEY];
	if (db_locations_array == nil) {
		return [[NSArray alloc] init];
	}

	NSMutableArray<LocationObject*>* locations_array = [[NSMutableArray<LocationObject*> alloc] init];
	for (int i = 0; i < db_locations_array.count; i++) {
		LocationObject* location_object = [[LocationObject alloc] init];
		[location_object buildFromData: db_locations_array[i]];

		[locations_array addObject: location_object];
	}

	return [locations_array copy];
}

/**
 * Return user setting by key
 *
 * @param key		Key to return
 *
 * @return NSObject
 */
- (NSObject*) getUserSettingByKey: (NSString*) key {
	NSUserDefaults* database = [NSUserDefaults standardUserDefaults];
	return [database objectForKey: key];
}

/**
 * Convert an array of location objects to the type to be saved
 *
 * @param locations_array			Location array to convert
 *
 * @return NSArray*
 */
- (NSArray*) mapLocationObjectArrayToDatabaseArray: (NSArray<LocationObject*>*) locations_array {
	NSMutableArray* db_locations_array = [[NSMutableArray alloc] init];

	for (int i = 0; i < [locations_array count]; i++) {
		LocationObject* temp_location_object = (LocationObject*) locations_array[i];
		NSData* temp_location_object_data = [temp_location_object returnAsData];

		[db_locations_array addObject: temp_location_object_data];
	}

	return [db_locations_array copy];
}

/**
 * Remove locations from database
 *
 * @param location_ids		Ids to remove
 *
 * @return void
 */
- (void) removeLocations: (NSArray<NSString*>*) location_ids {
	NSArray<LocationObject*>* _locations_array = [self getLocations];
	NSMutableArray<LocationObject*>* locations_array = [_locations_array mutableCopy];

	for (NSString* _id in location_ids) {
		for (long i = locations_array.count - 1; i >= 0; i--) {
			if ([_id isEqualToString: locations_array[i]._id]) {
				[locations_array removeObjectAtIndex: i];
			}
		}
	}

	NSArray* db_locations_array = [self mapLocationObjectArrayToDatabaseArray: [locations_array copy]];

	NSUserDefaults* database = [NSUserDefaults standardUserDefaults];
	[database setObject: db_locations_array forKey: DatabaseHelpers.LOCATION_USER_DEFAULTS_KEY];
	[database synchronize];

	NSLog(@"DatabaseHelpers.removeLocations: num locations in database = %li", [self getLocationCount]);
}

/**
 * Save location in database
 *
 * @param access_token		Token to save
 * @param location			Position to save
 *
 * @return void
 */
- (void) saveLocation: (NSString*) access_token location: (CLLocation*) location {
	double time_interval_since_1970 = [[location timestamp] timeIntervalSince1970];
	double latitude = [location coordinate].latitude;
	double longitude = [location coordinate].longitude;

	LocationObject* location_object = [[LocationObject alloc] init];
	location_object._id = [[NSNumber numberWithDouble: time_interval_since_1970] stringValue];
	location_object.access_token = access_token;
	location_object.latitude = [[NSNumber numberWithDouble: latitude] stringValue];
	location_object.longitude = [[NSNumber numberWithDouble: longitude] stringValue];
	location_object.other_data = @"";
	location_object.create_date = [[NSNumber numberWithDouble: time_interval_since_1970] stringValue];

	NSLog(@"location object data = %@", location_object._id);

	NSData* temp_data = [location_object returnAsData];
	LocationObject* new_location_object = [[LocationObject alloc] init];
	[new_location_object buildFromData: temp_data];

	NSLog(@"new location object data = %@", new_location_object._id);

	NSArray<LocationObject*>* _locations_array = [self getLocations];
	NSMutableArray<LocationObject*>* locations_array = [_locations_array mutableCopy];
	[locations_array addObject: location_object];

	NSArray* db_locations_array = [self mapLocationObjectArrayToDatabaseArray: [locations_array copy]];

	NSUserDefaults* database = [NSUserDefaults standardUserDefaults];
	[database setObject: db_locations_array forKey: DatabaseHelpers.LOCATION_USER_DEFAULTS_KEY];
	[database synchronize];

	NSLog(@"DatabaseHelpers.saveLocation: num locations in database = %li", [self getLocationCount]);
}

/**
 * Save user settings
 *
 * @param api_url								Url for api
 * @param access_token							User's access token
 * @param tracking_frequency_milliseconds		Number of milliseconds between getting location updates
 * @param geofence_json_string					Geofence string
 *
 * @return void
 */
- (void) saveUserSettings: (NSString*) api_url access_token: (NSString*) access_token tracking_frequency_milliseconds: (NSString*) tracking_frequency_milliseconds geofence_json_string: (NSString*) geofence_json_string {
	NSUserDefaults* database = [NSUserDefaults standardUserDefaults];

	[database setObject: api_url forKey:@"api_url"];
	[database setObject: access_token forKey:@"access_token"];
	[database setObject: tracking_frequency_milliseconds forKey:@"tracking_frequency_milliseconds"];
	[database setObject: geofence_json_string forKey:@"geofence_json_string"];

	[database synchronize];
}

@end
