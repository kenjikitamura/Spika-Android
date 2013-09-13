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

import com.cloverstudio.spikademo.utils.Const;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User
 * 
 * Model class for users.
 */

public class User {
	public User(String mId, String mRev, String mEmail, String mPassword,
			String mType, String mName, long mLastLogin, String mGender,
			long mBirthday, String mAbout, String mAndroidPushToken, String mToken, long mTokenTimestamp, String mOnlineStatus,
			String mAvatarFileId, int mMaxContactCount, int mMaxFavoriteCount,String mAvatarThumbFileId) {
		super();
		this.mId = mId;
		this.mRev = mRev;
		this.mEmail = mEmail;
		this.mPassword = mPassword;
		this.mType = mType;
		this.mName = mName;
		this.mLastLogin = mLastLogin;
		this.mGender = mGender;
		this.mBirthday = mBirthday;
		this.mAbout = mAbout;
		this.mAndroidPushToken = mAndroidPushToken;
		this.mToken = mToken;
		this.mTokenTimestamp = mTokenTimestamp;
		this.mOnlineStatus = mOnlineStatus;
		this.mAvatarFileId = mAvatarFileId;
		this.mMaxContactCount = mMaxContactCount;
		this.mMaxFavoriteCount = mMaxFavoriteCount;
		this.mAvatarThumbFileId = mAvatarThumbFileId;
	}

	@SerializedName("_id")
	@Expose
	private String mId;
	@SerializedName("_rev")
	@Expose
	private String mRev;
	@SerializedName("email")
	@Expose
	private String mEmail;
	@SerializedName("password")
	@Expose
	private String mPassword;
	@SerializedName("type")
	@Expose
	private String mType;
	@SerializedName("name")
	@Expose
	private String mName;
	@SerializedName("last_login")
	@Expose
	private long mLastLogin;
	@SerializedName("gender")
	@Expose
	private String mGender;
	@SerializedName("birthday")
	@Expose
	private long mBirthday;
	@SerializedName("about")
	@Expose
	private String mAbout;
	@SerializedName("android_push_token")
	@Expose
	private String mAndroidPushToken;
	@SerializedName("token")
	@Expose
	private String mToken;
	@SerializedName("token_timestamp")
	@Expose
	private long mTokenTimestamp;
	@SerializedName("online_status")
	@Expose
	private String mOnlineStatus;
	@SerializedName("avatar_file_id")
	@Expose
	private String mAvatarFileId;
	@SerializedName("max_contact_count")
	@Expose
	private int mMaxContactCount;
    @SerializedName("max_favorite_count")
    @Expose
    private int mMaxFavoriteCount;
    @SerializedName("avatar_thumb_file_id")
    @Expose
    private String mAvatarThumbFileId;

	private List<String> mContactIds = new ArrayList<String>();
	private List<String> mGroupIds = new ArrayList<String>();
	private ActivitySummary mActivitySummary = null;

	public User() {
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
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

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String email) {
		this.mEmail = email;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		this.mPassword = password;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		this.mType = type;
	}

	public List<String> getContactIds() {
		return mContactIds;
	}

	public void setContactIds(List<String> contactIds) {
		this.mContactIds = new ArrayList<String>(contactIds);
	}

	public boolean isInContacts(User user) {
		if (!mContactIds.isEmpty()) {
			for (String id : mContactIds) {
				if (id.equals(user.getId()))
					return true;
			}
		}
		return false;
	}

	public void addToContacts(User user) {
		this.mContactIds.add(user.getId());
	}

	public void removeFromContacts(User user) {
		this.mContactIds.remove(user.getId());
	}

	public List<String> getGroupIds() {
		return mGroupIds;
	}

	public void setGroupIds(List<String> groupIds) {
		this.mGroupIds = new ArrayList<String>(groupIds);
	}

	public boolean isInFavoriteGroups(Group group) {
		if (!mGroupIds.isEmpty()) {
			for (String id : mGroupIds) {
				if (id.equals(group.getId()))
					return true;
			}
		}
		return false;
	}

	public void addToFavoriteGroups(Group group) {
		this.mGroupIds.add(group.getId());
	}

	public void removeFromFavoriteGroups(Group group) {
		this.mGroupIds.remove(group.getId());
	}

	public long getLastLogin() {
		return mLastLogin;
	}

	public void setLastLogin(long lastLogin) {
		this.mLastLogin = lastLogin;
	}

	public String getGender() {
		return mGender;
	}

	public void setGender(String gender) {
		this.mGender = gender;
	}

	public long getBirthday() {
		return mBirthday;
	}

	public void setBirthday(long birthday) {
		this.mBirthday = birthday;
	}

	public String getAbout() {
		return mAbout;
	}

	public void setAbout(String about) {
		this.mAbout = about;
	}

	public String getAndroidToken() {
		return mAndroidPushToken;
	}

	public void setAndroidToken(String androidPushToken) {
		this.mAndroidPushToken = androidPushToken;
	}

	public String getToken() {
		return mToken;
	}

	public void setToken(String token) {
		this.mToken = token;
	}

	public long getTokenTimestamp() {
		return mTokenTimestamp;
	}

	public void setTokenTimestamp(long tokenTimestamp) {
		this.mTokenTimestamp = tokenTimestamp;
	}

	public ActivitySummary getActivitySummary() {
		return mActivitySummary;
	}

	public void setActivitySummary(ActivitySummary activitySummary) {
		this.mActivitySummary = activitySummary;
	}

	public String getOnlineStatus() {
		return mOnlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.mOnlineStatus = onlineStatus;
	}

    public String getAvatarThumbFileId() {
        return mAvatarThumbFileId;
    }

    public void setAvatarThumbFileId(String mAvatarThumbFileId) {
        this.mAvatarThumbFileId = mAvatarThumbFileId;
    }

    public String getAvatarFileId() {
        return mAvatarFileId;
    }

    public void setAvatarFileId(String mAvatarFileId) {
        this.mAvatarFileId = mAvatarFileId;
    }

	public int getMaxContactCount() {
		if (mMaxContactCount == 0) {
			return Const.MAX_CONTACTS;
		}
		return mMaxContactCount;
	}

	public void setMaxContactCount(int mMaxContactCount) {
		this.mMaxContactCount = mMaxContactCount;
	}

	public int getMaxFavoriteCount() {
		if (mMaxFavoriteCount == 0) {
			return Const.MAX_FAVORITES;
		}
		return mMaxFavoriteCount;
	}

	public void setMaxFavoriteCount(int mMaxFavoriteCount) {
		this.mMaxFavoriteCount = mMaxFavoriteCount;
	}
	
	public int getContactsCount() {
		return this.mContactIds.size();
	}
	
	public int getFavoritesCount() {
		return this.mGroupIds.size();
	}
	
	public boolean canAddContact() {
		return (getContactsCount() < getMaxContactCount());
	}
	
	public boolean canAddFavorite() {
		return (getFavoritesCount() < getMaxFavoriteCount());
	}

}
