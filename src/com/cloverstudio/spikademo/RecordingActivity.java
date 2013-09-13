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
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.dialog.HookUpAlertDialog;
import com.cloverstudio.spikademo.dialog.HookUpDialog;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.dialog.HookUpAlertDialog.ButtonType;
import com.cloverstudio.spikademo.extendables.SpikaActivity;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.messageshandling.SendMessageAsync;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.ExtAudioRecorder;
import com.cloverstudio.spikademo.utils.LayoutHelper;
import com.cloverstudio.spikademo.utils.Logger;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * RecordingActivity
 * 
 * Records a voice message.
 */

@SuppressLint("DefaultLocale")
public class RecordingActivity extends SpikaActivity {

	private static int START_PLAYING = 0;
	private static int PAUSE_PLAYING = 1;
	private static int STOP_PLAYING = 2;

	private static int PLAYING = 2;
	private static int PAUSE = 1;
	private static int STOP = 0;

	private boolean mIsRecording;
	private static String sFileName = null;

	private ExtAudioRecorder mExtAudioRecorder;

	private Chronometer mRecordTime;
	private TextView mRecordingText;
	private Handler mHandlerForProgressBar = new Handler();
	private Runnable mRunnForProgressBar;

	private int mIsPlaying = STOP; // 0 - play is on stop, 1 - play is on pause,
									// 2 - playing
	private MediaPlayer mPlayer = null;
	private ProgressBar mPbForPlaying;
	private ImageView mPlayPause;
	private ImageView mStopSound;
	private RelativeLayout mRlSoundControler;
	private Button mBtnSend;
	private Button mBtnRecording;
	private EditText mEtNameOfUserVoice;

