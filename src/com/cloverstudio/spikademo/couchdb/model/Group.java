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
 * Group
 * 
 * Model class for groups.
 */

public class Group {

	@SerializedName("_key")
	@Expose private String mKey;
	@SerializedName("_id")
	@Expose private String mId;
	@SerializedName("_rev")
	@Expose private String mRev;
	@SerializedName("type")
	@Expose private String mType;
	@SerializedName("name")
	@Expose private String mName;
	@SerializedName("group_password")
	@Expose private String mPassword;
	@SerializedName("user_id")
	@Expose private String mUserId;
	@SerializedName("description")
	@Expose private String mDescription;
	@SerializedName("avatar_file_id")
	@Expose private String mAvatarFileId;
    @SerializedName("avatar_thumb_file_id")
    @Expose private String mAvatarThumbFileId;
	@SerializedName("category_id")
	@Expose private String mCategoryId;
	@SerializedName("category_name")
	@Expose private String mCategoryName;
	@SerializedName("deleted")
	@Expose private boolean mDeleted;

	public Group() {
	}

	public Group(String key, String id, String rev, String type,
			String name, String password, String userId, String description, String avatarFileId, String categoryId, String categoryName, boolean deleted, String avatarThumbFileId) {
		super();
		this.mKey = key;
		this.mId = id;
		this.mRev = rev;
		this.mType = type;
		this.mName = name;
		this.mPassword = password;
		this.mUserId = userId;
		this.mDescription = description;
		this.mAvatarFileId = avatarFileId;
		this.mCategoryId = categoryId;
		this.mCategoryName = categoryName;
		this.mDeleted = deleted;
		this.mAvatarThumbFileId = avatarThumbFileId;
	}

	@Override
	public String toString() {
		return mName.toString();
	}

	public String getKey() {
		return mKey;
	}

	public void setKey(String key) {
		this.mKey = key;
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

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		this.mPassword = password;
	}

	public String getUserId() {
		return mUserId;
	}

	public void setUserId(String userId) {
		this.mUserId = userId;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		this.mDescription = description;
	}
	
	public String getAvatarFileId() {
		return mAvatarFileId;
	}

	public void setAvatarFileId(String mAvatarFileId) {
		this.mAvatarFileId = mAvatarFileId;
	}
    
    public String getAvatarThumbFileId() {
        return mAvatarThumbFileId;
    }

    public void setAvatarThumbFileId(String mAvatarThumbFileId) {
        this.mAvatarThumbFileId = mAvatarThumbFileId;
    }

	public String getCategoryId() {
		return mCategoryId;
	}

	public void setCategoryId(String mCategoryId) {
		this.mCategoryId = mCategoryId;
	}

	public String getCategoryName() {
		return mCategoryName;
	}

	public void setCategoryName(String mCategoryName) {
		this.mCategoryName = mCategoryName;
	}

	public boolean isDeleted() {
		return mDeleted;
	}

	public void setDeleted(boolean mDeleted) {
		this.mDeleted = mDeleted;
	}

}
