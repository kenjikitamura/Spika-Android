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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.messageshandling.FindAvatarFileIdAsync;
import com.cloverstudio.spikademo.messageshandling.GetCommentsAsync;
import com.cloverstudio.spikademo.messageshandling.RefreshCommentHandler;
import com.cloverstudio.spikademo.messageshandling.SendMessageAsync;
import com.cloverstudio.spikademo.utils.LayoutHelper;
import com.cloverstudio.spikademo.utils.Logger;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * VideoActivity
 * 
 * Displays video message and related comments.
 */

public class VideoActivity extends SpikaActivity {

	private static String sFileName = null;

	private VideoView mVideoView;

	private final int VIDEO_IS_PLAYING = 2;
	private final int VIDEO_IS_PAUSED = 1;
	private final int VIDEO_IS_STOPPED = 0;

	private int mIsPlaying = VIDEO_IS_STOPPED; // 0 - play is on stop, 1 - play
												// is on pause, 2
	// - playing

	private ProgressBar mPbForPlaying;
	private ImageView mPlayPause;
	private ImageView mStopVideo;

	private Handler mHandlerForProgressBar = new Handler();
	private Runnable mRunnForProgressBar;

	private long mDurationOfVideo = 0;

	private Message mMessage;
	private ListView mLvComments;

	private RefreshCommentHandler mRefreshCommentHandler;

	private CommentsAdapter mCommentsAdapter;

