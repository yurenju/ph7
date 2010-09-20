package org.ph7;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyReportActivity extends ListActivity implements OnScrollListener {
	IssueAdapter issueAdapter;
	boolean busy = false;
	private final static int THUMBNAIL_HEIGHT = 80;
	private int displayWidth = 0;
	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Display d = getWindowManager().getDefaultDisplay();
		displayWidth = d.getWidth();
		issueAdapter = new IssueAdapter(this);
        setContentView(R.layout.listissues);
        setListAdapter(issueAdapter);
        getListView().setOnScrollListener(this);
	}
	
	protected void onListItemClick (ListView listview, View view, int position, long id) {
		int localId = issueAdapter.getIssueId(position);
		Intent intent = new Intent ();
		intent.setClass(this, IssueDetails.class);
		intent.putExtra("id", localId);
		startActivity(intent);
	}
	
	private void settingHolder (ViewHolder holder, Issue issue) {
		Bitmap bm = Util.getBitmap(issue.imagePath, THUMBNAIL_HEIGHT);
		int textWidth = displayWidth - bm.getWidth() - 20; // 20 is margin
		holder.locationText.setText(issue.location);
		holder.typeText.setText(issue.type);
		holder.locationText.setWidth(textWidth);
		holder.typeText.setWidth(textWidth);
		holder.thumbnail.setImageBitmap(bm);
		holder.loading = false;
	}
	
	public class IssueAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private SQLiteDatabase db;
		private ArrayList<Issue> issueList = new ArrayList<Issue>();
		
		
		public int getIssueId (int position) {
			return issueList.get(position).id;
		}
		
		public IssueAdapter (Context context) {
			DbOpenHelper helper = new DbOpenHelper(getApplicationContext());
			db = helper.getReadableDatabase();
			inflater = LayoutInflater.from(context);
			
			Cursor c = db.rawQuery("SELECT location, issue_type, image_filename, local_id FROM " + 
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
					issue.id = c.getInt(3);
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
			
			if (!busy) {
				settingHolder(holder, issue);
			} else {
				holder.typeText.setText(R.string.loading);
				holder.locationText.setText(null);
				holder.thumbnail.setImageResource(R.drawable.loading);
				holder.loading = true;
			}
			
			return convertView;
		}

		public ArrayList<Issue> getIssueList() {
			return issueList;
		}
	}
	
	class ViewHolder {
		TextView locationText;
		TextView typeText;
		ImageView thumbnail;
		boolean loading = false;
	}
	
	class Issue {
		String location;
		String type;
		String imagePath;
		int id;
	}


	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			busy = false;
			int first = view.getFirstVisiblePosition();
			int count = view.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = view.getChildAt(i);
				ViewHolder holder = (ViewHolder)child.getTag();
				if (holder.loading) {
					Issue issue = issueAdapter.getIssueList().get(first+i);
					settingHolder(holder, issue);
				}
			}
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
		case OnScrollListener.SCROLL_STATE_FLING:
			busy = true;
			break;

		default:
			break;
		}
	}
}
