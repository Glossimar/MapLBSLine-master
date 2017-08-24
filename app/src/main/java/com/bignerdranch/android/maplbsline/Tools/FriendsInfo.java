package com.bignerdranch.android.maplbsline.Tools;

/**
 * Created by glossimar on 2017/8/20.
 */

public class FriendsInfo {
    private String name;
    private String phoneNumber;

    public FriendsInfo(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {return phoneNumber;}

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
