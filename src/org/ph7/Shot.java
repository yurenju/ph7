package org.ph7;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

public class Shot extends Activity implements Callback, AutoFocusCallback,
		OnClickListener, Camera.PictureCallback {
	private static final String TAG = "Shot";
	SurfaceHolder mHolder;
	SurfaceView surfaceView;
	Camera mCamera;
	LocationManager mLocationManager;
	Builder builder;

	LocationListener[] mLocationListeners = new LocationListener[] {
			new LocationListener(LocationManager.GPS_PROVIDER),
			new LocationListener(LocationManager.NETWORK_PROVIDER) };

	private void startReceivingLocationUpdates() {
		Log.d(TAG, "startReceivingLocationUpdates");
		if (mLocationManager != null) {
			try {
				mLocationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 1000, 0F,
						mLocationListeners[1]);
			} catch (java.lang.SecurityException ex) {
				Log.i(TAG, "fail to request location update, ignore", ex);
			} catch (IllegalArgumentException ex) {
				Log.d(TAG, "provider does not exist " + ex.getMessage());
			}
			try {
				mLocationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 1000, 0F,
						mLocationListeners[0]);
			} catch (java.lang.SecurityException ex) {
				Log.i(TAG, "fail to request location update, ignore", ex);
			} catch (IllegalArgumentException ex) {
				Log.d(TAG, "provider does not exist " + ex.getMessage());
			}
		}
	}

	private void stopReceivingLocationUpdates() {
		Log.d(TAG, "stopReceivingLocationUpdates");
		if (mLocationManager != null) {
			for (int i = 0; i < mLocationListeners.length; i++) {
				try {
					mLocationManager.removeUpdates(mLocationListeners[i]);
				} catch (Exception ex) {
					Log.i(TAG, "fail to remove location listners, ignore", ex);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Create our Preview view and set it as the content of our activity.

		setContentView(R.layout.shot);
		surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		Button button = (Button) findViewById(R.id.ButtonShutter);
		button.setOnClickListener(this);

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		startReceivingLocationUpdates();
		
		builder = new AlertDialog.Builder(this);
	}

	@Override
	protected void onPause() {
		stopReceivingLocationUpdates();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Camera.Parameters parameters = mCamera.getParameters();
		mCamera.stopPreview();

		List<Size> sizes = parameters.getSupportedPreviewSizes();
		Size optimalSize = getOptimalPreviewSize(sizes, h, w);
		parameters.setPreviewSize(optimalSize.width, optimalSize.height);

		mCamera.setParameters(parameters);
		mCamera.setDisplayOrientation(90);
		mCamera.startPreview();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	private class LocationListener implements android.location.LocationListener {
		Location mLastLocation;
		boolean mValid = false;
		String mProvider;

		public LocationListener(String provider) {
			mProvider = provider;
			mLastLocation = new Location(mProvider);
		}

		public void onLocationChanged(Location newLocation) {
//			Log.d(TAG, "onLocationChanged");
//			Log.d(TAG, newLocation.getLatitude() + ", " + newLocation.getLongitude());
//			builder.setMessage(newLocation.getLatitude() + ", " + newLocation.getLongitude());
//			AlertDialog alert = builder.create();
//			alert.show();
			showLocation(newLocation);
			
			if (newLocation.getLatitude() == 0.0
					&& newLocation.getLongitude() == 0.0) {
				// Hack to filter out 0.0,0.0 locations
				return;
			}

			mLastLocation.set(newLocation);
			mValid = true;
			
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
			mValid = false;
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE: {
				Log.d (TAG, "OUT_OF_SERVICE");
			}
			case LocationProvider.TEMPORARILY_UNAVAILABLE: {
				Log.d (TAG, "TEMPORARILY_UNAVAILABLE");
				mValid = false;
				break;
			}
			case LocationProvider.AVAILABLE: {
				Log.d (TAG, "AVAILABLE");
			}
			}
		}

		public Location current() {
			return mValid ? mLastLocation : null;
		}
	}

	public void onAutoFocus(boolean arg0, Camera camera) {
		Log.i(TAG, "onAutoFocus");
		camera.takePicture(null, null, this);
	}

	public void onClick(View arg0) {
		mCamera.autoFocus(this);

	}
	
	public void showLocation (Location location) {
		if (location != null) {
			Log.d(TAG, String.format("Location: %f, %f", location.getLatitude(), location.getLongitude()));
			((TextView)findViewById(R.id.TextLocation))
				.setText(String.format("(%f, %f)", location.getLatitude(), location.getLongitude()));
		}
		else {
			Log.d(TAG, "location is null");
		}
	}

	public File getStorageDir () {
		File dir = new File(Environment.getExternalStorageDirectory(), "ph7");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
	
	public void onPictureTaken(byte[] data, Camera arg1) {
		FileOutputStream outStream = null;
		TiffOutputSet outset = new TiffOutputSet();
		Location loc = getCurrentLocation();
		File dir = getStorageDir();
		String filename = String.format("%s/%d.jpg",
				dir.toString(), System.currentTimeMillis());
		
		try {
			outStream = new FileOutputStream(filename);
			Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			
			if (loc != null) {
				outStream = new FileOutputStream(filename);
				outset.setGPSInDegrees(loc.getLongitude(), loc.getLatitude());
				new ExifRewriter().updateExifMetadataLossless (data, outStream, outset);
			}
			else {
				outStream.write(data);	
			}
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImageWriteException e) {
			e.printStackTrace();
		} catch (ImageReadException e) {
			e.printStackTrace();
		} finally {
		}
		Log.d(TAG, "onPictureTaken - jpeg");
		showLocation (getCurrentLocation());
		setResult(RESULT_OK,
					new Intent().putExtra("path", filename));
		finish();
	}

	private Location getCurrentLocation() {
		// go in best to worst order
		for (int i = 0; i < mLocationListeners.length; i++) {
			Location l = mLocationListeners[i].current();
			if (l != null)
				return l;
		}
		return null;
	}
}
