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

package com.cloverstudio.spikademo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.extendables.SpikaAsync;
import com.cloverstudio.spikademo.extendables.SpikaFragmentActivity;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.GPSTracker;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.messageshandling.FindAvatarFileIdAsync;
import com.cloverstudio.spikademo.messageshandling.SendMessageAsync;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.LayoutHelper;
import com.cloverstudio.spikademo.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * LocationActivity
 * 
 * Shows user current location or other user's previous sent location.
 */

@SuppressLint("DefaultLocale")
public class LocationActivity extends SpikaFragmentActivity {

	private GoogleMap mMap;
	private Button mBtnBack;
	private Button mBtnSend;
	private GPSTracker mGpsTracker;
	private String mAddressText;

	private String mTypeOfLocation;

	private double mLatitude;
	private double mLongitude;
	private Bitmap mMapPinBlue;

	private Bundle mExtras;

	MarkerOptions markerOfUser;
	private EditText mEtNameOfUserLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_location);

		mExtras = getIntent().getExtras();

		mTypeOfLocation = mExtras.getString(Const.LOCATION);
		mLatitude = mExtras.getDouble(Const.LATITUDE);
		mLongitude = mExtras.getDouble(Const.LONGITUDE);

		initialization();
        setGps();

		if (mTypeOfLocation.equals("userLocation")) {
			setLocation(mLatitude, mLongitude);
			setAvatarAndName(false);
		} else {
			setAvatarAndName(true);
		}

		setOnClickListener();

	}

	private void initialization() {
		mBtnBack = (Button) findViewById(R.id.btnBack);
		mBtnSend = (Button) findViewById(R.id.btnSend);
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		if (mTypeOfLocation.equals("userLocation")) {
			mBtnSend.setVisibility(View.INVISIBLE);
		}
		mMapPinBlue = BitmapFactory.decodeResource(getResources(),
				R.drawable.location_more_icon_active);

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.LOCATION));
		
		mEtNameOfUserLocation = (EditText) findViewById(R.id.etNameOfUserLocation);
	}

	private void setGps() {
		mGpsTracker = new GPSTracker(this);
		if (mGpsTracker.canGetLocation()) {
			mLatitude = mGpsTracker.getLatitude();
			mLongitude = mGpsTracker.getLongitude();

			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					mLatitude, mLongitude), 16));

			final Marker myMarker=mMap.addMarker(new MarkerOptions().position(
					new LatLng(mLatitude, mLongitude)).icon(
					BitmapDescriptorFactory.fromBitmap(mMapPinBlue)));
			
