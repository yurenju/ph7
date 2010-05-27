package org.ph7;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

public class NewReportActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newreport);
        
        final Button button = (Button)findViewById(R.id.ButtonShot);
        button.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
				startActivity(intent);
				
			}
		});
    }
}