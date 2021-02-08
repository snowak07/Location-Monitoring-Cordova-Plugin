/**
 * GPS Monitoring Controller
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import <CoreLocation/CoreLocation.h>
#import <Foundation/Foundation.h>
#import "GeofenceManager.h"

@interface GPSMonitoringController: NSObject <CLLocationManagerDelegate>

@property (class, nonatomic, assign) GeofenceManager* geofence_manager;
@property (class, nonatomic, assign) CLLocationManager* location_manager;

- (void) addGeofencesFromString: (NSString*) json;
- (GPSMonitoringController*) init;
- (BOOL) checkLocationTrackingIsSupported;
- (NSDictionary*) getLastLocation;
- (NSString*) getMonitoringStatus;
- (NSString*) getPermissionStatus;
- (BOOL) hasPermissionToLocation;
- (BOOL) isLocationTrackingEnabled;
- (void) restartBackgroundTask;
- (void) restartTimer;
- (void) sendLocationsToServer;
- (void) startMonitoring: (void (^)(BOOL)) callback;
- (void) startTimer;
- (void) stopMonitoring;

@end
