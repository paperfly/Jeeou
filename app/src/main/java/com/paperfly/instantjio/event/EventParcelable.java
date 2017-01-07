package com.paperfly.instantjio.event;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.paperfly.instantjiocore.Model.Event;

import java.util.HashMap;
import java.util.Map;

public class EventParcelable extends Event implements Parcelable {
    public static final Parcelable.Creator<EventParcelable> CREATOR
            = new Parcelable.Creator<EventParcelable>() {
        public EventParcelable createFromParcel(Parcel in) {
            return new EventParcelable(in);
        }

        public EventParcelable[] newArray(int size) {
            return new EventParcelable[size];
        }
    };

    public EventParcelable(Event event) {
        setTitle(event.getTitle());
        setDescription(event.getDescription());
        setLocation(event.getLocation());
        setStartDate(event.getStartDate());
        setEndDate(event.getEndDate());
        setStartTime(event.getStartTime());
        setEndTime(event.getEndTime());
        setHost(event.getHost());
        setType(event.getType());
        setInvited(event.getInvited());
        setAttending(event.getAttending());
        setNotAttending(event.getNotAttending());
    }

    private EventParcelable(Parcel in) {
        setTitle(in.readString());
        setDescription(in.readString());
        setLocation(in.readString());
        setStartDate(in.readString());
        setEndDate(in.readString());
        setStartTime(in.readString());
        setEndTime(in.readString());
        setHost(in.readString());
        setType(in.readString());

        Bundle bInvited = in.readBundle();
        if (!bInvited.isEmpty()) {
            Map<String, Boolean> mInvited = new HashMap<>();
            for (String key : bInvited.keySet()) {
                mInvited.put(key, bInvited.getBoolean(key));
            }
            setInvited(mInvited);
        }

        Bundle bAttending = in.readBundle();
        if (!bAttending.isEmpty()) {
            Map<String, Boolean> mAttending = new HashMap<>();
            for (String key : bAttending.keySet()) {
                mAttending.put(key, bAttending.getBoolean(key));
            }
            setAttending(mAttending);
        }

        Bundle bNotAttending = in.readBundle();
        if (!bNotAttending.isEmpty()) {
            Map<String, Boolean> mNotAttending = new HashMap<>();
            for (String key : bNotAttending.keySet()) {
                mNotAttending.put(key, bNotAttending.getBoolean(key));
            }
            setNotAttending(mNotAttending);
        }
    }

    public EventParcelable getEvent() {
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getTitle());
        out.writeString(getDescription());
        out.writeString(getLocation());
        out.writeString(getStartDate());
        out.writeString(getEndDate());
        out.writeString(getStartTime());
        out.writeString(getEndTime());
        out.writeString(getHost());
        out.writeString(getType());

        Bundle bInvited = new Bundle();
        if (getInvited() != null) {
            for (Map.Entry<String, Boolean> entry : getInvited().entrySet()) {
                bInvited.putBoolean(entry.getKey(), entry.getValue());
            }
        }
        out.writeBundle(bInvited);

        Bundle bAttending = new Bundle();
        if (getAttending() != null) {
            for (Map.Entry<String, Boolean> entry : getAttending().entrySet()) {
                bAttending.putBoolean(entry.getKey(), entry.getValue());
            }
        }
        out.writeBundle(bAttending);

        Bundle bNotAttending = new Bundle();
        if (getNotAttending() != null) {
            for (Map.Entry<String, Boolean> entry : getNotAttending().entrySet()) {
                bNotAttending.putBoolean(entry.getKey(), entry.getValue());
            }
        }
        out.writeBundle(bNotAttending);
    }
}
