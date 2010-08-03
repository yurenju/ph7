package org.ph7;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;

public class NewReportActivity extends Activity implements OnClickListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newreport);
        checkLocationProvider();
        
        final Button buttonShot = (Button)findViewById(R.id.ButtonShot);
        buttonShot.setOnClickListener(this);
        final Button buttonGps = (Button)findViewById(R.id.ButtonGps);
        buttonGps.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent, 0);
			}
		});
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		checkLocationProvider();
	}

	public void onClick(View arg0) {
		Intent intent = new Intent ();
		intent.setClass(this, Shot.class);
		startActivityForResult(intent, RESULT_OK);
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			TabHost view = (TabHost)findViewById(R.layout.main);
			view.setCurrentTab(1);			
		}
	}
	
	private void checkLocationProvider () {
		LocationManager locationManager =
			(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean gpsStatus = 
			locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean netStatus = 
			locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (gpsStatus)
			((ImageView)findViewById(R.id.ImageStatusGps))
						.setImageResource(R.drawable.yes);
		else
			((ImageView)findViewById(R.id.ImageStatusGps))
						.setImageResource(R.drawable.no);
		
		if (netStatus)
			((ImageView)findViewById(R.id.ImageStatusNetwork))
						.setImageResource(R.drawable.yes);
		else
			((ImageView)findViewById(R.id.ImageStatusNetwork))
						.setImageResource(R.drawable.no);
		
		((Button)findViewById(R.id.ButtonShot)).setEnabled(!(!gpsStatus && !netStatus));
	}
}