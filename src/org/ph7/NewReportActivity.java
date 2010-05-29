package org.ph7;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NewReportActivity extends Activity implements OnClickListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newreport);
        
        final Button buttonShot = (Button)findViewById(R.id.ButtonShot);
        buttonShot.setOnClickListener(this);
//			public void onClick(View arg0) {
//				Intent intent = new Intent ();
//				intent.setClass(this, Shot.class);
//				startActivity(intent);
//			}
//		});
        final Button buttonGps = (Button)findViewById(R.id.ButtonGps);
        buttonGps.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
//				LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//				Criteria criteria = new Criteria();
//				criteria.setAccuracy(Criteria.ACCURACY_FINE);
//				criteria.setAltitudeRequired(false);
//				criteria.setBearingRequired(false);
//				criteria.setCostAllowed(false);
//				criteria.setPowerRequirement(Criteria.POWER_HIGH);
//				String provider = manager.getBestProvider(criteria, true);
//				Location location = manager.getLastKnownLocation(provider);
//				ContentResolver cr = getContentResolver();
//				Settings.Secure.setLocationProviderEnabled(cr, LocationManager.GPS_PROVIDER, true);
				Intent intent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		});
    }

	public void onClick(View arg0) {
		Intent intent = new Intent ();
		intent.setClass(this, Shot.class);
		startActivity(intent);
	}
}