package com.paperfly.instantjio.group;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.ChooserEventListener;

public class GroupViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = GroupViewHolder.class.getCanonicalName();
    private ChooserEventListener.ItemInteraction mItemInteraction;
    //    private Group mGroup;
    private String mGroupIndex;
    private TextView mGroupNameText;
    private Boolean mSelected;

    public GroupViewHolder(final View itemView, ChooserEventListener.ItemInteraction itemInteraction) {
        super(itemView);

        mItemInteraction = itemInteraction;
        mGroupNameText = (TextView) itemView.findViewById(R.id.group_name);
        mSelected = false;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemInteraction != null) {
                    mItemInteraction.onItemClick(mGroupIndex);
                }
                mSelected = !mSelected;
                select();

            }
        });
    }

    private void select() {
        if (mItemInteraction == null) {
            return;
        }


        if (mSelected) {
            Log.i(TAG, "Selected " + mGroupIndex);
//            itemView.setBackgroundColor(itemView.getResources().getColor(R.color.colorAccent));
        } else {
            Log.i(TAG, "Deselected " + mGroupIndex);
//            TypedValue outValue = new TypedValue();
//            itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
//            itemView.setBackgroundResource(outValue.resourceId);
        }
    }

    public void set(Group group, String groupIndex, Boolean selected) {
//        mGroup = group;
        mGroupIndex = groupIndex;
        mGroupNameText.setText(group.getName());

        if (selected != null) {
            mSelected = selected;
            select();
        }
    }
}
