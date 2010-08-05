package org.ph7;

import android.app.Activity;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

public class IssueDetails extends Activity {
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issue_details);
		DbOpenHelper dbHelper = new DbOpenHelper(this.getApplicationContext());
		db = dbHelper.getReadableDatabase();
		int id = getIntent().getExtras().getInt("id");

		String[] columns = { "location", "issue_type", "image_filename",
				"local_id", "created_time" };
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
			((TextView) findViewById(R.id.TextLocation))
					.setText(c.getString(0));
			((TextView) findViewById(R.id.TextIssueType)).setText(items[index]);
			((TextView) findViewById(R.id.TextCreatedTime))
					.setText(String.valueOf(c.getInt(4)));

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

	}
}
