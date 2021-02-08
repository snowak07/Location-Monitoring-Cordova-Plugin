/**
 * Proxy for working with GPS Monitoring
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
#import "Constants.h"
#import <Cordova/CDVPlugin.h>
#import <Foundation/Foundation.h>
#import "GPSMonitoringProxy.h"

@implementation GPSMonitoringProxy

/**
 * Clear settings
 *
 * @param command		Details of the command
 *
 * @return void
 */
- (void) clearSettings: (CDVInvokedUrlCommand*) command {
	[Constants.gps_monitoring_controller stopMonitoring];

	CDVPluginResult* plugin_result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK];

	[self.commandDelegate sendPluginResult: plugin_result callbackId: command.callbackId];
}

/**
 * Return last position
 *
 * @param command		Details of the command
 *
 * @return void
 */
- (void) getLastPosition: (CDVInvokedUrlCommand*) command {
	CDVPluginResult* plugin_result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsDictionary: [Constants.gps_monitoring_controller getLastLocation]];

	[self.commandDelegate sendPluginResult: plugin_result callbackId: command.callbackId];
}

/**
 * Return monitoring status
 *
 * @param command		Details of the command
 *
 * @return void
 */
- (void) getMonitoringStatus: (CDVInvokedUrlCommand*) command {
	CDVPluginResult* plugin_result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: [Constants.gps_monitoring_controller getMonitoringStatus]];

	[self.commandDelegate sendPluginResult: plugin_result callbackId: command.callbackId];
}

/**
 * Return permission status
 *
 * @param command		Details of the command
 *
 * @return void
 */
- (void) getPermissionStatus: (CDVInvokedUrlCommand*) command {
	CDVPluginResult* plugin_result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: [Constants.gps_monitoring_controller getPermissionStatus]];

	[self.commandDelegate sendPluginResult: plugin_result callbackId: command.callbackId];
}

/**
 * Initialize the plugin
 *
 * @param command		Details of the command
 *
 * @return void
 */
- (void) initialize: (CDVInvokedUrlCommand*) command {
	NSArray* arguments = [command arguments];
	NSDictionary* options = arguments[0];

	NSString* api_url = options[@"api_url"];
	NSString* access_token = options[@"access_token"];
	NSString* tracking_frequency_milliseconds = options[@"tracking_frequency_milliseconds"];

	NSString* geofence_json_string = options[@"geofences"];

	Constants.database_helpers = [[DatabaseHelpers alloc] init];
	[Constants.database_helpers saveUserSettings: api_url access_token: access_token tracking_frequency_milliseconds: tracking_frequency_milliseconds geofence_json_string: geofence_json_string];

	Constants.gps_monitoring_controller = [[GPSMonitoringController alloc] init];
	[Constants.gps_monitoring_controller addGeofencesFromString: geofence_json_string];

	CDVPluginResult* plugin_result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK];
	[self.commandDelegate sendPluginResult: plugin_result callbackId: command.callbackId];
}

/**
 * Start location tracking
 *
 * @param command		Details of the command
 *
 * @return void
 */
- (void) start: (CDVInvokedUrlCommand*) command {
	[Constants.gps_monitoring_controller startMonitoring:^(BOOL result) {
		CDVPluginResult* plugin_result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: [Constants.gps_monitoring_controller getPermissionStatus]];

		[self.commandDelegate sendPluginResult: plugin_result callbackId: command.callbackId];
	}];
}

/**
 * Stop location tracking
 *
 * @param command		Details of the command
 *
 * @return void
 */
- (void) stop: (CDVInvokedUrlCommand*) command {
	[Constants.gps_monitoring_controller stopMonitoring];

	CDVPluginResult* plugin_result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK];

	[self.commandDelegate sendPluginResult: plugin_result callbackId: command.callbackId];
}

@end
