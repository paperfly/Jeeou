package com.paperfly.instantjio.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.paperfly.instantjio.R;
import com.paperfly.instantjio.model.Contact;
import com.squareup.picasso.Picasso;

/**
 * Created by zzyzy on 6/10/2015.
 */
public class ContactViewHolder extends RecyclerView.ViewHolder {
    private QuickContactBadge mImage;
    private TextView mLabel;
    private Contact mBoundContact; // Can be null

    public ContactViewHolder(final View itemView) {
        super(itemView);
        mImage = (QuickContactBadge) itemView.findViewById(android.R.id.icon);
        mLabel = (TextView) itemView.findViewById(android.R.id.text1);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoundContact != null) {
                    Toast.makeText(
                            itemView.getContext(),
                            "Hi, I'm " + mBoundContact.name,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void bind(Contact contact) {
        mBoundContact = contact;
        mLabel.setText(contact.name);
        Picasso.with(itemView.getContext())
                .load(contact.profilePic)
                .placeholder(R.drawable.ic_action_person)
                .error(R.drawable.ic_action_person)
                .into(mImage);
    }
}
