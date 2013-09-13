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
import java.util.ArrayList;
import java.util.List;

import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Message
 * 
 * Model class for messages.
 */

public class Message implements Comparable<Message>, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("_id")
	@Expose private String mId;
	@SerializedName("_rev")
	@Expose private String mRev;
	@SerializedName("type")
	@Expose private String mType;
	@SerializedName("message_type")
	@Expose private String mMessageType;
	@SerializedName("message_target_type")
	@Expose private String mMessageTargetType;
	@SerializedName("body")
	@Expose private String mBody;
	@SerializedName("from_user_id")
	@Expose private String mFromUserId;
	@SerializedName("from_user_name")
	@Expose private String mFromUserName;
	@SerializedName("to_user_id")
	@Expose private String mToUserId;
	@SerializedName("to_user_name")
	@Expose private String mToUserName;
	@SerializedName("to_group_id")
	@Expose private String mToGroupId;
	@SerializedName("to_group_name")
	@Expose private String mToGroupName;
	@SerializedName("created")
	@Expose private long mCreated;
	@SerializedName("modified")
	@Expose private long mModified;
	@SerializedName("valid")
	@Expose private boolean mValid;
	@SerializedName("_attachments")
	@Expose private String mAttachments;
	
	@SerializedName("latitude")
	@Expose private String mLatitude;
	@SerializedName("longitude")
	@Expose private String mLongitude;
	
    @SerializedName("picture_file_id")
    @Expose private String mImageFileId;
    @SerializedName("picture_thumb_file_id")
    @Expose private String mImageThumbFileId;
	@SerializedName("video_file_id")
	@Expose private String mVideoFileId;
	@SerializedName("voice_file_id")
	@Expose private String mVoiceFileId;
    @SerializedName("emoticon_image_url")
    @Expose private String mEmoticonImageUrl;
    @SerializedName("message_url")
    @Expose private String mMessageUrl;
	
	
	private int mCommentCount = 0;
	
	private String mUserAvatarFileId;
	
	public Message() {}
		
	public Message(String id, String rev, String type, String messageType,
			String messageTargetType, String body, String fromUserId, 
			String fromUserName, String toUserId, String toUserName,
			String toGroupId, String toGroupName, long created, long modified, boolean valid, String attachments,
			String latitude, String longitude, String imageFileId, String voiceFileId, String videoFileId, String emoticonImageUrl,String pictureThumbFileId,String messageUrl) {
		this.mId = id;
		this.mRev = rev;
		this.mType = type;
		this.mMessageType = messageType;
		this.mMessageTargetType = messageTargetType;
		this.mBody = body;
		this.mFromUserId = fromUserId;
		this.mToUserId = toUserId;
		this.mToUserName = toUserName;
		this.mToGroupId = toGroupId;
		this.mToGroupName = toGroupName;
		this.mCreated = created;
		this.mModified = modified;
		this.mValid = valid;
		this.mAttachments = attachments;
		this.mFromUserName = fromUserName;
		this.mLatitude =latitude;
		this.mLongitude =longitude;
		this.mImageFileId = imageFileId;
		this.mVoiceFileId = voiceFileId;
		this.mVideoFileId = videoFileId;
		this.mEmoticonImageUrl = emoticonImageUrl;
		this.mImageThumbFileId = pictureThumbFileId;
		this.mMessageUrl = messageUrl;
	}

	
	public boolean equals(Message m){
		
		boolean retVal = false;
		
		if(this.getId().equals(m.getId())/* && this.get_modified() == m.get_modified()*/){
			retVal = true;
		}
		
		return retVal;
	}
	
	public boolean isUpdated(Message m){
		boolean retVal = false;
		
		if(this.getModified() < m.getModified()){
			retVal = true;
		}
		
		return retVal;
	}
	
	public String getFromUserName() {
		return mFromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.mFromUserName = fromUserName;
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

	public String getMessageType() {
		return mMessageType;
	}

	public void setMessageType(String messageType) {
		this.mMessageType = messageType;
	}

	public String getMessageTargetType() {
		return mMessageTargetType;
	}

	public void setMessageTargetType(String messageTargetType) {
		this.mMessageTargetType = messageTargetType;
	}

	public String getBody() {
		return mBody;
	}

	public void setBody(String body) {
		this.mBody = body;
	}

	public String getFromUserId() {
		return mFromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.mFromUserId = fromUserId;
	}

	public String getToUserId() {
		return mToUserId;
	}

	public void setToUserId(String toUserId) {
		this.mToUserId = toUserId;
	}

	public String getToUserName() {
		return mToUserName;
	}

	public void setToUserName(String toUserName) {
		this.mToUserName = toUserName;
	}

	public String getToGroupId() {
		return mToGroupId;
	}

	public void setToGroupId(String toGroupId) {
		this.mToGroupId = toGroupId;
	}

	public String getToGroupName() {
		return mToGroupName;
	}

	public void setToGroupName(String toGroupName) {
		this.mToGroupName = toGroupName;
	}

	public long getCreated() {
		return mCreated;
	}

	public void setCreated(long created) {
		this.mCreated = created;
	}

	public long getModified() {
		return mModified;
	}

	public void setModified(long modified) {
		this.mModified = modified;
	}


	public boolean isValid() {
		return mValid;
	}

	public void setValid(boolean valid) {
		this.mValid = valid;
	}

	public String getAttachments() {
		return mAttachments;
	}

	public void setAttachments(String attachments) {
		this.mAttachments = attachments;
	}
	
	public String getLatitude() {
	    return mLatitude;
	}

	public void setLatitude(String latitude) {
	    this.mLatitude = latitude;
	}

	public String getLongitude() {
	    return mLongitude;
	}

	public void setLongitude(String longitude) {
	    this.mLongitude = longitude;
	}

	@Override
	public int compareTo(Message another) {
		if(this.getModified() < another.getModified()){
			return -1;
		}else if(this.getModified() > another.getModified()){
			return 1;
		}else{
			return 0;
		}
	}
	

	public int getCommentCount() {
		return mCommentCount;
	}

	public void setCommentCount(int mCommentCount) {
		this.mCommentCount = mCommentCount;
	}

    public String getImageFileId() {
        return mImageFileId;
    }

    public void setImageFileId(String mImageFileId) {
        this.mImageFileId = mImageFileId;
    }

    public String getImageThumbFileId() {
        return mImageThumbFileId;
    }

    public void setImageThumbFileId(String mImageThumbFileId) {
        this.mImageThumbFileId = mImageThumbFileId;
    }

	public String getVideoFileId() {
		return mVideoFileId;
	}

	public void setVideoFileId(String mVideoFileId) {
		this.mVideoFileId = mVideoFileId;
	}

	public String getVoiceFileId() {
		return mVoiceFileId;
	}

	public void setVoiceFileId(String mVoiceFileId) {
		this.mVoiceFileId = mVoiceFileId;
	}

	public String getUserAvatarFileId() {
		return mUserAvatarFileId;
	}

	public void setUserAvatarFileId(String mUserAvatarFileId) {
		this.mUserAvatarFileId = mUserAvatarFileId;
	}

	public String getEmoticonImageUrl() {
		return mEmoticonImageUrl;
	}

	public void setEmoticonImageUrl(String mEmoticonImageUrl) {
		this.mEmoticonImageUrl = mEmoticonImageUrl;
	}

    public String getMessageUrl() {
        return mMessageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.mMessageUrl = messageUrl;
    }
    
}
