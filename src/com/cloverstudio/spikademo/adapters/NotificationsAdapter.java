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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.SpikaApp;
import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.Group;
import com.cloverstudio.spikademo.couchdb.model.Notification;
import com.cloverstudio.spikademo.couchdb.model.NotificationMessage;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.SettingsManager;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.messageshandling.FindAvatarFileIdAsync;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * NotificationsAdapter
 * 
 * Adapter class for notifications in recent activity.
 */

public class NotificationsAdapter extends BaseAdapter implements
		OnClickListener {

	private String TAG = "NotificationsAdapter";
	private List<Notification> mNotifications = new ArrayList<Notification>();
	private List<NotificationMessage> mAllMessages = new ArrayList<NotificationMessage>();
	private LinearLayout mParentLayout;
	private Activity mActivity;
	private String mTargetType;
	private final int MAX_SIZE = 10;

	public NotificationsAdapter(Activity activity, LinearLayout parentLayout,
			List<Notification> notifications, String targetType) {
		mNotifications = (ArrayList<Notification>) notifications;
		mActivity = activity;
		mTargetType = targetType;
		mParentLayout = parentLayout;
		for (Notification notification : mNotifications) {
			for (NotificationMessage message : notification.getMessages()) {
				message.setTargetId(notification.getTargetId());
				message.setCount(notification.getCount());
				mAllMessages.add(message);
			}
		}
		addViews();
	}

	public void setItems(List<Notification> notifications) {
		mNotifications = (ArrayList<Notification>) notifications;
		mAllMessages = new ArrayList<NotificationMessage>();
		for (Notification notification : mNotifications) {
			for (NotificationMessage message : notification.getMessages()) {
				message.setTargetId(notification.getTargetId());
				message.setCount(notification.getCount());
				mAllMessages.add(message);
			}
		}
		notifyDataSetChanged();
	}

	private void addViews() {
		for (int i = 0; i < this.getCount(); i++) {
			mParentLayout.addView(getView(i, null, null));
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mParentLayout.removeAllViews();
		addViews();
	}

	@Override
	public int getCount() {
		if (mAllMessages == null) {
			return 0;
		} else {
			int count = mAllMessages.size();
			if (count > MAX_SIZE) {
				count = MAX_SIZE;
			}
			return count;
		}
	}

	@Override
	public NotificationMessage getItem(int position) {
		if (mAllMessages != null) {
			return mAllMessages.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		ViewHolder holder = null;
		try {

			if (v == null) {
				LayoutInflater li = (LayoutInflater) mActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.notification_item, parent, false);

				final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 100);
				params.setMargins(0, 2, 0, 2);
				v.setLayoutParams(params);
				holder = new ViewHolder();
				holder.ivImage = (ImageView) v.findViewById(R.id.ivImage);
				holder.tvMessage = (TextView) v.findViewById(R.id.tvMessage);
				holder.pbLoading = (ProgressBar) v
						.findViewById(R.id.pbLoadingForImage);
				holder.rlNotificationsNumber = (RelativeLayout) v
						.findViewById(R.id.rlNotificationsNumber);
				holder.tvNotificationsNumber = (TextView) v
						.findViewById(R.id.tvNotificationsNumber);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			NotificationMessage notificationMessage = mAllMessages
					.get(position);
			holder.position = position;

			if (notificationMessage.getCount() > 1) {
				holder.rlNotificationsNumber.setVisibility(View.VISIBLE);
				holder.tvNotificationsNumber.setText(Integer
						.toString(notificationMessage.getCount()));
			} else {
				holder.rlNotificationsNumber.setVisibility(View.INVISIBLE);
			}

			int stubId = R.drawable.image_stub;
			if (mTargetType.equals(Const.USER)) {
				stubId = R.drawable.user_stub;
			}
			if (mTargetType.equals(Const.GROUP)) {
				stubId = R.drawable.group_stub;
			}
			Utils.displayImage(notificationMessage.getUserAvatarFileId(),
					holder.ivImage, holder.pbLoading, ImageLoader.SMALL, stubId, false);

			holder.tvMessage.setText(notificationMessage.getMessage());
			v.setOnClickListener(this);

		} catch (Exception e) {
			Log.e(TAG, "error on inflating notifications");
		}

		return v;
	}

	class ViewHolder {
		public ImageView ivImage;
		public TextView tvMessage;
		public ProgressBar pbLoading;
		public RelativeLayout rlNotificationsNumber;
		public TextView tvNotificationsNumber;
		public int position;
	}

	@Override
	public void onClick(View v) {
		ViewHolder holder = (ViewHolder) v.getTag();
		NotificationMessage message = getItem(holder.position);

		if (mTargetType.equals(Const.USER)) {
			try {
				User fromUser = new GetUserByIdAsync(mActivity).execute(
						message.getTargetId()).get();
				UsersManagement.setToUser(fromUser);
				UsersManagement.setToGroup(null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		if (mTargetType.equals(Const.GROUP)) {
			try {
				Group fromGroup = new GetGroupByIdAsync(mActivity).execute(
						message.getTargetId()).get();
				UsersManagement.setToUser(null);
				UsersManagement.setToGroup(fromGroup);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		}
		SettingsManager.ResetSettings();
		if (WallActivity.gCurrentMessages != null) {
			WallActivity.gCurrentMessages.clear();
		}
		mActivity.startActivity(new Intent(mActivity, WallActivity.class));

	}

	private class GetUserByIdAsync extends SpikaAsync<String, Void, User> {

		protected GetUserByIdAsync(Context context) {
			super(context);
		}

		@Override
		protected User doInBackground(String... params) {
			String userId = params[0];
			return CouchDB.findUserById(userId);
		}

		@Override
		protected void onPostExecute(User user) {
			super.onPostExecute(user);
		}

	}

	private class GetGroupByIdAsync extends SpikaAsync<String, Void, Group> {

		protected GetGroupByIdAsync(Context context) {
			super(context);
		}

		@Override
		protected Group doInBackground(String... params) {
			String id = params[0];
			return CouchDB.findGroupById(id);
		}

		@Override
		protected void onPostExecute(Group group) {
			super.onPostExecute(group);
		}
	}

}
