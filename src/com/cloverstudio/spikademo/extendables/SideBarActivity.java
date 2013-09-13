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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.GroupsActivity;
import com.cloverstudio.spikademo.SpikaApp;
import com.cloverstudio.spikademo.MyProfileActivity;
import com.cloverstudio.spikademo.RecentActivityActivity;
import com.cloverstudio.spikademo.SettingsActivity;
import com.cloverstudio.spikademo.SignInActivity;
import com.cloverstudio.spikademo.SplashScreenActivity;
import com.cloverstudio.spikademo.UsersActivity;
import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.ActivitySummary;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.dialog.HookUpDialog;
import com.cloverstudio.spikademo.management.LogoutReceiver;
import com.cloverstudio.spikademo.management.SettingsManager;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.google.android.gcm.GCMRegistrar;

/**
 * SideBarActivity
 * 
 * Handles open/close actions and animations for sidebar.
 */

public class SideBarActivity extends SpikaActivity {

	public RelativeLayout mRlSideBarHolder;
	protected RelativeLayout mRlBody;
	protected RelativeLayout mRlTitle;
	protected ImageButton mBtnOpenSideBar;
	protected TextView mTvTitle;
	protected boolean mSideBarOpened;
	private RelativeLayout mRlUsers;
	private RelativeLayout mRlGroups;
	private RelativeLayout mRlProfile;
	private RelativeLayout mRlSettings;
	private RelativeLayout mRlLogout;
	private RelativeLayout mRlPrivateWall;
	private RelativeLayout mRlNotificationsTitle;
	private ImageView mIvNotificationsBalloon;
	private TextView mTvNotificationsNumber;

	private final int BUTTON_USERS = 1000;
	private final int BUTTON_GROUPS = 1001;
	private final int BUTTON_PROFILE = 1002;
	private final int BUTTON_SETTINGS = 1003;
	private final int BUTTON_LOGOUT = 1004;
	private final int BUTTON_PRIVATE_WALL = 1005;
	private final int NOTIFICATIONS = 1006;

	public static SideBarActivity sInstance = null;

	public HookUpDialog mLogoutDialog;
   
    private class GetSupportUserAsync extends SpikaAsync<Void, Void, User> {

        protected GetSupportUserAsync(Context context) {
            super(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected User doInBackground(Void... params) {

            User supportUser = CouchDB.findUserById("7df093b56d11b8c5f961cf120d2ebc4c");
            return supportUser;
        }

        @Override
        protected void onPostExecute(User supportUser) {
            UsersManagement.setSupportUser(supportUser);
        }
    }

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sInstance = this;
		super.onCreate(savedInstanceState);
		new GetActivitySummary(this).execute();

		if(UsersManagement.getSupportUser() == null){
		    new GetSupportUserAsync(this).execute();
		}
		
	}

	public void setSideBar(String Title) {

		Initialization(Title);
		OnClickListeners();
		mTvTitle.setText(Title);
		mSideBarOpened = false;
	}

	@Override
	protected void refreshActivitySummaryViews() {
		super.refreshActivitySummaryViews();

		ActivitySummary summary = UsersManagement.getLoginUser()
				.getActivitySummary();

		if (summary != null) {

			if (summary.getTotalNotificationCount() == 0) {
				mIvNotificationsBalloon
						.setBackgroundResource(R.drawable.sidebar_no_notification_balloon);
			} else {
				mIvNotificationsBalloon
						.setBackgroundResource(R.drawable.sidebar_notification_balloon);
			}
			mTvNotificationsNumber.setText(Integer.toString(summary
					.getTotalNotificationCount()));

		}

	}

