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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.adapters.CommentsAdapter;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.Comment;
import com.cloverstudio.spikademo.couchdb.model.Message;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.extendables.SpikaActivity;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.CommentManagement;
import com.cloverstudio.spikademo.management.SettingsManager;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.messageshandling.CreateCommentAsync;
import com.cloverstudio.spikademo.messageshandling.FindAvatarFileIdAsync;
import com.cloverstudio.spikademo.messageshandling.GetCommentsAsync;
import com.cloverstudio.spikademo.messageshandling.RefreshCommentHandler;
import com.cloverstudio.spikademo.messageshandling.SendMessageAsync;
import com.cloverstudio.spikademo.utils.LayoutHelper;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * VoiceActivity
 * 
 * Displays voice message and related comments.
 */

public class VoiceActivity extends SpikaActivity {

	private static String sFileName = null;

	private Handler mHandlerForProgressBar = new Handler();
	private Runnable mRunnForProgressBar;

	private int mIsPlaying = 0; // 0 - play is on stop, 1 - play is on pause, 2
								// - playing
	private MediaPlayer mPlayer = null;
	private ProgressBar mPbForPlaying;
	private ImageView mPlayPause;
	private ImageView mStopSound;

	private Message mMessage;

	private RefreshCommentHandler mRefreshCommentHandler;

	private Bundle mExtras;

	private ListView mLvComments;
	private CommentsAdapter mCommentsAdapter;

