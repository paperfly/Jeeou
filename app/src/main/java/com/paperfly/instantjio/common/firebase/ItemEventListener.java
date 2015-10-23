package com.paperfly.instantjio.common.firebase;

public interface ItemEventListener<T> {
    void itemAdded(T item, String key, int position);

    void itemChanged(T oldItem, T newItem, String key, int position);

    void itemRemoved(T item, String key, int position);

    void itemMoved(T item, String key, int oldPosition, int newPosition);
}
