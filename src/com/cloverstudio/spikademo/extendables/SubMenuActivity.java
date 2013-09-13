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

package com.cloverstudio.spikademo.extendables;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.SpikaApp;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * SubMenuActivity
 * 
 * Handles open/close actions and animations for submenu.
 */

public class SubMenuActivity extends SideBarActivity {

	private ImageButton mBtnOpenSubMenu;
	protected boolean mSubMenuOpened;
	private RelativeLayout mRlSubMenuHolder;
	private TranslateAnimation mSlideOutLeft;
	private TranslateAnimation mSlideOutRight;
	private TranslateAnimation mSlideInLeft;
	private TranslateAnimation mSlideInRight;

	protected void setSubMenu() {
		mBtnOpenSubMenu = (ImageButton) findViewById(R.id.btnOpenSubMenu);
		mBtnOpenSubMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSubMenuOpened) {
					closeSubMenu();
				} else {
					openSubMenu();
				}

			}
		});

		mRlTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mSubMenuOpened) {
					closeSubMenu();
				} else if (mSideBarOpened) {
					closeSideBar();
				}

			}
		});

		mRlSubMenuHolder = (RelativeLayout) findViewById(R.id.rlSubMenuHolder);
		mRlSubMenuHolder.setVisibility(View.INVISIBLE);
		mRlSubMenuHolder.getLayoutParams().width = SpikaApp.getTransport();
		mRlSubMenuHolder.setClickable(false);

		final LinearInterpolator linearInterpolator = new LinearInterpolator();
		final int slidingDuration = getResources().getInteger(
				R.integer.sliding_duration);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		mSlideInLeft = new TranslateAnimation(-SpikaApp.getTransport(), 0, 0,
				0);
		mSlideInLeft.setDuration(slidingDuration);
		mSlideInLeft.setInterpolator(linearInterpolator);
		mSlideInLeft.setFillAfter(true);
		mSlideInLeft.setFillEnabled(true);

		mSlideOutRight = new TranslateAnimation(metrics.widthPixels
				- SpikaApp.getTransport(), metrics.widthPixels, 0, 0);
		mSlideOutRight.setDuration(slidingDuration);
		mSlideOutRight.setInterpolator(linearInterpolator);

		mSlideOutLeft = new TranslateAnimation(0, -SpikaApp.getTransport(), 0,
				0);
		mSlideOutLeft.setDuration(slidingDuration);
		mSlideOutLeft.setInterpolator(linearInterpolator);
		mSlideOutLeft.setFillAfter(true);
		mSlideOutLeft.setFillEnabled(true);

		mSlideInRight = new TranslateAnimation(metrics.widthPixels,
				metrics.widthPixels - SpikaApp.getTransport(), 0, 0);
		mSlideInRight.setDuration(slidingDuration);
		mSlideInRight.setInterpolator(linearInterpolator);
		// mSlideInRight.setFillEnabled(true);

		mSubMenuOpened = false;

	}

	protected void closeSubMenu() {

		if (mRlSubMenuHolder.getVisibility() == View.VISIBLE) {

			mBtnOpenSideBar.setEnabled(true);
			mBtnOpenSideBar.setVisibility(View.VISIBLE);
			mBtnOpenSubMenu.setImageResource(R.drawable.submenu_button_off);
			mRlBody.bringToFront();
			mRlSubMenuHolder.startAnimation(mSlideOutRight);
			mRlBody.startAnimation(mSlideInLeft);
			mRlSubMenuHolder.setVisibility(View.INVISIBLE);
			mSubMenuOpened = false;
		}
		
		enableViews();

	}

	protected void openSubMenu() {

		mRlSideBarHolder.setVisibility(View.GONE);
		mRlSubMenuHolder.setVisibility(View.VISIBLE);
		mRlSubMenuHolder.bringToFront();
		mRlSubMenuHolder.setClickable(true);
		mBtnOpenSubMenu.setImageResource(R.drawable.submenu_button_on);
		mRlSubMenuHolder.startAnimation(mSlideInRight);
		mRlBody.startAnimation(mSlideOutLeft);
		mSubMenuOpened = true;
		mBtnOpenSideBar.setEnabled(false);
		mBtnOpenSideBar.setVisibility(View.GONE);
		
		disableViews();

	}

	@Override
	protected void enableViews() {
		super.enableViews();
		mBtnOpenSubMenu.setVisibility(View.VISIBLE);
		mBtnOpenSubMenu.setEnabled(true);
	}

	@Override
	protected void disableViews() {
		super.disableViews();

		if (mSideBarOpened && !mSubMenuOpened) {
			mBtnOpenSubMenu.setEnabled(false);
			mBtnOpenSubMenu.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mSideBarOpened) {
				closeSideBar();
				return true;
			}
			if (mSubMenuOpened) {
				closeSubMenu();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
