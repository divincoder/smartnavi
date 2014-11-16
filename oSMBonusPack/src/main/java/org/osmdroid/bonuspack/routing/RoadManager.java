package org.osmdroid.bonuspack.routing;

import android.content.Context;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Generic class to get a route between a start and a destination point,
 * going through a list of waypoints.
 *
 * @author M.Kergall
 * @see MapQuestRoadManager
 * @see GoogleRoadManager
 * @see OSRMRoadManager
 */
public abstract class RoadManager {

    protected String mOptions;

    public RoadManager() {
        mOptions = "";
    }

    /**
     * Using the road high definition shape, builds and returns a Polyline.
     *
     * @param road
     * @param color
     * @param width
     * @param context
     */
    public static Polyline buildRoadOverlay(Road road, int color, float width, Context context) {
        Polyline roadOverlay = new Polyline(context);
        roadOverlay.setColor(color);
        roadOverlay.setWidth(width);
        if (road != null) {
            ArrayList<GeoPoint> polyline = road.mRouteHigh;
            roadOverlay.setPoints(polyline);
        }
        return roadOverlay;
    }

    /**
     * Builds an overlay for the road shape with a default (and nice!) color.
     *
     * @return route shape overlay
     */
    public static Polyline buildRoadOverlay(Road road, Context context) {
        return buildRoadOverlay(road, 0x800000FF, 5.0f, context);
    }

    public abstract Road getRoad(ArrayList<GeoPoint> waypoints);

    /**
     * Add an option that will be used in the route request.
     * Note that some options are set in the request in all cases.
     *
     * @param requestOption see provider documentation.
     *                      Just one example: "routeType=bicycle" for MapQuest; "mode=bicycling" for Google.
     */
    public void addRequestOption(String requestOption) {
        mOptions += "&" + requestOption;
    }

    /**
     * @return the GeoPoint as a string, properly formatted: lat,lon
     */
    protected String geoPointAsString(GeoPoint p) {
        StringBuffer result = new StringBuffer();
        double d = p.getLatitudeE6() * 1E-6;
        result.append(Double.toString(d));
        d = p.getLongitudeE6() * 1E-6;
        result.append("," + Double.toString(d));
        return result.toString();
    }

}
