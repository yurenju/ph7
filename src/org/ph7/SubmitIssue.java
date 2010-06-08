package org.ph7;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

public class SubmitIssue extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		double latitude = getIntent().getExtras().getDouble("latitude");
		double longitude = getIntent().getExtras().getDouble("longitude");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submitissue);
		ImageView image = (ImageView)findViewById(R.id.ImagePreview);
		String filename = getIntent().getExtras().getString("picture-path");
		Bitmap bm = BitmapFactory.decodeFile(filename);
		Matrix matrix = new Matrix ();
		float ratio = getRatio(bm);
		matrix.postScale(ratio, ratio);
		matrix.postRotate(90);
		Bitmap rbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		image.setImageBitmap(rbm);
		Geocoder geocoder = new Geocoder(this);
		try {
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 5);
			if (!addresses.isEmpty()) {
				((TextView)findViewById(R.id.TextLocation)).setText(addresses.get(0).getAddressLine(0));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private float getRatio (Bitmap bm) {
		Display d = getWindowManager().getDefaultDisplay();
		int targetHeight = d.getHeight()/4;
		return ((float)targetHeight)/bm.getHeight();		
	}
}
