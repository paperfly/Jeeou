package com.paperfly.instantjio.group;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.firebase.CrossReferenceAdapter;
import com.paperfly.instantjio.util.Constants;

import java.util.Map;

public class GroupScrollingActivity extends AppCompatActivity {
    public static final String TAG = GroupScrollingActivity.class.getCanonicalName();
    // Views
    private TextView vMemberCount;
    private Button vActionExit;
    // Members
    private GroupDetailAdapter mAdapter;
    private Group mGroup;
    private String mKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_scrolling);

        GroupParcelable groupParcelable = getIntent().getParcelableExtra(Constants.GROUP_OBJECT);
        mGroup = groupParcelable.getGroup();
        mKey = getIntent().getStringExtra(Constants.GROUP_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mGroup.getName());
        }

        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        final DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user != null ? user.getUid() : "";

        vMemberCount = (TextView) findViewById(R.id.group_member_count);
        vActionExit = (Button) findViewById(R.id.group_action_exit);

        vMemberCount.setText(String.valueOf(mGroup.getMembers().size()));
        if (mGroup.getLeader().equals(userId)) {
            // I am the leader of the group
            vActionExit.setText("Disband Group");
        } else {
            // I am just a member of the group
            vActionExit.setText("Leave Group");
        }
        vActionExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitGroup(groupRef, userRef, userId);
            }
        });

        Query groupQuery = groupRef.child(mKey).child("members");
        Query userQuery = userRef;
        mAdapter = new GroupDetailAdapter(groupQuery, userQuery);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.member_list);
        CustomLinearLayoutManager layoutManager = new CustomLinearLayoutManager(recyclerView, CustomLinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(mAdapter);
    }

    private void exitGroup(DatabaseReference groupRef, DatabaseReference userRef, String userId) {
        if (mGroup.getLeader().equals(userId)) {
            // I am the leader of the group
            userRef.child(userId).child("groups").child(mKey).removeValue();
            userRef.child(userId).child("newGroups").child(mKey).removeValue();

            for (Map.Entry<String, Boolean> entry : mGroup.getMembers().entrySet()) {
                userRef.child(entry.getKey()).child("groups").child(mKey).removeValue();
                userRef.child(entry.getKey()).child("newGroups").child(mKey).removeValue();
            }

            groupRef.child(mKey).removeValue();
        } else {
            // I am just a member of the group
            userRef.child(userId).child("groups").child(mKey).removeValue();
            groupRef.child(mKey).child("members").child(userId).removeValue();
        }

        finish();
    }

    protected class GroupDetailAdapter extends CrossReferenceAdapter<GroupDetailAdapter.GroupDetailViewHolder, User> {
        public GroupDetailAdapter(Query firstRef, Query secondRef) {
            super(firstRef, secondRef, User.class, null);
        }

        @Override
        public GroupDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View listItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_member, parent, false);
            return new GroupDetailViewHolder(listItemView);
        }

        @Override
        public void onBindViewHolder(GroupDetailViewHolder holder, int position) {
            holder.setView(getItem(position));
        }

        protected class GroupDetailViewHolder extends RecyclerView.ViewHolder {
            TextView title;

            public GroupDetailViewHolder(View itemView) {
                super(itemView);

                title = (TextView) itemView.findViewById(R.id.member_name);
            }

            public void setView(User user) {
                title.setText(user.getName());
            }
        }
    }
}
