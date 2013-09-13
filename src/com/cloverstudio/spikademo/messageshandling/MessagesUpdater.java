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

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.Message;
import com.cloverstudio.spikademo.dialog.HookUpProgressDialog;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.management.SettingsManager;
import com.cloverstudio.spikademo.management.TimeMeasurer;
import com.cloverstudio.spikademo.management.UsersManagement;

/**
 * MessagesUpdater
 * 
 * Executes AsyncTask for fetching messages from CouchDB.
 */

public class MessagesUpdater {

	public static boolean gRegularRefresh = true;
	public static ArrayList<Message> gNewMessages = null;
	public static boolean gIsLoading = false;

	public MessagesUpdater() {
	}

	public void update() {
		if (WallActivity.getInstance() != null) {
			new GetMessagesAsync(WallActivity.getInstance()).execute();
		}
	}

	public static class GetMessagesAsync extends
			SpikaAsync<Void, Void, ArrayList<Message>> {

		private HookUpProgressDialog mProgressDialog;

		public GetMessagesAsync(Context context) {
			super(context);
			//if (mProgressDialog == null) {
			//	mProgressDialog = new HookUpProgressDialog(context);
			//}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			gIsLoading = true;
			// if(gRegularRefresh){
			//mProgressDialog.show();
			// }
		}

		@Override
		protected ArrayList<Message> doInBackground(Void... params) {

			ArrayList<Message> newMessages = new ArrayList<Message>();

            TimeMeasurer.dumpInterval("Before request");

			if (gIsLoading) {
				if (gRegularRefresh) {
					newMessages = CouchDB.findMessagesForUser(
							UsersManagement.getLoginUser(), 0);
				} else {
					newMessages = CouchDB.findMessagesForUser(
							UsersManagement.getLoginUser(),
							SettingsManager.sPage);
				}
			}
			return newMessages;
		}

		@Override
		protected void onPostExecute(ArrayList<Message> result) {

            TimeMeasurer.dumpInterval("After request");

			if (gIsLoading) {
				UpdateMessagesInListView.updateListView(result);
			}

			gIsLoading = false;
			gRegularRefresh = true;
			
			//if (mProgressDialog != null) {
			//		mProgressDialog.dismiss();
			//}

			if (WallActivity.getInstance() != null)
				WallActivity.getInstance().checkMessagesCount();

			super.onPostExecute(result);
		}
	}

}
