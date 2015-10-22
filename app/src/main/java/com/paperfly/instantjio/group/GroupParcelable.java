package com.paperfly.instantjio.group;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class GroupParcelable extends Group implements Parcelable {
    public static final Parcelable.Creator<GroupParcelable> CREATOR
            = new Parcelable.Creator<GroupParcelable>() {
        public GroupParcelable createFromParcel(Parcel in) {
            return new GroupParcelable(in);
        }

        public GroupParcelable[] newArray(int size) {
            return new GroupParcelable[size];
        }
    };

    public GroupParcelable(Group group) {
        setName(group.getName());
        setLeader(group.getLeader());
        setMembers(group.getMembers());
    }

    private GroupParcelable(Parcel in) {
        setName(in.readString());
        setLeader(in.readString());

        Bundle membersBundle = in.readBundle();
        if (!membersBundle.isEmpty()) {
            Map<String, Boolean> membersMap = new HashMap<>();
            for (String key : membersBundle.keySet()) {
                membersMap.put(key, membersBundle.getBoolean(key));
            }
            setMembers(membersMap);
        }
    }

    public GroupParcelable getGroup() {
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getName());
        out.writeString(getLeader());

        Bundle membersBundle = new Bundle();
        if (getMembers() != null) {
            for (Map.Entry<String, Boolean> entry : getMembers().entrySet()) {
                membersBundle.putBoolean(entry.getKey(), entry.getValue());
            }
        }
        out.writeBundle(membersBundle);
    }
}
