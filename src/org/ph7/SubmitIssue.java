package org.ph7;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SubmitIssue extends Activity {
	private static final int SELECT_ISSUE_TYPE = 0;

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
		
		Button selectButton = (Button)findViewById(R.id.ButtonIssueType);
		selectButton.setOnClickListener(new OnClickListener() {			
			public void onClick(View arg0) {
				showDialog(SELECT_ISSUE_TYPE);
			}
		});
	}
	
	@Override
	protected Dialog onCreateDialog (int id) {
		switch (id) {
		case SELECT_ISSUE_TYPE:
			return new AlertDialog.Builder(SubmitIssue.this)
				.setTitle(R.string.select_issue_type)
				.setItems(R.array.issue_items, new DialogInterface.OnClickListener() {					
					public void onClick(DialogInterface dialog, int which) {
						String[] items = getResources().getStringArray(R.array.issue_items);
						Button btn = (Button) findViewById(R.id.ButtonIssueType);
						btn.setText(items[which]);
					}
				}).create();

		default:
			break;
		}
		return null;
	}
	
	private float getRatio (Bitmap bm) {
		Display d = getWindowManager().getDefaultDisplay();
		int targetHeight = d.getHeight()/4;
		return ((float)targetHeight)/bm.getHeight();		
	}
}
