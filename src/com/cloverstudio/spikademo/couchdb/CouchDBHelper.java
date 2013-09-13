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

package com.cloverstudio.spikademo.couchdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.cloverstudio.spikademo.SpikaApp;
import com.cloverstudio.spikademo.couchdb.model.ActivitySummary;
import com.cloverstudio.spikademo.couchdb.model.Attachment;
import com.cloverstudio.spikademo.couchdb.model.Comment;
import com.cloverstudio.spikademo.couchdb.model.Emoticon;
import com.cloverstudio.spikademo.couchdb.model.Group;
import com.cloverstudio.spikademo.couchdb.model.GroupCategory;
import com.cloverstudio.spikademo.couchdb.model.Message;
import com.cloverstudio.spikademo.couchdb.model.Notification;
import com.cloverstudio.spikademo.couchdb.model.NotificationMessage;
import com.cloverstudio.spikademo.couchdb.model.RecentActivity;
import com.cloverstudio.spikademo.couchdb.model.User;
import com.cloverstudio.spikademo.couchdb.model.UserGroup;
import com.cloverstudio.spikademo.extendables.SideBarActivity;
import com.cloverstudio.spikademo.management.UsersManagement;
import com.cloverstudio.spikademo.utils.Const;
import com.cloverstudio.spikademo.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * CouchDBHelper
 * 
 * Used for parsing JSON response from server.
 */
public class CouchDBHelper {

	private static String TAG = "CouchDbHelper: ";

	private static final Gson sGsonExpose = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation().create();

	/**
	 * Parse a single user JSON object
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static User parseSingleUserObject(JSONObject json)
			throws JSONException {
		User user = null;
		ArrayList<String> contactsIds = new ArrayList<String>();

		if (json != null) {
			
			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {

				JSONArray rows = json.getJSONArray(Const.ROWS);
				JSONObject row = rows.getJSONObject(0);
				JSONObject userJson = row.getJSONObject(Const.VALUE);

				user = sGsonExpose.fromJson(userJson.toString(), User.class);
				
				if (userJson.has(Const.FAVORITE_GROUPS)) {
					JSONArray favorite_groups = userJson
							.getJSONArray(Const.FAVORITE_GROUPS);

					List<String> groups = new ArrayList<String>();

					for (int i = 0; i < favorite_groups.length(); i++) {
						groups.add(favorite_groups.getString(i));
					}

					user.setGroupIds(groups);
				}

				if (userJson.has(Const.CONTACTS)) {
					JSONArray contacts = userJson.getJSONArray(Const.CONTACTS);

					for (int i = 0; i < contacts.length(); i++) {
						contactsIds.add(contacts.getString(i));
					}

					user.setContactIds(contactsIds);
				}
			} catch (JSONException e) {

			}

		}

		return user;
	}

	   /**
     * Parse a single user JSON object
     * 
     * @param json
     * @return
     * @throws JSONException
     */
    public static User parseSingleUserObjectWithoutRowParam(JSONObject userJson)
            throws JSONException {
        User user = null;
        ArrayList<String> contactsIds = new ArrayList<String>();

        if (userJson != null) {
        	
        	if (userJson.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(userJson));
				return null;
			}

            try {

                user = sGsonExpose.fromJson(userJson.toString(), User.class);
                
                if (userJson.has(Const.FAVORITE_GROUPS)) {
                    JSONArray favorite_groups = userJson
                            .getJSONArray(Const.FAVORITE_GROUPS);

                    List<String> groups = new ArrayList<String>();

                    for (int i = 0; i < favorite_groups.length(); i++) {
                        groups.add(favorite_groups.getString(i));
                    }

                    user.setGroupIds(groups);
                }

                if (userJson.has(Const.CONTACTS)) {
                    JSONArray contacts = userJson.getJSONArray(Const.CONTACTS);

                    for (int i = 0; i < contacts.length(); i++) {
                        contactsIds.add(contacts.getString(i));
                    }

                    user.setContactIds(contactsIds);
                }
            } catch (JSONException e) {

            }

        }

