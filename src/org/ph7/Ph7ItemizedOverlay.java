package org.ph7;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public class Ph7ItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private Context context;
	private ArrayList<Issue> issues;

	public ArrayList<Issue> getIssues() {
		return issues;
	}

	public void setIssues(ArrayList<Issue> issues) {
		this.issues = issues;
	}

	public Ph7ItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		context = mapView.getContext();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	public void addOverlay (OverlayItem overlay) {
		overlays.add(overlay);
		populate();
	}
	
	public void showBalloon (int index) {
		super.onTap(index);
	}

	@Override
	protected boolean onBalloonTap(int index) {
		Intent intent = new Intent ();
		intent.setClass(context, IssueDetails.class);
		intent.putExtra("id", issues.get(index).id);
		context.startActivity(intent);
		return true;
	}
}
