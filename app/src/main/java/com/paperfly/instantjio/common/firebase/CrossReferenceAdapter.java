package com.paperfly.instantjio.common.firebase;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public abstract class CrossReferenceAdapter<ViewHolder extends RecyclerView.ViewHolder, T> extends ReferenceAdapter<ViewHolder, T> {
    private static final String TAG = CrossReferenceAdapter.class.getCanonicalName();

    public CrossReferenceAdapter(Query firstRef, Query secondRef, Class<T> itemClass, ItemEventListener<T> itemListener) {
        super(firstRef, secondRef, itemClass, itemListener);

        mListener = new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot outerSnapshot, final String previousChildKey) {
                final String key = outerSnapshot.getKey();

                if (!mKeys.contains(key)) {
                    mSecondRef.getRef().child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot innerSnapshot) {
                            T item = innerSnapshot.getValue(CrossReferenceAdapter.this.mItemClass);

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

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(final DataSnapshot outerSnapshot, final String previousChildKey) {
                final String key = outerSnapshot.getKey();

                if (mKeys.contains(key)) {
                    final int index = mKeys.indexOf(key);
                    final T oldItem = mItems.get(index);

                    mSecondRef.getRef().child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot innerSnapshot) {
                            T newItem = innerSnapshot.getValue(CrossReferenceAdapter.this.mItemClass);

                            mItems.set(index, newItem);

                            notifyItemChanged(index);

                            if (mItemListener != null) {
                                mItemListener.itemChanged(oldItem, newItem, key, index);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });
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
            public void onChildMoved(final DataSnapshot outerSnapshot, final String previousChildKey) {
                final String key = outerSnapshot.getKey();
                final int index = mKeys.indexOf(key);

                mSecondRef.getRef().child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot innerSnapshot) {
                        T item = innerSnapshot.getValue(CrossReferenceAdapter.this.mItemClass);
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
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        };

        enable();
    }
}
