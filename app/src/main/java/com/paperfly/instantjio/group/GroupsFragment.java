package com.paperfly.instantjio.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.ChooserEventListener;
import com.paperfly.instantjio.common.firebase.CrossReferenceAdapter;
import com.paperfly.instantjio.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupsFragment extends Fragment {
    public static final String TAG = GroupsFragment.class.getCanonicalName();
    private Firebase mRef;
    private GroupViewAdapter mAdapter;
    private boolean mChoosingMode;
    //    private ArrayList<String> mChosenGroups;
    private ChooserEventListener.ItemInteraction mItemInteraction;

    public GroupsFragment() {

    }

    public static GroupsFragment newInstance(boolean choosingMode, ArrayList<String> chosenGroups) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.CHOOSING_MODE, choosingMode);
        args.putSerializable(Constants.CHOSEN_GROUPS, chosenGroups);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mChoosingMode = getArguments().getBoolean(Constants.CHOOSING_MODE);
        }

        mRef = new Firebase(getString(R.string.firebase_url));
        Query ref1 = mRef.child("users").child(mRef.getAuth().getUid()).child("groups");
        Query ref2 = mRef.child("groups");
        mAdapter = new GroupViewAdapter(ref1, ref2);

        if (mChoosingMode) {
            try {
                mItemInteraction = (ChooserEventListener.ItemInteraction) getContext();
            } catch (ClassCastException e) {
                throw new ClassCastException(getContext().toString()
                        + " must implement " + ChooserEventListener.class.getSimpleName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.group_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.groups_menu, menu);
        menu.findItem(R.id.menu_choose).setEnabled(mChoosingMode);
        menu.findItem(R.id.menu_choose).setVisible(mChoosingMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:

                break;
            case R.id.menu_choose:
                mItemInteraction.onConfirmSelection();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.disable();
    }

    public class GroupViewAdapter extends CrossReferenceAdapter<GroupViewAdapter.GroupViewViewHolder, Group> {
        public GroupViewAdapter(Query firstRef, Query secondRef) {
            super(firstRef, secondRef, Group.class, null);
        }

        @Override
        public GroupViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
            return new GroupViewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GroupViewViewHolder holder, int position) {
            holder.setView(getItem(position));
        }

        public class GroupViewViewHolder extends RecyclerView.ViewHolder {
            private TextView mName;
            private TextView mMembers;

            public GroupViewViewHolder(View view) {
                super(view);
                mName = (TextView) view.findViewById(R.id.group_name);
                mMembers = (TextView) view.findViewById(R.id.group_members);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mItemInteraction != null && !mChoosingMode) {
                            mItemInteraction.onItemClick(mKeys.get(getAdapterPosition()));
                        }
                    }
                });
            }

            public void setView(Group group) {
                final List<String> memberList = new ArrayList<>();
                for (Map.Entry<String, Boolean> entry : group.getMembers().entrySet()) {
                    mRef.child("users").child(entry.getKey()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            memberList.add(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                String members = TextUtils.join(", ", memberList);
                mName.setText(group.getName());
                mMembers.setText(members);
            }
        }
    }
}
