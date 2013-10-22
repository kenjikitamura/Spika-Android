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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.extendables.SideBarActivity;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Preferences;
import com.crittercism.app.Crittercism;

/**
 * SplashScreenActivity
 * 
 * Displays splash screen for 2 seconds.
 */

public class SplashScreenActivity extends Activity {

	private String mSavedEmail;
	private String mSavedPassword;
	public static SplashScreenActivity sInstance = null;
	private User mUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SplashScreenActivity.sInstance = this;
		setContentView(R.layout.activity_splash_screen);

		/* Initiate Crittercism */
		Crittercism.init(getApplicationContext(), Const.CRITTERCISM_APP_ID);

		new CouchDB();
		// new UsersManagement();

		if (SpikaApp.hasNetworkConnection()) {

			if (checkIfUserSignIn()) {
				mSavedEmail = SpikaApp.getPreferences().getUserEmail();
				mSavedPassword = SpikaApp.getPreferences().getUserPassword();

				mUser = new User();
				try {
					mUser = new GetUserByEmailAsync(SplashScreenActivity.this)
							.execute(mSavedEmail,mSavedPassword).get();

					 boolean tokenOk = new AuthentificationAsync(
					 SplashScreenActivity.this).execute().get();
					if (tokenOk && mUser!=null) {
						if (authentificationOk(mUser)) {
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									signIn(mUser);
								}
							}, 2000);
						}
					} else {
						SideBarActivity.appLogout(false, false, true);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

			} else {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						startActivity(new Intent(SplashScreenActivity.this,
								SignInActivity.class));
						finish();
					}
				}, 2000);

			}

		} else {

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(SplashScreenActivity.this,
							RecentActivityActivity.class);
					intent.putExtra(Const.SIGN_IN, true);
					SplashScreenActivity.this.startActivity(intent);
					Toast.makeText(SplashScreenActivity.this,
							getString(R.string.no_internet_connection),
							Toast.LENGTH_LONG).show();
				}
			}, 2000);

		}
	}

	private boolean checkIfUserSignIn() {
		boolean isSessionSaved = false;
		Preferences prefs = SpikaApp.getPreferences();
		if (prefs.getUserEmail() == null && prefs.getUserPassword() == null) {
			isSessionSaved = false;
		} else if (prefs.getUserEmail().equals("")
				&& prefs.getUserPassword().equals("")) {
			isSessionSaved = false;
		} else {
			isSessionSaved = true;
		}
		return isSessionSaved;
	}

	private class GetUserByEmailAsync extends SpikaAsync<String, Void, User> {

		protected GetUserByEmailAsync(Context context) {
			super(context);
		}

		@Override
		protected User doInBackground(String... params) {

            String email = params[0];
            String passwrod = params[1];

			User user = null;
			try {
				user = CouchDB.getUserByEmailAndPassword(email,passwrod);
			} catch (JSONException e) {
				// ignore. doInBackground return null.
			} catch (IOException e) {
				// ignore. doInBackground return null.
			}
			return user;
		}

		@Override
		protected void onPostExecute(User user) {

		}
	}

	private class AuthentificationAsync extends
			SpikaAsync<Void, Void, Boolean> {

		protected AuthentificationAsync(Context context) {
			super(context);
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			try {
				return CouchDB.auth(mSavedEmail, mSavedPassword).equals(
						Const.LOGIN_SUCCESS);
			} catch (JSONException e) {
				// ignore. doInBackground return false.
			} catch (IOException e) {
				// ignore. doInBackground return false.
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {

		}
	}

	private void signIn(User u) {

		UsersManagement.setLoginUser(u);
		UsersManagement.setToUser(u);
		UsersManagement.setToGroup(null);
		
		boolean passcodeProtect = SpikaApp.getPreferences()
				.getPasscodeProtect();
		boolean openPushNotification = getIntent().getBooleanExtra(
				Const.PUSH_INTENT, false);
		
		Intent intent = new Intent(SplashScreenActivity.this,
				RecentActivityActivity.class);
		if (openPushNotification)  {
			intent = getIntent();
			intent.setClass(SplashScreenActivity.this,
					RecentActivityActivity.class);
		}
		
		//parse URI hookup://user/[ime korisnika] and hookup://group/[ime grupe]
		Uri userUri = getIntent().getData();
		//If opened from link
		if (userUri != null) {
        		String scheme = userUri.getScheme(); 		// "hookup"
        		String host = userUri.getHost(); 		// "user" or "group"
        		if (host.equals("user")) {
                		List<String> params = userUri.getPathSegments();
                		String userName = params.get(0); 	// "ime korisnika"
        		    	intent.putExtra(Const.USER_URI_INTENT, true);
        		    	intent.putExtra(Const.USER_URI_NAME, userName);
        		} else if (host.equals("group")) {
                		List<String> params = userUri.getPathSegments();
                		String groupName = params.get(0); 	// "ime grupe"
        		    	intent.putExtra(Const.GROUP_URI_INTENT, true);
        		    	intent.putExtra(Const.GROUP_URI_NAME, groupName);
        		}
		}
				
		intent.putExtra(Const.SIGN_IN, true);
		SplashScreenActivity.this.startActivity(intent);
		
		if (passcodeProtect==true && openPushNotification == false) {
			Intent passcode = new Intent(SplashScreenActivity.this,
					PasscodeActivity.class);
			passcode.putExtra("protect", true);
			SplashScreenActivity.this.startActivity(passcode);
		}
		
		finish();
	}

	private boolean authentificationOk(User user) {

		boolean authentificationOk = false;

		if (user.getEmail() != null && !user.getEmail().equals("")) {
			if (user.getEmail().equals(mSavedEmail)) {
				authentificationOk = true;
			}
		}
		return authentificationOk;
	}

}
