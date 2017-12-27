package com.example.junmp.teamproject;

/**
 * Created by junmp on 2017-11-29.
 */

public class Friend {
    private String userId;
    private String userName;

    public Friend(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
