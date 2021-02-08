/**
 * Background Service Object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.json.JSONException;

import edu.wisc.chess.plugins.chesslocationmonitoring.controllers.GPSMonitoringController;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.Helpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.receivers.BackgroundServiceJobReceiver;
import edu.wisc.chess.plugins.chesslocationmonitoring.receivers.BootReceiver;

public class BackgroundService extends Service {
	/**
	 * Monitoring controller to use
	 *
	 * @var GPSMonitoringController
	 */
	protected static GPSMonitoringController gps_monitoring_controller = null;

	/**
	 * Schedule the job based on API version
	 *
	 * @param context		Context to operate in
	 *
	 * @return void
	 */
	public static void cancelJob(Context context) {
		BootReceiver.disable(context);

		(new BackgroundServiceJobReceiver()).removeAlarm(context);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Perform app maintenance
	 *
	 * @param context		Context to operate in
	 *
	 * @return void
	 */
	public static void performMaintenance(final Context context) {
		Helpers.log("BackgroundService.performMaintenance: Called");

		//if (gps_monitoring_controller == null) {
			Helpers.log("BackgroundService.performMaintenance: Creating gps_monitoring_controller");
			gps_monitoring_controller = new GPSMonitoringController(context, true);
		//}

		try {
			gps_monitoring_controller.saveMonitoringStatus();

		} catch (JSONException error) {
			// Do Nothing
		}

		gps_monitoring_controller.saveCurrentLocationThenStop();
		gps_monitoring_controller.sendLocationsToServer();

		Helpers.log("BackgroundService.performMaintenance: Finished Maintenance");
	}

	/**
	 * Schedule the job based on API version
	 *
	 * @param context		Context to operate in
	 *
	 * @return void
	 */
	public static void scheduleJob(Context context) {
		BootReceiver.enable(context);

		(new BackgroundServiceJobReceiver()).setAlarm(context);
	}
}
