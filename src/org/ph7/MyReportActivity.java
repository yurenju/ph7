package org.ph7;

import android.app.ListActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyReportActivity extends ListActivity {
	IssueAdapter issueAdapter;
	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.listissues);
	}
	
	
	public class IssueAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private SQLiteDatabase db;
		
		public IssueAdapter (Context context) {
			DbOpenHelper helper = new DbOpenHelper(getApplicationContext());
			db = helper.getReadableDatabase();
			inflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			SQLiteStatement stat = db.compileStatement("SELECT COUNT(*) FROM " +
									DbOpenHelper.REPORTS_TABLE_NAME);
			long count = stat.simpleQueryForLong();
			return (int) count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
		}
		
		class ViewHolder {
			TextView locationText;
		}
	}
}
