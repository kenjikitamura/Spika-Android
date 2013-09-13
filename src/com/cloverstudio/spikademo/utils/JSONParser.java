package com.cloverstudio.spikademo.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {

		// Making HTTP request
		try {
			// defaultHttpClient
			HttpParams httpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, "UTF-8");

			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// BufferedReader reader = new BufferedReader(new
			// InputStreamReader(is, "iso-8859-1"), 8);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, Charset.forName("UTF-8")), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "n");
			}
			is.close();
			json = sb.toString();

			Log.e("JSON", json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}

	public String getIdFromFileUploader(String url,
			List<NameValuePair> params) {
		// Making HTTP request
		try {
			// defaultHttpClient
			HttpParams httpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpPost httpPost = new HttpPost(url);

			httpPost.setHeader("database", Const.DATABASE);
		     
			Charset charSet = Charset.forName("UTF-8"); // Setting up the
														// encoding

			try {
				MultipartEntity entity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				for (int index = 0; index < params.size(); index++) {
					if (params.get(index).getName()
							.equalsIgnoreCase(Const.FILE)) {
						// If the key equals to "file", we use FileBody to
						// transfer the data
						entity.addPart(params.get(index).getName(),
								new FileBody(new File(params.get(index)
										.getValue())));
					} else {
						// Normal string data
						entity.addPart(params.get(index).getName(),
								new StringBody(params.get(index).getValue(),
										charSet));
					}
				}

				httpPost.setEntity(entity);
			} catch (IOException e) {
				e.printStackTrace();
			}

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// BufferedReader reader = new BufferedReader(new
			// InputStreamReader(is, "iso-8859-1"), 8);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, Charset.forName("UTF-8")), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			json = sb.toString();

			Log.e("RESPONSE", json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}
		
		return json;

	}
	
	
	public JSONObject fileUpload(String urlString,
			String filePath) {
		
		HttpURLConnection conn = null;
		String lineEnd = "\r\n";
	    String twoHyphens = "--";
	    String boundary = "*****";
		try {
	        // ------------------ CLIENT REQUEST


	        FileInputStream fileInputStream = new FileInputStream(new File(
	        		filePath));

	        // open a URL connection to the Servlet

	        URL url = new URL(urlString);

	        // Open a HTTP connection to the URL

	        conn = (HttpURLConnection) url.openConnection();

	        // Allow Inputs
	        conn.setDoInput(true);

	        // Allow Outputs
	        conn.setDoOutput(true);

	        // Don't use a cached copy.
	        conn.setUseCaches(false);

	        // Use a post method.
	        conn.setRequestMethod("POST");

	        conn.setRequestProperty("Connection", "Keep-Alive");

	        conn.setRequestProperty("Content-Type",
	                "multipart/form-data;boundary=" + boundary);

	        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

	        dos.writeBytes(twoHyphens + boundary + lineEnd);
	        dos
	                .writeBytes("Content-Disposition: post-data; file=uploadedfile;filename="
	                        + filePath + "" + lineEnd);
	        dos.writeBytes(lineEnd);


	        // create a buffer of maximum size

	        int bytesAvailable = fileInputStream.available();
	        int maxBufferSize = 1000;
	        // int bufferSize = Math.min(bytesAvailable, maxBufferSize);
	        byte[] buffer = new byte[bytesAvailable];

	        // read file and write it into form...

	        int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);

	        while (bytesRead > 0) {
	            dos.write(buffer, 0, bytesAvailable);
	            bytesAvailable = fileInputStream.available();
	            bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
	            bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
	        }

	        // send multipart form data necessary after file data...

	        dos.writeBytes(lineEnd);
	        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

	        fileInputStream.close();
	        dos.flush();
	        dos.close();

	    } catch (MalformedURLException ex) {
	    }

	    catch (IOException ioe) {
	    }


		try {
			// BufferedReader reader = new BufferedReader(new
			// InputStreamReader(is, "iso-8859-1"), 8);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn
	                .getInputStream(), Charset.forName("UTF-8")), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "n");
			}
			is.close();
			json = sb.toString();

			Log.e("JSON", json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}

}
