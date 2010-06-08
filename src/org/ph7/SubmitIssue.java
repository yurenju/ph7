package org.ph7;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;

public class SubmitIssue extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submitissue);
		ImageView image = (ImageView)findViewById(R.id.ImagePreview);
		String filename = getIntent().getExtras().getString("picture-path");
		Bitmap bm = BitmapFactory.decodeFile(filename);
		Matrix matrix = new Matrix ();
		float ratio = getRatio(bm);
		matrix.postScale(ratio, ratio);
		matrix.postRotate(90);
		Bitmap rbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		image.setImageBitmap(rbm);
	}
	private float getRatio (Bitmap bm) {
		Display d = getWindowManager().getDefaultDisplay();
		int targetHeight = d.getHeight()/4;
		return ((float)targetHeight)/bm.getHeight();		
	}
}
