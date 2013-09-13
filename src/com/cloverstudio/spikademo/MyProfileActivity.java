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

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.dialog.DatePickerDialogWithRange;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.extendables.SideBarActivity;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Preferences;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * MyProfileActivity
 * 
 * Shows login user profile with editing options.
 */

public class MyProfileActivity extends SideBarActivity {

	private ScrollView mSvProfile;
	private ImageView mIvProfileImage;
	private EditText mEtUserName;
	private EditText mEtUserEmail;
	private EditText mEtUserPassword;
	private EditText mEtUserAbout;
	private EditText mEtUserBirthday;
	private Button mBtnSave;
	private TextView mTvUserName;
	private String mUserName;
	private String mUserAbout;
	private String mUserAvatarId;
	private String mUserAvatarThumbId;
	private String mUserEmail;
	private long mUserBirthday;
	private long mNewBirthday;
	private String mUserGender;
	private String mNewGender;
	private String mUserOnlineStatus;
	private String mNewOnlineStatus;
	private String mNewAvatarId;
	private ProgressBar mPbLoading;
	private Button mBtnEdit;

	private static final int GET_IMAGE_DIALOG = 1001;
	private static final int GET_BIRTHDAY_DIALOG = 1002;
	private static final int UPDATE_IMAGE_REQUEST_CODE = 1003;
	private Dialog mGetImageDialog;
	private DatePickerDialogWithRange mGetBirthdayDialog;

	public static Bitmap gProfileImage = null;
	public static String gProfileImagePath = null;

