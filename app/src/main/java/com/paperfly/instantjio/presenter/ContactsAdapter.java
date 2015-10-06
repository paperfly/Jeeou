package com.paperfly.instantjio.presenter;

import android.content.ContentUris;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paperfly.instantjio.R;
import com.paperfly.instantjio.model.Contact;

/**
 * Created by zzyzy on 6/10/2015.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private Cursor mCursor;
    private final int mNameColIdx,
            mIdColIdx;



    public ContactsAdapter(Cursor cursor) {
        mCursor = cursor;
        mNameColIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        mIdColIdx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int pos) {

        View listItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);

        return new ContactViewHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int pos) {
        // Extract info from cursor
        mCursor.moveToPosition(pos);
        String contactName = mCursor.getString(mNameColIdx);
        long contactId = mCursor.getLong(mIdColIdx);

        // Create contact model and bind to viewholder
        Contact c = new Contact();
        c.name = contactName;
        c.profilePic = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);

        contactViewHolder.bind(c);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
}
