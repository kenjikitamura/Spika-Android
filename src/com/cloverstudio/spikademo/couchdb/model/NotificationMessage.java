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
 * NotificationMessage
 * 
 * Model class for recent activity notification messages.
 */

public class NotificationMessage {

	public NotificationMessage(String fromUserId, String message,
			String userImageUrl) {
		super();
		this.mFromUserId = fromUserId;
		this.mMessage = message;
		this.mUserImageUrl = userImageUrl;
	}

	@SerializedName("from_user_id")
	@Expose private String mFromUserId;
	@SerializedName("message")
	@Expose private String mMessage;
	@SerializedName("user_image_url")
	@Expose private String mUserImageUrl;
	
	private String mTargetId;
	private int mCount;
	private String mUserAvatarFileId;

	public NotificationMessage() {
	}

	public String getFromUserId() {
		return mFromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.mFromUserId = fromUserId;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String message) {
		this.mMessage = message;
	}

	@Override
	public String toString() {
		return "NotificationMessage [_fromUserId=" + mFromUserId
				+ ", _message=" + mMessage + "]";
	}

	public String getTargetId() {
		return mTargetId;
	}

	public void setTargetId(String targetId) {
		this.mTargetId = targetId;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int count) {
		this.mCount = count;
	}

	public String getUserImageUrl() {
		return mUserImageUrl;
	}

	public void setUserImageUrl(String userImageUrl) {
		this.mUserImageUrl = userImageUrl;
	}

	public String getUserAvatarFileId() {
		return mUserAvatarFileId;
	}

	public void setUserAvatarFileId(String mUserAvatarFileId) {
		this.mUserAvatarFileId = mUserAvatarFileId;
	}

}
