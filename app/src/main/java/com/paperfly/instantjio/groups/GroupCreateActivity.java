package com.paperfly.instantjio.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.paperfly.instantjio.R;

import java.util.HashMap;
import java.util.Map;

public class GroupCreateActivity extends AppCompatActivity {
    final static String TAG = GroupCreateActivity.class.getCanonicalName();
    HashMap<String, String> members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
//                Intent upIntent = NavUtils.getParentActivityIntent(this);
//                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//                    // This activity is NOT part of this app's task, so create a new task
//                    // when navigating up, with a synthesized back stack.
//                    TaskStackBuilder.create(this)
//                            // Add all of this activity's parents to the back stack
//                            .addNextIntentWithParentStack(upIntent)
//                                    // Navigate up to the closest parent
//                            .startActivities();
//                } else {
//                    // This activity is part of this app's task, so simply
//                    // navigate up to the logical parent activity.
//                    NavUtils.navigateUpTo(this, upIntent);
//                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addGroup() {
        EditText editGroupName = (EditText) findViewById(R.id.group_name);
        String name = editGroupName.getText().toString();
        Map<String, Boolean> _members = new HashMap<>();

        for (HashMap.Entry<String, String> entry : members.entrySet()) {
            _members.put(entry.getValue(), true);
        }

        Group group = new Group(name, _members);
        Firebase ref = new Firebase(getResources().getString(R.string.firebase_url));
        ref.child("groups").push().setValue(group);
        finish();
    }

    public void startContactsChooser() {
        Intent intent = new Intent(this, ContactsChooserActivity.class);

        if (members != null)
            intent.putExtra("CHOSEN_CONTACTS", members);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
//                String phoneNumber = data.getStringExtra("PHONE_NUMBER");
                if (members == null)
                    members = new HashMap<>();

                members = MergeMap(members, (HashMap<String, String>) data.getSerializableExtra("CHOSEN_CONTACTS"));
//                members.put(phoneNumber, true);
//                Log.d(TAG, phoneNumber);
//                Toast.makeText(this, phoneNumber, Toast.LENGTH_SHORT).show();
            }
        }
    }

    HashMap<String, String> MergeMap(HashMap<String, String> map1, HashMap<String, String> map2) {
        HashMap<String, String> temp = new HashMap<>();
        for (HashMap.Entry<String, String> entry : map1.entrySet()) {
            if (map2.containsKey(entry.getKey())) {
                temp.put(entry.getKey(), entry.getValue());
            }
        }
        for (HashMap.Entry<String, String> entry : map2.entrySet()) {
            temp.put(entry.getKey(), entry.getValue());
            Log.d(TAG, entry.getKey() + " ::: " + entry.getValue());
        }
        return temp;
    }
}
