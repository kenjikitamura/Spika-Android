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

import java.io.Serializable;

import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.utils.Const;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Comment
 * 
 * Model class for comments.
 */

public class Comment implements Serializable{
	public Comment(String mId, String mRev, String mComment, long mCreated,
			String mUserName, String mUserId, String mMessageId) {
		super();
		this.mId = mId;
		this.mRev = mRev;
		this.mComment = mComment;
		this.mCreated = mCreated;
		this.mUserName = mUserName;
		this.mUserId = mUserId;
		this.mMessageId = mMessageId;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	@SerializedName("_id")
	@Expose private String mId;
	@SerializedName("_rev")
	@Expose private String mRev;
	@SerializedName("comment")
	@Expose private String mComment;
	@SerializedName("created")
	@Expose private long mCreated;
	@SerializedName("user_name")
	@Expose private String mUserName;
	@SerializedName("user_id")
	@Expose private String mUserId;
	@SerializedName("message_id")
	@Expose private String mMessageId;
	
	public Comment() { }

	
	public String getComment() {
		return mComment;
	}
	public void setComment(String comment) {
		this.mComment = comment;
	}
	public long getCreated() {
		return mCreated;
	}
	public void setCreated(long created) {
		this.mCreated = created;
	}
	public String getUserName() {
		return mUserName;
	}
	public void setUserName(String userName) {
		this.mUserName = userName;
	}
	public String getUserId() {
		return mUserId;
	}
	public void setUserId(String userId) {
		this.mUserId = userId;
	}

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

	public String getMessageId() {
		return mMessageId;
	}

	public void setMessageId(String mMessageId) {
		this.mMessageId = mMessageId;
	}
	
}
