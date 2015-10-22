package com.paperfly.instantjio.event;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.util.Constants;
import com.squareup.picasso.Picasso;

public class EventTemplateScrollingActivity extends AppCompatActivity {

    // Views
    private Button vActionDelete;
    private Button vActionViewLocation;
    private ImageView vHostPicture;
    private TextView vHost;
    private TextView vLocation;
    private TextView vDescription;
    private TextView vStartDate;
    private TextView vEndDate;
    //    private FloatingActionButton vFAB;
    // Members
    private Event mEvent;
    private String mKey;
    private String mHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_template_scrolling);

        EventParcelable eventParcelable = getIntent().getParcelableExtra(Constants.EVENT_OBJECT);
        mEvent = eventParcelable.getEvent();
        mKey = getIntent().getStringExtra(Constants.EVENT_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mEvent.getTitle());
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
        Firebase ref = new Firebase(getString(R.string.firebase_url));

        vActionDelete = (Button) findViewById(R.id.event_template_action_delete);
        vActionViewLocation = (Button) findViewById(R.id.event_action_view_location);
        vHostPicture = (ImageView) findViewById(R.id.event_host_picture);
        vHost = (TextView) findViewById(R.id.event_host);
        vStartDate = (TextView) findViewById(R.id.event_start_date);
        vEndDate = (TextView) findViewById(R.id.event_end_date);
        vLocation = (TextView) findViewById(R.id.event_location);
        vDescription = (TextView) findViewById(R.id.event_description);


        if (ref.getAuth().getUid().equals(mEvent.getHost())) {
            vActionDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteTemplate();
                }
            });
//            vFAB.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        } else {
            vActionDelete.setVisibility(View.GONE);
            vActionDelete.setEnabled(false);

//            vFAB.setVisibility(View.GONE);
//            vFAB.setEnabled(false);
        }

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
        ref.child("users").child(mEvent.getHost()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mHost = dataSnapshot.getValue().toString();
                vHost.setText(mHost);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // Location & description
        vLocation.setText(mEvent.getLocation());
        vDescription.setText(mEvent.getDescription());
    }

    private void deleteTemplate() {
        Firebase ref = new Firebase(getString(R.string.firebase_url));

        ref.child("users").child(mEvent.getHost()).child("templates").child(mKey).removeValue();

        finish();
    }
}
