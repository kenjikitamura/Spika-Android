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
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.adapters.GroupCategoriesAdapter;
import com.cloverstudio.spikademo.adapters.GroupsAdapter;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.Group;
import com.cloverstudio.spikademo.couchdb.model.GroupCategory;
import com.cloverstudio.spikademo.couchdb.model.GroupSearch;
import com.cloverstudio.spikademo.couchdb.model.Notification;
import com.cloverstudio.spikademo.couchdb.model.RecentActivity;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.extendables.SubMenuActivity;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;

/**
 * GroupsActivity
 * 
 * Shows a list of groups that user is subscribed to by default; also contains a submenu with
 * options for searching groups, viewing groups by categories and creating a new
 * group.
 */

public class GroupsActivity extends SubMenuActivity {

	private List<Group> mGroups;
	private List<GroupCategory> mGroupCategories;

	private ListView mLvGroups;
	private ListView mLvGroupCategories;
	// private Button mBtnCreateGroup;

	private Button mBtnSearchGroups;
	private EditText mEtSearchGroups;
	private GroupsAdapter mGroupListAdapter;
	private GroupCategoriesAdapter mGroupCategoriesAdapter;
	private List<Notification> mGroupNotifications;
	private RelativeLayout mRlSearchGroups;
	private boolean flagToCreateGroup = false;

	private RelativeLayout mRlFavoriteGroups;
	private RelativeLayout mRlSearch;
	private RelativeLayout mRlCreateGroup;
	private RelativeLayout mRlCategories;
	private TextView mTvNoGroups;

	public static final int REQUEST_UPDATE_GROUPS = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		setSideBar(this.getString(R.string.GROUPS));
		Initialization();
		OnClickListeners();

