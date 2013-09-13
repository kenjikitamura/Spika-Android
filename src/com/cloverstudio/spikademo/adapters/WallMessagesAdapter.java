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

package com.cloverstudio.spikademo.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloverstudio.spikademo.R;
import com.cloverstudio.spikademo.SpikaApp;
import com.cloverstudio.spikademo.LocationActivity;
import com.cloverstudio.spikademo.MyProfileActivity;
import com.cloverstudio.spikademo.PhotoActivity;
import com.cloverstudio.spikademo.UserProfileActivity;
import com.cloverstudio.spikademo.VideoActivity;
import com.cloverstudio.spikademo.VoiceActivity;
import com.cloverstudio.spikademo.couchdb.model.Message;
import com.cloverstudio.spikademo.lazy.Emoticons;
import com.cloverstudio.spikademo.lazy.ImageLoader;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.LayoutHelper;
import com.cloverstudio.spikademo.utils.Logger;
import com.cloverstudio.spikademo.utils.Utils;

/**
 * WallMessagesAdapter
 * 
 * Adapter class for wall messages.
 */

public class WallMessagesAdapter extends BaseAdapter {

	private Activity mActivity;
	private ArrayList<Message> mMessages;

	public WallMessagesAdapter(Activity activity, ArrayList<Message> messages) {
		this.mActivity = activity;
		this.mMessages = messages;
	}

	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Message getItem(int arg0) {
		return mMessages.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		ViewHolder holder = null;

		try {

			if (v == null) {
				holder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater) mActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.message, null);

                holder.liNews = (LinearLayout) v
                        .findViewById(R.id.liNewsLayout);
                
                holder.tvNewsText = (TextView) v
                        .findViewById(R.id.newsText);
                
                holder.openWebButton = (Button) v
                        .findViewById(R.id.btnOpenBrowser);
                
				holder.rlFromMe = (RelativeLayout) v
						.findViewById(R.id.rlFromMeLayout);
				holder.rlFromMe.setVisibility(View.GONE);
				holder.tvMessageBodyFromMe = (TextView) v
						.findViewById(R.id.messageTextFromMe);
				holder.tvMessageBodyFromMe.setTypeface(SpikaApp
						.getTfMyriadPro());
				holder.ivPhotoFromMe = (ImageView) v
						.findViewById(R.id.ivMessagePhotoFromMe);
				holder.btnAvatarMe = (ImageButton) v
						.findViewById(R.id.btnAvatarMe);
				holder.pbLoadingAvatarMe = (ProgressBar) v
						.findViewById(R.id.pbLoadingAvatarMe);
				holder.tvMessageSubTextFromMe = (TextView) v
						.findViewById(R.id.messageSubTextFromMe);
				holder.tvMessageSubTextFromMe.setTypeface(SpikaApp
						.getTfMyriadPro());
				holder.pbPhotoFromMe = (ProgressBar) v
						.findViewById(R.id.pbPhotoFromMe);
				holder.rlImageFromMe = (RelativeLayout) v
						.findViewById(R.id.rlImageFromMe);
				holder.rlMyPhotoComments = (RelativeLayout) v
						.findViewById(R.id.rlMyPhotoComments);

				holder.tvMyPhotoComments = (TextView) v
						.findViewById(R.id.tvMyPhotoComments);
				holder.ivForLocationOrVoiceFromMe = (ImageView) v
						.findViewById(R.id.ivForLocationOrVoiceFromMe);
				holder.pbLoadingForImageFromMe = (ProgressBar) v
						.findViewById(R.id.pbLoadingForImageFromMe);
				LayoutHelper.scaleWidthAndHeightRelativeLayout(mActivity, 2.4f,
						holder.ivPhotoFromMe);
				LayoutHelper.scaleWidthAndHeightRelativeLayout(mActivity, 5f,
						holder.btnAvatarMe);

				holder.rlToMe = (RelativeLayout) v
						.findViewById(R.id.rlToMeLayout);
				holder.rlToMe.setVisibility(View.GONE);
				holder.tvMessageBodyToMe = (TextView) v
						.findViewById(R.id.messageTextToMe);
				holder.tvMessageBodyToMe
						.setTypeface(SpikaApp.getTfMyriadPro());
				holder.ivPhotoToMe = (ImageView) v
						.findViewById(R.id.ivMessagePhotoToMe);
				holder.btnAvatarToMe = (ImageButton) v
						.findViewById(R.id.btnAvatarToMe);
				holder.pbLoadingAvatarToMe = (ProgressBar) v
						.findViewById(R.id.pbLoadingAvatarToMe);
				holder.tvMessageSubTextToMe = (TextView) v
						.findViewById(R.id.messageSubTextToMe);
				holder.tvMessageSubTextToMe.setTypeface(SpikaApp
						.getTfMyriadPro());
				holder.rlImageToMe = (RelativeLayout) v
						.findViewById(R.id.rlImageToMe);
				holder.pbPhotoToMe = (ProgressBar) v
						.findViewById(R.id.pbPhotoToMe);
				holder.rlPhotoComments = (RelativeLayout) v
						.findViewById(R.id.rlPhotoComments);
				holder.rlPhotoComments.setVisibility(View.GONE);
				holder.tvPhotoComments = (TextView) v
						.findViewById(R.id.tvPhotoComments);
				holder.ivForLocationOrVoiceToMe = (ImageView) v
						.findViewById(R.id.ivForLocationOrVoiceToMe);
				holder.pbLoadingForImageToMe = (ProgressBar) v
						.findViewById(R.id.pbLoadingForImageToMe);
				LayoutHelper.scaleWidthAndHeightRelativeLayout(mActivity, 2.4f,
						holder.ivPhotoToMe);
				LayoutHelper.scaleWidthAndHeightRelativeLayout(mActivity, 5f,
						holder.btnAvatarToMe);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.liNews.setVisibility(View.GONE);
			holder.rlFromMe.setVisibility(View.GONE);
			holder.rlToMe.setVisibility(View.GONE);
			holder.rlMyPhotoComments.setVisibility(View.GONE);
			holder.rlPhotoComments.setVisibility(View.GONE);

			holder.ivPhotoToMe.setImageBitmap(null);
			holder.ivPhotoFromMe.setImageBitmap(null);
			
			Message message = mMessages.get(position);

			if (message.getMessageType().equals(Const.NEWS)) {
			    
			    showNewsMessage(message,holder);
			    
			}else{
		         
	            boolean isMessageFromMe = message.getFromUserId().equals(
	                    UsersManagement.getLoginUser().getId());
	            if (isMessageFromMe) {
	                showMessageFromMe(message, holder);
	            } else {
	                showMessageToMe(message, holder);
	            }

			}

		} catch (Exception e) {

			Logger.error("WallMessagesAdapter",
					"Error on inflating wall messages!");
		}

