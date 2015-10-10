package com.paperfly.instantjio.groups;

import java.util.Map;

public class Group {
    String name;
    Map<String, Boolean> members;

    public Group() {
    }

    public Group(String name, Map<String, Boolean> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }
}
