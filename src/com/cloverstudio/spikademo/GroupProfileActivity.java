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
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.Group;
import com.cloverstudio.spikademo.couchdb.model.GroupCategory;
import com.cloverstudio.spikademo.couchdb.model.GroupSearch;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.dialog.HookUpAlertDialog;
import com.cloverstudio.spikademo.dialog.HookUpDialog;
import com.cloverstudio.spikademo.dialog.HookUpGroupPasswordDialog;
import com.cloverstudio.spikademo.dialog.HookUpPasswordDialog;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.extendables.SpikaActivity;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.SettingsManager;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * GroupProfileActivity
 * 
 * Shows profile of a group; if user owns that group, profile will be editable,
 * otherwise it will have an option for user to subscribe or unsubscribe from the group.
 */

public class GroupProfileActivity extends SpikaActivity {

	private ImageView mIvGroupImage;
	private TextView mTvGroupName;
	private TextView mTvGroupOwner;
	private TextView mTvGroupDescription;
	private EditText mEtGroupDescription;
	private User mGroupOwner;
	private RelativeLayout mRlControlButtons;
	private RelativeLayout mRlBody;
	private ProgressBar mPbLoading;
	private Button mBtnDeleteGroup;
	private Button mBtnFavorites;
	private Button mBtnBack;
	private Button mBtnSaveGroup;
	private String mGroupDescription;
	private String mGroupName;
	private String mGroupPassword;
	private String mGroupAvatarId;
	private String mGroupAvatarThumbId;
	public boolean mIsDeletedDone;
	public static Bitmap gGroupImage = null;
	public static String gGroupImagePath = null;

	private static final int UPDATE_IMAGE_REQUEST_CODE = 1000;
	private static final int ADD = 1001;
	private static final int REMOVE = 1002;

	private boolean mAddRemoveControl = false;

	private HookUpDialog mDeleteAlertDialog;
	private HookUpPasswordDialog mPasswordDialog;

	private Button mBtnEdit;
	private EditText mEtGroupName;
	private RelativeLayout mRlGroupOwner;
	private RelativeLayout mRlGroupDescription;
	private RelativeLayout mRlGroupPassword;
	private ImageView mIvArrow;

	private Spinner mSpinnerCategory;
	private List<GroupCategory> mGroupCategories;
	private Button mBtnOpenWall;
	private EditText mEtGroupPassword;

