package com.cloverstudio.spikademo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.SpikaApp;

public class HookUpDialog extends Dialog {

	private TextView mTvAlertMessage;
	private Button mBtnOk;
	private Button mBtnCancel;

	public static final int BUTTON_OK = 1001;
	public static final int BUTTON_CANCEL = 1002;

	public HookUpDialog(final Context context) {
		super(context, R.style.Theme_Transparent);
		
		this.setContentView(R.layout.hookup_dialog);
		
		mTvAlertMessage = (TextView) this.findViewById(R.id.tvMessage);
		mTvAlertMessage.setTypeface(SpikaApp.getTfMyriadPro());

		mBtnOk = (Button) this.findViewById(R.id.btnOk);
		mBtnOk.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HookUpDialog.this.dismiss();

			}
		});
		mBtnCancel = (Button) this.findViewById(R.id.btnCancel);
		mBtnCancel.setTypeface(SpikaApp.getTfMyriadProBold(), Typeface.BOLD);
		mBtnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HookUpDialog.this.dismiss();

			}
		});

	}

//	static public void showAlert(Context context,final String alertMessage){
//	    final HookUpDialog dialog = new HookUpDialog(context);
//	   
//	    dialog.setOnButtonClickListener(
//                HookUpDialog.BUTTON_OK, new View.OnClickListener() {
//                    
//                    @Override
//                    public void onClick(View v) {
//                        
//                        dialog.dismiss();
//                        
//                    }
//                    
//                });
//        
//	    dialog.showOnlyOK(alertMessage);
//	}
	
	/**
	 * Sets custom alert message
	 * 
	 * @param alertMessage
	 */
	public void setMessage(final String alertMessage) {
		mTvAlertMessage.setText(alertMessage);
	}

	/**
	 * Shows dialog with custom message
	 * 
	 * @param message
	 */
	public void show(final String message) {
		mTvAlertMessage.setText(message);
		HookUpDialog.this.show();
	}

	
	/**
     * Shows dialog with custom message
     * 
     * @param message
     */
    public void showOnlyOK(final String message) {
        mTvAlertMessage.setText(message);
        mBtnCancel.setVisibility(View.GONE);
        this.show();
    }
    
	/**
	 * Sets new OnClickListener for button "OK" or "CANCEL"
	 * 
	 * @param button
	 *            HookUpAlertDialog.BUTTON_OK or HookUpAlertDialog.BUTTON_CANCEL
	 * @param onClickListener
	 */
	public void setOnButtonClickListener(final int button,
			final View.OnClickListener onClickListener) {
		switch (button) {
		case BUTTON_OK:
			mBtnOk.setOnClickListener(onClickListener);
			break;
		case BUTTON_CANCEL:
			mBtnCancel.setOnClickListener(onClickListener);
			break;
		default:
			break;

		}
	}

}
