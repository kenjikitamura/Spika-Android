/*
 * The MIT License (MIT)
 * 
 * Copyright © 2013 Clover Studio Ltd. All rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cloverstudio.spikademo.lazy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.ConnectionHandler;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Logger;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * ImageLoader
 * 
 * Loads images from web, compresses and saves them to both memory cache and file directory.
 */

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Handler handler = new Handler();// handler to display images in UI thread
	public static ImageLoader sInstance = null;
	public static final String SMALL = "_small";
	public static final String LARGE = "_large";
	private final int SIZE_SMALL = 100;
	private final int SIZE_LARGE = 400;

	public static void initImageLoaderInstance(Context context) {
		if (ImageLoader.sInstance == null) {
			ImageLoader.sInstance = new ImageLoader(context);
		}
	}

	public static ImageLoader getImageLoader() {
		return sInstance;
	}

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	final int stub_id = com.cloverstudio.spikademo.R.drawable.image_stub;

	public void DisplayImage(String fileId, ImageView imageView, String size, boolean downloadFromUrl) {
		imageViews.put(imageView, fileId + size);
		Bitmap bitmap = memoryCache.get(fileId + size);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		}
		else {
			queuePhoto(fileId, size, imageView, downloadFromUrl);
//			imageView.setImageResource(stub_id);
		}
	}

	public void DisplayImage(String fileId, ImageView imageView,
			ProgressBar pbLoading, String size, boolean downloadFromUrl) {
		imageViews.put(imageView, fileId + size);
		Bitmap bitmap = memoryCache.get(fileId + size);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			pbLoading.setVisibility(View.GONE);
		} else {
			queuePhotoWithProgress(fileId, size, imageView, pbLoading, downloadFromUrl);
		}
	}


	private void queuePhoto(String fileId, String size, ImageView imageView, boolean fromUrl) {
		PhotoToLoad p = new PhotoToLoad(fileId, size, imageView, fromUrl);
		executorService.submit(new PhotosLoader(p));
	}

	private void queuePhotoWithProgress(String fileId, String size,
			ImageView imageView, ProgressBar progressBar, boolean fromUrl) {
		PhotoToLoad p = new PhotoToLoad(fileId, size, imageView, progressBar, fromUrl);
		executorService.submit(new PhotosLoader(p));
	}

	public Bitmap getBitmap(String fileId, String size, boolean fromUrl) {
		File file = fileCache.getFile(fileId + size);

		// from SD cache
		Bitmap bitmap = decodeFile(file, size);

		if (bitmap != null) {
			return bitmap;
		}
		
		// from web
		try {
			bitmap = null;
			
			if (fromUrl) {
				InputStream is = ConnectionHandler.httpGetRequest(fileId,
						UsersManagement.getLoginUser().getId());
				OutputStream os = new FileOutputStream(file);
				Utils.copyStream(is, os);
				os.close();
				is.close();
			} else {
				CouchDB.downloadFile(fileId, file);
			}
			
			bitmap = decodeFile(file, size);

			return bitmap;
			// return ConnectionHandler.getBitmapObject(url);
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}

	}

	public Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap bitmap = decodeFile(f);

		if (bitmap != null) {
			Logger.error("ImageLoader", "fileCache.returnBitmap : " + url);
			return bitmap;
		}

		// from web
		try {

			bitmap = null;
			InputStream is = ConnectionHandler.httpGetRequest(url,
					UsersManagement.getLoginUser().getId());
			OutputStream os = new FileOutputStream(f);
			Utils.copyStream(is, os);
			os.close();
			is.close();
			// conn.disconnect();
			bitmap = decodeFile(f);

			return bitmap;
			// return ConnectionHandler.getBitmapObject(url);
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}

	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f, String size) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			int REQUIRED_SIZE = 200;
			// Find the correct scale value. It should be the power of 2.
			if (size.equals(SMALL)) {
				REQUIRED_SIZE = SIZE_SMALL;
			} else if (size.equals(LARGE)) {
				REQUIRED_SIZE = SIZE_LARGE;
			}
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// scale=4;
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			if (Utils.isOsVersionHigherThenGingerbread()) {
				o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
			} else {
				o2.inPreferredConfig = Bitmap.Config.RGB_565;
			}
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			int REQUIRED_SIZE = 200;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// scale=4;
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			if (Utils.isOsVersionHigherThenGingerbread()) {
				o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
			} else {
				o2.inPreferredConfig = Bitmap.Config.RGB_565;
			}
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String fileId;
		public String size;
		public ImageView imageView;
		public ProgressBar pbLoading = null;
		public boolean fromUrl = false;

		public PhotoToLoad(String f, String s, ImageView i, ProgressBar pb, boolean url) {
			fileId = f;
			size = s;
			imageView = i;
			pbLoading = pb;
			fromUrl = url;
		}

		public PhotoToLoad(String f, String s, ImageView i, boolean url) {
			size = s;
			fileId = f;
			imageView = i;
			fromUrl = url;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad))
					return;

				Bitmap bmp = getBitmap(photoToLoad.fileId, photoToLoad.size, photoToLoad.fromUrl);
				memoryCache.put(photoToLoad.fileId + photoToLoad.size, bmp);
				if (imageViewReused(photoToLoad))
					return;
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.fileId + photoToLoad.size))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null) {
				photoToLoad.imageView.setImageBitmap(bitmap);
				if (photoToLoad.pbLoading != null) {
					photoToLoad.pbLoading.setVisibility(View.GONE);
				}
			} else {
				// if (photoToLoad.pbLoading == null) {
//				 photoToLoad.imageView.setImageResource(stub_id); 
				// }
			}
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}
	
	public void cancelDisplayTaskFor(ImageView imageView) {
	    imageViews.remove(imageView);
	}
	
	public void cancelDisplayTaskFor(ImageView imageView, ProgressBar progressBar) {
	    imageViews.remove(imageView);
//	    progressBar.setVisibility(View.GONE);
	}

}
