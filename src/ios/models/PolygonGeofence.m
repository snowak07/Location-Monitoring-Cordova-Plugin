/**
 * Polygon Geofence
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "PolygonGeofence.h"

@implementation PolygonGeofence

/**
 * Polygon that fence forms
 *
 * @var CGPathRef*
 */
CGPathRef polygon;

/**
 * Construct object
 *
 * @param new_id		Identifier for geofence
 * @param points		Path of points that make up geofence
 *
 * @return Geofence
 */
- (id) init: (NSString*) new_id points: (CGPoint*) points {
	self = [super init: new_id];

	CGMutablePathRef new_polygon = CGPathCreateMutable();
	CGPathAddLines(new_polygon, NULL, points, sizeof points);
	self.polygon = CGPathCreateCopy(new_polygon);

	return self;
}

/**
 * Check if point is within the geofence
 *
 * @return BOOL
*/
- (BOOL) isPointWithinGeofence: (CLLocation*) point {
	CGPoint coordinate = CGPointMake(point.coordinate.longitude, point.coordinate.latitude);
	return CGPathContainsPoint(self.polygon, NULL, coordinate, true);
}

@end