		return v;
	}

	class ViewHolder {

        public LinearLayout liNews;
        public TextView tvNewsText;
        public Button openWebButton;

        public RelativeLayout rlFromMe;
		public TextView tvMessageBodyFromMe;
		public ImageView ivPhotoFromMe;
		public ImageButton btnAvatarMe;
		public ProgressBar pbLoadingAvatarMe;
		public TextView tvMessageSubTextFromMe;
		public ProgressBar pbPhotoFromMe;
		public RelativeLayout rlImageFromMe;
		public RelativeLayout rlMyPhotoComments;
		public TextView tvMyPhotoComments;
		public ImageView ivForLocationOrVoiceFromMe;
		public ProgressBar pbLoadingForImageFromMe;

		public RelativeLayout rlToMe;
		public TextView tvMessageBodyToMe;
		public ImageView ivPhotoToMe;
		public ImageButton btnAvatarToMe;
		public ProgressBar pbLoadingAvatarToMe;
		public TextView tvMessageSubTextToMe;
		public RelativeLayout rlImageToMe;
		public ProgressBar pbPhotoToMe;
		public RelativeLayout rlPhotoComments;
		public TextView tvPhotoComments;
		public ImageView ivForLocationOrVoiceToMe;
		public ProgressBar pbLoadingForImageToMe;

	}

	private String setSubText(Message message) {
		String subText = null;

		long timeOfCreationOrUpdate = message.getCreated();
		if (message.getCreated() < message.getModified()) {
			timeOfCreationOrUpdate = message.getModified();
		}

		long diff = System.currentTimeMillis()
				- (Long.valueOf(timeOfCreationOrUpdate) * 1000);

		// long diffs = diff / (1000);
		long diffm = diff / (1000 * 60);
		long diffh = diff / (1000 * 60 * 60);
		long diffd = diff / (1000 * 60 * 60 * 24);
		if (diffh > 24 && diffh < 48) {
			subText = mActivity.getString(R.string.posted) + " " + diffd + " "
					+ mActivity.getString(R.string.day_ago) + " "
					+ message.getFromUserName();
		} else if (diffh >= 48) {
			subText = mActivity.getString(R.string.posted) + " " + diffd + " "
					+ mActivity.getString(R.string.days_ago) + " "
					+ message.getFromUserName();
		} else if (diffm > 60 && diffm < 120) {
			subText = mActivity.getString(R.string.posted) + " " + diffh + " "
					+ mActivity.getString(R.string.hour_ago) + " "
					+ message.getFromUserName();
		} else if (diffm >= 120) {
			subText = mActivity.getString(R.string.posted) + " " + diffh + " "
					+ mActivity.getString(R.string.hours_ago) + " "
					+ message.getFromUserName();
		} else if (diffm < 60 && diffm > 1) {
			subText = mActivity.getString(R.string.posted) + " " + diffm + " "
					+ mActivity.getString(R.string.minutes_ago) + " "
					+ message.getFromUserName();
		} else if (diffm == 1) {
			subText = mActivity.getString(R.string.posted) + " " + diffm + " "
					+ mActivity.getString(R.string.minute_ago) + " "
					+ message.getFromUserName();
		} else {
			subText = mActivity
					.getString(R.string.posted_less_than_a_minute_ago)
					+ " "
					+ message.getFromUserName();
		}

		return subText;
	}

	private OnClickListener getUserImageListener(final String userId) {

		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent profileIntent = null;
				if (userId.equals(UsersManagement.getLoginUser().getId())) {
					profileIntent = new Intent(mActivity,
							MyProfileActivity.class);
				} else {
					profileIntent = new Intent(mActivity,
							UserProfileActivity.class);
					profileIntent.putExtra("user_id", userId);
				}
				mActivity.startActivity(profileIntent);
			}
		};
	}

	private OnClickListener getPhotoListener(final Message m) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, PhotoActivity.class);
				intent.putExtra("message", m);
				mActivity.startActivity(intent);
			}
		};
	}

	
    private void showNewsMessage(final Message m, ViewHolder holder) {
        
        holder.liNews.setVisibility(View.VISIBLE);
        holder.tvNewsText.setText(m.getBody());
        
        if(m.getMessageUrl() != null && m.getMessageUrl().length() > 0){
            
            holder.openWebButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getMessageUrl()));
                    mActivity.startActivity(browserIntent);
                    
                }
                
            });
            
        }else{
            holder.openWebButton.setVisibility(View.GONE);
        }
    }
    
    private void showMessageFromMe(final Message m, ViewHolder holder) {

		holder.rlFromMe.setVisibility(View.VISIBLE);
		holder.ivPhotoFromMe.setVisibility(View.VISIBLE);
		holder.tvMessageBodyFromMe.setVisibility(View.GONE);
		holder.ivForLocationOrVoiceFromMe.setVisibility(View.GONE);
		holder.rlImageFromMe.setVisibility(View.GONE);

		String avatarId = UsersManagement.getLoginUser().getAvatarThumbFileId();

		Utils.displayImage(avatarId, holder.btnAvatarMe,
				holder.pbLoadingAvatarMe, ImageLoader.SMALL,
				R.drawable.user_stub, false);

		holder.btnAvatarMe
				.setOnClickListener(getUserImageListener(UsersManagement
						.getLoginUser().getId()));

		if (m.getMessageType().equals(Const.TEXT)
				|| m.getMessageType().equals(Const.LOCATION)) {

			holder.tvMessageBodyFromMe.setVisibility(View.VISIBLE);
			holder.tvMessageBodyFromMe.setText(m.getBody());

			holder.ivPhotoFromMe.setVisibility(View.GONE);

			if (m.getMessageType().equals(Const.LOCATION)) {
				holder.ivForLocationOrVoiceFromMe.setVisibility(View.VISIBLE);
				holder.ivForLocationOrVoiceFromMe
						.setImageResource(R.drawable.location_more_icon);
					if (m.getBody().equals("")) {
						holder.tvMessageBodyFromMe.setText(m.getFromUserName()
								+ " sent Location");
					} else {
						holder.tvMessageBodyFromMe.setText(m.getFromUserName()
								+ " sent Location\n" + '\"' + m.getBody() + '\"');
					}
			} else {
				holder.ivForLocationOrVoiceFromMe.setVisibility(View.GONE);
			}

			holder.rlFromMe.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (m.getMessageType().equals(Const.LOCATION)) {
						Intent intent = new Intent(mActivity,
								LocationActivity.class);
						intent.putExtra(Const.LOCATION, "userLocation");
						intent.putExtra(Const.LATITUDE,
								Double.parseDouble(m.getLatitude()));
						intent.putExtra(Const.LONGITUDE,
								Double.parseDouble(m.getLongitude()));
						intent.putExtra("idOfUser", UsersManagement
								.getLoginUser().getId());
						intent.putExtra("nameOfUser", UsersManagement
								.getLoginUser().getName());
						mActivity.startActivity(intent);
					}
				}
			});

		} else if (m.getMessageType().equals(Const.IMAGE)) {
			
			holder.rlImageFromMe.setVisibility(View.VISIBLE);
			holder.rlFromMe.setClickable(false);
			holder.ivPhotoFromMe.setOnClickListener(getPhotoListener(m));

			Utils.displayImage(m.getImageFileId(), holder.ivPhotoFromMe,
					holder.pbLoadingForImageFromMe, ImageLoader.SMALL,
					R.drawable.image_stub, false);

			holder.tvMessageBodyFromMe.setVisibility(View.GONE);

			if (m.getCommentCount() > 0) {
				holder.rlMyPhotoComments.setVisibility(View.VISIBLE);
				holder.tvMyPhotoComments.setText(String.valueOf(m
						.getCommentCount()));
			} else {
				holder.rlMyPhotoComments.setVisibility(View.INVISIBLE);
			}

		} else if (m.getMessageType().equals(Const.EMOTICON)) {

			holder.rlImageFromMe.setVisibility(View.VISIBLE);
			holder.ivPhotoFromMe.setBackgroundColor(Color.TRANSPARENT);
			
			Utils.displayImage(m.getEmoticonImageUrl(), holder.ivPhotoFromMe,
					holder.pbLoadingForImageFromMe, ImageLoader.SMALL,
					R.drawable.image_stub, true);
			
//			holder.ivPhotoFromMe.setImageBitmap(Emoticons.getInstance()
//					.getItem(m.getBody()).getBitmap());
//			holder.pbLoadingForImageFromMe.setVisibility(View.GONE);

			holder.ivPhotoFromMe.setOnClickListener(null);
			holder.tvMessageBodyFromMe.setVisibility(View.GONE);
			holder.rlFromMe.setOnClickListener(null);

		} else if (m.getMessageType().equals(Const.VOICE)
				|| m.getMessageType().equals(Const.VIDEO)) {

			if (m.getMessageType().equals(Const.VIDEO)) {
				if (m.getBody().equals("")) {
					holder.tvMessageBodyFromMe.setText(m.getFromUserName()
							+ " sent Video");
				} else {
					holder.tvMessageBodyFromMe.setText(m.getFromUserName()
							+ " sent Video\n" + '\"' + m.getBody() + '\"');
				}
			} else {
				if (m.getBody().equals("")) {
					holder.tvMessageBodyFromMe.setText(m.getFromUserName()
							+ " sent Voice");
				} else {
					holder.tvMessageBodyFromMe.setText(m.getFromUserName()
							+ " sent Voice\n" + '\"' + m.getBody() + '\"');
				}
			}

			holder.tvMessageBodyFromMe.setVisibility(View.VISIBLE);

			holder.ivPhotoFromMe.setVisibility(View.GONE);
			holder.ivForLocationOrVoiceFromMe.setVisibility(View.VISIBLE);
			if (m.getMessageType().equals(Const.VIDEO)) {
				holder.ivForLocationOrVoiceFromMe
						.setImageResource(R.drawable.video_more_icon);
			} else {
				holder.ivForLocationOrVoiceFromMe
						.setImageResource(R.drawable.mic_voice_icon);
			}

			if (m.getCommentCount() > 0) {
				holder.rlMyPhotoComments.setVisibility(View.VISIBLE);
				holder.tvMyPhotoComments.setText(String.valueOf(m
						.getCommentCount()));
			} else {
				holder.rlMyPhotoComments.setVisibility(View.INVISIBLE);
			}

			holder.rlFromMe.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (m.getMessageType().equals(Const.VOICE)) {

						Intent intent = new Intent(mActivity,
								VoiceActivity.class);
						intent.putExtra("idOfUser", UsersManagement
								.getLoginUser().getId());
						intent.putExtra("nameOfUser", UsersManagement
								.getLoginUser().getName());
						intent.putExtra("message", m);

						mActivity.startActivity(intent);

					} else if (m.getMessageType().equals(Const.VIDEO)) {

						Intent intent = new Intent(mActivity,
								VideoActivity.class);
						intent.putExtra("videoFromUser", false);
						intent.putExtra("idOfUser", UsersManagement
								.getLoginUser().getId());
						intent.putExtra("nameOfUser", UsersManagement
								.getLoginUser().getName());
						intent.putExtra("message", m);

						mActivity.startActivity(intent);

					}
				}
			});

		} else {
			holder.tvMessageBodyFromMe.setVisibility(View.VISIBLE);
			holder.tvMessageBodyFromMe.setText(m.getBody());
		}

		holder.tvMessageSubTextFromMe.setText(setSubText(m));
	}

	private void showMessageToMe(final Message m, ViewHolder holder) {
		holder.rlToMe.setVisibility(View.VISIBLE);
		holder.ivPhotoToMe.setVisibility(View.VISIBLE);
		holder.ivForLocationOrVoiceToMe.setVisibility(View.GONE);
		holder.rlImageToMe.setVisibility(View.GONE);
		holder.tvMessageBodyToMe.setVisibility(View.GONE);
		
		
		Utils.displayImage(m.getUserAvatarFileId(), holder.btnAvatarToMe,
					holder.pbLoadingAvatarToMe, ImageLoader.SMALL,
					R.drawable.user_stub, false);

		holder.btnAvatarToMe.setOnClickListener(getUserImageListener(m
				.getFromUserId()));

		if (m.getMessageType().equals(Const.TEXT)
				|| m.getMessageType().equals(Const.LOCATION)) {

			holder.tvMessageBodyToMe.setVisibility(View.VISIBLE);
			holder.tvMessageBodyToMe.setText(m.getBody());
			holder.ivPhotoToMe.setVisibility(View.GONE);

			if (m.getMessageType().equals(Const.LOCATION)) {
				holder.ivForLocationOrVoiceToMe.setVisibility(View.VISIBLE);
				holder.ivForLocationOrVoiceToMe
						.setImageResource(R.drawable.location_more_icon);
				if (m.getBody().equals("")) {
					holder.tvMessageBodyToMe.setText(m.getFromUserName()
							+ " sent Location");
				} else {
					holder.tvMessageBodyToMe.setText(m.getFromUserName()
							+ " sent Location\n" + '\"' + m.getBody() + '\"');
				}
			} else {
				holder.ivForLocationOrVoiceToMe.setVisibility(View.GONE);
			}

			holder.rlToMe.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (m.getMessageType().equals(Const.LOCATION)) {
						Intent intent = new Intent(mActivity,
								LocationActivity.class);
						intent.putExtra(Const.LOCATION, "userLocation");
						intent.putExtra(Const.LATITUDE,
								Double.parseDouble(m.getLatitude()));
						intent.putExtra(Const.LONGITUDE,
								Double.parseDouble(m.getLongitude()));
						intent.putExtra("idOfUser", m.getFromUserId());
						intent.putExtra("nameOfUser", m.getFromUserName());
						mActivity.startActivity(intent);
					}
				}
			});
		} else if (m.getMessageType().equals(Const.IMAGE)) {

			holder.rlImageToMe.setVisibility(View.VISIBLE);
			holder.rlToMe.setClickable(false);
			holder.ivPhotoToMe.setOnClickListener(getPhotoListener(m));

			Utils.displayImage(m.getImageThumbFileId(), holder.ivPhotoToMe,
					holder.pbLoadingForImageToMe, ImageLoader.LARGE,
					R.drawable.image_stub, false);

			holder.tvMessageBodyToMe.setVisibility(View.GONE);

			if (m.getCommentCount() > 0) {
				holder.rlPhotoComments.setVisibility(View.VISIBLE);
				holder.tvPhotoComments.setText(String.valueOf(m
						.getCommentCount()));
			} else {
				holder.rlPhotoComments.setVisibility(View.INVISIBLE);
			}

		} else if (m.getMessageType().equals(Const.EMOTICON)) {

			holder.rlImageToMe.setVisibility(View.VISIBLE);
			holder.ivPhotoToMe.setBackgroundColor(Color.TRANSPARENT);
			
			Utils.displayImage(m.getEmoticonImageUrl(), holder.ivPhotoToMe,
					holder.pbLoadingForImageToMe, ImageLoader.SMALL,
					R.drawable.image_stub, true);
			
//			holder.ivPhotoToMe.setImageBitmap(Emoticons.getInstance()
//					.getItem(m.getBody()).getBitmap());
//			holder.pbLoadingForImageToMe.setVisibility(View.GONE);

			holder.tvMessageBodyToMe.setVisibility(View.GONE);
			holder.ivPhotoToMe.setOnClickListener(null);
			holder.rlToMe.setOnClickListener(null);

		} else if (m.getMessageType().equals(Const.VOICE)
				|| m.getMessageType().equals(Const.VIDEO)) {

			if (m.getMessageType().equals(Const.VIDEO)) {
				if (m.getBody().equals("")) {
					holder.tvMessageBodyToMe.setText(m.getFromUserName()
							+ " sent Video");
				} else {
					holder.tvMessageBodyToMe.setText(m.getFromUserName()
							+ " sent Video\n" + '\"' + m.getBody() + '\"');
				}
			} else {
				if (m.getBody().equals("")) {
					holder.tvMessageBodyToMe.setText(m.getFromUserName()
							+ " sent Voice");
				} else {
					holder.tvMessageBodyToMe.setText(m.getFromUserName()
							+ " sent Voice\n" + '\"' + m.getBody() + '\"');
				}
			}

			holder.tvMessageBodyToMe.setVisibility(View.VISIBLE);

			holder.ivForLocationOrVoiceToMe.setVisibility(View.VISIBLE);

			if (m.getMessageType().equals(Const.VIDEO)) {
				holder.ivForLocationOrVoiceToMe
						.setImageResource(R.drawable.video_more_icon);
			} else {
				holder.ivForLocationOrVoiceToMe
						.setImageResource(R.drawable.mic_voice_icon);
			}

			holder.ivPhotoToMe.setVisibility(View.GONE);

			if (m.getCommentCount() > 0) {
				holder.rlPhotoComments.setVisibility(View.VISIBLE);
				holder.tvPhotoComments.setText(String.valueOf(m
						.getCommentCount()));
			} else {
				holder.rlPhotoComments.setVisibility(View.INVISIBLE);
			}

			holder.rlToMe.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (m.getMessageType().equals(Const.VOICE)) {

						Intent intent = new Intent(mActivity,
								VoiceActivity.class);
						intent.putExtra("idOfUser", m.getFromUserId());
						intent.putExtra("nameOfUser", m.getFromUserName());
						intent.putExtra("message", m);

						mActivity.startActivity(intent);

					} else if (m.getMessageType().equals(Const.VIDEO)) {

						Intent intent = new Intent(mActivity,
								VideoActivity.class);
						intent.putExtra("videoFromUser", true);
						intent.putExtra("idOfUser", m.getFromUserId());
						intent.putExtra("nameOfUser", m.getFromUserName());
						intent.putExtra("message", m);

						mActivity.startActivity(intent);

					}
				}
			});

		} else {
			holder.tvMessageBodyToMe.setVisibility(View.VISIBLE);
			holder.tvMessageBodyToMe.setText(m.getBody());
		}

		holder.tvMessageSubTextToMe.setText(setSubText(m));
	}

}
