/**
 * GPS Monitoring Controller
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "AsyncAPITask.h"
#import "Constants.h"
#import <CoreLocation/CoreLocation.h>
#import "GeofenceManager.h"
#import "GPSMonitoringController.h"
#import <Foundation/Foundation.h>
#import "NotificationHelpers.h"

@implementation GPSMonitoringController
/**
 * Geofence manager
 *
 * @var GeofenceManager*
 */
static GeofenceManager* _geofence_manager = nil;

/**
 * Location manager
 *
 * @var CLLocationManager*
 */
static CLLocationManager* _location_manager = nil;

/**
 * Details for the geofence notification
 *
 * @var NSMutableDictionary*
 */
NSMutableDictionary* geofence_notification_details = nil;

/**
 * Date of last geofence notification update (i.e. activation or cancelling)
 *
 * @var NSDate*
 */
NSDate* last_geofence_notification_update_date = nil;

/**
 * Return geofence_manager value
 *
 * @return GeofenceManager*
 */
+ (GeofenceManager*) geofence_manager {
	return _geofence_manager;
}

/**
 * Set geofence_manager value
 *
 * @param geofence_manager		Manager to set
 *
 * @return void
 */
+ (void) setGeofence_manager: (GeofenceManager*) geofence_manager {
	_geofence_manager = geofence_manager;
}

/**
 * Return location_manager value
 *
 * @return CLLocationManager*
 */
+ (CLLocationManager*) location_manager {
	return _location_manager;
}

/**
 * Set location_manager value
 *
 * @param location_manager		Manager to set
 *
 * @return void
 */
+ (void) setLocation_manager: (CLLocationManager*) location_manager {
	_location_manager = location_manager;
}

/**
 * Initialize the object
 *
 * @return void
 */
- (GPSMonitoringController*) init {
	self = [super init];
	if (self) {
		[self resetLocationManager];
	}

	return self;
}

/**
 * Add polygon geofences from string
 *
 * @param json		String of geofences to parse and add
 *
 * @return void
 */
- (void) addGeofencesFromString: (NSString*) json {
	if (json == nil) {
		return;
	}

	NSData* geofences = [json dataUsingEncoding:NSUTF8StringEncoding];

	NSError* json_error;
	id json_object = [NSJSONSerialization JSONObjectWithData: geofences options: NSJSONWritingPrettyPrinted error: &json_error];

	NSDictionary* notification_details = [json_object objectForKey: @"notification"];

	NSString* title = [notification_details objectForKey: @"title"];
	[geofence_notification_details setValue: title forKey: @"title"];

	NSString* body = [notification_details objectForKey: @"body"];
	[geofence_notification_details setValue: body forKey: @"body"];

	NSDictionary* circular_geofences_object = [json_object objectForKey: @"circular_geofences"];
	NSDictionary* polygon_geofences_object = [json_object objectForKey: @"polygon_geofences"];

	int i = 0;
	for (NSString* place_id in circular_geofences_object) {
		NSDictionary* circular_geofence = [circular_geofences_object objectForKey: place_id];

		NSDictionary* center_object = [circular_geofence objectForKey: @"center"];
		double latitude = [[center_object objectForKey:@"lat"] doubleValue];
		double longitude = [[center_object objectForKey:@"lon"] doubleValue];
		CLLocationCoordinate2D center = CLLocationCoordinate2DMake(latitude, longitude);

		double radius = [[circular_geofence objectForKey:@"radius"] doubleValue];
		[_geofence_manager addCircularGeofence: place_id center: &center radius: &radius];

		i ++;
	}

	int j = 0;
	for (NSString* place_id in polygon_geofences_object) {
		NSArray* coordinates = [polygon_geofences_object objectForKey: place_id];
		CGPoint points[[coordinates count]];

		for (int k = 0; k < [coordinates count]; k++) {
			NSDictionary* coordinate = coordinates[k];

			double latitude = [[coordinate objectForKey:@"lat"] doubleValue];
			double longitude = [[coordinate objectForKey:@"lon"] doubleValue];

			points[k] = CGPointMake(longitude, latitude);
		}

		[_geofence_manager addPolygonGeofence: place_id points: points];

		j ++;
	}

	NSLog(@"GPSMonitoringController.addGeofencesFromString: Num circular geofences = %d, Num polygon geofences = %d, Total = %d", i, j, i+j);
}

/**
 * Check whether location tracking is supported and enabled
 *
 * @return BOOL
 */
- (BOOL) checkLocationTrackingIsSupported {
	BOOL has_gps_enabled = [self isLocationTrackingEnabled];

	if (!has_gps_enabled) {
		[self updateMonitoringStatus: @"gps not enabled"];

	} else {
		[self updateMonitoringStatus: @"gps enabled"];
	}

	return has_gps_enabled;
}

