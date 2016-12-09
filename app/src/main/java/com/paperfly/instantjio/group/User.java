package com.paperfly.instantjio.group;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonIgnoreProperties({"templates", "newEvents", "newGroups", "oldEvents"})
@IgnoreExtraProperties
public class User {
    String name;
    String email;
    Map<String, Boolean> friends;
    Map<String, Boolean> groups;
    Map<String, Boolean> events;
    Map<String, String> providers;
    String regionCode;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Boolean> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Boolean> friends) {
        this.friends = friends;
    }

    public void addFriend(String friendName) {
        if (friends == null)
            friends = new HashMap<>();

        friends.put(friendName, true);
    }

    public Map<String, Boolean> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Boolean> groups) {
        this.groups = groups;
    }

    public void addGroup(String groupName) {
        if (groups == null)
            groups = new HashMap<>();

        groups.put(groupName, true);
    }

    public Map<String, Boolean> getEvents() {
        return events;
    }

    public void setEvents(Map<String, Boolean> events) {
        this.events = events;
    }

    public Map<String, String> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, String> providers) {
        this.providers = providers;
    }

    public void addProviders(String providerName, String id) {
        if (providers == null)
            providers = new HashMap<>();

        providers.put(providerName, id);
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
}
