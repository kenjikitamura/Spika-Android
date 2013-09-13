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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.adapters.UsersAdapter;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.Notification;
import com.cloverstudio.spikademo.couchdb.model.RecentActivity;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.couchdb.model.UserSearch;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.extendables.SubMenuActivity;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.view.GenderButton;
import com.cloverstudio.spikademo.view.GenderButton.ButtonType;

/**
 * UsersActivity
 * 
 * Shows a list of users that are added to login user's contacts by default; also contains a submenu with
 * options for searching and exploring users.
 */

public class UsersActivity extends SubMenuActivity {

	private ListView mLvUsers;
	private List<User> mUsers;
	private List<Notification> mUserNotifications;
	private UsersAdapter mUserListAdapter;

	private RelativeLayout mLayoutUserSearch;
	private RelativeLayout mLayoutUserExplore;

	private RelativeLayout mRlMyContacts;
	private RelativeLayout mRlSearch;

	private String mSearchGender;
	private RelativeLayout mRlExplore;
	private TextView mTvNoUsers;
	private TextView mTvFromAge;
	private TextView mTvToAge;

	private int mFullWidth;
	private boolean firstMeasure = true;
	private static final int FROM_AGE = 0;
	private static final int TO_AGE = 100;
	private boolean mOnlineUsersChecked;

	public static final int REQUEST_UPDATE_USERS = 8;
	
