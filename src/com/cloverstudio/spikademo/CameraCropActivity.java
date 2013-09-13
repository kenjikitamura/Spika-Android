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

package com.cloverstudio.spikademo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.FloatMath;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.extendables.SpikaActivity;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.management.BitmapManagement;
import com.cloverstudio.spikademo.management.FileManagement;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.messageshandling.SendMessageAsync;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Logger;
import com.cloverstudio.spikademo.utils.Utils;
import com.cloverstudio.spikademo.view.CroppedImageView;

/**
 * CameraCropActivity
 * 
 * Creates cropped image from a gallery photo using a square frame.
 */

public class CameraCropActivity extends SpikaActivity implements
		OnTouchListener {

	// These matrices will be used to move and zoom image
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private Matrix translateMatrix = new Matrix();

	// We can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	// Remember some things for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;

	private CroppedImageView mImageView;
	private Bitmap mBitmap;

	public int crop_container_size;

	private float mAspect;

	private float start_x, start_y;

	// Gallery type marker
	private static final int GALLERY = 1;
	// Camera type marker
	private static final int CAMERA = 2;
	// Uri for captured image so we can get image path
	private String _path;

	public static boolean return_flag;

	private boolean mIsCamera;
	private boolean mTrio;
	private boolean mPlanner;
	private String mGroupId = "";

//	private ProgressDialog mProgressDialog;

	private boolean mForProfile;
	private boolean mForGroup;
	private boolean mForGroupUpdate;

	private String mFilePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_crop);

		getImageIntents();
		mImageView = (CroppedImageView) findViewById(R.id.ivCameraCropPhoto);
		mImageView.setDrawingCacheEnabled(true);

		return_flag = false;

		Button btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// Cancel button
		findViewById(R.id.btnCameraCancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						if (getIntent().getStringExtra("type")
								.equals("gallery")) {
							Intent intent = new Intent(CameraCropActivity.this,
									CameraCropActivity.class);
							intent.putExtra("type", "gallery");
							startActivity(intent);
						} else {
							Intent intent = new Intent(CameraCropActivity.this,
									CameraCropActivity.class);
							intent.putExtra("type", "camera");
							startActivity(intent);
						}
					}
				});

		// Next button
		findViewById(R.id.btnCameraOk).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Bitmap resizedBitmap = getBitmapFromView(mImageView);
						ByteArrayOutputStream bs = new ByteArrayOutputStream();
						resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
								bs);

						if (saveBitmapToFile(resizedBitmap, mFilePath) == true) {

							if (mForProfile == true) {
								MyProfileActivity.gProfileImage = getBitmapFromView(mImageView);
								MyProfileActivity.gProfileImagePath = mFilePath;
								finish();
							} else if (mForGroup == true) {
								CreateGroupActivity.gGroupImage = getBitmapFromView(mImageView);
								CreateGroupActivity.gGroupImagePath = mFilePath;
								finish();
							} else if (mForGroupUpdate == true) {
								GroupProfileActivity.gGroupImage = getBitmapFromView(mImageView);
								GroupProfileActivity.gGroupImagePath = mFilePath;
								finish();
							} else {

								new FileUploadAsync(CameraCropActivity.this)
										.execute(mFilePath);
								// new SendMessageAsync(getApplicationContext(),
								// SendMessageAsync.TYPE_PHOTO)
								// .execute(resizedBitmap);
							}
							
						} else {
							Toast.makeText(CameraCropActivity.this,
									"Failed to send photo", Toast.LENGTH_LONG)
									.show();
						}

					}
				});
	}

	private void getImageIntents() {
		/*
		 * if (getIntent().hasExtra("trio")) { _trio = true; _groupId =
		 * getIntent().getStringExtra("groupId"); _planner =
		 * getIntent().getBooleanExtra("planner", false); } else { _trio =
		 * false; _planner = false; _groupId = ""; }
		 */
		if (getIntent().getStringExtra("type").equals("gallery")) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			this.startActivityForResult(intent, GALLERY);

			mIsCamera = false;

		} else {
			try {
				startCamera();
				mIsCamera = true;
			} catch (UnsupportedOperationException ex) {
				ex.printStackTrace();
				Toast.makeText(getBaseContext(),
						"UnsupportedOperationException", Toast.LENGTH_SHORT)
						.show();
			}
		}
		if (getIntent().getBooleanExtra("profile", false) == true) {
			mForProfile = true;
		} else {
			mForProfile = false;
		}
		if (getIntent().getBooleanExtra("createGroup", false) == true) {
			mForGroup = true;
		} else {
			mForGroup = false;
		}
		if (getIntent().getBooleanExtra("groupUpdate", false) == true) {
			mForGroupUpdate = true;
		} else {
			mForGroupUpdate = false;
		}
	}


	public void startCamera() {
		// Check if camera exists
		if (!getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG)
					.show();
			finish();
		} else {
			try {
				long date = System.currentTimeMillis();
				String filename = DateFormat
						.format("yyyy-MM-dd_kk.mm.ss", date).toString()
						+ ".jpg";
				_path = this.getExternalCacheDir() + "/" + filename;
				File file = new File(_path);
//				File file = new File(getFileDir(getBaseContext()), filename);
				Uri outputFileUri = Uri.fromFile(file);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				startActivityForResult(intent, CAMERA);
			} catch (Exception ex) {
				Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG)
						.show();
				finish();
			}

		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		String pManufacturer = android.os.Build.MANUFACTURER;
		String pModel = android.os.Build.MODEL;

		if ("GT-I9300".equals(pModel) && "samsung".equals(pManufacturer)) {

			RelativeLayout main = (RelativeLayout) findViewById(R.id.relativeLayout_main);
			main.invalidate();

			setContentView(R.layout.activity_camera_crop);

			mImageView = (CroppedImageView) findViewById(R.id.ivCameraCropPhoto);
			mImageView.setDrawingCacheEnabled(true);

			scaleView();

			// Cancel button
			findViewById(R.id.btnCameraCancel).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {

							finish();
							if (getIntent().getStringExtra("type").equals(
									"gallery")) {
								Intent intent = new Intent(
										CameraCropActivity.this,
										CameraCropActivity.class);
								intent.putExtra("type", "gallery");
								startActivity(intent);
							} else {
								Intent intent = new Intent(
										CameraCropActivity.this,
										CameraCropActivity.class);
								intent.putExtra("type", "camera");
								startActivity(intent);
							}
						}
					});

			// Next button
			findViewById(R.id.btnCameraOk).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							Bitmap resizedBitmap = getBitmapFromView(mImageView);

							ByteArrayOutputStream bs = new ByteArrayOutputStream();
							resizedBitmap.compress(Bitmap.CompressFormat.JPEG,
									100, bs);

							if (saveBitmapToFile(resizedBitmap, mFilePath) == true) {

								if (mForProfile == true) {
									MyProfileActivity.gProfileImage = getBitmapFromView(mImageView);
									MyProfileActivity.gProfileImagePath = mFilePath;
								} else if (mForGroup == true) {
									CreateGroupActivity.gGroupImage = getBitmapFromView(mImageView);
									CreateGroupActivity.gGroupImagePath = mFilePath;
								} else if (mForGroupUpdate == true) {
									GroupProfileActivity.gGroupImage = getBitmapFromView(mImageView);
									GroupProfileActivity.gGroupImagePath = mFilePath;
								} else {

									new FileUploadAsync(CameraCropActivity.this)
											.execute(mFilePath);
									// new
									// SendMessageAsync(getApplicationContext(),
									// SendMessageAsync.TYPE_PHOTO)
									// .execute(resizedBitmap);
								}
							} else {
								Toast.makeText(CameraCropActivity.this,
										"Failed to send photo",
										Toast.LENGTH_LONG).show();
							}

						}
					});

			File file = new File(_path);
			boolean exists = file.exists();
			if (exists)
				onPhotoTaken(_path);
			else
				Toast.makeText(
						getBaseContext(),
						"Something went wrong while taking picture, please try again.",
						Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			scaleView();
		}
	}

	public void scaleView() {
		// instantiate the views
		View top_view = findViewById(R.id.topView);
		View bottom_view = findViewById(R.id.bottomView);
		LinearLayout footer = (LinearLayout) findViewById(R.id.llFooter);
		LinearLayout crop_frame = (LinearLayout) findViewById(R.id.llCropFrame);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		// 90% of width
		crop_container_size = (int) ((float) width * (1f - (10f / 100f)));

		// 10% margins
		float margin = ((float) width * (1f - (90f / 100f)));

		// Parameters for white crop border
		LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(
				crop_container_size, crop_container_size);
		par.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		par.setMargins((int) (margin / 2f), 0, (int) (margin / 2f), 0);
		crop_frame.setLayoutParams(par);

		// Margins for other transparent views
		float top_view_height = ((float) (height - crop_container_size - footer
				.getHeight())) / (float) 2;
		top_view.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) top_view_height));
		bottom_view.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) top_view_height));

		// Image container
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				crop_container_size, crop_container_size);
		params.setMargins((int) (margin / 2f), (int) top_view_height,
				(int) (margin / 2f), 0);
		mImageView.setLayoutParams(params);
		mImageView.setImageBitmap(mBitmap);
		mImageView.setMaxZoom(4f);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
				start_x = event.getX() - start.x;
				start_y = event.getY() - start.y;
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					mAspect = scale;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		view.invalidate();
		return true;
	}

	/**
	 * Get the image from container - it is already cropped and zoomed If the
	 * image is smaller than container it will be black color set aside
	 * */
	private Bitmap getBitmapFromView(View view) {
		Bitmap returnedBitmap = Bitmap.createBitmap(crop_container_size,
				crop_container_size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			bgDrawable.draw(canvas);
		else
			canvas.drawColor(Color.BLACK);
		view.draw(canvas);
		return returnedBitmap;
	}

	/** Determine the space between the first two fingers */
	@SuppressLint("FloatMath")
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private void _scaleBitmap() {
		int image_width = this.mBitmap.getWidth();
		int image_height = this.mBitmap.getHeight();
		int new_image_width;
		int new_image_height;
		int _screen_width = 640;

		if (image_width >= image_height) {
			if (image_height < _screen_width) {
				new_image_width = (int) ((float) image_width * ((float) _screen_width / (float) image_height));
			} else {
				new_image_width = (int) ((float) image_width / ((float) image_height / (float) _screen_width)); // ok
			}
			this.mBitmap = Bitmap.createScaledBitmap(this.mBitmap,
					new_image_width, _screen_width, true);

		} else if (image_width < image_height) {
			if (image_width < _screen_width) {
				new_image_height = (int) ((float) image_height * ((float) _screen_width / (float) image_width));
			} else {
				new_image_height = (int) ((float) image_height / ((float) image_width / (float) _screen_width));
			}

			this.mBitmap = Bitmap.createScaledBitmap(mBitmap, _screen_width,
					new_image_height, true);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case GALLERY:
				try {
					Uri selected_image = data.getData();
					String selected_image_path = getImagePath(selected_image);
					onPhotoTaken(selected_image_path);
				} catch (Exception e) {
					Toast.makeText(this, "Error loading image from Gallery!",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
					finish();
				}

				break;
			case CAMERA:
				File file = new File(_path);
				boolean exists = file.exists();
				if (exists)
					onPhotoTaken(_path);
				else
					Toast.makeText(
							getBaseContext(),
							"Something goes wrong while taking picture, please try again.",
							Toast.LENGTH_SHORT).show();
				break;
			default:
				finish();
				break;
			}

		}
		/** if there is no image, just finish the activity */
		else {
			finish();
		}
	}

	protected void onPhotoTaken(String path) {

		mFilePath = path;

		new AsyncTask<String, Void, byte[]>() {
			boolean loadingFailed = false;

			@Override
			protected byte[] doInBackground(String... params) {
				try {

					if (params == null)
						return null;

					File f = new File(params[0]);
					ExifInterface exif = new ExifInterface(f.getPath());
					int orientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);

					int angle = 0;

					if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
						angle = 90;
					} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
						angle = 180;
					} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
						angle = 270;
					}

					Matrix mat = new Matrix();
					mat.postRotate(angle);

					BitmapFactory.Options optionsMeta = new BitmapFactory.Options();
					optionsMeta.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(f.getAbsolutePath(), optionsMeta);

					BitmapFactory.Options options = new BitmapFactory.Options();

					options.inSampleSize = BitmapManagement
							.calculateInSampleSize(optionsMeta, 640, 640);
					options.inPurgeable = true;
					options.inInputShareable = true;
					mBitmap = BitmapFactory.decodeStream(
							new FileInputStream(f), null, options);
					mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
							mBitmap.getWidth(), mBitmap.getHeight(), mat, true);
					_scaleBitmap();
					return null;
				} catch (Exception ex) {
					loadingFailed = true;
					finish();
				}

				return null;
			}

			@Override
			protected void onPostExecute(byte[] result) {
				super.onPostExecute(result);

				if (null != mBitmap) {
					mImageView.setImageBitmap(mBitmap);
					mImageView.setScaleType(ScaleType.MATRIX);
					translateMatrix.setTranslate(
							-(mBitmap.getWidth() - crop_container_size) / 2f,
							-(mBitmap.getHeight() - crop_container_size) / 2f);
					mImageView.setImageMatrix(translateMatrix);

					matrix = translateMatrix;

				} 

			}
		}.execute(path);

	}

	private boolean saveBitmapToFile(Bitmap bitmap, String path) {
		File file = new File(path);
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
			fOut.flush();
			fOut.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

	}

	private String getImagePath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (return_flag)
			finish();
	}

	@Override
	protected void checkIfAppIsInForeground() {
		SpikaApp.gOpenFromBackground = false;
	}

	private class FileUploadAsync extends SpikaAsync<String, Void, ArrayList<String>> {
		
		private HookUpProgressDialog mProgressDialog;

		protected FileUploadAsync(Context context) {
			super(context);
		}
		
		@Override
		protected void onPreExecute() {
			if (mProgressDialog == null)
				mProgressDialog = new HookUpProgressDialog(CameraCropActivity.this);
			mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			String filePath = params[0];
            String tmppath = CameraCropActivity.this.getExternalCacheDir() + "/" + Const.TMP_BITMAP_FILENAME;             

			String fileId = CouchDB.uploadFile(filePath);
			
            Bitmap originalBitmap = BitmapFactory.decodeFile(filePath);
            Bitmap thumbBitmap = Utils.scaleBitmap(originalBitmap, Const.PICTURE_THUMB_SIZE, Const.PICTURE_THUMB_SIZE);
            Utils.saveBitmapToFile(thumbBitmap,tmppath);
            String thumbFileId = CouchDB.uploadFile(tmppath);

            ArrayList<String> list = new ArrayList<String>();
            list.add(fileId);
            list.add(thumbFileId);
            
			return list;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if (result != null) {
				try {
				    String fileId = result.get(0);
				    String fileThumbId = result.get(1);
				    
					new SendMessageAsync(getApplicationContext(),
							SendMessageAsync.TYPE_PHOTO).execute("Photo", false, false,
							        fileId,fileThumbId).get();
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else {
				Logger.debug("FileUploadAsync", "Failed");
			}
			finish();
		}
	}
}
