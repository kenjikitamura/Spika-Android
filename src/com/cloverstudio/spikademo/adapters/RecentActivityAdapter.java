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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.model.RecentActivity;

/**
 * RecentActivityAdapter
 * 
 * Adapter class for recent activity categories in recent activity.
 */

public class RecentActivityAdapter extends BaseAdapter {

	private String TAG = "RecentActivityAdapter";
	private List<RecentActivity> mRecentActivityList = new ArrayList<RecentActivity>();
	private LinearLayout mParentLayout;
	private Activity mActivity;

	public RecentActivityAdapter(Activity activity, LinearLayout parentLayout,
			List<RecentActivity> recentActivityList) {
		mRecentActivityList = (ArrayList<RecentActivity>) recentActivityList;
		mActivity = activity;
		mParentLayout = parentLayout;
		mParentLayout.removeAllViews();
		addViews();
	}

	public void setItems(List<RecentActivity> recentActivityList) {
		mRecentActivityList = (ArrayList<RecentActivity>) recentActivityList;
		notifyDataSetChanged();
	}

	public void addViews() {
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
		if (mRecentActivityList == null) {
			return 0;
		} else {
			return mRecentActivityList.size();
		}
	}

	@Override
	public RecentActivity getItem(int position) {
		if (mRecentActivityList != null) {
			return mRecentActivityList.get(position);
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
				v = li.inflate(R.layout.recent_activity_item, parent, false);
				holder = new ViewHolder();
				holder.tvCategoryTitle = (TextView) v
						.findViewById(R.id.tvCategoryTitle);
				holder.llCategoryNotifications = (LinearLayout) v
						.findViewById(R.id.llCategoryNotifications);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			RecentActivity recentActivity = mRecentActivityList.get(position);
			
			String categoryNameKey = recentActivity.get_name().toUpperCase();
			
			categoryNameKey = categoryNameKey.replace(' ', '_');
			
		    String packageName = mActivity.getPackageName();
		    int resId = mActivity.getResources().getIdentifier(categoryNameKey, "string", packageName);

			String localizedCategoryTitle = mActivity.getString(resId);
			
			if(localizedCategoryTitle != null)
			    holder.tvCategoryTitle.setText(localizedCategoryTitle);
			else
			    holder.tvCategoryTitle.setText(categoryNameKey);
			    
			if (holder.notificationsAdapter == null) {
				holder.notificationsAdapter = new NotificationsAdapter(
						mActivity, holder.llCategoryNotifications,
						recentActivity.getNotifications(),
						recentActivity.getTargetType());
			} else {
				holder.notificationsAdapter.setItems(recentActivity
						.getNotifications());
			}

		} catch (Exception e) {
			Log.e(TAG, "error on inflating recent activity list");
		}

		return v;
	}

	class ViewHolder {
		public TextView tvCategoryTitle;
		public LinearLayout llCategoryNotifications;
		public NotificationsAdapter notificationsAdapter;
	}

}