	private static final String ALL_USERS = "all_users";
	private static final String CONTACTS = "contacts";
	private static final String SEARCH_USERS = "search_users";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users);
		setSideBar(getString(R.string.USERS));
		initialization();
		initUserSearch();
		initUserExplore();

		showTutorial(getString(R.string.tutorial_users));
	}

	@Override
	protected void enableViews() {
		super.enableViews();
		mLvUsers.setEnabled(true);
	}

	@Override
	protected void disableViews() {
		super.disableViews();
		mLvUsers.setEnabled(false);
	}

	private void initialization() {
		super.setSubMenu();
		mOnlineUsersChecked = false;
		mLvUsers = (ListView) findViewById(R.id.lvUsers);
		mLayoutUserSearch = (RelativeLayout) findViewById(R.id.rlSearchUsers);
		mLayoutUserExplore = (RelativeLayout) findViewById(R.id.rlExploreUsers);

		mRlMyContacts = (RelativeLayout) findViewById(R.id.rlMyContacts);
		mRlMyContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTvNoUsers.setVisibility(View.GONE);
				mTvTitle.setText(getString(R.string.MY_CONTACTS));
				closeSubMenu();
				if (SpikaApp.hasNetworkConnection()) {
					new GetUsersAsync(UsersActivity.this).execute(CONTACTS);
				}
				mLayoutUserSearch.setVisibility(View.GONE);
				mLayoutUserExplore.setVisibility(View.GONE);

			}
		});
		mRlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
		mRlSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTvNoUsers.setVisibility(View.GONE);
				mTvTitle.setText(getString(R.string.USERS));
				closeSubMenu();
				mLayoutUserExplore.setVisibility(View.GONE);
				mLayoutUserSearch.setVisibility(View.VISIBLE);
				
				clearListView();
			}
		});
		mRlExplore = (RelativeLayout) findViewById(R.id.rlExplore);
		mRlExplore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTvNoUsers.setVisibility(View.GONE);
				mTvTitle.setText(getString(R.string.USERS));
				closeSubMenu();
				mLayoutUserSearch.setVisibility(View.GONE);
				mLayoutUserExplore.setVisibility(View.VISIBLE);

				clearListView();
			}
		});

		mTvNoUsers = (TextView) findViewById(R.id.tvNoUsers);
		mTvNoUsers.setVisibility(View.GONE);

		mTvTitle.setText(getString(R.string.MY_CONTACTS));
		closeSubMenu();
		if (SpikaApp.hasNetworkConnection()) {
			// XXX Delete this if no error accures
			//new GetLoginUserAsync(this).execute();
			new GetUsersAsync(UsersActivity.this).execute(CONTACTS);
		}
		mLayoutUserSearch.setVisibility(View.GONE);
		mLayoutUserExplore.setVisibility(View.GONE);
	}

	private void initUserSearch() {

		final EditText etSearchName = (EditText) findViewById(R.id.etSearchName);
		etSearchName.setTypeface(SpikaApp.getTfMyriadPro());
		final Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setTypeface(SpikaApp.getTfMyriadPro(), Typeface.BOLD);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserSearch userSearch = new UserSearch();

				userSearch.setName(etSearchName.getText().toString());

				etSearchName.setText("");
				new SearchUsersAsync(UsersActivity.this).execute(userSearch);

			}
		});

		etSearchName.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {

					UserSearch userSearch = new UserSearch();

					userSearch.setName(etSearchName.getText().toString());

					etSearchName.setText("");

					hideKeyboard();
					new SearchUsersAsync(UsersActivity.this)
							.execute(userSearch);
					return true;
				}
				return false;
			}
		});

	}

	private void initUserExplore() {

		final GenderButton btnMale = (GenderButton) findViewById(R.id.btnMale);
		btnMale.setType(ButtonType.LEFT);
		btnMale.setChecked(false);
		final GenderButton btnFemale = (GenderButton) findViewById(R.id.btnFemale);
		btnFemale.setType(ButtonType.MIDDLE);
		btnFemale.setChecked(false);
		final GenderButton btnAll = (GenderButton) findViewById(R.id.btnAll);
		btnAll.setType(ButtonType.RIGHT);
		btnAll.setChecked(true);

		btnMale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnMale.setChecked(true);
				mSearchGender = Const.MALE;
				btnFemale.setChecked(false);
				btnAll.setChecked(false);

			}
		});
		btnFemale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnMale.setChecked(false);
				mSearchGender = Const.FEMALE;
				btnFemale.setChecked(true);
				btnAll.setChecked(false);

			}
		});
		btnAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnMale.setChecked(false);
				mSearchGender = null;
				btnFemale.setChecked(false);
				btnAll.setChecked(true);

			}
		});

		final Button btnExplore = (Button) findViewById(R.id.btnUserExplore);
		btnExplore.setTypeface(SpikaApp.getTfMyriadPro(), Typeface.BOLD);
		btnExplore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserSearch userSearch = new UserSearch();
				userSearch.setGender(mSearchGender);

				if (mTvFromAge.getText().toString()
						.equals(String.valueOf(FROM_AGE))) {
					userSearch.setFromAge(null);
				} else {
					userSearch.setFromAge(mTvFromAge.getText().toString());
				}
				if (mTvFromAge.getText().toString()
						.equals(String.valueOf(FROM_AGE))) {
					userSearch.setToAge(null);
				} else {
					userSearch.setToAge(mTvToAge.getText().toString());
				}
				if (mOnlineUsersChecked) {
					userSearch.setOnlineStatus(Const.ONLINE);
				} else {
					userSearch.setOnlineStatus("");
				}

				new SearchUsersAsync(UsersActivity.this).execute(userSearch);

			}
		});

		firstMeasure = true;
		final View seekBar = (View) findViewById(R.id.seekBar);
		final RelativeLayout rlSeekBar = (RelativeLayout) findViewById(R.id.rlSeekBar);

		final int MARGIN_SIZE = (int) getResources().getDimension(
				R.dimen.seekBar_margin) + 1;

		mTvFromAge = (TextView) findViewById(R.id.tvFromAge);
		mTvFromAge.setText(String.valueOf(FROM_AGE));
		mTvToAge = (TextView) findViewById(R.id.tvToAge);
		mTvToAge.setText(String.valueOf(TO_AGE));

		rlSeekBar.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN) {

					int xPointTouch = Math.round(event.getX());

					if (firstMeasure) {
						mFullWidth = seekBar.getWidth();
						firstMeasure = false;
					}

					final RelativeLayout.LayoutParams layoutParams = new LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.MATCH_PARENT);
					layoutParams
							.addRule(RelativeLayout.RIGHT_OF, R.id.viewLeft);
					layoutParams
							.addRule(RelativeLayout.LEFT_OF, R.id.viewRight);

					RelativeLayout.LayoutParams currentParams = (RelativeLayout.LayoutParams) seekBar
							.getLayoutParams();

					Rect rectSeekBar = new Rect();
					seekBar.getLocalVisibleRect(rectSeekBar);
					int xPointLeft = MARGIN_SIZE + currentParams.leftMargin;
					int xPointRight = MARGIN_SIZE + mFullWidth
							- currentParams.rightMargin;

					int distanceLeft = Math.abs(xPointTouch - xPointLeft);
					int distanceRight = Math.abs(xPointTouch - xPointRight);

					int leftMargin = currentParams.leftMargin;
					int rightMargin = currentParams.rightMargin;

					if (distanceLeft < distanceRight) {
						leftMargin = 0;
						if (xPointTouch >= MARGIN_SIZE) {
							leftMargin = xPointTouch - MARGIN_SIZE;
						}
						if (xPointTouch >= MARGIN_SIZE + mFullWidth) {
							leftMargin = mFullWidth;
						}
						int fromAge = (int) Math.round((leftMargin * 1.0)
								/ mFullWidth * (TO_AGE - FROM_AGE));
						mTvFromAge.setText(String.valueOf(fromAge));
					} else {
						rightMargin = MARGIN_SIZE + mFullWidth - xPointTouch;
						if (xPointTouch >= MARGIN_SIZE + mFullWidth) {
							rightMargin = 0;
						}
						int toAge = (int) Math
								.round(((mFullWidth - rightMargin) * 1.0)
										/ mFullWidth * (TO_AGE - FROM_AGE));
						mTvToAge.setText(String.valueOf(toAge));
					}

					layoutParams.leftMargin = leftMargin;
					layoutParams.rightMargin = rightMargin;

					seekBar.setLayoutParams(layoutParams);
					seekBar.invalidate();

				}
				return true;
			}
		});

		final CheckBox checkBoxOnlineUsers = (CheckBox) findViewById(R.id.checkboxOnlineUsers);
		checkBoxOnlineUsers.setTypeface(SpikaApp.getTfMyriadPro());
	}

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();

		// Check which checkbox was clicked
		switch (view.getId()) {
		case R.id.checkboxOnlineUsers:
			if (checked)
				mOnlineUsersChecked = true;
			else
				mOnlineUsersChecked = false;
			break;
		default:
			break;
		}
	}

	private class GetUsersAsync extends SpikaAsync<String, Void, List<User>> {

		String searchType = "";

		protected GetUsersAsync(Context context) {
			super(context);
		}

		HookUpProgressDialog progressDialog = new HookUpProgressDialog(
				UsersActivity.this);

		@Override
		protected void onPreExecute() {
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected List<User> doInBackground(String... params) {

			searchType = params[0];

			if (params[0].equals(ALL_USERS)) {
				return CouchDB.findAllUsers();
			} else if (params[0].equals(SEARCH_USERS)) {
				return CouchDB.findUsersByName(params[1]);
			} else if (params[0].equals(CONTACTS)) {
				return CouchDB.findUserContacts(UsersManagement.getLoginUser()
						.getId());
			} else {
				return CouchDB.findAllUsers();
			}
		}

		@Override
		protected void onPostExecute(List<User> result) {

			if (searchType.equals(CONTACTS)
					&& (result == null || result.size() == 0)) {
				UsersActivity.this
						.showTutorialOnceAfterBoot(getString(R.string.tutorial_nocontact));
			}

			if (UsersManagement.getLoginUser().getActivitySummary() != null) {
				for (RecentActivity recentActivity : UsersManagement
						.getLoginUser().getActivitySummary()
						.getRecentActivityList()) {
					if (recentActivity.getTargetType().equals(Const.USER)) {
						mUserNotifications = recentActivity.getNotifications();
					}
				}
			}

			mUsers = (ArrayList<User>) result;

			if (mUsers.size() == 0) {
				mTvNoUsers.setVisibility(View.VISIBLE);
				mTvNoUsers.setText(getString(R.string.no_users_in_contacts));
			} else {
				mTvNoUsers.setVisibility(View.GONE);
			}

			// sorting users by name
			Collections.sort(mUsers, new Comparator<User>() {
				@Override
				public int compare(User lhs, User rhs) {
					return lhs.getName().compareToIgnoreCase(rhs.getName());
				}
			});

			if (mUserListAdapter == null) {
				mUserListAdapter = new UsersAdapter(UsersActivity.this, mUsers,
						mUserNotifications);
				mLvUsers.setAdapter(mUserListAdapter);
				mLvUsers.setOnItemClickListener(mUserListAdapter);
			} else {
				mUserListAdapter.setItems(mUsers, mUserNotifications);
			}

			progressDialog.dismiss();

		}
	}

	private void clearListView() {
		mUserListAdapter = new UsersAdapter(UsersActivity.this,
				new ArrayList<User>(), new ArrayList<Notification>());
		mLvUsers.setAdapter(mUserListAdapter);
	}

	private class SearchUsersAsync extends
			SpikaAsync<UserSearch, Void, List<User>> {

		protected SearchUsersAsync(Context context) {
			super(context);
		}

		HookUpProgressDialog progressDialog = new HookUpProgressDialog(
				UsersActivity.this);

		@Override
		protected void onPreExecute() {
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected List<User> doInBackground(UserSearch... params) {
			return CouchDB.searchUsers(params[0]);
		}

		@Override
		protected void onPostExecute(List<User> result) {

			if (result != null) {
				if (UsersManagement.getLoginUser().getActivitySummary() != null) {
					for (RecentActivity recentActivity : UsersManagement
							.getLoginUser().getActivitySummary()
							.getRecentActivityList()) {
						if (recentActivity.getTargetType().equals(Const.USER)) {
							mUserNotifications = recentActivity
									.getNotifications();
						}
					}
				}
				mUsers = (ArrayList<User>) result;

				if (mUsers.size() == 0) {
					mTvNoUsers.setVisibility(View.VISIBLE);
					mTvNoUsers.setText(getString(R.string.no_users_found));
				} else {
					mTvNoUsers.setVisibility(View.GONE);
				}

				// sorting users by name
				Collections.sort(mUsers, new Comparator<User>() {
					@Override
					public int compare(User lhs, User rhs) {
						return lhs.getName().compareToIgnoreCase(rhs.getName());
					}
				});

				if (mUserListAdapter == null) {
					mUserListAdapter = new UsersAdapter(UsersActivity.this,
							mUsers, mUserNotifications);
					mLvUsers.setAdapter(mUserListAdapter);
					mLvUsers.setOnItemClickListener(mUserListAdapter);
				} else {
					mUserListAdapter.setItems(mUsers, mUserNotifications);
				}
			}

			progressDialog.dismiss();

		}
	}

	@Override
	public void onBackPressed() {
		if (mLayoutUserSearch.getVisibility() == View.VISIBLE) {
			mLayoutUserSearch.setVisibility(View.GONE);
			if (SpikaApp.hasNetworkConnection()) {
				new GetUsersAsync(UsersActivity.this).execute(CONTACTS);
			}
		} else if (mLayoutUserExplore.getVisibility() == View.VISIBLE) {
			mLayoutUserExplore.setVisibility(View.GONE);
			if (SpikaApp.hasNetworkConnection()) {
				new GetUsersAsync(UsersActivity.this).execute(CONTACTS);
			}
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void refreshActivitySummaryViews() {
		super.refreshActivitySummaryViews();

		if (UsersManagement.getLoginUser().getActivitySummary() != null) {
			for (RecentActivity recentActivity : UsersManagement.getLoginUser()
					.getActivitySummary().getRecentActivityList()) {
				if (recentActivity.getTargetType().equals(Const.USER)) {
					mUserNotifications = recentActivity.getNotifications();
				}
			}
		}

		if (mUserListAdapter != null) {
			mUserListAdapter.setItems(mUsers, mUserNotifications);
		}
	}

	@Override
	protected void setObjectsNull() {
		mLvUsers.setAdapter(null);
		mLvUsers = null;
		mLayoutUserSearch = null;
		mUserListAdapter = null;
		super.setObjectsNull();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Check which request we're responding to
		if (requestCode == REQUEST_UPDATE_USERS) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				mTvNoUsers.setVisibility(View.GONE);
				mTvTitle.setText(getString(R.string.MY_CONTACTS));
				if (SpikaApp.hasNetworkConnection()) {
					new GetUsersAsync(UsersActivity.this).execute(CONTACTS);
				}
				mLayoutUserSearch.setVisibility(View.GONE);
				mLayoutUserExplore.setVisibility(View.GONE);
			}
		}
	}

}