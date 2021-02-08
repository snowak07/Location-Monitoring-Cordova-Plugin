/**
 * Helper functions
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.helpers;

import edu.wisc.chess.plugins.chesslocationmonitoring.Constants;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class Helpers {
	/**
	 * Return distance between points in meters
	 *
	 * @return float
	 */
	public static float getDistanceBetweenTwoLocations(Location position_1, Location position_2) {
		return position_1.distanceTo(position_2); // In Meters
	}

	/**
	 * Return unix time
	 *
	 * @return String
	 */
	public static String getUnixTimeAsString() {
		Double current_time = Double.valueOf("" + System.currentTimeMillis());
		return "" + (current_time / 1000);
	}

	/**
	 * Return if a manifest permission is granted
	 *
	 * @param context			Context to operate in
	 * @param permission		Permission to check
	 *
	 * @return boolean
	 */
	public static boolean isManifestPermissionGranted(Context context, String permission) {
		return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * Log a message
	 *
	 * @param message		Message to log
	 *
	 * @return String
	 */
	public static String log(String message)
	{
		Log.v(Constants.LOG_TAG, message);
		return message;
	}

	/**
	 * Round a value to the given number of places
	 *
	 * @param number		Number to round
	 * @param places		Number of places to round to
	 *
	 * @return double
	 */
	public static double round(double number, int places) {
		number = number / Math.pow(10, places);
		number = Long.valueOf(Math.round(number)).doubleValue();
		number = number * Math.pow(10, places);

		return number;
	}
}
