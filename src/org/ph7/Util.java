package org.ph7;

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

}
