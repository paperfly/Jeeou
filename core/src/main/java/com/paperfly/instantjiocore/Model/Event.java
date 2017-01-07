package com.paperfly.instantjiocore.Model;

import java.util.HashMap;
import java.util.Map;

public class Event {
    // Event & Template
    private String title;
    private String description;
    private String location;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private Map<String, Boolean> invited;
    // Event only
    private String host;
    private String type;
    private Map<String, Boolean> attending;
    private Map<String, Boolean> notAttending;

    public Event() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Map<String, Boolean> getInvited() {
        return invited;
    }

    public void setInvited(Map<String, Boolean> invited) {
        this.invited = invited;
    }

    public void addInvited(String invited) {
        if (this.invited == null) {
            this.invited = new HashMap<>();
        }

        this.invited.put(invited, true);
    }

    public void removeInvited(String invited) {
        if (this.invited != null) {
            this.invited.remove(invited);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Boolean> getAttending() {
        return attending;
    }

    public void setAttending(Map<String, Boolean> attending) {
        this.attending = attending;
    }

    public void addAttending(String attending) {
        if (this.attending == null) {
            this.attending = new HashMap<>();
        }

        this.attending.put(attending, true);
    }

    public void removeAttending(String attending) {
        if (this.attending != null) {
            this.attending.remove(attending);
        }
    }

    public Map<String, Boolean> getNotAttending() {
        return notAttending;
    }

    public void setNotAttending(Map<String, Boolean> notAttending) {
        this.notAttending = notAttending;
    }

    public void addNotAttending(String notAttending) {
        if (this.notAttending == null) {
            this.notAttending = new HashMap<>();
        }

        this.notAttending.put(notAttending, true);
    }

    public void removeNotAttending(String notAttending) {
        if (this.notAttending != null) {
            this.notAttending.remove(notAttending);
        }
    }
}
