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

import java.util.Locale;

/**
 * Provide views to RecyclerView with data from mCursor
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactViewHolder> {
    private Cursor mCursor;

    public ContactsAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        // Create a new view
        View listItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);

        return new ContactViewHolder(listItemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, final int pos) {
        // Extract info from cursor
        mCursor.moveToPosition(pos);
        final String photoUri = mCursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);
        final String displayName = mCursor.getString(ContactsQuery.DISPLAY_NAME);
        final Uri contactUri = ContactsContract.Contacts.getLookupUri(
                mCursor.getLong(ContactsQuery.ID),
                mCursor.getString(ContactsQuery.LOOKUP_KEY));
//        final int startIndex = indexOfSearchQuery(displayName);

        // Create contact model and bind to ViewHolder
        contactViewHolder.set(new Contact(photoUri, displayName, contactUri));
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
