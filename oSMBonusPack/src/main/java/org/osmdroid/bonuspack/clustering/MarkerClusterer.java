package org.osmdroid.bonuspack.clustering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

/**
 * An overlay allowing to perform markers clustering.
 * Usage: put your markers inside with add(Marker), and add the MarkerClusterer to the map overlays.
 * Depending on the zoom level, markers will be displayed separately, or grouped as a single Marker. <br/>
 * <p/>
 * This abstract class provides the framework. Sub-classes have to implement the clustering algorithm,
 * and the rendering of a cluster.
 *
 * @author M.Kergall
 */
public abstract class MarkerClusterer extends Overlay {

    /**
     * impossible value for zoom level, to force clustering
     */
    protected static final int FORCE_CLUSTERING = -1;

    protected ArrayList<Marker> mItems = new ArrayList<Marker>();
    protected Point mPoint = new Point();
    protected ArrayList<StaticCluster> mClusters = new ArrayList<StaticCluster>();
    ;
    protected int mLastZoomLevel;
    protected Bitmap mClusterIcon;

    // abstract methods:

    public MarkerClusterer(Context ctx) {
        super(ctx);
        mLastZoomLevel = FORCE_CLUSTERING;
    }

    /**
     * clustering algorithm
     */
    public abstract ArrayList<StaticCluster> clusterer(MapView mapView);

    /**
     * Build the marker for a cluster.
     */
    public abstract Marker buildClusterMarker(StaticCluster cluster, MapView mapView);

    /**
     * build clusters markers to be used at next draw
     */
    public abstract void renderer(ArrayList<StaticCluster> clusters, Canvas canvas, MapView mapView);

    /**
     * Set the cluster icon to be drawn when a cluster contains more than 1 marker.
     * If not set, default will be the default osmdroid marker icon (which is really inappropriate as a cluster icon).
     */
    public void setIcon(Bitmap icon) {
        mClusterIcon = icon;
    }

    /**
     * Add the Marker.
     * Important: Markers added in a MarkerClusterer should not be added in the map overlays.
     */
    public void add(Marker marker) {
        mItems.add(marker);
    }

    /**
     * Force a rebuild of clusters at next draw, even without a zooming action.
     * Should be done when you changed the content of a MarkerClusterer.
     */
    public void invalidate() {
        mLastZoomLevel = FORCE_CLUSTERING;
    }

    /**
     * @return the Marker at id (starting at 0)
     */
    public Marker getItem(int id) {
        return mItems.get(id);
    }

    /**
     * @return the list of Markers.
     */
    public ArrayList<Marker> getItems() {
        return mItems;
    }

    @Override
    protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
        //if zoom has changed and mapView is now stable, rebuild clusters:
        int zoomLevel = mapView.getZoomLevel();
        if (zoomLevel != mLastZoomLevel && !mapView.isAnimating()) {
            mClusters = clusterer(mapView);
            renderer(mClusters, canvas, mapView);
            mLastZoomLevel = zoomLevel;
        }

		/*
        final Projection pj = mapView.getProjection();
		Drawable dd = new BitmapDrawable(mClusterIcon);
		dd.setBounds(-15, -15, 15, 15);
		*/

        for (StaticCluster cluster : mClusters) {
            cluster.getMarker().draw(canvas, mapView, shadow);

			/*
            if (cluster.getSize()>1){
				GeoPoint p1 = cluster.getPosition();
				GeoPoint p2 = new GeoPoint(p1.getLatitude()+gridSizeY, p1.getLongitude()-gridSizeX);
				Point p2Pixels= pj.toMapPixels(p2, null);
				drawAt(canvas, dd, p2Pixels.x, p2Pixels.y, false, 0.0f);
			}
			*/
        }
    }

    @Override
    public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView) {
        for (StaticCluster cluster : mClusters) {
            if (cluster.getMarker().onSingleTapConfirmed(event, mapView))
                return true;
        }
        return false;
    }

    @Override
    public boolean onLongPress(final MotionEvent event, final MapView mapView) {
        for (StaticCluster cluster : mClusters) {
            if (cluster.getMarker().onLongPress(event, mapView))
                return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
        for (StaticCluster cluster : mClusters) {
            if (cluster.getMarker().onTouchEvent(event, mapView))
                return true;
        }
        return false;
    }
}
