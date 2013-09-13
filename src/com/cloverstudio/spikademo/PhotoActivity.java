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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.cloverstudio.spikademo.extendables.SpikaActivity;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.CommentManagement;
import com.cloverstudio.spikademo.messageshandling.FindAvatarFileIdAsync;
import com.cloverstudio.spikademo.messageshandling.GetCommentsAsync;
import com.cloverstudio.spikademo.messageshandling.RefreshCommentHandler;
import com.cloverstudio.spikademo.messageshandling.SendMessageAsync;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.LayoutHelper;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * PhotoActivity
 * 
 * Displays photo message and related comments.
 */

public class PhotoActivity extends SpikaActivity {

	private ImageView mIvPhotoImage;
	private EditText mEtComment;
	private Button mBtnSendComment;
	private ImageButton mBtnAvatarUser;
	private TextView mTvPostedBy;

	private ListView mLvComments;
	private Message mMessage;
	private CommentsAdapter mCommentsAdapter;
	private List<Comment> mComments;
	private Button mBtnBack;
	private ProgressBar mPbLoading;

	private RefreshCommentHandler mRefreshCommentHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		mMessage = (Message) getIntent().getSerializableExtra("message");
		
		initialization();
		onClickListeners();

		mComments = new ArrayList<Comment>();
		mRefreshCommentHandler = new RefreshCommentHandler(this, mMessage,
				mComments, mCommentsAdapter, mLvComments, 5000);
		new GetCommentsAsync(PhotoActivity.this, mMessage, mComments,
				mCommentsAdapter, mLvComments, mRefreshCommentHandler, true).execute(mMessage.getId());
		scrollListViewToBottom();
		mRefreshCommentHandler.startRefreshing();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Utils.hideKeyboard(PhotoActivity.this);
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

	private void initialization() {

		mLvComments = (ListView) findViewById(R.id.lvPhotoComments);
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup photoHolder = (ViewGroup) inflater.inflate(
				R.layout.photo_holder, mLvComments, false);
		mLvComments.addHeaderView(photoHolder, null, false);
		mLvComments.setSelection(0);
		mLvComments.setCacheColorHint(0);

		mIvPhotoImage = (ImageView) findViewById(R.id.ivPhotoImage);
		mBtnAvatarUser = (ImageButton) findViewById(R.id.btnAvatarUser);
		mTvPostedBy = (TextView) findViewById(R.id.tvPostedBy);
		mEtComment = (EditText) findViewById(R.id.etComment);
		mEtComment.setTypeface(SpikaApp.getTfMyriadPro());
		mBtnSendComment = (Button) findViewById(R.id.btnSendComment);
		mBtnSendComment.setTypeface(SpikaApp.getTfMyriadProBold(),
				Typeface.BOLD);
		mBtnBack = (Button) findViewById(R.id.btnBack);
		mBtnBack.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);

		mPbLoading = (ProgressBar) findViewById(R.id.pbLoadingForImage);

		if (mMessage != null) {
			Utils.displayImage(
						mMessage.getImageFileId(), mIvPhotoImage, mPbLoading,
						ImageLoader.LARGE, R.drawable.image_stub, false);
			
			LayoutHelper.scaleWidthAndHeightRelativeLayout(this, 5f,
					mBtnAvatarUser);

			String avatarFileId = null;
			try {
				avatarFileId = new FindAvatarFileIdAsync(this).execute(mMessage
						.getFromUserId()).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			Utils.displayImage(avatarFileId,
						mBtnAvatarUser, ImageLoader.SMALL, R.drawable.user_stub, false);

			mTvPostedBy.setText(getSubTextDateAndUser(mMessage));
		}
	}

	private void onClickListeners() {

		mBtnSendComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String commentText = mEtComment.getText().toString();
				if (!commentText.equals("")) {
					Comment comment = CommentManagement
							.createComment(commentText, mMessage.getId());
					scrollListViewToBottom();
					new CreateCommentAsync(PhotoActivity.this).execute(comment);
					
					mEtComment.setText("");
					Utils.hideKeyboard(PhotoActivity.this);
				}

			}
		});

		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PhotoActivity.this.finish();
			}
		});
	}

	public void setMessageFromAsync(Message message) {
		mMessage = message;
	}

	private String getSubTextDateAndUser(Message message) {
		String subText = null;
		long timeOfCreationOrUpdate = message.getCreated();
		subText = Utils.getFormattedDateTime(timeOfCreationOrUpdate) + " by "
				+ message.getFromUserName();
		return subText;
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
				new SendMessageAsync(PhotoActivity.this, SendMessageAsync.TYPE_PHOTO).execute(mMessage, false, true);
				new GetCommentsAsync(PhotoActivity.this, mMessage, mComments,
						mCommentsAdapter, mLvComments, mRefreshCommentHandler, true).execute(mMessage.getId());
			} else {
				Toast.makeText(PhotoActivity.this, "Error with creating comment", Toast.LENGTH_SHORT).show();
			}
		}
	}


}
