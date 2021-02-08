/**
 * Helpers for saving in the database
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "LocationObject.h"
#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@interface DatabaseHelpers: NSObject

@property (class, nonatomic, assign) NSString* LOCATION_USER_DEFAULTS_KEY;

- (long) getLocationCount;
- (NSArray<LocationObject*>*) getLocations;
- (NSObject*) getUserSettingByKey: (NSString*) key;
- (NSArray*) mapLocationObjectArrayToDatabaseArray: (NSArray<LocationObject*>*) locations_array;
- (void) removeLocations: (NSArray<NSString*>*) location_ids;
- (void) saveLocation: (NSString*) access_token location: (CLLocation*) location;
- (void) saveUserSettings: (NSString*) api_url access_token: (NSString*) access_token tracking_frequency_milliseconds: (NSString*) tracking_frequency_milliseconds geofence_json_string: (NSString*) geofence_json_string;

@end
