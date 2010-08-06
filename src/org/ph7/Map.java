package org.ph7;


import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        int latitudeE6 = (int)(getIntent().getExtras().getFloat("latitude") * 1e6);
        int longitudeE6 = (int)(getIntent().getExtras().getFloat("longitude") * 1e6);
        float accuracy = getIntent().getExtras().getFloat("accuracy");
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        Ph7ItemizedOverlay itemizedoverlay = new Ph7ItemizedOverlay(drawable, this);
        itemizedoverlay.setAccuracy(accuracy);
        GeoPoint point = new GeoPoint(latitudeE6, longitudeE6);
        OverlayItem overlayitem = new OverlayItem(point, "hello", "World");
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);
        mapView.getController().animateTo(point);
        itemizedoverlay.zoom(mapView, accuracy);
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
