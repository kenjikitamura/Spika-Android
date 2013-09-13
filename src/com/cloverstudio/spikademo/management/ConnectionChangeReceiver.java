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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

/**
 * ConnectionChangeReceiver
 * 
 * Sends broadcast on connection change.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {

	/* Connection constants */
	public static final String NO_INTERNET_CONNECTION = "no_internet_connection";
	public static final String HAS_INTERNET_CONNECTION = "has_internet_connection";

	public static final String INTERNET_CONNECTION_CHANGE = "internet_connection_change";
	private Intent mConnectionChangeBroadcast = new Intent(
			INTERNET_CONNECTION_CHANGE);

	@Override
	public void onReceive(Context context, Intent intent) {
		final ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		boolean hasInternetConnection = activeNetInfo != null;
		if (hasInternetConnection) {
			mConnectionChangeBroadcast.putExtra(HAS_INTERNET_CONNECTION, true);
		} else {
			mConnectionChangeBroadcast.putExtra(HAS_INTERNET_CONNECTION, false);
		}
		LocalBroadcastManager.getInstance(context).sendBroadcast(
				mConnectionChangeBroadcast);
	}
}
