package com.cloverstudio.spikademo.management;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapManagement {

	public static Bitmap BitmapFromFile(File f){
		Bitmap mBitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = calculateInSampleSize(options, 640, 640);
		options.inJustDecodeBounds = false;
		mBitmap = BitmapFactory.decodeFile(f.getPath(), options);
		mBitmap = Bitmap.createScaledBitmap(mBitmap, 640, 640, true);
		
		return mBitmap;
	}
	
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width. 
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
}
}