/**
 * Return last location
 *
 * @return NSData*
 */
- (NSDictionary*) getLastLocation {
	if ([Constants last_location] == nil) {
		return nil;
	}

	CLLocation* location = [Constants last_location];
	double latitude = [location coordinate].latitude;
	double longitude = [location coordinate].longitude;

	NSMutableDictionary* data = [[NSMutableDictionary alloc] init];
	data[@"latitude"] = [[NSNumber numberWithDouble: latitude] stringValue];
	data[@"longitude"] = [[NSNumber numberWithDouble: longitude] stringValue];

	return [data copy];
}

/**
 * Return monitoring status
 *
 * @return NSString*
 */
- (NSString*) getMonitoringStatus {
	[self checkLocationTrackingIsSupported];
	return Constants.monitoring_status;
}

/**
 * Return permission status
 *
 * @return NSString*
 */
- (NSString*) getPermissionStatus {
	[self hasPermissionToLocation];
	return Constants.permission_status;
}

/**
 * Return whether permissions are set
 *
 * @return BOOL
 */
- (BOOL) hasPermissionToLocation {
	[self requestLocationPermissions];

	CLAuthorizationStatus authorization_status = [CLLocationManager authorizationStatus];
	if (authorization_status == kCLAuthorizationStatusNotDetermined || authorization_status == kCLAuthorizationStatusDenied || authorization_status == kCLAuthorizationStatusRestricted) {
		[self updatePermissionStatus: @"not granted"];
		return false;
	}

	NSMutableString* status = [[NSMutableString alloc] init];
	[status appendString: @"granted"];

	if (authorization_status == kCLAuthorizationStatusAuthorizedAlways) {
		[status appendString: @", always"];

	} else if (authorization_status == kCLAuthorizationStatusAuthorizedWhenInUse) {
		[status appendString: @", always when in use"];
	}

	[self updatePermissionStatus: (NSString *) status];
	return true;
}


/**
 * Indicates if location services are enabled
 *
 * @return BOOL
 */
- (BOOL) isLocationTrackingEnabled {
	return [CLLocationManager locationServicesEnabled];
}

/**
 * Handle when we have a location update
 *
 * @param manager		Manager to use
 * @param locations		Locations received
 *
 * @return void
 */
- (void) locationManager: (CLLocationManager *) manager didUpdateLocations: (NSArray*) locations {
	[self startTimer];

	double last_timestamp = -1;
	if (Constants.last_location != nil) {
		last_timestamp = Constants.last_location.timestamp.timeIntervalSince1970;
	}

	NSLog(@"GPSMonitoringController.locationManager: last_timestamp = %f", last_timestamp);

	NSString* temp_tracking_frequency_milliseconds = (NSString*)[[Constants database_helpers] getUserSettingByKey:@"tracking_frequency_milliseconds"];
	double tracking_frequency_milliseconds = [temp_tracking_frequency_milliseconds doubleValue];

	// Save the location in the database if it 0.9 * tracking_frequency_milliseconds seconds after the last update i.e. if frequency was 10, then 9 seconds after last update
	CLLocation* location = locations.lastObject;
	if (location == nil) {
		return;
	}

	double difference_seconds = location.timestamp.timeIntervalSince1970 - last_timestamp;
	double tracking_frequency_seconds = tracking_frequency_milliseconds / 1000 * 0.90;

	NSLog(@"GPSMonitoringController.locationManager updated: %f %f. %f, %f", difference_seconds, tracking_frequency_seconds, location.coordinate.latitude, location.coordinate.longitude);
	if (last_timestamp < 0 || difference_seconds >= tracking_frequency_seconds) {
		[self updateLocation: location];
	}

	[self updateGeofenceManagerWithLocation: location];
}

/**
 * Handle when there is a location manager failure
 *
 * @param manager		Manager to use
 * @param error			Error to handle
 *
 * @return void
 */
- (void) locationManager: (CLLocationManager*) manager didFailWithError: (NSError*) error {
	NSLog(@"GPSMonitoring location error: %s", [error.localizedDescription UTF8String]);
	[self startTimer];
}

/**
 * Handle when authorization changes
 *
 * @param manager			Manager to use
 * @param status			New status
 *
 * @return void
 */
- (void) locationManager: (CLLocationManager*) manager didChangeAuthorizationStatus: (CLAuthorizationStatus) status {
	if ([self hasPermissionToLocation]) {
		[self startMonitoring: nil];
	} else {
		[self stopMonitoring];
	}
}

/**
 * Request location services
 *
 * @return void
 */
- (void) requestLocationPermissions {
	[_location_manager requestWhenInUseAuthorization];
	[_location_manager requestAlwaysAuthorization];
}

