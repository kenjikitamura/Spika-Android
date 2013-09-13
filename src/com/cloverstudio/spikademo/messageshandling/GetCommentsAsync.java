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

package com.cloverstudio.spikademo.messageshandling;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.PhotoActivity;
import com.cloverstudio.spikademo.VideoActivity;
import com.cloverstudio.spikademo.VoiceActivity;
import com.cloverstudio.spikademo.adapters.CommentsAdapter;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.Comment;
import com.cloverstudio.spikademo.couchdb.model.Message;

/**
 * GetCommentsAsync
 * 
 * AsyncTask for fetching comments from CouchDB.
 */

public class GetCommentsAsync extends AsyncTask<String, Void, List<Comment>> {

	private Context mContext;
	private Message mMessage;
	private List<Comment> mComments;
	private CommentsAdapter mCommentsAdapter;
	private ListView mCommentListView;
	private RefreshCommentHandler mRefreshCommentHandler;
	private boolean mFirstTime;

	public GetCommentsAsync(Context context, Message message,
			List<Comment> comments, CommentsAdapter commentsAdapter,
			ListView commentListView) {
		mContext = context;
		mMessage = message;
		mComments = comments;
		mCommentsAdapter = commentsAdapter;
		mCommentListView = commentListView;
	}

	public GetCommentsAsync(Context context, Message message,
			List<Comment> comments, CommentsAdapter commentsAdapter,
			ListView commentListView,
			RefreshCommentHandler refreshCommentHandler, boolean firstTime) {
		mContext = context;
		mMessage = message;
		mComments = comments;
		mCommentsAdapter = commentsAdapter;
		mCommentListView = commentListView;
		mRefreshCommentHandler = refreshCommentHandler;
		mFirstTime=firstTime;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected List<Comment> doInBackground(String... params) {

		return CouchDB.findCommentsByMessageId(params[0]);
	}

	@Override
	protected void onPostExecute(List<Comment> result) {

		if (mRefreshCommentHandler != null)
			mRefreshCommentHandler.setComments(result);

		if (result.size() > mComments.size() || mFirstTime) {
			mComments = result;
			if (mCommentsAdapter == null) {
				mCommentsAdapter = new CommentsAdapter(mContext,
						R.layout.comment_item, mComments);
				mCommentListView.setAdapter(mCommentsAdapter);
				mCommentListView.setOnScrollListener(mCommentsAdapter);
			} else {
				mCommentsAdapter.notifyDataSetChanged();
			}
		}

		if (mContext instanceof VoiceActivity) {
			((VoiceActivity) mContext).setMessageFromAsync(mMessage);
		} else if (mContext instanceof VideoActivity) {
			((VideoActivity) mContext).setMessageFromAsync(mMessage);
		} else if (mContext instanceof PhotoActivity) {
			((PhotoActivity) mContext).setMessageFromAsync(mMessage);
			
		}

	}

}
