package org.ph7;


import java.util.ArrayList;
import java.util.List;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity {
	ArrayList<Issue> issues = null;
	ArrayList<GeoPoint> points = null;
	int currentIndex = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
        final MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(
				R.drawable.ic_maps_indicator_current_position);
        
        boolean single = (boolean)(getIntent().getExtras()).getBoolean("single");
        if (single) {
        	int latitudeE6 = (int)(getIntent().getExtras().getFloat("latitude") * 1e6);
            int longitudeE6 = (int)(getIntent().getExtras().getFloat("longitude") * 1e6);
            float accuracy = getIntent().getExtras().getFloat("accuracy");
            
            ((Button)findViewById(R.id.ButtonPrevious)).setVisibility(View.INVISIBLE);      	
        	((Button)findViewById(R.id.ButtonNext)).setVisibility(View.INVISIBLE);
            
            Ph7ItemizedOverlay overlay = new Ph7ItemizedOverlay(drawable, this);
            overlay.setAccuracy(accuracy);
            GeoPoint point = new GeoPoint(latitudeE6, longitudeE6);
            OverlayItem item = new OverlayItem(point, "hello", "World");
            overlay.addOverlay(item);
            
            mapOverlays.add(overlay);
            
            mapView.getController().animateTo(point);
            overlay.zoom(mapView, accuracy);
        }
        else {
        	issues = Util.getIssueList(this);
        	points = new ArrayList<GeoPoint>();
        	for (Issue issue : issues) {
        		int latitudeE6 = (int)(issue.latitude * 1e6);
                int longitudeE6 = (int)(issue.longitude * 1e6);
                Ph7ItemizedOverlay overlay = new Ph7ItemizedOverlay(drawable, this);
                GeoPoint point = new GeoPoint(latitudeE6, longitudeE6);
                OverlayItem item = new OverlayItem(point, issue.type, issue.location);
                overlay.addOverlay(item);
                mapOverlays.add(overlay);
                points.add(point);
			}
        	if (points.size() > 0) {
        		mapView.getController().animateTo(points.get(0));
        	}
        }
        
        ((Button)findViewById(R.id.ButtonPrevious)).setOnClickListener(new OnClickListener() {			
			public void onClick(View view) {
				// TODO Auto-generated method stub
				
			}
		});
        
        ((Button)findViewById(R.id.ButtonNext)).setOnClickListener(new OnClickListener() {			
			public void onClick(View view) {
				// TODO Auto-generated method stub
				currentIndex = (currentIndex + 1) % points.size();
				if (points.size() > 0) {
					
					mapView.getController().animateTo(points.get(currentIndex));
				}
			}
		});
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}
