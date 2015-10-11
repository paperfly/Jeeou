package com.paperfly.instantjio.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.paperfly.instantjio.R;
import com.paperfly.instantjio.contacts.ContactsChooserActivity;
import com.paperfly.instantjio.groups.GroupsChooserActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class EventCreateActivity extends AppCompatActivity {
    private static final String TAG = EventCreateActivity.class.getCanonicalName();
    private static final String CHOSEN_GROUPS = "CHOSEN_GROUPS";
    private static final String CHOSEN_CONTACTS = "CHOSEN_CONTACTS";
    private static final int GROUPS_CHOOSER_RC = 0;
    private static final int CONTACTS_CHOOSER_RC = 1;
    private ArrayList<String> mChosenGroups;
    private HashMap<String, String> mChosenContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mChosenGroups = new ArrayList<>();
        mChosenContacts = new HashMap<>();

        initEventListeners();
    }

    void initEventListeners() {
        findViewById(R.id.add_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGroupsChooser();
            }
        });

        findViewById(R.id.add_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContactsChooser();
            }
        });

        findViewById(R.id.add_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void startGroupsChooser() {
        final Intent intent = new Intent(this, GroupsChooserActivity.class);

        if (mChosenGroups != null) {
            intent.putExtra(CHOSEN_GROUPS, mChosenGroups);
        }

        startActivityForResult(intent, GROUPS_CHOOSER_RC);
    }

    public void startContactsChooser() {
        final Intent intent = new Intent(this, ContactsChooserActivity.class);

        if (mChosenContacts != null)
            intent.putExtra(CHOSEN_CONTACTS, mChosenContacts);

        startActivityForResult(intent, CONTACTS_CHOOSER_RC);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GROUPS_CHOOSER_RC) {
            if (resultCode == RESULT_OK) {
                mChosenGroups = MergeList(mChosenGroups, (ArrayList<String>) data.getSerializableExtra(CHOSEN_GROUPS));

                for (int i = 0; i < mChosenGroups.size(); ++i) {
                    Log.i(TAG, mChosenGroups.get(i));
                }
            }
        } else if (requestCode == CONTACTS_CHOOSER_RC) {
            if (resultCode == RESULT_OK) {
                mChosenContacts = MergeMap(mChosenContacts, (HashMap<String, String>) data.getSerializableExtra(CHOSEN_CONTACTS));

                for (HashMap.Entry<String, String> entry : mChosenContacts.entrySet()) {
                    Log.i(TAG, entry.getKey() + " " + entry.getValue());
                }
            }
        }
    }

    // Merge two maps from right to left
    private HashMap<String, String> MergeMap(HashMap<String, String> left, HashMap<String, String> right) {
        final HashMap<String, String> temp = new HashMap<>();

        // Remove deselected chosenContacts here
        for (HashMap.Entry<String, String> entry : left.entrySet()) {
            if (right.containsKey(entry.getKey())) {
                temp.put(entry.getKey(), entry.getValue());
            }
        }

        // Merge them
        for (HashMap.Entry<String, String> entry : right.entrySet()) {
            temp.put(entry.getKey(), entry.getValue());
        }

        return temp;
    }

    private ArrayList<String> MergeList(ArrayList<String> left, ArrayList<String> right) {
        final ArrayList<String> temp = new ArrayList<>();

        // Remove deselected chosenContacts here

        for (int i = 0; i < left.size(); ++i) {
            if (right.contains(left.get(i))) {
                temp.add(left.get(i));
            }
        }

        for (int i = 0; i < right.size(); ++i) {
            if (!temp.contains(right.get(i))) {
                temp.add(right.get(i));
            }
        }

        return temp;
    }
}
