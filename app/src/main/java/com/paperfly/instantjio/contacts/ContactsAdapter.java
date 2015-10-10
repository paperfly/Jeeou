package com.paperfly.instantjio.contacts;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paperfly.instantjio.R;

import java.util.HashMap;
import java.util.Locale;

/**
 * Provide views to RecyclerView with data from mCursor
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactViewHolder> {
    HashMap<String, String> mContacts;
    private Cursor mCursor;
    private ContactsFragment.OnContactClickListener mCallback;

    public ContactsAdapter(Cursor cursor, ContactsFragment.OnContactClickListener callback, HashMap<String, String> contacts) {
        mCursor = cursor;
        mCallback = callback;
        mContacts = contacts;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        // Create a new view
        View listItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ContactViewHolder(listItemView, mCallback);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, final int pos) {
        // Extract info from cursor
        mCursor.moveToPosition(pos);
        final String photoUri = mCursor.getString(ContactsQuery.Contact.PHOTO_THUMBNAIL_DATA);
        final String displayName = mCursor.getString(ContactsQuery.Contact.DISPLAY_NAME);


        final String lookupKey = mCursor.getString(ContactsQuery.Contact.LOOKUP_KEY);
        Uri contactUri = null;
        if (mCallback == null)
            contactUri = ContactsContract.Contacts.getLookupUri(mCursor.getLong(ContactsQuery.Contact.ID), mCursor.getString(ContactsQuery.Contact.LOOKUP_KEY));
//        final int startIndex = indexOfSearchQuery(displayName);

        Boolean checked;

        if (mContacts != null) {
            if (mContacts.containsKey(lookupKey)) {
                checked = true;
            } else {
                checked = false;
            }
        } else {
            checked = null;
        }


        // Create contact model and bind to ViewHolder
        contactViewHolder.set(new Contact(photoUri, displayName, lookupKey, contactUri), checked);
    }

    // Return the size of the cursor (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * Identifies the start of the search string in the display name column of a Cursor row.
     * E.g. If displayName was "Adam" and search query (mSearchTerm) was "da" this would
     * return 1.
     *
     * @param displayName The contact display name.
     * @return The starting position of the search string in the display name, 0-based. The
     * method returns -1 if the string is not found in the display name, or if the search
     * string is empty or null.
     */
    private int indexOfSearchQuery(String displayName, String searchTerm) {
        if (!TextUtils.isEmpty(searchTerm)) {
            return displayName.toLowerCase(Locale.getDefault()).indexOf(
                    searchTerm.toLowerCase(Locale.getDefault()));
        }
        return -1;
    }


}
