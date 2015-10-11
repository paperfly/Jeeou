package com.paperfly.instantjio.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.contacts.ContactsChooserActivity;

import java.util.HashMap;
import java.util.Map;

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
        final Firebase ref = new Firebase(getResources().getString(R.string.firebase_url));
        final EditText editText = (EditText) findViewById(R.id.group_name);
        final String groupName = editText.getText().toString();
        final Group group = new Group(groupName);
        final Firebase newRef = ref.child("groups").push();
        final Map<String, Object> groupIndexMap = new HashMap<>();

        groupIndexMap.put(newRef.getKey(), true);

        if (chosenContacts != null) {
            // Group only needs the index of the members (the uid -> phoneNumber in this case)
            for (HashMap.Entry<String, String> entry : chosenContacts.entrySet()) {
                group.addMembers(entry.getValue());
            }

            // Members need to have the mGroup's index too
            for (HashMap.Entry<String, Boolean> entry : group.getMembers().entrySet()) {
                ref.child("users").child(entry.getKey()).child("groups").updateChildren(groupIndexMap);
            }
        }

        group.setLeader(ref.getAuth().getUid());

        // Add mGroup to Firebase
        newRef.setValue(group);


        ref.child("users").child(ref.getAuth().getUid()).child("groups").updateChildren(groupIndexMap);

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
                if (chosenContacts == null)
                    chosenContacts = new HashMap<>();

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
