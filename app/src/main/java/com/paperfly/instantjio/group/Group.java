package com.paperfly.instantjio.group;

import java.util.HashMap;
import java.util.Map;

public class Group {
    private String name;
    private String leader;
    private Map<String, Boolean> members;

    public Group() {
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

    public void addMember(String memberName) {
        if (members == null)
            members = new HashMap<>();

        members.put(memberName, true);
    }

    public void removeMember(String memberName) {
        if (members != null) {
            members.remove(memberName);
        }
    }
}