	private Group mGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_profile);
		setResult(RESULT_CANCELED);
		initialization();

		showTutorial(getString(R.string.tutorial_groupprofile));

		new GetOwnerAsync(this).execute();
		new GetGroupCategoriesAsync(this).execute();
		// new GetLoginUserAsync(this).execute();
		// new GetGroupAsync(this).execute(mGroup.getId());

		mIsDeletedDone = false;
	}

	private enum ProfileMode {
		EDIT, CANCEL

	}

	private void setProfileMode(ProfileMode newProfileMode) {
		switch (newProfileMode) {
		case EDIT:
			mRlGroupPassword.setVisibility(View.VISIBLE);
			mEtGroupPassword.setEnabled(true);
			mSpinnerCategory.setClickable(true);
			mRlGroupDescription.setVisibility(View.VISIBLE);
			mTvGroupName.setText(getString(R.string.GROUP_NAME));
			mEtGroupName.setVisibility(View.VISIBLE);
			mEtGroupName.setText(mGroupName);
			mEtGroupName.setEnabled(true);
			mIvGroupImage.setEnabled(true);
			mTvGroupDescription.setVisibility(View.GONE);
			mEtGroupDescription.setVisibility(View.VISIBLE);
			mRlControlButtons.setVisibility(View.VISIBLE);
			mBtnEdit.setBackgroundResource(R.drawable.alert_selector);
			mBtnEdit.setText(getString(R.string.CANCEL));
			mBtnEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setProfileMode(ProfileMode.CANCEL);

				}
			});
			break;
		case CANCEL:
			if (mGroupCategories != null) {
				String categoryId = mGroup.getCategoryId();
				for (GroupCategory category : mGroupCategories) {
					if (category.getId().equals(categoryId)) {
						int position = mGroupCategories.indexOf(category);
						mSpinnerCategory.setSelection(position);
					}
				}
			}
			mSpinnerCategory.setClickable(false);
			mTvGroupName.setText(getString(R.string.GROUP_NAME));
			mEtGroupName.setVisibility(View.VISIBLE);
			mEtGroupName.setText(mGroupName);
			mEtGroupName.setEnabled(false);
			mRlControlButtons.setVisibility(View.GONE);
			mTvGroupDescription.setVisibility(View.VISIBLE);
			mEtGroupDescription.setVisibility(View.GONE);
			if (mGroupDescription != null && !mGroupDescription.equals("")) {
				mRlGroupDescription.setVisibility(View.VISIBLE);
				mTvGroupDescription.setText(mGroupDescription);
				mEtGroupDescription.setText(mGroupDescription);
			} else {
				mRlGroupDescription.setVisibility(View.GONE);
			}
			mBtnEdit.setBackgroundResource(R.drawable.positive_selector);
			mBtnEdit.setText(getString(R.string.EDIT));
			mBtnEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setProfileMode(ProfileMode.EDIT);

				}
			});

			mGroupAvatarId = mGroup.getAvatarFileId();
			mGroupAvatarThumbId = mGroup.getAvatarThumbFileId();
			Utils.displayImage(mGroupAvatarId, mIvGroupImage, mPbLoading,
					ImageLoader.LARGE, R.drawable.group_stub_large, false);

			mRlGroupPassword.setVisibility(View.VISIBLE);

			if (mGroupPassword != null && !mGroupPassword.equals("")) {
				mEtGroupPassword.setText(mGroupPassword);
			} else {
				mEtGroupPassword.setText(mGroupPassword);
			}

			mEtGroupPassword.setEnabled(false);

			break;
		default:
			break;
		}
	}

	@Override
	protected void setObjectsNull() {
		if (gGroupImage != null) {
			gGroupImage.recycle();
			gGroupImage = null;

		}
		unbindDrawables(findViewById(R.id.ivGroupImage));
		mRlControlButtons = null;
		mRlBody = null;
		super.setObjectsNull();
	}

	private void initialization() {

		// If opened from link hookup://group/[ime grupa]
		if (getIntent().getBooleanExtra(Const.GROUP_URI_INTENT, false)) {
			getIntent().removeExtra(Const.GROUP_URI_INTENT);
			String groupName = getIntent().getStringExtra(Const.GROUP_URI_NAME);
			try {
				mGroup = new GetGroupByNameAsync(this).execute(groupName).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			// If opened from another activity
			mGroup = UsersManagement.getToGroup();
		}

		mRlGroupDescription = (RelativeLayout) findViewById(R.id.rlGroupDescription);
		mPasswordDialog = new HookUpPasswordDialog(this, true);
		mIvGroupImage = (ImageView) findViewById(R.id.ivGroupImage);
		mPbLoading = (ProgressBar) findViewById(R.id.pbLoadingForImage);

		Utils.displayImage(mGroup.getAvatarFileId(), mIvGroupImage, mPbLoading,
				ImageLoader.LARGE, R.drawable.group_stub, false);

		mTvGroupName = (TextView) findViewById(R.id.tvGroupName);
		mRlControlButtons = (RelativeLayout) findViewById(R.id.rlControlButtons);

		mBtnDeleteGroup = (Button) findViewById(R.id.btnDeleteGroup);
		mBtnDeleteGroup.setTypeface(SpikaApp.getTfMyriadProBold(),
				Typeface.BOLD);

		mBtnFavorites = (Button) findViewById(R.id.btnFavorites);
		mBtnFavorites
				.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnBack = (Button) findViewById(R.id.btnBack);
		mBtnBack.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GroupProfileActivity.this.finish();
			}
		});
		mTvGroupOwner = (TextView) findViewById(R.id.tvGroupOwner);
		mTvGroupDescription = (TextView) findViewById(R.id.tvGroupDescription);
		mEtGroupDescription = (EditText) findViewById(R.id.etGroupDescription);
		mBtnSaveGroup = (Button) findViewById(R.id.btnSaveGroup);

		mRlBody = (RelativeLayout) findViewById(R.id.rlBody);
		mRlBody.bringToFront();

		mBtnEdit = (Button) findViewById(R.id.btnEdit);
		mBtnEdit.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);

		mEtGroupName = (EditText) findViewById(R.id.etGroupName);
		mEtGroupName.setTypeface(SpikaApp.getTfMyriadPro());

		mIvArrow = (ImageView) findViewById(R.id.ivArrow);
		mRlGroupOwner = (RelativeLayout) findViewById(R.id.rlGroupOwner);

		mSpinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);

		mBtnOpenWall = (Button) findViewById(R.id.btnOpenWall);
		mBtnOpenWall.setTypeface(SpikaApp.getTfMyriadPro());
		mBtnOpenWall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openGroupWall(mGroup);

			}
		});
		mRlGroupPassword = (RelativeLayout) findViewById(R.id.rlGroupPassword);
		mEtGroupPassword = (EditText) findViewById(R.id.etGroupPassword);
		mEtGroupPassword.setTypeface(SpikaApp.getTfMyriadPro());
		mEtGroupPassword.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mPasswordDialog.show();
				hideKeyboard();
				return false;
			}
		});

		mEtGroupPassword.setEnabled(false);
		mEtGroupName.setEnabled(false);
	}

	private void setupProfile() {

		mGroupName = mGroup.getName();
		mTvGroupName.setText(getString(R.string.GROUP_NAME));
		mEtGroupName.setText(mGroup.getName());
		mTvGroupOwner.setText(mGroupOwner.getName());
		mGroupDescription = mGroup.getDescription();
		mTvGroupDescription.setText(mGroupDescription);
		mEtGroupDescription.setText(mGroupDescription);
		mGroupPassword = mGroup.getPassword();
		mGroupAvatarId = mGroup.getAvatarFileId();
		mGroupAvatarThumbId = mGroup.getAvatarThumbFileId();

		if (mGroupPassword != null && !mGroupPassword.equals("")) {
			mEtGroupPassword.setText(mGroupPassword);
		} else {
			mEtGroupPassword.setText(null);
		}

		boolean userOwnsGroup = mGroupOwner.getId().equals(
				UsersManagement.getLoginUser().getId());
		if (userOwnsGroup) {
			setupMyGroupProfile();
		} else {
			setupOtherGroupProfile();
		}

	}

	private void setupMyGroupProfile() {

		mIvArrow.setVisibility(View.GONE);
		mRlControlButtons.setVisibility(View.VISIBLE);

		mBtnEdit.setVisibility(View.VISIBLE);
		mBtnFavorites.setVisibility(View.GONE);

		/* Delete group dialog :start */
		mDeleteAlertDialog = new HookUpDialog(this);
		mDeleteAlertDialog.setMessage(getString(R.string.delete_group_message));
		mDeleteAlertDialog.setOnButtonClickListener(HookUpDialog.BUTTON_OK,
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						new DeleteGroupAsync(GroupProfileActivity.this)
								.execute(mGroup.getId(), mGroup.getRev());
						mDeleteAlertDialog.dismiss();

					}
				});
		mDeleteAlertDialog.setOnButtonClickListener(HookUpDialog.BUTTON_CANCEL,
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mDeleteAlertDialog.dismiss();
					}
				});
		/* :end */

		mIvGroupImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getImageDialog();
			}
		});

		mBtnSaveGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mGroupName = mEtGroupName.getText().toString();
				mGroupDescription = mEtGroupDescription.getText().toString();
				if (nameIsValid(mGroupName) == true) {
					new AvailabilityAsync(GroupProfileActivity.this).execute();
				}

			}
		});

		mBtnDeleteGroup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDeleteAlertDialog.show();
			}
		});

		setProfileMode(ProfileMode.CANCEL);
		mRlGroupOwner.setClickable(false);

		if (mGroup.getDescription() == null
				|| mGroup.getDescription().equals("")) {
			mRlGroupDescription.setVisibility(View.GONE);
		} else {
			mRlGroupDescription.setVisibility(View.VISIBLE);
			mTvGroupDescription.setText(mGroup.getDescription());
		}

	}

	private boolean nameIsValid(String name) {
		String nameResult = Utils.checkName(this, name);

		if (!nameResult.equals(getString(R.string.name_ok))) {
			Toast.makeText(this, nameResult, Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}

	private void setupOtherGroupProfile() {

		mIvArrow.setVisibility(View.VISIBLE);
		// mRlGroupPassword.setVisibility(View.GONE);

		mEtGroupDescription.setVisibility(View.GONE);
		mTvGroupDescription.setVisibility(View.VISIBLE);
		if (mGroup.getDescription() == null
				|| mGroup.getDescription().equals("")) {
			mRlGroupDescription.setVisibility(View.GONE);
		} else {
			mRlGroupDescription.setVisibility(View.VISIBLE);
			mTvGroupDescription.setText(mGroup.getDescription());
		}
		mBtnFavorites.setVisibility(View.VISIBLE);
		mRlControlButtons.setVisibility(View.GONE);
		mBtnEdit.setVisibility(View.GONE);

		for (String id : UsersManagement.getLoginUser().getGroupIds()) {
			if (id.equals(mGroup.getId())) {
				mAddRemoveControl = true;
			}
		}

		if (mAddRemoveControl) {
			setButtonFavorites(REMOVE);
		} else {
			setButtonFavorites(ADD);
		}

		final HookUpAlertDialog alertDialog = new HookUpAlertDialog(this);

		mBtnFavorites.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mAddRemoveControl) {
					new RemoveFromFavoritesAsync(GroupProfileActivity.this,
							true).execute(mGroup.getId());
				} else {

					if (UsersManagement.getLoginUser().canAddFavorite()) {
						new AddToFavoritesAsync(GroupProfileActivity.this)
								.execute(mGroup.getId());
					} else {
						alertDialog
								.show(getString(R.string.max_favorites_alert));
					}

				}
			}
		});

		mRlGroupOwner.setClickable(true);
		mRlGroupOwner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent profileIntent = new Intent(GroupProfileActivity.this,
						UserProfileActivity.class);
				profileIntent.putExtra("user_id", mGroup.getUserId());
				startActivity(profileIntent);

			}
		});

	}

	private void getImageDialog() {
		final Dialog imageDialog = new Dialog(GroupProfileActivity.this,
				R.style.TransparentDialogTheme);
		imageDialog.getWindow().setGravity(Gravity.BOTTOM);
		imageDialog.setContentView(R.layout.dialog_get_image);
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		Window window = imageDialog.getWindow();
		layoutParams.copyFrom(window.getAttributes());
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(layoutParams);

		final Button btnGallery = (Button) imageDialog
				.findViewById(R.id.btnGallery);
		btnGallery.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		btnGallery.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent galleryIntent = new Intent(GroupProfileActivity.this,
						CameraCropActivity.class);
				galleryIntent.putExtra("type", "gallery");
				galleryIntent.putExtra("groupUpdate", true);
				GroupProfileActivity.this.startActivityForResult(galleryIntent,
						UPDATE_IMAGE_REQUEST_CODE);
				imageDialog.dismiss();

			}
		});

		final Button btnCamera = (Button) imageDialog
				.findViewById(R.id.btnCamera);
		btnCamera.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		btnCamera.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent cameraIntent = new Intent(GroupProfileActivity.this,
						CameraCropActivity.class);
				cameraIntent.putExtra("type", "camera");
				cameraIntent.putExtra("groupUpdate", true);
				GroupProfileActivity.this.startActivityForResult(cameraIntent,
						UPDATE_IMAGE_REQUEST_CODE);
				imageDialog.dismiss();

			}
		});

		final Button btnRemovePhoto = (Button) imageDialog
				.findViewById(R.id.btnRemovePhoto);
		btnRemovePhoto.setTypeface(SpikaApp.getTfMyriadProBold(),
				Typeface.BOLD);
		btnRemovePhoto.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				mGroupAvatarId = "";
				gGroupImage = null;
				Utils.displayImage(mGroupAvatarId, mIvGroupImage, mPbLoading,
						ImageLoader.LARGE, R.drawable.group_stub_large, false);
				imageDialog.dismiss();

			}
		});

		imageDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == UPDATE_IMAGE_REQUEST_CODE) {
			if (gGroupImage != null) {
				mIvGroupImage.setImageBitmap(gGroupImage);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class GetOwnerAsync extends SpikaAsync<Void, Void, User> {

		protected GetOwnerAsync(Context context) {
			super(context);
		}

		@Override
		protected User doInBackground(Void... params) {
			mGroupOwner = CouchDB.findUserById(mGroup.getUserId());
			return mGroupOwner;
		}

		@Override
		protected void onPostExecute(User result) {
			setupProfile();
			super.onPostExecute(result);
		}

	}

	private void setButtonFavorites(int updateType) {
		switch (updateType) {
		case ADD:
			mBtnFavorites.setText(getString(R.string.ADD_TO_FAVORITES));
			mBtnFavorites.setBackgroundResource(R.drawable.positive_selector);
			mBtnFavorites.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			break;
		case REMOVE:
			mBtnFavorites.setText(getString(R.string.REMOVE_FROM_FAVORITES));
			mBtnFavorites.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			mBtnFavorites.setBackgroundResource(R.drawable.alert_selector);
			break;
		default:
			break;
		}
	}

	private class GetGroupAsync extends SpikaAsync<String, Void, Group> {

		protected GetGroupAsync(Context context) {
			super(context);
		}

		@Override
		protected Group doInBackground(String... params) {
			String id = params[0];
			return CouchDB.findGroupById(id);
		}

		@Override
		protected void onPostExecute(Group group) {
			mGroup = group;

			mGroupDescription = group.getDescription();
			mGroupName = group.getName();

			mTvGroupDescription.setText(mGroupDescription);
			mEtGroupDescription.setText(mGroupDescription);
		}
	}

	private class AvailabilityAsync extends SpikaAsync<Group, Void, Group> {

		private Group mGroupFound;
		private HookUpProgressDialog mProgressDialog;

		protected AvailabilityAsync(Context context) {
			super(context);
			mProgressDialog = new HookUpProgressDialog(context);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Group doInBackground(Group... params) {

			mGroupFound = CouchDB.getGroupByName(mGroupName);

			return mGroupFound;

		}

		@Override
		protected void onPostExecute(Group result) {
			super.onPostExecute(result);

			if (mGroupFound != null
					&& !mGroupFound.getId().equals(mGroup.getId())) {
				mProgressDialog.dismiss();
				Toast.makeText(mContext, getString(R.string.groupname_taken),
						Toast.LENGTH_SHORT).show();
			} else {
				new UpdateGroupAsync(mContext).execute();
			}
		}
	}

	private class UpdateGroupAsync extends SpikaAsync<Void, Void, Boolean> {

		private HookUpProgressDialog progressDialog;

		protected UpdateGroupAsync(Context context) {
			super(context);
		}

		Group currentGroupData = null;

		@Override
		protected void onPreExecute() {
			// save data of current group so if anything goes wrong with
			// update, we can return to previous state
			currentGroupData = mGroup;

			if (progressDialog == null) {
				progressDialog = new HookUpProgressDialog(
						GroupProfileActivity.this);
			}
			progressDialog.show();

			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			if (gGroupImage != null) {

				String tmppath = GroupProfileActivity.this
						.getExternalCacheDir()
						+ "/"
						+ Const.TMP_BITMAP_FILENAME;
				Bitmap originalBitmap = BitmapFactory
						.decodeFile(gGroupImagePath);

				Bitmap avatarBitmap = Utils.scaleBitmap(originalBitmap,
						Const.PICTURE_SIZE, Const.PICTURE_SIZE);
				Utils.saveBitmapToFile(avatarBitmap, tmppath);
				String avatarFileId = CouchDB.uploadFile(tmppath);

				Bitmap avatarThumb = Utils.scaleBitmap(originalBitmap,
						Const.AVATAR_THUMB_SIZE, Const.AVATAR_THUMB_SIZE);
				Utils.saveBitmapToFile(avatarThumb, tmppath);
				String avatarThumbFileId = CouchDB.uploadFile(tmppath);

				mGroup.setAvatarFileId(avatarFileId);
				mGroup.setAvatarThumbFileId(avatarThumbFileId);
			} else {
				mGroup.setAvatarFileId(mGroupAvatarId);
				mGroup.setAvatarThumbFileId(mGroupAvatarThumbId);
			}

			int selectedPosition = mSpinnerCategory.getSelectedItemPosition();
			GroupCategory selectedCategory = mGroupCategories
					.get(selectedPosition);

			/* set new data */
			mGroup.setName(mGroupName);
			mGroup.setPassword(mGroupPassword);
			mGroup.setDescription(mGroupDescription);
			mGroup.setCategoryId(selectedCategory.getId());
			mGroup.setCategoryName(selectedCategory.getTitle());

			return CouchDB.updateGroup(mGroup);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				/* update successful */
				Toast.makeText(GroupProfileActivity.this,
						getString(R.string.group_updated), Toast.LENGTH_SHORT)
						.show();
				new GetGroupAsync(GroupProfileActivity.this).execute(mGroup
						.getId());
				GroupProfileActivity.this.setResult(RESULT_OK);
			} else {
				/*
				 * something went wrong with update group, returning logged in
				 * user to state before update
				 */
				Toast.makeText(GroupProfileActivity.this,
						getString(R.string.failed_to_update_group),
						Toast.LENGTH_SHORT).show();
				mGroupName = currentGroupData.getName();
				mGroupPassword = currentGroupData.getPassword();
				mGroupDescription = currentGroupData.getDescription();
				mGroupAvatarId = currentGroupData.getAvatarFileId();
				mGroup = currentGroupData;
			}
			super.onPostExecute(result);
			setProfileMode(ProfileMode.CANCEL);
			progressDialog.dismiss();
		}

	}

	/**
	 * Add group to user favorites
	 * 
	 * @author Matej Vida
	 * 
	 */
	private class AddToFavoritesAsync extends
			SpikaAsync<String, Void, Boolean> {

		private HookUpProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (progressDialog == null) {
				progressDialog = new HookUpProgressDialog(
						GroupProfileActivity.this);
			}
			progressDialog.show();
		}

		protected AddToFavoritesAsync(Context context) {
			super(context);
		}

		@Override
		protected Boolean doInBackground(String... params) {

			return CouchDB.addFavoriteGroup(params[0]);

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == true) {
				setButtonFavorites(REMOVE);
				mAddRemoveControl = true;
				GroupProfileActivity.this.setResult(RESULT_OK);
			}
			progressDialog.dismiss();
		}

	}

	/**
	 * Remove group from user favorites
	 * 
	 * @author Matej Vida
	 * 
	 */
	private class RemoveFromFavoritesAsync extends
			SpikaAsync<String, Void, Boolean> {

		private HookUpProgressDialog progressDialog;
		private boolean mShowProgress;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (mShowProgress) {
				if (progressDialog == null) {
					progressDialog = new HookUpProgressDialog(
							GroupProfileActivity.this);
				}
				progressDialog.show();
			}
		}

		protected RemoveFromFavoritesAsync(Context context, boolean showProgress) {
			super(context);
			mShowProgress = showProgress;
		}

		@Override
		protected Boolean doInBackground(String... params) {

			return CouchDB.removeFavoriteGroup(params[0]);

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == true) {
				setButtonFavorites(ADD);
				mAddRemoveControl = false;
				GroupProfileActivity.this.setResult(RESULT_OK);

			}
			if (mShowProgress) {
				progressDialog.dismiss();
			}

			if (GroupProfileActivity.this.mIsDeletedDone) {
				GroupProfileActivity.this.finish();
			}

		}

	}

	private class DeleteGroupAsync extends SpikaAsync<String, Void, Boolean> {

		private HookUpProgressDialog progressDialog;

		Group currentGroupData = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (progressDialog == null) {
				progressDialog = new HookUpProgressDialog(
						GroupProfileActivity.this);
			}
			progressDialog.show();
			// save data of current group so if anything goes wrong with
			// update, we can return to previous state
			currentGroupData = mGroup;
		}

		protected DeleteGroupAsync(Context context) {
			super(context);
		}

		@Override
		protected Boolean doInBackground(String... params) {

			mGroup.setDeleted(true);

			return CouchDB.updateGroup(mGroup);

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {

				GroupProfileActivity.this.mIsDeletedDone = true;

				new RemoveFromFavoritesAsync(GroupProfileActivity.this, false)
						.execute(mGroup.getId());
			} else {
				/*
				 * something went wrong with delete group, returning logged in
				 * user to state before update
				 */
				Toast.makeText(GroupProfileActivity.this,
						getString(R.string.failed_to_delete_group),
						Toast.LENGTH_SHORT).show();
				UsersManagement.setToGroup(currentGroupData);
			}
			progressDialog.dismiss();

		}
	}

	public void setNewPassword(String newPassword) {
		mGroupPassword = newPassword;
		hideKeyboard();
		new UpdateGroupAsync(this).execute();
	}

	private class GetGroupCategoriesAsync extends
			SpikaAsync<GroupSearch, Void, List<GroupCategory>> {

		protected GetGroupCategoriesAsync(Context context) {
			super(context);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected List<GroupCategory> doInBackground(GroupSearch... params) {

			return CouchDB.findGroupCategories();
		}

		@Override
		protected void onPostExecute(List<GroupCategory> result) {
			mGroupCategories = (ArrayList<GroupCategory>) result;
			final ArrayAdapter<GroupCategory> categoryAdapter = new ArrayAdapter<GroupCategory>(
					GroupProfileActivity.this,
					android.R.layout.simple_spinner_item, mGroupCategories) {

				@Override
				public GroupCategory getItem(int position) {
					return super.getItem(position);
				}

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTextSize(16);
					((TextView) v).setTypeface(SpikaApp.getTfMyriadPro());
					((TextView) v).setTextColor(getResources().getColor(
							R.color.hookup_positive));
					GroupCategory category = mGroupCategories.get(position);
					((TextView) v).setText(category.getTitle());
					return v;
				}

				@Override
				public View getDropDownView(int position, View convertView,
						ViewGroup parent) {
					View v = convertView;
					if (v == null) {
						// inflate your custom layout for the textview
						LayoutInflater inflater = GroupProfileActivity.this
								.getLayoutInflater();
						v = inflater.inflate(R.layout.group_category_item,
								parent, false);
					}
					// put the data in it
					GroupCategory category = mGroupCategories.get(position);
					if (category != null) {
						ImageView ivCategoryImage = (ImageView) v
								.findViewById(R.id.ivGroupCategoryImage);
						ProgressBar pbLoading = (ProgressBar) v
								.findViewById(R.id.pbLoadingForImage);
						TextView tvCategoryTitle = (TextView) v
								.findViewById(R.id.tvGroupCategory);

						Utils.displayImage(category.getImageUrl(),
								ivCategoryImage, pbLoading, ImageLoader.SMALL,
								R.drawable.image_stub, true);
						tvCategoryTitle.setText(category.getTitle());
					}

					return v;
				}
			};

			mSpinnerCategory.setAdapter(categoryAdapter);
			mSpinnerCategory
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}
					});

			String categoryId = mGroup.getCategoryId();
			for (GroupCategory category : mGroupCategories) {
				if (category.getId().equals(categoryId)) {
					int position = mGroupCategories.indexOf(category);
					mSpinnerCategory.setSelection(position);
				}
			}
			mSpinnerCategory.setClickable(false);

		}
	}

	private void openGroupWall(final Group group) {

		boolean userOwnsGroup = group.getUserId().equals(
				UsersManagement.getLoginUser().getId());

		if (group.getPassword() != null && !group.getPassword().equals("")
				&& !userOwnsGroup) {
			new HookUpGroupPasswordDialog(GroupProfileActivity.this).show(group
					.getPassword());
		} else {
			redirect();
		}
	}

	public void redirect() {

		UsersManagement.setToGroup(mGroup);
		UsersManagement.setToUser(null);

		SettingsManager.ResetSettings();
		if (WallActivity.gCurrentMessages != null) {
			WallActivity.gCurrentMessages.clear();
		}
		WallActivity.gIsRefreshUserProfile = true;
		GroupProfileActivity.this.startActivity(new Intent(
				GroupProfileActivity.this, WallActivity.class));

	}

}
