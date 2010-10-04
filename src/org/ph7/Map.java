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
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity {
	ArrayList<Issue> issues = null;
	ArrayList<GeoPoint> points = null;
	int currentIndex = 0;
	Ph7ItemizedOverlay overlay;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
        final MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        boolean single = (boolean)(getIntent().getExtras().getBoolean("single"));
        
        Drawable marker = getResources().getDrawable(R.drawable.marker);
        overlay = new Ph7ItemizedOverlay(marker, mapView);
        if (single) {
        	issues = new ArrayList<Issue>();
        	int issueId = getIntent().getExtras().getInt("issue-id");
        	Issue issue = Util.getIssue(this, issueId);
        	GeoPoint geoPoint = Util.getGeoPoint(issue);
        	OverlayItem item = new OverlayItem(geoPoint, issue.type, issue.location);
        	overlay.addOverlay(item);      
        	issues.add(issue);
        	((Button)findViewById(R.id.ButtonNext)).setVisibility(View.INVISIBLE);
        	((Button)findViewById(R.id.ButtonPrevious)).setVisibility(View.INVISIBLE);
        }
        else {
        	issues = Util.getIssueList(this);
        	for (Issue issue : issues) {
        		GeoPoint point = Util.getGeoPoint(issue);
        		OverlayItem item = new OverlayItem(point, issue.type, issue.location);
				overlay.addOverlay(item);
			}
        }
        
//        final Ph7Overlay overlay = new Ph7Overlay(this);
//        if (single) {
//        	int issueId = getIntent().getExtras().getInt("issue-id");
//        	Issue issue = Util.getIssue (this, issueId);
//        	overlay.add(issue);
//        }
//        else {
//        	issues = Util.getIssueList(this);
//            for (Issue issue : issues) {
//    			overlay.add(issue);
//    		}	
//        }
//        
//        
        
//        overlay.animateToCurrent (mapView);
//        
//        
        ((Button)findViewById(R.id.ButtonPrevious)).setOnClickListener(new OnClickListener() {			
			public void onClick(View view) {
				currentIndex = (currentIndex-1) < 0 ? issues.size()-1 : currentIndex-1;
				animateToCurrent();
			}
		});
        
        ((Button)findViewById(R.id.ButtonNext)).setOnClickListener(new OnClickListener() {			
			public void onClick(View view) {
				currentIndex = (currentIndex + 1) >= issues.size() ? 0 : currentIndex +1;
				animateToCurrent();
			}
		});
        
        overlay.setIssues(issues);
        mapOverlays.add(overlay);
        if (issues.size() > 0) {
        	animateToCurrent();
        }
    }
    
    private void animateToCurrent () {
    	final MapView mapView = (MapView) findViewById(R.id.mapview);
    	
    	GeoPoint point = Util.getGeoPoint(issues.get(currentIndex));
    	mapView.getController().animateTo(point);
    	mapView.getController().setZoom(16);
    	overlay.showBalloon(currentIndex);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}
