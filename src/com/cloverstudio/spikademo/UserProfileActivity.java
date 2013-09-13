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

package com.cloverstudio.spikademo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.dialog.HookUpAlertDialog;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.extendables.SpikaActivity;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.SettingsManager;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * UserProfileActivity
 * 
 * Shows profile of a user; has an option for login user to remove/add this user
 * to favorites.
 */

public class UserProfileActivity extends SpikaActivity {

	private ImageView mIvUserImage;
	private TextView mTvUserName;
	private TextView mTvUserLastLogin;
	private TextView mTvUserAbout;
	private TextView mTvUserBirthday;
	private TextView mTvUserGender;
	private Button mBtnContacts;
	private Button mBtnBack;
	private ProgressBar mPbLoading;
	private User mUser;
	private RelativeLayout mRlAbout;
	private RelativeLayout mRlBirthday;
	private RelativeLayout mRlGender;
	private Spinner mSpinnerStatus;
	private String mUserOnlineStatus;
	private Button mBtnOpenWall;
	// private boolean mIsUpdated = false;

	private static final int ADD = 1000;
	private static final int REMOVE = 1001;

	private static final int NO_BIRTHDAY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		setContentView(R.layout.activity_user_profile);
		Initialization();
		OnClickListeners();

		showTutorial(getString(R.string.tutorial_userprofile));
	}

	@Override
	protected void setObjectsNull() {
		unbindDrawables(findViewById(R.id.ivUserImage));
		mRlBirthday = null;
		mRlAbout = null;
		mRlGender = null;
		super.setObjectsNull();
	}

	private void Initialization() {

		mIvUserImage = (ImageView) findViewById(R.id.ivProfileImage);
		mTvUserName = (TextView) findViewById(R.id.tvUserName);
		mTvUserLastLogin = (TextView) findViewById(R.id.tvUserLastLogin);
		mTvUserAbout = (TextView) findViewById(R.id.tvUserAbout);
		mTvUserBirthday = (TextView) findViewById(R.id.tvUserBirthday);
		mTvUserGender = (TextView) findViewById(R.id.tvUserGender);
		mBtnContacts = (Button) findViewById(R.id.btnContacts);
		mBtnContacts.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnBack = (Button) findViewById(R.id.btnBack);
		mBtnBack.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mPbLoading = (ProgressBar) findViewById(R.id.pbLoadingForImage);
		mRlAbout = (RelativeLayout) findViewById(R.id.rlAbout);
		mRlGender = (RelativeLayout) findViewById(R.id.rlGender);
		mRlBirthday = (RelativeLayout) findViewById(R.id.rlBirthday);
		mBtnOpenWall = (Button) findViewById(R.id.btnOpenWall);
		mBtnOpenWall.setTypeface(SpikaApp.getTfMyriadPro());

		mBtnOpenWall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openUserWall(mUser);

			}
		});

		mSpinnerStatus = (Spinner) findViewById(R.id.spinnerStatus);
		mSpinnerStatus.setClickable(false);

		// If opened from link
		if (getIntent().getBooleanExtra(Const.USER_URI_INTENT, false)) {
			String userName = getIntent().getStringExtra(Const.USER_URI_NAME);
			new FindUserByNameAsync(this).execute(userName);

			// If opened from another activity
		} else {
			String userId = getIntent().getStringExtra("user_id");
			if (getIntent().getStringExtra("user_id") != null) {
				getIntent().removeExtra("user_id");
			} else {
				userId = UsersManagement.getToUser().getId();
			}
			new FindUserByIdAsync(this).execute(userId);
		}

		final ArrayAdapter<String> onlineStatusAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.online_status)) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTextSize(16);
				((TextView) v).setTypeface(SpikaApp.getTfMyriadPro());

				Drawable statusIcon = null;

				switch (position) {
				case 0:
					statusIcon = getContext().getResources().getDrawable(
							R.drawable.user_online_icon);
					((TextView) v).setTextColor(getResources().getColor(
							R.color.hookup_positive));
					break;
				case 1:
					statusIcon = getContext().getResources().getDrawable(
							R.drawable.user_away_icon);
					((TextView) v).setTextColor(getResources().getColor(
							R.color.hookup_positive));
					break;
				case 2:
					statusIcon = getContext().getResources().getDrawable(
							R.drawable.user_busy_icon);
					((TextView) v).setTextColor(getResources().getColor(
							R.color.hookup_positive));
					break;
				case 3:
					statusIcon = getContext().getResources().getDrawable(
							R.drawable.user_offline_icon);
					((TextView) v).setTextColor(getResources().getColor(
							R.color.hookup_positive));
					break;
				default:
					((TextView) v).setTextColor(getResources().getColor(
							R.color.hookup_positive));
					break;
				}

				((TextView) v).setCompoundDrawablePadding(10);
				((TextView) v).setCompoundDrawablesWithIntrinsicBounds(
						statusIcon, null, null, null);

				return v;
			}

		};
		mSpinnerStatus.setAdapter(onlineStatusAdapter);

	}

	private void setUserProfile() {

		mUserOnlineStatus = mUser.getOnlineStatus();
		if (mUserOnlineStatus != null && !"".equals(mUserOnlineStatus)) {
			if (mUserOnlineStatus.equals(Const.ONLINE)) {
				mSpinnerStatus.setSelection(0);
			}
			if (mUserOnlineStatus.equals(Const.AWAY)) {
				mSpinnerStatus.setSelection(1);
			}
			if (mUserOnlineStatus.equals(Const.BUSY)) {
				mSpinnerStatus.setSelection(2);
			}
			if (mUserOnlineStatus.equals(Const.OFFLINE)) {
				mSpinnerStatus.setSelection(3);
			}
		} else {
			mSpinnerStatus.setSelection(3);
		}

		Utils.displayImage(mUser.getAvatarFileId(), mIvUserImage, mPbLoading,
				ImageLoader.LARGE, R.drawable.user_stub_large, false);

		mTvUserName.setText(mUser.getName());

		if (mUser.getLastLogin() != 0) {
			mTvUserLastLogin.setText(Utils.getFormattedDateTime(mUser
					.getLastLogin()));
		}
		if (mUser.getAbout() != null && !"".equals(mUser.getAbout())) {
			mRlAbout.setVisibility(View.VISIBLE);
			mTvUserAbout.setText(mUser.getAbout());
		} else {
			mRlAbout.setVisibility(View.GONE);
		}

		if (mUser.getBirthday() == NO_BIRTHDAY) {
			mRlBirthday.setVisibility(View.GONE);
		} else {
			mRlBirthday.setVisibility(View.VISIBLE);
			String stringBirthday = DateFormat.format(
					getString(R.string.hookup_date_format),
					mUser.getBirthday() * 1000).toString();
			mTvUserBirthday.setText(stringBirthday);
		}

		if (mUser.getGender() != null && !"".equals(mUser.getGender())) {
			mRlGender.setVisibility(View.VISIBLE);
			if (mUser.getGender().equalsIgnoreCase(Const.MALE)) {
				mTvUserGender.setText(R.string.male);
			}
			if (mUser.getGender().equalsIgnoreCase(Const.FEMALE)) {
				mTvUserGender.setText(R.string.female);
			}
		} else {
			mRlGender.setVisibility(View.GONE);
		}

		if (UsersManagement.getLoginUser().isInContacts(mUser)) {
			setButtonContacts(REMOVE);
		} else {
			setButtonContacts(ADD);
		}
	}

	private void OnClickListeners() {

		final HookUpAlertDialog alertDialog = new HookUpAlertDialog(this);

		mBtnContacts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UsersManagement.getLoginUser().isInContacts(mUser)) {

					new UpdateContactsAsync().execute(REMOVE);

				} else {

					if (UsersManagement.getLoginUser().canAddContact()) {
						new UpdateContactsAsync().execute(ADD);
					} else {
						alertDialog
								.show(getString(R.string.max_contacts_alert));
					}

				}
			}
		});

		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserProfileActivity.this.finish();
			}
		});
	}

	private void setButtonContacts(int type) {

		mBtnContacts.setVisibility(View.VISIBLE);

		switch (type) {
		case ADD:
			mBtnContacts.setText(getString(R.string.ADD_CONTACT));
			mBtnContacts.setBackgroundResource(R.drawable.positive_selector);
			mBtnContacts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			break;
		case REMOVE:
			mBtnContacts.setText(getString(R.string.REMOVE_CONTACT));
			mBtnContacts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			mBtnContacts.setBackgroundResource(R.drawable.alert_selector);
			break;
		default:
			break;
		}
	}

	// TODO need to check if success
	private class FindUserByIdAsync extends SpikaAsync<String, Void, User> {

		protected FindUserByIdAsync(Context context) {
			super(context);
		}

		@Override
		protected User doInBackground(String... params) {
			return CouchDB.findUserById(params[0]);
		}

		@Override
		protected void onPostExecute(User user) {
			super.onPostExecute(user);
			if (user != null) {
				mUser = user;
			}
			setUserProfile();
		}

	}

	// TODO need to check if success
	private class FindUserByNameAsync extends SpikaAsync<String, Void, User> {

		protected FindUserByNameAsync(Context context) {
			super(context);
		}

		@Override
		protected User doInBackground(String... params) {
			return CouchDB.findUsersByName(params[0]).get(0);
		}

		@Override
		protected void onPostExecute(User user) {
			super.onPostExecute(user);
			if (user != null) {
				mUser = user;
			}
			setUserProfile();
		}

	}

	private class UpdateContactsAsync extends AsyncTask<Integer, Void, Boolean> {

		private HookUpProgressDialog progressDialog;
		int updateType;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (progressDialog == null) {
				progressDialog = new HookUpProgressDialog(
						UserProfileActivity.this);
			}
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Integer... params) {

			updateType = params[0];
			if (updateType == ADD) {
				return CouchDB.addUserContact(mUser.getId());
			} else if (updateType == REMOVE) {
				return CouchDB.removeUserContact(mUser.getId());
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean updated) {
			if (updated) {
				if (updateType == ADD) {

					setButtonContacts(REMOVE);
				}
				if (updateType == REMOVE) {

					setButtonContacts(ADD);
				}
				UserProfileActivity.this.setResult(RESULT_OK);
			} else {
				Toast.makeText(UserProfileActivity.this,
						getString(R.string.failed_to_update_contacts),
						Toast.LENGTH_SHORT).show();
			}
			progressDialog.dismiss();
		}
	}

	private void openUserWall(User user) {

		UsersManagement.setToUser(user);
		UsersManagement.setToGroup(null);

		SettingsManager.ResetSettings();
		if (WallActivity.gCurrentMessages != null) {
			WallActivity.gCurrentMessages.clear();
		}

		WallActivity.gIsRefreshUserProfile = true;

		UserProfileActivity.this.startActivity(new Intent(
				UserProfileActivity.this, WallActivity.class));

	}

}
