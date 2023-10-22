package com.example.smartirrigation;

public class PhoneNumber {
    String id;
    String phoneNum;

    public PhoneNumber() {
        // Required default constructor for Firebase
    }

    public PhoneNumber(String id, String phoneNum) {
        this.id = id;
        this.phoneNum = phoneNum;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }
}
