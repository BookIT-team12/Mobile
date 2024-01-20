package com.example.bookit;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.core.content.ContextCompat;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class CustomMapView extends MapView {
    private Marker marker;
    private GeoPoint selectedGeoPoint;
    private GestureDetector gestureDetector;

    public CustomMapView(Context context) {
        super(context);
        initializeGestureDetector(context);
        initializeMarker();
    }

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeGestureDetector(context);
        initializeMarker();
    }

    private void initializeGestureDetector(Context context) {
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                selectedGeoPoint = (GeoPoint) getProjection().fromPixels((int) e.getX(), (int) e.getY());
                addMarker(selectedGeoPoint);
                return true;
            }
        });
        gestureDetector.setIsLongpressEnabled(false); // Add this line
    }

    private void initializeMarker(){
        GeoPoint initialCenter = new GeoPoint(45.2671, 19.8335);
        selectedGeoPoint = initialCenter;
        this.marker = new Marker(this);
        this.marker.setPosition(initialCenter);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Drawable customDrawable = ContextCompat.getDrawable(getContext(), R.drawable.location_icon_big);
        int colorResourceId = R.color.redwood;
        int colorValue = ContextCompat.getColor(getContext(), colorResourceId);
        customDrawable.setColorFilter(colorValue, PorterDuff.Mode.SRC_IN);
        marker.setIcon(customDrawable);
        getOverlays().add(this.marker);
        invalidate();
    }

    private void addMarker(GeoPoint geoPoint) {
        marker.setPosition(geoPoint);
        Drawable customDrawable = ContextCompat.getDrawable(getContext(), R.drawable.location_icon_big);
        int colorResourceId = R.color.redwood;
        int colorValue = ContextCompat.getColor(getContext(), colorResourceId);
        customDrawable.setColorFilter(colorValue, PorterDuff.Mode.SRC_IN);
        marker.setIcon(customDrawable);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        getOverlays().add(marker);
        invalidate(); // Redraw the map to show the new marker
    }

    public GeoPoint getSelectedGeoPoint() {
        return selectedGeoPoint;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Let the GestureDetector handle the event before interception
        getParent().requestDisallowInterceptTouchEvent(true);
        gestureDetector.onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }
}
