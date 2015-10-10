package com.paperfly.instantjio.groups;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.paperfly.instantjio.R;

public class GroupViewHolder extends RecyclerView.ViewHolder {
    Group mGroup;
    TextView name;

    public GroupViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.read_group_name);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void set(Group group) {
        mGroup = group;
        name.setText(group.getName());
    }
}
