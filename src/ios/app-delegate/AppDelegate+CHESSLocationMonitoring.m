/**
 * Add stuff to the App Delegate file
 *
 * @copyright Center for Health Enhancement Systems Studies
 */

#import "AppDelegate+CHESSLocationMonitoring.h"
#import "Constants.h"

@implementation AppDelegate (CHESSLocationMonitoring)

/**
 * Used for starting location monitoring when it goes into the background
 */
- (void) applicationDidEnterBackground:(UIApplication *)application {
	[Constants.gps_monitoring_controller startMonitoring: nil];
}

@end
