/**
 * Proxy for working with GPS Monitoring
 *
 * @copyright Center for Health Enhancement Systems Studies
 */

#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>

@interface GPSMonitoringProxy : CDVPlugin

- (void) clearSettings: (CDVInvokedUrlCommand*) command;
- (void) getLastPosition: (CDVInvokedUrlCommand*) command;
- (void) getMonitoringStatus: (CDVInvokedUrlCommand*) command;
- (void) getPermissionStatus: (CDVInvokedUrlCommand*) command;
- (void) initialize: (CDVInvokedUrlCommand*) command;
- (void) start: (CDVInvokedUrlCommand*) command;
- (void) stop: (CDVInvokedUrlCommand*) command;

@end
