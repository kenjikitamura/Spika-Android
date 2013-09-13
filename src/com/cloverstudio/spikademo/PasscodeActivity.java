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

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.utils.Logger;

/**
 * PasscodeActivity
 * 
 * Shows passcode interface allowing user to type new passcode or change existing one.
 */

public class PasscodeActivity extends Activity {

	private TextView mTvPasscode;
	private Button mBtnDelete;
	private ImageView mPasscode1;
	private ImageView mPasscode2;
	private ImageView mPasscode3;
	private ImageView mPasscode4;
	private Button mBtnPasscodeOk;
	private RelativeLayout mRlPasscodeResult;
	private TextView mTvPasscodeResult;

	private InputState mInputState;
	private PasscodeState mPasscodeState;
	private String mTargetPasscode = "";
	private String mTypedPasscode = "";

	private final AlphaAnimation mFadeIn = new AlphaAnimation(0F, 1.0F);
	private final AlphaAnimation mFadeOut = new AlphaAnimation(1.0F, 0F);
	private TableLayout mTlPasscodeKeyboard;
	private List<Button> mKeyboardButtons;
	
	private boolean mIsProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_passcode);
		
		if (getIntent().getBooleanExtra("protect", false) == true) {
			mIsProtect = true;
		} else {
			mIsProtect = false;
		}

		mRlPasscodeResult = (RelativeLayout) findViewById(R.id.rlPasscodeResult);
		mRlPasscodeResult.setVisibility(View.GONE);
		mTvPasscodeResult = (TextView) findViewById(R.id.tvPasscodeResult);

		mTvPasscode = (TextView) findViewById(R.id.tvPasscode);
		mBtnDelete = (Button) findViewById(R.id.btnDelete);
		mPasscode1 = (ImageView) findViewById(R.id.passcode1);
		mPasscode2 = (ImageView) findViewById(R.id.passcode2);
		mPasscode3 = (ImageView) findViewById(R.id.passcode3);
		mPasscode4 = (ImageView) findViewById(R.id.passcode4);
		setInput(InputState.NONE);

		mBtnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteInput();

			}
		});

		mTlPasscodeKeyboard = (TableLayout) findViewById(R.id.tlPasscodeKeyboard);
		mKeyboardButtons = new ArrayList<Button>();
		for (View view : mTlPasscodeKeyboard.getTouchables()) {
			if (view instanceof Button) {
				mKeyboardButtons.add((Button) view);
				((Button) view).setOnClickListener(getKeyboardListener());
			}
		}

		mBtnPasscodeOk = (Button) findViewById(R.id.btnPasscodeOk);
		mBtnPasscodeOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkPasscode();
			}
		});

		getPasscodeFromPreferences();

	}

	@Override
	protected void onDestroy() {
		unbindDrawables(findViewById(R.id.rlPasscodeResult));
		mKeyboardButtons.clear();
		mKeyboardButtons = null;
		mTlPasscodeKeyboard = null;
		super.onDestroy();

	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	private void getPasscodeFromPreferences() {
		String savedPasscode = SpikaApp.getPreferences().getPasscode();
		if (savedPasscode.equals(null) || savedPasscode.equals("")) {
			mPasscodeState = PasscodeState.NEW_PASSCODE;
			mTvPasscode.setText(getString(R.string.please_enter_new_passcode));
		} else {
			mPasscodeState = PasscodeState.CURRENT_PASSCODE;
			mTvPasscode.setText(getString(R.string.please_enter_passcode));
			mTargetPasscode = savedPasscode;
		}
	}

	private OnClickListener getKeyboardListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (mInputState) {
				case NONE:
					setInput(InputState.ONE);
					break;
				case ONE:
					setInput(InputState.TWO);
					break;
				case TWO:
					setInput(InputState.THREE);
					break;
				case THREE:
					setInput(InputState.ALL);
					break;
				case ALL:
					return;
				default:
					break;
				}

				switch (v.getId()) {
				case R.id.btn1:
					mTypedPasscode += "1";
					break;
				case R.id.btn2:
					mTypedPasscode += "2";
					break;
				case R.id.btn3:
					mTypedPasscode += "3";
					break;
				case R.id.btn4:
					mTypedPasscode += "4";
					break;
				case R.id.btn5:
					mTypedPasscode += "5";
					break;
				case R.id.btn6:
					mTypedPasscode += "6";
					break;
				case R.id.btn7:
					mTypedPasscode += "7";
					break;
				case R.id.btn8:
					mTypedPasscode += "8";
					break;
				case R.id.btn9:
					mTypedPasscode += "9";
					break;
				case R.id.btn0:
					mTypedPasscode += "0";
					break;
				default:
					break;
				}

			}
		};

	}

	private void deleteInput() {
		switch (mInputState) {
		case NONE:
			break;
		case ONE:
			mTypedPasscode = "";
			setInput(InputState.NONE);
			break;
		case TWO:
			setInput(InputState.ONE);
			mTypedPasscode = mTypedPasscode.substring(0,
					mTypedPasscode.length() - 1);
			break;
		case THREE:
			setInput(InputState.TWO);
			mTypedPasscode = mTypedPasscode.substring(0,
					mTypedPasscode.length() - 1);
			break;
		case ALL:
			setInput(InputState.THREE);
			mTypedPasscode = mTypedPasscode.substring(0,
					mTypedPasscode.length() - 1);
			break;
		default:
			break;
		}

	}

	private void setInput(InputState state) {
		mInputState = state;
		switch (state) {
		case NONE:
			mPasscode1.setImageResource(R.drawable.circle_shape_neutral);
			mPasscode2.setImageResource(R.drawable.circle_shape_neutral);
			mPasscode3.setImageResource(R.drawable.circle_shape_neutral);
			mPasscode4.setImageResource(R.drawable.circle_shape_neutral);
			break;
		case ONE:
			mPasscode1.setImageResource(R.drawable.circle_shape_alert);
			mPasscode2.setImageResource(R.drawable.circle_shape_neutral);
			mPasscode3.setImageResource(R.drawable.circle_shape_neutral);
			mPasscode4.setImageResource(R.drawable.circle_shape_neutral);
			break;
		case TWO:
			mPasscode1.setImageResource(R.drawable.circle_shape_alert);
			mPasscode2.setImageResource(R.drawable.circle_shape_alert);
			mPasscode3.setImageResource(R.drawable.circle_shape_neutral);
			mPasscode4.setImageResource(R.drawable.circle_shape_neutral);
			break;
		case THREE:
			mPasscode1.setImageResource(R.drawable.circle_shape_alert);
			mPasscode2.setImageResource(R.drawable.circle_shape_alert);
			mPasscode3.setImageResource(R.drawable.circle_shape_alert);
			mPasscode4.setImageResource(R.drawable.circle_shape_neutral);
			break;
		case ALL:
			mPasscode1.setImageResource(R.drawable.circle_shape_alert);
			mPasscode2.setImageResource(R.drawable.circle_shape_alert);
			mPasscode3.setImageResource(R.drawable.circle_shape_alert);
			mPasscode4.setImageResource(R.drawable.circle_shape_alert);
			break;
		case SUCCESS:
			animateResultForSuccess(true);
			break;
		case FAIL:
			animateResultForSuccess(false);
			break;
		}
	}

	private void checkPasscode() {

		if (mInputState == InputState.ALL) {

			switch (mPasscodeState) {
			case CURRENT_PASSCODE:
				if (mTypedPasscode.equals(mTargetPasscode)) {
					setInput(InputState.SUCCESS);
				} else {
					setInput(InputState.FAIL);
				}
				break;
			case NEW_PASSCODE:
				setInput(InputState.NONE);
				mTargetPasscode = mTypedPasscode;
				mTypedPasscode = "";
				mPasscodeState = PasscodeState.NEW_PASSCODE_REPEAT;
				mTvPasscode
						.setText(getString(R.string.please_enter_new_passcode_again));
				break;
			case NEW_PASSCODE_REPEAT:
				if (mTypedPasscode.equals(mTargetPasscode)) {
					SpikaApp.getPreferences().setPasscode(mTypedPasscode);
					setInput(InputState.SUCCESS);
				} else {
					setInput(InputState.FAIL);
					mTargetPasscode = "";
					mPasscodeState = PasscodeState.NEW_PASSCODE;
					mTvPasscode
							.setText(getString(R.string.please_enter_new_passcode));
				}
				break;
			default:
				break;
			}

		}

	}

	private void animateResultForSuccess(final boolean isSuccess) {

		if (isSuccess) {
			mRlPasscodeResult.setBackgroundResource(R.color.hookup_positive);
			mTvPasscodeResult.setText(getString(R.string.SUCCESS));
		} else {
			mRlPasscodeResult.setBackgroundResource(R.color.hookup_alert);
			mTvPasscodeResult.setText(getString(R.string.FAIL));
		}
		setInput(InputState.NONE);
		mTypedPasscode = "";

		mFadeIn.setFillAfter(false);
		mFadeIn.setFillEnabled(false);
		mFadeIn.setDuration(getResources().getInteger(
				android.R.integer.config_longAnimTime));
		mFadeIn.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				mRlPasscodeResult.setVisibility(View.VISIBLE);
				for (Button button : mKeyboardButtons) {
					button.setEnabled(false);
				}
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mRlPasscodeResult.startAnimation(mFadeOut);
			}
		});

		mFadeOut.setStartOffset(800);
		mFadeOut.setDuration(getResources().getInteger(
				android.R.integer.config_longAnimTime));
		mFadeOut.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				if (isSuccess) {
					setResult(Activity.RESULT_OK);
					finish();
				}
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mRlPasscodeResult.setVisibility(View.INVISIBLE);
				for (Button button : mKeyboardButtons) {
					button.setEnabled(true);
				}

			}
		});
		mRlPasscodeResult.startAnimation(mFadeIn);

	}

	@Override
	public void onBackPressed() {
		if (mInputState != InputState.NONE || mIsProtect) {
			deleteInput();
		} else {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mInputState != InputState.NONE || mIsProtect) {
				deleteInput();
				return true;
			}
		}
		setResult(Activity.RESULT_CANCELED);
		return super.onKeyDown(keyCode, event);
	}

	private enum InputState {
		NONE, ONE, TWO, THREE, ALL, FAIL, SUCCESS

	}

	private enum PasscodeState {
		NEW_PASSCODE, NEW_PASSCODE_REPEAT, CURRENT_PASSCODE

	}

}
