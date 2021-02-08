/**
 * Notification Helpers
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "NotificationHelpers.h"
#import <Foundation/Foundation.h>
#import <UserNotifications/UserNotifications.h>

@implementation NotificationHelpers

/**
 * Cancel notification by identifier
 *
 * @param identifier		Identifier of notification
 *
 * @return void
 */
- (void) cancelNotificationsByIdentifier: (NSString*) identifier {
	NSLog(@"Canceling notification by identifier %@", identifier);

	NSMutableArray<NSString*>* identifiers = [[NSMutableArray alloc] init];
	[identifiers addObject: identifier];

	[UNUserNotificationCenter.currentNotificationCenter removeDeliveredNotificationsWithIdentifiers: [identifiers copy]];
}

/**
 * Show a notification
 *
 * @param identifier		Identifier of notification
 * @param title				Title for the notification
 * @param body				Body for the notification
 * @param data				Data to include with the notification
 *
 * @return void
 */
- (void) showNotification: (NSString*) identifier title: (NSString*) title body: (NSString*) body data: (NSDictionary*) data {
	[UNUserNotificationCenter.currentNotificationCenter getDeliveredNotificationsWithCompletionHandler:^(NSArray<UNNotification *> * _Nonnull notifications)  {
		BOOL already_exists = false;
		for (UNNotification* notification in notifications) {
			if (notification.request.identifier == identifier) {
				already_exists = true;
			}
		}

		if (already_exists) {
			NSLog(@"%@ notification already showing", identifier);
			return;
		}

		// Show a new notification
		UNMutableNotificationContent* content = [[UNMutableNotificationContent alloc] init];
		content.categoryIdentifier = identifier;
		content.title = title;
		content.body = body;
		content.sound = UNNotificationSound.defaultSound;

		// Add the data
		if (data != nil) {
			content.userInfo = data;
		}

		// Hacks for enabling alerts because of Local Notifications plugin dependency
		NSMutableDictionary* new_user_info = [content.userInfo mutableCopy];
		[new_user_info setValue: @"1" forKey: @"foreground"];
		[new_user_info setValue: @"1" forKey: @"priority"];
		// End Hacks

		content.userInfo = [new_user_info copy];

		// Set up notification
		UNTimeIntervalNotificationTrigger* trigger = [UNTimeIntervalNotificationTrigger triggerWithTimeInterval: 1 repeats: false];
		UNNotificationRequest* request = [UNNotificationRequest requestWithIdentifier: identifier content: content trigger: trigger];

		[UNUserNotificationCenter.currentNotificationCenter addNotificationRequest: request withCompletionHandler: ^(NSError * _Nullable error) {
			if (!error) {
				NSLog(@"[NotificationHelpers.m] showNotification: Notification successfully scheduled");
			}
		}];
	}];
}

@end
