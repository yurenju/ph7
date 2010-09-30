package org.ph7;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class Util {
	public static Bitmap getBitmap (String path, int height) {
		Bitmap bm = BitmapFactory.decodeFile(path);
		Matrix matrix = new Matrix ();
		float ratio = ((float)height)/bm.getHeight();
		matrix.postScale(ratio, ratio);
		matrix.postRotate(90);
		Bitmap rbm = Bitmap.createBitmap(bm, 0, 0,
				bm.getWidth(), bm.getHeight(), matrix, true);
		return rbm;
	}
	
	public static ArrayList<Issue> getIssueList(Context context) {
		ArrayList<Issue> issues =  new ArrayList<Issue>();
		DbOpenHelper helper = new DbOpenHelper(context);
		SQLiteDatabase db =helper.getReadableDatabase(); 
		Cursor c = db.rawQuery(
				"SELECT location, issue_type, image_filename, local_id, longitude, latitude FROM "
						+ DbOpenHelper.REPORTS_TABLE_NAME, null);
		if (c == null)
			return null;

		try {
			while (c.moveToNext()) {
				String[] items = context.getResources().getStringArray(
						R.array.issue_items);

				Issue issue = new Issue();
				int index = c.getInt(1);
				issue.location = c.getString(0);
				issue.type = items[index];
				issue.imagePath = c.getString(2);
				issue.id = c.getInt(3);
				issue.longitude = c.getDouble(4);
				issue.latitude = c.getDouble(5);
				issues.add(issue);
			}
		} finally {
			c.close();
		}
		return issues;
	}

}
