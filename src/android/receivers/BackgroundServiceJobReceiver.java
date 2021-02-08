/**
 * Background Recevier for Pre-Marshmallow Devices
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.receivers;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.legacy.content.WakefulBroadcastReceiver;

import java.util.Calendar;

import edu.wisc.chess.plugins.chesslocationmonitoring.Constants;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.Helpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.SettingsDatabaseTableHelpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.services.MaintenanceService;

@TargetApi(19)
public class BackgroundServiceJobReceiver extends WakefulBroadcastReceiver {
	/**
	 * App's AlarmManager, which provides access to the system alarm services.
	 *
	 * @var AlarmManager
	 */
	private AlarmManager alarm_manager;

	/**
	 * Pending intent that is triggered when the alarm fires.
	 *
	 * @var PendingIntent
	 */
	private PendingIntent alarm_intent;

	/**
	 * Handle the alarm
	 *
	 * @param context		Context to operate in
	 * @param intent		Intent to use (if any)
	 *
	 * @return void
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Helpers.log("BackgroundServiceJobReceiver.onReceive: Alarm received at " + Helpers.getUnixTimeAsString());
		this.setAlarm(context);

		// Start the service, keeping the device awake while it is launching.
		Intent service = new Intent(context, MaintenanceService.class);
		startWakefulService(context, service);
	}

	/**
	 * Remove an alarm
	 *
	 * @param context		Context to operate in
	 *
	 * @return void
	 */
	public void removeAlarm(Context context) {
		this.alarm_manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		SettingsDatabaseTableHelpers settings_helper = new SettingsDatabaseTableHelpers(context);
		int tracking_frequency_milliseconds = Integer.parseInt(settings_helper.getSettingByKey("tracking_frequency_milliseconds"));

		Intent intent = new Intent(context, BackgroundServiceJobReceiver.class);
		this.alarm_intent = PendingIntent.getBroadcast(context, tracking_frequency_milliseconds, intent, 0);
		this.alarm_manager.cancel(this.alarm_intent);
	}

	/**
	 * Set an alarm
	 *
	 * @param context		Context to operate in
	 *
	 * @return void
	 */
	public void setAlarm(Context context) {
		this.alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		SettingsDatabaseTableHelpers settings_helper = new SettingsDatabaseTableHelpers(context);
		int tracking_frequency_milliseconds = Integer.parseInt(settings_helper.getSettingByKey("tracking_frequency_milliseconds"));

		Intent intent = new Intent(context, BackgroundServiceJobReceiver.class);
		this.alarm_intent = PendingIntent.getBroadcast(context, tracking_frequency_milliseconds, intent, 0);

		// Set the alarm to fire right now with an interval of every scheduledJobFrequencyInMilliseconds minutes
		// Use exact for API 19 bug with inexact alarms
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		long alarm_time = calendar.getTimeInMillis() + tracking_frequency_milliseconds;

		// Set an exact alarm
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			this.alarm_manager.setExactAndAllowWhileIdle(
				AlarmManager.RTC_WAKEUP,
				alarm_time,
				this.alarm_intent
			);

		} else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			this.alarm_manager.setExact(
					AlarmManager.RTC_WAKEUP,
					alarm_time,
					this.alarm_intent
			);

		} else {
			this.alarm_manager.set(
					AlarmManager.RTC_WAKEUP,
					alarm_time,
					this.alarm_intent
			);
		}

		Helpers.log("BackgroundServiceJobReceiver.setAlarm: Alarm set for " + alarm_time);
	}
}
