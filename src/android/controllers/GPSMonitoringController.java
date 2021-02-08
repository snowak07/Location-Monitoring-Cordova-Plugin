/**
 * GPS Monitoring Controller
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.controllers;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import edu.wisc.chess.plugins.chesslocationmonitoring.Constants;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.EventDatabaseTableHelpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.GPSDatabaseTableHelpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.GeofenceManager;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.Helpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.NotificationHelpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.ServiceStatusDatabaseTableHelpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.SettingsDatabaseTableHelpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.models.EventDatabaseRowObject;
import edu.wisc.chess.plugins.chesslocationmonitoring.models.GPSDatabaseRowObject;
import edu.wisc.chess.plugins.chesslocationmonitoring.models.GPSPoint;
import edu.wisc.chess.plugins.chesslocationmonitoring.models.Geofence;
import edu.wisc.chess.plugins.chesslocationmonitoring.models.ServiceStatusDatabaseRowObject;
import edu.wisc.chess.plugins.chesslocationmonitoring.services.APIService;
import edu.wisc.chess.plugins.chesslocationmonitoring.services.LocationService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GPSMonitoringController {
	/**
	 * API URL to use when saving Events
	 *
	 * @var String
	 */
	protected String EVENTS_API_URL = "logging/save-event";

	/**
	 * Notification ID for activated geofences
	 *
	 * @var int
	 */
	protected int GEOFENCE_ACTIVATED_NOTIFICATION_ID = 12587;

	/**
	 * API URL to use when saving the GPS coordinates
	 *
	 * @var String
	 */
	protected String GPS_COORDINATES_API_URL = "gps-coordinates/save-coordinates";

	/**
	 * Number of events to store before making an api request for geofence events
	 *
	 * @var int
	 */
	protected int NUM_GEOFENCE_EVENTS_BEFORE_API_REQUEST = 5;

	/**
	 * Number of events to store before making an api request for sensitive events (i.e. location provider or availability changing)
	 *
	 * @var int
	 */
	protected int NUM_EVENTS_BEFORE_API_REQUEST_SENSITIVE = 1;

	/**
	 * Number of events to send per API request
	 *
	 * @var int
	 */
	protected int NUM_EVENTS_PER_API_REQUEST = 30;

	/**
	 * Number of locations to store before making an api request
	 *
	 * @var int
	 */
	protected int NUM_LOCATIONS_BEFORE_API_REQUEST = 2;

	/**
	 * Number of locations to send per API request
	 *
	 * @var int
	 */
	protected int NUM_LOCATIONS_PER_API_REQUEST = 30;

	/**
	 * Number of meters that user has to move in order to get an update
	 *
	 * @var float
	 */
	protected float NUM_METERS_BETWEEN_UPDATES = 0; //4;

	/**
	 * Number of seconds to wait before sending locations to server when inactive
	 *
	 * @var double
	 */
	protected double NUM_SECONDS_BETWEEN_SERVER_SENDS_WHEN_INACTIVE = 2*60*60;

	/**
	 * Access token to use
	 *
	 * @var String
	 */
	protected String access_token = null;

	/**
	 * API Service to use
	 *
	 * @var APIService
	 */
	protected APIService api_service = null;

	/**
	 * Store the application context
	 *
	 * @var Context
	 */
	protected Context context = null;

	/**
	 * Manager for geofences
	 *
	 * @var GeofenceManager
	 */
	protected GeofenceManager geofence_manager = null;

	/**
	 * Details for the geofence notification
	 *
	 * @var HashMap<String, String>
	 */
	protected HashMap<String, String> geofence_notification_details = null;

	/**
	 * Helpers for gps table to use
	 *
	 * @var GPSDatabaseTableHelpers
	 */
	protected GPSDatabaseTableHelpers gps_database_helpers = null;

	/**
	 * Date of last geofence notification update (i.e. activation or cancelling)
	 *
	 * @var Date
	 */
	protected Date last_geofence_notification_update_date = null;

	/**
	 * Last known position
	 *
	 * @var Location
	 */
	protected Location last_position = null;

	/**
	 * Location service
	 *
	 * @var LocationService
	 */
	protected LocationService location_service = null;

	/**
	 * Used for location service notification
	 *
	 * @var ServiceConnection
	 */
	private ServiceConnection location_service_connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			String name = className.getClassName();
			if (name.endsWith("LocationService")) {
				location_service = ((LocationService.LocationServiceBinder) service).getService();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			if (className.getClassName().equals("LocationService")) {
				location_service = null;
			}
		}
	};

	/**
	 * Status of GPS Monitoring
	 *		no gps feature on device: Devices lacks GPS
	 * 		gps not enabled: GPS not enabled
	 *		not started: Monitoring has not been started
	 *		started: Monitoring is started
	 *		stopped: Monitoring is stopped
	 *
	 * @var string
	 */
	protected String monitoring_status = "";

	/**
	 * Permission status of GPS Monitoring possible values:
	 *		not checked: Permissions not checked yet
	 *		not granted: Permission not granted by user
	 *		granted, coarse, fine: Permissions granted and value depends on what level of permissions have been granted
	 *
	 * @var string
	 */
	protected String permission_status = "";

	/**
	 * Indicates if we should stop after our next location
	 *
	 * @var boolean
	 */
	protected boolean should_stop_after_next_location = false;

	/**
	 * Constructor for object
	 *
	 * @param context				Context to operate in
	 *
	 * @return void
	 */
	public GPSMonitoringController(Context context, boolean initialize_location_service) {
		this.context = context;

		SettingsDatabaseTableHelpers settings_helper = new SettingsDatabaseTableHelpers(context);
		this.access_token = settings_helper.getSettingByKey("access_token");

		this.gps_database_helpers = new GPSDatabaseTableHelpers(context);
		this.api_service = new APIService(context);
		this.geofence_manager = new GeofenceManager();

		if (initialize_location_service) {
			this.initializeLocationService();
		}
	}

	/**
	 * Add polygon geofences from string
	 *
	 * @param json		String of geofences to parse and add
	 *
	 * @return void
	 */
	public void addGeofencesFromString(String json) {
		if (json == null) {
			return;
		}

		int i = 0;
		int j = 0;

		try {
			JSONObject json_object = new JSONObject(json);
			JSONObject notification_details = json_object.getJSONObject("notification");

			if (this.geofence_notification_details == null) {
				this.geofence_notification_details = new HashMap<String, String>();
			}

			String title = notification_details.getString("title");
			this.geofence_notification_details.put("title", title);

			String body = notification_details.getString("body");
			this.geofence_notification_details.put("body", body);

			JSONObject circular_geofences_object = json_object.getJSONObject("circular_geofences");
			JSONObject polygon_geofences_object = json_object.getJSONObject("polygon_geofences");

			Iterator<String> iterable = circular_geofences_object.keys();
			while (iterable.hasNext()) {
				String place_id = iterable.next();

				JSONObject circular_geofence = circular_geofences_object.getJSONObject(place_id);
				JSONObject center_object = circular_geofence.getJSONObject("center");

				double latitude = center_object.getDouble("lat");
				double longitude = center_object.getDouble("lon");
				double radius = Double.parseDouble(circular_geofence.getString("radius"));

				GPSPoint center = new GPSPoint(latitude, longitude);
				this.geofence_manager.addCircularGeofence(place_id, center, radius);

				i++;
			}

			iterable = polygon_geofences_object.keys();
			while (iterable.hasNext()) {
				String place_id = iterable.next();

				JSONArray coordinates = polygon_geofences_object.getJSONArray(place_id);
				GPSPoint[] points = new GPSPoint[coordinates.length()];

				for (int k = 0; k < coordinates.length(); k++) {
					JSONObject coordinate = coordinates.getJSONObject(k);

					double latitude = coordinate.getDouble("lat");
					double longitude = coordinate.getDouble("lon");

					points[k] = new GPSPoint(latitude, longitude);
				}

				this.geofence_manager.addPolygonGeofence(place_id, points);

				j++;
			}

		} catch (Exception error) {
			// Do Nothing
			error.printStackTrace();
		}

		Helpers.log("GPSMonitoringController.addGeofencesFromString: Num circular geofences = " + Double.toString(i) + ", Num polygon geofences = "  + Double.toString(j) + ", Total = " + Long.toString(this.geofence_manager.getGeofencesByState("").size()));
	}

	/**
	 * Check whether location tracking is supported and enabled
	 *
	 * @return boolean
	 */
	protected boolean checkLocationTrackingIsSupported() {
		PackageManager package_manager = context.getPackageManager();
		boolean has_gps = package_manager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

		if (!has_gps) {
			this.updateMonitoringStatus("no gps feature on device");
			return false;
		}

		boolean has_gps_enabled = this.isLocationTrackingEnabled();
		if (!has_gps_enabled) {
			this.updateMonitoringStatus("gps not enabled");

		} else {
			this.updateMonitoringStatus("gps enabled");
		}

		return has_gps_enabled;
	}

	/**
	 * Create a callback after events are saved to the server
	 *
	 * @return Callback
	 */
	protected Callback createSaveEventsToServerCallback() {
		return (new Callback() {
			@Override
			public void onFailure(Call call, IOException error) {
				// Something went wrong
				error.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String response_string = response.body().string();
				Helpers.log("createSaveEventsToServerCallback: onResponse callback: HTTP Status: " + response.code());
				Helpers.log("createSaveEventsToServerCallback: GPSMonitoringController.onResponse callback: Response Body: " + response_string);

				if (response.isSuccessful()) {
					try {
						JSONObject response_data = new JSONObject(response_string);
						handleSaveEventsResponseData(response_data);
					} catch (JSONException error) {
						error.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * Create a callback after locations are saved to the server
	 *
	 * @return Callback
	 */
	protected Callback createSaveLocationsToServerCallback() {
		return (new Callback() {
			@Override
			public void onFailure(Call call, IOException error) {
				// Something went wrong
				error.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String response_string = response.body().string();
				Helpers.log("createSaveLocationsToServerCallback: onResponse callback: HTTP Status: " + response.code());
				Helpers.log("createSaveLocationsToServerCallback: onResponse callback: Response Body: " + response_string);

				if (response.isSuccessful()) {
					try {
						JSONObject response_data = new JSONObject(response_string);
						handleSaveLocationsResponseData(response_data);
					} catch (JSONException error) {
						error.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * Create the location callback
	 *
	 * @return LocationCallback
	 */
	protected LocationCallback createLocationCallback() {
		GPSMonitoringController __this = this;

		return new LocationCallback() {
			public void onLocationResult(LocationResult location_result) {
				__this.handleLocationResult(location_result);
			}
		};
	}

	/**
	 * Return last known position
	 *
	 * @return JSONObject
	 */
	public JSONObject getLastPosition(CallbackContext callback) {
		Helpers.log("GPSMonitoringController.getLastPosition: Started");
		ArrayList<GPSDatabaseRowObject> results = this.gps_database_helpers.getLastNLocations(1);

		if (results.size() > 0) {
			try {
				return results.get(0).returnAsJSONObject();

			} catch (JSONException error) {
				error.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * Return monitoring status
	 *
	 * @return String
	 */
	public String getMonitoringStatus() {
		this.checkLocationTrackingIsSupported();
		return this.monitoring_status;
	}

	/**
	 * Return permission status
	 *
	 * @return String
	 */
	public String getPermissionStatus() {
		this.hasPermissionToLocation();
		return this.permission_status;
	}

	/**
	 * Handle location result
	 *
	 * @param location_result   Location to handle
	 *
	 * @return void
	 */
	public void handleLocationResult(LocationResult location_result) {
		if (location_result == null) {
			Helpers.log("GPSMonitoringController.handleLocationResult: LocationResults is null");
			return;
		}

		Location last_position = location_result.getLastLocation();
		ArrayList<Location> locations = (ArrayList<Location>)location_result.getLocations();

		Helpers.log("GPSMonitoringController.handleLocationResult: Handling " + locations.size() + " location(s)");

		int i = 0;
		while (i < locations.size()) {
			Location location = locations.get(i);
			Helpers.log("GPSMonitoringController.handleLocationResult: Checking location: " + location.toString());

			if (last_position.getLatitude() != location.getLatitude() && last_position.getLongitude() != location.getLongitude()) {
				Helpers.log("GPSMonitoringController.handleLocationResult: Saving location: " + location.toString());
				this.gps_database_helpers.insertLocation(this.context, this.access_token, location);
			}

			i = i + 1;
		}

		this.updatePosition(last_position);
		this.updateGeofenceManagerWithLocation(last_position);

		if (this.should_stop_after_next_location) {
			//this.location_service.removeLocationUpdates(this.createLocationCallback());
		}
	}

	/**
	 * Handle location
	 *
	 * @param location   Location to handle
	 *
	 * @return void
	 */
	public void handleLocation(Location location) {
		if (location == null) {
			return;
		}

		this.updatePosition(location);
		this.updateGeofenceManagerWithLocation(location);

		if (this.should_stop_after_next_location) {
			// this.location_service.removeLocationUpdates(this.createLocationCallback());
		}
	}

	/**
	 * Handle the response data from saving events
	 *
	 * @param response_data			Data to handle
	 *
	 * @return void
	 */
	protected void handleSaveEventsResponseData(JSONObject response_data) throws JSONException {
		EventDatabaseTableHelpers events_helpers = new EventDatabaseTableHelpers(this.context);

		JSONArray ids_saved = response_data.getJSONArray("ids_saved");
		events_helpers.deleteEventsById(ids_saved);

		if (events_helpers.getEventCount() >= this.NUM_GEOFENCE_EVENTS_BEFORE_API_REQUEST) {
			this.sendEventsToServer();
		}
	}

	/**
	 * Handle the response data from saving locations
	 *
	 * @param response_data			Data to handle
	 *
	 * @return void
	 */
	protected void handleSaveLocationsResponseData(JSONObject response_data) throws JSONException {
		JSONArray ids_saved = response_data.getJSONArray("ids_saved");
		this.gps_database_helpers.deleteLocationsById(ids_saved);

		if (this.gps_database_helpers.getLocationCount() >= this.NUM_LOCATIONS_BEFORE_API_REQUEST) {
			this.sendLocationsToServer();
		}
	}

	/**
	 * Check whether user has granted the app permissions
	 *
	 * @return boolean
	 */
	protected boolean hasPermissionToLocation() {
		String status = "";

		if (Helpers.isManifestPermissionGranted(this.context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
			status += ", coarse";
		}

		if (Helpers.isManifestPermissionGranted(this.context, Manifest.permission.ACCESS_FINE_LOCATION)) {
			status += ", fine";
		}

		if (Helpers.isManifestPermissionGranted(this.context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
			status += ", background";
		}

		if (!status.equalsIgnoreCase("")) {
			this.updatePermissionStatus("granted" + status);
			return true;
		}

		this.updatePermissionStatus("not granted");
		return false;
	}

	/**
	 * Initialize location services
	 *
	 * @return void
	 */
	protected void initializeLocationService() {
		if (!this.hasPermissionToLocation()) {
			return;
		}

		if (this.location_service != null) {
			return;
		}

		this.location_service = new LocationService();
		Intent intent = new Intent(this.context, LocationService.class);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			this.context.startForegroundService(intent);

		} else {
			this.context.startService(intent);
		}

		this.context.bindService(intent, location_service_connection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * Returns whether location tracking is enabled
	 *
	 * @return boolean
	 */
	protected boolean isLocationTrackingEnabled() {
		int location_mode = 0;
		String location_providers;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			try {
				location_mode = Settings.Secure.getInt(this.context.getContentResolver(), Settings.Secure.LOCATION_MODE);

			} catch (Exception error) {
				error.printStackTrace();
				return false;
			}

			return (location_mode != Settings.Secure.LOCATION_MODE_OFF);

		} else {
			location_providers = Settings.Secure.getString(this.context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

			return !TextUtils.isEmpty(location_providers);
		}
	}

	/**
	 * Start getting locations, save the current location, then stop location requests
	 *
	 * @return void
	 */
	public void saveCurrentLocationThenStop() {
		this.should_stop_after_next_location = true;
		this.start();
	}

	/**
	 * Save monitoring status to database
	 *
	 * @return void
	 *
	 * @throws JSONException
	 */
	public void saveMonitoringStatus() throws JSONException {
		this.checkLocationTrackingIsSupported(); // Sets the monitoring status
		this.hasPermissionToLocation(); // Sets the permission status

		// Check if our monitoring status has changed compared to what is in the database
		ServiceStatusDatabaseTableHelpers services_helpers = new ServiceStatusDatabaseTableHelpers(this.context);
		ServiceStatusDatabaseRowObject location_monitoring_status = services_helpers.getByService(Constants.LOCATION_MONITORING_SERVICE_NAME);
		ServiceStatusDatabaseRowObject location_permission_status = services_helpers.getByService(Constants.LOCATION_PERMISSION_SERVICE_NAME);

		if (location_monitoring_status == null) {
			location_monitoring_status = new ServiceStatusDatabaseRowObject();
			location_monitoring_status.setService(Constants.LOCATION_MONITORING_SERVICE_NAME);
			location_monitoring_status.setStatus("");
		}

		if (location_permission_status == null) {
			location_permission_status = new ServiceStatusDatabaseRowObject();
			location_permission_status.setService(Constants.LOCATION_PERMISSION_SERVICE_NAME);
			location_permission_status.setStatus("");
		}

		Helpers.log("GPSMonitoringController.saveMonitoringStatus: Monitoring Status: " + this.monitoring_status);
		Helpers.log("GPSMonitoringController.saveMonitoringStatus: Stored Monitoring Status: " + location_monitoring_status.getStatus());
		Helpers.log("GPSMonitoringController.saveMonitoringStatus: Permission Status: " + this.permission_status);
		Helpers.log("GPSMonitoringController.saveMonitoringStatus: Stored Permission Status: " + location_permission_status.getStatus());

		// If it has not changed, do nothing
		if (location_monitoring_status.getStatus().equals(this.monitoring_status) &&
			location_permission_status.getStatus().equals(this.permission_status)) {
			return;
		}

		location_monitoring_status.setStatus(this.monitoring_status);
		location_permission_status.setStatus(this.permission_status);

		// Save an event
		JSONObject objects = new JSONObject();
		objects.put("location_monitoring_status", location_monitoring_status.getStatus());
		objects.put("location_permission_status", location_permission_status.getStatus());

		services_helpers.insertServiceStatus(
			location_monitoring_status.getService(),
			location_monitoring_status.getStatus()
		);

		services_helpers.insertServiceStatus(
			location_permission_status.getService(),
			location_permission_status.getStatus()
		);

		EventDatabaseTableHelpers events_helpers = new EventDatabaseTableHelpers(this.context);
		events_helpers.insertEvent(this.access_token, "location tracking", "changing monitoring or permissions status", objects);
		if (events_helpers.getEventCount() >= this.NUM_EVENTS_BEFORE_API_REQUEST_SENSITIVE) {
			this.sendEventsToServer();
		}
	}

	/**
	 * Send events to server
	 *
	 * @return void
	 */
	protected void sendEventsToServer() {
		EventDatabaseTableHelpers helpers = new EventDatabaseTableHelpers(this.context);
		ArrayList<EventDatabaseRowObject> database_events = helpers.getFirstNEvents(this.NUM_EVENTS_PER_API_REQUEST);
		JSONArray events = new JSONArray();

		int i = 0;
		while (i < database_events.size()) {
			try {
				events.put(database_events.get(i).returnAsJSONObject());
			} catch (JSONException error) { }

			i = i + 1;
		}

		try {
			JSONObject data = new JSONObject();
			data.put("events", events);

			this.api_service.makeRequest(
				"post",
				this.EVENTS_API_URL,
				data,
				this.createSaveEventsToServerCallback()
			);

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Send locations to server
	 *
	 * @return void
	 */
	public void sendLocationsToServer() {
		this.sendLocationsToServer(false);
	}

	/**
	 * Send locations to server
	 *
	 * @param force_send		Indicates if we should force send the locations
	 *
	 * @return void
	 */
	public void sendLocationsToServer(boolean force_send) {
		if (!force_send && this.gps_database_helpers.getLocationCount() < this.NUM_LOCATIONS_BEFORE_API_REQUEST) {
			return;
		}

		ArrayList<GPSDatabaseRowObject> database_locations = this.gps_database_helpers.getFirstNLocations(this.NUM_LOCATIONS_PER_API_REQUEST);

		if (database_locations.size() == 0) {
			return;
		}

		JSONArray locations = new JSONArray();

		int i = 0;
		while (i < database_locations.size()) {
			try {
				locations.put(database_locations.get(i).returnAsJSONObject());
			} catch (JSONException error) {
				// Do Nothing
			}

			i = i + 1;
		}

		try {
			JSONObject data = new JSONObject();
			data.put("coordinates", locations);

			this.api_service.makeRequest(
				"post",
				this.GPS_COORDINATES_API_URL,
				data,
				this.createSaveLocationsToServerCallback()
			);

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Start monitoring
	 *
	 * @return void
	 */
	public void start() {
		Helpers.log("GPSMonitoringController.start: Number of locations - " + Long.toString(this.gps_database_helpers.getLocationCount()));

		if (!this.hasPermissionToLocation()) {
			Helpers.log("GPSMonitoringController.start: No permissions");
			return;
		}

		this.location_service.requestLocationUpdates(this.context, this.createLocationCallback());

		Helpers.log("GPSMonitoringController.start: Started Location Updates");
	}

	/**
	 * Stop monitoring
	 *
	 * @return void
	 */
	public void stop() {
		if (!this.hasPermissionToLocation()) {
			return;
		}

		this.location_service.removeLocationUpdates(this.createLocationCallback());

		Helpers.log("GPSMonitoringController.stop: Stopped Location Updates");
	}

	/**
	 * Update geofence manager with a location
	 *
	 * @param location		Location to update with
	 *
	 * @return void
	 */
	public void updateGeofenceManagerWithLocation(Location location) {
		// Check if geofences have been added yet
		if (this.geofence_manager.getGeofencesByState("").size() == 0) {
			SettingsDatabaseTableHelpers settings_helper = new SettingsDatabaseTableHelpers(this.context);
			String geofence_json_string = settings_helper.getSettingByKey("geofence_json_string");

			try {
				this.addGeofencesFromString(geofence_json_string);
				this.geofence_manager.loadGeofenceStatesFromDatabase(this.context);
			} catch (Exception error) {
				error.printStackTrace();
			}
		}

		Date now = new Date();
		double seconds_since_last_geofence_notification_update = 0;
		if (this.last_geofence_notification_update_date != null) {
			seconds_since_last_geofence_notification_update = ((double)(now.getTime() - this.last_geofence_notification_update_date.getTime())) / 1000.0;
		}

		// Reset geofence states every 24 hours
		if (seconds_since_last_geofence_notification_update > 24 * 3600) {
			this.geofence_manager.resetGeofenceStates();
		}

		this.geofence_manager.updateGeofenceStates(location);
		this.geofence_manager.saveGeofenceStatesToDatabase(this.context);

		NotificationHelpers notification_helpers = new NotificationHelpers(this.context);

		HashMap<String, Geofence> activated_geofences = this.geofence_manager.getGeofencesByState("activated");
		HashMap<String, Geofence> inactivated_geofences = this.geofence_manager.getGeofencesByState("inactivated");

		double num_activated_geofences = activated_geofences.size();
		double num_active_geofences = this.geofence_manager.getGeofencesByState("active").size();
		double num_inactivated_geofences = inactivated_geofences.size();

		Helpers.log("GPSMonitoringController.updateGeofenceManagerWithLocation:\nNum Activated Geofences = " + Double.toString(num_activated_geofences) + "\nNum Active Geofences = " + Double.toString(num_active_geofences) + "\nNum Inactivated Geofences = " + Double.toString(num_inactivated_geofences) + "\nLast activation (seconds) = " + Double.toString(seconds_since_last_geofence_notification_update));

		EventDatabaseTableHelpers events_helpers = new EventDatabaseTableHelpers(this.context);
		try {
			if (num_activated_geofences > 0) {
				String json_str = "";
				for (String place_id : activated_geofences.keySet()) {
					if (!json_str.equalsIgnoreCase("")) {
						json_str = json_str.concat(",");
					}

					json_str = json_str.concat(place_id);
				}

				JSONObject json = new JSONObject();
				json.put("activated_geofence_place_ids", json_str);

				events_helpers.insertEvent(this.access_token, "location tracking", "entering geofence", json);
			}

			if (num_inactivated_geofences > 0) {
				String json_str = "";
				for (String place_id : inactivated_geofences.keySet()) {
					if (!json_str.equalsIgnoreCase("")) {
						json_str = json_str.concat(",");
					}

					json_str = json_str.concat(place_id);
				}

				JSONObject json = new JSONObject();
				json.put("inactivated_geofence_place_ids", json_str);

				events_helpers.insertEvent(this.access_token, "location tracking", "exiting geofence", json);
			}

		} catch (JSONException error) {
			error.printStackTrace();
		}

		if (
			num_activated_geofences > 0 &&
			(seconds_since_last_geofence_notification_update > 60 || this.last_geofence_notification_update_date == null) &&
			!notification_helpers.isNotificationShowing(this.GEOFENCE_ACTIVATED_NOTIFICATION_ID)
		) {
			String title = "";
			if (this.geofence_notification_details.containsKey("title")) {
				title = this.geofence_notification_details.get("title");
			}

			String body = "";
			if (this.geofence_notification_details.containsKey("body")) {
				body = this.geofence_notification_details.get("body");
			}

			try {
				JSONObject json = new JSONObject();
				json.put("title", title);
				json.put("body", body);
				events_helpers.insertEvent(this.access_token, "location tracking", "showed geofence notification", json);

			} catch (Exception error) {
				error.printStackTrace();
			}

			notification_helpers.showNotification(this.GEOFENCE_ACTIVATED_NOTIFICATION_ID, title, body, null);
			this.last_geofence_notification_update_date = new Date();
		}

		if (
				num_activated_geofences == 0 &&
				num_active_geofences == 0 &&
				notification_helpers.isNotificationShowing(this.GEOFENCE_ACTIVATED_NOTIFICATION_ID)
		) {
			notification_helpers.cancelNotificationsByIdentifier(this.GEOFENCE_ACTIVATED_NOTIFICATION_ID);
			this.last_geofence_notification_update_date = new Date();
		}

		if (events_helpers.getEventCount() >= this.NUM_GEOFENCE_EVENTS_BEFORE_API_REQUEST) {
			this.sendEventsToServer();
		}
	}

	/**
	 * Update monitoring status
	 *
	 * @param new_status		Status to set
	 *
	 * @return void
	 */
	protected void updateMonitoringStatus(String new_status) {
		this.monitoring_status = new_status;
	}

	/**
	 * Update permission status
	 *
	 * @param new_status		Status to set
	 *
	 * @return void
	 */
	protected void updatePermissionStatus(String new_status) {
		this.permission_status = new_status;
	}

	/**
	 * Update monitoring object position. Skips positions that are exactly the same.
	 * @NOTE Caveat: This will save a new position even if they haven't moved if the user switches off their phone and turns it back on.
	 *
	 * @param position		Position to set
	 *
	 * @return void
	 */
	protected void updatePosition(Location position) {
		if (position == null) {
			return;
		}

		float num_meters_between_locations = -1;

		Helpers.log("GPSMonitoringController.updatePosition: Position" + position.toString());
		ArrayList<GPSDatabaseRowObject> results = this.gps_database_helpers.getLastNLocations(1);
		String last_location_time = this.gps_database_helpers.getLastLocationTime();

		if (results.size() > 0) {
			GPSDatabaseRowObject last_saved_position = results.get(0);

			this.last_position = new Location("");
			this.last_position.setLatitude(Double.parseDouble(last_saved_position.getLatitude()));
			this.last_position.setLongitude(Double.parseDouble(last_saved_position.getLongitude()));

			num_meters_between_locations = Helpers.getDistanceBetweenTwoLocations(this.last_position, position);
		}

		Helpers.log("GPSMonitoringController.updatePosition: Number of meters between this location and last location - " + num_meters_between_locations);

		if (this.last_position != null && num_meters_between_locations < this.NUM_METERS_BETWEEN_UPDATES) {
			String last_location_information = this.last_position.toString();
			if (last_location_time != null) {
				last_location_information += " at " + last_location_time;
			}

			Helpers.log("GPSMonitoringController.updatePosition: Too close to original position, not saving");
			Helpers.log("GPSMonitoringController.updatePosition: Number of locations - " + this.gps_database_helpers.getLocationCount() + " - Last location: " + last_location_information);
			Helpers.log("GPSMonitoringController.updatePosition: Next send, if inactive - " + (Double.parseDouble(last_location_time) + this.NUM_SECONDS_BETWEEN_SERVER_SENDS_WHEN_INACTIVE));

			// Try sending events to the server if it has been hour since the last location time
			if (last_location_time != null && !last_location_time.equalsIgnoreCase("0") && Double.parseDouble(Helpers.getUnixTimeAsString()) > Double.parseDouble(last_location_time) + this.NUM_SECONDS_BETWEEN_SERVER_SENDS_WHEN_INACTIVE) {
				Helpers.log("GPSMonitoringController.updatePosition: User inactive since " + last_location_time + ", sending data to server");
				this.sendLocationsToServer(true);
			}

			return;
		}

		this.last_position = position;
		this.gps_database_helpers.insertLocation(this.context, this.access_token, position);

		Helpers.log("GPSMonitoringController.updatePosition: Next send, if inactive - " + (Double.parseDouble(last_location_time) + this.NUM_SECONDS_BETWEEN_SERVER_SENDS_WHEN_INACTIVE));

		// Try sending events to the server if it has been hour since the last location time
		if (last_location_time != null && !last_location_time.equalsIgnoreCase("0") && Double.parseDouble(Helpers.getUnixTimeAsString()) > Double.parseDouble(last_location_time) + this.NUM_SECONDS_BETWEEN_SERVER_SENDS_WHEN_INACTIVE) {
			Helpers.log("GPSMonitoringController.updatePosition: User inactive since " + last_location_time + ", sending data to server");
			this.sendLocationsToServer(true);

		} else {
			this.sendLocationsToServer();
		}

		String last_location_information = this.last_position.toString();
		if (last_location_time != null) {
			last_location_information += " at " + last_location_time;
		}

		Helpers.log("GPSMonitoringController.updatePosition: Number of locations - " + this.gps_database_helpers.getLocationCount() + " - Last location: " + last_location_information);
	}
}