	private ProfileMode mProfileMode;
	private Spinner mSpinnerGender;
	private Spinner mSpinnerStatus;
	private static final long NO_BIRTHDAY = 0;
	private RelativeLayout mRlEditControls;
	private RelativeLayout mRlBirthday;
	private RelativeLayout mRlAbout;
	private RelativeLayout mRlGender;
	private RelativeLayout mRlOnlineStatus;
	private RelativeLayout mRlEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);
		setSideBar(getString(R.string.MY_PROFILE));
		Initialization();
		OnClickListeners();
	}

	private void Initialization() {

		if (SpikaApp.hasNetworkConnection()) {
			getLoginUserData();
		}

		mSvProfile = (ScrollView) findViewById(R.id.svProfile);
		mRlEditControls = (RelativeLayout) findViewById(R.id.rlEditControls);
		mRlBirthday = (RelativeLayout) findViewById(R.id.rlBirthday);
		mRlAbout = (RelativeLayout) findViewById(R.id.rlAbout);
		mRlGender = (RelativeLayout) findViewById(R.id.rlGender);
		mRlEmail = (RelativeLayout) findViewById(R.id.rlEmail);
		mRlOnlineStatus = (RelativeLayout) findViewById(R.id.rlOnlineStatus);
		mIvProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
		mPbLoading = (ProgressBar) findViewById(R.id.pbLoadingForImage);
		mBtnEdit = (Button) findViewById(R.id.btnEdit);
		mBtnEdit.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnSave = (Button) findViewById(R.id.btnSave);
		mBtnSave.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mTvUserName = (TextView) findViewById(R.id.tvUserName);
		mEtUserName = (EditText) findViewById(R.id.etUserName);
		mEtUserName.setTypeface(SpikaApp.getTfMyriadPro());
		mEtUserEmail = (EditText) findViewById(R.id.etUserEmail);
		mEtUserEmail.setTypeface(SpikaApp.getTfMyriadPro());
		mEtUserEmail.setInputType(InputType.TYPE_NULL);

		mSpinnerGender = (Spinner) findViewById(R.id.spinnerGender);
		final ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.gender)) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTextSize(16);
				((TextView) v).setTypeface(SpikaApp.getTfMyriadPro());
				if (position == 2) {
					((TextView) v).setTextColor(getResources().getColor(
							R.color.light_gray_subtext));
					((TextView) v).setText(getResources().getString(
							R.string.tap_to_add_gender));
				} else {
					((TextView) v).setTextColor(getResources().getColor(
							R.color.hookup_positive));
				}
				return v;
			}

			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				View v = super.getDropDownView(position, convertView, parent);
				((TextView) v).setTextSize(20);
				((TextView) v).setTypeface(SpikaApp.getTfMyriadPro());
				((TextView) v).setPadding(15, 15, 15, 15);
				if (position == 2) {
					((TextView) v).setTextColor(getResources().getColor(
							R.color.hookup_neutral));
				} else {
					((TextView) v).setTextColor(getResources().getColor(
							R.color.hookup_positive));
				}
				return v;
			}
		};
		mSpinnerGender.setAdapter(genderAdapter);
		mSpinnerGender.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg2) {
				case 0:
					mNewGender = Const.MALE;
					break;
				case 1:
					mNewGender = Const.FEMALE;
					break;
				case 2:
					mNewGender = null;
					break;
				default:
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		mSpinnerStatus = (Spinner) findViewById(R.id.spinnerStatus);
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

			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				View v = super.getDropDownView(position, convertView, parent);
				((TextView) v).setTextSize(20);
				((TextView) v).setTypeface(SpikaApp.getTfMyriadPro());
				((TextView) v).setPadding(15, 15, 15, 15);
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
		mSpinnerStatus.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg2) {
				case 0:
					mNewOnlineStatus = Const.ONLINE;
					break;
				case 1:
					mNewOnlineStatus = Const.AWAY;
					break;
				case 2:
					mNewOnlineStatus = Const.BUSY;
					break;
				case 3:
					mNewOnlineStatus = Const.OFFLINE;
					break;
				default:
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		mEtUserPassword = (EditText) findViewById(R.id.etUserPassword);
		mEtUserPassword.setTypeface(SpikaApp.getTfMyriadPro());

		mEtUserAbout = (EditText) findViewById(R.id.etUserAbout);
		mEtUserAbout.setTypeface(SpikaApp.getTfMyriadPro());
		mEtUserBirthday = (EditText) findViewById(R.id.etUserBirthday);
		mEtUserBirthday.setTypeface(SpikaApp.getTfMyriadPro());

		setProfileMode(ProfileMode.CANCEL);

	}

	private void getLoginUserData() {
		new GetLoginUserAsync(this).execute();

		mUserName = UsersManagement.getLoginUser().getName();
		mUserAbout = UsersManagement.getLoginUser().getAbout();
		mUserBirthday = UsersManagement.getLoginUser().getBirthday();
		mNewBirthday = mUserBirthday;
		mUserGender = UsersManagement.getLoginUser().getGender();
		mNewGender = mUserGender;
		mUserAvatarId = UsersManagement.getLoginUser().getAvatarFileId();
		mUserAvatarThumbId = UsersManagement.getLoginUser()
				.getAvatarThumbFileId();
		mNewAvatarId = mUserAvatarId;
		mUserOnlineStatus = UsersManagement.getLoginUser().getOnlineStatus();
		mUserEmail = UsersManagement.getLoginUser().getEmail();
		mNewOnlineStatus = mUserOnlineStatus;
	}

	private class GetLoginUserAsync extends SpikaAsync<Void, Void, User> {

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

			mUserName = UsersManagement.getLoginUser().getName();
			mUserAbout = UsersManagement.getLoginUser().getAbout();
			mUserBirthday = UsersManagement.getLoginUser().getBirthday();
			mNewBirthday = mUserBirthday;
			mUserGender = UsersManagement.getLoginUser().getGender();
			mNewGender = mUserGender;
			mUserAvatarId = UsersManagement.getLoginUser().getAvatarFileId();
			mUserAvatarThumbId = UsersManagement.getLoginUser()
					.getAvatarThumbFileId();
			mNewAvatarId = mUserAvatarId;
			mUserOnlineStatus = UsersManagement.getLoginUser()
					.getOnlineStatus();
			mUserEmail = UsersManagement.getLoginUser().getEmail();
			mNewOnlineStatus = mUserOnlineStatus;
		}
	}

	private void resetProfile() {
		mEtUserName.setText(mUserName);

		if (mUserGender != null && !"".equals(mUserGender)) {
			if (mUserGender.equals(Const.FEMALE)) {
				mSpinnerGender.setSelection(1);
			}
			if (mUserGender.equals(Const.MALE)) {
				mSpinnerGender.setSelection(0);
			}
		} else {
			mSpinnerGender.setSelection(2);
			mRlGender.setVisibility(View.GONE);
		}

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

		if (mUserAbout != null && !"".equals(mUserAbout)) {
			mEtUserAbout.setText(mUserAbout);
		} else {
			mEtUserAbout.setText(null);
			mRlAbout.setVisibility(View.GONE);
		}

		if (mUserBirthday == NO_BIRTHDAY) {
			mEtUserBirthday.setText(null);
			mRlBirthday.setVisibility(View.GONE);
		} else {
			String birthdayString = DateFormat.format(
					getString(R.string.hookup_date_format),
					mUserBirthday * 1000).toString();
			mEtUserBirthday.setText(birthdayString);
		}

		mEtUserEmail.setText(mUserEmail);

		mUserAvatarId = UsersManagement.getLoginUser().getAvatarFileId();
		mUserAvatarThumbId = UsersManagement.getLoginUser()
				.getAvatarThumbFileId();
		Utils.displayImage(mUserAvatarId, mIvProfileImage, mPbLoading,
				ImageLoader.LARGE, R.drawable.user_stub_large, false);
	}

	private void setProfileMode(ProfileMode newMode) {
		mProfileMode = newMode;
		switch (newMode) {
		case EDIT:
			enableViews();
			mTvUserName.setText(getString(R.string.USERNAME));
			mEtUserName.setVisibility(View.VISIBLE);
			mEtUserName.setText(mUserName);
			mRlEmail.setVisibility(View.GONE);
			mRlEditControls.setVisibility(View.VISIBLE);
			// mRlPassword.setVisibility(View.VISIBLE);
			mRlAbout.setVisibility(View.VISIBLE);
			mRlBirthday.setVisibility(View.VISIBLE);
			mRlGender.setVisibility(View.VISIBLE);
			mRlOnlineStatus.setVisibility(View.VISIBLE);
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
			disableViews();
			mBtnEdit.setEnabled(true);
			mBtnEdit.setVisibility(View.VISIBLE);
			mRlEmail.setVisibility(View.VISIBLE);
			resetProfile();
			mBtnEdit.setBackgroundResource(R.drawable.positive_selector);
			mBtnEdit.setText(getString(R.string.EDIT));
			mBtnEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setProfileMode(ProfileMode.EDIT);

				}
			});
			break;
		default:
			break;
		}
	}

	private enum ProfileMode {
		EDIT, CANCEL
	}

	@Override
	protected void enableViews() {
		super.enableViews();
		mIvProfileImage.setEnabled(true);
		mEtUserName.setEnabled(true);
		mEtUserPassword.setEnabled(true);
		mSvProfile.setEnabled(true);
		mEtUserAbout.setEnabled(true);
		mEtUserBirthday.setEnabled(true);
		mSpinnerGender.setEnabled(true);
		mSpinnerStatus.setEnabled(true);
		mBtnEdit.setEnabled(true);
		mBtnEdit.setVisibility(View.VISIBLE);

	}

	@Override
	protected void disableViews() {
		super.disableViews();
		mIvProfileImage.setEnabled(false);
		mEtUserName.setEnabled(false);
		mEtUserPassword.setEnabled(false);
		mSvProfile.setEnabled(false);
		mEtUserAbout.setEnabled(false);
		mEtUserBirthday.setEnabled(false);
		mRlEditControls.setVisibility(View.GONE);
		mSpinnerGender.setEnabled(false);
		mSpinnerStatus.setEnabled(false);
		mBtnEdit.setEnabled(false);
		mBtnEdit.setVisibility(View.GONE);
	}

	@Override
	protected void setObjectsNull() {
		if (gProfileImage != null) {
			gProfileImage.recycle();
			gProfileImage = null;

		}
		mIvProfileImage.setOnClickListener(null);
		mRlEditControls = null;
		mRlBirthday = null;
		mRlAbout = null;
		mRlGender = null;
		unbindDrawables(findViewById(R.id.ivProfileImage));
		super.setObjectsNull();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == UPDATE_IMAGE_REQUEST_CODE) {
			if (gProfileImage != null) {
				mIvProfileImage.setImageBitmap(gProfileImage);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void OnClickListeners() {

		mIvProfileImage.setOnClickListener(getImageClickListener());

		mBtnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mUserName = mEtUserName.getText().toString();
				mUserAbout = mEtUserAbout.getText().toString();
				mUserBirthday = mNewBirthday;
				mUserGender = mNewGender;
				mUserOnlineStatus = mNewOnlineStatus;
				mUserAvatarId = mNewAvatarId;
				hideKeyboard();

				new CheckUniqueAsync(MyProfileActivity.this).execute(mUserName);

			}
		});

		mEtUserBirthday.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(GET_BIRTHDAY_DIALOG);
			}
		});

	}

	private OnClickListener getImageClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(GET_IMAGE_DIALOG);
			}
		};
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case GET_IMAGE_DIALOG:
			mGetImageDialog = new Dialog(MyProfileActivity.this,
					R.style.TransparentDialogTheme);
			mGetImageDialog.getWindow().setGravity(Gravity.BOTTOM);
			mGetImageDialog.setContentView(R.layout.dialog_get_image);
			WindowManager.LayoutParams params = new WindowManager.LayoutParams();
			Window window = mGetImageDialog.getWindow();
			params.copyFrom(window.getAttributes());
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			window.setAttributes(params);

			final Button btnGallery = (Button) mGetImageDialog
					.findViewById(R.id.btnGallery);
			btnGallery.setTypeface(SpikaApp.getTfMyriadProBold(),
					Typeface.BOLD);
			btnGallery.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					Intent galleryIntent = new Intent(MyProfileActivity.this,
							CameraCropActivity.class);
					galleryIntent.putExtra("type", "gallery");
					galleryIntent.putExtra("profile", true);
					MyProfileActivity.this.startActivityForResult(
							galleryIntent, UPDATE_IMAGE_REQUEST_CODE);
					mGetImageDialog.dismiss();

				}
			});

			final Button btnCamera = (Button) mGetImageDialog
					.findViewById(R.id.btnCamera);
			btnCamera
					.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
			btnCamera.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					Intent cameraIntent = new Intent(MyProfileActivity.this,
							CameraCropActivity.class);
					cameraIntent.putExtra("type", "camera");
					cameraIntent.putExtra("profile", true);
					MyProfileActivity.this.startActivityForResult(cameraIntent,
							UPDATE_IMAGE_REQUEST_CODE);
					mGetImageDialog.dismiss();

				}
			});

			final Button btnRemovePhoto = (Button) mGetImageDialog
					.findViewById(R.id.btnRemovePhoto);
			btnRemovePhoto.setTypeface(SpikaApp.getTfMyriadProBold(),
					Typeface.BOLD);
			btnRemovePhoto.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					mNewAvatarId = "";
					gProfileImage = null;
					Utils.displayImage(mNewAvatarId, mIvProfileImage,
							mPbLoading, ImageLoader.LARGE,
							R.drawable.user_stub_large, false);
					mGetImageDialog.dismiss();

				}
			});

			return mGetImageDialog;
		case GET_BIRTHDAY_DIALOG:

			int intMaxYear = Calendar.getInstance().get(Calendar.YEAR);
			int intMaxMonth = Calendar.getInstance().get(Calendar.MONTH);
			int intMaxDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

			mGetBirthdayDialog = new DatePickerDialogWithRange(this,
					new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							Time chosenDate = new Time();
							chosenDate.set(dayOfMonth, monthOfYear, year);
							mNewBirthday = chosenDate.toMillis(true) / 1000;
							CharSequence stringDate = DateFormat.format(
									getString(R.string.hookup_date_format),
									chosenDate.toMillis(true));
							mEtUserBirthday.setText(stringDate.toString());
						}
					}, intMaxYear, intMaxMonth, intMaxDay);
			mGetBirthdayDialog
					.setMessage(getString(R.string.when_is_your_birthday));
			return mGetBirthdayDialog;
		default:
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);

		switch (id) {
		case GET_BIRTHDAY_DIALOG:
			DatePickerDialog dateDialog = (DatePickerDialog) dialog;
			int monthOfYear = Calendar.getInstance().get(Calendar.MONTH);
			int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			int year = Calendar.getInstance().get(Calendar.YEAR);
			dateDialog.updateDate(year, monthOfYear, dayOfMonth);
			break;
		}
	}

	private class CheckUniqueAsync extends SpikaAsync<String, Void, Boolean> {

		private String mUsername;
		private User mUserByName;

		protected CheckUniqueAsync(Context context) {
			super(context);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			mUsername = params[0];
			mUserByName = CouchDB.getUserByName(mUsername);

			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (mUserByName != null
					&& !mUserByName.getId().equals(
							UsersManagement.getLoginUser().getId())) {
				Toast.makeText(MyProfileActivity.this,
						getString(R.string.username_taken), Toast.LENGTH_SHORT)
						.show();
			} else {
				new UpdateUserAsync(MyProfileActivity.this).execute();
			}
		}
	}

	private class UpdateUserAsync extends SpikaAsync<Void, Void, Boolean> {

		protected UpdateUserAsync(Context context) {
			super(context);
		}

		User currentUserData = null;
		private HookUpProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			// save data of current login user so if anything goes wrong with
			// update, we can return to previous state
			currentUserData = UsersManagement.getLoginUser();
			if (progressDialog == null) {
				progressDialog = new HookUpProgressDialog(
						MyProfileActivity.this);
			}
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			if (gProfileImage != null) {

				String tmppath = MyProfileActivity.this.getExternalCacheDir()
						+ "/" + Const.TMP_BITMAP_FILENAME;
				Bitmap originalBitmap = BitmapFactory
						.decodeFile(gProfileImagePath);

				Bitmap avatarBitmap = Utils.scaleBitmap(originalBitmap,
						Const.PICTURE_SIZE, Const.PICTURE_SIZE);
				Utils.saveBitmapToFile(avatarBitmap, tmppath);
				String avatarFileId = CouchDB.uploadFile(tmppath);

				Bitmap avatarThumb = Utils.scaleBitmap(originalBitmap,
						Const.AVATAR_THUMB_SIZE, Const.AVATAR_THUMB_SIZE);
				Utils.saveBitmapToFile(avatarThumb, tmppath);
				String avatarThumbFileId = CouchDB.uploadFile(tmppath);

				UsersManagement.getLoginUser().setAvatarFileId(avatarFileId);
				UsersManagement.getLoginUser().setAvatarThumbFileId(
						avatarThumbFileId);

			} else {
				UsersManagement.getLoginUser().setAvatarFileId(mUserAvatarId);
				UsersManagement.getLoginUser().setAvatarThumbFileId(
						mUserAvatarThumbId);
			}

			/* set new email, username and password */
			UsersManagement.getLoginUser().setName(mUserName);
			UsersManagement.getLoginUser().setAbout(mUserAbout);
			UsersManagement.getLoginUser().setBirthday(mUserBirthday);
			UsersManagement.getLoginUser().setGender(mUserGender);
			UsersManagement.getLoginUser().setOnlineStatus(mUserOnlineStatus);

			return CouchDB.updateUser(UsersManagement.getLoginUser());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				/* update successful */

				WallActivity.gIsRefreshUserProfile = true;
				// MyProfileActivity.this.finish();

				new GetLoginUserAsync(MyProfileActivity.this).execute();

			} else {
				/*
				 * something went wrong with update profile, returning logged in
				 * user to state before update
				 */

				Toast.makeText(MyProfileActivity.this, "Error",
						Toast.LENGTH_SHORT).show();

				UsersManagement.setLoginUser(currentUserData);

				mUserName = UsersManagement.getLoginUser().getName();
				mUserAbout = UsersManagement.getLoginUser().getAbout();
				mUserBirthday = UsersManagement.getLoginUser().getBirthday();
				mUserGender = UsersManagement.getLoginUser().getGender();
				mUserAvatarId = UsersManagement.getLoginUser()
						.getAvatarFileId();
			}
			setProfileMode(ProfileMode.CANCEL);
			progressDialog.dismiss();
			super.onPostExecute(result);
		}
	}

}
