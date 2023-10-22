package com.example.smartirrigation;

public class PhoneNumber {
    String id;
    String name;
    String phoneNum;
    public PhoneNumber() {
        // Required default constructor for Firebase
    }

    public PhoneNumber(String id, String name, String phoneNum) {
        this.id = id;
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }
}

