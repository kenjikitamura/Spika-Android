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
import android.os.Handler;
import android.widget.ListView;

import com.cloverstudio.spikademo.adapters.CommentsAdapter;
import com.cloverstudio.spikademo.couchdb.model.Comment;
import com.cloverstudio.spikademo.couchdb.model.Message;

/**
 * RefreshCommentHandler
 * 
 * Refreshes comments continuously with custom time interval.
 */

public class RefreshCommentHandler {
	
	private Handler refreshCommentHandler=new Handler();
	private Runnable refreshCommentRunnable;
	
	private Context mContext;
	private Message mMessage;
	private List<Comment> mComments;
	private CommentsAdapter mCommentsAdapter;
	private ListView mCommentListView;
	private int mDuration=5000;
	
	public RefreshCommentHandler(Context context, Message message, List<Comment> comments, 
			CommentsAdapter commentsAdapter, ListView commentListView, int duration){
		mContext=context;
		mMessage=message;
		mComments=comments;
		mCommentsAdapter=commentsAdapter;
		mCommentListView=commentListView;
		mDuration=duration;
	}
	
	public void setComments(List<Comment> comments) {
		this.stopRefreshing();
		mComments=comments;
		this.startRefreshing();
	}
	
	public void startRefreshing(){
		refreshCommentRunnable=new Runnable() {
			
			@Override
			public void run() {
				new GetCommentsAsync(mContext, mMessage, mComments, mCommentsAdapter, mCommentListView, RefreshCommentHandler.this, false).execute(mMessage.getId());
				refreshCommentHandler.postDelayed(refreshCommentRunnable, mDuration);
			}
		};
		
		refreshCommentHandler.postDelayed(refreshCommentRunnable, mDuration);
	}
	
	public void stopRefreshing(){
		refreshCommentHandler.removeCallbacks(refreshCommentRunnable);
	}

}
