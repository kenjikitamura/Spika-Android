/*
 * The MIT License (MIT)
 * 
 * Copyright ½ 2013 Clover Studio Ltd. All rights reserved.
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

package com.cloverstudio.spikademo.couchdb;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.cloverstudio.spikademo.SpikaApp;
import com.cloverstudio.spikademo.management.BitmapManagement;
import com.cloverstudio.spikademo.management.FileManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Logger;

/**
 * ConnectionHandler
 * 
 * Handles basic HTTP connections with server.
 */

public class ConnectionHandler {

	private static String TAG = "ConnectionHandler";

	public ConnectionHandler() {
	}

	/**
	 * Http GET
	 * 
	 * @param url
	 * @return
	 */
	public static JSONObject getJsonObject(String url, String userId) {

		JSONObject retVal = null;

		try {

			InputStream is = httpGetRequest(url, userId);
			String result = getString(is);

			is.close();

			retVal = jObjectFromString(result);
			
		} catch (Exception e) {

			Logger.error(TAG + "getJsonObject", e);

			return null;

		}

		Log.e("Response: ", retVal.toString());
		return retVal;
	}
	
	/**
	 * Http GET
	 * 
	 * @param url
	 * @return
	 */
	public static String getString(String url, String userId) {

		String result = null;

		try {

			InputStream is = httpGetRequest(url, userId);
			result = getString(is);

			is.close();


		} catch (Exception e) {

			Logger.error(TAG + "getJsonObject", e);

			return null;

		}

		Log.e("Response: ", result.toString());
		return result;
	}
	
	/**
	 * Http GET
	 * 
	 * @param url
	 * @return
	 */
	public static JSONArray getJsonArray(String url, String userId,
			String token) {

		JSONArray retVal = null;

		try {

			InputStream is = httpGetRequest(url, userId);
			String result = getString(is);

			is.close();

			retVal = jArrayFromString(result);

		} catch (Exception e) {

			Logger.error(TAG + "getJsonObject", e);

			return null;

		}

		Log.e("Response: ", retVal.toString());
		return retVal;
	}

	
    /**
     * Http POST
     * 
     * @param create
     * @return
     */
    public static JSONObject postJsonObject(String apiName,JSONObject create, String userId,
            String token) {

        JSONObject retVal = null;

        try {

            InputStream is = httpPostRequest(CouchDB.getUrl() + apiName, create, userId);
            String result = getString(is);

            is.close();

            retVal = jObjectFromString(result);

        } catch (Exception e) {

            Logger.error(TAG + "postJsonObject", e);

            return null;

        }

        Log.e("Response: ", retVal.toString());
        return retVal;
    }
    
	/**
	 * Http POST
	 * 
	 * @param create
	 * @deprecated Because the exception is not used.
	 * @return
	 */
    @Deprecated public static JSONObject deprecatedPostJsonObject(JSONObject create, String userId,
			String token) {

		JSONObject retVal = null;

		try {

			InputStream is = httpPostRequest(CouchDB.getUrl(), create, userId);
			String result = getString(is);

			is.close();

			retVal = jObjectFromString(result);

		} catch (Exception e) {

			Logger.error(TAG + "postJsonObject", e);

			return null;

		}
		
		Log.e("Response: ", retVal.toString());
		return retVal;
	}
    
	/**
	 * Http POST
	 * 
	 * @param create
	 * @return
	 * @throws IOException 
	 * @throws JSONException 
	 */
    public static JSONObject postJsonObject(JSONObject create, String userId,
			String token) throws IOException, JSONException {

		JSONObject retVal = null;

		InputStream is = httpPostRequest(CouchDB.getUrl(), create, userId);
		String result = getString(is);

		is.close();

		retVal = jObjectFromString(result);

		Log.e("Response: ", retVal.toString());
		return retVal;
	}

	/**
	 * Http Auth POST
	 * 
	 * @param create
	 * @deprecated Because the exception is not used.
	 * @return
	 */
    @Deprecated public static JSONObject deprecatedPostAuth(JSONObject jPost) {

		JSONObject retVal = null;

		try {

			InputStream is = httpPostRequest(CouchDB.getAuthUrl(), jPost, "");
			String result = getString(is);

			is.close();

			retVal = jObjectFromString(result);

		} catch (Exception e) {

			Logger.error(TAG + "postAuth", e);

			return null;

		}

		Log.e("Response: ", retVal.toString());
		return retVal;
	}
    
