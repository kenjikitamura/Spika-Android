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

package com.cloverstudio.spikademo.uitems;

import com.cloverstudio.spikademo.lazy.SetFontFace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * MyriadProTextView
 * 
 * TextView with text in MyriadProRegular font.
 */

public class MyriadProTextView extends TextView {
	
	private String font="fonts/Roboto-Regular.ttf";
	private Typeface mTypeface;
	
	public MyriadProTextView(Context context) {
		super(context);
		mTypeface=SetFontFace.getFont();
		this.setTypeface(mTypeface); 
		if(Build.VERSION.RELEASE.equals("2.3.7")){
			this.setGravity(Gravity.LEFT);
		} 
	}

	public MyriadProTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTypeface=SetFontFace.getFont();
		this.setTypeface(mTypeface);
		if(Build.VERSION.RELEASE.equals("2.3.7")){
			this.setGravity(Gravity.LEFT);
		}
	}

	public MyriadProTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mTypeface=SetFontFace.getFont();
		this.setTypeface(mTypeface);
		if(Build.VERSION.RELEASE.equals("2.3.7")){
			this.setGravity(Gravity.LEFT);
		}
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

	}
}
