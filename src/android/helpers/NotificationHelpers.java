/**
 * Notification Helpers
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.helpers;

import android.content.Context;

import org.json.JSONObject;

import java.util.Date;
import java.util.Dictionary;

import de.appplant.cordova.plugin.localnotification.TriggerReceiver;
import de.appplant.cordova.plugin.notification.Manager;
import de.appplant.cordova.plugin.notification.Notification;
import de.appplant.cordova.plugin.notification.Options;
import de.appplant.cordova.plugin.notification.Request;

public class NotificationHelpers {
	/**
	* Store the application context
	*
	* @var Context
	*/
	protected Context context = null;

	/**
	 * Notification manager
	 *
	 * @var Manager
	 */
	protected Manager notification_manager = null;

	/**
	 * Constructor
	 *
	 * @param context   Context to operate in
	 *
	 * @return void
	 */
	public NotificationHelpers(Context context) {
		this.context = context;

		this.notification_manager = Manager.getInstance(this.context);
	}

	/**
	 * Cancel notification by identifier
	 *
	 * @param identifier		Identifier of notification
	 *
	 * @return void
	 */
	public void cancelNotificationsByIdentifier(int identifier) {
		this.notification_manager.cancel(identifier);
	}

	/**
	 * Return whether a notification is showing
	 *
	 * @param identifier		Identifier of notification
	 *
	 * @return boolean
	 */
	public boolean isNotificationShowing(int identifier) {
		Notification notification = this.notification_manager.get(identifier);
		if (notification == null) {
			return false;
		}

		return (notification.getType() == Notification.Type.TRIGGERED);
	}

	/**
	 * Show a notification
	 *
	 * @param identifier		Identifier of notification
	 * @param title				Title for the notification
	 * @param body				Body for the notification
	 * @param data				Data to include with the notification
	 *
	 * @return void
	 */
	public void showNotification(int identifier, String title, String body, Dictionary<String, String> data) {
		/*
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pending_intent = PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_ONE_SHOT);

		SettingsDatabaseTableHelpers settings_helper = new SettingsDatabaseTableHelpers(this.context);
		String channel_id = settings_helper.getSettingByKey("scheduled_job_id");

		Uri default_sound_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notification_builder =
			new NotificationCompat.Builder(context, channel_id)
				.setContentTitle(title)
				.setContentText(body)
				.setAutoCancel(true)
				.setSound(default_sound_uri)
				.setLights(Color.GREEN, 1000, 1000)
				.setContentIntent(pending_intent);

		notification_builder.setDefaults(Notification.DEFAULT_VIBRATE);
		notification_builder.setPriority(Notification.PRIORITY_HIGH);

		NotificationManager notification_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(channel_id, "Notifications", NotificationManager.IMPORTANCE_DEFAULT);
			notification_manager.createNotificationChannel(channel);
		}

		notification_manager.notify(identifier, notification_builder.build());
		*/

		JSONObject json_options = new JSONObject();

		try {
			json_options.put("title", title);
			json_options.put("text", body);
			json_options.put("id", identifier);
			json_options.put("foreground", true);
			json_options.put("priority", 1);
			json_options.put("icon", "res://ic_launcher");
			json_options.put("smallIcon", "res://ic_launcher");

			JSONObject progress_bar = new JSONObject();
			progress_bar.put("enabled", false);
			json_options.put("progressBar", progress_bar);

			JSONObject trigger_object = new JSONObject();

			// Schedule the notification for one second from now
			long time = (new Date()).getTime() + 1000;
			trigger_object.put("at", time);
			json_options.put("trigger", trigger_object);

			Helpers.log("NotificationHelpers.showNotification json scheduled: " + json_options.toString());
		} catch (Exception error) {
			error.printStackTrace();
		}

		Options options = new Options(this.context, json_options);
		Request request = new Request(options);
		this.notification_manager.schedule(request, TriggerReceiver.class);
	}
}
