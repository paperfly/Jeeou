package com.paperfly.instantjio.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.contact.ContactsChooserActivity;
import com.paperfly.instantjiocore.Model.Group;

import java.util.HashMap;

public class GroupCreateActivity extends AppCompatActivity {
    final static String TAG = GroupCreateActivity.class.getCanonicalName();
    final String CHOSEN_CONTACTS = "CHOSEN_CONTACTS";
    HashMap<String, String> chosenContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chosenContacts = new HashMap<>();

        initEventListeners();
    }

    void initEventListeners() {
        findViewById(R.id.group_add_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContactsChooser();
            }
        });

        findViewById(R.id.group_add_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup();
            }
        });
    }

    public void startContactsChooser() {
        final Intent intent = new Intent(this, ContactsChooserActivity.class);

        if (chosenContacts != null)
            intent.putExtra(CHOSEN_CONTACTS, chosenContacts);

        //TODO May need to change request code?
        startActivityForResult(intent, 0);
    }

    public void addGroup() {
        final DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
//        final Firebase ref = new Firebase(getResources().getString(R.string.firebase_url));
        final DatabaseReference newGroupRef = groupRef.push();
        final Group group = new Group();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user != null ? user.getUid() : "";

        final EditText groupName = (EditText) findViewById(R.id.group_name);

        group.setName(groupName.getText().toString());
        group.setLeader(userId);
        // Add group members' indices
        for (HashMap.Entry<String, String> entry : chosenContacts.entrySet()) {
            group.addMember(entry.getValue());
        }

        // The leader is also a member of the group
        group.addMember(userId);

        // Add group to Firebase
        newGroupRef.setValue(group);

        // Leader need to have the group's index
        userRef.child(userId).child("groups").child(newGroupRef.getKey()).setValue(true);
        // Members need to have the group's index too
        for (HashMap.Entry<String, Boolean> entry : group.getMembers().entrySet()) {
            if (entry.getKey().equals(userId)) {
                continue;
            }

            userRef.child(entry.getKey()).child("newGroups").child(newGroupRef.getKey()).setValue(true);
        }

        finish();
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

                chosenContacts = MergeMap(chosenContacts, (HashMap<String, String>) data.getSerializableExtra(CHOSEN_CONTACTS));
            }
        }
    }

    // Merge two maps from right to left
    HashMap<String, String> MergeMap(HashMap<String, String> left, HashMap<String, String> right) {
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
}
