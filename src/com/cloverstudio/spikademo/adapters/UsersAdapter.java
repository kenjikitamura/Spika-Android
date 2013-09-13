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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.MyProfileActivity;
import com.cloverstudio.spikademo.UserProfileActivity;
import com.cloverstudio.spikademo.UsersActivity;
import com.cloverstudio.spikademo.couchdb.model.Notification;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * UsersAdapter
 * 
 * Adapter class for users.
 */

public class UsersAdapter extends BaseAdapter implements OnItemClickListener {

	private String TAG = "UsersAdapter";
	private List<User> mUsers = new ArrayList<User>();
	private List<Notification> mUserNotifications = new ArrayList<Notification>();
	private Activity mActivity;

	public UsersAdapter(Activity activity, List<User> users,
			List<Notification> userNotifications) {
		mUsers = (ArrayList<User>) users;
		mUserNotifications = (ArrayList<Notification>) userNotifications;
		mActivity = activity;
	}

	public void setItems(List<User> users, List<Notification> userNotifications) {
		mUsers = (ArrayList<User>) users;
		mUserNotifications = (ArrayList<Notification>) userNotifications;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mUsers == null) {
			return 0;
		} else {
			return mUsers.size();
		}
	}

	@Override
	public User getItem(int position) {
		if (mUsers != null) {
			return mUsers.get(position);
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
				v = li.inflate(R.layout.user_item, parent, false);
				holder = new ViewHolder();
				holder.ivUserImage = (ImageView) v
						.findViewById(R.id.ivUserImage);
				holder.tvUser = (TextView) v.findViewById(R.id.tvUser);
				holder.tvNotifications = (TextView) v
						.findViewById(R.id.numberOfNotifications);
				holder.rlNotifications = (RelativeLayout) v
						.findViewById(R.id.notificationBalloon);
				holder.pbLoading = (ProgressBar) v
						.findViewById(R.id.pbLoadingForImage);
				holder.pbLoading.setVisibility(View.VISIBLE);
				holder.ivFavorites = (ImageView) v
						.findViewById(R.id.ivFavorites);
				holder.tvMessages = (TextView) v.findViewById(R.id.tvMessages);
				holder.ivOnlineStatus = (ImageView) v
						.findViewById(R.id.ivOnlineStatus);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			User user = mUsers.get(position);

			holder.tvMessages
					.setBackgroundResource(R.drawable.no_messages_icon);
			holder.tvMessages.setText(null);
			if (mUserNotifications != null) {
				for (Notification notification : mUserNotifications) {

					if (notification.getTargetId() != null && notification.getTargetId().equals(user.getId())) {

						boolean hasNotifications = notification.getCount() > 0;
						if (hasNotifications) {
							holder.tvMessages
									.setBackgroundResource(R.drawable.messages_icon);
							holder.tvMessages.setText(Integer
									.toString(notification.getCount()));
						}
					}
				}
			}

			Utils.displayImage(user.getAvatarThumbFileId(), holder.ivUserImage,
					holder.pbLoading, ImageLoader.SMALL, R.drawable.user_stub, false);

			holder.tvUser.setText(user.getName());
			holder.ivFavorites
					.setBackgroundResource(R.drawable.not_in_favorites_icon);
			for (String contactId : UsersManagement.getLoginUser()
					.getContactIds()) {
				if (user.getId().equals(contactId)) {
					holder.ivFavorites
							.setBackgroundResource(R.drawable.favorites_icon);
				}
			}
			if (user.getOnlineStatus() != null) {
				holder.ivOnlineStatus.setVisibility(View.VISIBLE);
				if (user.getOnlineStatus().equals(
						mActivity.getString(R.string.online))) {
					holder.ivOnlineStatus
							.setImageResource(R.drawable.user_online_icon);
				}
				if (user.getOnlineStatus().equals(
						mActivity.getString(R.string.away))) {
					holder.ivOnlineStatus
							.setImageResource(R.drawable.user_away_icon);
				}
				if (user.getOnlineStatus().equals(
						mActivity.getString(R.string.busy))) {
					holder.ivOnlineStatus
							.setImageResource(R.drawable.user_busy_icon);
				}
				if (user.getOnlineStatus().equals(
						mActivity.getString(R.string.offline))) {
					holder.ivOnlineStatus
							.setImageResource(R.drawable.user_offline_icon);
				}
			} else {
				holder.ivOnlineStatus
						.setImageResource(R.drawable.user_offline_icon);
			}

		} catch (Exception e) {
			Log.e(TAG, "error on inflating users");
		}

		return v;
	}

	class ViewHolder {
		public ImageView ivUserImage;
		public TextView tvUser;
		public TextView tvNotifications;
		public RelativeLayout rlNotifications;
		public ProgressBar pbLoading;
		public ImageView ivFavorites;
		public TextView tvMessages;
		public ImageView ivOnlineStatus;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		User user = (User) arg0.getItemAtPosition(arg2);
		Context context = arg0.getContext();
		
		UsersManagement.setToUser(user);
		UsersManagement.setToGroup(null);
		
		boolean isLoginUser = user.getId().equals(UsersManagement.getLoginUser().getId());
		
		if (isLoginUser) {
			((Activity) context).startActivity(new Intent(context,
					MyProfileActivity.class));
		} else {
			((UsersActivity) context).startActivityForResult(new Intent(context,
					UserProfileActivity.class), UsersActivity.REQUEST_UPDATE_USERS);
		}

	}

}