        return user;
    }
    
	/**
	 * Parse multi JSON objects of type user
	 * 
	 * @param json
	 * @return
	 */
	public static List<User> parseMultiUserObjects(JSONObject json) {

		List<User> users = null;
		ArrayList<String> contactsIds = new ArrayList<String>();

		if (json != null) {
			
			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				users = new ArrayList<User>();

				// Get the element that holds the users ( JSONArray )
				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);
					JSONObject userJson = row.getJSONObject(Const.VALUE);

					User user = new User();

					user = sGsonExpose
							.fromJson(userJson.toString(), User.class);

					if (userJson.has(Const.CONTACTS)) {

						JSONArray contacts = userJson
								.getJSONArray(Const.CONTACTS);

						for (int j = 0; j < contacts.length(); j++) {
							contactsIds.add(contacts.getString(j));
						}

						user.setContactIds(contactsIds);
					}

					if (userJson.has(Const.FAVORITE_GROUPS)) {
						JSONArray favorite_groups = userJson
								.getJSONArray(Const.FAVORITE_GROUPS);

						List<String> groups = new ArrayList<String>();

						for (int k = 0; k < favorite_groups.length(); k++) {
							groups.add(favorite_groups.getString(k));
						}

						user.setGroupIds(groups);
					}

					users.add(user);
				}
			} catch (Exception e) {
				Logger.error(
						TAG + "parseMultiUserObjects",
						"Error while retrieving data from json... Probably no users found!",
						e);
			}
		}

		return users;
	}

	/**
	 * Parse multi JSON objects of type user for search users
	 * 
	 * @param json
	 * @return
	 */
	public static List<User> parseSearchUsersResult(JSONArray jsonArray) {

		List<User> users = null;
		ArrayList<String> contactsIds = new ArrayList<String>();

		if (jsonArray != null) {

			try {
				users = new ArrayList<User>();

				// Get the element that holds the users ( JSONArray )

				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject userJson = jsonArray.getJSONObject(i);

					User user = new User();

					user = sGsonExpose
							.fromJson(userJson.toString(), User.class);


					if (userJson.has(Const.CONTACTS)) {

						JSONArray contacts = userJson
								.getJSONArray(Const.CONTACTS);

						for (int j = 0; j < contacts.length(); j++) {
							contactsIds.add(contacts.getString(j));
						}

						user.setContactIds(contactsIds);
					}

					if (userJson.has(Const.FAVORITE_GROUPS)) {
						JSONArray favorite_groups = userJson
								.getJSONArray(Const.FAVORITE_GROUPS);

						List<String> groups = new ArrayList<String>();

						for (int k = 0; k < favorite_groups.length(); k++) {
							groups.add(favorite_groups.getString(k));
						}

						user.setGroupIds(groups);
					}

					users.add(user);
				}
			} catch (Exception e) {
				Logger.error(
						TAG + "parseMultiUserObjects",
						"Error while retrieving data from json... Probably no users found!",
						e);
			}
		}

		return users;
	}

	/**
	 * Parse multi JSON objects of type group for search groups
	 * 
	 * @param json
	 * @return
	 */
	public static List<Group> parseSearchGroupsResult(JSONArray jsonArray) {

		List<Group> groups = null;

		if (jsonArray != null) {

			try {
				groups = new ArrayList<Group>();

				// Get the element that holds the groups ( JSONArray )

				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject groupJson = jsonArray.getJSONObject(i);

					Group group = new Group();

					group = sGsonExpose.fromJson(groupJson.toString(),
							Group.class);

					// if (groupJson.has(Const.ATTACHMENTS)) {
					//
					// List<Attachment> attachments = new
					// ArrayList<Attachment>();
					//
					// JSONObject json_attachments = groupJson
					// .getJSONObject(Const.ATTACHMENTS);
					//
					// @SuppressWarnings("unchecked")
					// Iterator<String> keys = json_attachments.keys();
					// while (keys.hasNext()) {
					// String key = keys.next();
					// try {
					//
					// JSONObject json_attachment = json_attachments
					// .getJSONObject(key);
					// Attachment attachment = sGsonExpose.fromJson(
					// json_attachment.toString(),
					// Attachment.class);
					// attachment.setName(key);
					// attachments.add(attachment);
					// } catch (Exception e) {
					// }
					// }
					// group.setAttachments(attachments);
					//
					// String url = null;
					// if (group.getAvatarName() != null) {
					// url = CouchDB.getUrl() + group.getId() + "/"
					// + group.getAvatarName();
					// } else {
					// url = CouchDB.getUrl() + group.getId() + "/"
					// + Const.GROUP_AVATAR;
					// }
					// group.setImageUrl(url);
					//
					// } else {
					// group.setImageUrl(null);
					// }

					groups.add(group);
				}
			} catch (Exception e) {
				Logger.error(
						TAG + "parseMultiUserObjects",
						"Error while retrieving data from json... Probably no users found!",
						e);
			}
		}

		return groups;
	}

	/**
	 * Parses a single activity summary JSON object
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public static ActivitySummary parseSingleActivitySummaryObject(
			JSONObject json) {

		ActivitySummary activitySummary = null;

		if (json != null) {
			
			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {

				JSONArray rows = json.getJSONArray(Const.ROWS);

				if (rows.length() > 0) {
					JSONObject row = rows.getJSONObject(0);
					JSONObject activitySummaryJson = row
							.getJSONObject(Const.VALUE);

					activitySummary = new ActivitySummary();
					activitySummary = sGsonExpose.fromJson(
							activitySummaryJson.toString(),
							ActivitySummary.class);

					if (activitySummaryJson.has(Const.RECENT_ACTIVITY)) {
						JSONObject recentActivityListJson = activitySummaryJson
								.getJSONObject(Const.RECENT_ACTIVITY);
						List<RecentActivity> recentActivityList = CouchDBHelper
								.parseMultiRecentActivityObjects(recentActivityListJson);
						activitySummary
								.setRecentActivityList(recentActivityList);
					}
				}
			} catch (Exception e) {
				Logger.error(TAG + "parseSingleActivitySummaryObject",
						"Error while retrieving data from json", e);
			}
		}

		return activitySummary;
	}

	/**
	 * Parses multi RecentActivity JSON Objects
	 * 
	 * @param recentActivityListJson
	 * @return
	 */
	public static List<RecentActivity> parseMultiRecentActivityObjects(
			JSONObject recentActivityListJson) {

		List<RecentActivity> recentActivityList = new ArrayList<RecentActivity>();

		@SuppressWarnings("unchecked")
		Iterator<String> iterator = recentActivityListJson.keys();
		while (iterator.hasNext()) {
			String key = iterator.next();
			try {
				JSONObject recentActivityJson = recentActivityListJson
						.getJSONObject(key);
				RecentActivity recentActivity = new RecentActivity();
				recentActivity = sGsonExpose.fromJson(
						recentActivityJson.toString(), RecentActivity.class);

				if (recentActivityJson.has(Const.NOTIFICATIONS)) {
					JSONObject notificationsJson = recentActivityJson
							.getJSONObject(Const.NOTIFICATIONS);
					recentActivity
							.set_notifications(parseMultiNotificationObjects(notificationsJson));
				}
				recentActivityList.add(recentActivity);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return recentActivityList;
	}

	/**
	 * Parses multi notification objects
	 * 
	 * @param notificationsJson
	 * @return
	 */
	public static List<Notification> parseMultiNotificationObjects(
			JSONObject notificationsJson) {

		List<Notification> notifications = new ArrayList<Notification>();

		@SuppressWarnings("unchecked")
		Iterator<String> iterator = notificationsJson.keys();
		while (iterator.hasNext()) {
			String key = iterator.next();
			try {
				JSONObject notificationJson = notificationsJson
						.getJSONObject(key);
				Notification notification = new Notification();
				notification = sGsonExpose.fromJson(
						notificationJson.toString(), Notification.class);

				if (notificationJson.has(Const.MESSAGES)) {
					JSONObject messagesJson = notificationJson
							.getJSONObject(Const.MESSAGES);
					notification
							.setMessages(parseMultiNotificationMessageObjects(
									messagesJson, notification.getTargetId()));
				}

				notifications.add(notification);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return notifications;
	}

	/**
	 * Parses multi notification message objects
	 * 
	 * @param messagesJson
	 * @return
	 */
	public static List<NotificationMessage> parseMultiNotificationMessageObjects(
			JSONObject messagesJson, String targetId) {

		List<NotificationMessage> messages = new ArrayList<NotificationMessage>();

		@SuppressWarnings("unchecked")
		Iterator<String> iterator = messagesJson.keys();
		while (iterator.hasNext()) {
			String key = iterator.next();
			try {
				JSONObject messageJson = messagesJson.getJSONObject(key);
				NotificationMessage notificationMessage = new NotificationMessage();
				notificationMessage = sGsonExpose.fromJson(
						messageJson.toString(), NotificationMessage.class);
				notificationMessage.setTargetId(targetId);
				notificationMessage.setUserAvatarFileId(CouchDB
						.findAvatarFileId(notificationMessage.getFromUserId()));
				messages.add(notificationMessage);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return messages;
	}

	/**
	 * Parse user JSON objects from get user contacts call
	 * 
	 * @param json
	 * @return
	 */
	public static List<User> parseUserContacts(JSONObject json) {

		List<User> users = null;

		if (json != null) {
			
			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				users = new ArrayList<User>();

				// Get the element that holds the users ( JSONArray )
				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);
					if (!row.isNull(Const.DOC)) {
						JSONObject userJson = row.getJSONObject(Const.DOC);

						User user = new User();

						user = sGsonExpose.fromJson(userJson.toString(),
								User.class);

						// if (userJson.has(Const.ATTACHMENTS)) {
						//
						// List<Attachment> attachments = new
						// ArrayList<Attachment>();
						//
						// JSONObject json_attachments = userJson
						// .getJSONObject(Const.ATTACHMENTS);
						//
						// @SuppressWarnings("unchecked")
						// Iterator<String> keys = json_attachments.keys();
						// while (keys.hasNext()) {
						// String key = keys.next();
						// try {
						//
						// JSONObject json_attachment = json_attachments
						// .getJSONObject(key);
						// Attachment attachment = sGsonExpose
						// .fromJson(
						// json_attachment.toString(),
						// Attachment.class);
						// attachment.setName(key);
						// attachments.add(attachment);
						// } catch (Exception e) {
						// }
						// }
						// user.setAttachments(attachments);
						//
						// String url = null;
						// if (user.getAvatarName() != null) {
						// url = CouchDB.getUrl() + user.getId() + "/"
						// + user.getAvatarName();
						// } else {
						// url = CouchDB.getUrl() + user.getId() + "/"
						// + Const.USER_AVATAR;
						// }
						// user.setImageUrl(url);
						//
						// } else {
						// user.setImageUrl(null);
						// }

						if (userJson.has(Const.FAVORITE_GROUPS)) {
							JSONArray favorite_groups = userJson
									.getJSONArray(Const.FAVORITE_GROUPS);

							List<String> groups = new ArrayList<String>();

							for (int z = 0; z < favorite_groups.length(); z++) {
								groups.add(favorite_groups.getString(z));
							}

							user.setGroupIds(groups);
						}

						if (userJson.has(Const.CONTACTS)) {
							JSONArray contacts = userJson
									.getJSONArray(Const.CONTACTS);

							List<String> contactsIds = new ArrayList<String>();

							for (int j = 0; j < contacts.length(); j++) {
								contactsIds.add(contacts.getString(j));
							}
							user.setContactIds(contactsIds);
						}

						users.add(user);
					}
				}
			} catch (Exception e) {
				Logger.error(
						TAG + "parseUserContacts",
						"Error while retrieving data from json... Probably no users found!",
						e);
			}
		}

		return users;
	}

	/**
	 * Parse comment JSON objects from get message comments
	 * 
	 * @param json
	 * @return
	 */
	public static List<Comment> parseMessageComments(JSONObject json) {

		List<Comment> comments = null;

		if (json != null) {
			
			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				comments = new ArrayList<Comment>();

				// Get the element that holds the users ( JSONArray )
				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);
					if (!row.isNull(Const.DOC)) {
						JSONObject commentJson = row.getJSONObject(Const.DOC);

						Comment comment = new Comment();
						comment = sGsonExpose.fromJson(commentJson.toString(),
								Comment.class);
						comments.add(comment);
					}
				}
			} catch (Exception e) {
				Logger.error(
						TAG + "parseMessageComments",
						"Error while retrieving data from json... Probably no comments found!",
						e);
			}
		}

		return comments;
	}

	/**
	 * Create user response object
	 * 
	 * @param json
	 * @return
	 */
	public static String createUser(JSONObject json) {

		boolean ok = false;
		String id = null;

		if (json != null) {
			
			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				ok = json.getBoolean(Const.OK);
				id = json.getString(Const.ID);
			} catch (Exception e) {
				Logger.error(TAG + "createUser",
						"Error while retrieving data from json", e);
			}
		}

		if (!ok) {
			Logger.error(TAG + "createUser", "error in creating user");
		}

		return id;
	}

	/**
	 * Update user response object, the Const.REV value is important in order to
	 * continue using the application
	 * 
	 * If you are updating contacts or favorites on of them should be null
	 * 
	 * @param json
	 * @return
	 */
	public static boolean updateUser(JSONObject json, List<String> contactsIds,
			List<String> groupsIds) {

		boolean ok = false;
		String rev = "";

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(false, true, isInvalidToken(json));
				return false;
			}

			try {
				ok = json.getBoolean(Const.OK);
				rev = json.getString(Const.REV);

				if (ok) {
					UsersManagement.getLoginUser().setRev(rev);

					if (null != contactsIds) {
						UsersManagement.getLoginUser().setContactIds(
								contactsIds);
					}

					if (null != groupsIds) {
						UsersManagement.getLoginUser().setGroupIds(groupsIds);
					}

					return true;
				}
			} catch (Exception e) {
				Logger.error(TAG + "updateUser",
						"Error while retrieving data from json", e);
			}
		}

		return false;
	}

	/**
	 * JSON response from creating a group
	 * 
	 * @param json
	 * @return
	 */
	public static String createGroup(JSONObject json) {

		boolean ok = false;
		String id = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				ok = json.getBoolean(Const.OK);
				id = json.getString(Const.ID);
			} catch (Exception e) {
				Logger.error(TAG + "createGroup",
						"Error while retrieving data from json", e);
			}
		}

		if (!ok) {
			Logger.error(TAG + "createGroup", "error in creating a group");
			return null;
		}

		return id;
	}

	/**
	 * JSON response from deleting a group
	 * 
	 * @param json
	 * @return
	 */
	public static boolean deleteGroup(JSONObject json) {

		boolean ok = false;

		if (json != null) {

		    if (json.has(Const.ERROR)) {
				appLogout(false, false, isInvalidToken(json));
				return false;
			}

			try {
				ok = json.getBoolean(Const.OK);
			} catch (Exception e) {
				Logger.error(TAG + "deleteGroup",
						"Error while retrieving data from json", e);
			}
		}

		return ok;
	}

	public static String findAvatarFileId(JSONObject json) {
		String avatarFileId = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {

				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);
					avatarFileId = row.getString(Const.VALUE);

				}

			} catch (Exception e) {
				Logger.error(TAG + "findAvatarFileId",
						"Error while retrieving data from json", e);
			}
		}

		return avatarFileId;
	}

	/**
	 * JSON response from deleting a user group
	 * 
	 * @param json
	 * @return
	 */
	public static boolean deleteUserGroup(JSONObject json) {

		boolean ok = false;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(false, false, isInvalidToken(json));
				return false;
			}

			try {
				ok = json.getBoolean(Const.OK);
			} catch (Exception e) {
				Logger.error(TAG + "deleteUserGroup",
						"Error while retrieving data from json", e);
			}
		}

		return ok;
	}

	/**
	 * JSON response from creating a user group
	 * 
	 * @param json
	 * @return
	 */
	public static String createUserGroup(JSONObject json) {

		boolean ok = false;
		String id = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				ok = json.getBoolean(Const.OK);
				id = json.getString(Const.ID);
			} catch (Exception e) {
				Logger.error(TAG + "createUserGroup",
						"Error while retrieving data from json", e);
			}
		}

		if (!ok) {
			Logger.error(TAG + "createUserGroup", "error in creating a group");
			return null;
		}

		return id;
	}

	/**
	 * JSON response from creating a comment
	 * 
	 * @param json
	 * @return
	 */
	public static String createComment(JSONObject json) {

		boolean ok = false;
		String id = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				ok = json.getBoolean(Const.OK);
				id = json.getString(Const.ID);
			} catch (Exception e) {
				Logger.error(TAG + "createComment",
						"Error while retrieving data from json", e);
			}
		}

		if (!ok) {
			Logger.error(TAG + "createComment", "error in creating comment");
			return null;
		}

		return id;
	}

	/**
	 * JSON response from updating a group you own
	 * 
	 * @param json
	 * @return
	 */
	public static boolean updateGroup(JSONObject json) {

		boolean ok = false;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(false, false, isInvalidToken(json));
				return false;
			}

			try {
				ok = json.getBoolean(Const.OK);

				/* Important */
				UsersManagement.getToGroup().setRev(json.getString(Const.REV));

			} catch (Exception e) {
				Logger.error(TAG + "updateGroup",
						"Error while retrieving data from json", e);
			}
		}

		if (!ok) {
			Logger.error(TAG + "updateGroup", "error in updating a group");
		}

		return ok;
	}

	/**
	 * Parse single JSON object of type Group
	 * 
	 * @param json
	 * @return
	 */
	public static Group parseSingleGroupObject(JSONObject json) {

		Group group = null;

		if (json != null) {
			
			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {

				JSONArray rows = json.getJSONArray(Const.ROWS);
				JSONObject row = rows.getJSONObject(0);

				JSONObject groupJson = row.getJSONObject(Const.VALUE);
				group = sGsonExpose.fromJson(groupJson.toString(), Group.class);

				// if (groupJson.has(Const.ATTACHMENTS)) {
				// List<Attachment> attachments = new ArrayList<Attachment>();
				//
				// JSONObject json_attachments = groupJson
				// .getJSONObject(Const.ATTACHMENTS);
				//
				// @SuppressWarnings("unchecked")
				// Iterator<String> keys = json_attachments.keys();
				// while (keys.hasNext()) {
				// String key = keys.next();
				// try {
				//
				// JSONObject json_attachment = json_attachments
				// .getJSONObject(key);
				// Attachment attachment = sGsonExpose.fromJson(
				// json_attachment.toString(),
				// Attachment.class);
				// attachment.setName(key);
				// attachments.add(attachment);
				// } catch (Exception e) {
				// }
				// }
				// group.setAttachments(attachments);
				//
				// String url = null;
				// if (group.getAvatarName() != null) {
				// url = CouchDB.getUrl() + group.getId() + "/"
				// + group.getAvatarName();
				// } else {
				// url = CouchDB.getUrl() + group.getId() + "/"
				// + Const.GROUP_AVATAR;
				// }
				// group.setImageUrl(url);
				// } else {
				// group.setImageUrl(null);
				// }

			} catch (Exception e) {
				Logger.error(TAG + "parseSingleGroupObject",
						"Error while retrieving data from json", e);
			}
		}

		return group;
	}

	   /**
     * Parse single JSON object of type Group
     * 
     * @param json
     * @return
     */
    public static Group parseSingleGroupObjectWithoutRowParam(JSONObject json) {

        Group group = null;

        if (json != null) {
        	
        	if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

            try {

                group = sGsonExpose.fromJson(json.toString(), Group.class);

            } catch (Exception e) {
                Logger.error(TAG + "parseSingleGroupObject",
                        "Error while retrieving data from json", e);
            }
        }

        return group;
    }
    
	/**
	 * Parse multi JSON objects of type Group
	 * 
	 * @param json
	 * @return
	 */
	public static List<Group> parseMultiGroupObjects(JSONObject json) {

		List<Group> groups = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				groups = new ArrayList<Group>();

				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);
					String key = row.getString(Const.KEY);

					if (!key.equals(Const.NULL)) {

						JSONObject groupJson = row.getJSONObject(Const.VALUE);

						Group group = sGsonExpose.fromJson(
								groupJson.toString(), Group.class);

						// if (groupJson.has(Const.ATTACHMENTS)) {
						// List<Attachment> attachments = new
						// ArrayList<Attachment>();
						//
						// JSONObject json_attachments = groupJson
						// .getJSONObject(Const.ATTACHMENTS);
						//
						// @SuppressWarnings("unchecked")
						// Iterator<String> keys = json_attachments.keys();
						// while (keys.hasNext()) {
						// String attachmentKey = keys.next();
						// try {
						//
						// JSONObject json_attachment = json_attachments
						// .getJSONObject(attachmentKey);
						// Attachment attachment = sGsonExpose
						// .fromJson(
						// json_attachment.toString(),
						// Attachment.class);
						// attachment.setName(attachmentKey);
						// attachments.add(attachment);
						// } catch (Exception e) {
						// }
						// }
						// group.setAttachments(attachments);
						//
						// String url = null;
						// if (group.getAvatarName() != null) {
						// url = CouchDB.getUrl() + group.getId() + "/"
						// + group.getAvatarName();
						// } else {
						// url = CouchDB.getUrl() + group.getId() + "/"
						// + Const.GROUP_AVATAR;
						// }
						// group.setImageUrl(url);
						//
						// } else {
						// group.setImageUrl(null);
						// }

						groups.add(group);
					}
				}
			} catch (Exception e) {
				Logger.error(TAG + "parseMultiGroupObjects",
						"Error while retrieving data from json", e);
			}
		}

		return groups;
	}

	/**
	 * Parse favorite groups JSON objects
	 * 
	 * @param json
	 * @return
	 */
	public static List<Group> parseFavoriteGroups(JSONObject json) {

		List<Group> groups = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				groups = new ArrayList<Group>();

				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);

					try {
						JSONObject groupJson = row.getJSONObject(Const.DOC);

						String type = groupJson.getString(Const.TYPE);
						if (!type.equals(Const.GROUP)) {
							continue;
						}

						Group group = sGsonExpose.fromJson(
								groupJson.toString(), Group.class);

						groups.add(group);
					} catch (Exception e) {
					}
				}
			} catch (Exception e) {
				Logger.error(TAG + "parseFavoriteGroups", e);
				e.printStackTrace();
			}
		}

		return groups;
	}

	/**
	 * Parse multi JSON objects of type UserGroup
	 * 
	 * @param json
	 * @return
	 */
	public static List<UserGroup> parseMultiUserGroupObjects(JSONObject json) {

		List<UserGroup> usersGroup = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				usersGroup = new ArrayList<UserGroup>();

				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);
					String key = row.getString(Const.KEY);

					if (!key.equals(Const.NULL)) {

						JSONObject userGroupJson = row
								.getJSONObject(Const.VALUE);

						UserGroup userGroup = sGsonExpose.fromJson(
								userGroupJson.toString(), UserGroup.class);
						usersGroup.add(userGroup);
					}
				}
			} catch (Exception e) {
				Logger.error(TAG + "parseMultiUserGroupObjects",
						"Error while retrieving data from json", e);
			}
		}

		return usersGroup;
	}
	
	/**
	 * Parse multi JSON objects of type GroupCategory
	 * 
	 * @param json
	 * @return
	 */
	public static List<GroupCategory> parseMultiGroupCategoryObjects(JSONObject json) {
		List<GroupCategory> groupCategories = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				groupCategories = new ArrayList<GroupCategory>();

				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);
					String key = row.getString(Const.KEY);

					if (!key.equals(Const.NULL)) {

						JSONObject groupCategoryJson = row.getJSONObject(Const.VALUE);

						GroupCategory groupCategory = sGsonExpose.fromJson(
								groupCategoryJson.toString(), GroupCategory.class);

						if (groupCategoryJson.has(Const.ATTACHMENTS)) {
							List<Attachment> attachments = new ArrayList<Attachment>();

							JSONObject json_attachments = groupCategoryJson
									.getJSONObject(Const.ATTACHMENTS);

							@SuppressWarnings("unchecked")
							Iterator<String> keys = json_attachments.keys();
							while (keys.hasNext()) {
								String attachmentKey = keys.next();
								try {

									JSONObject json_attachment = json_attachments
											.getJSONObject(attachmentKey);
									Attachment attachment = sGsonExpose
											.fromJson(
													json_attachment.toString(),
													Attachment.class);
									attachment.setName(attachmentKey);
									attachments.add(attachment);
								} catch (Exception e) {
								}
							}
							groupCategory.setAttachments(attachments);

							String imageUrl = CouchDB.getUrl() + groupCategory.getId() + "/"
										+ Const.GROUP_CATEGORY_AVATAR;
							groupCategory.setImageUrl(imageUrl);
						} else {
							groupCategory.setImageUrl(null);
						}

						groupCategories.add(groupCategory);
					}
				}
			} catch (Exception e) {
				Logger.error(TAG + "parseMultiGroupObjects",
						"Error while retrieving data from json", e);
			}
		}

		return groupCategories;
	}

	/**
	 * 
	 * @param json
	 * @return
	 */
	public static int getCommentCount(JSONObject json) {

		int count = 0;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return 0;
			}

			try {

				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);
					count = row.getInt(Const.VALUE);

				}
			} catch (Exception e) {
				Logger.error(TAG + "getCommentCount",
						"Error while retrieving data from json", e);
			}
		}

		return count;
	}

	/**
	 * Find a single Message object
	 * 
	 * @param json
	 * @return
	 */
	public static Message findMessage(JSONObject json) {
		
		if (json.has(Const.ERROR)) {
			appLogout(null, false, isInvalidToken(json));
			return null;
		}
		
		return parseMessageObject(json, false, false, false);
	}

	/**
	 * Find all messages for current user
	 * 
	 * @param json
	 * @return
	 */
	public static ArrayList<Message> findMessagesForUser(JSONObject json) {
		ArrayList<Message> messages = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				messages = new ArrayList<Message>();

				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);
					JSONObject msgJson = row.getJSONObject(Const.VALUE);

					Message message = null;

					try {

						String messageType = msgJson
								.getString(Const.MESSAGE_TYPE);

						if (messageType.equals(Const.TEXT)) {

							message = new Gson().fromJson(msgJson.toString(),
									Message.class);

						} else if (messageType.equals(Const.IMAGE)) {

							message = parseMessageObject(msgJson, true, false,
									false);

						} else if (messageType.equals(Const.VOICE)) {

							message = parseMessageObject(msgJson, false, true,
									false);

						} else if (messageType.equals(Const.VIDEO)) {

							message = parseMessageObject(msgJson, false, false,
									true);
						}
						else if (messageType.equals(Const.EMOTICON)) {

							message = parseMessageObject(msgJson, false, false,
									false);
						} else {

							message = new Gson().fromJson(msgJson.toString(),
									Message.class);

						}

					} catch (Exception e) {
						continue;
					}

					if (null == message) {
						continue;
					} else {
					    
					    String avatarFileId = CouchDB.getFromMemCache(message.getFromUserId());
					    
					    if(avatarFileId == null){
	                        avatarFileId = CouchDB.findAvatarFileId(message.getFromUserId());
	                        CouchDB.saveToMemCache(message.getFromUserId(),avatarFileId);
					    }else{
					        Log.d("test",avatarFileId);
					    }

						message.setUserAvatarFileId(avatarFileId);
						messages.add(message);
					}

				}

			} catch (Exception e) {
				Logger.error(TAG + "findMessagesForUser",
						"Error while retrieving data from json", e);
			}
		}

		if (null != messages) {
			Collections.sort(messages);
		}

		return messages;
	}

	/**
	 * Parse a single JSON object of Message type
	 * 
	 * @param json
	 * @param image
	 * @param voice
	 * @return
	 */
	private static Message parseMessageObject(JSONObject json, boolean image,
			boolean voice, boolean video) {

		Message message = new Message();

		if (json == null) {
			return message;
		}

		if (json.has(Const.ERROR)) {
			appLogout(null, false, isInvalidToken(json));
			return null;
		}

		try {
			message.setId(json.getString(Const._ID));
		} catch (JSONException e) {
			message.setId("");
		}

		try {
			message.setRev(json.getString(Const._REV));
		} catch (JSONException e) {
			message.setRev("");
		}

		try {
			message.setType(json.getString(Const.TYPE));
		} catch (JSONException e) {
			message.setType("");
		}

		try {
			message.setMessageType(json.getString(Const.MESSAGE_TYPE));
		} catch (JSONException e) {
			message.setMessageType("");
		}

		try {
			message.setMessageTargetType(json
					.getString(Const.MESSAGE_TARGET_TYPE));
		} catch (JSONException e) {
			message.setMessageTargetType("");
		}

		try {
			message.setBody(json.getString(Const.BODY));
		} catch (JSONException e) {
			message.setBody("");
		}

		try {
			message.setFromUserId(json.getString(Const.FROM_USER_ID));
		} catch (JSONException e) {
			message.setFromUserId("");
		}

		try {
			message.setFromUserName(json.getString(Const.FROM_USER_NAME));
		} catch (JSONException e) {
			message.setFromUserName("");
		}

		try {
			message.setToUserId(json.getString(Const.TO_USER_ID));
		} catch (JSONException e) {
			message.setToUserId("");
		}

		try {
			message.setToGroupName(json.getString(Const.TO_USER_NAME));
		} catch (JSONException e) {
			message.setToGroupName("");
		}

		try {
			message.setToGroupId(json.getString(Const.TO_GROUP_ID));
		} catch (JSONException e) {
			message.setToGroupId("");
		}

		try {
			message.setToGroupName(json.getString(Const.TO_GROUP_NAME));
		} catch (JSONException e) {
			message.setToGroupName("");
		}

		try {
			message.setCreated(json.getLong(Const.CREATED));
		} catch (JSONException e) {
			return null;
		}

		try {
			message.setModified(json.getLong(Const.MODIFIED));
		} catch (JSONException e) {
			return null;
		}

		try {
			message.setValid(json.getBoolean(Const.VALID));
		} catch (JSONException e) {
			message.setValid(true);
		}

		try {
			message.setAttachments(json.getJSONObject(Const.ATTACHMENTS)
					.toString());
		} catch (JSONException e) {
			message.setAttachments("");
		}

		try {
			message.setLatitude(json.getString(Const.LATITUDE));
		} catch (JSONException e) {
			message.setLatitude("");
		}

		try {
			message.setLongitude(json.getString(Const.LONGITUDE));
		} catch (JSONException e) {
			message.setLongitude("");
		}

		try {
			message.setImageFileId((json.getString(Const.PICTURE_FILE_ID)));
		} catch (JSONException e) {
			message.setImageFileId("");
		}

       try {
                message.setImageThumbFileId((json.getString(Const.PICTURE_THUMB_FILE_ID)));
        } catch (JSONException e) {
                message.setImageThumbFileId("");
        }
	      
		try {
			message.setVideoFileId((json.getString(Const.VIDEO_FILE_ID)));
		} catch (JSONException e) {
			message.setVideoFileId("");
		}

		try {
			message.setVoiceFileId((json.getString(Const.VOICE_FILE_ID)));
		} catch (JSONException e) {
			message.setVoiceFileId("");
		}

		try {
			message.setEmoticonImageUrl(json
					.getString(Const.EMOTICON_IMAGE_URL));
		} catch (JSONException e) {
			message.setEmoticonImageUrl("");
		}

		if (image || video || voice) {
			message.setCommentCount(CouchDB.getCommentCount(message.getId()));
		}

		return message;
	}

	/**
	 * Parse comments Json
	 * 
	 * @param json
	 * @return
	 */
	public static List<Comment> parseCommentsJson(JSONObject json) {

		List<Comment> comments = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				comments = new ArrayList<Comment>();

				JSONArray rows = json.getJSONArray(Const.COMMENTS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject commentJson = rows.getJSONObject(i);

					Comment comment = sGsonExpose.fromJson(
							commentJson.toString(), Comment.class);

					comments.add(comment);

				}
			} catch (Exception e) {
				Logger.error(TAG + "parseCommentsJson",
						"Error in parsing JSON data", e);
			}
		}

		return comments;
	}

	/**
	 * Parse multi comment objects
	 * 
	 * @param json
	 * @return
	 */
	public static List<Comment> parseMultiCommentObjects(JSONObject json) {

		List<Comment> comments = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				comments = new ArrayList<Comment>();

				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);

					String key = row.getString(Const.KEY);

					if (!"null".equals(key)) {

						JSONObject commentJson = row.getJSONObject(Const.VALUE);

						Comment comment = sGsonExpose.fromJson(
								commentJson.toString(), Comment.class);

						comments.add(comment);
					}

				}
			} catch (Exception e) {
				Logger.error(TAG + "parseMultiComments",
						"Error in parsing JSON data", e);
			}
		}

		return comments;
	}

	/**
	 * Parse multi emoticon objects
	 * 
	 * @param json
	 * @return
	 */
	public static List<Emoticon> parseMultiEmoticonObjects(JSONObject json) {

		List<Emoticon> emoticons = null;

		if (json != null) {
			
			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				emoticons = new ArrayList<Emoticon>();

				JSONArray rows = json.getJSONArray(Const.ROWS);

				for (int i = 0; i < rows.length(); i++) {

					JSONObject row = rows.getJSONObject(i);

					String key = row.getString(Const.KEY);

					if (!"null".equals(key)) {

						JSONObject emoticonJson = row
								.getJSONObject(Const.VALUE);

						Emoticon emoticon = sGsonExpose.fromJson(
								emoticonJson.toString(), Emoticon.class);

						emoticons.add(emoticon);

//						SpikaApp.getFileDir().saveFile(
//								emoticon.getIdentifier(),
//								emoticon.getImageUrl());
					}
				}
			} catch (Exception e) {
				Logger.error(TAG + "parseMultiEmoticons",
						"Error i parsing JSON data", e);
			}
		}

		return emoticons;
	}
	
	/**
	 * JSON response from creating a watching group log
	 * 
	 * @param json
	 * @return
	 */
	public static String createWatchingGroupLog(JSONObject json) {

		boolean ok = false;
		String id = null;
		String rev = null;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(null, false, isInvalidToken(json));
				return null;
			}

			try {
				ok = json.getBoolean(Const.OK);
				id = json.getString(Const.ID);
				rev = json.getString(Const.REV);
				
				SpikaApp.getPreferences().setWatchingGroupId(id);
				SpikaApp.getPreferences().setWatchingGroupRev(rev);
			} catch (Exception e) {
				Logger.error(TAG + "createWatchingGroupLog",
						"Error while retrieving data from json", e);
			}
		}

		if (!ok) {
			Logger.error(TAG + "createWatchingGroupLog", "error in creating a watching group log");
			return null;
		}

		return id;
	}
	
	/**
	 * JSON response from deleting a watching group log
	 * 
	 * @param json
	 * @return
	 */
	public static boolean deleteWatchingGroupLog(JSONObject json) {

		boolean ok = false;

		if (json != null) {

			if (json.has(Const.ERROR)) {
				appLogout(false, false, isInvalidToken(json));
				return false;
			}

			try {
				ok = json.getBoolean(Const.OK);
				
			} catch (Exception e) {
				Logger.error(TAG + "deleteWatchingGroupLog",
						"Error while retrieving data from json", e);
			}
		}

		return ok;
	}
	
	private static boolean isInvalidToken(JSONObject json) {
		if (json.has(Const.MESSAGE)) {
			try {
				String errorMessage = json.getString(Const.MESSAGE);
				if (errorMessage.equalsIgnoreCase(Const.INVALID_TOKEN)) {
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static Object appLogout(Object object, boolean isUserUpdateConflict, boolean isInvalidToken) {
		SideBarActivity.appLogout(isUserUpdateConflict, true, isInvalidToken);
		return object;
	}
	

}
