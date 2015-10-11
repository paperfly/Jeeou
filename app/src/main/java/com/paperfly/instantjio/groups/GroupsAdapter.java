package com.paperfly.instantjio.groups;

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
import com.paperfly.instantjio.common.ChooserEventListener;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupViewHolder> implements ChildEventListener {
    private static final String TAG = GroupsAdapter.class.getCanonicalName();
    private Firebase mBaseRef;
    private ArrayList<Group> mItems;
    private ArrayList<String> mKeys;
    private ChooserEventListener.ItemInteraction mItemInteraction;
    private ArrayList<String> mChosenGroups;

    public GroupsAdapter(Firebase ref, ArrayList<String> chosenGroups) {
        mBaseRef = ref;
        mItems = new ArrayList<>();
        mKeys = new ArrayList<>();
        mChosenGroups = chosenGroups;
        Query mQueryRef = mBaseRef.child("users").child(mBaseRef.getAuth().getUid()).child("groups");
        mQueryRef.addChildEventListener(this);
    }

    public void setChooserListener(ChooserEventListener.ItemInteraction itemInteraction) {
        mItemInteraction = itemInteraction;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(listItemView, mItemInteraction);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {

        final Boolean checked = mChosenGroups != null && mChosenGroups.contains(mKeys.get(position));

        holder.set(getItem(position), mKeys.get(position), checked);
    }

    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    void itemAdded(Group item, String key, int position) {

    }

    void itemChanged(Group oldItem, Group newItem, String key, int position) {

    }

    void itemRemoved(Group item, String key, int position) {

    }

    void itemMoved(Group item, String key, int oldPosition, int newPosition) {

    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, final String previousChildKey) {
        final Group item = new Group();
        final String key = snapshot.getKey();

        if (!mKeys.contains(key)) {
            mBaseRef.child("groups").child(key).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    item.setName(dataSnapshot.getValue().toString());

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
            final Group oldItem = mItems.get(index);
            final Group newItem = new Group();

            mBaseRef.child("groups").child(key).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newItem.setName(dataSnapshot.getValue().toString());

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
            Group item = mItems.get(index);

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

        final Group item = new Group();

        mBaseRef.child("groups").child(key).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item.setName(dataSnapshot.getValue().toString());

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

    public ArrayList<Group> getItems() {
        return mItems;
    }

    public ArrayList<String> getKeys() {
        return mKeys;
    }

    public Group getItem(int position) {
        return mItems.get(position);
    }

    public int getPositionForItem(Group item) {
        return mItems != null && mItems.size() > 0 ? mItems.indexOf(item) : -1;
    }

    public boolean contains(Group item) {
        return mItems != null && mItems.contains(item);
    }
}