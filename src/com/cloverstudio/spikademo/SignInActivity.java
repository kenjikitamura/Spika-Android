/*
 * The MIT License (MIT)
 * 
 * Copyright ½ 2013 Clover Studio Ltd. All rights reserved.
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

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.ActivitySummary;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.dialog.HookUpAlertDialog;
import com.cloverstudio.spikademo.dialog.HookUpAlertDialog.ButtonType;
import com.cloverstudio.spikademo.dialog.HookUpDialog;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.dialog.Tutorial;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.management.FileManagement;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * SignInActivity
 * 
 * Allows user to sign in, sign up or receive an email with password if user
 * is already registered with a valid email.
 */

public class SignInActivity extends Activity {

	private EditText mEtSignInEmail;
	private EditText mEtSignInPassword;
	private EditText mEtSignUpName;
	private EditText mEtSignUpEmail;
	private EditText mEtSignUpPassword;
	private EditText mEtSendPasswordEmail;
	private Button mBtnActive;
	private Button mBtnInactive;
	private Button mBtnForgotPassword;
	private Button mBtnBack;
	private Button mBtnSendPassword;
	private LinearLayout mLlSignIn;
	private LinearLayout mLlSignUp;
	private TextView mTvTitle;

	private String mSignInEmail;
	private String mSignInPassword;
	private String mSignUpName;
	private String mSignUpEmail;
	private String mSignUpPassword;
	private String mSendPasswordEmail;

	private boolean mUserCreated = false;
	private boolean mUserSignedIn = false;
	private LinearLayout mLlForgotPassword;
	private static SignInActivity sInstance = null;
	private Screen mActiveScreen;
	private HookUpDialog mSendPasswordDialog;

	private HookUpProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		initialization();
		sInstance = this;
		SpikaApp.gOpenFromBackground = true;