	/**
	 * Http Auth POST
	 * 
	 * @param create
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject postAuth(JSONObject jPost) throws IOException,
			JSONException {

		JSONObject retVal = null;

		InputStream is = httpPostRequest(CouchDB.getAuthUrl(), jPost, "");
		String result = getString(is);

		is.close();

		retVal = jObjectFromString(result);

		return retVal;
	}

	/**
	 * Http DELETE
	 * 
	 * @param create
	 * @return
	 */
	public static JSONObject deleteJsonObject(String documentId,
			String documentRev, String userId, String token) {

		JSONObject retVal = null;

		try {

			InputStream is = httpDeleteRequest(CouchDB.getUrl() + documentId
					+ "?rev=" + documentRev, userId);
			String result = getString(is);

			is.close();

			retVal = jObjectFromString(result);
			
		} catch (Exception e) {
			
			Logger.error(TAG + "postJsonObject", e);

			return null;

		}

		return retVal;
	}

	/**
	 * Http PUT
	 * 
	 * @param create
	 * @param id
	 * @return
	 */
	public static JSONObject putJsonObject(JSONObject create, String id,
			String userId, String token) {

		JSONObject retVal = null;

		try {

			InputStream is = httpPutRequest(CouchDB.getUrl() + id, create,
					userId);
			String result = getString(is);

			is.close();

			retVal = jObjectFromString(result);

		} catch (Exception e) {
			e.printStackTrace();

			Logger.error(TAG + "putJsonObject", e);

			return null;

		}

		return retVal;
	}

	/**
	 * Get a Bitmap object from an URL
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapObject(String url, String userId, String token) {

		Bitmap mBitmap = null;

		try {
			File file = null;
			if (FileManagement.FileExists(url)) {

				file = FileManagement.GetFile(url);

				mBitmap = BitmapManagement.BitmapFromFile(file);

			} else {

				InputStream is = httpGetRequest(url, userId);

				file = FileManagement.CreateFile(url, is);

				is.close();

				mBitmap = BitmapManagement.BitmapFromFile(file);
			}
		} catch (Exception e) {
			Logger.error(TAG + "getBitmapObject", "Error retrieving picture: ",
					e);
		}

		return mBitmap;
	}

	/**
	 * Get a File object from an URL
	 * 
	 * @param url
	 * @param path
	 * @return
	 */
	public static void getFile(String url, File file, String userId,
			String token) {

		File mFile = file;

		try {

			//URL mUrl = new URL(url); // you can write here any link

			InputStream is = httpGetRequest(url, userId);
			BufferedInputStream bis = new BufferedInputStream(is);

			ByteArrayBuffer baf = new ByteArrayBuffer(20000);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(mFile);
			fos.write(baf.toByteArray());
			fos.flush();
			fos.close();
			is.close();

		} catch (Exception e) {
			Logger.error(TAG + "getFile", "Error retrieving file: ", e);
		}

	}

	/**
	 * Convert a string response to JSON object
	 * 
	 * @param result
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject jObjectFromString(String result)
			throws JSONException {
		return new JSONObject(result);
	}
	
	/**
	 * Convert a string response to JSON array
	 * 
	 * @param result
	 * @return
	 * @throws JSONException
	 */
	private static JSONArray jArrayFromString(String result)
			throws JSONException {
		return new JSONArray(result);
	}

