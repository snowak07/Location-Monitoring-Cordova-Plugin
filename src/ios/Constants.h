/**
 * Constants within the plugin
 *
 * @copyright Center for Health Enhancement Systems Studies
*/
#import "DatabaseHelpers.h"
#import "GPSMonitoringController.h"
#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@interface Constants: NSObject

@property (class, nonatomic, assign) NSString* GPS_COORDINATES_API_URL;
@property (class, nonatomic, assign) int NUM_LOCATIONS_BEFORE_API_REQUEST;

@property (class, nonatomic, assign) UIBackgroundTaskIdentifier current_background_task_id;
@property (class, nonatomic, assign) DatabaseHelpers* database_helpers;
@property (class, nonatomic, assign) GPSMonitoringController* gps_monitoring_controller;
@property (class, nonatomic, assign) CLLocation* last_location;
@property (class, nonatomic, assign) NSString* monitoring_status;
@property (class, nonatomic, assign) NSString* permission_status;
@property (class, nonatomic, assign) NSTimer* timer;

@end
