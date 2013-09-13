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
 * Attachment
 * 
 * Model class for CouchDB attachments.
 */

public class Attachment {
	@SerializedName("length")
	@Expose private double mLength;
	@SerializedName("content_type")
	@Expose private String mContentType;
	@SerializedName("stub")
	@Expose private boolean mStub;
	@SerializedName("revpos")
	@Expose private double mRevpos;
	
	private String mName = "";

	public Attachment() {
	}

	public Attachment(double length, String contentType, boolean stub, double revpos) {
		super();
		this.mLength = length;
		this.mContentType = contentType;
		this.mStub = stub;
		this.mRevpos = revpos;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public double getLength() {
		return mLength;
	}

	public void setLength(double length) {
		this.mLength = length;
	}

	public String getContentType() {
		return mContentType;
	}

	public void setContentType(String contentType) {
		this.mContentType = contentType;
	}

	public boolean getStub() {
		return mStub;
	}

	public void setStub(boolean stub) {
		this.mStub = stub;
	}

	public double getRevpos() {
		return mRevpos;
	}

	public void setRevpos(double revpos) {
		this.mRevpos = revpos;
	}

	@Override
	public String toString() {
		return "Attachment [mLength=" + mLength + ", mContentType="
				+ mContentType + ", mStub=" + mStub + ", mRevpos=" + mRevpos
				+ ", mName=" + mName + "]";
	}

}
