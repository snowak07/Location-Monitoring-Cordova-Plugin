/**
 * Geofence Superclass
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "Geofence.h"

@implementation Geofence
/**
 * Identifier
 *
 * @var NSString*
 */
NSString* __id = @"";

/**
 * State of geofence
 *
 * @var NSString*
 */
NSString* _state = @"";

/**
 * Initialize object
 *
 * @param new_id		Identifier for object
 *
 * @return Geofence
 */
- (id) init: (NSString*) new_id {
	self = [super init];
	self.__id = new_id;

	return self;
}

/**
 * Return state value
 *
 * @return NSString*
 */
- (NSString*) getState {
	return self._state;
}

/**
 * Check if point is within the geofence
 *
 * @param point			Point to check
 *
 * @return BOOL
 */
- (BOOL) isPointWithinGeofence: (CLLocation*) point {
	[NSException raise:@"`isPointWithinGeofence` must be overwritten in subclass" format:@"isPointWithinGeofence` must be overwritten in subclass"];
	return NO;
}

/**
 * Set state of the geofence
 *
 * @param new_state		State to set
 *
 * @return void
 */
- (void) setState: (NSString*) new_state {
	self._state = new_state;
}

@end
