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

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

/**
 * DatePickerDialogWithRange
 * 
 * Lets user choose some date between 01/01/1900 and current date.
 */

public class DatePickerDialogWithRange extends DatePickerDialog {

	 static int maxYear=2013; 
	 static int maxMonth=11;
	 static int maxDay=31;

	 int minYear=1900;
	 int minMonth=0;
	 int minDay=1;


	public DatePickerDialogWithRange(Context context,  OnDateSetListener callBack, int maxYear, int maxMonth, int maxDay) {
	    super(context, callBack, maxYear, maxMonth, maxDay);
	    DatePickerDialogWithRange.maxDay = maxDay;
	    DatePickerDialogWithRange.maxMonth = maxMonth;
	    DatePickerDialogWithRange.maxYear = maxYear;
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	    super.onDateChanged(view, year, monthOfYear, dayOfMonth);

	        if (year > maxYear ||monthOfYear > maxMonth && year == maxYear||
	                 dayOfMonth > maxDay && year == maxYear && monthOfYear == maxMonth){
	            view.updateDate(maxYear, maxMonth, maxDay);
	            }else if(year < minYear ||monthOfYear < minMonth && year == minYear||
	                 dayOfMonth < minDay && year == minYear && monthOfYear == minMonth){
	             view.updateDate(minYear, minMonth, minDay );
	            }
	}
}
