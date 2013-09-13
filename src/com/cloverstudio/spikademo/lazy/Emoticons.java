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

package com.cloverstudio.spikademo.lazy;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.cloverstudio.spikademo.couchdb.model.Emoticon;

/**
 * Emoticons
 * 
 * Singleton for storing the list of emoticons from database.
 */

public class Emoticons {

	private List<Emoticon> mEmoticons = null;

	public List<Emoticon> getEmoticons() {
		return sInstance.mEmoticons;
	}

	public void setEmoticons(List<Emoticon> emoticons) {
		sInstance.mEmoticons = new ArrayList<Emoticon>(emoticons);
	}

	private static Emoticons sInstance = null;

	private Emoticons() {
	}

	public static Emoticons getInstance() {
		if (sInstance == null) {
			sInstance = new Emoticons();
		}
		return sInstance;
	}
	
	public Emoticon getItem(String identifier) {
		if (!sInstance.mEmoticons.isEmpty()) {
			for (Emoticon emoticon : sInstance.mEmoticons) {
				if (emoticon.getIdentifier().equals(identifier)) {
					return emoticon;
				}
			}
		}
		return null;
	}

	public Bitmap getItemBitmap(String identifier) {
		if (!sInstance.mEmoticons.isEmpty()) {
			for (Emoticon emoticon : sInstance.mEmoticons) {
				if (emoticon.getIdentifier().equals(identifier)) {
					return emoticon.getBitmap();
				}
			}
		}
		return null;
	}
}
