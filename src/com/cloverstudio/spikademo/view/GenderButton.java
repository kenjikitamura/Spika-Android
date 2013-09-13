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

package com.cloverstudio.spikademo.view;

import com.cloverstudio.spikademo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * GenderButton
 * 
 * Customized version of radio button for gender in user search.
 */

public class GenderButton extends Button {
	
	public GenderButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GenderButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private boolean mChecked;
	private ButtonType mType;

	public GenderButton(Context context) {
		super(context);
	}

	public boolean isChecked() {
		return mChecked;
	}

	public void setChecked(boolean mChecked) {
		this.mChecked = mChecked;
		if (mChecked == true) {
			switch(mType) {
			case LEFT: this.setBackgroundResource(R.drawable.gender_button_left_on);
				break;
			case MIDDLE: this.setBackgroundResource(R.drawable.gender_button_middle_on);
				break;
			case RIGHT: this.setBackgroundResource(R.drawable.gender_button_right_on);
				break;
			}
		} else {
			switch(mType) {
			case LEFT: this.setBackgroundResource(R.drawable.gender_button_left_off);
				break;
			case MIDDLE: this.setBackgroundResource(R.drawable.gender_button_middle_off);
				break;
			case RIGHT: this.setBackgroundResource(R.drawable.gender_button_right_off);
				break;
			}
		}
	}
	
	public static enum ButtonType {
		LEFT, MIDDLE, RIGHT
	}

	public ButtonType getType() {
		return mType;
	}

	public void setType(ButtonType mType) {
		this.mType = mType;
	}

}
