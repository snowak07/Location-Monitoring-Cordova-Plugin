/**
 * Proxy for working with GPS Monitoring
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
import UIKit
import Foundation

@objc(GPSMonitoringProxy) class GPSMonitoringProxy: CDVPlugin {
	/**
	 * Return last position
	 *
	 * @param command		Details of the command
	 *
	 * @return void
	 */
	@objc(getLastPosition:)
	func getLastPosition(command: CDVInvokedUrlCommand) {
		let plugin_result = CDVPluginResult(
			status: CDVCommandStatus_OK,
			messageAsArrayBuffer: Constants.gps_monitoring_controller.getLastLocation()
		)

		self.commandDelegate!.send(
			plugin_result,
			callbackId: command.callbackId
		)
	}

	/**
	 * Return monitoring status
	 *
	 * @param command		Details of the command
	 *
	 * @return void
	 */
	@objc(getMonitoringStatus:)
	func getMonitoringStatus(command: CDVInvokedUrlCommand) {
		let plugin_result = CDVPluginResult(
			status: CDVCommandStatus_OK,
			messageAs: Constants.gps_monitoring_controller.getMonitoringStatus()
		)

		self.commandDelegate!.send(
			plugin_result,
			callbackId: command.callbackId
		)
	}

	/**
	 * Return permission status
	 *
	 * @param command		Details of the command
	 *
	 * @return void
	 */
	@objc(getPermissionStatus:)
	func getPermissionStatus(command: CDVInvokedUrlCommand) {
		let plugin_result = CDVPluginResult(
			status: CDVCommandStatus_OK,
			messageAs: Constants.gps_monitoring_controller.getPermissionStatus()
		)

		self.commandDelegate!.send(
			plugin_result,
			callbackId: command.callbackId
		)
	}

	/**
	 * Initialize the plugin
	 *
	 * @param command		Details of the command
	 *
	 * @return void
	 */
	@objc(initialize:)
	func initialize(command: CDVInvokedUrlCommand) {
		let options = command.argument(at: 0) as! [AnyHashable: Any];

		let api_url = options["api_url"] as? String ?? "";
		let access_token = options["access_token"] as? String ?? "";
		let tracking_frequency_milliseconds = options["tracking_frequency_milliseconds"] as? Int ?? -1;

		let database_helpers = DatabaseHelpers();
		database_helpers.saveUserSettings(
			api_url: api_url,
			access_token: access_token,
			tracking_frequency_milliseconds: tracking_frequency_milliseconds
		);

		let plugin_result = CDVPluginResult(
			status: CDVCommandStatus_OK
		)

		self.commandDelegate!.send(
			plugin_result,
			callbackId: command.callbackId
		)
	}

	/**
	 * Start location tracking
	 *
	 * @param command		Details of the command
	 *
	 * @return void
	 */
	@objc(start:)
	func start(command: CDVInvokedUrlCommand) {
		Constants.gps_monitoring_controller.start() {
			(result) in
				let plugin_result = CDVPluginResult(
					status: CDVCommandStatus_OK,
					messageAs: result
				)

				self.commandDelegate!.send(
					plugin_result,
					callbackId: command.callbackId
				)
			};
	}

	/**
	 * Stop location tracking
	 *
	 * @param command		Details of the command
	 *
	 * @return void
	 */
	@objc(stop:)
	func stop(command: CDVInvokedUrlCommand) {
		Constants.gps_monitoring_controller.stop();

		let plugin_result = CDVPluginResult(
			status: CDVCommandStatus_OK
		)

		self.commandDelegate!.send(
			plugin_result,
			callbackId: command.callbackId
		)
	}
}
