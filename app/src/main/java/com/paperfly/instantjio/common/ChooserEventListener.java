package com.paperfly.instantjio.common;

import java.util.HashMap;

public interface ChooserEventListener {
    interface ItemChosen {
        void updateChosenItems(HashMap<String, String> chosenItems);
    }

    interface ItemInteraction {
        void onItemClick(String s);

        void onConfirmSelection();
    }
}
