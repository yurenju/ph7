package org.ph7;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class Ph7Overlay extends Overlay {
	private Drawable issueMarker;
	private ArrayList<Issue> issues;
	private int pointWidth;
	private int pointHeight;
	private int currentIndex = 0;
	
	public Ph7Overlay(Context context) {
		issueMarker = context.getResources().getDrawable(R.drawable.ic_maps_indicator_current_position);
		pointHeight = issueMarker.getIntrinsicHeight();
		pointWidth = issueMarker.getIntrinsicWidth();
		issueMarker.setBounds(0, 0, pointWidth, pointHeight);
		issues = new ArrayList<Issue>();
	}
	
	public void add (Issue issue) {
		issues.add(issue);
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
		if (issues.size() > 0) {
			drawIssues(canvas, mapView);
			drawCurrentIssue (canvas, mapView);
		}
	}
	
	private void drawCurrentIssue(Canvas canvas, MapView mapView) {
		Issue issue = issues.get(currentIndex);
		GeoPoint geoPoint = Util.getGeoPoint(issue);
		Point point = mapView.getProjection().toPixels(geoPoint, null);
		float accuracypx = mapView.getProjection().metersToEquatorPixels((float)issue.accuracy);
		Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setARGB(60, 114, 159, 207);
		Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setARGB(100, 52, 101, 164);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(2);

		canvas.drawCircle(point.x, point.y, accuracypx, circlePaint);
		canvas.drawCircle(point.x, point.y, accuracypx, linePaint);
	}

	public void drawIssues (Canvas canvas, MapView mapView) {
		for (Issue issue : issues) {
			GeoPoint geoPoint = Util.getGeoPoint(issue);
			Point point = mapView.getProjection().toPixels(geoPoint, null);
			canvas.save();
			//TODO i don't know what's that.
			canvas.translate(point.x - (pointWidth / 2) + 3, point.y- pointHeight);
			issueMarker.draw(canvas);
			canvas.restore();
		}
	}
	
	public void zoom(MapView mapView, GeoPoint geoPoint) {
		Point point = mapView.getProjection().toPixels(geoPoint, null);
		Issue issue = issues.get(currentIndex);

		if (point == null)
			return;
		Projection projection = mapView.getProjection();
		float accuracypx = projection.metersToEquatorPixels((float)issue.accuracy);
		int minX = (int) (point.x - accuracypx);
		int maxX = (int) (point.x + accuracypx);
		int minY = (int) (point.y - accuracypx);
		int maxY = (int) (point.y + accuracypx);

		GeoPoint minGeoPoint = projection.fromPixels(minX, minY);
		GeoPoint maxGeoPoint = projection.fromPixels(maxX, maxY);
		int latSpanE6 = maxGeoPoint.getLatitudeE6()
				- minGeoPoint.getLatitudeE6();
		int lonSpanE6 = maxGeoPoint.getLongitudeE6()
				- minGeoPoint.getLongitudeE6();

		mapView.getController().zoomToSpan(latSpanE6, lonSpanE6);

	}

	public void animateToCurrent(MapView mapView) {
		if (!issues.isEmpty()) {
			GeoPoint geopoint = Util.getGeoPoint(issues.get(currentIndex));
			// TODO zoom doesn't work
//			zoom(mapView, geopoint);
			mapView.getController().animateTo(geopoint);
		}
	}

	public void nextIssue(MapView mapView) {
		currentIndex = (currentIndex + 1) % issues.size();
		animateToCurrent(mapView);
	}

	public void previousIssue(MapView mapView) {
		currentIndex = (currentIndex - 1) < 0 ? issues.size()-1 : (currentIndex-1);
		animateToCurrent(mapView);
	}
}
