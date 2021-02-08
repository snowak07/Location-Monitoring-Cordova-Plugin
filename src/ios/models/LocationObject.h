/**
 * Object for location
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import <Foundation/Foundation.h>

@interface LocationObject: NSObject <NSCoding>

@property NSString* _id;
@property NSString* access_token;
@property NSString* latitude;
@property NSString* longitude;
@property NSString* other_data;
@property NSString* create_date;

- (id) init;
- (void) buildFromData: (NSData*) data;
- (NSData*) returnAsData;

- (id) initWithCoder: (NSCoder *) decoder;
- (void) encodeWithCoder: (NSCoder *) coder;

@end
