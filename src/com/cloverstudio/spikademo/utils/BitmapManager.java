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

package com.cloverstudio.spikademo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.ConnectionHandler;
import com.cloverstudio.spikademo.management.UsersManagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public enum BitmapManager {
	INSTANCE;

	private final Map<String, SoftReference<Bitmap>> cache;
	private final ExecutorService pool;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	private Bitmap placeholder;
	private int placeholder_id;
	private float imgRatio;
	private boolean smallImg;

	BitmapManager() {
		cache = new HashMap<String, SoftReference<Bitmap>>();
		pool = Executors.newFixedThreadPool(5);
	}

	public void setPlaceholder(Bitmap bmp) {
		placeholder = bmp;
	}

	public Bitmap getBitmapFromCache(String url) {
		if (cache.containsKey(url)) {
			return cache.get(url).get();
		}

		return null;
	}

	public void removeBitmapFromCache(String url) {
		if (cache.containsKey(url)) {
			cache.remove(url);
		}
	}

	public void clearMemoryCache() {
		if (cache != null) {
			cache.clear();
		}
	}

	public void queueJob(final String url, final ImageView imageView,
			final ProgressBar pbLoading) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						if (smallImg) {
							imageView.setScaleType(ImageView.ScaleType.CENTER);
						} else {
							imageView
									.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
						}
						imageView.setImageBitmap((Bitmap) msg.obj);
						if (pbLoading != null)
							pbLoading.setVisibility(View.GONE);
					} else {
//						imageView.setImageBitmap(placeholder);
						imageView.setImageResource(R.drawable.image_stub);
						if (pbLoading != null)
							pbLoading.setVisibility(View.GONE);
					}
				}
			}
		};

		pool.submit(new Runnable() {
			@Override
			public void run() {
				final Bitmap bmp = downloadBitmap(url);
				Message message = Message.obtain();
				message.obj = bmp;

				handler.sendMessage(message);
			}
		});
	}

	public void loadBitmap(final String url, final ImageView imageView,
			ProgressBar pbLoading) {
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);

		// check in UI thread, so no concurrency issues
		if (bitmap != null) {
			if (smallImg) {
				imageView.setScaleType(ImageView.ScaleType.CENTER);
			} else {
				imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			}
			imageView.setImageBitmap(bitmap);
			if (pbLoading != null)
				pbLoading.setVisibility(View.GONE);
		} else {
			imageView.setImageBitmap(null);
			imageView.setBackgroundResource(R.color.loading_background);
			queueJob(url, imageView, pbLoading);
		}
	}

	private Bitmap downloadBitmap(String url) {
		try {
			Bitmap bitmap = BitmapFactory
					.decodeStream((InputStream) ConnectionHandler
							.httpGetRequest(url, UsersManagement
									.getLoginUser().getId()));
			imgRatio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
			smallImg = false;

//			int REQUIRED_SIZE = 100;
//
//			int width = 0;
//			int height = 0;
//
//			if (bitmap.getHeight() < REQUIRED_SIZE
//					&& bitmap.getWidth() < REQUIRED_SIZE) {
//				width = bitmap.getWidth();
//				height = bitmap.getHeight();
//				smallImg = true;
//			} else if (bitmap.getHeight() < height) {
//				height = bitmap.getHeight();
//			} else if (bitmap.getWidth() < width) {
//				width = bitmap.getWidth();
//			}
//
//
//			bitmap = Bitmap.createScaledBitmap(bitmap,
//					(int) (height * imgRatio), height, true);
			cache.put(url, new SoftReference<Bitmap>(bitmap));
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
