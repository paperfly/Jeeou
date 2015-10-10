package com.paperfly.instantjio.contacts;

import android.net.Uri;

/**
 * Entity model that represents a contact
 */
public class Contact {
    String photoUri;
    String displayName;
    String lookupKey;
    Uri contactUri;

    public Contact() {
    }

    public Contact(String photoUri, String displayName, String lookupKey, Uri contactUri) {
        this.photoUri = photoUri;
        this.displayName = displayName;
        this.lookupKey = lookupKey;
        this.contactUri = contactUri;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public Uri getContactUri() {
        return contactUri;
    }
}
