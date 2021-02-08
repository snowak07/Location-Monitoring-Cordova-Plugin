/**
 * GPS Monitoring Controller
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "CircularGeofence.h"
#import "Geofence.h"
#import "GeofenceManager.h"
#import <Foundation/Foundation.h>
#import "PolygonGeofence.h"

@implementation GeofenceManager
/**
 * Dictionary of geofences and their ids
 *
 * @var NSDictionary*
*/
NSDictionary* geofences = nil;

/**
 * Add a geofence to the list of geofences
 *
 * @param _id			Identifier for geofence
 * @param center		Center of geofence
 * @param radius		Radius to use
 *
 * @return void
 */
- (void) addCircularGeofence: (NSString*) _id center:(CLLocationCoordinate2D*) center radius: (CLLocationDistance*) radius {
	if (geofences == nil) {
		geofences = [[NSDictionary alloc] init];
	}

	CircularGeofence* geofence = [[CircularGeofence alloc] init: _id center: center radius: radius];

	NSMutableDictionary* temp_geofences = [geofences mutableCopy];
	[temp_geofences setValue: geofence forKey: _id];

	geofences = [temp_geofences copy];
}

/**
 * Add a geofence to the list of geofences
 *
 * @param _id			Identifier for the geofence
 * @param points		Points that make up the geofence's boundary
 *
 * @return void
 */
- (void) addPolygonGeofence: (NSString*) _id points: (CGPoint*) points {
	if (geofences == nil) {
		geofences = [[NSDictionary alloc] init];
	}

	PolygonGeofence* geofence = [[PolygonGeofence alloc] init: _id points: points];

	NSMutableDictionary* temp_geofences = [geofences mutableCopy];
	[temp_geofences setValue: geofence forKey: _id];

	geofences = [temp_geofences copy];
}

/**
 * Return geofences by their current state
 *
 * @param state			State to return
 *
 * @return NSDictionary*
 */
- (NSDictionary*) getGeofencesByState: (NSString*) state {
	NSMutableDictionary* geofence_states = [[NSMutableDictionary alloc] init];

	for (NSString* _id in geofences.allKeys) {
		Geofence* geofence = [geofences objectForKey: _id];

		if ([[geofence getState] isEqual: state]) {
			[geofence_states setValue: geofence forKey: _id];
		}
	}

	return [geofence_states copy];
}

/**
 * Update geofence states
 *
 * @param point		Point used to update geofence states
 *
 * @return void
 */
- (void) updateGeofenceStates: (CLLocation*) point {
	NSMutableDictionary* temp_geofences = [[NSMutableDictionary alloc] init];

	for (NSString* _id in geofences.allKeys) {
		Geofence* temp_geofence = [geofences objectForKey: _id];
		NSString* geofence_state = [temp_geofence getState];

		if ([temp_geofence isPointWithinGeofence: point]) {
			if ([geofence_state isEqual: @"inactive"] || [geofence_state isEqual: @""] || geofence_state == nil) {
				[temp_geofence setState: @"activated"];

			} else {
				[temp_geofence setState: @"active"];
			}

		} else {
			if ([geofence_state isEqual: @"active"]) {
				[temp_geofence setState: @"inactivated"];

			} else {
				[temp_geofence setState: @"inactive"];
			}
		}

		[temp_geofences setValue:temp_geofence forKey:_id];
	}

	geofences = [temp_geofences copy];
}

@end
