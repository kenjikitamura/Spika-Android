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

package com.cloverstudio.spikademo.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.Comment;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.messageshandling.FindAvatarFileIdAsync;
import com.cloverstudio.spikademo.utils.LayoutHelper;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * CommentsAdapter
 * 
 * Adapter class for photo, video and voice comments.
 */

public class CommentsAdapter extends ArrayAdapter<Comment> implements
		OnScrollListener {

	private String TAG = "CommentsAdapter";
	private ArrayList<Comment> mComments = new ArrayList<Comment>();
	private Context mContext;
	private int mCount = 20; /* starting amount */
	private static final int LOAD_MORE = 20;
	private Toast mToast;

	public CommentsAdapter(Context context, int textViewResourceId,
			List<Comment> objects) {
		super(context, textViewResourceId, objects);
		mComments = (ArrayList<Comment>) objects;
		mContext = context;

	}

	@Override
	public int getCount() {
		if (mComments == null) {
			return 0;
		} else {
			if (mComments.size() < mCount)
				return mComments.size();
			else
				return mCount;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		try {

			Comment comment = mComments.get(mComments.size() - getCount()
					+ position);

			if (v == null) {
				LayoutInflater li = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.comment_item, parent, false);
			}

			if (comment != null) {

				RelativeLayout rlFromMe = (RelativeLayout) v
						.findViewById(R.id.rlCommentFromMe);
				RelativeLayout rlToMe = (RelativeLayout) v
						.findViewById(R.id.rlCommentToMe);
				rlFromMe.setVisibility(View.GONE);
				rlToMe.setVisibility(View.GONE);

				if (comment.getUserId().equals(
						UsersManagement.getLoginUser().getId())) {
					TextView tvCommentFromMe = (TextView) v
							.findViewById(R.id.tvCommentFromMe);
					TextView tvSubTextFromMe = (TextView) v
							.findViewById(R.id.tvSubTextFromMe);
					tvCommentFromMe.setText(comment.getComment());
					tvSubTextFromMe.setText(getSubTextDateAndUser(comment));
					ImageButton btnAvatarMe = (ImageButton) v
							.findViewById(R.id.btnAvatarMe);
					LayoutHelper.scaleWidthAndHeightRelativeLayout(mContext,
							5f, btnAvatarMe);

					Utils.displayImage(UsersManagement.getLoginUser()
							.getAvatarFileId(), btnAvatarMe, ImageLoader.SMALL,
							R.drawable.user_stub, false);

					rlFromMe.setVisibility(View.VISIBLE);

				} else {
					TextView tvCommentToMe = (TextView) v
							.findViewById(R.id.tvCommentToMe);
					TextView tvSubTextToMe = (TextView) v
							.findViewById(R.id.tvSubTextToMe);
					tvCommentToMe.setText(comment.getComment());
					tvSubTextToMe.setText(getSubTextDateAndUser(comment));
					ImageButton btnAvatarToMe = (ImageButton) v
							.findViewById(R.id.btnAvatarToMe);
					LayoutHelper.scaleWidthAndHeightRelativeLayout(mContext,
							5f, btnAvatarToMe);

					String avatarFileId = null;
					try {
						avatarFileId = new FindAvatarFileIdAsync(mContext).execute(
								comment.getUserId()).get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}

					Utils.displayImage(avatarFileId, btnAvatarToMe,
							ImageLoader.SMALL, R.drawable.user_stub, false);

					rlToMe.setVisibility(View.VISIBLE);
				}
			}

		} catch (Exception e) {
			Log.e(TAG, "error on inflating items");
		}

		return v;
	}

	private String getSubTextDateAndUser(Comment comment) {
		String subText = null;
		long timeOfCreationOrUpdate = comment.getCreated();
		subText = Utils.getFormattedDateTime(timeOfCreationOrUpdate) + " by "
				+ comment.getUserName();
		return subText;
	}
	
	private HookUpProgressDialog progressDialog;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		boolean loadMore = /* maybe add a padding */
		firstVisibleItem == 1;

		if (loadMore) {
			if (mCount > mComments.size()) {
			} else {
				this.mCount += LOAD_MORE; // or any other amount
				this.notifyDataSetChanged();
				if (mToast == null) {
					mToast = Toast.makeText(mContext, "Load more",
							Toast.LENGTH_SHORT);
				}
				
//				if (progressDialog == null) {
//					progressDialog = new HookUpProgressDialog(mContext);
//				}
				mToast.show();
				view.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
				view.setSelection(LOAD_MORE-(mCount-getCount()));
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
