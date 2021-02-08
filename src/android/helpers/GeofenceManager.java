/**
 * Manager for Geofences
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.helpers;

import android.content.Context;
import android.location.Location;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import edu.wisc.chess.plugins.chesslocationmonitoring.models.CircularGeofence;
import edu.wisc.chess.plugins.chesslocationmonitoring.models.GPSPoint;
import edu.wisc.chess.plugins.chesslocationmonitoring.models.Geofence;
import edu.wisc.chess.plugins.chesslocationmonitoring.models.PolygonGeofence;

public class GeofenceManager {
	/**
	 * Dictionary of geofences and their ids
	 *
	 * @var HashMap<String, Geofence>
	 */
	protected HashMap<String, Geofence> geofences;

	/**
	 * Construct the object
	 *
	 * @return void
	 */
	public GeofenceManager() {
		this.geofences = new HashMap<>();
	}

	/**
	 * Add a geofence to the list of geofences
	 *
	 * @param _id			Identifier for geofence
	 * @param center		Center of geofence
	 * @param radius		Radius to use
	 *
	 * @return void
	 */
	public void addCircularGeofence(String _id, GPSPoint center, double radius) {
		this.geofences.put(_id, new CircularGeofence(_id, center, radius));
	}

	/**
	 * Add a geofence to the list of geofences
	 *
	 * @param _id			Identifier for the geofence
	 * @param points		Points that make up the geofence's boundary
	 *
	 * @return void
	 */
	public void addPolygonGeofence(String _id, GPSPoint[] points) {
		this.geofences.put(_id, new PolygonGeofence(_id, points));
	}

	/**
	 * Return geofences by their current state
	 *
	 * @param state			State to return
	 *
	 * @return HashMap<String, Geofence>
	 */
	public HashMap<String, Geofence> getGeofencesByState(String state) {
		HashMap<String, Geofence> geofence_states = new HashMap<>();

		for (String place_id : this.geofences.keySet()) {
			Geofence temp_geofence = this.geofences.get(place_id);

			if (state.isEmpty() || temp_geofence.getState().equalsIgnoreCase(state)) {
				geofence_states.put(place_id, temp_geofence);
			}
		}

		return geofence_states;
	}

	/**
	 * Load geofence states from database
	 *
	 * @param context     Context to use
	 *
	 * @return void
	 */
	public void loadGeofenceStatesFromDatabase(Context context) {
		SettingsDatabaseTableHelpers settings_helper = new SettingsDatabaseTableHelpers(context);

		String geofence_states_string = settings_helper.getSettingByKey("geofence_states_string");
		if (geofence_states_string == null) {
			return;
		}

		try {
			JSONObject geofence_states = new JSONObject(geofence_states_string);

			Iterator<String> iterable = geofence_states.keys();
			while (iterable.hasNext()) {
				String place_id = iterable.next();
				if (!this.geofences.containsKey(place_id)) {
					continue;
				}

				Geofence temp_geofence = this.geofences.get(place_id);
				temp_geofence.setState(geofence_states.getString(place_id));

				this.geofences.put(place_id, temp_geofence);
			}

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Reset all geofence states
	 *
	 * @return void
	 */
	public void resetGeofenceStates() {
		for (String place_id : this.geofences.keySet()) {
			Geofence temp_geofence = this.geofences.get(place_id);
			temp_geofence.setState("");

			this.geofences.put(place_id, temp_geofence);
		}
	}

	/**
	 * Save geofence states to database
	 *
	 * @param context     Context to use
	 *
	 * @return void
	 */
	public void saveGeofenceStatesToDatabase(Context context) {
		SettingsDatabaseTableHelpers settings_helper = new SettingsDatabaseTableHelpers(context);

		try {
			JSONObject geofence_states = new JSONObject();

			for (String place_id : this.geofences.keySet()) {
				Geofence temp_geofence = this.geofences.get(place_id);
				geofence_states.put(place_id, temp_geofence.getState());
			}

			settings_helper.insertSetting("geofence_states_string", geofence_states.toString());

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Update geofence states
	 *
	 * @param point		Point used to update geofence states
	 *
	 * @return void
	 */
	public void updateGeofenceStates(Location point) {
		Helpers.log("GeofenceManager.updateGeofenceStates start, num geofences = " + Integer.toString(this.geofences.size()));

		for (String place_id : this.geofences.keySet()) {
			Geofence temp_geofence = this.geofences.get(place_id);

			try {
				String geofence_state = temp_geofence.getState();

				if (temp_geofence.isPointWithinGeofence(point)) {
					Helpers.log("GeofenceManager.updateGeofenceStates within geofence: id = " + place_id + ", state = " + temp_geofence.getState());

					if (geofence_state.equalsIgnoreCase("inactivated") || geofence_state.equalsIgnoreCase("inactive") || geofence_state.equalsIgnoreCase("")) {
						temp_geofence.setState("activated");

					} else {
						temp_geofence.setState("active");
					}

				} else {
					if (geofence_state.equalsIgnoreCase("activated") || geofence_state.equalsIgnoreCase("active")  || geofence_state.equalsIgnoreCase("")) {
						temp_geofence.setState("inactivated");

					} else {
						temp_geofence.setState("inactive");
					}
				}

				this.geofences.put(place_id, temp_geofence);

			} catch (Exception error) {
				error.printStackTrace();
			}
		}

		Helpers.log("GeofenceManager.updateGeofenceStates end");
	}
}
