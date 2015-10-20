package com.paperfly.instantjio.common.firebase;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;

import java.util.ArrayList;

public abstract class ReferenceAdapter<ViewHolder extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<ViewHolder> {
    private final String TAG = ReferenceAdapter.class.getCanonicalName();
    protected Firebase mFirstRef;
    protected Firebase mSecondRef;
    protected Class<T> mItemClass;
    protected ItemEventListener<T> mItemListener;
    protected ArrayList<T> mItems;
    protected ArrayList<String> mKeys;
    protected ChildEventListener mListener;

    public ReferenceAdapter(Firebase firstRef, Firebase secondRef, Class<T> itemClass, ItemEventListener<T> itemListener) {
        mFirstRef = firstRef;
        mSecondRef = secondRef;
        mItemClass = itemClass;
        mItemListener = itemListener;
        mItems = new ArrayList<>();
        mKeys = new ArrayList<>();
    }

    protected void create() {
        mFirstRef.addChildEventListener(mListener);
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    public void destroy() {
        mFirstRef.removeEventListener(mListener);
    }

    public ArrayList<T> getItems() {
        return mItems;
    }

    public ArrayList<String> getKeys() {
        return mKeys;
    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    public int getPositionForItem(T item) {
        return mItems != null && mItems.size() > 0 ? mItems.indexOf(item) : -1;
    }

    public boolean contains(T item) {
        return mItems != null && mItems.contains(item);
    }

    protected interface ItemEventListener<T> {
        void itemAdded(T item, String key, int position);

        void itemChanged(T oldItem, T newItem, String key, int position);

        void itemRemoved(T item, String key, int position);

        void itemMoved(T item, String key, int oldPosition, int newPosition);
    }
}
