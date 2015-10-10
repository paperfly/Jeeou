package com.paperfly.instantjio.groups;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.paperfly.instantjio.R;
import com.paperfly.instantjio.contacts.ContactsFragment;
import com.paperfly.instantjio.contacts.ContactsQuery;

import java.util.HashMap;

public class ContactsChooserActivity extends AppCompatActivity implements ContactsFragment.OnContactClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    final String TAG = ContactsChooserActivity.class.getCanonicalName();
    HashMap<String, String> contacts;
    Intent intent;
    private String mLookupKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_chooser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.contacts_chooser_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            if (getIntent().getExtras() != null) {
                if (contacts == null)
                    contacts = new HashMap<>();
                contacts = (HashMap<String, String>) getIntent().getSerializableExtra("CHOSEN_CONTACTS");
            }

            ContactsFragment contactsFragment = ContactsFragment.newInstance(true, contacts);
            getSupportFragmentManager().beginTransaction().add(R.id.contacts_chooser_container, contactsFragment).commit();
        }
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

    public void onContactClick(String lookupKey) {
        Log.d(TAG, lookupKey);
        mLookupKey = lookupKey;
        getSupportLoaderManager().restartLoader(ContactsQuery.Phone.QUERY_ID, null, this);
    }

    public void onConfirmClick() {
        if (contacts == null)
            finish();

        if (intent == null)
            intent = new Intent();
        intent.putExtra("CHOSEN_CONTACTS", contacts);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ContactsQuery.Phone.QUERY_ID:
                String[] selectionArgs = {""};
                selectionArgs[0] = mLookupKey;
                return new CursorLoader(this,
                        ContactsQuery.Phone.CONTENT_URI,
                        ContactsQuery.Phone.PROJECTION,
                        ContactsQuery.Phone.SELECTION,
                        selectionArgs, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == ContactsQuery.Phone.QUERY_ID) {
            if (data.moveToNext()) {
                final String phoneNumber = String.valueOf(data.getString(ContactsQuery.Phone.PHONE_NUMBER));
                Log.d(TAG, phoneNumber);


                if (contacts == null)
                    contacts = new HashMap<>();

                if (contacts.containsKey(mLookupKey))
                    contacts.remove(mLookupKey);
                else
                    contacts.put(mLookupKey, phoneNumber);


//                setResult(RESULT_OK, resultData);
//                finish();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nothing to do here. The Cursor does not need to be released as it was never directly
        // bound to anything (like an adapter).
    }

}
