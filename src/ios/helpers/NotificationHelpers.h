/**
 * Notification Helpers
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import <Foundation/Foundation.h>

@interface NotificationHelpers: NSObject

- (void) cancelNotificationsByIdentifier: (NSString*) identifier;
- (void) showNotification: (NSString*) identifier title: (NSString*) title body: (NSString*) body data: (NSDictionary*) data;

@end
