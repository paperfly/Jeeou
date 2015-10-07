package com.paperfly.instantjio.contacts;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.paperfly.instantjio.R;
import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 * Contains a Contact list item
 */
public class ContactViewHolder extends RecyclerView.ViewHolder {
    final private QuickContactBadge mImage;
    final private TextView mLabel;
    private Contact mBoundContact; // Can be null

    public ContactViewHolder(View v) {
        super(v);
        mImage = (QuickContactBadge) v.findViewById(android.R.id.icon);
        mLabel = (TextView) v.findViewById(android.R.id.text1);

        // Define click listener for the ViewHolder's View
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoundContact != null) {
                    Toast.makeText(
                            v.getContext(),
                            "Hi, I'm " + mBoundContact.getDisplayName(),
                            Toast.LENGTH_SHORT).show();
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

    public void set(Contact contact) {
        mBoundContact = contact;
        mLabel.setText(contact.getDisplayName());
        Picasso.with(itemView.getContext())
                .load(contact.getPhotoUri())
                .placeholder(R.drawable.ic_action_person)
                .error(R.drawable.ic_action_person)
                .into(mImage);
        mImage.assignContactUri(contact.getContactUri());
    }
}
