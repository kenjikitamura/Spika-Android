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

package com.cloverstudio.spikademo.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.CreateGroupActivity;
import com.cloverstudio.spikademo.GroupProfileActivity;
import com.cloverstudio.spikademo.SpikaApp;
import com.cloverstudio.spikademo.SettingsActivity;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * HookUpPasswordDialog
 * 
 * Shows dialog and lets user change his password or if it's used for a group, simply type new password and repeat it.
 */

public class HookUpPasswordDialog extends Dialog {

	private EditText mEtCurrentPassword;
	private EditText mEtNewPassword;
	private EditText mEtNewPasswordAgain;
	private Button mBtnSave;
	private Button mBtnCancel;
	private String mCurrentPassword;

	public static final int BUTTON_SAVE = 0;
	public static final int BUTTON_CANCEL = 1;
	private static final String PASSWORDS_DONT_MATCH = "Passwords don't match";
	private static final String WRONG_PASSWORD = "Wrong password";
	private static final String PASSWORD_CHANGE_SUCCESS = "Success";

	private boolean mIsForGroup;

	private Activity mActivity;

	public HookUpPasswordDialog(final Activity activity,
			final boolean isForGroup) {
		super(activity, R.style.Theme_Transparent);
		mIsForGroup = isForGroup;
		mActivity = activity;

		if (mIsForGroup) {
			setContentView(R.layout.new_group_password_dialog);
		} else {
			setContentView(R.layout.password_dialog);
			mEtCurrentPassword = (EditText) findViewById(R.id.etCurrentPassword);
			mEtCurrentPassword.setTypeface(SpikaApp.getTfMyriadPro());
		}

		mEtNewPassword = (EditText) findViewById(R.id.etNewPassword);
		mEtNewPassword.setTypeface(SpikaApp.getTfMyriadPro());

		mEtNewPasswordAgain = (EditText) findViewById(R.id.etNewPasswordAgain);
		mEtNewPasswordAgain.setTypeface(SpikaApp.getTfMyriadPro());

		mBtnSave = (Button) findViewById(R.id.btnSave);
		mBtnSave.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String passwordsResult = checkPasswords();
				if (passwordsResult.equals(PASSWORD_CHANGE_SUCCESS)) {
					
					String newPassword = mEtNewPassword.getText().toString();
					if (activity instanceof SettingsActivity) {
						if (passwordIsValid(newPassword, mIsForGroup)) {
							((SettingsActivity) activity)
									.setNewPassword(newPassword);
							HookUpPasswordDialog.this.dismiss();
							
						}
					}
					if (activity instanceof GroupProfileActivity) {
						if (passwordIsValid(newPassword, mIsForGroup)) {
							((GroupProfileActivity) activity)
									.setNewPassword(newPassword);
							HookUpPasswordDialog.this.dismiss();
						}
					}
					if (activity instanceof CreateGroupActivity) {
						if (passwordIsValid(newPassword, mIsForGroup)) {
							((CreateGroupActivity) activity)
									.setNewPassword(newPassword);
							HookUpPasswordDialog.this.dismiss();
						}
					}
					
				} else {
					Toast.makeText(activity, passwordsResult,
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		mBtnCancel = (Button) this.findViewById(R.id.btnCancel);
		mBtnCancel.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HookUpPasswordDialog.this.dismiss();

			}
		});

	}

	@Override
	public void dismiss() {
		mEtNewPassword.setText(null);
		mEtNewPasswordAgain.setText(null);
		if (!mIsForGroup)
			mEtCurrentPassword.setText(null);
		super.dismiss();
	}

	private String checkPasswords() {
		String newPass = mEtNewPassword.getText().toString();
		String newPassAgain = mEtNewPasswordAgain.getText().toString();
		if (!mIsForGroup) {
			String currentPass = mEtCurrentPassword.getText().toString();
			if (!currentPass.equals(mCurrentPassword)) {
				return WRONG_PASSWORD;
			}
			if (!newPass.equals(newPassAgain)) {
				return PASSWORDS_DONT_MATCH;
			}
		} else {
			if (!newPass.equals(newPassAgain)) {
				return PASSWORDS_DONT_MATCH;
			}
		}
		return PASSWORD_CHANGE_SUCCESS;
	}

	public void show(String currentPassword) {
		mCurrentPassword = currentPassword;
		super.show();
	}

	private boolean passwordIsValid(String password, boolean forGroup) {
		String passwordResult = "";
		if (forGroup) {
			passwordResult = Utils.checkPasswordForGroup(mActivity, password);
		} else {
			passwordResult = Utils.checkPassword(mActivity, password);
		}
		if (!passwordResult.equals(mActivity.getString(R.string.password_ok))) {
			Toast.makeText(mActivity, passwordResult, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else {
			return true;
		}
	}

}
