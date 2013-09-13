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

package com.cloverstudio.spikademo.couchdb.model;

/**
 * ActivitySummary
 * 
 * Model class for user activity summary.
 */

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivitySummary {

	public ActivitySummary(String id, String rev, String type, String userId) {
		super();
		this.mId = id;
		this.mRev = rev;
		this.mType = type;
		this.mUserId = userId;
	}

	@SerializedName("_id")
	@Expose
	private String mId;
	@SerializedName("_rev")
	@Expose
	private String mRev;
	@SerializedName("type")
	@Expose
	private String mType;
	@SerializedName("user_id")
	@Expose
	private String mUserId;

	private int mTotalNotificationCount;
	private List<RecentActivity> mRecentActivityList = new ArrayList<RecentActivity>();

	public ActivitySummary() {
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		this.mId = id;
	}

	public String getRev() {
		return mRev;
	}

	public void setRev(String rev) {
		this.mRev = rev;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		this.mType = type;
	}

	public String getUserId() {
		return mUserId;
	}

	public void setUserId(String userId) {
		this.mUserId = userId;
	}

	public int getTotalNotificationCount() {
		mTotalNotificationCount = 0;
		for (RecentActivity recentActivity : mRecentActivityList) {
			for (Notification notification : recentActivity.getNotifications()) {
				mTotalNotificationCount = mTotalNotificationCount
						+ notification.getCount();
			}
		}
		return mTotalNotificationCount;
	}

	public void setTotalNotificationCount(int totalNotificationCount) {
		this.mTotalNotificationCount = totalNotificationCount;
	}

	public List<RecentActivity> getRecentActivityList() {
		return mRecentActivityList;
	}

	public void setRecentActivityList(List<RecentActivity> recentActivityList) {
		this.mRecentActivityList = recentActivityList;
	}

}
