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

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.couchdb.model.Message;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.management.UsersManagement;

/**
 * FindMessagesForUserAsync
 * 
 * AsyncTask for finding messages for user on CouchDB.
 */

public class FindMessagesForUserAsync extends SpikaAsync<Void, Void, ArrayList<Message>>{

	private String TAG = "FindMessageForUser";
	
	private ProgressDialog mProgessDialog;

	
	public FindMessagesForUserAsync(Context context){
		super(context);
	}
	
	@Override
	protected void onPreExecute() {
		
		super.onPreExecute();
		
		try{
    		mProgessDialog = new ProgressDialog(mContext);
            mProgessDialog.setMessage("Loading messages. Please wait...");
            mProgessDialog.setIndeterminate(false);
            mProgessDialog.setCancelable(false);
            mProgessDialog.show();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected ArrayList<Message> doInBackground(Void... params) {
		return CouchDB.findMessagesForUser(UsersManagement.getFromUser(), 0);
	}
	
	@Override
	protected void onPostExecute(ArrayList<Message> result) {
		
		UpdateMessagesInListView.updateListView(result);
		mProgessDialog.dismiss();
	}
	
	
}
