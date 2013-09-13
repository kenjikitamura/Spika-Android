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

/**
 * GroupCategoriesAdapter
 * 
 * Adapter class for group categories.
 */

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
import com.cloverstudio.spikademo.GroupsActivity;
import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.model.Group;
import com.cloverstudio.spikademo.couchdb.model.GroupCategory;
import com.cloverstudio.spikademo.couchdb.model.Notification;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.SettingsManager;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Utils;

public class GroupCategoriesAdapter extends BaseAdapter implements
		OnItemClickListener {

	private String TAG = "GroupCategoriesAdapter";
	private List<GroupCategory> mGroupCategories = new ArrayList<GroupCategory>();
	private Activity mActivity;

	public GroupCategoriesAdapter(Activity activity,
			List<GroupCategory> groupCategories) {
		mGroupCategories = (ArrayList<GroupCategory>) groupCategories;
		mActivity = activity;
	}
	
	public void setItems(List<GroupCategory> groupCategories) {
		mGroupCategories = (ArrayList<GroupCategory>) groupCategories;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mGroupCategories == null) {
			return 0;
		} else {
			return mGroupCategories.size();
		}
	}

	@Override
	public GroupCategory getItem(int position) {
		if (mGroupCategories != null) {
			return mGroupCategories.get(position);
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
				v = li.inflate(R.layout.group_category_item, parent, false);
				holder = new ViewHolder();
				holder.ivGroupCategoryImage = (ImageView) v
						.findViewById(R.id.ivGroupCategoryImage);
				holder.pbLoadingForImage = (ProgressBar) v
						.findViewById(R.id.pbLoadingForImage);
				holder.tvGroupCategory = (TextView) v
						.findViewById(R.id.tvGroupCategory);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			GroupCategory groupCategory = mGroupCategories.get(position);

			Utils.displayImage(groupCategory.getImageUrl(),
					holder.ivGroupCategoryImage, holder.pbLoadingForImage,
					ImageLoader.SMALL, R.drawable.image_stub, true);
			holder.tvGroupCategory.setText(groupCategory.getTitle().toUpperCase());

		} catch (Exception e) {
			Log.e(TAG, "error on inflating group categories");
		}

		return v;
	}

	class ViewHolder {
		public ImageView ivGroupCategoryImage;
		public ProgressBar pbLoadingForImage;
		public TextView tvGroupCategory;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		GroupCategory groupCategory = (GroupCategory) arg0.getItemAtPosition(arg2);
		
		if (mActivity instanceof GroupsActivity) {
			((GroupsActivity) mActivity).getGroupsForCategory(groupCategory);
		}

	}

}
