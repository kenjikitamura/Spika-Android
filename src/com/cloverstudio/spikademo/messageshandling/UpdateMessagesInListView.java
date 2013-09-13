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
import java.util.Collections;

import android.app.Activity;
import android.util.Log;

import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.model.Message;
import com.cloverstudio.spikademo.management.TimeMeasurer;
import com.cloverstudio.spikademo.management.UsersManagement;

/**
 * UpdateMessagesInListView
 * 
 * Handles updating wall messages.
 */

public class UpdateMessagesInListView {

	private static String TAG = "UpdateMessagesInListView";

	public UpdateMessagesInListView(Activity activity) {
	}

	public static void updateListView(ArrayList<Message> newMessages) {

        TimeMeasurer.dumpInterval("Start update list view");

		if (newMessages != null) {

			boolean refreshNew = false;

			if (newMessages.size() != 0) {
				try {
					newMessages = filterDoubles(newMessages);
					newMessages = addRealNewMessages(newMessages);

					if (newMessages.size() > 0) {
						WallActivity.gCurrentMessages.addAll(newMessages);
						refreshNew = true;
					} else {
						
					}

				} catch (NullPointerException npe) {
					Log.e(TAG,
							"nullpointerexception: "
									+ npe.getLocalizedMessage());
					refreshNew = false;
				}
			}

			if (refreshNew) {

				Collections.sort(WallActivity.gCurrentMessages);

				WallActivity.gMessagesAdapter.notifyDataSetChanged();

				if (MessagesUpdater.gRegularRefresh) {
					WallActivity.gLvWallMessages
							.setSelection(WallActivity.gLvWallMessages
									.getCount() - 1);
				} else {
					WallActivity.gLvWallMessages.setSelection(newMessages
							.size());
				}
			}
		}
	}

	private static ArrayList<Message> addRealNewMessages(
			ArrayList<Message> newMessages) throws NullPointerException {
		ArrayList<Message> realOnes = new ArrayList<Message>();

		for (int index = 0; index < newMessages.size(); ++index) {

			if (!currentContainsNew(newMessages.get(index))) {

				realOnes.add(newMessages.get(index));
			}
		}

		return realOnes;
	}

	private static boolean currentContainsNew(Message newMessage)
			throws NullPointerException {

		for (int index = 0; index < WallActivity.gCurrentMessages.size(); ++index) {
			if (WallActivity.gCurrentMessages.get(index).equals(newMessage)) {
				if (WallActivity.gCurrentMessages.get(index).getModified() < newMessage
						.getModified()) {
					WallActivity.gCurrentMessages.remove(index);
					return false;
				}

				return true;
			}
		}
		return false;
	}

	// Method to clear all double messages in new messages... this happens only
	// when toUser and fromUser are the same.
	private static ArrayList<Message> filterDoubles(
			ArrayList<Message> newMessages) throws NullPointerException {
		if (UsersManagement.getToUser() != null) {
			if (UsersManagement.getFromUser().getId()
					.equals(UsersManagement.getToUser().getId())) {
				ArrayList<Message> newList = new ArrayList<Message>();
				for (int i = 0; i < newMessages.size(); ++i) {
					Message message = newMessages.get(i);
					boolean toBeAdded = true;
					for (Message m : newList) {
						if (m.getId().equals(message.getId()))
							toBeAdded = false;
					}
					if (toBeAdded)
						newList.add(message);
				}
				return newList;
			}
		}
		return newMessages;
	}

}