	private List<Comment> mComments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_voice);

		setInitComments();

		mExtras = getIntent().getExtras();

		setInitHeaderAndAvatar();

		setInitSoundControl();

	}

	private void setInitHeaderAndAvatar() {
		ImageView ivAvatar = (ImageView) findViewById(R.id.ivAvatarVoice);
		LayoutHelper.scaleWidthAndHeightRelativeLayout(this, 5f, ivAvatar);

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("VOICE");

		TextView tvNameOfUser = (TextView) findViewById(R.id.tvNameOfUserVoice);

		// message from somebody
		new FileDownloadAsync(this).execute(mMessage.getVoiceFileId());

		String idOfUser = mExtras.getString("idOfUser");
		String nameOfUser = mExtras.getString("nameOfUser");

		String avatarId = null;
		try {
			avatarId = new FindAvatarFileIdAsync(this).execute(idOfUser).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		Utils.displayImage(avatarId, ivAvatar, ImageLoader.SMALL, R.drawable.user_stub, false);

		if (mMessage.getBody().equals(null) || mMessage.getBody().equals("")) {
			tvNameOfUser.setText(nameOfUser.toUpperCase(Locale.getDefault())
					+ "'S VOICE");
		} else {
			tvNameOfUser.setText(mMessage.getBody());
		}

		Button back = (Button) findViewById(R.id.btnBack);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void scrollListViewToBottom() {
		mLvComments.post(new Runnable() {
	        @Override
	        public void run() {
	            // Select the last row so it will scroll into view...
	        	mLvComments.setSelection(mLvComments.getCount() - 1);
	        }
	    });
	}

	private void setInitComments() {
		// for comment
		// **********************************************
		mLvComments = (ListView) findViewById(R.id.lvPhotoComments);
		mLvComments.setCacheColorHint(0);

		final EditText etComment = (EditText) findViewById(R.id.etComment);
		etComment.setTypeface(SpikaApp.getTfMyriadPro());

		mMessage = (Message) getIntent().getSerializableExtra("message");

		mComments = new ArrayList<Comment>();

		mRefreshCommentHandler = new RefreshCommentHandler(VoiceActivity.this,
				mMessage, mComments, mCommentsAdapter, mLvComments, 5000);
		mRefreshCommentHandler.startRefreshing();
		new GetCommentsAsync(VoiceActivity.this, mMessage, mComments,
				mCommentsAdapter, mLvComments, mRefreshCommentHandler, true).execute(mMessage.getId());
		scrollListViewToBottom();

		Button btnSendComment = (Button) findViewById(R.id.btnSendComment);
		btnSendComment.setTypeface(SpikaApp.getTfMyriadProBold(),
				Typeface.BOLD);

		btnSendComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String commentText = etComment.getText().toString();
				if (!commentText.equals("")) {
					Comment comment = CommentManagement.createComment(
							commentText, mMessage.getId());
					scrollListViewToBottom();
					new CreateCommentAsync(VoiceActivity.this).execute(
								comment);

					etComment.setText("");
					Utils.hideKeyboard(VoiceActivity.this);

				}

			}
		});

		// **********************************************
	}

	public void setMessageFromAsync(Message message) {
		mMessage = message;
	}

	private void setInitSoundControl() {
		mPbForPlaying = (ProgressBar) findViewById(R.id.pbVoice);
		mPlayPause = (ImageView) findViewById(R.id.ivPlayPause);
		mStopSound = (ImageView) findViewById(R.id.ivStopSound);

		mIsPlaying = 0;

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
				mIsPlaying = 2;

			} catch (IOException e) {
				Log.e("LOG", "prepare() failed");
			}
		} else if (mIsPlaying == 1) {
			mPlayer.start();
			mHandlerForProgressBar.post(mRunnForProgressBar);
			mIsPlaying = 2;
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
		mPbForPlaying.setProgress(0);
		mPlayer = null;
		mIsPlaying = 0;
	}

	private void pausePlaying() {
		mPlayer.pause();
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
		mIsPlaying = 1;
	}

	public void onPause() {
		super.onPause();

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
			mIsPlaying = 0;
			mPbForPlaying.setProgress(0);
			mPlayPause.setImageResource(R.drawable.play_btn);
		}
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
	}

	private class GetVoiceFileAsync extends SpikaAsync<Void, Void, Void> {

		protected GetVoiceFileAsync(Context context) {
			super(context);
		}

		HookUpProgressDialog progressBarLoadingBar = new HookUpProgressDialog(
				VoiceActivity.this);
		boolean isLoaded = false;

		@Override
		protected void onPreExecute() {
			progressBarLoadingBar.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				File file = new File(getHookUpPath(), "voice_download.wav");

				CouchDB.getFile(sFileName, file);

				isLoaded = true;

			} catch (Exception e) {
				Log.e("LOG", e.toString());
				isLoaded = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressBarLoadingBar.dismiss();
			if (isLoaded) {
				sFileName = getHookUpPath().getAbsolutePath()
						+ "/voice_download.wav";
			} else {
				Toast.makeText(VoiceActivity.this,
						"Error in downloading voice...", Toast.LENGTH_LONG)
						.show();
				mPlayPause.setClickable(false);

			}
			super.onPostExecute(result);
		}

	}

	private class FileDownloadAsync extends SpikaAsync<String, Void, Void> {

		protected FileDownloadAsync(Context context) {
			super(context);
		}

		private HookUpProgressDialog mProgressDialog;
		boolean isLoaded = false;

		@Override
		protected void onPreExecute() {
			if (mProgressDialog == null) {
				mProgressDialog = new HookUpProgressDialog(VoiceActivity.this);
			}
			mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {

				File file = new File(getHookUpPath(), "voice_download.wav");

				CouchDB.downloadFile(params[0], file);

				isLoaded = true;

			} catch (Exception e) {
				Log.e("LOG", e.toString());
				isLoaded = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
			if (isLoaded) {
				sFileName = getHookUpPath().getAbsolutePath()
						+ "/voice_download.wav";
			} else {
				Toast.makeText(VoiceActivity.this,
						"Error in downloading voice...", Toast.LENGTH_LONG)
						.show();
				mPlayPause.setClickable(false);

			}
			super.onPostExecute(result);
		}

	}

	private File getHookUpPath() {
		File root = android.os.Environment.getExternalStorageDirectory();

		File dir = new File(root.getAbsolutePath() + "/HookUp");
		if (dir.exists() == false) {
			dir.mkdirs();
		}

		return dir;
	}

	@Override
	protected void onDestroy() {
		mRefreshCommentHandler.stopRefreshing();
		super.onDestroy();
	}
	
	private class CreateCommentAsync extends SpikaAsync<Comment, Void, String> {

		public CreateCommentAsync(Context context) {
			super(context);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Comment... params) {

			String commentId = CouchDB.createComment(params[0]);

			if (commentId != null) {
				if (WallActivity.gCurrentMessages != null) {
					WallActivity.gCurrentMessages.clear();
				}
				WallActivity.gIsRefreshUserProfile = true;
			}

			return commentId;
		}

		@Override
		protected void onPostExecute(String commentId) {
			if (commentId != null) {
				new SendMessageAsync(VoiceActivity.this, SendMessageAsync.TYPE_VOICE).execute(mMessage, false, true);
				new GetCommentsAsync(VoiceActivity.this, mMessage, mComments,
						mCommentsAdapter, mLvComments, mRefreshCommentHandler, true).execute(mMessage.getId());
			}
		}
	}

}
