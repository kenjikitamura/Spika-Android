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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.SpikaApp;

/**
 * HookUpAlertDialog
 * 
 * Shows alert with buttons options OK and CLOSE, lets user define custom actions for those buttons.
 */

public class HookUpAlertDialog extends Dialog {

	private TextView mTvAlertMessage;
	private Button mBtnDialog;

	private Context mContext;

	public HookUpAlertDialog(final Context context) {
		super(context, R.style.Theme_Transparent);
		mContext = context;

		this.setContentView(R.layout.hookup_alert_dialog);

		mTvAlertMessage = (TextView) this.findViewById(R.id.tvMessage);
		mTvAlertMessage.setTypeface(SpikaApp.getTfMyriadPro());

		mBtnDialog = (Button) this.findViewById(R.id.btnDialog);
		mBtnDialog.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnDialog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				HookUpAlertDialog.this.dismiss();

			}
		});

	}

	/**
	 * Sets custom alert message
	 * 
	 * @param alertMessage
	 */
	public void setMessage(final String alertMessage) {
		mTvAlertMessage.setText(alertMessage);
	}

	public void setButton(ButtonType buttonType) {
		switch (buttonType) {
		case OK:
			mBtnDialog.setText(mContext.getString(R.string.OK));
			mBtnDialog.setBackgroundResource(R.drawable.rounded_rect_positive_selector);
			break;
		case CLOSE:
			mBtnDialog.setText(mContext.getString(R.string.CLOSE));
			mBtnDialog.setBackgroundResource(R.drawable.rounded_rect_alert_selector);
			break;
		default:
			break;
		}
	}

	/**
	 * Shows dialog with custom alert message
	 * 
	 * @param alertMessage
	 */
	public void show(String alertMessage) {
		mTvAlertMessage.setText(alertMessage);
		HookUpAlertDialog.this.show();
	}
	
	public void show(String alertMessage, ButtonType buttonType) {
		setButton(buttonType);
		mTvAlertMessage.setText(alertMessage);
		HookUpAlertDialog.this.show();
	}

	public enum ButtonType {
		OK, CLOSE
	}

}
