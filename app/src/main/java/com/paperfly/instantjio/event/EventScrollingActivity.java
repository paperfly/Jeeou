package com.paperfly.instantjio.event;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.util.Constants;
import com.paperfly.instantjiocore.Model.Event;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class EventScrollingActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    public static final String TAG = EventScrollingActivity.class.getCanonicalName();
    // Views
    private Button vActionCancel;
    private Button vActionAttending;
    private Button vActionNotAttending;
    private Button vActionViewLocation;
    private ImageView vHostPicture;
    private TextView vHost;
    private TextView vAttendingCount;
    private TextView vWaitingCount;
    private TextView vStartDate;
    private TextView vEndDate;
    private TextView vLocation;
    private TextView vDescription;
    private FloatingActionButton vFAB;
    // Members
    private Event mEvent;
    private String mKey;
    //    private Firebase mRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mEventRef;
    private String mUserId;
    private String mHost;
    private int mAttendingCount;
    private int mWaitingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_scrolling);

        EventParcelable eventParcelable = getIntent().getParcelableExtra(Constants.EVENT_OBJECT);
        mEvent = eventParcelable.getEvent();
        mKey = getIntent().getStringExtra(Constants.EVENT_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mEvent.getTitle());
        }

//        mRef = new Firebase(getString(R.string.firebase_url));
        mUserRef = FirebaseDatabase.getInstance().getReference("users");
        mEventRef = FirebaseDatabase.getInstance().getReference("events");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = user != null ? user.getUid() : "";

        initViews();
        initRealtimeListeners();
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
        vActionCancel = (Button) findViewById(R.id.event_action_cancel);
        vActionAttending = (Button) findViewById(R.id.event_action_attending);
        vActionNotAttending = (Button) findViewById(R.id.event_action_not_attending);
        vActionViewLocation = (Button) findViewById(R.id.event_action_view_location);
        vHostPicture = (ImageView) findViewById(R.id.event_host_picture);
        vHost = (TextView) findViewById(R.id.event_host);
        vAttendingCount = (TextView) findViewById(R.id.event_attending_count);
        vWaitingCount = (TextView) findViewById(R.id.event_waiting_count);
        vStartDate = (TextView) findViewById(R.id.event_start_date);
        vEndDate = (TextView) findViewById(R.id.event_end_date);
        vLocation = (TextView) findViewById(R.id.event_location);
        vDescription = (TextView) findViewById(R.id.event_description);
        vFAB = (FloatingActionButton) findViewById(R.id.fab);

        if (mUserId.equals(mEvent.getHost())) {
            vActionCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelEvent();
                }
            });
            vFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            vActionCancel.setVisibility(View.GONE);
            vActionCancel.setEnabled(false);

            vFAB.setVisibility(View.GONE);
            vFAB.setEnabled(false);
        }

        vActionAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionAttending();
            }
        });
        vActionNotAttending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionNotAttending();
            }
        });
        vActionViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + vLocation.getText());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        // Host picture
        Picasso.with(this)
                .load(R.drawable.ic_account_circle_black)
                .placeholder(R.drawable.ic_account_circle_black)
                .error(R.drawable.ic_account_circle_black)
                .into(vHostPicture);

        // Host name
        mUserRef.child(mEvent.getHost()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mHost = dataSnapshot.getValue().toString();
                vHost.setText(mHost);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

//        // Attending count
//        if (mEvent.getAttending() != null) {
//            mAttendingCount = mEvent.getAttending().size();
//        } else {
//            mAttendingCount = 0;
//        }
//        vAttendingCount.setText(String.valueOf(mAttendingCount));
//
//        // Waiting count
//        if (mEvent.getInvited() != null) {
//            mWaitingCount += mEvent.getInvited().size();
//        }
//        if (mEvent.getNotAttending() != null) {
//            mWaitingCount += mEvent.getNotAttending().size();
//        }
//        if (mEvent.getInvited() == null && mEvent.getNotAttending() == null) {
//            mWaitingCount = 0;
//        }
//        vWaitingCount.setText(String.valueOf(mWaitingCount));

        // Dates
        vStartDate.setText(mEvent.getStartDate());
        vEndDate.setText(mEvent.getEndDate());

        // Location & description
        vLocation.setText(mEvent.getLocation());
        vDescription.setText(mEvent.getDescription());
    }

    private void initRealtimeListeners() {
        mEventRef.child(mKey).child("attending").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ++mAttendingCount;
                mEvent.addAttending(dataSnapshot.getKey());
                vAttendingCount.setText(String.valueOf(mAttendingCount));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                --mAttendingCount;
                mEvent.removeAttending(dataSnapshot.getKey());
                vAttendingCount.setText(String.valueOf(mAttendingCount));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        mEventRef.child(mKey).child("invited").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ++mWaitingCount;
                mEvent.addInvited(dataSnapshot.getKey());
                vWaitingCount.setText(String.valueOf(mWaitingCount));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                --mWaitingCount;
                mEvent.removeInvited(dataSnapshot.getKey());
                vWaitingCount.setText(String.valueOf(mWaitingCount));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        mEventRef.child(mKey).child("notAttending").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ++mWaitingCount;
                mEvent.addNotAttending(dataSnapshot.getKey());
                vWaitingCount.setText(String.valueOf(mWaitingCount));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                --mWaitingCount;
                mEvent.removeNotAttending(dataSnapshot.getKey());
                vWaitingCount.setText(String.valueOf(mWaitingCount));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    private void actionAttending() {
        mEventRef.child(mKey).child("attending").child(mUserId).setValue(true);
        mEventRef.child(mKey).child("invited").child(mUserId).removeValue();
        mEventRef.child(mKey).child("notAttending").child(mUserId).removeValue();
    }

    private void actionNotAttending() {
        mEventRef.child(mKey).child("notAttending").child(mUserId).setValue(true);
        mEventRef.child(mKey).child("attending").child(mUserId).removeValue();
        mEventRef.child(mKey).child("invited").child(mUserId).removeValue();
    }

    private void cancelEvent() {
//        Firebase ref = new Firebase(getString(R.string.firebase_url));

        if (mEvent.getInvited() != null) {
            for (Map.Entry<String, Boolean> entry : mEvent.getInvited().entrySet()) {
                mUserRef.child(entry.getKey()).child("events").child(mKey).removeValue();
                mUserRef.child(entry.getKey()).child("newEvents").child(mKey).removeValue();
            }
        }

        if (mEvent.getAttending() != null) {
            for (Map.Entry<String, Boolean> entry : mEvent.getAttending().entrySet()) {
                mUserRef.child(entry.getKey()).child("events").child(mKey).removeValue();
                mUserRef.child(entry.getKey()).child("newEvents").child(mKey).removeValue();
            }
        }

        if (mEvent.getNotAttending() != null) {
            for (Map.Entry<String, Boolean> entry : mEvent.getNotAttending().entrySet()) {
                mUserRef.child(entry.getKey()).child("events").child(mKey).removeValue();
                mUserRef.child(entry.getKey()).child("newEvents").child(mKey).removeValue();
            }
        }

        mUserRef.child(mEvent.getHost()).child("events").child(mKey).removeValue();
        mUserRef.child(mEvent.getHost()).child("newEvents").child(mKey).removeValue();

        mEventRef.child(mKey).removeValue();
        finish();
    }
}
