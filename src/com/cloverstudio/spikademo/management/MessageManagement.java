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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Base64OutputStream;

import com.cloverstudio.spikademo.couchdb.model.Message;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * MessageManagement
 * 
 * Creates message object.
 */

public class MessageManagement {

	public static Message createMessage(String messageType,
			String messageTargetType, String body,
			String latitude, String longitude, String imageFileId, String voiceFileId, String videoFileId, String emoticonImageUrl,String imageThumbFileId,String messageUrl) {

		long created = System.currentTimeMillis() / 1000;
		long modified = created;
		String fromUserName = UsersManagement.getLoginUser().getName();
		String fromUserId = UsersManagement.getLoginUser().getId();

		String toGroupName = "";
		String toGroupId = "";
		String toUserName = "";
		String toUserId = "";

		String type = Const.MESSAGE;
		boolean valid = true;
		String attachments = "";


		if (messageTargetType.equals(Const.USER)) {
			toUserName = UsersManagement.getToUser().getName();
			toUserId = UsersManagement.getToUser().getId();
		}

		if (messageTargetType.equals(Const.GROUP)) {
			toGroupName = UsersManagement.getToGroup().getName();
			toGroupId = UsersManagement.getToGroup().getId();
		}

		Message message = new Message(Const._ID, Const._REV, type, messageType,
				messageTargetType, body, fromUserId, fromUserName, toUserId,
				toUserName, toGroupId, toGroupName, created, modified,
				valid, attachments, latitude, longitude, imageFileId, voiceFileId, videoFileId, emoticonImageUrl,imageThumbFileId,messageUrl);
		return message;

	}

	public void encode(File file, OutputStream base64OutputStream) {
		try {
			InputStream is = new FileInputStream(file);
			OutputStream out = new Base64OutputStream(base64OutputStream, 0);
			Utils.copyStream(is, out);
			is.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
