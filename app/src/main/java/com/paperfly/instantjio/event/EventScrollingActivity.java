package com.paperfly.instantjio.event;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;
import com.paperfly.instantjio.R;

import java.util.Map;

public class EventScrollingActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    public static final String TAG = EventScrollingActivity.class.getCanonicalName();
    // Views
    @SuppressWarnings("FieldCanBeLocal")
    private Button eventCancel;
    // Members
    private Event event;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_scrolling);

        EventParcelable eventParcelable = getIntent().getParcelableExtra(EventsFragment.EVENT_OBJECT);
        event = eventParcelable.getEvent();
        key = getIntent().getStringExtra(EventsFragment.EVENT_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(event.getTitle());
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
        eventCancel = (Button) findViewById(R.id.event_cancel);
        eventCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEvent();
            }
        });
    }

    private void cancelEvent() {
        Firebase ref = new Firebase(getString(R.string.firebase_url));

        if (event.getInvited() != null) {
            for (Map.Entry<String, Boolean> entry : event.getInvited().entrySet()) {
                ref.child("users").child(entry.getKey()).child("events").child(key).removeValue();
            }
        }

        if (event.getAttending() != null) {
            for (Map.Entry<String, Boolean> entry : event.getAttending().entrySet()) {
                ref.child("users").child(entry.getKey()).child("events").child(key).removeValue();
            }
        }

        if (event.getNotAttending() != null) {
            for (Map.Entry<String, Boolean> entry : event.getNotAttending().entrySet()) {
                ref.child("users").child(entry.getKey()).child("events").child(key).removeValue();
            }
        }

        ref.child("users").child(event.getHost()).child("events").child(key).removeValue();

        ref.child("events").child(key).removeValue();
        finish();
    }
}
