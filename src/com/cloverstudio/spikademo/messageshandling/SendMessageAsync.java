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

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cloverstudio.spikademo.WallActivity;
import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.Emoticon;
import com.cloverstudio.spikademo.couchdb.model.Message;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.management.MessageManagement;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.messageshandling.MessagesUpdater.GetMessagesAsync;
import com.cloverstudio.spikademo.utils.Const;

/**
 * SendMessageAsync
 * 
 * AsyncTask for sending all sorts of messages or adding comments.
 */

public class SendMessageAsync extends SpikaAsync<Object, Void, Boolean> {

	private String TAG = "SendMessage";
	private int messageType;

	public static final int TYPE_PHOTO = 0;
	public static final int TYPE_TEXT = 1;
	public static final int TYPE_EMOTICON = 2;
	public static final int TYPE_LOCATION = 3;
	public static final int TYPE_VOICE = 4;
	public static final int TYPE_VIDEO = 5;

	private boolean isComment;

	public SendMessageAsync(Context context, int messageType) {
		super(context);
		this.messageType = messageType;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Object... params) {

		boolean isRedirection = false;
		boolean isSuccess = false;
		isComment = false;
		Object obj = params[0];

		try {
			isRedirection = (Boolean) params[1];
		} catch (Exception e) {
			Log.v(TAG, "no redirection");
			isRedirection = false;
		}

		try {
			isComment = (Boolean) params[2];
		} catch (Exception e) {
			Log.e(TAG, "not a comment");
			isComment = false;
		}

		String body = "";
		Message message = null;
		Emoticon emoticon = null;
		String latitude = "";
		String longtitude = "";
        String fileId = null;
        String fileThumbId = null;

		if (isComment) {
			try {
				message = (Message) obj;
				message.setModified(System.currentTimeMillis() / 1000);
				sendMessage(message, isComment);
				isSuccess = true;// TODO
			} catch (Exception e) {
				Log.e(TAG, "params not message!");
				message = null;
				isSuccess = false;
			}
		} else {
			switch (messageType) {
			case TYPE_PHOTO:
				try {
                    fileId = (String) params[3];
                    fileThumbId = (String) params[4];
					if (UsersManagement.getToUser() != null) {
						message = MessageManagement.createMessage(Const.IMAGE,
								Const.USER, body, latitude, longtitude, fileId,
								"", "", "",fileThumbId,"");
						isSuccess = CouchDB.sendMessageToUser(message,
								isComment);
					}
					if (UsersManagement.getToGroup() != null) {
						message = MessageManagement.createMessage(Const.IMAGE,
								Const.GROUP, body, latitude, longtitude,
								fileId, "", "", "",fileThumbId,"");
						isSuccess = CouchDB.sendMessageToGroup(message,
								isComment);
					}

				} catch (Exception e) {
					Log.e(TAG, "params not bitmap!");
				}
				break;
			case TYPE_TEXT:
				try {
					body = (String) obj;
					Log.v(TAG, body);

					if (UsersManagement.getToUser() != null) {
						message = MessageManagement.createMessage(Const.TEXT,
								Const.USER, body, latitude, longtitude, "", "",
								"", "","","");
						isSuccess = CouchDB.sendMessageToUser(message,
								isComment);
					}
					if (UsersManagement.getToGroup() != null) {
						message = MessageManagement.createMessage(Const.TEXT,
								Const.GROUP, body, latitude, longtitude, "",
								"", "", "","","");
						isSuccess = CouchDB.sendMessageToGroup(message,
								isComment);
					}

				} catch (Exception e) {
					Log.e(TAG, "params not string!");
				}
				break;
			case TYPE_EMOTICON:
				try {
					emoticon = (Emoticon) obj;
					Log.v(TAG, body);

					if (UsersManagement.getToUser() != null) {
						message = MessageManagement.createMessage(
								Const.EMOTICON, Const.USER,
								emoticon.getIdentifier(), latitude, longtitude,
								"", "", "", emoticon.getImageUrl(),"","");
						isSuccess = CouchDB.sendMessageToUser(message,
								isComment);
					}
					if (UsersManagement.getToGroup() != null) {
						message = MessageManagement.createMessage(
								Const.EMOTICON, Const.GROUP,
								emoticon.getIdentifier(), latitude, longtitude,
								"", "", "", emoticon.getImageUrl(),"","");
						isSuccess = CouchDB.sendMessageToGroup(message,
								isComment);
					}

				} catch (Exception e) {
					Log.e(TAG, "params not emoticon!");
				}
				break;
			case TYPE_LOCATION:
				try {
					body = (String) obj;
					latitude = (String) params[3];
					longtitude = (String) params[4];
					Log.v(TAG, body);

					if (UsersManagement.getToUser() != null) {
						message = MessageManagement.createMessage(
								Const.LOCATION, Const.USER, body, latitude,
								longtitude, "", "", "", "","","");
						isSuccess = CouchDB.sendMessageToUser(message,
								isComment);
					}
					if (UsersManagement.getToGroup() != null) {
						message = MessageManagement.createMessage(
								Const.LOCATION, Const.GROUP, body, latitude,
								longtitude, "", "", "", "","","");
						isSuccess = CouchDB.sendMessageToGroup(message,
								isComment);
					}

				} catch (Exception e) {
					Log.e(TAG, "params not string!");
				}
				break;
			case TYPE_VOICE:
				try {
					fileId = (String) params[3];
					body = (String) obj;
					Log.v(TAG, body);

					if (UsersManagement.getToUser() != null) {
						message = MessageManagement.createMessage(Const.VOICE,
								Const.USER, body, latitude, longtitude, "",
								fileId, "", "","","");
						isSuccess = CouchDB.sendMessageToUser(message,
								isComment);
					}
					if (UsersManagement.getToGroup() != null) {
						message = MessageManagement.createMessage(Const.VOICE,
								Const.GROUP, body, latitude, longtitude, "",
								fileId, "", "","","");
						isSuccess = CouchDB.sendMessageToGroup(message,
								isComment);
					}

				} catch (Exception e) {
					Log.e(TAG, "params not string!");
				}
				break;
			case TYPE_VIDEO:
				try {
					body = (String) obj;
					fileId = (String) params[3];
					Log.v(TAG, body);

					if (UsersManagement.getToUser() != null) {
						message = MessageManagement.createMessage(Const.VIDEO,
								Const.USER, body, latitude, longtitude, "", "",
								fileId, "","","");
						isSuccess = CouchDB.sendMessageToUser(message,
								isComment);
					}
					if (UsersManagement.getToGroup() != null) {
						message = MessageManagement.createMessage(Const.VIDEO,
								Const.GROUP, body, latitude, longtitude, "",
								"", fileId, "","","");
						isSuccess = CouchDB.sendMessageToGroup(message,
								isComment);
					}

				} catch (Exception e) {
					Log.e(TAG, "params not string!");
				}
				break;
			default:
				break;
			}
		}

		if (message != null) {

		} else {
			Log.e(TAG, "user and/or target group = null");
		}

		// return isRedirection;
		return isSuccess;
	}

	private void sendMessage(Message message, boolean isPut) {
		
		if (UsersManagement.getToUser() != null) {
			CouchDB.updateMessageForUser(message);
		}
		if (UsersManagement.getToGroup() != null) {
			CouchDB.updateMessageForGroup(message);
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (WallActivity.getInstance() != null) {
			new GetMessagesAsync(WallActivity.getInstance()).execute();
		}
		if (!isComment) {
			if (result) {
				//Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
			}
		}
	}
}