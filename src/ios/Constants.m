/**
 * Constants within the plugin
 *
 * @copyright Center for Health Enhancement Systems Studies
 */

#import "Constants.h"
#import "DatabaseHelpers.h"
#import "GPSMonitoringController.h"
#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@implementation Constants

/**
 * API URL to use when saving the GPS coordinates
 *
 * @var String
 */
static NSString* _GPS_COORDINATES_API_URL = @"gps-coordinates/save-coordinates";

/**
 * Return GPS_COORDINATES_API_URL value
 *
 * @return NSString*
 */
+ (NSString*) GPS_COORDINATES_API_URL {
	return _GPS_COORDINATES_API_URL;
}

/**
 * Set the GPS_COORDINATES_API_URL value
 *
 * @param GPS_COORDINATES_API_URL		Value to set
 *
 * @return void
 */
+ (void) setGPS_COORDINATES_API_URL: (NSString*) GPS_COORDINATES_API_URL {
	_GPS_COORDINATES_API_URL = GPS_COORDINATES_API_URL;
}

/**
 * Number of locations to save before saving to api request
 *
 * @var Int
 */
static int _NUM_LOCATIONS_BEFORE_API_REQUEST = 5;

/**
 * Return NUM_LOCATIONS_BEFORE_API_REQUEST value
 *
 * @return int
 */
+ (int) NUM_LOCATIONS_BEFORE_API_REQUEST {
	return _NUM_LOCATIONS_BEFORE_API_REQUEST;
}

/**
 * Set the NUM_LOCATIONS_BEFORE_API_REQUEST value
 *
 * @param NUM_LOCATIONS_BEFORE_API_REQUEST		Value to set
 *
 * @return void
 */
+ (void) setNUM_LOCATIONS_BEFORE_API_REQUEST: (int) NUM_LOCATIONS_BEFORE_API_REQUEST {
	_NUM_LOCATIONS_BEFORE_API_REQUEST = NUM_LOCATIONS_BEFORE_API_REQUEST;
}

/**
 * Background task identifier
 *
 * @var UIBackgroundTaskIdentifier
 */
static UIBackgroundTaskIdentifier _current_background_task_id = 100000000000000;

/**
 * Return current_background_task_id value
 *
 * @return UIBackgroundTaskIdentifier
 */
+ (UIBackgroundTaskIdentifier) current_background_task_id {
	return _current_background_task_id;
}

/**
 * Set the current_background_task_id value
 *
 * @param current_background_task_id		Object to set
 *
 * @return void
 */
+ (void) setCurrent_background_task_id: (UIBackgroundTaskIdentifier) current_background_task_id {
	_current_background_task_id = current_background_task_id;
}

/**
 * Database Helpers
 *
 * @var DatabaseHelpers*
 */
static DatabaseHelpers* _database_helpers = nil;

/**
 * Return database_helpers value
 *
 * @return DatabaseHelpers*
 */
+ (DatabaseHelpers*) database_helpers {
	return _database_helpers;
}

/**
 * Set database_helpers value
 *
 * @param database_helpers		Object to set
 *
 * @return void
 */
+ (void) setDatabase_helpers: (DatabaseHelpers*) database_helpers {
	_database_helpers = database_helpers;
}

/**
 * GPS Monitoring Controller
 *
 * @var GPSMonitoringController*
 */
static GPSMonitoringController* _gps_monitoring_controller = nil;

/**
 * Return gps_monitoring_controller value
 *
 * @return GPSMonitoringController*
 */
+ (GPSMonitoringController*) gps_monitoring_controller {
	return _gps_monitoring_controller;
}

/**
 * Set the gps_monitoring_controller value
 *
 * @param gps_monitoring_controller		Object to set
 *
 * @return void
 */
+ (void) setGps_monitoring_controller: (GPSMonitoringController*) gps_monitoring_controller {
	_gps_monitoring_controller = gps_monitoring_controller;
}

/**
 * Last Location
 *
 * @var CLLocation*
 */
static CLLocation* _last_location = nil;

/**
 * Return last_location value
 *
 * @return CLLocation*
 */
+ (CLLocation*) last_location {
	return _last_location;
}

/**
 * Set the last_location value
 *
 * @param last_location		Object to set
 *
 * @return void
 */
+ (void) setLast_location: (CLLocation*) last_location {
	_last_location = last_location;
}

/**
 * Monitoring status
 *
 * @var NSString*
 */
static NSString* _monitoring_status = nil;

/**
 * Return monitoring_status value
 *
 * @return NSString*
 */
+ (NSString*) monitoring_status {
	return _monitoring_status;
}

/**
 * Set the monitoring_status value
 *
 * @param monitoring_status		Object to set
 *
 * @return void
 */
+ (void) setMonitoring_status: (NSString*) monitoring_status {
	_monitoring_status = monitoring_status;
}

/**
 * Permission status
 *
 * @var NSString*
 */
static NSString* _permission_status = nil;

/**
 * Return permission_status value
 *
 * @return NSString*
 */
+ (NSString*) permission_status {
	return _permission_status;
}

/**
 * Set the permission_status value
 *
 * @param permission_status		Object to set
 *
 * @return void
 */
+ (void) setPermission_status: (NSString*) permission_status {
	_permission_status = permission_status;
}

/**
 * Timer
 *
 * @var NSTimer*
 */
static NSTimer* _timer = nil;

/**
 * Return timer value
 *
 * @return NSTimer*
 */
+ (NSTimer*) timer {
	return _timer;
}

/**
 * Set timer value
 *
 * @param timer		Object to set
 *
 * @return void
 */
+ (void) setTimer: (NSTimer*) timer {
	_timer = timer;
}

@end
