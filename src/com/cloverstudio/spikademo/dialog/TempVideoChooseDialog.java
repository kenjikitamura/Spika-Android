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

package com.cloverstudio.spikademo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.cloverstudio.spikademo.R;

/**
 * TempVideoChooseDialog
 * 
 * Lets user choose between opening a camera or a video gallery.
 */

public class TempVideoChooseDialog extends Dialog {
	
	public static final int BUTTON_CAMERA=1;
	public static final int BUTTON_GALLERY=2;
	
	private ImageButton mButtonCamera;
	private ImageButton mButtonGallery;

	public TempVideoChooseDialog(final Context context) {
		super(context, R.style.Theme_Transparent);
		
		LinearLayout layout=new LinearLayout(context);
		layout.setBackgroundResource(R.drawable.dialog_bg);
		layout.setGravity(Gravity.CENTER);
		
		mButtonCamera=new ImageButton(context);
		mButtonCamera.setImageResource(R.drawable.camera_more_icon_selector);
		mButtonCamera.setBackgroundResource(0);
		
		mButtonGallery=new ImageButton(context);
		mButtonGallery.setImageResource(R.drawable.albums_more_icon_selector);
		mButtonGallery.setBackgroundResource(0);
		
		layout.addView(mButtonCamera);
		layout.addView(mButtonGallery);
		
		this.setContentView(layout);
		
		LayoutParams params=(LayoutParams) mButtonCamera.getLayoutParams();
		params.setMargins(0, 0, 50, 0);
		mButtonCamera.setLayoutParams(params);
		
	}
	
	public void setOnButtonClickListener(final int button,
			final View.OnClickListener clickListener) {
		switch (button) {
		case BUTTON_CAMERA:
			mButtonCamera.setOnClickListener(clickListener);
			break;
		case BUTTON_GALLERY:
			mButtonGallery.setOnClickListener(clickListener);
			break;
		default:
			break;

		}
	}

}