	/**
	 * Get a String from InputStream
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private static String getString(InputStream is) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"UTF-8"), 8);
		StringBuilder builder = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null) {
			builder.append(line + "\n");
		}

		is.close();

		return builder.toString();
	}

	/**
	 * Forming a GET request
	 * 
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static InputStream httpGetRequest(String url, String userId) throws ClientProtocolException, IOException {

		HttpGet httpget = new HttpGet(url);

		httpget.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

		httpget.setHeader("Content-Type", "application/json");
		httpget.setHeader("Encoding", "utf-8");
	    httpget.setHeader("database", Const.DATABASE);

		if(userId != null && userId.length() > 0)
		    httpget.setHeader("user_id", userId);
		else{
		    String userIdSaved = SpikaApp.getPreferences().getUserId();
		    if(userIdSaved != null)
		        httpget.setHeader("user_id", userIdSaved);
		}
		
		String token = SpikaApp.getPreferences().getUserToken();
		
        if(token != null && token.length() > 0)
            httpget.setHeader("token", token);
		
		Logger.error("httpGetRequest", SpikaApp.getPreferences().getUserToken());

		print (httpget);
		
		HttpResponse response = HttpSingleton.getInstance().execute(httpget);
		HttpEntity entity = response.getEntity();

		return entity.getContent();
	}

	/**
	 * Forming a POST request
	 * 
	 * @param url
	 * @param create
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static InputStream httpPostRequest(String url, Object create,
			String userId) throws ClientProtocolException,
			IOException {

		HttpPost httppost = new HttpPost(url);

		httppost.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

		httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Encoding", "utf-8");
        httppost.setHeader("database", Const.DATABASE);

        if(userId != null && userId.length() > 0)
            httppost.setHeader("user_id", userId);
        else{
            String userIdSaved = SpikaApp.getPreferences().getUserId();
            if(userIdSaved != null)
                httppost.setHeader("user_id", userIdSaved);
        }
        
        String token = SpikaApp.getPreferences().getUserToken();
        if(token != null && token.length() > 0)
            httppost.setHeader("token", token);

        

		StringEntity stringEntity = new StringEntity(create.toString(),
				HTTP.UTF_8);

		httppost.setEntity(stringEntity);

		print (httppost);
		
		HttpResponse response = HttpSingleton.getInstance().execute(httppost);
		HttpEntity entity = response.getEntity();
		
		return entity.getContent();
	}

	/**
	 * Forming a PUT request
	 * 
	 * @param url
	 * @param create
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static InputStream httpPutRequest(String url, JSONObject create,
			String userId) throws ClientProtocolException,
			IOException {

		HttpPut httpput = new HttpPut(url);

		httpput.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

		httpput.setHeader("Content-Type", "application/json");
		httpput.setHeader("Encoding", "utf-8");
	    httpput.setHeader("database", Const.DATABASE);

        if(userId != null && userId.length() > 0)
            httpput.setHeader("user_id", userId);
        else{
            String userIdSaved = SpikaApp.getPreferences().getUserId();
            if(userIdSaved != null)
                httpput.setHeader("user_id", userIdSaved);
        }
        
        String token = SpikaApp.getPreferences().getUserToken();
        if(token != null && token.length() > 0)
            httpput.setHeader("token", token);
        

		StringEntity stringEntity = new StringEntity(create.toString(),
				HTTP.UTF_8);

		httpput.setEntity(stringEntity);

		print (httpput);
		
		HttpResponse response = HttpSingleton.getInstance().execute(httpput);
		HttpEntity entity = response.getEntity();

		return entity.getContent();
	}

	/**
	 * Form a DELETE reqest
	 * 
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static InputStream httpDeleteRequest(String url, String userId) throws ClientProtocolException, IOException {

		HttpDelete httpdelete = new HttpDelete(url);

		httpdelete.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

		httpdelete.setHeader("Content-Type", "application/json");
		httpdelete.setHeader("Encoding", "utf-8");
	    httpdelete.setHeader("database", Const.DATABASE);

        if(userId != null && userId.length() > 0)
            httpdelete.setHeader("user_id", userId);
        else{
            String userIdSaved = SpikaApp.getPreferences().getUserId();
            if(userIdSaved != null)
                httpdelete.setHeader("user_id", userIdSaved);
        }
        
        String token = SpikaApp.getPreferences().getUserToken();
        if(token != null && token.length() > 0)
            httpdelete.setHeader("token", token);
        
        print (httpdelete);
        
		HttpResponse response = HttpSingleton.getInstance().execute(httpdelete);
		HttpEntity entity = response.getEntity();
		
		return entity.getContent();
	}

	/**
	 * HttpClient mini singleton
	 */
	public static class HttpSingleton {
		private static HttpClient sInstance = null;

		protected HttpSingleton() {

		}

		public static HttpClient getInstance() {
			if (sInstance == null) {

				HttpParams params = new BasicHttpParams();
				params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
						HttpVersion.HTTP_1_1);

				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(new Scheme("http", PlainSocketFactory
						.getSocketFactory(), 80));
				final SSLSocketFactory sslSocketFactory = SSLSocketFactory
						.getSocketFactory();
				schemeRegistry.register(new Scheme("https", sslSocketFactory,
						443));
				ClientConnectionManager cm = new ThreadSafeClientConnManager(
						params, schemeRegistry);

				sInstance = new DefaultHttpClient(cm, params);
			}

			return sInstance;
		}
	}

	public static void print (HttpEntity entity) throws IOException
	{
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		entity.writeTo(outstream);
		String content = outstream.toString();
		Log.e("content", content);
	}
	
	public static void print (HttpEntityEnclosingRequestBase httpMethod) throws IOException
	{
		Log.e (httpMethod.getMethod(), httpMethod.getURI().toString());
		Log.e (httpMethod.getMethod(), httpMethod.getRequestLine().toString());
		
		print(httpMethod.getEntity());
		
		Header[] headers = httpMethod.getAllHeaders();
		for (Header header : headers) {
			Log.e("headers", header.toString());
		}
	}
	
	public static void print (HttpRequestBase httpMethod)
	{
		Log.e (httpMethod.getMethod(), httpMethod.getURI().toString());
		Log.e (httpMethod.getMethod(), httpMethod.getRequestLine().toString());
				
		Header[] headers = httpMethod.getAllHeaders();
		for (Header header : headers) {
			Log.e("headers", header.toString());
		}
	}
}
