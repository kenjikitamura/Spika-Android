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

import java.util.concurrent.ExecutionException;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Logger;
import com.google.android.gcm.GCMBaseIntentService;

/**
 * GCMIntentService
 * 
 * Handles push broadcast and generates HookUp notification if application is in
 * foreground or Android notification if application is in background.
 */

public class GCMIntentService extends GCMBaseIntentService {

	private static int mNotificationCounter = 1;
	public final static String PUSH = "com.cloverstudio.spikademo.GCMIntentService.PUSH";
	private static final Intent mPushBroadcast = new Intent(PUSH);

	public GCMIntentService() {
		super(Const.PUSH_SENDER_ID);
	}

	private final String TAG = "=== GCMIntentService ===";

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {

		if (!registrationId.equals(null)) {
			new SavePushTokenAsync(context).execute(registrationId,
					Const.ONLINE);
		}

	}

	/**
	 * Method called on device unregistered
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {

		if (!registrationId.equals(null)) {
			new RemovePushTokenAsync(context).execute(Const.OFFLINE);
		}
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {

		Bundle pushExtras = intent.getExtras();
		String pushMessage = intent.getStringExtra(Const.PUSH_MESSAGE);
		String pushFromName = intent.getStringExtra(Const.PUSH_FROM_NAME);
		try {
			boolean appIsInForeground = new SpikaApp.ForegroundCheckAsync()
					.execute(getApplicationContext()).get();
			boolean screenLocked = ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE))
					.inKeyguardRestrictedInputMode();
			if (appIsInForeground && !screenLocked) {

				mPushBroadcast.replaceExtras(pushExtras);

				LocalBroadcastManager.getInstance(this).sendBroadcast(
						mPushBroadcast);
			} else {
				triggerNotification(this, pushMessage, pushFromName, pushExtras);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method called on Error
	 * */
	@Override
	protected void onError(Context arg0, String errorId) {
		Logger.error(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}

	@SuppressWarnings("deprecation")
	public void triggerNotification(Context context, String message,
			String fromName, Bundle pushExtras) {

		if (fromName != null) {
			final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Notification notification = new Notification(
					R.drawable.icon_notification, message,
					System.currentTimeMillis());
			notification.number = mNotificationCounter + 1;
			mNotificationCounter = mNotificationCounter + 1;

			Intent intent = new Intent(this, SplashScreenActivity.class);
			intent.replaceExtras(pushExtras);
			intent.putExtra(Const.PUSH_INTENT, true);
			intent.setAction(Long.toString(System.currentTimeMillis()));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_FROM_BACKGROUND
					| Intent.FLAG_ACTIVITY_TASK_ON_HOME);
			PendingIntent pendingIntent = PendingIntent.getActivity(this,
					notification.number, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(this,
					context.getString(R.string.app_name), message,
					pendingIntent);
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			String notificationId = Double.toString(Math.random());
			notificationManager.notify(notificationId, 0, notification);
		}
	}

	private class SavePushTokenAsync extends SpikaAsync<String, Void, Boolean> {

		protected SavePushTokenAsync(Context context) {
			super(context);
		}

		User currentUserData = null;
		String currentPushToken = null;

		@Override
		protected void onPreExecute() {
			// save data of current login user so if anything goes wrong with
			// update, we can return to previous state

			currentPushToken = SpikaApp.getPreferences().getUserPushToken();
			super.onPreExecute();
		}

		String pushToken = null;
		String onlineStatus = null;

		@Override
		protected Boolean doInBackground(String... params) {
			/*
			 * XXX SpikaApp.getPreferences().getUserEmail() returns null,
			 * somewhere in code the email is lost from preferences. Preferences
			 * are set in SignInActivity -> CouchDB.auth(mSignUpEmail,
			 * mSignUpPassword);
			 */

			// Old code:
			// User loginUser =
			// CouchDB.findUserByEmail(SpikaApp.getPreferences()
			// .getUserEmail(), false);
			// end:Old code

			// New code
			User loginUser = CouchDB.findUserByEmail(UsersManagement
					.getLoginUser().getEmail(), false);
			SpikaApp.getPreferences().setUserEmail(
					UsersManagement.getLoginUser().getEmail());
			// end:New code

			UsersManagement.setLoginUser(loginUser);
			currentUserData = UsersManagement.getLoginUser();

			pushToken = params[0];
			onlineStatus = params[1];

			/* set new androidToken and onlineStatus */
			UsersManagement.getLoginUser().setOnlineStatus(onlineStatus);
			SpikaApp.getPreferences().setUserPushToken(pushToken);
			return CouchDB.updateUser(UsersManagement.getLoginUser());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				/* update successful */

			} else {
				/*
				 * something went wrong with update profile, returning logged in
				 * user to state before update
				 */
				UsersManagement.setLoginUser(currentUserData);
				SpikaApp.getPreferences().setUserPushToken(currentPushToken);
			}

			super.onPostExecute(result);
		}
	}

	private class RemovePushTokenAsync extends
			SpikaAsync<String, Void, String> {

		protected RemovePushTokenAsync(Context context) {
			super(context);
		}

		@Override
		protected String doInBackground(String... params) {

			SpikaApp.getPreferences().setUserPushToken("");

			if (UsersManagement.getLoginUser() != null)
				return CouchDB.unregisterPushToken(UsersManagement
						.getLoginUser().getId());
			else
				return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null && result.contains("OK")) {
				/* update successful */
				SpikaApp.getPreferences().setUserEmail("");
				SpikaApp.getPreferences().setUserPassword("");

			}

			super.onPostExecute(result);
		}
	}

}