		showTutorial(getString(R.string.tutorial_groups));

	}

	@Override
	protected void onResume() {
		if (flagToCreateGroup) {
			// if you are comming back from create group then refresh groups
			// list
			mTvTitle.setText(getString(R.string.FAVORITES));
			new GetGroupsAsync(GroupsActivity.this).execute(FAVORITES);
			mRlSearchGroups.setVisibility(View.GONE);
			flagToCreateGroup = false;
		}
		super.onResume();
	}

	@Override
	protected void enableViews() {
		super.enableViews();
		mBtnSearchGroups.setEnabled(true);
		mEtSearchGroups.setEnabled(true);
		mLvGroups.setEnabled(true);
		mLvGroupCategories.setEnabled(true);
	}

	@Override
	protected void disableViews() {
		super.disableViews();
		mBtnSearchGroups.setEnabled(false);
		mEtSearchGroups.setEnabled(false);
		mLvGroups.setEnabled(false);
		mLvGroupCategories.setEnabled(false);
	}

	@Override
	protected void setObjectsNull() {
		mGroupListAdapter = null;
		mGroupCategoriesAdapter = null;
		if (mGroupNotifications != null) {
			mGroupNotifications.clear();
			mGroupNotifications = null;
		}
		mLvGroups.setAdapter(null);
		mLvGroups = null;
		mLvGroupCategories.setAdapter(null);
		mLvGroupCategories = null;
		if (mGroups != null) {
			mGroups.clear();
			mGroups = null;
		}
		if (mGroupCategories != null) {
			mGroupCategories.clear();
			mGroupCategories = null;
		}
		super.setObjectsNull();
	}

	private void Initialization() {
		super.setSubMenu();

		mLvGroups = (ListView) findViewById(R.id.lvGroups);
		mLvGroupCategories = (ListView) findViewById(R.id.lvGroupCategories);
		mLvGroupCategories.setVisibility(View.GONE);

		mBtnSearchGroups = (Button) findViewById(R.id.btnSearch);
		mEtSearchGroups = (EditText) findViewById(R.id.etSearchName);
		mEtSearchGroups.setTypeface(SpikaApp.getTfMyriadPro());

		mRlSearchGroups = (RelativeLayout) findViewById(R.id.rlSearchGroups);
		mRlSearchGroups.setVisibility(View.GONE);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		mRlFavoriteGroups = (RelativeLayout) findViewById(R.id.rlFavoriteGroups);
		mRlFavoriteGroups.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeSubMenu();
				mLvGroupCategories.setVisibility(View.GONE);
				mTvNoGroups.setVisibility(View.GONE);
				mTvTitle.setText(getString(R.string.FAVORITES));
				if (SpikaApp.hasNetworkConnection()) {
					new GetGroupsAsync(GroupsActivity.this).execute(FAVORITES);
				}
				mRlSearchGroups.setVisibility(View.GONE);

			}
		});

		mRlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
		mRlSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeSubMenu();
				mLvGroupCategories.setVisibility(View.GONE);
				mRlSearchGroups.setVisibility(View.VISIBLE);
				mTvTitle.setText(getString(R.string.GROUPS));

				clearListView();
			}
		});

		mRlCreateGroup = (RelativeLayout) findViewById(R.id.rlCreate);
		mRlCreateGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeSubMenu();
				Intent intent = new Intent(GroupsActivity.this,
						CreateGroupActivity.class);
				GroupsActivity.this.startActivity(intent);
				flagToCreateGroup = true;

			}
		});

		mRlCategories = (RelativeLayout) findViewById(R.id.rlCategories);
		mRlCategories.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeSubMenu();
				mTvTitle.setText(getString(R.string.CATEGORIES));
				mTvNoGroups.setVisibility(View.GONE);
				mRlSearchGroups.setVisibility(View.GONE);
				new GetGroupCategoriesAsync(GroupsActivity.this).execute();

			}
		});

		mTvNoGroups = (TextView) findViewById(R.id.tvNoGroups);
		mTvNoGroups.setVisibility(View.GONE);
		mTvTitle.setText(getString(R.string.FAVORITE_GROUPS));
		if (SpikaApp.hasNetworkConnection()) {
			new GetGroupsAsync(GroupsActivity.this).execute(FAVORITES);
		}
		mRlSearchGroups.setVisibility(View.GONE);
	}

	private void clearListView() {
		mGroupListAdapter = new GroupsAdapter(GroupsActivity.this,
				new ArrayList<Group>(), new ArrayList<Notification>());
		mLvGroups.setAdapter(mGroupListAdapter);
	}

	@Override
	public void onBackPressed() {
		if (mRlSearchGroups.getVisibility() == View.VISIBLE) {
			mRlSearchGroups.setVisibility(View.GONE);
			if (SpikaApp.hasNetworkConnection()) {
				new GetGroupsAsync(GroupsActivity.this).execute(FAVORITES);
			}
		} else {
			super.onBackPressed();
		}
	}

	private void OnClickListeners() {

		mBtnSearchGroups.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				GroupSearch groupSearch = new GroupSearch();
				groupSearch.setName(mEtSearchGroups.getText().toString());
				new SearchGroupsAsync(GroupsActivity.this).execute(groupSearch);

			}
		});

		mEtSearchGroups.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					GroupSearch groupSearch = new GroupSearch();
					groupSearch.setName(mEtSearchGroups.getText().toString());
					new SearchGroupsAsync(GroupsActivity.this)
							.execute(groupSearch);
					return true;
				}
				return false;
			}
		});
	}

	public void getGroupsForCategory(GroupCategory groupCategory) {
		new GetGroupsAsync(GroupsActivity.this).execute(CATEGORY,
				groupCategory.getId());
		mTvTitle.setText(groupCategory.getTitle().toUpperCase());
	}

	private static final String ALL_GROUPS = "all_groups";
	private static final String FAVORITES = "favorites";
	private static final String CATEGORY = "category";

	private class GetGroupsAsync extends SpikaAsync<String, Void, List<Group>> {

		String searchType = "";

		protected GetGroupsAsync(Context context) {
			super(context);
		}

		HookUpProgressDialog mProgressDialog = new HookUpProgressDialog(
				GroupsActivity.this);

		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected List<Group> doInBackground(String... params) {

			searchType = params[0];

			if (UsersManagement.getLoginUser().getActivitySummary() != null) {

				for (RecentActivity recentActivity : UsersManagement
						.getLoginUser().getActivitySummary()
						.getRecentActivityList()) {
					if (recentActivity.getTargetType().equals(Const.GROUP)) {
						mGroupNotifications = recentActivity.getNotifications();
					}
				}

			}

			if (params[0].equals(ALL_GROUPS)) {
				return CouchDB.findAllGroups();
			} else if (params[0].equals(FAVORITES)) {
				return CouchDB.findUserFavoriteGroups(UsersManagement
						.getLoginUser().getId());
			} else if (params[0].equals(CATEGORY)) {
				return CouchDB.findGroupByCategoryId(params[1]);
			}
			return CouchDB.findAllGroups();
		}

		@Override
		protected void onPostExecute(List<Group> result) {

			if (result != null) {
				mGroups = (ArrayList<Group>) result;

				if (searchType.equals(FAVORITES)
						&& (result == null || result.size() == 0)) {
					GroupsActivity.this
							.showTutorialOnceAfterBoot(getString(R.string.tutorial_nofavorite));
				}

				mLvGroupCategories.setVisibility(View.GONE);
				mLvGroups.setVisibility(View.VISIBLE);

				if (mGroups.size() == 0) {
					mTvNoGroups.setVisibility(View.VISIBLE);
					mTvNoGroups
							.setText(getString(R.string.no_groups_in_favorites));
				} else {
					mTvNoGroups.setVisibility(View.GONE);
				}

				// sorting groups by name
				Collections.sort(mGroups, new Comparator<Group>() {
					@Override
					public int compare(Group lhs, Group rhs) {
						return lhs.getName().compareToIgnoreCase(rhs.getName());
					}
				});

				if (mGroupListAdapter == null) {
					mGroupListAdapter = new GroupsAdapter(GroupsActivity.this,
							mGroups, mGroupNotifications);
					mLvGroups.setAdapter(mGroupListAdapter);
					mLvGroups.setOnItemClickListener(mGroupListAdapter);
				} else {
					mGroupListAdapter.setItems(mGroups, mGroupNotifications);
				}
			}
			mProgressDialog.dismiss();
		}
	}

	private class SearchGroupsAsync extends
			SpikaAsync<GroupSearch, Void, List<Group>> {

		protected SearchGroupsAsync(Context context) {
			super(context);
		}

		HookUpProgressDialog mProgressDialog = new HookUpProgressDialog(
				GroupsActivity.this);

		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected List<Group> doInBackground(GroupSearch... params) {

			return CouchDB.searchGroups(params[0]);
		}

		@Override
		protected void onPostExecute(List<Group> result) {
			mGroups = (ArrayList<Group>) result;

			mLvGroupCategories.setVisibility(View.GONE);
			mLvGroups.setVisibility(View.VISIBLE);

			if (mGroups.size() == 0) {
				mTvNoGroups.setVisibility(View.VISIBLE);
				mTvNoGroups.setText(getString(R.string.no_groups_found));
			} else {
				mTvNoGroups.setVisibility(View.GONE);
			}

			// sorting groups by name
			Collections.sort(mGroups, new Comparator<Group>() {
				@Override
				public int compare(Group lhs, Group rhs) {
					return lhs.getName().compareToIgnoreCase(rhs.getName());
				}
			});

			if (mGroupListAdapter == null) {
				mGroupListAdapter = new GroupsAdapter(GroupsActivity.this,
						mGroups, mGroupNotifications);
				mLvGroups.setAdapter(mGroupListAdapter);
				mLvGroups.setOnItemClickListener(mGroupListAdapter);
			} else {
				mGroupListAdapter.setItems(mGroups, mGroupNotifications);
			}
			mProgressDialog.dismiss();
		}
	}

	private class GetGroupCategoriesAsync extends
			SpikaAsync<GroupSearch, Void, List<GroupCategory>> {

		protected GetGroupCategoriesAsync(Context context) {
			super(context);
		}

		HookUpProgressDialog mProgressDialog = new HookUpProgressDialog(
				GroupsActivity.this);

		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected List<GroupCategory> doInBackground(GroupSearch... params) {

			return CouchDB.findGroupCategories();
		}

		@Override
		protected void onPostExecute(List<GroupCategory> result) {
			mGroupCategories = (ArrayList<GroupCategory>) result;

			mLvGroupCategories.setVisibility(View.VISIBLE);
			mLvGroups.setVisibility(View.GONE);

			if (mGroupCategoriesAdapter == null) {
				mGroupCategoriesAdapter = new GroupCategoriesAdapter(
						GroupsActivity.this, mGroupCategories);
				mLvGroupCategories.setAdapter(mGroupCategoriesAdapter);
				mLvGroupCategories
						.setOnItemClickListener(mGroupCategoriesAdapter);
			} else {
				mGroupCategoriesAdapter.setItems(mGroupCategories);
			}
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Check which request we're responding to
		if (requestCode == REQUEST_UPDATE_GROUPS) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				mLvGroupCategories.setVisibility(View.GONE);
				mTvNoGroups.setVisibility(View.GONE);
				mTvTitle.setText(getString(R.string.FAVORITES));
				if (SpikaApp.hasNetworkConnection()) {
					new GetGroupsAsync(GroupsActivity.this).execute(FAVORITES);
				}
				mRlSearchGroups.setVisibility(View.GONE);
			}
		}
	}

}