//			mMap.addMarker(
//					new MarkerOptions().position(
//							new LatLng(mLatitude, mLongitude)).icon(
//							BitmapDescriptorFactory.fromBitmap(mMapPinBlue)))
//					.setTitle("title");

			mMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {
					mLatitude = point.latitude;
					mLongitude = point.longitude;
					mMap.clear();
					mMap.addMarker(new MarkerOptions().position(point).icon(
							BitmapDescriptorFactory.fromBitmap(mMapPinBlue)));
				}
			});
			
			mGpsTracker.setOnLocationChangedListener(new OnLocationChangedListener() {
				
				@Override
				public void onLocationChanged(Location location) {
					mLatitude=location.getLatitude();
					mLongitude=location.getLongitude();
					myMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
				}
			});

		} else {
			mGpsTracker.showSettingsAlert();
		}
	}

	private void setLocation(double lat, double lon) {
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon),
				16));

		markerOfUser = new MarkerOptions().position(new LatLng(lat, lon)).icon(
				BitmapDescriptorFactory.fromBitmap(mMapPinBlue));

		mMap.addMarker(markerOfUser);

		new GetAdressNameAsync(LocationActivity.this).execute(lat, lon);

		mGpsTracker = new GPSTracker(this);
		if (mGpsTracker.canGetLocation()) {
			double myLat = mGpsTracker.getLatitude();
			double myLon = mGpsTracker.getLongitude();

			mMap.addMarker(new MarkerOptions().position(
					new LatLng(myLat, myLon)).icon(
					BitmapDescriptorFactory
							.fromResource(R.drawable.location_more_icon)));

		}
	}

	private void setOnClickListener() {
		mBtnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// try to get address, and city, if failed then location sent
				// (just latitude and longitude)
				new GetCityAsync(LocationActivity.this).execute();
			}
		});
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	private void setAvatarAndName(boolean isMe) {
		ImageView ivAvatar = (ImageView) findViewById(R.id.ivAvatarLocation);
		LayoutHelper.scaleWidthAndHeightRelativeLayout(this, 5f, ivAvatar);

		TextView tvNameOfUser = (TextView) findViewById(R.id.tvNameOfUserLocation);
		
		String avatarId = null;

		if (isMe) {
			avatarId = UsersManagement.getLoginUser().getAvatarFileId();
			tvNameOfUser.setText(UsersManagement.getLoginUser().getName()
					.toUpperCase()
					+ "'S LOCATION");

		} else {

			String idOfUser = mExtras.getString("idOfUser");
			String nameOfUser = mExtras.getString("nameOfUser");
			
			try {
				avatarId = new FindAvatarFileIdAsync(this).execute(idOfUser).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			tvNameOfUser.setText(nameOfUser.toUpperCase() + "'S LOCATION");
		}
		
		Utils.displayImage(avatarId,
					ivAvatar, ImageLoader.SMALL, R.drawable.user_stub, false);

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mGpsTracker.stopUsingGPS();
	}

	class GetAdressNameAsync extends SpikaAsync<Double, Void, Void> {

		protected GetAdressNameAsync(Context context) {
			super(context);
		}

		private boolean mLoaded = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Double... params) {
			Geocoder geocoder = new Geocoder(LocationActivity.this,
					Locale.getDefault());
			List<Address> addresses = null;
			try {
				// Call the synchronous getFromLocation() method by passing in
				// the lat/long values.
				addresses = geocoder.getFromLocation(params[0], params[1], 1);
			} catch (IOException e) {
				Log.e("LOG", e.toString());
				// Update UI field with the exception.
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and
				// country name.
				mAddressText="";
				if(address.getMaxAddressLineIndex()>0){
					mAddressText=mAddressText+address.getAddressLine(0);
				}
				if(address.getLocality()!=null){
					mAddressText=mAddressText+", "+address.getLocality();
				}
				if(address.getCountryName()!=null){
					mAddressText=mAddressText+", "+address.getCountryName();
				}
//				mAddressText = String.format(
//						"%s, %s, %s",
//						address.getMaxAddressLineIndex() > 0 ? address
//								.getAddressLine(0) : "", address.getLocality(),
//						address.getCountryName());
				// Update the UI via a message handler
				mLoaded = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!mLoaded) {
				mAddressText = "Getting adress failed.";
			} else {
				markerOfUser.title(mAddressText);
				mMap.addMarker(markerOfUser);
			}

			super.onPostExecute(result);
		}

	}

	class GetCityAsync extends SpikaAsync<Void, Void, Void> {

		protected GetCityAsync(Context context) {
			super(context);
		}

		private boolean mLoaded = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			Geocoder geocoder = new Geocoder(LocationActivity.this,
					Locale.getDefault());
			List<Address> addresses = null;
			try {
				// Call the synchronous getFromLocation() method by passing in
				// the lat/long values.
				addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
			} catch (IOException e) {
				Log.e("LOG", e.toString());
				// Update UI field with the exception.
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and
				// country name.
				mAddressText="";
				if(address.getMaxAddressLineIndex()>0){
					mAddressText=mAddressText+address.getAddressLine(0);
				}
				if(address.getLocality()!=null){
					mAddressText=mAddressText+", "+address.getLocality();
				}
				if(address.getCountryName()!=null){
					mAddressText=mAddressText+", "+address.getCountryName();
				}
//				mAddressText = String.format(
//						"%s, %s, %s",
//						address.getMaxAddressLineIndex() > 0 ? address
//								.getAddressLine(0) : "", address.getLocality(),
//						address.getCountryName());
				// Update the UI via a message handler
				mLoaded = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!mLoaded) {
				mAddressText = "";
			}
			if (mLatitude != 0 && mLongitude != 0) {
				mLoaded = true;
			} else {
				mLoaded = false;
			}
//			String locationSubject = mEtNameOfUserLocation.getText().toString();
//			if (locationSubject.equals(null) || locationSubject.equals("")) {
//				locationSubject = "";
//			}
			
			if (mLoaded == true) {
				new SendMessageAsync(getApplicationContext(),
						SendMessageAsync.TYPE_LOCATION).execute(mAddressText, false, false, Double.toString(mLatitude),
						Double.toString(mLongitude));
				finish();
			} else {
				Toast.makeText(LocationActivity.this,
						"Getting location failed", Toast.LENGTH_SHORT).show();
			}

			super.onPostExecute(result);
		}

	}

}
