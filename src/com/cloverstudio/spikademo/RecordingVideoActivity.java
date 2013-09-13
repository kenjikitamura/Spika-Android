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

import java.io.File;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.dialog.HookUpDialog;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.dialog.TempVideoChooseDialog;
import com.cloverstudio.spikademo.extendables.SpikaActivity;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.messageshandling.SendMessageAsync;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.LayoutHelper;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * RecordingVideoActivity
 * 
 * Records a video message.
 */

public class RecordingVideoActivity extends SpikaActivity {

	private static final int RESULT_FROM_GALLERY = 55;
	private static final int RESULT_FROM_CAMERA = 56;

	private static String sFileName = null;

	private VideoView mVideoView;

	private int mIsPlaying = 0; // 0 - play is on stop, 1 - play is on pause, 2
								// - playing

	private ProgressBar mPbForPlaying;
	private ImageView mPlayPause;
	private ImageView mStopSound;

	private Handler mHandlerForProgressBar = new Handler();
	private Runnable mRunnForProgressBar;

	private long mDurationOfVideo = 0;

	MediaRecorder recorder;
	Camera camera;
	private EditText mEtNameOfUserVideo;

    int videoDuration = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRecordingVideoActivity();
		
		Bundle extras = getIntent().getExtras();
		if (extras.getBoolean("camera") == true) { 
			gotoGalleryOrCamera(
					TempVideoChooseDialog.BUTTON_CAMERA);
		}
		if (extras.getBoolean("gallery") == true) { 
				gotoGalleryOrCamera(
						TempVideoChooseDialog.BUTTON_GALLERY);
		}

	}
	
	private void setRecordingVideoActivity() {
		
		setContentView(R.layout.activity_video_recording);

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("VIDEO");

		ImageView ivAvatar = (ImageView) findViewById(R.id.ivAvatarVideo);
		LayoutHelper.scaleWidthAndHeightRelativeLayout(this, 5f, ivAvatar);
		Utils.displayImage(
				UsersManagement.getLoginUser().getAvatarFileId(), ivAvatar,
				ImageLoader.SMALL, R.drawable.user_stub, false);
		
		mEtNameOfUserVideo = (EditText) findViewById(R.id.etNameOfUserVideo);

		final Button send = (Button) findViewById(R.id.btnSend);

		final Button back = (Button) findViewById(R.id.btnBack);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mIsPlaying = 0;

		mVideoView = (VideoView) findViewById(R.id.videoViewForVideo);

		mPbForPlaying = (ProgressBar) findViewById(R.id.pbVoice);
		mPlayPause = (ImageView) findViewById(R.id.ivPlayPause);
		mStopSound = (ImageView) findViewById(R.id.ivStopSound);

		mPlayPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsPlaying == 2) {
					// pause
					mPlayPause.setImageResource(R.drawable.play_btn);
					onPlay(1);
				} else {
					// play
					mPlayPause.setImageResource(R.drawable.pause_btn);
					onPlay(0);
				}
			}
		});

		mStopSound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsPlaying == 2 || mIsPlaying == 1) {
					// stop
					mPlayPause.setImageResource(R.drawable.play_btn);
					onPlay(2);
				}
			}
		});

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("LOG", sFileName);
				new FileUploadAsync(RecordingVideoActivity.this)
						.execute(sFileName);
			}
		});
		
	}


	private void gotoGalleryOrCamera(int chooseWhereToGo) {
		switch (chooseWhereToGo) {
		case TempVideoChooseDialog.BUTTON_CAMERA:

			if (!getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_CAMERA)) {
				Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG)
						.show();
				finish();
			} else {

				try {
					Intent cameraIntent = new Intent(
							MediaStore.ACTION_VIDEO_CAPTURE);
					cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, Const.MAX_RECORDING_TIME_VIDEO);
					File videoFolder = getFileDir(this);

					videoFolder.mkdirs(); // <----
					File video = new File(videoFolder, "video.mp4");
					Uri uriSavedVideo = Uri.fromFile(video);

					sFileName = video.getPath();

					cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
//					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//							uriSavedVideo);
					startActivityForResult(cameraIntent, RESULT_FROM_CAMERA);
				} catch (Exception ex) {
					Toast.makeText(this, getString(R.string.no_camera),
							Toast.LENGTH_LONG).show();
					finish();
				}

			}
			break;

		case TempVideoChooseDialog.BUTTON_GALLERY:
			Intent intent = new Intent();
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			this.startActivityForResult(intent, RESULT_FROM_GALLERY);
			break;

		default:
			break;
		}
	}

	private File getFileDir(Context context) {
		File cacheDir = null;

		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"HookUp");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();

		return cacheDir;
	}

	private void onPlay(int playPauseStop) {

		if (playPauseStop == 0) {

			startPlaying();

		} else if (playPauseStop == 1) {

			pausePlaying();

		} else {

			stopPlaying();

		}
	}

	private void startPlaying() {
		if (mIsPlaying == 0) {
			mVideoView.requestFocus();

			mVideoView.setVideoURI(Uri.parse(sFileName));

			mVideoView.start();

			mVideoView.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mDurationOfVideo = mVideoView.getDuration();
					mPbForPlaying.setMax((int) mDurationOfVideo);

					mRunnForProgressBar = new Runnable() {

						@Override
						public void run() {
							mPbForPlaying.setProgress((int) mVideoView
									.getCurrentPosition());
							if (mDurationOfVideo - 99 > mVideoView
									.getCurrentPosition()) {
								mHandlerForProgressBar.postDelayed(
										mRunnForProgressBar, 100);
							} else {
								mPbForPlaying.setProgress((int) mVideoView
										.getDuration());
							}
						}
					};
					mHandlerForProgressBar.post(mRunnForProgressBar);
					mIsPlaying = 2;
				}
			});

		} else if (mIsPlaying == 1) {
			mVideoView.start();
			mHandlerForProgressBar.post(mRunnForProgressBar);
			mIsPlaying = 2;
		}

	}

	private void stopPlaying() {
		mVideoView.stopPlayback();
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
		mPbForPlaying.setProgress(0);
		mIsPlaying = 0;
	}

	private void pausePlaying() {
		mVideoView.pause();
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
		mIsPlaying = 1;
	}

	private String getVideoPath(Uri uri) {
		String[] projection = { MediaStore.Video.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private String getVideoPathNew(Uri uri) {
		String path = null;
		Cursor cursor = getContentResolver().query(
				Media.EXTERNAL_CONTENT_URI,
				new String[] { Media.DATA, Media.DATE_ADDED,
						MediaStore.Images.ImageColumns.ORIENTATION },
				Media.DATE_ADDED, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				uri = Uri.parse(cursor.getString(cursor
						.getColumnIndex(Media.DATA)));
				path = uri.toString();
			} while (cursor.moveToNext());
			cursor.close();
		}
		return path;
	}

   private int getVideoDuration(Uri uri) {
        String duration = "0";
        
        Cursor cursor = getContentResolver().query(
                uri,
                new String[] { MediaStore.Video.VideoColumns.DURATION },
                Media.DATE_ADDED, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                duration = cursor.getString(cursor.getColumnIndex("duration"));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return Integer.parseInt(duration);
    }
	   
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    
		if (requestCode == RESULT_FROM_CAMERA) {

			if (data != null) {
				 Uri contentUri = data.getData();
				 try {
				     videoDuration = getVideoDuration(contentUri);
					 String tmppath = getVideoPath(contentUri);
					 sFileName = tmppath;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				finish();
			}

		} else if (requestCode == RESULT_FROM_GALLERY) {
			try {
				Uri selected_video = data.getData();
				
				videoDuration = getVideoDuration(selected_video);
				sFileName = getVideoPath(selected_video);
			} catch (Exception e) {
				e.printStackTrace();
				finish();
			}
		}
		

		new Handler().postDelayed(new Runnable() {
		  @Override
		  public void run() {

	        if(videoDuration != 0 && videoDuration > Const.MAX_RECORDING_TIME_VIDEO * 1000){
	              
	            final HookUpDialog errorDialog = new HookUpDialog(RecordingVideoActivity.this);
	            
	            errorDialog.setOnButtonClickListener(
	                    
	                HookUpDialog.BUTTON_OK, new OnClickListener() {

	                    @Override
	                    public void onClick(View v) {
	                        
	                        RecordingVideoActivity.this.finish();
	                        
	                        errorDialog.dismiss();
	                        
	                    }
	                    
	            });
	            
	            errorDialog.showOnlyOK(getString(R.string.exceed_video_duration));
	            
	       
	        }  
		            
		  }
		}, 100);

		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void checkIfAppIsInForeground() {
		SpikaApp.gOpenFromBackground = false;
	}

	private class FileUploadAsync extends SpikaAsync<String, Void, String> {

		private HookUpProgressDialog mProgressDialog;

		protected FileUploadAsync(Context context) {
			super(context);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (mProgressDialog == null) {
				mProgressDialog = new HookUpProgressDialog(
						RecordingVideoActivity.this);
			}
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String filePath = params[0];
			String fileId = CouchDB.uploadFile(filePath);
			return fileId;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {

			} else {

			}
			
			String videoSubject = mEtNameOfUserVideo.getText().toString();
			if (videoSubject.equals(null) || videoSubject.equals("")) {
//				videoSubject = UsersManagement.getLoginUser().getName()
//						.toUpperCase()
//						+ "'S VIDEO";
				videoSubject = "";
			}
			try {
				new SendMessageAsync(getApplicationContext(),
						SendMessageAsync.TYPE_VIDEO).execute(videoSubject, false, false,
								result).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			if (mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			finish();
		}
	}

}
