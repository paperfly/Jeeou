package com.paperfly.instantjio.common.firebase;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

public abstract class DirectReferenceAdapter<ViewHolder extends RecyclerView.ViewHolder, T> extends ReferenceAdapter<ViewHolder, T> {
    private static final String TAG = DirectReferenceAdapter.class.getCanonicalName();

    public DirectReferenceAdapter(Query ref, Class<T> itemClass, ItemEventListener<T> itemListener) {
        super(ref, null, itemClass, itemListener);

        mListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
                String key = dataSnapshot.getKey();

                if (!mKeys.contains(key)) {
                    T item = dataSnapshot.getValue(mItemClass);

                    int insertedPosition;
                    if (previousChildKey == null) {
                        mItems.add(0, item);
                        mKeys.add(0, key);
                        insertedPosition = 0;
                    } else {
                        int previousIndex = mKeys.indexOf(previousChildKey);
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == mItems.size()) {
                            mItems.add(item);
                            mKeys.add(key);
                        } else {
                            mItems.add(nextIndex, item);
                            mKeys.add(nextIndex, key);
                        }
                        insertedPosition = nextIndex;
                    }
                    notifyItemInserted(insertedPosition);

                    if (mItemListener != null) {
                        mItemListener.itemAdded(item, key, insertedPosition);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
                String key = dataSnapshot.getKey();

                if (mKeys.contains(key)) {
                    int index = mKeys.indexOf(key);
                    T oldItem = mItems.get(index);
                    T newItem = dataSnapshot.getValue(mItemClass);

                    mItems.set(index, newItem);

                    notifyItemChanged(index);

                    if (mItemListener != null) {
                        mItemListener.itemChanged(oldItem, newItem, key, index);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();

                if (mKeys.contains(key)) {
                    int index = mKeys.indexOf(key);
                    T item = mItems.get(index);

                    mKeys.remove(index);
                    mItems.remove(index);

                    notifyItemRemoved(index);

                    if (mItemListener != null) {
                        mItemListener.itemRemoved(item, key, index);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildKey) {
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);

                T item = dataSnapshot.getValue(mItemClass);
                mItems.remove(index);
                mKeys.remove(index);
                int newPosition;
                if (previousChildKey == null) {
                    mItems.add(0, item);
                    mKeys.add(0, key);
                    newPosition = 0;
                } else {
                    int previousIndex = mKeys.indexOf(previousChildKey);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mItems.size()) {
                        mItems.add(item);
                        mKeys.add(key);
                    } else {
                        mItems.add(nextIndex, item);
                        mKeys.add(nextIndex, key);
                    }
                    newPosition = nextIndex;
                }
                notifyItemMoved(index, newPosition);

                if (mItemListener != null) {
                    mItemListener.itemMoved(item, key, index, newPosition);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, firebaseError.getMessage());
            }
        };
    }
}
