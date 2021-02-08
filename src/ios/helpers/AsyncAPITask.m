/**
 * Helpers for working with APIs
 *
 * @copyright Center for Health Enhancement Systems Studies
*/
#import "AsyncAPITask.h"
#import <Foundation/Foundation.h>
#import <Foundation/NSJSONSerialization.h>

@implementation AsyncAPITask

/**
 * Execute a URLRequest object
 *
 * @param request					Request to execute
 * @param completion_handler		Callback to execute after we receive our response
 *
 * @return NSURLSessionDataTask*
 */
- (NSURLSessionDataTask*) executeURLRequest: (NSURLRequest*) request completion_handler: (void (^)(NSHTTPURLResponse* response, NSDictionary* data)) completion_handler {
	NSURLSession* session = [NSURLSession sharedSession];
	NSURLSessionDataTask* task = [session dataTaskWithRequest: request completionHandler:^(NSData* data, NSURLResponse* response, NSError* error) {
		if (response == nil) {
			NSLog(@"-- Response (nil) --");
			return;
		}

		NSHTTPURLResponse *http_response = (NSHTTPURLResponse*) response;
		NSString* response_body = [[NSString alloc] initWithData: data encoding: NSASCIIStringEncoding];

		NSLog(@"-- Response (%li) --", (long)[http_response statusCode]);
		NSLog(@"%@", response_body);
		NSLog(@"\n");

		NSDictionary* json = [NSJSONSerialization JSONObjectWithData: data options: NSJSONReadingAllowFragments error: nil];
		completion_handler(http_response, json);
	}];

	[task resume];
	return task;
}

/**
 * GET to a url
 *
 * @param url						URL to make request to
 * @param data						Data encoded as a string of the form key1=val1&key2=val2&...
 * @param completion_handler		Callback to execute
 *
 * @return NSURLSessionDataTask*
 */
- (NSURLSessionDataTask*) GET: (NSString*) url data: (NSString*) data completion_handler: (void (^)(NSHTTPURLResponse* response, NSDictionary* data)) completion_handler {
	NSLog(@"-- Request (GET) --");
	NSLog(@"%@", url);

	if (data != nil) {
		NSLog(@"%@\n", data);
	}

	NSURL* url_object = [[NSURL alloc] initWithString: url];
	NSMutableURLRequest* request = [[NSMutableURLRequest alloc] initWithURL: url_object];

	[request setHTTPMethod:@"GET"];
	[request setCachePolicy:NSURLRequestReloadIgnoringCacheData];

	if (data != nil) {
		[request setHTTPBody: [data dataUsingEncoding:NSUTF8StringEncoding]];
	}

	return [self executeURLRequest: request completion_handler:completion_handler];
}

/**
 * POST to a url
 *
 * @param url						URL to make request to
 * @param data						Data encoded as a string of the form key1=val1&key2=val2&...
 * @param completion_handler		Callback to execute
 *
 * @return NSURLSessionDataTask*
 */
- (NSURLSessionDataTask*) POST: (NSString*) url data: (NSString*) data completion_handler: (void (^)(NSHTTPURLResponse* response, NSDictionary* data)) completion_handler {
	NSLog(@"-- Request (POST) --");
	NSLog(@"%@", url);

	if (data != nil) {
		NSLog(@"%@\n", data);
	}

	NSURL* url_object = [[NSURL alloc] initWithString: url];
	NSMutableURLRequest* request = [[NSMutableURLRequest alloc] initWithURL: url_object];

	[request setHTTPMethod:@"POST"];
	[request setCachePolicy:NSURLRequestReloadIgnoringCacheData];

	if (data != nil) {
		[request setHTTPBody: [data dataUsingEncoding:NSUTF8StringEncoding]];
	}

	return [self executeURLRequest: request completion_handler: completion_handler];
}

/**
 * POST JSON to a url
 *
 * @param url						URL to make request to
 * @param dictionary				Data to pass
 * @param completion_handler		Callback to execute
 *
 * @return NSURLSessionDataTask*
 */
- (NSURLSessionDataTask*) POSTJSON: (NSString*) url dictionary: (NSDictionary*) dictionary completion_handler: (void (^)(NSHTTPURLResponse* response, NSDictionary* data)) completion_handler {
	NSError* error = nil;

	NSData* json_data = [NSJSONSerialization dataWithJSONObject: dictionary options: NSJSONWritingPrettyPrinted error: &error];

	if (error != nil) {
		NSLog(@"AsyncAPITask POSTJSON dictionary JSONString error: %@", [error localizedDescription]);
	}

	return [self POSTJSON: url data: json_data completion_handler: completion_handler];
}

/**
 * POST JSON to a url
 *
 * @param url						URL to make request to
 * @param data						JSON-encoded data
 * @param completion_handler		Callback to execute
 *
 * @return NSURLSessionDataTask*
 */
- (NSURLSessionDataTask*) POSTJSON: (NSString*) url data: (NSData*) data completion_handler: (void (^)(NSHTTPURLResponse* response, NSDictionary* data)) completion_handler {
	NSLog(@"POSTJSON -- Request (POST) --");
	NSLog(@"%@", url);

	if (data != nil) {
		NSLog(@"%@\n", data);
	}

	NSURL* url_object = [[NSURL alloc] initWithString: url];
	NSMutableURLRequest* request = [[NSMutableURLRequest alloc] initWithURL: url_object];

	[request setHTTPMethod:@"POST"];
	[request setCachePolicy:NSURLRequestReloadIgnoringCacheData];

	if (data != nil) {
		[request setHTTPBody: data];
	}

	[request addValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
	[request addValue:@"application/json" forHTTPHeaderField:@"Accept"];

	return [self executeURLRequest: request completion_handler: completion_handler];
}

@end
