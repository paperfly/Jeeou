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

        Bundle bMembers = in.readBundle();
        if (!bMembers.isEmpty()) {
            Map<String, Boolean> mMembers = new HashMap<>();
            for (String key : mMembers.keySet()) {
                mMembers.put(key, bMembers.getBoolean(key));
            }
            setMembers(mMembers);
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

        Bundle bMembers = new Bundle();
        if (getMembers() != null) {
            for (Map.Entry<String, Boolean> entry : getMembers().entrySet()) {
                bMembers.putBoolean(entry.getKey(), entry.getValue());
            }
        }
        out.writeBundle(bMembers);
    }
}
