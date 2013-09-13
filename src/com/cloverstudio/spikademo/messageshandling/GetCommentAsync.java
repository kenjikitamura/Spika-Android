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

public class GetCommentAsync extends AsyncTask<String, Void, List<Comment>> {
	
	private Context mContext;
	private Message mMessage;
	private List<Comment> mComments;
	private CommentsAdapter mCommentsAdapter;
	private ListView mCommentListView;
	
	public GetCommentAsync(Context context, Message message, List<Comment> comments, 
			CommentsAdapter commentsAdapter, ListView commentListView){
		mContext=context;
		mMessage=message;
		mComments=comments;
		mCommentsAdapter=commentsAdapter;
		mCommentListView=commentListView;
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
//		mMessage = result;
//		if (mComments == null) {
//			mComments = new ArrayList<Comment>();
//		}
		
		mComments.clear();
		
		for (Comment c : result) {
			mComments.add(c);
		}
		if (mCommentsAdapter == null) {
			mCommentsAdapter = new CommentsAdapter(
					mContext, R.layout.comment_item, mComments);
			mCommentListView.setAdapter(mCommentsAdapter);
		} else {
			mCommentsAdapter.notifyDataSetChanged();
		}
		
		if(mContext instanceof VoiceActivity){
			((VoiceActivity)mContext).setMessageFromAsync(mMessage);
		}else if(mContext instanceof VideoActivity){
			((VideoActivity)mContext).setMessageFromAsync(mMessage);
		}else if(mContext instanceof PhotoActivity){
			((PhotoActivity)mContext).setMessageFromAsync(mMessage);
		}

	}

}
