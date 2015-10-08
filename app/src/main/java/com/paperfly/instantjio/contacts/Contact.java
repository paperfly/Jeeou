package com.paperfly.instantjio.contacts;

import android.net.Uri;

/**
 * Entity model that represents a contact
 */
public class Contact {
    String photoUri;
    String displayName;
    Uri contactUri;

    public Contact() {
    }

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
