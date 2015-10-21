package com.paperfly.instantjio.event;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.paperfly.instantjio.R;

public class EventScrollingActivity extends AppCompatActivity {
    private final String TAG = EventScrollingActivity.class.getCanonicalName();
    private Button eventCancel;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Why you bojio!");
        }

        eventCancel = (Button) findViewById(R.id.event_cancel);
        eventCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEvent();
            }
        });
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        EventParcelable eventParcelable = getIntent().getParcelableExtra("EVENT");
        event = eventParcelable.getEvent();
    }

    private void cancelEvent() {

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
}
