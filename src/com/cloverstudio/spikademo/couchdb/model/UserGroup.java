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
 * UserGroup
 * 
 * Model class for CouchDB user group, used for binding users and their favorite groups.
 */

public class UserGroup {

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
	@SerializedName("user_name")
	@Expose private String mUserName;

	public UserGroup() {
	}
	
	public UserGroup(String id, String rev, String type, String userId,
			String groupId, String userName) {
		super();
		this.mId = id;
		this.mRev = rev;
		this.mType = type;
		this.mUserId = userId;
		this.mGroupId = groupId;
		this.mUserName = userName;
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

	public String getGroupId() {
		return mGroupId;
	}

	public void setGroupId(String groupId) {
		this.mGroupId = groupId;
	}

	public String getUserName() {
		return mUserName;
	}

	public void set_userName(String userName) {
		this.mUserName = userName;
	}

}
