package com.cloverstudio.spikademo.messageshandling;

import android.content.Context;

import com.cloverstudio.spikademo.couchdb.CouchDB;
import com.cloverstudio.spikademo.extendables.SpikaAsync;

public class FindAvatarFileId extends SpikaAsync<String, Void, String> {

	public FindAvatarFileId(Context context) {
		super(context);
	}

	@Override
	protected String doInBackground(String... params) {
		String userId = params[0];
		return CouchDB.findAvatarFileId(userId);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}
}
