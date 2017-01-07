package com.paperfly.instantjio.contact;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.ChooserEventListener;
import com.paperfly.instantjiocore.Model.Contact;
import com.squareup.picasso.Picasso;

/**
 * Contains a Contact list item
 */
public class ContactViewHolder extends RecyclerView.ViewHolder {
    final CheckBox checkbox;
    final private QuickContactBadge mImage;
    final private TextView mLabel;
    private Contact mBoundContact; // Can be null
    private ChooserEventListener.ItemInteraction mCallback;

    public ContactViewHolder(View v, ChooserEventListener.ItemInteraction callback) {
        super(v);
        mImage = (QuickContactBadge) v.findViewById(android.R.id.icon);
        mLabel = (TextView) v.findViewById(android.R.id.text1);
        checkbox = (CheckBox) v.findViewById(R.id.contact_checkbox);
        mCallback = callback;

        if (mCallback == null) {
            checkbox.setVisibility(View.GONE);
        } else {
            mImage.setClickable(false);
            checkbox.setClickable(false);
        }

        // Define click listener for the ViewHolder's View
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (checkbox.isChecked())
//                    checkbox.setChecked(false);
//                else
//                    checkbox.setChecked(true);

                checkbox.performClick();
                if (mBoundContact != null) {
//                    Toast.makeText(
//                            v.getContext(),
//                            "Hi, I'm " + mBoundContact.getDisplayName(),
//                            Toast.LENGTH_SHORT).show();
                    if (mCallback != null)
                        mCallback.onItemClick(mBoundContact.getLookupKey());
                }
            }
        });

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mBoundContact != null) {
                    Toast.makeText(
                            v.getContext(),
                            "Hi, I'm " + mBoundContact.getDisplayName() + " and I got long pressed",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }


    public void set(Contact contact, Boolean checked) {
        mBoundContact = contact;
        mLabel.setText(contact.getDisplayName());
        Picasso.with(itemView.getContext())
                .load(contact.getPhotoUri())
                .placeholder(R.drawable.ic_action_person)
                .error(R.drawable.ic_action_person)
                .into(mImage);
        mImage.assignContactUri(contact.getContactUri());

        if (checked != null) {
            checkbox.setChecked(checked);
        }
    }

}
