/**
 * Helpers for working with APIs
 *
 * @copyright Center for Health Enhancement Systems Studies
*/
#import <Foundation/Foundation.h>

@interface AsyncAPITask: NSObject

- (NSURLSessionDataTask*) executeURLRequest: (NSURLRequest*) request completion_handler: (void (^)(NSHTTPURLResponse* response, NSDictionary* data)) completion_handler;
- (NSURLSessionDataTask*) GET: (NSString*) url data: (NSString*) data completion_handler: (void (^)(NSHTTPURLResponse* response, NSDictionary* data)) completion_handler;
- (NSURLSessionDataTask*) POST: (NSString*) url data: (NSString*) data completion_handler: (void (^)(NSHTTPURLResponse* response, NSDictionary* data)) completion_handler;
- (NSURLSessionDataTask*) POSTJSON: (NSString*) url dictionary: (NSDictionary<NSString*, NSObject*>*) dictionary completion_handler: (void (^)(NSHTTPURLResponse* response, NSDictionary* data)) completion_handler;
- (NSURLSessionDataTask*) POSTJSON: (NSString*) url data: (NSData*) data completion_handler: (void (^)(NSHTTPURLResponse* response, NSDictionary* data)) completion_handler;

@end
