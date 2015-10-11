package com.paperfly.instantjio.contacts;

import java.util.HashMap;

public interface ContactsListener {
    interface ContactsChooser {
        void updateChosenContacts(HashMap<String, String> chosenContacts);
    }

    interface ContactClick {
        void onContactClick(String lookupKey);

        void onConfirmClick();
    }
}
