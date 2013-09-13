package com.cloverstudio.spikademo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

import com.cloverstudio.spikademo.R;

public class ChooseAgeDialog extends Dialog {
	
	OnClickCustomListener mListener;

	public ChooseAgeDialog(Context context) {
		super(context);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		setContentView(R.layout.dialog_choose_age);
		
		Button btnOk=(Button)findViewById(R.id.chooseAgeOk);
		
		btnOk.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP){
					mListener.onClickOk(getStringDate());
				}
				return false;
			}
		});
		
		Button btnCancel=(Button)findViewById(R.id.chooseAgeCancel);
		
		btnCancel.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP){
					mListener.onClickCancel();
				}
				return false;
			}
		});
		
		this.setCancelable(false);
	}
	
	public void show(OnClickCustomListener listener) {
		this.show();
		mListener=listener;
	}
	
	private String getStringDate(){
		
		DatePicker dPicker=(DatePicker) findViewById(R.id.datePicker1);
		
		String date=dPicker.getDayOfMonth()+" "+(dPicker.getMonth()+1)+" "+dPicker.getYear();
		
		return date;
				
	}
	
	public interface OnClickCustomListener{
		
		public void onClickOk(String date);
		
		public void onClickCancel();
	
	}
	
}
