package com.paperfly.instantjio.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.paperfly.instantjio.R;
import com.paperfly.instantjio.groups.GroupsChooserActivity;

import java.util.ArrayList;

public class EventCreateActivity extends AppCompatActivity {
    private static final String TAG = EventCreateActivity.class.getCanonicalName();
    private static final String CHOSEN_GROUPS = "CHOSEN_GROUPS";
    private ArrayList<String> mChosenGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mChosenGroups = new ArrayList<>();

        initEventListeners();
    }

    void initEventListeners() {
        findViewById(R.id.add_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGroupsChooser();
            }
        });

//        findViewById(R.id.group_add_confirm).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addGroup();
//            }
//        });
    }

    public void startGroupsChooser() {
        final Intent intent = new Intent(this, GroupsChooserActivity.class);

        if (mChosenGroups != null) {
            intent.putExtra(CHOSEN_GROUPS, mChosenGroups);
        }

        //TODO May need to change request code?
        startActivityForResult(intent, 0);
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
        //TODO May need to change request code?
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                mChosenGroups = MergeList(mChosenGroups, (ArrayList<String>) data.getSerializableExtra(CHOSEN_GROUPS));

                for (int i = 0; i < mChosenGroups.size(); ++i) {
                    Log.i(TAG, mChosenGroups.get(i));
                }
            }
        }
    }

    // Merge two maps from right to left
    ArrayList<String> MergeList(ArrayList<String> left, ArrayList<String> right) {
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
