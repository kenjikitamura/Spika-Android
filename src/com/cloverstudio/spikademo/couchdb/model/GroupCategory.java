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
 * GroupCategory
 * 
 * Model class for group categories.
 */

public class GroupCategory {

	public GroupCategory(String mId, String mRev, String mType, String mTitle) {
		super();
		this.mId = mId;
		this.mRev = mRev;
		this.mType = mType;
		this.mTitle = mTitle;
	}

	@SerializedName("_id")
	@Expose private String mId;
	@SerializedName("_rev")
	@Expose private String mRev;
	@SerializedName("type")
	@Expose private String mType;
	@SerializedName("title")
	@Expose private String mTitle;
	
	private List<Attachment> mAttachments = new ArrayList<Attachment>();
	private String mImageUrl = null;

	public GroupCategory() {
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

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public List<Attachment> getAttachments() {
		return mAttachments;
	}

	public void setAttachments(List<Attachment> mAttachments) {
		this.mAttachments = mAttachments;
	}

	public String getImageUrl() {
		return mImageUrl;
	}

	public void setImageUrl(String mImageUrl) {
		this.mImageUrl = mImageUrl;
	}

	@Override
	public String toString() {
		return "GroupCategory [mId=" + mId + ", mRev=" + mRev + ", mType="
				+ mType + ", mTitle=" + mTitle + ", mAttachments="
				+ mAttachments + ", mImageUrl=" + mImageUrl + "]";
	}

}
