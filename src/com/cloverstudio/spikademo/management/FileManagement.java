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

package com.cloverstudio.spikademo.management;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * FileManagement
 * 
 * Helper class for managing application's files.
 */

public class FileManagement {

	private static String TAG = "FileManagement";

	private static String _mainDirName = Environment
			.getExternalStorageDirectory().toString()
			+ "/Android/data/com.cloverstudio.spikademo/";
	private static String _cacheDirName = "downloads/images";
	// private static String _cameraCacheDirName = "camera_cache/images";
	public static File _cacheDir;
	public static File _cameraCacheDir;

	public static FileManagement _fileManagement;

	public FileManagement(Context context) {
		CreateCacheDir(context);
		_fileManagement = this;
	}

	public static boolean FileExists(String url) {

		String filename = md5(url) + ".jpg";

		File f = null;
		if (!filename.equals("")) {
			f = new File(_cacheDir, filename);
		} else {
			// TODO: Handle situation when file is not created!
		}

		return f.exists();
	}

	public static File CreateFile(String url, InputStream is)
			throws IOException {

		String filename = md5(url) + ".jpg";
		File f = null;
		if (!filename.equals("")) {
			if (_cacheDir == null) {
				Log.v(TAG, "null");
			}
			Log.v(TAG, _cacheDir.getPath());
			f = new File(_cacheDir, filename);
		} else {
			// TODO: Handle situation when file is not created!
		}

		Bitmap mBitmap = BitmapFactory.decodeStream(is);
		FileOutputStream os = new FileOutputStream(f);
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
		os.flush();
		os.close();

		return f;
	}

	public static File GetFile(String filename) {
		filename = md5(filename) + ".jpg";
		File f = null;
		if (!filename.equals("")) {
			f = new File(_cacheDir, filename);
		} else {
			// TODO: Handle situation when file is not created!
		}

		return f;
	}

	private static void CreateCacheDir(Context context) {
		if (_cacheDir == null) {
			_cacheDir = new File(_mainDirName, _cacheDirName);
			if (!_cacheDir.exists()) {
				_cacheDir.mkdirs();
			}
		}
		if (_cameraCacheDir == null) {
			/*
			 * _cameraCacheDir = new File(_mainDirName, _cameraCacheDirName);
			 * if(!_cameraCacheDir.exists()){ _cameraCacheDir.mkdirs(); }
			 */
			File cacheDir = null;

			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED))
				cacheDir = context.getExternalCacheDir();
			else
				cacheDir = context.getCacheDir();
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
				cacheDir.deleteOnExit();
			}

			_cameraCacheDir = cacheDir;
		}
	}

	private static String md5(String s) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes(), 0, s.length());
			String hash = new BigInteger(1, digest.digest()).toString(16);
			return hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
