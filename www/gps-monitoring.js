/**
 * Interface between Ionic code and plugin code
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
var exec = require('cordova/exec');
var service = "GPSMonitoring";

// Default constructor for the Monitoring object
var GPSMonitoring = {
	/**
	 * Clear settings
	 *
	 * @param function success_callback		Function to call after execution
	 * @param function error_callback		Function to call when there is an error (OPTIONAL)
	 *
	 * @return void
	 */
	clearSettings: function(success_callback, error_callback) {
		exec(success_callback, error_callback, service, "clearSettings", []);
	},

	/**
	 * Return last position
	 *
	 * @param function success_callback		Function to call after execution
	 * @param function error_callback		Function to call when there is an error (OPTIONAL)
	 *
	 * @return void
	 */
	getLastPosition: function(success_callback, error_callback) {
		exec(success_callback, error_callback, service, "getLastPosition", []);
	},

	/**
	 * Return monitoring status
	 *
	 * @param function success_callback		Function to call after execution
	 * @param function error_callback		Function to call when there is an error (OPTIONAL)
	 *
	 * @return void
	 */
	getMonitoringStatus: function(success_callback, error_callback) {
		exec(success_callback, error_callback, service, "getMonitoringStatus", []);
	},

	/**
	 * Return permission status
	 *
	 * @param function success_callback		Function to call after execution
	 * @param function error_callback		Function to call when there is an error (OPTIONAL)
	 *
	 * @return void
	 */
	getPermissionStatus: function(success_callback, error_callback) {
		exec(success_callback, error_callback, service, "getPermissionStatus", []);
	},

	/**
	 * Initialize
	 *
	 * @param array		options					Options to pass to the initialize
	 * @param function	success_callback		Function to call after execution
	 * @param function	error_callback			Function to call when there is an error (OPTIONAL)
	 *
	 * @return void
	 */
	initialize: function(options, success_callback, error_callback) {
		exec(success_callback, error_callback, service, "initialize", options);
	},

	/**
	 * Start monitoring
	 *
	 * @param function success_callback		Function to call after execution
	 * @param function error_callback		Function to call when there is an error (OPTIONAL)
	 *
	 * @return void
	 */
	start: function(success_callback, error_callback) {
		exec(success_callback, error_callback, service, "start", []);
	},

	/**
	 * Stop monitoring
	 *
	 * @param function success_callback		Function to call after execution
	 * @param function error_callback		Function to call when there is an error (OPTIONAL)
	 *
	 * @return void
	 */
	stop: function(success_callback, error_callback) {
		exec(success_callback, error_callback, service, "stop", []);
	},
};

module.exports = GPSMonitoring;