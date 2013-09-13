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

package com.cloverstudio.spikademo.messageshandling;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.management.SettingsManager;
import com.cloverstudio.spikademo.messageshandling.MessagesUpdater.GetMessagesAsync;

/**
 * WallScrollListener
 * 
 * Detects scroll changes in list view.
 */

public class WallScrollListener implements OnScrollListener {

	private int mLastFirstVisibleItem;
	private boolean mIsScrollingUp;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		boolean loadMore = firstVisibleItem == 2 && mIsScrollingUp;/*
												 * maybe add a padding
												 * firstVisibleItem +
												 * visibleItemCount >=
												 * totalItemCount;
												 */

		if (loadMore) {
			if (!MessagesUpdater.gIsLoading) {
				// adapter.count += visibleCount; // or any other amount
				SettingsManager.sVisibleMessageCount += SettingsManager.sMessageCount;
				SettingsManager.sPage += 1;
				MessagesUpdater.gRegularRefresh = false;
				new GetMessagesAsync(WallActivity.getInstance()).execute();
			} else {

			}
		}
	}
	

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		final int currentFirstVisibleItem = view.getFirstVisiblePosition();

		if (currentFirstVisibleItem > mLastFirstVisibleItem) {
			mIsScrollingUp = false;
		} else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
			mIsScrollingUp = true;
		}

		mLastFirstVisibleItem = currentFirstVisibleItem;
	}

}
