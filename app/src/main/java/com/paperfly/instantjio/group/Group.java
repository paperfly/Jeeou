package com.paperfly.instantjio.group;

import java.util.HashMap;
import java.util.Map;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    String name;
    String leader;
    Map<String, Boolean> members;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(String name, String leader, Map<String, Boolean> members) {
        this.name = name;
        this.leader = leader;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
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
