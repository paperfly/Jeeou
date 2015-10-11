package com.paperfly.instantjio.groups;

import java.util.HashMap;
import java.util.Map;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    String leader;
    String name;
    Map<String, Boolean> members;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(String leader, String name, Map<String, Boolean> members) {
        this.leader = leader;
        this.name = name;
        this.members = members;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
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