	private CountDownTimer mRecordingTimer;
	private HookUpAlertDialog mRecordingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_recording);
		
		mRecordingDialog = new HookUpAlertDialog(this);

		mRecordingTimer = new CountDownTimer(Const.MAX_RECORDING_TIME_VOICE, 1000) {

			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
			    
				mRecordingDialog.show(getString(R.string.exceed_voice_duration), ButtonType.OK);
			    
				onRecord(false);
			}
			
		};

		Button back = (Button) findViewById(R.id.btnBack);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("VOICE");

		ImageView ivAvatar = (ImageView) findViewById(R.id.ivAvatarVoice);
		LayoutHelper.scaleWidthAndHeightRelativeLayout(this, 5f, ivAvatar);
		Utils.displayImage(UsersManagement.getLoginUser().getAvatarFileId(),
				ivAvatar, ImageLoader.SMALL, R.drawable.user_stub, false);

		mEtNameOfUserVoice = (EditText) findViewById(R.id.etNameOfUserVoice);

		mBtnRecording = (Button) findViewById(R.id.btnRecording);
		mRecordTime = (Chronometer) findViewById(R.id.recordTime);
		mRecordingText = (TextView) findViewById(R.id.tvRecording);
		mRlSoundControler = (RelativeLayout) findViewById(R.id.soundControler);
		mBtnSend = (Button) findViewById(R.id.btnSend);
		mBtnSend.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mPbForPlaying = (ProgressBar) findViewById(R.id.pbVoice);
		mPlayPause = (ImageView) findViewById(R.id.ivPlayPause);
		mStopSound = (ImageView) findViewById(R.id.ivStopSound);

		mBtnRecording.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsRecording) {

					onRecord(!mIsRecording);

				} else {
					hideAndRestartSoundController();
					mIsRecording = true;
					onRecord(mIsRecording);
				}
			}
		});
	}

	private void onRecord(boolean start) {
		if (start) {
			startRecording();
			mBtnRecording
					.setBackgroundResource(R.drawable.icon_microphone_rec_selector);
		} else {
			stopRecording();
			mBtnRecording
					.setBackgroundResource(R.drawable.icon_microphone_selector);
		}
	}

	private void startRecording() {

		mRecordTime.setVisibility(View.VISIBLE);

		mRecordTime.setBase(SystemClock.elapsedRealtime());
		mRecordTime.start();
		mRecordingText.setText("RECORDING...");

		setRecordingFile();

		mExtAudioRecorder = ExtAudioRecorder.getInstanse(false);
		mExtAudioRecorder.setOutputFile(sFileName);
		mExtAudioRecorder.prepare();
		mExtAudioRecorder.start();

		mRecordingTimer.start();

	}

	// stop recodrding for extaudio class
	private void stopRecording() {
		mExtAudioRecorder.stop();
		mExtAudioRecorder.release();
		mRecordTime.stop();
		mRecordTime.setVisibility(View.INVISIBLE);
		applyAlphaAnimationToView(mRecordTime, true);
		mRecordingText.setText("RECORDING DONE");
		showSoundController();
		mIsRecording = false;
		mRecordingTimer.cancel();
	}

	private void hideAndRestartSoundController() {

		mRlSoundControler.setVisibility(View.INVISIBLE);

		mBtnSend.setVisibility(View.INVISIBLE);

		mPbForPlaying.setProgress(0);
		mRecordTime.setVisibility(View.INVISIBLE);

		mRecordingText.setText("RECORDING...");
		mPlayPause.setBackgroundResource(R.drawable.play_btn);
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}

	private void showSoundController() {

		mBtnSend.setVisibility(View.VISIBLE);
		applyAlphaAnimationToView(mBtnSend, false);

		mBtnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("LOG", sFileName);
				new FileUploadAsync(RecordingActivity.this).execute(sFileName);
			}
		});

		mIsPlaying = STOP;

		mPlayPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsPlaying == PLAYING) {
					// pause
					mPlayPause.setImageResource(R.drawable.play_btn);
					onPlay(PAUSE_PLAYING);
				} else {
					// play
					mPlayPause.setImageResource(R.drawable.pause_btn);
					onPlay(START_PLAYING);
				}
			}
		});

		mStopSound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsPlaying == PLAYING || mIsPlaying == PAUSE) {
					// stop
					mPlayPause.setImageResource(R.drawable.play_btn);
					onPlay(STOP_PLAYING);
				}
			}
		});

		mRlSoundControler.setVisibility(View.VISIBLE);

	}

	private void onPlay(int playPauseStop) { // 0 is to start playing, 1 is to
												// pause playing and 2 is for
												// stop playing

		if (playPauseStop == START_PLAYING) {

			startPlaying();

		} else if (playPauseStop == PAUSE_PLAYING) {

			pausePlaying();

		} else {

			stopPlaying();

		}
	}

	private void startPlaying() {
		if (mIsPlaying == STOP) {
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(sFileName);
				mPlayer.prepare();
				mPlayer.start();
				mPbForPlaying.setMax((int) mPlayer.getDuration());

				mRunnForProgressBar = new Runnable() {

					@Override
					public void run() {
						mPbForPlaying.setProgress((int) mPlayer
								.getCurrentPosition());
						if (mPlayer.getDuration() - 99 > mPlayer
								.getCurrentPosition()) {
							mHandlerForProgressBar.postDelayed(
									mRunnForProgressBar, 100);
						} else {
							mPbForPlaying.setProgress((int) mPlayer
									.getDuration());
						}
					}
				};
				mHandlerForProgressBar.post(mRunnForProgressBar);
				mIsPlaying = PLAYING;

			} catch (IOException e) {
				Log.e("LOG", "prepare() failed");
			}
		} else if (mIsPlaying == PAUSE) {
			mPlayer.start();
			mHandlerForProgressBar.post(mRunnForProgressBar);
			mIsPlaying = PLAYING;
		}

	}

	private void stopPlaying() {
		mPlayer.release();
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
		mPbForPlaying.setProgress(0);
		mPlayer = null;
		mIsPlaying = STOP;
	}

	private void pausePlaying() {
		mPlayer.pause();
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
		mIsPlaying = PAUSE;
	}

	private void setRecordingFile() {

		File audio = getFileDir(getApplicationContext());
		audio.mkdirs();
		sFileName = audio.getAbsolutePath() + "/voice.wav";
		Log.d("Dir:", sFileName);
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

	public void onPause() {
		super.onPause();
		if (mExtAudioRecorder != null) {
			stopRecording();
			mExtAudioRecorder.release();
			mExtAudioRecorder.stop();
			mExtAudioRecorder = null;
		}
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
			mIsPlaying = STOP;
			mPbForPlaying.setProgress(0);
			mPlayPause.setImageResource(R.drawable.play_btn);
		}
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
	}

	public void onFinish() {
		super.onDestroy();
		if (mExtAudioRecorder != null) {
			mExtAudioRecorder.release();
			mExtAudioRecorder.stop();
			mExtAudioRecorder = null;
		}
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
			mIsPlaying = STOP;
			mPbForPlaying.setProgress(0);
			mPlayPause.setImageResource(R.drawable.play_btn);
		}
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
	}

	private void applyAlphaAnimationToView(View view, boolean toDisapear) {
		AlphaAnimation animation;
		if (!toDisapear) {
			animation = new AlphaAnimation(0.0f, 1.0f);
		} else {
			animation = new AlphaAnimation(1.0f, 0.0f);
		}
		animation.setDuration(200);
		view.startAnimation(animation);
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
						RecordingActivity.this);
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
			if (mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			String voiceSubject = mEtNameOfUserVoice.getText().toString();
			if (voiceSubject.equals(null) || voiceSubject.equals("")) {
				voiceSubject = "";
			}
			try {
				new SendMessageAsync(getApplicationContext(),
						SendMessageAsync.TYPE_VOICE).execute(voiceSubject,
						false, false, result).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		mRecordingTimer.cancel();
		mRecordingTimer = null;
		super.onDestroy();
	}

}