	private ArrayList<Comment> mComments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_video);

		setGeneralInit();

		setInitComments();

		setHeaderAndAvatar();

		setVideoControl();

	}

	private void setHeaderAndAvatar() {
		findViewById(R.id.btnSend).setVisibility(View.GONE);

		ImageView ivAvatar = (ImageView) findViewById(R.id.ivAvatarVideo);
		LayoutHelper.scaleWidthAndHeightRelativeLayout(this, 5f, ivAvatar);

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("VIDEO");

		TextView tvNameOfUserVideo = (TextView) findViewById(R.id.tvNameOfUserVideo);

		Button back = (Button) findViewById(R.id.btnBack);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Bundle extras = getIntent().getExtras();
		String idOfUser = extras.getString("idOfUser");
		String nameOfUser = extras.getString("nameOfUser");

		String avatarId = null;
		if (extras.getBoolean("videoFromUser")) {
			try {
				avatarId = new FindAvatarFileIdAsync(this).execute(idOfUser)
						.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			avatarId = UsersManagement.getLoginUser().getAvatarFileId();
		}

		Utils.displayImage(avatarId, ivAvatar, ImageLoader.SMALL,
				R.drawable.user_stub, false);

		if (mMessage.getBody().equals(null) || mMessage.getBody().equals("")) {
			tvNameOfUserVideo.setText(nameOfUser.toUpperCase() + "'S VIDEO");
		} else {
			tvNameOfUserVideo.setText(mMessage.getBody());
		}

		new FileDownloadAsync(this).execute(mMessage.getVideoFileId());

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

	private void setGeneralInit() {
		View mTop = getLayoutInflater().inflate(
				R.layout.header_for_listview_in_video_activity, mLvComments,
				false);
		mVideoView = (VideoView) mTop.findViewById(R.id.videoViewForVideo);
		mPlayPause = (ImageView) mTop.findViewById(R.id.ivPlayPause);
		mStopVideo = (ImageView) mTop.findViewById(R.id.ivStopSound);
		mPbForPlaying = (ProgressBar) mTop.findViewById(R.id.pbVoice);
		mLvComments = (ListView) findViewById(R.id.lvPhotoComments);

		LinearLayout relativeLayoutForVideo = (LinearLayout) mTop
				.findViewById(R.id.videoWrapper);
		LayoutParams params = (LayoutParams) relativeLayoutForVideo
				.getLayoutParams();
		// android.widget.LinearLayout.LayoutParams params =
		// (android.widget.LinearLayout.LayoutParams)
		// mVideoView.getLayoutParams();
		params.height = getResources().getDisplayMetrics().heightPixels / 2;
		relativeLayoutForVideo.setLayoutParams(params);
		mVideoView.setBackgroundColor(Color.TRANSPARENT);

		mLvComments.addHeaderView(mTop);

	}

	private void setVideoControl() {
		mIsPlaying = VIDEO_IS_STOPPED;

		mPlayPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsPlaying == VIDEO_IS_PLAYING) {
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

		mStopVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsPlaying == VIDEO_IS_PLAYING
						|| mIsPlaying == VIDEO_IS_PAUSED) {
					// stop
					mPlayPause.setImageResource(R.drawable.play_btn);
					onPlay(2);
				}
			}
		});
	}

	private void setInitComments() {
		// for comment
		// **********************************************

		mLvComments.setSelection(0);
		mLvComments.setCacheColorHint(0);

		final EditText etComment = (EditText) findViewById(R.id.etComment);
		etComment.setTypeface(SpikaApp.getTfMyriadPro());

		mMessage = (Message) getIntent().getSerializableExtra("message");

		mComments = new ArrayList<Comment>();

		mRefreshCommentHandler = new RefreshCommentHandler(VideoActivity.this,
				mMessage, mComments, mCommentsAdapter, mLvComments, 5000);
		mRefreshCommentHandler.startRefreshing();
		new GetCommentsAsync(VideoActivity.this, mMessage, mComments,
				mCommentsAdapter, mLvComments, mRefreshCommentHandler, true)
				.execute(mMessage.getId());
		scrollListViewToBottom();

		final Button btnSendComment = (Button) findViewById(R.id.btnSendComment);
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
					new CreateCommentAsync(VideoActivity.this).execute(comment);

					etComment.setText("");
					Utils.hideKeyboard(VideoActivity.this);
				}

			}
		});

		// **********************************************
	}

	public void setMessageFromAsync(Message message) {
		mMessage = message;
	}

	private void onPlay(int playPauseStop) {
		if (playPauseStop == 0) {
			mRefreshCommentHandler.stopRefreshing();
			startPlaying();

		} else if (playPauseStop == 1) {

			mRefreshCommentHandler.stopRefreshing();
			pausePlaying();

		} else {

			mRefreshCommentHandler.startRefreshing();
			stopPlaying();

		}
	}

	private void startPlaying() {
		if (mIsPlaying == VIDEO_IS_STOPPED) {
			mVideoView.requestFocus();

			 mVideoView.setVideoURI(Uri.parse(sFileName));
			mVideoView.setVideoPath(sFileName);

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
								new Handler().postDelayed(new Runnable() {
									// *******wait for video to finish
									@Override
									public void run() {
										mPlayPause
												.setImageResource(R.drawable.play_btn);
										onPlay(2);
									}
								}, 120);
							}
						}
					};
					mHandlerForProgressBar.post(mRunnForProgressBar);
					mIsPlaying = VIDEO_IS_PLAYING;
				}
			});

		} else if (mIsPlaying == VIDEO_IS_PAUSED) {
			mVideoView.start();
			mHandlerForProgressBar.post(mRunnForProgressBar);
			mIsPlaying = VIDEO_IS_PLAYING;
		}
	}

	private void stopPlaying() {
		mVideoView.stopPlayback();
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
		mPbForPlaying.setProgress(0);
		mIsPlaying = VIDEO_IS_STOPPED;
	}

	private void pausePlaying() {
		mVideoView.pause();
		mHandlerForProgressBar.removeCallbacks(mRunnForProgressBar);
		mIsPlaying = VIDEO_IS_PAUSED;
	}

	private class GetVideoFileAsync extends SpikaAsync<Void, Void, Void> {

		protected GetVideoFileAsync(Context context) {
			super(context);
		}

		HookUpProgressDialog progressBarLoadingBar = new HookUpProgressDialog(
				VideoActivity.this);
		boolean isLoaded = false;

		@Override
		protected void onPreExecute() {
			progressBarLoadingBar.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				File file = new File(getHookUpPath(), "video_download.mp4");

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
						+ "/video_download.mp4";
			} else {
				Toast.makeText(VideoActivity.this,
						"Error in downloading video...", Toast.LENGTH_LONG)
						.show();
				mPlayPause.setClickable(false);
				mStopVideo.setClickable(false);

			}
			super.onPostExecute(result);
		}

		private File getHookUpPath() {
			File root = android.os.Environment.getExternalStorageDirectory();

			File dir = new File(root.getAbsolutePath() + "/HookUp");
			if (dir.exists() == false) {
				dir.mkdirs();
			}

			return dir;
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
				mProgressDialog = new HookUpProgressDialog(VideoActivity.this);
			}
			mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {

				File file = new File(getHookUpPath(), "video_download.mp4");

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
						+ "/video_download.mp4";
			} else {
				Toast.makeText(VideoActivity.this,
						"Error in downloading video...", Toast.LENGTH_LONG)
						.show();
				mPlayPause.setClickable(false);
				mStopVideo.setClickable(false);

			}
			super.onPostExecute(result);
		}

		private File getHookUpPath() {
			File root = android.os.Environment.getExternalStorageDirectory();
			File dir = new File(root.getAbsolutePath() + "/HookUp");
			if (dir.exists() == false) {
				dir.mkdirs();
			}
			return dir;
		}

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
				new SendMessageAsync(VideoActivity.this, SendMessageAsync.TYPE_VIDEO).execute(mMessage, false, true);
				new GetCommentsAsync(VideoActivity.this, mMessage, mComments,
						mCommentsAdapter, mLvComments, mRefreshCommentHandler,
						true).execute(mMessage.getId());
			}
		}
	}

}
