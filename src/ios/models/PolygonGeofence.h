/**
 * Polygon Geofence object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "Geofence.h"
#import <CoreLocation/CoreLocation.h>
#import <Foundation/Foundation.h>

@interface PolygonGeofence: Geofence

@property CGPathRef polygon;

- (id) init: (NSString*) _id points:(CGPoint*) points;
- (BOOL) isPointWithinGeofence: (CLLocation*) point;

@end
