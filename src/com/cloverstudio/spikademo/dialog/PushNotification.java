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

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.model.Group;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.SettingsManager;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * PushNotification
 * 
 * Animates push notification on the top of the screen.
 */

public class PushNotification {

	private static PushNotification sInstance = new PushNotification();

	public static final int SHORT_ANIM_DURATION = 0;
	public static final int MEDIUM_ANIM_DURATION = 1;
	public static final int LONG_ANIM_DURATION = 2;

	private int mShowingDuration = 4000;
	private int mAnimationDuration;

	private RelativeLayout mPushLayout;
	private User mFromUser;
	private Group mFromGroup;
	private String mFromType;
	private Context mContext;

	private final TranslateAnimation mSlideFromTop = new TranslateAnimation(
			TranslateAnimation.RELATIVE_TO_PARENT, 0,
			TranslateAnimation.RELATIVE_TO_PARENT, 0,
			TranslateAnimation.RELATIVE_TO_SELF, (float) -1.0,
			TranslateAnimation.RELATIVE_TO_SELF, (float) 0);

	private final TranslateAnimation mSlideOutTop = new TranslateAnimation(
			TranslateAnimation.RELATIVE_TO_PARENT, 0,
			TranslateAnimation.RELATIVE_TO_PARENT, 0,
			TranslateAnimation.RELATIVE_TO_SELF, (float) 0,
			TranslateAnimation.RELATIVE_TO_SELF, (float) -1.0);

	private final TranslateAnimation mSlideOutTopOnClose = new TranslateAnimation(
			TranslateAnimation.RELATIVE_TO_PARENT, 0,
			TranslateAnimation.RELATIVE_TO_PARENT, 0,
			TranslateAnimation.RELATIVE_TO_SELF, (float) 0,
			TranslateAnimation.RELATIVE_TO_SELF, (float) -1.0);

	private PushNotification() {
	}

	/**
	 * 
	 * @param context
	 * @param layout
	 * @param message
	 * @param fromUser
	 */
	public static void show(Context context, RelativeLayout layout,
			String message, User fromUser, Group fromGroup, String fromType) {
		sInstance.mFromUser = fromUser;
		sInstance.mFromGroup = fromGroup;
		sInstance.mFromType = fromType;
		sInstance.mContext = context;
		sInstance.mPushLayout = layout;
		sInstance.showNotification(message);

	}

	private void showNotification(String message) {
		setDuration(MEDIUM_ANIM_DURATION);
		addView(message);
		setTranslateAnimations();
		startTranslateAnimations();
	}

	private void setDuration(int duration) {
		switch (duration) {
		case SHORT_ANIM_DURATION:
			mAnimationDuration = mContext.getResources().getInteger(
					android.R.integer.config_shortAnimTime);
			break;
		case MEDIUM_ANIM_DURATION:
			mAnimationDuration = mContext.getResources().getInteger(
					android.R.integer.config_mediumAnimTime);
			break;
		case LONG_ANIM_DURATION:
			mAnimationDuration = mContext.getResources().getInteger(
					android.R.integer.config_longAnimTime);
			break;
		default:
			mAnimationDuration = mContext.getResources().getInteger(
					android.R.integer.config_mediumAnimTime);
			break;
		}
	}

	private void addView(String message) {

		mPushLayout.setVisibility(View.VISIBLE);
		final TextView tvUserName = (TextView) mPushLayout
				.findViewById(R.id.tvUserName);
		final TextView tvNotification = (TextView) mPushLayout
				.findViewById(R.id.tvNotification);
		final ImageView ivUserImage = (ImageView) mPushLayout
				.findViewById(R.id.ivUserImage);
		final ProgressBar pbLoading = (ProgressBar) mPushLayout
				.findViewById(R.id.pbLoadingForImage);
		final ImageButton btnClose = (ImageButton) mPushLayout
				.findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideNotification();
			}
		});
		final RelativeLayout rlBody = (RelativeLayout) mPushLayout
				.findViewById(R.id.rlNotificationBody);

		String avatarId = null;
		int stubId = R.drawable.image_stub;

		if (mFromType.equals(Const.PUSH_TYPE_USER)) {
			stubId = R.drawable.user_stub;
			tvUserName.setText(mFromUser.getName().toUpperCase());
			tvNotification.setText(message);
			avatarId = mFromUser.getAvatarFileId();

			rlBody.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					UsersManagement.setToUser(mFromUser);
					UsersManagement.setToGroup(null);
					SettingsManager.ResetSettings();
					if (WallActivity.gCurrentMessages != null) {
						WallActivity.gCurrentMessages.clear();
					}
					WallActivity.gIsRefreshUserProfile = true;
					Intent wallIntent = new Intent(mContext, WallActivity.class);
					mContext.startActivity(wallIntent);
					hideNotification();

				}
			});
		}

		if (mFromType.equals(Const.PUSH_TYPE_GROUP)) {
			stubId = R.drawable.group_stub;
			tvUserName.setText(mFromGroup.getName().toUpperCase());
			tvNotification.setText(message);
			avatarId = mFromGroup.getAvatarFileId();
			rlBody.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					UsersManagement.setToGroup(mFromGroup);
					UsersManagement.setToUser(null);
					SettingsManager.ResetSettings();
					if (WallActivity.gCurrentMessages != null) {
						WallActivity.gCurrentMessages.clear();
					}
					Intent wallIntent = new Intent(mContext, WallActivity.class);
					mContext.startActivity(wallIntent);
					hideNotification();
				}
			});
		}

		Utils.displayImage(avatarId, ivUserImage,
					pbLoading, ImageLoader.SMALL, stubId, false);

		btnClose.setClickable(true);
		btnClose.setFocusable(true);

	}

	private void hideNotification() {
		if (!mSlideOutTopOnClose.hasStarted() || mSlideOutTopOnClose.hasEnded())
			if (mSlideFromTop.hasStarted()) {
				mPushLayout.clearAnimation();
				mPushLayout.startAnimation(mSlideOutTopOnClose);
			} else {
				return;
			}
	}

	private void setTranslateAnimations() {
		mSlideFromTop.setFillAfter(false);
		mSlideFromTop.setFillEnabled(false);
		mSlideFromTop.setDuration(mAnimationDuration);
		mSlideFromTop.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				mPushLayout.setVisibility(View.VISIBLE);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mPushLayout.startAnimation(mSlideOutTop);
			}
		});
		mSlideOutTop.setStartOffset(mShowingDuration);
		mSlideOutTop.setDuration(mAnimationDuration);
		mSlideOutTop.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mPushLayout.setVisibility(View.GONE);
			}
		});
		mSlideOutTopOnClose.setStartOffset(0);
		mSlideOutTopOnClose.setDuration(mAnimationDuration);
		mSlideOutTopOnClose.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mPushLayout.setVisibility(View.GONE);
			}
		});
	}

	private void startTranslateAnimations() {
		mPushLayout.startAnimation(mSlideFromTop);
	}

}