/**
 * Reset the location manager
 *
 * @return void
 */
- (void) resetLocationManager {
	_location_manager = [[CLLocationManager alloc] init];
	[_location_manager setDelegate:(id<CLLocationManagerDelegate>)self];
	[_location_manager setActivityType:CLActivityTypeOther];

	_location_manager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters;
	_location_manager.pausesLocationUpdatesAutomatically = false;

	// Create a geofence
	_geofence_manager = [[GeofenceManager alloc] init];
	geofence_notification_details = [[NSMutableDictionary alloc] init];

	/*
	// Adam's Upnorth Geofence
	CGPoint adam_1_geofence[5] = {
		CGPointMake(-91.733328, 45.731289),
		CGPointMake(-91.733328, 45.731589),
		CGPointMake(-91.733028, 45.731589),
		CGPointMake(-91.733028, 45.731289),
		CGPointMake(-91.733328, 45.731289)
	};
	[_geofence_manager addGeofence:@"adam_1" points: adam_1_geofence];
	*/

	/*
	// Adam's Madison Geofence
	CGPoint adam_2_geofence[5] = {
		CGPointMake(-89.495, 43.015),
		CGPointMake(-89.485, 43.015),
		CGPointMake(-89.485, 43.005),
		CGPointMake(-89.495, 43.005),
		CGPointMake(-89.495, 43.015)
	};

	[_geofence_manager addGeofence:@"adam_2" points: adam_2_geofence];

	// CHESS' Geofence
	CGPoint chess_geofence[5] = {
		CGPointMake(-89.415, 43.074),
		CGPointMake(-89.405, 43.074),
		CGPointMake(-89.405, 43.064),
		CGPointMake(-89.415, 43.064),
		CGPointMake(-89.415, 43.074)
	};

	[_geofence_manager addGeofence:@"chess" points: chess_geofence];
	*/

	// Comment out the following if you are disabling background location updates
	//_location_manager.allowsBackgroundLocationUpdates = true;
}

/**
 * Restart the background task
 *
 * @return void
 */
- (void) restartBackgroundTask {
	if (Constants.current_background_task_id != 100000000000000) {
		[[UIApplication sharedApplication] endBackgroundTask: Constants.current_background_task_id];
	}

	Constants.current_background_task_id = [[UIApplication sharedApplication] beginBackgroundTaskWithExpirationHandler:^{ }];
}

/**
 * Restart the timer
 *
 * @return void
 */
- (void) restartTimer {
	if (Constants.timer == nil) {
		return;
	}

	[[Constants timer] invalidate];
	Constants.timer = nil;

	[_location_manager startUpdatingLocation];
	[_location_manager startMonitoringSignificantLocationChanges];
}

/**
 * Send saved locations to server
 *
 * @return void
 */
- (void) sendLocationsToServer {
	NSArray<LocationObject*>* locations_array = [[Constants database_helpers] getLocations];

	NSMutableArray* coordinates = [[NSMutableArray alloc] init];
	for (LocationObject* location_object in locations_array) {
		NSMutableDictionary* location = [[NSMutableDictionary alloc] init];

		location[@"client_database_id"] = (NSString*) location_object._id;
		location[@"access_token"] = (NSString*) location_object.access_token;
		location[@"latitude"] = (NSString*) location_object.latitude;
		location[@"longitude"] = (NSString*) location_object.longitude;
		location[@"other_data"] = (NSString*) location_object.other_data;
		location[@"create_date"] = (NSString*) location_object.create_date;

		[coordinates addObject: location];
	}

	NSMutableDictionary* data = [[NSMutableDictionary alloc] init];
	data[@"token"] = (NSString*) [[Constants database_helpers] getUserSettingByKey:@"access_token"];
	data[@"coordinates"] = [coordinates copy];

	NSMutableString* url = [[NSMutableString alloc] init];
	[url appendString: (NSString*) [[Constants database_helpers] getUserSettingByKey:@"api_url"]];
	[url appendString: [Constants GPS_COORDINATES_API_URL]];

	NSLog(@"GPSMonitoringController.sendLocationsToServer url = %@, num locations = %li", url, [locations_array count]);

	AsyncAPITask* task = [[AsyncAPITask alloc] init];
	[task POSTJSON: url dictionary: [data copy] completion_handler: ^(NSHTTPURLResponse* response, NSDictionary* data) {
		NSLog(@"GPSMonitoringController.sendLocationsToServer response = %li", (long)[response statusCode]);

		if ([response statusCode] != 200) {
			return;
		}

		if (data[@"ids_saved"] == nil) {
			return;
		}

		NSArray<NSString*>* ids_saved = data[@"ids_saved"];
		[[Constants database_helpers] removeLocations: ids_saved];
	}];
}

