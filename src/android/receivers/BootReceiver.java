/**
 * Boot Receiver object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import edu.wisc.chess.plugins.chesslocationmonitoring.services.BackgroundService;

public class BootReceiver extends BroadcastReceiver {
	/**
	 * Handle when boot happens
	 *
	 * @param context			Context to operate in
	 * @param intent			Intent to handle
	 *
	 * @return void
	 */
	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	@Override
	public void onReceive(Context context, Intent intent) {
		// Set up the alarms for the app
		BackgroundService.scheduleJob(context);
	}

	/**
	 * Enable the AppBootReceiver (across reboots)
	 *
	 * @param context		Context to use
	 *
	 * @return void
	 */
	public static void enable(Context context) {
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager package_manager = context.getPackageManager();

		package_manager.setComponentEnabledSetting(receiver,
			PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
			PackageManager.DONT_KILL_APP);
	}

	/**
	 * Disable the AppBootReceiver (across reboots)
	 *
	 * @param context		Context to use
	 *
	 * @return void
	 */
	public static void disable(Context context) {
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager package_manager = context.getPackageManager();

		package_manager.setComponentEnabledSetting(receiver,
			PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
			PackageManager.DONT_KILL_APP);
	}
}
