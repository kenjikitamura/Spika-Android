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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * RecentActivity
 * 
 * Model class for recent activity categories.
 */

public class RecentActivity {

	public RecentActivity(String name, String targetType) {
		super();
		this.mName = name;
		this.mTargetType = targetType;
	}

	public RecentActivity() {
	}

	@SerializedName("name")
	@Expose private String mName;
	@SerializedName("target_type")
	@Expose private String mTargetType;
	
	private List<Notification> mNotifications = new ArrayList<Notification>();
	private int mNotificationCount;

	public String get_name() {
		return mName;
	}

	public void set_name(String name) {
		this.mName = name;
	}

	public String getTargetType() {
		return mTargetType;
	}

	public void set_targetType(String targetType) {
		this.mTargetType = targetType;
	}

	public List<Notification> getNotifications() {
		return mNotifications;
	}

	public void set_notifications(List<Notification> notifications) {
		this.mNotifications = notifications;
	}

	@Override
	public String toString() {
		return "RecentActivity [_name=" + mName + ", _targetType="
				+ mTargetType + ", _notifications=" + mNotifications + "]";
	}

	public int getNotificationCount() {
		mNotificationCount = 0;
		for (Notification notification : this.getNotifications()) {
			mNotificationCount+=notification.getCount();
		}
		return mNotificationCount;
	}

	public void setNotificationCount(int mNotificationCount) {
		this.mNotificationCount = mNotificationCount;
	}

}
