package org.ph7;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class main extends TabActivity { 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        TabAttribute[] attrs = {
        	new TabAttribute("new-report", "New report", NewReportActivity.class),
        	new TabAttribute("my-report", "My report", MyReportActivity.class)
        };
        
        for (TabAttribute tabAttribute : attrs) {
            intent = new Intent().setClass(this, tabAttribute.activityClass);
            spec = tabHost.newTabSpec(tabAttribute.tagName)
            			  .setIndicator(tabAttribute.indicator).setContent(intent);
            tabHost.addTab(spec);
		}
        
        tabHost.setCurrentTab(0);
    }
}

class TabAttribute {
	public String tagName;
	public String indicator;
	public Class<?> activityClass;
	
	public TabAttribute(String tagName, String indicator, Class<?> activityClass) {
		super();
		this.tagName = tagName;
		this.indicator = indicator;
		this.activityClass = activityClass;
	}	
}