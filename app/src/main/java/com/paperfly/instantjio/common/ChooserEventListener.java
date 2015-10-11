package com.paperfly.instantjio.common;

public interface ChooserEventListener {
    interface ItemInteraction {
        void onItemClick(String s);

        void onConfirmSelection();
    }
}