	private void OnClickListeners() {
		mBtnOpenSideBar.setClickable(true);
		mBtnOpenSideBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openSideBar();

			}
		});

		mRlTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSideBarOpened) {
					closeSideBar();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mSideBarOpened) {
			closeSideBar();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mSideBarOpened) {
				closeSideBar();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void closeSideBar() {

		if (mRlSideBarHolder.getVisibility() == View.VISIBLE) {
			mRlBody.bringToFront();
			mRlSideBarHolder.startAnimation(SpikaApp.getSlideOutLeft());
			mRlBody.startAnimation(SpikaApp.getSlideInRight());
			mRlSideBarHolder.setVisibility(View.INVISIBLE);
			mSideBarOpened = false;
		}

		enableViews();
	}

	public void openSideBar() {

		new GetActivitySummary(this).execute();

		mRlSideBarHolder.setVisibility(View.VISIBLE);
		mRlSideBarHolder.bringToFront();
		mRlSideBarHolder.startAnimation(SpikaApp.getSlideInLeft());
		mRlBody.startAnimation(SpikaApp.getSlideOutRight());
		mSideBarOpened = true;

		if (!this.equals(RecentActivityActivity.getInstance())
				&& !this.getComponentName().toString()
						.contains("UsersActivity")
				&& !this.getComponentName().toString()
						.contains("GroupsActivity")) {
			hideKeyboard();
		}

		disableViews();

	}

	private void Initialization(String title) {

		mRlSideBarHolder = (RelativeLayout) findViewById(R.id.rlSideBarHolder);
		mRlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
		mBtnOpenSideBar = (ImageButton) findViewById(R.id.btnOpenSideBar);
		mTvTitle = (TextView) findViewById(R.id.tvTitle);

		mRlUsers = (RelativeLayout) findViewById(R.id.rlUsers);
		mRlUsers.setOnClickListener(new SideMenuButtonListener(
				getComponentName(), BUTTON_USERS));

		mRlGroups = (RelativeLayout) findViewById(R.id.rlGroups);
		mRlGroups.setOnClickListener(new SideMenuButtonListener(
				getComponentName(), BUTTON_GROUPS));

		mRlProfile = (RelativeLayout) findViewById(R.id.rlProfile);
		mRlProfile.setOnClickListener(new SideMenuButtonListener(
				getComponentName(), BUTTON_PROFILE));

		mRlSettings = (RelativeLayout) findViewById(R.id.rlSettings);
		mRlSettings.setOnClickListener(new SideMenuButtonListener(
				getComponentName(), BUTTON_SETTINGS));

		mRlLogout = (RelativeLayout) findViewById(R.id.rlLogout);
		mRlLogout.setOnClickListener(new SideMenuButtonListener(
				getComponentName(), BUTTON_LOGOUT));

		mRlPrivateWall = (RelativeLayout) findViewById(R.id.rlPrivateWall);
		mRlPrivateWall.setOnClickListener(new SideMenuButtonListener(
				getComponentName(), BUTTON_PRIVATE_WALL));

		mRlNotificationsTitle = (RelativeLayout) findViewById(R.id.rlNotifications);
		mRlNotificationsTitle.setOnClickListener(new SideMenuButtonListener(
				getComponentName(), NOTIFICATIONS));

		mIvNotificationsBalloon = (ImageView) findViewById(R.id.ivNotificationsBalloon);
		mTvNotificationsNumber = (TextView) findViewById(R.id.tvNotificationsNumber);

		mRlSideBarHolder.getLayoutParams().width = SpikaApp.getTransport();

		mRlBody = (RelativeLayout) findViewById(R.id.rlBody);

		mSideBarOpened = false;

	}

	protected void disableViews() {
	}

	protected void enableViews() {
	}

   public void logout(){


        mLogoutDialog = new HookUpDialog(this);
        mLogoutDialog.setMessage(getString(R.string.logout_message));
        mLogoutDialog.setOnButtonClickListener(HookUpDialog.BUTTON_OK,
                
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        appLogout(false, false, false);
                    }
                    
                });
        mLogoutDialog.setOnButtonClickListener(HookUpDialog.BUTTON_CANCEL,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mLogoutDialog.dismiss();
                        SideBarActivity.this.closeSideBar();
                    }
                    
                });
       
        mLogoutDialog.show();

    }
	   
   
	private class SideMenuButtonListener implements OnClickListener {
		int buttonId;
		ComponentName componentName;

		public SideMenuButtonListener(ComponentName componentName, int buttonId) {
			this.buttonId = buttonId;
			this.componentName = componentName;
		}

		public void onClick(View v) {

			Intent intent = null;
			@SuppressWarnings("rawtypes")
			Class nextActivity = null;

			switch (buttonId) {
			case BUTTON_USERS:
				intent = new Intent(SideBarActivity.this, UsersActivity.class);
				nextActivity = UsersActivity.class;
				break;
			case BUTTON_GROUPS:
				intent = new Intent(SideBarActivity.this, GroupsActivity.class);
				nextActivity = GroupsActivity.class;
				break;
			case BUTTON_PROFILE:
				intent = new Intent(SideBarActivity.this,
						MyProfileActivity.class);
				nextActivity = MyProfileActivity.class;
				break;
			case BUTTON_SETTINGS:
				intent = new Intent(SideBarActivity.this,
						SettingsActivity.class);
				nextActivity = SettingsActivity.class;
				break;
			case BUTTON_LOGOUT:
				SideBarActivity.this.logout();
				break;
			case BUTTON_PRIVATE_WALL:
                intent = new Intent(SideBarActivity.this,
                        WallActivity.class);
                nextActivity = WallActivity.class;
			    break;
			case NOTIFICATIONS:
				intent = new Intent(SideBarActivity.this,
						RecentActivityActivity.class);
				nextActivity = RecentActivityActivity.class;
				break;
			default:
				break;
			}

			if(nextActivity == null)
			    return;
			
			boolean returnToActivity = nextActivity.getCanonicalName().equals(
					componentName.getClassName());
			boolean logout = nextActivity.getCanonicalName().contains(
					"SignInActivity");
			boolean openBaseActivity = componentName.getClassName().contains(
					"RecentActivity");
			boolean openWall = nextActivity.getCanonicalName().contains(
					"WallActivity");

			if (intent != null) {

				if (returnToActivity && !openWall) {
					SideBarActivity.this.closeSideBar();
				} else if (logout) {
					appLogout(false, false, false);
				} else {
					if (openWall) {
						SettingsManager.ResetSettings();
						WallActivity.gIsRefreshUserProfile = true;
						if (WallActivity.gCurrentMessages != null) {
							WallActivity.gCurrentMessages.clear();
						}
						UsersManagement.setToUser(UsersManagement
								.getSupportUser());
						UsersManagement.setToGroup(null);

					}
					if (openBaseActivity || openWall) {
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					} else {
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						SideBarActivity.this.finish();
					}
					SideBarActivity.this.startActivity(intent);
					SideBarActivity.this.overridePendingTransition(
							R.anim.slide_in_right, R.anim.slide_out_left);
				}

			}
		}
	}

	@Override
	protected void setObjectsNull() {
		mRlSideBarHolder = null;
		mRlBody = null;
		mRlTitle = null;
		mTvTitle = null;
		mRlUsers = null;
		mRlGroups = null;
		mRlProfile = null;
		mRlSettings = null;
		mRlLogout = null;
		mRlPrivateWall = null;
		mRlNotificationsTitle = null;
		super.setObjectsNull();
	}

	public static void appLogout(boolean isUserUpdateConflict,
			boolean isServerError, boolean isInvalidToken) {
		
		Activity fromActivity = SideBarActivity.getValidContext();

		Intent goToSignIn = new Intent(fromActivity, SignInActivity.class);

		if (isServerError) {
			goToSignIn.putExtra("password_from_prefs", SpikaApp
					.getPreferences().getUserPassword());
			goToSignIn.putExtra("email_from_prefs", SpikaApp.getPreferences()
					.getUserEmail());
		}
		if (isInvalidToken) {
			goToSignIn.putExtra("invalid_token", true);
		}

		GCMRegistrar
				.unregister(SpikaApp.getInstance().getApplicationContext());

		SpikaApp.getPreferences().setWatchingGroupId("");
		SpikaApp.getPreferences().setWatchingGroupRev("");

		if (SideBarActivity.sInstance != null) {
			goToSignIn.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			SideBarActivity.sInstance.startActivity(goToSignIn);
			
			sendBroadcastLogout(SideBarActivity.sInstance);
		} else if (SplashScreenActivity.sInstance != null) {
			SplashScreenActivity.sInstance.startActivity(goToSignIn);
			
			sendBroadcastLogout(SplashScreenActivity.sInstance);
		}
	}
	
	private static void sendBroadcastLogout(Context context){
		/*
		 * Send logout broadcast
		 */
		Intent intent = new Intent();
	    intent.setAction(LogoutReceiver.LOGOUT);
	    context.sendBroadcast(intent);
	    intent = null;
	    // End: Send logout broadcast
	}

	public static Activity getValidContext() {
		if (SideBarActivity.sInstance != null)
			return SideBarActivity.sInstance;
		else if (SignInActivity.getInstance() != null)
			return SignInActivity.getInstance();
		else if (WallActivity.getInstance() != null)
			return WallActivity.getInstance();
		else if (RecentActivityActivity.getInstance() != null)
			return RecentActivityActivity.getInstance();
		else if (SplashScreenActivity.sInstance != null)
			return SplashScreenActivity.sInstance;
		else
			return null;
	}

	protected static class UpdateUserStatusAsync extends
			AsyncTask<String, Void, Boolean> {

		public UpdateUserStatusAsync() {
			super();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			UsersManagement.getLoginUser().setOnlineStatus(params[0]);
			return CouchDB.updateUser(UsersManagement.getLoginUser());
		}

	}

}
