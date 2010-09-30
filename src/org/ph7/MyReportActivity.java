package org.ph7;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyReportActivity extends ListActivity implements OnScrollListener {
	IssueAdapter issueAdapter;
	boolean busy = false;
	private final static int THUMBNAIL_HEIGHT = 80;
	private int displayWidth = 0;
	private Bitmap[] thumbnails;
	private Executor executor = Executors.newFixedThreadPool(1);
	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Display d = getWindowManager().getDefaultDisplay();
		displayWidth = d.getWidth();
		issueAdapter = new IssueAdapter(this);
		thumbnails = new Bitmap[issueAdapter.getCount()];
		
        setContentView(R.layout.listissues);
        setListAdapter(issueAdapter);
        getListView().setOnScrollListener(this);
        
        Button btnMap = (Button)findViewById(R.id.ButtonOpenMap);
        btnMap.setOnClickListener(new OnClickListener() {			
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(MyReportActivity.this, Map.class);
				intent.putExtra("single", false);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (!issueAdapter.isDataSynced()) {
			issueAdapter.initialIssueList();
			thumbnails = new Bitmap[issueAdapter.getCount()];
			issueAdapter.notifyDataSetChanged();
		}
	}
	
	protected void onListItemClick (ListView listview, View view, int position, long id) {
		int localId = issueAdapter.getIssueId(position);
		Intent intent = new Intent ();
		intent.setClass(this, IssueDetails.class);
		intent.putExtra("id", localId);
		startActivity(intent);
	}
	
	private void setImage (ViewHolder holder, Bitmap bitmap) {
		int textWidth = displayWidth - bitmap.getWidth() - 20; // 20 is margin
		holder.locationText.setWidth(textWidth);
		holder.typeText.setWidth(textWidth);
		holder.thumbnail.setImageBitmap(bitmap);
	}
	
	Handler handler = new Handler() {
		public void handleMessage (Message msg) {
			int index = msg.getData().getInt("index");
			String imagePath = msg.getData().getString("image-path");
			View view = null;
			for (int i = 0; i < getListView().getChildCount(); i++) {
				view = getListView().getChildAt(i);
				ViewHolder holder = (ViewHolder)view.getTag();
				if (holder.issue.imagePath.compareTo(imagePath) == 0) {
					break;
				}
				view = null;
			}
			
			if (view == null) return;

			ViewHolder holder = (ViewHolder)view.getTag();
			setImage(holder, thumbnails[index]);
		}
	};
	
	private class LoadBitmapTask implements Runnable {
		String imagePath = null;
		int index = 0;
		
		public LoadBitmapTask (int index, String imagePath) {
			this.imagePath = imagePath;
			this.index = index;
		}
		
		public void run() {
			Bitmap bm = null;
			try {
				bm = Util.getBitmap(imagePath, THUMBNAIL_HEIGHT);
			} catch (Exception e) {
				Log.e("ph7", e.getMessage());
			}
			thumbnails[index] = bm;
			sendMessage(index, imagePath);
		}
	}
	
	private void sendMessage (int index, String imagePath) {
		Message msg = Message.obtain();
		Bundle data = new Bundle();
		data.putInt("index", index);
		data.putString("image-path", imagePath);
		msg.setData(data);
		handler.sendMessage(msg);
	}
	
	private void settingHolder (ViewHolder holder, Issue issue) {
		int index = issueAdapter.getIssueList().indexOf(issue);
		holder.locationText.setText(issue.location);
		holder.typeText.setText(issue.type);
		holder.loading = false;	
		if (thumbnails[index] == null) 
			executor.execute(new LoadBitmapTask(index, issue.imagePath));
		else {
			setImage(holder, thumbnails[index]);
		}
		
	}
	
	public class IssueAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private SQLiteDatabase db;
		private ArrayList<Issue> issueList = new ArrayList<Issue>();
		
		public boolean isDataSynced () {
			Cursor c = db.rawQuery("SELECT location, issue_type, image_filename, local_id FROM " + 
					DbOpenHelper.REPORTS_TABLE_NAME, null);
			int count = c.getCount();
			c.close();
			return (count == issueList.size());
		}
		
		public int getIssueId (int position) {
			return issueList.get(position).id;
		}
		
		public void initialIssueList() {
			issueList = Util.getIssueList(MyReportActivity.this);
		}
		
		public IssueAdapter (Context context) {
			DbOpenHelper helper = new DbOpenHelper(context);
			db = helper.getReadableDatabase();
			inflater = LayoutInflater.from(context);
			
			initialIssueList();			
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
			holder.issue = issue;
			
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
		Issue issue;
		boolean loading = false;
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
