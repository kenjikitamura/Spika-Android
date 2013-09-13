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

import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.GroupProfileActivity;
import com.cloverstudio.spikademo.GroupsActivity;
import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.model.Group;
import com.cloverstudio.spikademo.couchdb.model.Notification;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.SettingsManager;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * GroupsAdapter
 * 
 * Adapter class for groups.
 */

public class GroupsAdapter extends BaseAdapter implements OnItemClickListener {

	private String TAG = "GroupsAdapter";
	private List<Group> mGroups = new ArrayList<Group>();
	private List<Notification> mGroupNotifications = new ArrayList<Notification>();
	private Activity mActivity;

	public GroupsAdapter(Activity activity, List<Group> groups,
			List<Notification> groupNotifications) {
		mGroups = (ArrayList<Group>) groups;
		mGroupNotifications = (ArrayList<Notification>) groupNotifications;
		mActivity = activity;
	}

	public void setItems(List<Group> groups,
			List<Notification> groupNotifications) {
		mGroups = (ArrayList<Group>) groups;
		mGroupNotifications = (ArrayList<Notification>) groupNotifications;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		ViewHolder holder = null;

		try {

			if (v == null) {

				LayoutInflater li = (LayoutInflater) mActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.group_item, parent, false);
				holder = new ViewHolder();
				holder.ivGroupImage = (ImageView) v
						.findViewById(R.id.ivGroupImage);
				holder.tvGroup = (TextView) v.findViewById(R.id.tvGroup);
				holder.pbLoading = (ProgressBar) v
						.findViewById(R.id.pbLoadingForImage);
				holder.pbLoading.setVisibility(View.VISIBLE);
				holder.tvNotifications = (TextView) v
						.findViewById(R.id.numberOfNotifications);
				holder.rlNotifications = (RelativeLayout) v
						.findViewById(R.id.notificationBalloon);
				holder.ivFavorites = (ImageView) v
						.findViewById(R.id.ivFavorites);
				holder.tvMessages = (TextView) v.findViewById(R.id.tvMessages);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Group group = mGroups.get(position);

			holder.tvMessages
					.setBackgroundResource(R.drawable.no_messages_icon);
			holder.tvMessages.setText(null);
			if (mGroupNotifications != null) {
				for (Notification notification : mGroupNotifications) {

					if (notification.getTargetId().equals(group.getId())) {

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

			Utils.displayImage(group.getAvatarThumbFileId(), holder.ivGroupImage,
					holder.pbLoading, ImageLoader.SMALL, R.drawable.group_stub,
					false);

			holder.tvGroup.setText(group.getName());

			holder.ivFavorites
					.setBackgroundResource(R.drawable.not_in_favorites_icon);
			for (String groupId : UsersManagement.getLoginUser().getGroupIds()) {
				if (group.getId().equals(groupId)) {
					holder.ivFavorites
							.setBackgroundResource(R.drawable.favorites_icon);
				}

				if (group.getUserId().equals(
						UsersManagement.getLoginUser().getId())) {
					holder.ivFavorites
							.setBackgroundResource(R.drawable.favorites_icon_group);
				}
			}

		} catch (Exception e) {
			Log.e(TAG, "error on inflating groups");
		}

		return v;
	}

	class ViewHolder {
		public ImageView ivGroupImage;
		public TextView tvGroup;
		public TextView tvNotifications;
		public RelativeLayout rlNotifications;
		public ProgressBar pbLoading;
		public ImageView ivFavorites;
		public TextView tvMessages;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Group group = (Group) arg0.getItemAtPosition(arg2);
		Context context = arg0.getContext();

		UsersManagement.setToGroup(group);
		UsersManagement.setToUser(null);

		((GroupsActivity)context).startActivityForResult(new Intent(context,
				GroupProfileActivity.class), GroupsActivity.REQUEST_UPDATE_GROUPS);

	}

	@Override
	public int getCount() {
		if (mGroups == null) {
			return 0;
		} else {
			return mGroups.size();
		}
	}

	@Override
	public Group getItem(int position) {
		if (mGroups != null) {
			return mGroups.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
