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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * WatchingGroupLog
 * 
 * Model class for CouchDB watching group log, used for detecting which group wall messages need refreshing.
 */

public class WatchingGroupLog {
	
	public WatchingGroupLog() {
		super();
	}
	
	public WatchingGroupLog(String mId, String mRev, String mType,
			String mUserId, String mGroupId, long mCreated) {
		super();
		this.mId = mId;
		this.mRev = mRev;
		this.mType = mType;
		this.mUserId = mUserId;
		this.mGroupId = mGroupId;
		this.mCreated = mCreated;
	}
	
	@SerializedName("_id")
	@Expose private String mId;
	@SerializedName("_rev")
	@Expose private String mRev;
	@SerializedName("type")
	@Expose private String mType;
	@SerializedName("user_id")
	@Expose private String mUserId;
	@SerializedName("group_id")
	@Expose private String mGroupId;
	@SerializedName("created")
	@Expose private long mCreated;
	
	public String getId() {
		return mId;
	}
	public void setId(String mId) {
		this.mId = mId;
	}
	public String getRev() {
		return mRev;
	}
	public void setRev(String mRev) {
		this.mRev = mRev;
	}
	public String getType() {
		return mType;
	}
	public void setType(String mType) {
		this.mType = mType;
	}
	public String getUserId() {
		return mUserId;
	}
	public void setUserId(String mUserId) {
		this.mUserId = mUserId;
	}
	public String getGroupId() {
		return mGroupId;
	}
	public void setGroupId(String mGroupId) {
		this.mGroupId = mGroupId;
	}
	public long getCreated() {
		return mCreated;
	}
	public void setCreated(long mCreated) {
		this.mCreated = mCreated;
	}

}
