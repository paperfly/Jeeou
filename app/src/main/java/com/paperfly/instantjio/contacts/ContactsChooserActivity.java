package com.paperfly.instantjio.contacts;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.paperfly.instantjio.BuildConfig;
import com.paperfly.instantjio.R;
import com.paperfly.instantjio.common.ChooserEventListener;

import java.util.HashMap;

public class ContactsChooserActivity extends AppCompatActivity implements
        ChooserEventListener.ItemInteraction,
        LoaderManager.LoaderCallbacks<Cursor> {
    final static String TAG = ContactsChooserActivity.class.getCanonicalName();
    final String CHOSEN_CONTACTS = "CHOSEN_CONTACTS";
    final Boolean CHOOSING_MODE = true;
    HashMap<String, String> chosenContacts;
    String mLookupKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_chooser);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null)
            return;

        chosenContacts = new HashMap<>();

        if (getIntent().getExtras() != null) {
//            if (chosenContacts == null)
//                chosenContacts = new HashMap<>();

            chosenContacts = (HashMap<String, String>) getIntent().getSerializableExtra(CHOSEN_CONTACTS);
        }

        ContactsFragment contactsFragment = ContactsFragment.newInstance(CHOOSING_MODE, chosenContacts);
        getSupportFragmentManager().beginTransaction().add(R.id.contacts_chooser_container, contactsFragment).commit();
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

    public void onItemClick(String s) {
        mLookupKey = s;
        getSupportLoaderManager().restartLoader(ContactsQuery.Phone.QUERY_ID, null, this);
    }

    public void onConfirmSelection() {
        if (chosenContacts == null)
            finish();

        final Intent intent = new Intent();
        intent.putExtra(CHOSEN_CONTACTS, chosenContacts);
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
                final String phoneNumberStr = String.valueOf(data.getString(ContactsQuery.Phone.PHONE_NUMBER));
                final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                final TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                final String ISO2 = BuildConfig.DEBUG ? getResources().getString(R.string.default_country) : manager.getSimCountryIso().toUpperCase();

                Phonenumber.PhoneNumber phoneNumberProto = new Phonenumber.PhoneNumber();

                try {
                    phoneNumberProto = phoneUtil.parse(phoneNumberStr, ISO2);
                } catch (NumberParseException e) {
                    Log.e(TAG, "NumberParseException was thrown: " + e.toString());
                }

                if (!phoneUtil.isValidNumber(phoneNumberProto))
                    return;

                final String phoneNumber = phoneUtil.format(phoneNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

                Log.d(TAG, phoneNumber);

                if (chosenContacts == null)
                    chosenContacts = new HashMap<>();

                if (chosenContacts.containsKey(mLookupKey))
                    chosenContacts.remove(mLookupKey);
                else
                    chosenContacts.put(mLookupKey, phoneNumber);

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nothing to do here. The Cursor does not need to be released as it was never directly
        // bound to anything (like an adapter).
    }


}
