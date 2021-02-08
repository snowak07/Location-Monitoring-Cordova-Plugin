/**
 * Object for location
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "LocationObject.h"
#import <Foundation/Foundation.h>

@implementation LocationObject
/**
 * Identifier
 *
 * @var NSString*
 */
NSString* _id = @"";

/**
 * Access token of user that had location
 *
 * @var NSString*
 */
NSString* access_token = @"";

/**
 * Latitude
 *
 * @var NSString*
 */
NSString* latitude = @"";

/**
 * Longitude
 *
 * @var NSString*
 */
NSString* longitude = @"";

/**
 * Other data for the location
 *
 * @var NSString*
 */
NSString* other_data = @"";

/**
 * Date the location was collected
 *
 * @var NSString*
 */
NSString* create_date = @"";

/**
 * Construct object
 *
 * @return LocationObject
 */
- (id) init {
	self = [super init];
	return self;
}

/**
 * Build from Data object
 *
 * @param data		Data to build from
 *
 * @return void
 */
- (void) buildFromData: (NSData*) data {
	LocationObject* location_object = [NSKeyedUnarchiver unarchiveObjectWithData: data];

	self._id = location_object._id;
	self.access_token = location_object.access_token;
	self.latitude = location_object.latitude;
	self.longitude = location_object.longitude;
	self.other_data = location_object.other_data;
	self.create_date = location_object.create_date;
}

/**
 * Return as data
 *
 * @return NSData*
 */
- (NSData*) returnAsData {
	return [NSKeyedArchiver archivedDataWithRootObject: self];
}

/**
 * Construct the object from a decoder
 *
 * @param decoder		Decoder to use
 *
 * @return void
 */
- (id) initWithCoder: (NSCoder *) decoder {
	self = [super init];

	if (self) {
		self._id = [decoder decodeObjectForKey: @"_id"];
		self.access_token = [decoder decodeObjectForKey: @"access_token"];
		self.latitude = [decoder decodeObjectForKey: @"latitude"];
		self.longitude = [decoder decodeObjectForKey: @"longitude"];
		self.other_data = [decoder decodeObjectForKey: @"other_data"];
		self.create_date = [decoder decodeObjectForKey: @"create_date"];
	}

	return self;
}

/**
 * Encode the object
 *
 * @param coder		Code to encode
 *
 * @return void
 */
- (void) encodeWithCoder: (NSCoder *) coder {
	[coder encodeObject: self._id forKey: @"_id"];
	[coder encodeObject: self.access_token forKey: @"access_token"];
	[coder encodeObject: self.latitude forKey: @"latitude"];
	[coder encodeObject: self.longitude forKey: @"longitude"];
	[coder encodeObject: self.other_data forKey: @"other_data"];
	[coder encodeObject: self.create_date forKey: @"create_date"];
}

@end
