/**
 * Adapted from: https://github.com/android/location-samples/blob/main/LocationUpdatesPendingIntent/app/src/main/java/com/google/android/gms/location/sample/locationupdatespendingintent/LocationUpdatesBroadcastReceiver.java
 *
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.wisc.chess.plugins.chesslocationmonitoring.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.LocationResult;

import edu.wisc.chess.plugins.chesslocationmonitoring.controllers.GPSMonitoringController;

/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O
 * {@link android.app.PendingIntent#getBroadcast(Context, int, Intent, int)} should be used when
 * requesting location updates. Due to limits on background services,
 * {@link android.app.PendingIntent#getService(Context, int, Intent, int)} should not be used.
 *
 *  Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 *  less frequently than the interval specified in the
 *  {@link com.google.android.gms.location.LocationRequest} when the app is no longer in the
 *  foreground.
 */
public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
	/**
	 * Action Receiver to use
	 *
	 * @var String
	 */
	public static final String ACTION_PROCESS_UPDATES = "edu.wisc.chess.plugins.chesslocationmonitoring.locationupdatespendingintent.action.PROCESS_UPDATES";

	/**
	 * Handle broadcast
	 *
	 * @param context		Context to handle
	 * @param intent		Intent to handle
	 *
	 * @return void
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) {
			return;
		}

		final String action = intent.getAction();
		if (!ACTION_PROCESS_UPDATES.equals(action)) {
			return;
		}

		LocationResult result = LocationResult.extractResult(intent);
		if (result != null) {
			GPSMonitoringController gps_monitoring = new GPSMonitoringController(context, false);
			gps_monitoring.handleLocationResult(result);
		}
	}
}
