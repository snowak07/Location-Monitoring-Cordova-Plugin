/**
 * Polygon geofence class
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.models;

import android.location.Location;

public class PolygonGeofence extends Geofence {
	/**
	 * Set of vertices saved as coordinates that describe the perimeter of a polygon.
	 *
	 * @var GPSPoint[]
	 */
	GPSPoint[] points;

	/**
	 * Construct geofence
	 *
	 * @param id          Identifier for geofence
	 * @param points      Set of vertices saved as coordinates that describe the perimeter of a polygon.
	 *
	 * @return void
	 */
	public PolygonGeofence(String id, GPSPoint[] points) {
		super(id);
		this.points = points;
	}

	/**
	 * Check if point is within the geofence
	 *
	 * @param gps_point Point to check
	 *
	 * @return boolean
	 */
	public boolean isPointWithinGeofence(Location gps_point) {
		return this.pointIsInRegion(gps_point.getLongitude(), gps_point.getLatitude(), this.points);
	}

	/**
	 * This method checks to see if the provided Longitude(x) and Latitude(y) are located within
	 * the polygon whose perimeter is traced by the coordinates in polygonPerimeterPath.
	 *
	 * The method it uses to determine this is as follows:
	 *
	 * 1. Draw a horizontal line to the east(right) of the point made by the passed in parameters of
	 *      x and y.
	 * 2. Count the number of times the line intersects with the polygon edges.
	 * 3. The point is inside the polygon if either the number of intersections is odd or the point
	 *      lies on the edge of a polygon. If neither of these conditions are true, then the point
	 *      lies outside the polygon.
	 *
	 * NOTE: Algorithm based off of https://stackoverflow.com/questions/18486284/android-geofencing-polygon
	 *
	 * @param x The longitude of the point or user.
	 * @param y The latitude of the point or user.
	 * @param perimeter The set of vertices saved as coordinates that describe the
	 *                             perimeter of a polygon.
	 * @return boolean whether or not the provided point is within the provided region.
	 */
	protected boolean pointIsInRegion(double x, double y, GPSPoint[] perimeter) {
		int crossings = 0;

		GPSPoint point = new GPSPoint(x, y);
		int count = perimeter.length;

		// For each edge
		for (int i = 0; i < count; i++) {
			GPSPoint a = perimeter[i];

			// Get index of next vertice
			int j = i + 1;

			// If j is greater than the number of vertices on the polygon, go to first vertice
			// because it is a closed path and will loop around to the beginning.
			if (j >= count) {
				j = 0;
			}

			// Obtain the second point on the polygon path that will now complete the line segment
			// from a to b.
			GPSPoint b = perimeter[j];

			// If an infinitely long line is drawn out towards the east(right) of the point and
			// intercepts the line segment made by a and b, increment crossings by 1.
			if (rayCrossesSegment(point, a, b)) {
				crossings++;
			}
		}
		// If there are a Odd number of crossings, then the point is located inside the polygon.
		// Otherwise, it is located outside of it.
		return (crossings % 2 == 1);
	}

	/**
	 * This method takes a point and in a sense draws an infinitely long line to the east(right) of
	 * the point and determines if that line will intersect the line segment made by the points a
	 * and b.
	 *
	 * This method accomplishes this by first determining if the point has a higher or lower y
	 * position than the points a and b. Next it determines if the point is located to the left or
	 * right of the line segment. If it is located to the left, when an infinitely long line
	 * is drawn to the right of the point, the line will have to cross over the line segment made by
	 * a and b.
	 *
	 * NOTE: Algorithm based off of https://stackoverflow.com/questions/18486284/android-geofencing-polygon
	 *
	 * @param point point that you want to determine if crosses the a-b line segment
	 * @param a first point of line segment
	 * @param b second point of line segment
	 * @return boolean true if a line drawn to the right of point crosses the a-b line segment otherwise
	 *          return false.
	 */
	protected boolean rayCrossesSegment(GPSPoint point, GPSPoint a, GPSPoint b) {
		double px = point.longitude;
		double py = point.latitude;
		double ax = a.longitude;
		double ay = a.latitude;
		double bx = b.longitude;
		double by = b.latitude;

		// By always putting "b" above "a" in terms of latitude, the code doesn't have to check
		// which is higher later on.
		if (ay > by) {
			ax = b.longitude;
			ay = b.latitude;
			bx = a.longitude;
			by = a.latitude;
		}
		// alter the longitude to cater for 180 degree crossings
		if (px < 0) { px += 360; }
		if (ax < 0) { ax += 360; }
		if (bx < 0) { bx += 360; }

		// If the ray is too close to the segment it could cause errors so offset slightly.
		if (py == ay || py == by) { py += 0.00000001; }

		// If the point is located higher or lower than the points a and b, an infinitely long line
		// won't intersect the line segment made by a and b. Also, if the point is located farther
		// to the right of both of the points a and b, then an infinitely long line segment also
		// won't intersect the line segment made by a and b.
		if ((py > by || py < ay) || (px > Math.max(ax, bx))) { return false; }

		// At this point, the point has to be located within a range of y's where the max and min
		// are determined by the y values of the points a and b. If the point is located farther
		// to the left than both a and b, then an infinitely long line drawn to the right of the
		// point will intersect the line segment made by a and b.
		if (px < Math.min(ax, bx)) { return true; }

		// At this point, the only area not covered by the previous if statements is the area made
		// by creating a square whose diagonal is the line segment between a and b.
		// red = slope of the line segment from a to b
		// blue = slope of the line segment from a to p
		// If the slope of the line made by a and p is greater than the slope of the line made by
		// a and b, then the point p has to be to the left of the line segment a-b.
		// The exceptions are
		//  1. If the point p is directly above a, causing infinite slope.
		//  2. a-b is a vertical line which means px has to be equal to ax at this point in the if
		//      statements.
		double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.MAX_VALUE;
		double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.MAX_VALUE;
		return (blue >= red);
	}
}
