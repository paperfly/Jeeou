package com.paperfly.instantjio.groups;

import java.util.HashMap;
import java.util.Map;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    String name;
    Map<String, Boolean> members;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(String name, Map<String, Boolean> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    public void addMembers(String memberName) {
        if (members == null)
            members = new HashMap<>();

        members.put(memberName, true);
    }
}
