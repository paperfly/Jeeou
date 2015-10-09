package com.paperfly.instantjio.groups;

import java.util.Map;

public class User {
    String name;
    String phoneNumber;
    Map<String, Boolean> friends;
    String provider;

    public User() {
    }

    public User(String name, String phoneNumber, Map<String, Boolean> friends, String provider) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.friends = friends;
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Map<String, Boolean> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Boolean> friends) {
        this.friends = friends;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
