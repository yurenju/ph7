package org.ph7;

import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SubmitIssue extends Activity {
	private static final int SELECT_ISSUE_TYPE = 0;
	private DbOpenHelper dbHelper;
	private String locationText;
	private double latitude;
	private double longitude;
	private double accuracy;
	private int issueType;
	private String imageFilename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		dbHelper = new DbOpenHelper(this.getApplicationContext());
		
		latitude = getIntent().getExtras().getDouble("latitude");
		longitude = getIntent().getExtras().getDouble("longitude");
		accuracy = getIntent().getExtras().getDouble("accuracy");
		imageFilename = getIntent().getExtras().getString("picture-path");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submitissue);
		
		Display d = getWindowManager().getDefaultDisplay();
		int targetHeight = d.getHeight()/4;
		
		ImageView image = (ImageView)findViewById(R.id.ImagePreview);
		Bitmap bm = Util.getBitmap(imageFilename, targetHeight);
		image.setImageBitmap(bm);
		Geocoder geocoder = new Geocoder(this);
		try {
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 5);
			if (!addresses.isEmpty()) {
				locationText = addresses.get(0).getAddressLine(0);
				((TextView)findViewById(R.id.TextLocation)).setText(locationText);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Button selectButton = (Button)findViewById(R.id.ButtonIssueType);
		selectButton.setOnClickListener(new OnClickListener() {			
			public void onClick(View arg0) {
				showDialog(SELECT_ISSUE_TYPE);
			}
		});
		
		Button submitBtn = (Button)findViewById(R.id.ButtonSubmit);
		submitBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				String comment = ((TextView)findViewById(R.id.TextAdditionalInfo))
									.getText().toString();
				Long  now = Long.valueOf(System.currentTimeMillis());
				
				ContentValues values = new ContentValues();
				values.put("location", locationText);
				values.put("longitude", longitude);
				values.put("latitude", latitude);
				values.put("comment", comment);
				values.put("accuracy", accuracy);
				values.put("issue_type", issueType);
				values.put("created_time", now);
				values.put("image_filename", imageFilename);
				
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.insert(DbOpenHelper.REPORTS_TABLE_NAME,
						  DbOpenHelper.REPORT, values);
				setResult(RESULT_OK);
				finish();
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
						issueType = which;
					}
				}).create();

		default:
			break;
		}
		return null;
	}
}
