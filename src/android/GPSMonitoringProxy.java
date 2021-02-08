/**
 * GPSMonitoringProxy Object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring;

import android.Manifest;
import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.app.ActivityCompat;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import edu.wisc.chess.plugins.chesslocationmonitoring.controllers.GPSMonitoringController;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.Helpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.SettingsDatabaseTableHelpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.services.BackgroundService;

public class GPSMonitoringProxy extends CordovaPlugin {
	/**
	 * GPS Monitoring controller
	 *
	 * @var GPSMonitoringController
	 */
	protected GPSMonitoringController gps_monitoring = null;

	/**
	 * Settings for web engine
	 *
	 * @var WebSettings
	 */
	public static WebSettings settings;

	/**
	 * Initialize the plugin
	 *
	 * @return void
	 */
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView web_view) {
		super.initialize(cordova, web_view);
		Helpers.log("GPSMonitoringProxy.initialize started");

		try {
			WebView my_web_view = (WebView) web_view.getView();
			my_web_view.post(() -> GPSMonitoringProxy.settings = my_web_view.getSettings());

		}catch (Exception error) {
			settings = null;
		}
	}

	/**
	 * Executes calls to the plugin through cordova.exec().
	 *
	 * @param action				Function to be called
	 * @param args					Arguments passed into function call
	 * @param callback_context		Context to operate in
	 *
	 * @return boolean
	 *
	 * @throws JSONException
	 */
	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callback_context) throws JSONException {
		Helpers.log("GPSMonitoringProxy.execute: action = " + action);

		Context context = this.cordova.getActivity().getApplicationContext();
		this.gps_monitoring = new GPSMonitoringController(context, true);

		if (action.equalsIgnoreCase("clearSettings")) {
			BackgroundService.cancelJob(context);

			SettingsDatabaseTableHelpers settings_helper = new SettingsDatabaseTableHelpers(context);
			settings_helper.deleteSettings();

			PluginResult result = new PluginResult(PluginResult.Status.OK, true);
			callback_context.sendPluginResult(result);

			return true;

		} else if (action.equalsIgnoreCase("getLastPosition")) {
			JSONObject position = gps_monitoring.getLastPosition(callback_context);

			PluginResult result = new PluginResult(PluginResult.Status.OK, position);
			callback_context.sendPluginResult(result);

			return true;

		} else if (action.equalsIgnoreCase("getMonitoringStatus")) {
			String status = gps_monitoring.getMonitoringStatus();

			PluginResult result = new PluginResult(PluginResult.Status.OK, status);
			callback_context.sendPluginResult(result);

			return true;

		} else if (action.equalsIgnoreCase("getPermissionStatus")) {
			String status = gps_monitoring.getPermissionStatus();

			PluginResult result = new PluginResult(PluginResult.Status.OK, status);
			callback_context.sendPluginResult(result);

			return true;

		} else if (action.equalsIgnoreCase("initialize")) {
			try {
				this.initializeSettings(args);
			} catch (Exception error) { }

			PluginResult result = new PluginResult(PluginResult.Status.OK, true);
			callback_context.sendPluginResult(result);

			return true;

		} else if (action.equalsIgnoreCase("start")) {
			if (
				!Helpers.isManifestPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION) ||
					!Helpers.isManifestPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
					!Helpers.isManifestPermissionGranted(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
			) {
				Helpers.log("GPSMonitoringProxy.execute: Permission check");

				ActivityCompat.requestPermissions(
					this.cordova.getActivity(),
					new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION },
					1
				);
			}

			// Set alarm to start location monitoring
			BackgroundService.cancelJob(context);
			BackgroundService.performMaintenance(context);
			BackgroundService.scheduleJob(context);

			PluginResult result = new PluginResult(PluginResult.Status.OK, true);
			callback_context.sendPluginResult(result);

			return true;

		} else if (action.equalsIgnoreCase("stop")) {
			// Set alarm to start location monitoring
			BackgroundService.cancelJob(context);

			PluginResult result = new PluginResult(PluginResult.Status.OK, true);
			callback_context.sendPluginResult(result);

			return true;
		}

		return false;
	}

	/**
	 * Initialize the plugin
	 *
	 * @param args		Options to initialize gps monitoring with
	 *
	 * @return void
	 */
	protected void initializeSettings(JSONArray args) throws Exception {
		Helpers.log("GPSMonitoringProxy.initializeSettings: Start saving settings");

		Context context = this.cordova.getActivity().getApplicationContext();
		JSONObject options = args.getJSONObject(0);
		Helpers.log("GPSMonitoringProxy.initializeSettings: Options=" + options.toString());

		if (!options.has("api_url") || options.getString("api_url").equalsIgnoreCase("")) {
			String error = "Invalid settings, requires a string `api_url`";
			Helpers.log("GPSMonitoringProxy.initializeSettings: " + error);
			throw new Exception(error);
		}

		if (!options.has("access_token") || options.getString("access_token").equalsIgnoreCase("")) {
			String error = "Invalid settings, requires a string `access_token`";
			Helpers.log("GPSMonitoringProxy.initializeSettings: " + error);
			throw new Exception(error);
		}

		if (!options.has("scheduled_job_id")) {
			String error = "Invalid settings, requires an integer `scheduled_job_id`";
			Helpers.log("GPSMonitoringProxy.initializeSettings: " + error);
			throw new Exception(error);
		}

		if (!options.has("tracking_frequency_milliseconds")) {
			String error = "Invalid settings, requires an integer `tracking_frequency_milliseconds`";
			Helpers.log("GPSMonitoringProxy.initializeSettings: " + error);
			throw new Exception(error);
		}

		String user_agent = "";
		if (settings != null) {
			user_agent = settings.getUserAgentString();
		}

		Helpers.log("GPSMonitoringProxy.initializeSettings: user_agent" + user_agent);

		// Save options in the database
		SettingsDatabaseTableHelpers settings_helper = new SettingsDatabaseTableHelpers(context);
		HashMap<String, String> settings = settings_helper.getSettings();

		settings.put("api_url", options.getString("api_url"));
		settings.put("access_token", options.getString("access_token"));
		settings.put("scheduled_job_id", Integer.toString(options.getInt("scheduled_job_id")));
		settings.put("tracking_frequency_milliseconds", Integer.toString(options.getInt("tracking_frequency_milliseconds")));
		settings.put("user_agent", user_agent);

		if (options.has("geofences")) {
			String geofence_json_string = options.getString("geofences");
			settings.put("geofence_json_string", geofence_json_string);
		}

		settings_helper.saveSettings(settings);

		Helpers.log("GPSMonitoringProxy.initializeSettings: Settings Saved: " + settings.toString());
	}

	/**
	 * Handle permission result
	 *
	 * @param request_code
	 * @param permissions
	 * @param grant_results
	 *
	 * @return void
	 */
	public void onRequestPermissionsResult(int request_code, String[] permissions, int[] grant_results) {
		if (request_code == 1) {
			Context context = this.cordova.getContext();

			// Set alarm to start location monitoring
			BackgroundService.cancelJob(context);
			BackgroundService.scheduleJob(context);
		}
	}
}
