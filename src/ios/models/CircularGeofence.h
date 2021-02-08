/**
 * Circular Geofence object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "Geofence.h"
#import <CoreLocation/CoreLocation.h>
#import <Foundation/Foundation.h>

@interface CircularGeofence: Geofence

@property CLLocationCoordinate2D* center;
@property CLRegion* geofence;
@property CLLocationDistance* radius;

- (id) init: (NSString*) new_id center:(CLLocationCoordinate2D*) center radius: (CLLocationDistance*) radius;
- (BOOL) isPointWithinGeofence: (CLLocation*) point;

@end
