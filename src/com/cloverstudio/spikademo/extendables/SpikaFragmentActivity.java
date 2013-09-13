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

import java.util.concurrent.ExecutionException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.GCMIntentService;
import com.cloverstudio.spikademo.SpikaApp;
import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.ActivitySummary;
import com.cloverstudio.spikademo.couchdb.model.Group;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.dialog.PushNotification;
import com.cloverstudio.spikademo.management.ConnectionChangeReceiver;
import com.cloverstudio.spikademo.management.LogoutReceiver;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;

/**
 * SpikaFragmentActivity
 * 
 * HookUp base FragmentActivity, registers receivers, handles push notifications, connection changes and logout.
 */

public class SpikaFragmentActivity extends FragmentActivity {

	protected RelativeLayout mRlNoInternetNotification;
	protected RelativeLayout mRlPushNotification;
	private TranslateAnimation mSlideFromTop;
	private TranslateAnimation mSlideOutTop;
	private final IntentFilter mPushFilter = new IntentFilter(
			GCMIntentService.PUSH);
	private final IntentFilter mConnectionChangeFilter = new IntentFilter(
			ConnectionChangeReceiver.INTERNET_CONNECTION_CHANGE);

	private IntentFilter intentFilter = new IntentFilter(LogoutReceiver.LOGOUT);
	private LogoutReceiver logoutRec = new LogoutReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSlideFromTop = SpikaApp.getSlideFromTop();
		mSlideFromTop.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				mRlNoInternetNotification.setVisibility(View.VISIBLE);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
			}
		});

		mSlideOutTop = SpikaApp.getSlideOutTop();
		mSlideOutTop.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mRlNoInternetNotification.setVisibility(View.GONE);
			}
		});

		// Logout finish activity
		this.registerReceiver(logoutRec, intentFilter);
	};

	@Override
	protected void onResume() {
		LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver,
				mPushFilter);

		SpikaApp.getLocalBroadcastManager().registerReceiver(
				mConnectionChangeReceiver, mConnectionChangeFilter);

		checkInternetConnection();
		super.onResume();
		mRlNoInternetNotification = (RelativeLayout) findViewById(R.id.rlNoInternetNotification);
		mRlPushNotification = (RelativeLayout) findViewById(R.id.rlPushNotification);
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mPushReceiver);
	}

	private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			handlePushNotification(intent);
		}
	};

	private void handlePushNotification(Intent intent) {

		new GetActivitySummary(this).execute();

		String message = intent.getStringExtra(Const.PUSH_MESSAGE);
		String fromUserId = intent.getStringExtra(Const.PUSH_FROM_USER_ID);
		String fromType = intent.getStringExtra(Const.PUSH_FROM_TYPE);

		if (mRlPushNotification != null) {

			User fromUser = null;
			Group fromGroup = null;

			try {
				fromUser = new GetUserByIdAsync(this).execute(fromUserId).get();
				if (fromType.equals(Const.PUSH_TYPE_GROUP)) {
					String fromGroupId = intent
							.getStringExtra(Const.PUSH_FROM_GROUP_ID);
					fromGroup = new GetGroupByIdAsync(this)
							.execute(fromGroupId).get();

					if (UsersManagement.getToGroup() != null) {
						boolean groupWallIsOpened = fromGroupId
								.equals(UsersManagement.getToGroup().getId())
								&& WallActivity.gIsVisible;
						if (groupWallIsOpened) {
							refreshWallMessages();
							return;
						}
					}
				}
				if (fromType.equals(Const.PUSH_TYPE_USER)) {

					if (UsersManagement.getToUser() != null) {

						boolean userWallIsOpened = fromUserId
								.equals(UsersManagement.getToUser().getId())
								&& WallActivity.gIsVisible;
						if (userWallIsOpened) {
							refreshWallMessages();
							return;
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			PushNotification.show(this, mRlPushNotification, message, fromUser,
					fromGroup, fromType);

		}

	}

	private BroadcastReceiver mConnectionChangeReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			if (intent.getBooleanExtra(
					ConnectionChangeReceiver.HAS_INTERNET_CONNECTION, true) == true) {
				hideNoInternetNotification();
			} else {
				showNoInternetNotification();
			}

		}
	};

	private void checkInternetConnection() {
		if (SpikaApp.hasNetworkConnection()) {
			hideNoInternetNotification();
		} else {
			showNoInternetNotification();
		}
	}

	protected void showNoInternetNotification() {
		if (mRlNoInternetNotification != null) {
			mRlNoInternetNotification.startAnimation(mSlideFromTop);
		}

	}

	protected void hideNoInternetNotification() {
		if (mRlNoInternetNotification != null) {
			if (mRlNoInternetNotification.getVisibility() == View.VISIBLE) {
				mRlNoInternetNotification.startAnimation(mSlideOutTop);
			}
		}
	}

	private class GetUserByIdAsync extends SpikaAsync<String, Void, User> {

		protected GetUserByIdAsync(Context context) {
			super(context);
		}

		@Override
		protected User doInBackground(String... params) {
			String userId = params[0];
			return CouchDB.findUserById(userId);
		}

		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
		}
	}

	private class GetGroupByIdAsync extends SpikaAsync<String, Void, Group> {

		protected GetGroupByIdAsync(Context context) {
			super(context);
		}

		@Override
		protected Group doInBackground(String... params) {
			String id = params[0];
			return CouchDB.findGroupById(id);
		}

		@Override
		protected void onPostExecute(Group group) {
			super.onPostExecute(group);
		}
	}

	protected void refreshActivitySummaryViews() {
		// Child activities override this method
	}

	protected void refreshWallMessages() {
		// Child activities override this method
	}

	protected class GetActivitySummary extends
			SpikaAsync<Void, Void, ActivitySummary> {

		public GetActivitySummary(Context context) {
			super(context);
		}

		@Override
		protected ActivitySummary doInBackground(Void... params) {
			return CouchDB.findUserActivitySummary(UsersManagement
					.getLoginUser().getId());
		}

		@Override
		protected void onPostExecute(ActivitySummary activitySummary) {
			if (activitySummary != null) {

				UsersManagement.getLoginUser().setActivitySummary(
						activitySummary);

				// Refresh all views that show data from user activity summary
				SpikaFragmentActivity.this.refreshActivitySummaryViews();

			}

		}
	}

	@Override
	protected void onDestroy() {
		setObjectsToNull();
		this.unregisterReceiver(logoutRec);
		super.onDestroy();
	}

	protected void setObjectsToNull() {
		mRlNoInternetNotification = null;
		mRlPushNotification = null;
		mPushReceiver = null;
		mSlideFromTop = null;
		mSlideOutTop = null;
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mConnectionChangeReceiver);

	}

	protected void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

}
