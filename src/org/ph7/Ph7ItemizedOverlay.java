package org.ph7;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class Ph7ItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private Context context;
	private float accuracy;

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public Ph7ItemizedOverlay(Drawable defaultMarker, Context content) {
		super(boundCenterBottom(defaultMarker));
		context = content;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
		overlays.add(overlay);
		populate();
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = overlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Point point = getPoint(mapView);

		if (point == null)
			return;

		Projection projection = mapView.getProjection();
		float accuracypx = projection.metersToEquatorPixels(accuracy);
		Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setARGB(60, 114, 159, 207);
		Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setARGB(100, 52, 101, 164);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(2);
		drawCircle(canvas, circlePaint, point, accuracypx);
		drawCircle(canvas, linePaint, point, accuracypx);

		super.draw(canvas, mapView, shadow);
	}

	private Point getPoint(MapView mapView) {
		OverlayItem item = overlays.size() == 0 ? null : overlays.get(0);
		if (item != null) {
			Projection projection = mapView.getProjection();
			GeoPoint geoPoint = item.getPoint();
			Point point = projection.toPixels(geoPoint, null);
			return point;
		}
		return null;
	}

	public void zoom(MapView mapView, float accuracy) {
		Point point = getPoint(mapView);

		if (point == null)
			return;
		Projection projection = mapView.getProjection();
		float accuracypx = projection.metersToEquatorPixels(accuracy);
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

	// FIXME GPL code!!
	private void drawCircle(Canvas canvas, Paint paint, Point center,
			float radius) {
		RectF circleRect = new RectF(center.x - radius, center.y - radius,
				center.x + radius, center.y + radius);
		canvas.drawOval(circleRect, paint);
	}
	// FIXME GPL code!!!
}
