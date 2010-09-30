package org.ph7;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class IssueDetails extends Activity {
	private SQLiteDatabase db;
	private float latitude;
	private float longitude;
	private float accuracy;
	private String comment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issue_details);
		DbOpenHelper dbHelper = new DbOpenHelper(this.getApplicationContext());
		db = dbHelper.getReadableDatabase();
		int id = getIntent().getExtras().getInt("id");

		String[] columns = { "location", "issue_type", "image_filename",
				"local_id", "created_time", "latitude", "longitude", "accuracy", "comment"};
		String[] args = { String.valueOf(id) };
		Cursor c = null;
		try {
			c = db.query(DbOpenHelper.REPORTS_TABLE_NAME, columns,
					"local_id = ?", args, null, null, null);
		} catch (Exception e) {
			Log.d("TEST", e.getMessage());
		}

		if (c == null)
			return;

		try {
			c.moveToNext();

			String[] items = getResources().getStringArray(R.array.issue_items);

			int index = c.getInt(1);
			long ldate = c.getLong(4);
			comment = c.getString(8) == null ? "" : c.getString(8);
			Date date = new Date(ldate);
			SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String dateStr = df.format(date);
			((TextView) findViewById(R.id.TextLocation))
					.setText(c.getString(0));
			((TextView) findViewById(R.id.TextIssueType)).setText(items[index]);
			((TextView) findViewById(R.id.TextCreatedTime))
					.setText(dateStr);
			((TextView) findViewById(R.id.TextAdditionalInfo)).setText(comment);
			
			latitude = c.getFloat(5);
			longitude = c.getFloat(6);
			accuracy = c.getFloat(7);
			

			Display d = getWindowManager().getDefaultDisplay();
			int targetHeight = d.getHeight() / 4;
			ImageView image = (ImageView) findViewById(R.id.ImagePreviewDetail);
			Bitmap bm = Util.getBitmap(c.getString(2), targetHeight);
			image.setImageBitmap(bm);
		} catch (Exception e) {
			Log.d("TEST", e.getMessage());

		} finally {
			c.close();
		}
		
		Button btn = ((Button)findViewById(R.id.ButtonOpenOnMap));
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(IssueDetails.this, Map.class);
				intent.putExtra("single", true);
				intent.putExtra("latitude", latitude);
				intent.putExtra("longitude", longitude);
				intent.putExtra("accuracy", accuracy);
				startActivity(intent);
			}
		});
	}
}
