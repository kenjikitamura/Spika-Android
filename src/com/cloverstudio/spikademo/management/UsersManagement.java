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

import com.cloverstudio.spikademo.couchdb.model.Group;
import com.cloverstudio.spikademo.couchdb.model.User;

/**
 * UsersManagement
 * 
 * Holds reference to login user, user/group which wall is currently opened by login user.
 */

public class UsersManagement {

	private static UsersManagement sUsersManagementInstance;

	private static User sFromUser;
    private static User sToUser;
    private static User supportUser;
	private static Group sToGroup;

	public UsersManagement() {
		setUsersManagement(this);
	}

	public static boolean isTheSameUser() {
		boolean retVal = false;

		if (sToUser != null && sFromUser != null) {
			if (sToUser.getId().equals(sFromUser.getId())) {
				retVal = true;
			}
		}
		return retVal;
	}

	public static User getFromUser() {
		return sFromUser;
	}

	public static void setFromUser(User fromUser) {
		UsersManagement.sFromUser = fromUser;
	}

	public static User getToUser() {
		return sToUser;
	}

	public static void setToUser(User toUser) {
		UsersManagement.sToUser = toUser;
	}

    public static User getLoginUser() {
        return sFromUser;
    }

    public static void setLoginUser(User loginUser) {
        UsersManagement.sFromUser = loginUser;
    }

    public static User getSupportUser() {
        return UsersManagement.supportUser;
    }

    public static void setSupportUser(User loginUser) {
        UsersManagement.supportUser = loginUser;
    }

	public static Group getToGroup() {
		return sToGroup;
	}

	public static void setToGroup(Group toGroup) {
		UsersManagement.sToGroup = toGroup;
	}

	public static UsersManagement getUsersManagement() {
		return sUsersManagementInstance;
	}

	public static void setUsersManagement(UsersManagement usersManagement) {
		UsersManagement.sUsersManagementInstance = usersManagement;
	}
}
