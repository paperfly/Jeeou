package com.paperfly.instantjio.events;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.paperfly.instantjio.R;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventViewHolder> implements ChildEventListener {
    private static final String TAG = EventsAdapter.class.getCanonicalName();
    private Firebase mBaseRef;
    private ArrayList<Event> mItems;
    private ArrayList<String> mKeys;

    public EventsAdapter(Firebase ref) {
        mBaseRef = ref;
        mItems = new ArrayList<>();
        mKeys = new ArrayList<>();
        Query mQueryRef = mBaseRef.child("users").child(mBaseRef.getAuth().getUid()).child("events");
        mQueryRef.addChildEventListener(this);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(listItemView);
    }

    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.set(getItem(position));
    }

    void itemAdded(Event item, String key, int position) {

    }

    void itemChanged(Event oldItem, Event newItem, String key, int position) {

    }

    void itemRemoved(Event item, String key, int position) {

    }

    void itemMoved(Event item, String key, int oldPosition, int newPosition) {

    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, final String previousChildKey) {
        final Event item = new Event();
        final String key = snapshot.getKey();

        if (!mKeys.contains(key)) {
            mBaseRef.child("events").child(key).child("title").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) return;

                    item.setTitle(dataSnapshot.getValue().toString());

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
                    itemAdded(item, key, insertedPosition);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        final String key = dataSnapshot.getKey();

        if (mKeys.contains(key)) {
            final int index = mKeys.indexOf(key);
            final Event oldItem = mItems.get(index);
            final Event newItem = new Event();

            mBaseRef.child("events").child(key).child("title").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) return;
                    newItem.setTitle(dataSnapshot.getValue().toString());

                    mItems.set(index, newItem);

                    notifyItemChanged(index);
                    itemChanged(oldItem, newItem, key, index);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();

        if (mKeys.contains(key)) {
            int index = mKeys.indexOf(key);
            Event item = mItems.get(index);

            mKeys.remove(index);
            mItems.remove(index);

            notifyItemRemoved(index);
            itemRemoved(item, key, index);
        }
    }


    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, final String previousChildKey) {
        final String key = dataSnapshot.getKey();

        final int index = mKeys.indexOf(key);
//        Group item = dataSnapshot.getValue(FirebaseRecyclerAdapter.this.mItemClass);

        final Event item = new Event();

        mBaseRef.child("events").child(key).child("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) return;
                item.setTitle(dataSnapshot.getValue().toString());

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
                itemMoved(item, key, index, newPosition);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Log.e(TAG, "Listen was cancelled, no more updates will occur.");
    }

    public void destroy() {
        mBaseRef.removeEventListener(this);
    }

    public ArrayList<Event> getItems() {
        return mItems;
    }

    public ArrayList<String> getKeys() {
        return mKeys;
    }

    public Event getItem(int position) {
        return mItems.get(position);
    }

    public int getPositionForItem(Event item) {
        return mItems != null && mItems.size() > 0 ? mItems.indexOf(item) : -1;
    }

    public boolean contains(Event item) {
        return mItems != null && mItems.contains(item);
    }
}
