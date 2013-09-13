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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.GCMIntentService;
import com.cloverstudio.spikademo.SpikaApp;
import com.cloverstudio.spikademo.PasscodeActivity;
import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.ActivitySummary;
import com.cloverstudio.spikademo.couchdb.model.Group;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.dialog.PushNotification;
import com.cloverstudio.spikademo.dialog.Tutorial;
import com.cloverstudio.spikademo.management.ConnectionChangeReceiver;
import com.cloverstudio.spikademo.management.LogoutReceiver;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Preferences;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * SpikaActivity
 * 
 * HookUp base Activity, registers receivers, handles push notifications, connection changes and logout.
 */

public class SpikaActivity extends Activity {

	protected RelativeLayout mRlNoInternetNotification;
	protected RelativeLayout mRlPushNotification;
	protected HookUpProgressDialog mProgressDialog;
	private TranslateAnimation mSlideFromTop;
	private TranslateAnimation mSlideOutTop;
	private final IntentFilter mPushFilter = new IntentFilter(
			GCMIntentService.PUSH);
	private final IntentFilter mConnectionChangeFilter = new IntentFilter(
			ConnectionChangeReceiver.INTERNET_CONNECTION_CHANGE);

	private boolean tutorialShowed = false;
	
	private IntentFilter intentFilter = new IntentFilter(LogoutReceiver.LOGOUT);
	private LogoutReceiver logoutRec = new LogoutReceiver();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSlideFromTop = SpikaApp.getSlideFromTop();
		mSlideOutTop = SpikaApp.getSlideOutTop();
		mSlideFromTop.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				mRlNoInternetNotification.setVisibility(View.VISIBLE);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
			}
		});
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
	protected void onStart() {
		super.onStart();
		handlePasscode();
	}

	protected void handlePasscode() {
		if (SpikaApp.gOpenFromBackground) {
			SpikaApp.gOpenFromBackground = false;

			if (getIntent().getBooleanExtra(Const.SIGN_IN, false) == false) {
				if (SpikaApp.getPreferences().getPasscodeProtect() == true) {
					Intent passcode = new Intent(SpikaActivity.this,
							PasscodeActivity.class);
					passcode.putExtra("protect", true);
					startActivity(passcode);
				}
			} else {
				getIntent().removeExtra(Const.SIGN_IN);
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mRlNoInternetNotification = (RelativeLayout) findViewById(R.id.rlNoInternetNotification);
		mRlPushNotification = (RelativeLayout) findViewById(R.id.rlPushNotification);

		SpikaApp.getLocalBroadcastManager().registerReceiver(mPushReceiver,
				mPushFilter);

		SpikaApp.getLocalBroadcastManager().registerReceiver(
				mConnectionChangeReceiver, mConnectionChangeFilter);
		
		mProgressDialog = new HookUpProgressDialog(this);

		checkInternetConnection();

	}

	@Override
	protected void onPause() {
		super.onPause();
		SpikaApp.getLocalBroadcastManager().unregisterReceiver(mPushReceiver);
	}

	@Override
	protected void onDestroy() {
		setObjectsNull();
		this.unregisterReceiver(logoutRec);
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
		checkIfAppIsInForeground();
		if (mProgressDialog != null) {
			if (mProgressDialog.isShowing())
				mProgressDialog.dismiss();
		}
	}

	protected void checkIfAppIsInForeground() {
		try {
			boolean appIsInForeground = new SpikaApp.ForegroundCheckAsync()
					.execute(getApplicationContext()).get();
			if (!appIsInForeground) {
				SpikaApp.gOpenFromBackground = true;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			handlePushNotification(intent);
		}
	};

	private void handlePushNotification(Intent intent) {

		new GetActivitySummary(SpikaActivity.this).execute();

		String message = intent.getStringExtra(Const.PUSH_MESSAGE);
		String fromUserId = intent.getStringExtra(Const.PUSH_FROM_USER_ID);
		String fromType = intent.getStringExtra(Const.PUSH_FROM_TYPE);

		if (mRlPushNotification != null) {

			User fromUser = null;
			Group fromGroup = null;

			try {
				fromUser = new GetUserByIdAsync(SpikaActivity.this).execute(
						fromUserId).get();
				if (fromType.equals(Const.PUSH_TYPE_GROUP)) {
					String fromGroupId = intent
							.getStringExtra(Const.PUSH_FROM_GROUP_ID);
					fromGroup = new GetGroupByIdAsync(SpikaActivity.this)
							.execute(fromGroupId).get();

					if (UsersManagement.getToGroup() != null) {
						boolean isGroupWallOpened = fromGroupId
								.equals(UsersManagement.getToGroup().getId())
								&& WallActivity.gIsVisible;
						if (isGroupWallOpened) {
							refreshWallMessages();
							return;
						}
					}
				}
				if (fromType.equals(Const.PUSH_TYPE_USER)) {

					if (UsersManagement.getToUser() != null) {

						boolean isUserWallOpened = fromUserId
								.equals(UsersManagement.getToUser().getId())
								&& WallActivity.gIsVisible;
						if (isUserWallOpened) {
							
							WallActivity.gIsRefreshUserProfile = false;
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

	private void showNoInternetNotification() {
		if (mRlNoInternetNotification != null) {
			if (mRlNoInternetNotification.getVisibility() == View.GONE) {
				mRlNoInternetNotification.startAnimation(mSlideFromTop);
			} else {
				mRlNoInternetNotification.setVisibility(View.VISIBLE);
			}
		}

	}

	private void hideNoInternetNotification() {
		if (mRlNoInternetNotification != null) {
			if (mRlNoInternetNotification.getVisibility() == View.VISIBLE) {
				mRlNoInternetNotification.startAnimation(mSlideOutTop);
			}
		}
	}

	protected class GetUserByIdAsync extends SpikaAsync<String, Void, User> {

		public GetUserByIdAsync(Context context) {
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

	protected class GetGroupByIdAsync extends SpikaAsync<String, Void, Group> {

		public GetGroupByIdAsync(Context context) {
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
	
	protected class GetGroupByNameAsync extends SpikaAsync<String, Void, Group> {

	    	private HookUpProgressDialog mProgressDialog;
	    
		public GetGroupByNameAsync(Context context) {
			super(context);
			mProgressDialog = new HookUpProgressDialog(context);
		}

		@Override
		protected Group doInBackground(String... params) {
			String id = params[0];
			return CouchDB.findGroupsByName(id).get(0);
		}

		@Override
		protected void onPostExecute(Group group) {
			super.onPostExecute(group);
			mProgressDialog.dismiss();
		}
	}

	protected void refreshActivitySummaryViews() {
	}

	protected void refreshWallMessages() {
	}

	protected class GetActivitySummary extends
			SpikaAsync<Void, Void, ActivitySummary> {

		public GetActivitySummary(Context context) {
			super(context);
		}

		@Override
		protected ActivitySummary doInBackground(Void... params) {
			if (UsersManagement.getLoginUser() != null) {
				
				return CouchDB.findUserActivitySummary(UsersManagement
						.getLoginUser().getId());
			} else
				return null;
		}

		@Override
		protected void onPostExecute(ActivitySummary activitySummary) {
			if (activitySummary != null) {
				UsersManagement.getLoginUser().setActivitySummary(
						activitySummary);

				SpikaActivity.this.refreshActivitySummaryViews();

			}

		}
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

	protected void setObjectsNull() {
		mPushReceiver = null;
		mSlideFromTop = null;
		mSlideOutTop = null;
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mConnectionChangeReceiver);
	}
	
	protected void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), 0);
	}
	
	protected class GetLoginUserAsync extends SpikaAsync<Void, Void, User> {

		public GetLoginUserAsync(Context context) {
			super(context);
		}

		@Override
		protected User doInBackground(Void... params) {
			
			Preferences prefs = SpikaApp.getPreferences();
			return CouchDB.findUserByEmail(prefs.getUserEmail(), true);
		}

		@Override
		protected void onPostExecute(User loginUser) {
			UsersManagement.setLoginUser(loginUser);
		}
	}
	
    protected void showTutorial(String textTutorial) {
        
        if (tutorialShowed == false && SpikaApp.getPreferences().getShowTutorial(Utils.getClassNameInStr(this))) {
            Tutorial.show(this, textTutorial);
            SpikaApp.getPreferences().setShowTutorial(false, Utils.getClassNameInStr(this));
            tutorialShowed = true;
        }

        
    }
    
    protected void showTutorialOnceAfterBoot(String textTutorial) {

        if (tutorialShowed == false && SpikaApp.getPreferences().getShowTutorialForBoot(Utils.getClassNameInStr(this))) {
            Tutorial.show(this, textTutorial);
            SpikaApp.getPreferences().setShowTutorialForBoot(false, Utils.getClassNameInStr(this));
            tutorialShowed = true;
        }
        
        
    }
    
    

}
