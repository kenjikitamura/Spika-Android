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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloverstudio.spikademo.R;

/**
 * Tutorial
 * 
 * Animates dialog on the screen with customized tutorial text.
 */

public class Tutorial extends Activity {

	private static Tutorial sInstance = null;

	public static final int SHORT_ANIM_DURATION = 0;
	public static final int MEDIUM_ANIM_DURATION = 1;
	public static final int LONG_ANIM_DURATION = 2;

	private int mAnimationDuration;

	private RelativeLayout mTutorialLayout;
	private Activity mActivity;
	private TextView mTutorialText;

	private static final String TEXT = "tutorial_text";

	private final TranslateAnimation mSlideFromTop = new TranslateAnimation(
			TranslateAnimation.RELATIVE_TO_PARENT, 0,
			TranslateAnimation.RELATIVE_TO_PARENT, 0,
			TranslateAnimation.RELATIVE_TO_SELF, (float) -1.0,
			TranslateAnimation.RELATIVE_TO_SELF, (float) 0.0);

	private final TranslateAnimation mSlideOutBottom = new TranslateAnimation(
			TranslateAnimation.RELATIVE_TO_PARENT, 0,
			TranslateAnimation.RELATIVE_TO_PARENT, 0,
			TranslateAnimation.RELATIVE_TO_SELF, (float) 0.0,
			TranslateAnimation.RELATIVE_TO_PARENT, (float) 1.0);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hookup_tutorial);
		sInstance = this;

		LayoutParams params = getWindow().getAttributes();
		params.height = LayoutParams.MATCH_PARENT;
		params.width = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(
				(android.view.WindowManager.LayoutParams) params);

		setDuration(LONG_ANIM_DURATION);
		mTutorialLayout = (RelativeLayout) findViewById(R.id.rlTutorial);
		mTutorialText = (TextView) findViewById(R.id.tvTutorialText);
		mTutorialText.setText(getIntent().getStringExtra(TEXT));
		setTranslateAnimations();
		startTranslateAnimations();

	}

	public static void show(Activity activity, String tutorialText) {
		
		Intent tutorialIntent = new Intent(activity, Tutorial.class);
		tutorialIntent.putExtra(TEXT, tutorialText);
		activity.startActivity(tutorialIntent);
	}

	private void setDuration(int duration) {
		switch (duration) {
		case SHORT_ANIM_DURATION:
			mAnimationDuration = getResources().getInteger(
					android.R.integer.config_shortAnimTime);
			break;
		case MEDIUM_ANIM_DURATION:
			mAnimationDuration = getResources().getInteger(
					android.R.integer.config_mediumAnimTime);
			break;
		case LONG_ANIM_DURATION:
			mAnimationDuration = getResources().getInteger(
					android.R.integer.config_longAnimTime);
			break;
		default:
			mAnimationDuration = mActivity.getResources().getInteger(
					android.R.integer.config_mediumAnimTime);
			break;
		}
	}


	private void setTranslateAnimations() {
		mSlideFromTop.setFillAfter(false);
		mSlideFromTop.setFillEnabled(false);
		mSlideFromTop.setDuration(mAnimationDuration);
		mSlideFromTop.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				mTutorialLayout.setVisibility(View.VISIBLE);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {

			}
		});

		final Button buttonOk = (Button) findViewById(R.id.btnOk);
		buttonOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mTutorialLayout.startAnimation(mSlideOutBottom);

			}

		});

		mSlideOutBottom.setDuration(mAnimationDuration);
		mSlideOutBottom.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mTutorialLayout.setVisibility(View.GONE);
				sInstance.finish();
			}
		});

	}

	private void startTranslateAnimations() {
		mTutorialLayout.startAnimation(mSlideFromTop);
	}

}