		showTutorial(getString(R.string.tutorial_login));
	}

	private void showTutorial(String textTutorial) {
		if (SpikaApp.getPreferences().getShowTutorial(
				Utils.getClassNameInStr(this))) {
			Tutorial.show(this, textTutorial);
			SpikaApp.getPreferences().setShowTutorial(false,
					Utils.getClassNameInStr(this));
		}
	}

	private void initialization() {

		// initialize singletons CouchDB & UsersManagement
		new CouchDB();
		new UsersManagement();
		new FileManagement(getApplicationContext());

		mSendPasswordDialog = new HookUpDialog(this);
		mSendPasswordDialog.setOnButtonClickListener(HookUpDialog.BUTTON_OK,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new SendPasswordAsync(SignInActivity.this)
								.execute(mEtSendPasswordEmail.getText()
										.toString());
						mSendPasswordDialog.dismiss();
					}
				});
		mSendPasswordDialog.setOnButtonClickListener(
				HookUpDialog.BUTTON_CANCEL, new OnClickListener() {

					@Override
					public void onClick(View v) {
						mSendPasswordDialog.dismiss();

					}
				});

		mEtSignInEmail = (EditText) findViewById(R.id.etSignInEmail);
		mEtSignInPassword = (EditText) findViewById(R.id.etSignInPassword);
		mEtSignUpName = (EditText) findViewById(R.id.etSignUpName);
		mEtSignUpEmail = (EditText) findViewById(R.id.etSignUpEmail);
		mEtSignUpPassword = (EditText) findViewById(R.id.etSignUpPassword);
		mEtSendPasswordEmail = (EditText) findViewById(R.id.etForgotPasswordEmail);
		mBtnBack = (Button) findViewById(R.id.btnBack);
		mBtnBack.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mActiveScreen == Screen.FORGOT_PASSWORD) {
					setActiveScreen(Screen.SIGN_IN);
				}

			}
		});
		mBtnActive = (Button) findViewById(R.id.btnActive);
		mBtnActive.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnActive
				.setBackgroundResource(R.drawable.rounded_rect_positive_selector);
		mBtnInactive = (Button) findViewById(R.id.btnInactive);
		mBtnInactive.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnInactive
				.setBackgroundResource(R.drawable.rounded_rect_neutral_selector);
		mBtnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
		mBtnForgotPassword
				.setTextColor(new ColorStateList(new int[][] {
						new int[] { android.R.attr.state_pressed },
						new int[] {} }, new int[] { Color.rgb(190, 190, 190),
						Color.rgb(125, 125, 125), }));
		mBtnForgotPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setActiveScreen(Screen.FORGOT_PASSWORD);

			}
		});
		mBtnSendPassword = (Button) findViewById(R.id.btnSendPassword);
		mBtnSendPassword.setTypeface(SpikaApp.getTfMyriadProBold(),
				Typeface.BOLD);
		mBtnSendPassword.setVisibility(View.GONE);
		mBtnSendPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isEmailValid(mEtSendPasswordEmail.getText().toString())) {
					new CheckEmailAsync(SignInActivity.this)
							.execute(mEtSendPasswordEmail.getText().toString());
				} else {

					final HookUpDialog dialog = new HookUpDialog(
							SignInActivity.this);
					dialog.showOnlyOK(getString(R.string.email_not_valid));

				}

			}
		});
		mLlSignIn = (LinearLayout) findViewById(R.id.llSignInBody);
		mLlSignIn.setVisibility(View.VISIBLE);
		mLlSignUp = (LinearLayout) findViewById(R.id.llSignUpBody);
		mLlSignUp.setVisibility(View.GONE);
		mLlForgotPassword = (LinearLayout) findViewById(R.id.llForgotPasswordBody);
		mLlForgotPassword.setVisibility(View.GONE);
		mTvTitle = (TextView) findViewById(R.id.tvSignInTitle);
		mTvTitle.setText(getString(R.string.SIGN_IN));
		mTvTitle.setTypeface(SpikaApp.getTfMyriadPro());

		mEtSignInEmail.setTypeface(SpikaApp.getTfMyriadPro());
		mEtSignInPassword.setTypeface(SpikaApp.getTfMyriadPro());
		mEtSignUpName.setTypeface(SpikaApp.getTfMyriadPro());
		mEtSignUpEmail.setTypeface(SpikaApp.getTfMyriadPro());
		mEtSignUpPassword.setTypeface(SpikaApp.getTfMyriadPro());
		mEtSendPasswordEmail.setTypeface(SpikaApp.getTfMyriadPro());

		getEmailAndPasswordFromIntent();
		checkToken();

		setActiveScreen(Screen.SIGN_IN);
	}

	private void getEmailAndPasswordFromIntent() {
		String passwordFromPrefs = getIntent().getStringExtra(
				"password_from_prefs");
		String emailFromPrefs = getIntent().getStringExtra("email_from_prefs");
		if (passwordFromPrefs != null && emailFromPrefs != null) {
			mEtSignInEmail.setText(emailFromPrefs);
			mEtSignInPassword.setText(passwordFromPrefs);
		}
	}

	private void checkToken() {
		if (isInvalidToken()) {
			final HookUpAlertDialog invalidTokenDialog = new HookUpAlertDialog(
					this);
			invalidTokenDialog.show(getString(R.string.invalid_token_message),
					ButtonType.CLOSE);
		}
	}

	private boolean isInvalidToken() {
		return getIntent().getBooleanExtra("invalid_token", false);
	}

	private void setActiveScreen(Screen activeScreen) {
		mActiveScreen = activeScreen;
		switch (activeScreen) {
		case SIGN_IN:
			mTvTitle.setText(getString(R.string.SIGN_IN));
			mLlSignIn.setVisibility(View.VISIBLE);
			mLlSignUp.setVisibility(View.GONE);
			mBtnActive.setVisibility(View.VISIBLE);
			mBtnInactive.setVisibility(View.VISIBLE);
			mBtnForgotPassword.setVisibility(View.VISIBLE);
			mBtnActive.setText(getString(R.string.SIGN_IN));
			mBtnInactive.setText(getString(R.string.SIGN_UP));
			mBtnBack.setVisibility(View.GONE);
			mLlForgotPassword.setVisibility(View.GONE);
			mBtnSendPassword.setVisibility(View.GONE);
			mBtnActive.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					mSignInEmail = mEtSignInEmail.getText().toString();
					mSignInPassword = mEtSignInPassword.getText().toString();

					new AuthentificationAsync().execute("SignIn");

				}
			});
			mBtnInactive.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (mLlSignIn.getVisibility() == View.VISIBLE) {
						SignInActivity.this.setActiveScreen(Screen.SIGN_UP);
					}

				}
			});
			break;
		case SIGN_UP:
			mTvTitle.setText(getString(R.string.SIGN_UP));
			mLlSignUp.setVisibility(View.VISIBLE);
			mLlSignIn.setVisibility(View.GONE);
			mBtnActive.setVisibility(View.VISIBLE);
			mBtnInactive.setVisibility(View.VISIBLE);
			mBtnForgotPassword.setVisibility(View.VISIBLE);
			mBtnActive.setText(getString(R.string.SIGN_UP));
			mBtnInactive.setText(getString(R.string.SIGN_IN));
			mBtnBack.setVisibility(View.GONE);
			mLlForgotPassword.setVisibility(View.GONE);
			mBtnSendPassword.setVisibility(View.GONE);
			mBtnActive.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					mSignUpName = mEtSignUpName.getText().toString();
					mSignUpEmail = mEtSignUpEmail.getText().toString();
					mSignUpPassword = mEtSignUpPassword.getText().toString();

					if (isNameValid(mSignUpName) && isEmailValid(mSignUpEmail)
							&& isPasswordValid(mSignUpPassword)) {
						new AvailabilityAsync(mSignUpName, mSignUpEmail)
								.execute();
					}

				}
			});
			mBtnInactive.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (mLlSignUp.getVisibility() == View.VISIBLE) {
						SignInActivity.this.setActiveScreen(Screen.SIGN_IN);
					}

				}
			});
			break;
		case FORGOT_PASSWORD:
			mTvTitle.setText(getString(R.string.FORGOT_PASSWORD));
			mLlSignUp.setVisibility(View.GONE);
			mLlSignIn.setVisibility(View.GONE);
			mBtnActive.setVisibility(View.GONE);
			mBtnInactive.setVisibility(View.GONE);
			mBtnForgotPassword.setVisibility(View.GONE);
			mBtnBack.setVisibility(View.VISIBLE);
			mLlForgotPassword.setVisibility(View.VISIBLE);
			mBtnSendPassword.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private boolean isNameValid(String name) {
		String nameResult = Utils.checkName(this, name);
		if (!nameResult.equals(getString(R.string.name_ok))) {

			final HookUpDialog dialog = new HookUpDialog(SignInActivity.this);
			dialog.showOnlyOK(nameResult);

			return false;
		} else {
			return true;
		}
	}

	private boolean isPasswordValid(String password) {
		String passwordResult = Utils.checkPassword(this, password);
		if (!passwordResult.equals(getString(R.string.password_ok))) {

			final HookUpDialog dialog = new HookUpDialog(SignInActivity.this);
			dialog.showOnlyOK(passwordResult);

			return false;

		} else {
			return true;
		}
	}

	private boolean isEmailValid(String email) {
		String emailResult = Utils.checkEmail(this, email);

		if (!emailResult.equals(getString(R.string.email_ok))) {

			final HookUpDialog dialog = new HookUpDialog(SignInActivity.this);
			dialog.showOnlyOK(emailResult);

			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mActiveScreen == Screen.FORGOT_PASSWORD) {
				setActiveScreen(Screen.SIGN_IN);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		if (mActiveScreen == Screen.FORGOT_PASSWORD) {
			setActiveScreen(Screen.SIGN_IN);
		} else {
			super.onBackPressed();
		}
	}

	private class SendPasswordAsync extends SpikaAsync<String, Void, Void> {

		private HookUpProgressDialog mProgressDialog;

		protected SendPasswordAsync(Context context) {
			super(context);
			mProgressDialog = new HookUpProgressDialog(SignInActivity.this);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {

			CouchDB.sendPassword(params[0]);

			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			super.onPostExecute(param);

			mProgressDialog.dismiss();

			final HookUpAlertDialog emailSentDialog = new HookUpAlertDialog(
					SignInActivity.this);
			emailSentDialog.show(getString(R.string.email_sent), ButtonType.OK);

		}
	}

	private class CheckEmailAsync extends SpikaAsync<String, Void, Boolean> {

		private String mEmail;
		private User mUserByEmail;

		private HookUpProgressDialog mProgressDialog;

		protected CheckEmailAsync(Context context) {
			super(context);
			mProgressDialog = new HookUpProgressDialog(SignInActivity.this);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			mEmail = params[0];
			mUserByEmail = CouchDB.getUserByEmail(mEmail);

			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			mProgressDialog.dismiss();

			if (mUserByEmail != null) {

				mSendPasswordEmail = mEtSendPasswordEmail.getText().toString();
				mSendPasswordDialog.show(getString(R.string.send_password)
						+ "\n" + mSendPasswordEmail + "?");

			} else {

				final HookUpDialog dialog = new HookUpDialog(
						SignInActivity.this);
				dialog.showOnlyOK(getString(R.string.email_notexists));

			}
		}
	}

	private class AvailabilityAsync extends AsyncTask<Void, Void, Void> {

		private String mUsername;
		private String mEmail;

		private User mUserByName;
		private User mUserByEmail;

		private HookUpProgressDialog mProgressDialog;

		public AvailabilityAsync(String username, String email) {
			this.mUsername = username;
			this.mEmail = email;

			mProgressDialog = new HookUpProgressDialog(SignInActivity.this);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... voids) {

			mUserByName = CouchDB.getUserByName(mUsername);
			mUserByEmail = CouchDB.getUserByEmail(mEmail);

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			mProgressDialog.dismiss();

			if (mUserByName != null && mUserByEmail != null) {

				final HookUpDialog dialog = new HookUpDialog(
						SignInActivity.this);
				dialog.showOnlyOK(getString(R.string.username_and_email_taken));

			} else if (mUserByName != null) {

				final HookUpDialog dialog = new HookUpDialog(
						SignInActivity.this);
				dialog.showOnlyOK(getString(R.string.username_taken));

			} else if (mUserByEmail != null) {

				final HookUpDialog dialog = new HookUpDialog(
						SignInActivity.this);
				dialog.showOnlyOK(getString(R.string.email_taken));

			} else {
				new AuthentificationAsync().execute("SignUp");
			}
		}
	}

	private class AuthentificationAsync extends AsyncTask<String, Void, String> {

		private String action;
		private Exception exception;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			mProgressDialog = new HookUpProgressDialog(SignInActivity.this);
			mProgressDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {

			action = params[0];

			if (action.equals("SignIn")) {
				if (mSignInPassword.equals("") && mSignInEmail.equals("")) {
					return Const.LOGIN_ERROR;
				} else {
					mUserSignedIn = true;
					//return CouchDB.auth(mSignInEmail, mSignInPassword);
					try {
						return CouchDB.auth(mSignInEmail, mSignInPassword);
					} catch (JSONException e) {
						e.printStackTrace();
						exception = e;
						return Const.LOGIN_ERROR;
					} catch (IOException e) {
						e.printStackTrace();
						exception = e;
						return Const.LOGIN_ERROR;
					} catch (Exception e){
						e.printStackTrace();
						exception = e;
						return Const.LOGIN_ERROR;
					}
				}
			} else if (action.equals("SignUp")) {
				if (mSignUpName.equals("") && mSignUpEmail.equals("")
						&& mSignUpPassword.equals("")) {
					return Const.LOGIN_ERROR;
				} else {
					User user = CouchDB.findUserByEmail(mSignUpEmail, false);
					if (user == null) {
						mUserCreated = true;
						String newUser = CouchDB.createUser(mSignUpName, mSignUpEmail,
								mSignUpPassword);
						
						//return CouchDB.auth(mSignUpEmail, mSignUpPassword);
						try {
							return CouchDB.auth(mSignUpEmail, mSignUpPassword);
						} catch (JSONException e) {
							exception = e;
							return Const.LOGIN_ERROR;
						} catch (IOException e) {
							exception = e;
							return Const.LOGIN_ERROR;
						}
					} else {
						mUserCreated = false;
					}
				}
			}
			return Const.LOGIN_ERROR;
		}

		@Override
		protected void onPostExecute(String result) {

			mProgressDialog.dismiss();

			if (SpikaApp.hasNetworkConnection()) {
				if (result != null) {

					if (mUserSignedIn || mUserCreated) {
						if (result.equals(Const.LOGIN_SUCCESS)) {

							signIn();

						} else {

							final HookUpDialog dialog = new HookUpDialog(
									SignInActivity.this);
							//dialog.showOnlyOK(getString(R.string.no_valid_email_password));
							String errorMessage = null;
							if (exception == null){
								errorMessage = getString(R.string.no_valid_email_password);
							}else if (exception instanceof IOException){
							    errorMessage = getString(R.string.can_not_connect_to_server);
							}else if(exception instanceof JSONException){
							    errorMessage = getString(R.string.an_internal_error_has_occurred,exception.getClass().getName());
							}else{
							    errorMessage = getString(R.string.an_internal_error_has_occurred,exception.getClass().getName());
							}
							dialog.showOnlyOK(errorMessage);
						}
					} else {
						if (action.equals("SignIn")) {

							final HookUpDialog dialog = new HookUpDialog(
									SignInActivity.this);
							dialog.showOnlyOK(getString(R.string.no_user_registered));
						}
						if (action.equals("SignUp")) {

							final HookUpDialog dialog = new HookUpDialog(
									SignInActivity.this);
							dialog.showOnlyOK(getString(R.string.no_valid_email_password));

						}

					}
				} else {

					final HookUpDialog dialog = new HookUpDialog(
							SignInActivity.this);
					dialog.showOnlyOK(getString(R.string.no_email_password));

				}
			} else {

				final HookUpDialog dialog = new HookUpDialog(
						SignInActivity.this);
				dialog.showOnlyOK(getString(R.string.no_internet_connection));

			}
		}
	}

	private void signIn() {
		new SignInUserAsync(this).execute();
	}

	private class SignInUserAsync extends SpikaAsync<Void, Void, User> {

		protected SignInUserAsync(Context context) {
			super(context);
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			mProgressDialog = new HookUpProgressDialog(SignInActivity.this);
			mProgressDialog.show();

		}

		@Override
		protected User doInBackground(Void... params) {

			if (UsersManagement.getLoginUser() != null) {
				ActivitySummary loginUserActivitySummary = CouchDB
						.findUserActivitySummary(UsersManagement.getLoginUser()
								.getId());
				UsersManagement.getLoginUser().setActivitySummary(
						loginUserActivitySummary);

				return UsersManagement.getLoginUser();
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(User loginUser) {

			mProgressDialog.dismiss();

			if (loginUser == null) {

				final HookUpDialog dialog = new HookUpDialog(
						SignInActivity.this);
				dialog.showOnlyOK(getString(R.string.no_internet_connection));

				return;
			}

			UsersManagement.setLoginUser(loginUser);

			// setEmailAndPassToPreference(UsersManagement.getLoginUser()
			// .getEmail(), UsersManagement.getLoginUser().getPassword());
			Intent intent = new Intent(SignInActivity.this,
					RecentActivityActivity.class);
			intent.putExtra(Const.SIGN_IN, true);
			SignInActivity.this.startActivity(intent);

			if (SpikaApp.getPreferences().getPasscodeProtect() == true) {
				Intent passcode = new Intent(SignInActivity.this,
						PasscodeActivity.class);
				passcode.putExtra("protect", true);
				SignInActivity.this.startActivity(passcode);
			}
			SignInActivity.this.finish();

		}
	}

	public static SignInActivity getInstance() {
		return sInstance;
	}

	@Override
	protected void onDestroy() {
		sInstance = null;
		mLlSignIn = null;
		mLlSignUp = null;
		super.onDestroy();
	}

	private enum Screen {
		SIGN_IN, SIGN_UP, FORGOT_PASSWORD
	}

}