/**
 * Start location manager
 *
 * @param callback			Callback to execute
 *
 * @return void
 */
- (void) startMonitoring: (void (^)(BOOL)) callback {
	if ([self hasPermissionToLocation]) {
		[self startTimer];

		NSLog(@"GPSMonitoringController.start: Started Location Updates");
		if (callback != nil) {
			callback(true);
		}

		return;
	}

	NSLog(@"GPSMonitoringController.start: No permissions");
	if (callback != nil) {
		callback(false);
	}
}

/**
 * Start location manager timer
 *
 * @return void
 */
- (void) startTimer {
	[self restartBackgroundTask];

	NSNumber* _tracking_frequency_milliseconds = (NSNumber*) [[Constants database_helpers] getUserSettingByKey: @"tracking_frequency_milliseconds"];
	int tracking_frequency_milliseconds = [_tracking_frequency_milliseconds intValue];

	[_location_manager startUpdatingLocation];
	[_location_manager startMonitoringSignificantLocationChanges];

	NSTimeInterval duration = tracking_frequency_milliseconds / 1000;
	[NSTimer scheduledTimerWithTimeInterval: duration
		target: self
		selector: @selector(restartTimer)
		userInfo: nil
		repeats: NO
	 ];
}

/**
 * Stop monitoring
 *
 * @return void
 */
- (void) stopMonitoring {
	if (![self hasPermissionToLocation]) {
		return;
	}

	Constants.current_background_task_id = nil;

	[[Constants timer] invalidate];
	Constants.timer = nil;

	[_location_manager stopUpdatingLocation];
	[_location_manager stopMonitoringSignificantLocationChanges];

	NSLog(@"GPSMonitoringController.stop: Stopped Location Updates");
}

/**
 * Update geofence manager with a location
 *
 * @param location		Location to update with
 *
 * @return void
 */
- (void) updateGeofenceManagerWithLocation: (CLLocation*) location {
	[_geofence_manager updateGeofenceStates: location];
	NotificationHelpers* notification_helpers = [[NotificationHelpers alloc] init];

	NSTimeInterval seconds_since_last_geofence_notification_update = [[NSDate date] timeIntervalSinceDate: last_geofence_notification_update_date];

	double num_activated_geofences = [[[_geofence_manager getGeofencesByState: @"activated"] allKeys] count];
	double num_inactivated_geofences = [[[_geofence_manager getGeofencesByState: @"inactivated"] allKeys] count];

	NSLog(@"GPSMonitoringController.locationManager Num activated geofences = %f, last activation seconds = %f, Num inactivated geofences = %f", num_activated_geofences, seconds_since_last_geofence_notification_update, num_inactivated_geofences);

	if (num_activated_geofences > 0 && (seconds_since_last_geofence_notification_update > 60 || last_geofence_notification_update_date == nil)) {
		NSLog(@"GPSMonitoringController.locationManager have %f activated geofences", num_activated_geofences);

		NSString* title = [geofence_notification_details objectForKey: @"title"];
		NSString* body = [geofence_notification_details objectForKey: @"body"];
		[notification_helpers showNotification: @"geofence-activated" title: title body: body data: nil];

		last_geofence_notification_update_date = [NSDate date];
	}

	if (num_inactivated_geofences > 0 && num_activated_geofences == 0) {
		NSLog(@"GPSMonitoringController.locationManager have %f inactivated geofences", num_inactivated_geofences);

		[notification_helpers cancelNotificationsByIdentifier: @"geofence-activated"];

		last_geofence_notification_update_date = [NSDate date];
	}
}

/**
 * Update monitoring status
 *
 * @param new_status		Status to set
 *
 * @return void
 */
- (void) updateMonitoringStatus: (NSString*) new_status {
	Constants.monitoring_status = new_status;
}

/**
 * Update permission status
 *
 * @param new_status		Status to set
 *
 * @return void
 */
- (void) updatePermissionStatus: (NSString*) new_status {
	Constants.permission_status = new_status;
}

/**
 * Update based on current location
 *
 * @param location		Location to set
 *
 * @return void
 */
- (void) updateLocation: (CLLocation*) location {
	Constants.last_location = location;

	NSString* access_token = (NSString*)[[Constants database_helpers] getUserSettingByKey:@"access_token"];
	[[Constants database_helpers] saveLocation: access_token location: location];

	long number_locations = [[Constants database_helpers] getLocationCount];
	if (number_locations >= [Constants NUM_LOCATIONS_BEFORE_API_REQUEST]) {
		[self sendLocationsToServer];
	}

	NSLog(@"GPSMonitoringController.updateLocation: Location: %f, %f", location.coordinate.latitude, location.coordinate.longitude);
	NSLog(@"GPSMonitoringController.updateLocation: number_locations: %li", number_locations);
}

@end
