## Passing Geofences

### Circular Geofence
Each circular geofence takes a center point and a radius (in meters)

Example:
	"center": {
		"lat": 43.012050,
		"lon": -89.490087
	},
	"radius": 5.0

### Polygon Geofence
- Polygons can be many sided, the code will make straight lines between the current point and the subsequent point.
- Each polygon geofence must have a beginning and ending point that are the same.
- Google maps can help determine GPS locations by clicking on specific unnamed places.

Example of a square geofence:
	{ "lat": "43.015", "lon": "-89.495" },
	{ "lat": "43.015", "lon": "-89.485" },
	{ "lat": "43.005", "lon": "-89.485" },
	{ "lat": "43.005", "lon": "-89.495" },
	{ "lat": "43.015", "lon": "-89.495" }

### Full Example for Geofence with notification shown geofence is activated
{
	"notification": {
		"title": "High Risk Location",
		"body": "Click here to access A-CHESS"
	},
	"circular_geofences": {
		"fence_1": {
			"center": {
				"lat": 43.012050,
				"lon": -89.490087
			},
			"radius": 5.0
		}
	},
	"polygon_geofences": {
		"fence_2": {
			{ "lat": "43.015", "lon": "-89.495" },
			{ "lat": "43.015", "lon": "-89.485" },
			{ "lat": "43.005", "lon": "-89.485" },
			{ "lat": "43.005", "lon": "-89.495" },
			{ "lat": "43.015", "lon": "-89.495" }
		}
	}
}

## Logic and Gotchas
### Geofences Edge cases
- When phone turns off within a geofence then we should show notification when phone is turned back on.
- When phone remains in geofence indefinitely then should show notification every 24 hours that phone is in geofence.
- When phone stops location tracking within geofence with notification showing then notification should remain showing.

### Known Issues with Geofences
- Android: Service sometimes doesn't start after phone reboot and sometimes stops if app is swiped away. Workaround: Open app.
- Android: Location updates don't seem very frequent. Workaround: Open an app that actively queries your location and displays it on a map or open Tick Activity within Tick App.
- Android: In order to get locations to happen continuously even when app is swiped away, need to allow for background location tracking, have a foreground service with a notification. This notification can be disabled in Settings. On Android O (Android 8.0+, API 26+), you will need to have Gradle's target sdk version be 29.

## How to test updates to plugin
### Android
ionic cordova platform rm android; rm -r plugins/chess-location-monitoring; ionic cordova platform add android; ionic cordova build android

### iOS
ionic cordova platform rm ios; rm -r plugins/chess-location-monitoring; ionic cordova platform add ios; ionic cordova build ios

#### Swift files
There are a number of .swift files located in the unused-swift-files that have been kept for documentation purposes.
