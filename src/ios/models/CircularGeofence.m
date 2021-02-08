/**
 * Circular Geofence
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "CircularGeofence.h"
#import <CoreLocation/CoreLocation.h>

@implementation CircularGeofence
/**
 * Center of geofence
 *
 * @var CLLocationCoordinate2D*
 */
CLLocationCoordinate2D* center = nil;

/**
 * Geofence that we are working with
 *
 * @var CLRegion*
 */
CLRegion* geofence = nil;

/**
 * Radius of geofence (in meters)
 *
 * @var CLLocationDistance*
 */
CLLocationDistance* radius = nil;

/**
 * Construct object
 *
 * @param new_id		Identifier for geofence
 * @param center		Center of geofence
 * @param radius		Radius to use
 *
 * @return Geofence
 */
- (id) init: (NSString*) new_id center:(CLLocationCoordinate2D*) center radius: (CLLocationDistance*) radius {
	self = [super init: new_id];

	self.center = center;
	self.radius = radius;

	self.geofence = [[CLCircularRegion alloc] initWithCenter: *center radius: *radius identifier: new_id];

	return self;
}

/**
 * Check if point is within the geofence
 *
 * @return BOOL
*/
- (BOOL) isPointWithinGeofence: (CLLocation*) point {
	return [self.geofence containsCoordinate: point.coordinate];
}

@end
