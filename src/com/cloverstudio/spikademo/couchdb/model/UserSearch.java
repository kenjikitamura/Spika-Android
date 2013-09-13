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

/**
 * UserSearch
 * 
 * Model class that holds parameters for conducting a user search.
 */

public class UserSearch {
	
	public UserSearch(String mName, String mFromAge, String mToAge, String mGender, String mOnlineStatus) {
		super();
		this.mName = mName;
		this.mFromAge = mFromAge;
		this.mToAge = mToAge;
		this.mGender = mGender;
		this.mOnlineStatus = mOnlineStatus;
	}
	public UserSearch() {
		// TODO Auto-generated constructor stub
	}
	private String mName;
	private String mFromAge;
	private String mToAge;
	private String mGender;
	private String mOnlineStatus;
	
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public String getFromAge() {
		return mFromAge;
	}
	public void setFromAge(String mFromAge) {
		this.mFromAge = mFromAge;
	}
	public String getToAge() {
		return mToAge;
	}
	public void setToAge(String mToAge) {
		this.mToAge = mToAge;
	}
	public String getGender() {
		return mGender;
	}
	public void setGender(String mGender) {
		this.mGender = mGender;
	}
	public String getOnlineStatus() {
		return mOnlineStatus;
	}
	public void setOnlineStatus(String mOnlineStatus) {
		this.mOnlineStatus = mOnlineStatus;
	}

}
