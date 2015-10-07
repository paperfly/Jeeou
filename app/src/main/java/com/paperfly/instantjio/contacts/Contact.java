package com.paperfly.instantjio.contacts;

import android.net.Uri;

/**
 * Entity model that represents a contact
 */
final public class Contact {
    final private String photoUri;
    final private String displayName;
    final private Uri contactUri;

    public Contact(String photoUri, String displayName, Uri contactUri) {
        this.photoUri = photoUri;
        this.displayName = displayName;
        this.contactUri = contactUri;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Uri getContactUri() {
        return contactUri;
    }
}
