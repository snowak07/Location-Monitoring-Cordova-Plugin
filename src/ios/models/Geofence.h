/**
 * Geofence object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import <CoreLocation/CoreLocation.h>
#import <Foundation/Foundation.h>

@interface Geofence: NSObject

@property NSString* __id;
@property NSString* _state; // 4 states: active, inactive. inactivated (was previously active), activated (was previously inactive)

- (id) init: (NSString*) new_id;
- (NSString*) getState;
- (BOOL) isPointWithinGeofence: (CLLocation*) point;
- (void) setState: (NSString*) _state;

@end
