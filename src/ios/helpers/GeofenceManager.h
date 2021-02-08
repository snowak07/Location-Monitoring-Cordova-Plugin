/**
 * Manager for Geofences
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import <CoreLocation/CoreLocation.h>
#import <Foundation/Foundation.h>

@interface GeofenceManager: NSObject

@property NSDictionary* geofences;

- (void) addCircularGeofence: (NSString*) _id center:(CLLocationCoordinate2D*) center radius: (CLLocationDistance*) radius;
- (void) addPolygonGeofence: (NSString*) _id points: (CGPoint*) points;
- (NSDictionary*) getGeofencesByState: (NSString*) state;
- (void) updateGeofenceStates: (CLLocation*) point;

@end
