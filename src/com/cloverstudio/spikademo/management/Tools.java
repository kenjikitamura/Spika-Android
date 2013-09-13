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

package com.cloverstudio.spikademo.management;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.SpikaApp;

/**
 * Tools
 * 
 * Contains some date, time helpers methods and a token generator.
 */

public class Tools {

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat _dateFormat = new SimpleDateFormat(
			SpikaApp.getInstance()
					.getString(R.string.hookup_date_time_format));

	/**
	 * Returns current time
	 * 
	 * 
	 * @return time in milliseconds
	 * @throws Exception
	 */
	public static long getCurrentDateTime() {
		long currentTime = System.currentTimeMillis();
		return currentTime;
	}

	/**
	 * Returns time formatted as: JAN 24, 13:30
	 * 
	 * @param timeInSeconds
	 * @return date as a String
	 */
	public static String getFormattedDateTime(long timeInSeconds) {
		Date dateTime = new Date(timeInSeconds * 1000);
		StringBuilder date = new StringBuilder(_dateFormat.format(dateTime));
		return date.toString();
	}

	// /**
	// * Returns current time formatted as: JAN 24, 13:30
	// * TimeZone UTC
	// *
	// * @return date as a String
	// * @throws Exception
	// */
	// public static String getCurrentDateTimeInUTC() throws Exception {
	// Date currentDate = new Date(System.currentTimeMillis());
	// StringBuilder date = new StringBuilder(_dateFormat.format(currentDate));
	// return convertLocalTimeToUTC(date.toString());
	// }
	//
	// /**
	// * Returns current time formatted as: JAN 24, 13:30
	// * TimeZone Local
	// *
	// * @return date as a String
	// * @throws Exception
	// */
	// public static String getDateTimeInLocalTime(String dateInUTC) throws
	// Exception {
	// return convertUTCtoLocalTime(dateInUTC);
	// }

	public static String convertLocalTimeToUTC(String p_localDateTime)
			throws Exception {

		String lv_dateFormateInUTC = "";// Will hold the final converted date
		Date lv_localDate = null;

		// create a new Date object using the timezone of the specified city
		_dateFormat.setTimeZone(TimeZone.getDefault());
		lv_localDate = _dateFormat.parse(p_localDateTime);

		// Convert the date from the local timezone to UTC timezone
		_dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		lv_dateFormateInUTC = _dateFormat.format(lv_localDate);

		return lv_dateFormateInUTC;
	}

	public static String convertUTCtoLocalTime(String p_UTCDateTime)
			throws Exception {

		String lv_dateFormateInLocalTimeZone = "";// Will hold the final
													// converted date
		Date lv_localDate = null;

		// create a new Date object using the UTC timezone
		_dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		lv_localDate = _dateFormat.parse(p_UTCDateTime);

		// Convert the UTC date to Local timezone
		_dateFormat.setTimeZone(TimeZone.getDefault());
		lv_dateFormateInLocalTimeZone = _dateFormat.format(lv_localDate);

		return lv_dateFormateInLocalTimeZone;
	}

	public static String generateToken() {
		char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
				.toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < 40; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}

		return sb.toString();
	}

}
