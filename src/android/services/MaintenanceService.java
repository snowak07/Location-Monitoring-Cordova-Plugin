/**
 * Background Service for Pre-Marshmallow Devices
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import edu.wisc.chess.plugins.chesslocationmonitoring.receivers.BackgroundServiceJobReceiver;

@TargetApi(19)
public class MaintenanceService extends IntentService {
	/**
	 * Construct the MaintenanceService object
	 *
	 * @return void
	 */
	public MaintenanceService() {
		super("MaintenanceService");
	}

	/**
	 * Handle service intents
	 *
	 * @param intent		Intent to handle
	 *
	 * @return void
	 */
	@Override
	protected void onHandleIntent(@Nullable final Intent intent) {
		// Perform job
		BackgroundService.performMaintenance(getApplicationContext());

		// Schedule next job
		BackgroundService.scheduleJob(getApplicationContext());

		// Release the wake lock provided by the WakefulBroadcastReceiver
		BackgroundServiceJobReceiver.completeWakefulIntent(intent);
	}
}
