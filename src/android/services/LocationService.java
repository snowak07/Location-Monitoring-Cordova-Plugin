/**
 * Location Service: There are two ways to collect location in the background.
 * 	1) If location is not important, then you can get location a few times an hour using code without a notification however, this will not collect location is app is closed e.g. swiped away.
 * 	2) If you show a notification then the app will do consistent collection. The notification can be hidden on most Android devices in the Settings.
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.Helpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.SettingsDatabaseTableHelpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.receivers.LocationUpdatesBroadcastReceiver;

public class LocationService extends Service {
	/**
	 * Description for notification channel (will be shown to user)
	 *
	 * @var String
	 */
	protected String notification_channel_description = "Location tracking is active.";

	/**
	 * Identifier for notification
	 *
	 * @var int
	 */
	protected int notification_id = 12345678;

	/**
	 * Identifier for notification channel
	 *
	 * @var String
	 */
	protected String notification_channel_id = "channel_location_tracking";

	/**
	 * Name for notification channel (will be shown to user)
	 *
	 * @var String
	 */
	protected String notification_channel_name = "Actively Tracking Location";

	/**
	 * Thread to use for Location Requests
	 *
	 * @var HandlerThread
	 */
	protected final HandlerThread handler_thread = new HandlerThread("RequestLocation");

	/**
	 * Location client to use
	 *
	 * @var FusedLocationProviderClient
	 */
	protected FusedLocationProviderClient client = null;

	/**
	 * Indicates if location updates have been requested
	 *
	 * @var boolean
	 */
	protected boolean location_updates_requested = false;

	/**
	 * Create notification for Service that will allow us to collect location more frequently
	 *
	 * @return Notification
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	private Notification createNotification() {
		NotificationChannel channel = new NotificationChannel(this.notification_channel_id, this.notification_channel_name, NotificationManager.IMPORTANCE_DEFAULT);

		if (!this.notification_channel_description.isEmpty()) {
			channel.setDescription(this.notification_channel_description);
		}

		NotificationManager notificationManager = getSystemService(NotificationManager.class);
		notificationManager.createNotificationChannel(channel);

		Notification.Builder builder = new Notification.Builder(getApplicationContext(), this.notification_channel_id).setAutoCancel(true);
		return builder.build();
	}

	/**
	 * Return a pending intent for the location request
	 *
	 * @return PendingIntent
	 */
	private PendingIntent getLocationRequestPendingIntent(Context context) {
		Intent intent = new Intent(context, LocationUpdatesBroadcastReceiver.class);
		intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);

		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	/**
	 * Handle when service is bound
	 *
	 * @param intent		 Intent to handle
	 *
	 * @return IBinder
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Handle when service is created
	 *
	 * @return void
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			startForeground(this.notification_id, createNotification());
		}
	}

	/**
	 * Handle when service is destroyed
	 *
	 * @return void
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * Handle when start command is issued
	 *
	 * @param intent
	 * @param flags
	 * @param startId
	 *
	 * @return int
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	/**
	 * Start location updates
	 * @NOTE Based on LocationRequest: https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest#top_of_page
	 *
	 * @param context					Context to operate in
	 * @param location_callback			Callback used to handle location result
	 *
	 * @return void
	 */
	@SuppressLint("MissingPermission")
	public void requestLocationUpdates(Context context, LocationCallback location_callback) {
		// Since the code will run continuously, check if we have a client
		if (this.client != null && this.location_updates_requested) {
			Helpers.log("LocationService.requestLocationUpdates: Requested Location Updates already active");
			return;
		}

		if (this.client == null) {
			this.client = LocationServices.getFusedLocationProviderClient(context);
		}

		if (!this.location_updates_requested) {
			Helpers.log("LocationService.requestLocationUpdates: Requesting location updates");

			LocationRequest request = new LocationRequest();
			request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			request.setInterval(15*1000); // Sets slowest interval to get locations
			request.setFastestInterval(7*1000); // Sets fastest interval to get locations if other applications are getting location
			request.setExpirationDuration(7*1000); // Limits how long we look for locations
			request.setSmallestDisplacement(0); // Requires users to move at least n meters between location updates
			request.setMaxWaitTime(60*1000); // Sets the max amount of time to wait for a new location: https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest#public-locationrequest-setmaxwaittime-long-millis

			try {
				this.handler_thread.start();
				this.client.requestLocationUpdates(request, this.getLocationRequestPendingIntent(context));

			} catch (Exception error) {
				error.printStackTrace();
			}
		}

		this.location_updates_requested = true;
		Helpers.log("LocationService.requestLocationUpdates: Requested Location Updates");
	}

	/**
	 * Stop location updates
	 *
	 * @param location_callback		Callback used for handling locations
	 *
	 * @return void
	 */
	public void removeLocationUpdates(LocationCallback location_callback) {
		Helpers.log("LocationService.removeLocationUpdates: stopping location updates");
		try {
			this.handler_thread.quit();

		} catch (Exception error) {
			error.printStackTrace();
		}

		this.client.removeLocationUpdates(location_callback);
		this.location_updates_requested = false;
	}

	/**
	 * Class for Binding the service
	 */
	public class LocationServiceBinder extends Binder {
		public LocationService getService() {
			return LocationService.this;
		}
	}
}
