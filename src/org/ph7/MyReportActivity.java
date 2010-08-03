package org.ph7;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyReportActivity extends ListActivity {
	IssueAdapter issueAdapter;
	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		issueAdapter = new IssueAdapter(this);
        setContentView(R.layout.listissues);
        setListAdapter(issueAdapter);
	}
	
	
	public class IssueAdapter extends BaseAdapter {
		private final static int THUMBNAIL_HEIGHT = 80;
		private LayoutInflater inflater;
		private SQLiteDatabase db;
		private ArrayList<Issue> issueList = new ArrayList<Issue>();
		private int displayWidth = 0; 
		
		public IssueAdapter (Context context) {
			Display d = getWindowManager().getDefaultDisplay();
			displayWidth = d.getWidth();
			DbOpenHelper helper = new DbOpenHelper(getApplicationContext());
			db = helper.getReadableDatabase();
			inflater = LayoutInflater.from(context);
			
			Cursor c = db.rawQuery("SELECT location, issue_type, image_filename FROM " + 
									DbOpenHelper.REPORTS_TABLE_NAME, null);
			if (c == null)
				return;
			
			try {
				while (c.moveToNext()) {
					
					String[] items = getResources().getStringArray(R.array.issue_items);
					
					Issue issue = new Issue();
					int index = c.getInt(1);
					issue.location = c.getString(0);
					issue.type = items[index];
					issue.imagePath = c.getString(2);
					issueList.add(issue);
				}
			} finally {
				c.close();
			}
			
		}
		
		public int getCount() {
			return (int) issueList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			Issue issue = issueList.get(position);
			
			if (convertView == null) {
				convertView = inflater.inflate (R.layout.list_item_thumbnail_text, null);
				holder = new ViewHolder();
				holder.locationText = (TextView)convertView.findViewById(R.id.LocationText);
				holder.typeText = (TextView)convertView.findViewById(R.id.IssueType);
				holder.thumbnail = (ImageView)convertView.findViewById(R.id.Thumbnail);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder)convertView.getTag();
			}
			Bitmap bm = Util.getBitmap(issue.imagePath, THUMBNAIL_HEIGHT);
			int textWidth = displayWidth - bm.getWidth() - 20; // 20 is margin
			holder.locationText.setText(issue.location);
			holder.typeText.setText(issue.type);
			holder.locationText.setWidth(textWidth);
			holder.typeText.setWidth(textWidth);
			holder.thumbnail.setImageBitmap(bm);
			
			return convertView;
		}
		
		class ViewHolder {
			TextView locationText;
			TextView typeText;
			ImageView thumbnail;
		}
		
		class Issue {
			String location;
			String type;
			String imagePath;
		}
	}
}
