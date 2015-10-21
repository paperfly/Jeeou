package com.paperfly.instantjio.group;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.firebase.CrossReferenceAdapter;

public class GroupScrollingActivity extends AppCompatActivity {

    private GroupDetailAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Why you bo group?!");

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Firebase ref = new Firebase(getString(R.string.firebase_url));
        Firebase groupRef = ref.child("groups").child("-K12x9E326mMVjAFbT6Y").child("members");
        Firebase userRef = ref.child("users");
        mAdapter = new GroupDetailAdapter(groupRef, userRef, User.class, null);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.member_list);
        CustomLinearLayoutManager layoutManager = new CustomLinearLayoutManager(recyclerView, CustomLinearLayoutManager.VERTICAL, false);
        layoutManager.setChildSize(R.attr.listPreferredItemHeightLarge);
        layoutManager.setOverScrollMode(ViewCompat.OVER_SCROLL_IF_CONTENT_SCROLLS);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(this, CustomLinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    protected class GroupDetailAdapter extends CrossReferenceAdapter<GroupDetailAdapter.GroupDetailViewHolder, User> {
        public GroupDetailAdapter(Firebase firstRef, Firebase secondRef, Class<User> itemClass, ItemEventListener<User> itemListener) {
            super(firstRef, secondRef, itemClass, itemListener);
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
