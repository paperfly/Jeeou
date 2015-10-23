package com.paperfly.instantjio.group;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.paperfly.instantjio.common.firebase.ItemEventListener;
import com.paperfly.instantjio.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class GroupsFragment extends Fragment {
    public static final String TAG = GroupsFragment.class.getCanonicalName();
    private View vRoot;
    private Firebase mRef;
    private GroupViewAdapter mAdapter;
    private boolean mChoosingMode;
    private ChooserEventListener.ItemInteraction mItemInteraction;
    private ItemEventListener<Group> mListener = new ItemEventListener<Group>() {
        @Override
        public void itemAdded(Group item, String key, int position) {
            if (mAdapter.getItemCount() > 0) {
                vRoot.setBackgroundResource(0);
            }
        }

        @Override
        public void itemChanged(Group oldItem, Group newItem, String key, int position) {

        }

        @Override
        public void itemRemoved(Group item, String key, int position) {
            if (mAdapter.getItemCount() == 0) {
                if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT >= 22) {
                    vRoot.setBackground(getResources().getDrawable(R.drawable.empty_group_light_bg, null));
                } else if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
                    vRoot.setBackground(getResources().getDrawable(R.drawable.empty_group_light_bg));
                } else {
                    vRoot.setBackgroundDrawable(getResources().getDrawable(R.drawable.empty_group_light_bg));
                }
            }
        }

        @Override
        public void itemMoved(Group item, String key, int oldPosition, int newPosition) {

        }
    };

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

        mChoosingMode = getArguments() != null && getArguments().getBoolean(Constants.CHOOSING_MODE);

        mRef = new Firebase(getString(R.string.firebase_url));
        Query ref1 = mRef.child("users").child(mRef.getAuth().getUid()).child("groups");
        Query ref2 = mRef.child("groups");
        mAdapter = new GroupViewAdapter(ref1, ref2, mListener);

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
        vRoot = inflater.inflate(R.layout.fragment_groups, container, false);
        RecyclerView recyclerView = (RecyclerView) vRoot.findViewById(R.id.group_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return vRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mAdapter.getItemCount() == 0) {
            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT >= 22) {
                view.setBackground(getResources().getDrawable(R.drawable.empty_group_light_bg, null));
            } else if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
                view.setBackground(getResources().getDrawable(R.drawable.empty_group_light_bg));
            } else {
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.empty_group_light_bg));
            }
        } else {
            view.setBackgroundResource(0);
        }
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
        public GroupViewAdapter(Query firstRef, Query secondRef, ItemEventListener<Group> listener) {
            super(firstRef, secondRef, Group.class, listener);
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
            private TextView vName;
            private TextView vMembers;

            public GroupViewViewHolder(View view) {
                super(view);
                vName = (TextView) view.findViewById(R.id.group_name);
                vMembers = (TextView) view.findViewById(R.id.group_members);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mChoosingMode) {
                            mItemInteraction.onItemClick(mKeys.get(getAdapterPosition()));
                        } else {
                            Intent intent = new Intent(getContext(), GroupScrollingActivity.class);
                            Group group = getItem(getAdapterPosition());
                            GroupParcelable groupParcelable = new GroupParcelable(group);
                            intent.putExtra(Constants.GROUP_OBJECT, groupParcelable);
                            intent.putExtra(Constants.GROUP_KEY, mKeys.get(getAdapterPosition()));
                            startActivity(intent);
                        }
                    }
                });
            }

            public void setView(Group group) {
                vName.setText(group.getName());
                new GetMemberNames().execute(group);
            }

            public class GetMemberNames extends AsyncTask<Group, Void, String> {
                private String mMembers;
                private List<String> mMemberList;

                @Override
                protected String doInBackground(Group... params) {
                    mMemberList = new ArrayList<>();
                    final Semaphore semaphore = new Semaphore(0);

                    for (Map.Entry<String, Boolean> entry : params[0].getMembers().entrySet()) {
                        mRef.child("users").child(entry.getKey()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mMemberList.add(dataSnapshot.getValue().toString());
                                semaphore.release();
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }

                    try {
                        semaphore.acquire(params[0].getMembers().size());
                        mMembers = TextUtils.join(", ", mMemberList);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }

                    return mMembers;
                }

                @Override
                protected void onPostExecute(String s) {
                    vMembers.setText(s);
                }
            }
        }
    }
}
