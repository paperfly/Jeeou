package com.paperfly.instantjio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.paperfly.instantjio.contacts.ContactsFragment;
import com.paperfly.instantjio.events.EventsFragment;
import com.paperfly.instantjio.groups.GroupCreateActivity;
import com.paperfly.instantjio.groups.GroupsFragment;
import com.paperfly.instantjio.groups.User;
import com.paperfly.instantjio.login.FacebookLoginFragment;
import com.paperfly.instantjio.login.GoogleLoginFragment;
import com.paperfly.instantjio.util.TokenGenerator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        FacebookLoginFragment.OnFacebookLoginListener,
        GoogleLoginFragment.OnGoogleLoginListener,
        ViewPager.OnPageChangeListener {

    private static final String TAG = MainActivity.class.getCanonicalName();
    Map<String, Object> auth = new HashMap<>();
    private Firebase ref;
    private Firebase.AuthStateListener authStateListener;
    private Firebase.AuthResultHandler authResultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.appbar).setEnabled(false);
        findViewById(R.id.container_main).setEnabled(false);
        findViewById(R.id.container_login).setEnabled(false);
        findViewById(R.id.container_signup).setEnabled(false);
        findViewById(R.id.fab).setEnabled(false);

        initFirebase();
        initToolbar();
        initViewPagerAndTabs();
//        initFAB();


    }

    private void initPhonePromptLayout() {
        findViewById(R.id.container_login).setEnabled(false);
        findViewById(R.id.container_signup).setEnabled(true);
        findViewById(R.id.container_login).setVisibility(View.GONE);
        findViewById(R.id.container_signup).setVisibility(View.VISIBLE);
        findViewById(R.id.button_phone_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editPhoneNum = (EditText) findViewById(R.id.edit_phone_number);

                final String phoneNumberStr = editPhoneNum.getText().toString();
                Log.d(TAG, "%%%%%%%%% " + phoneNumberStr);
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                final String ISO2 = BuildConfig.DEBUG ? getResources().getString(R.string.default_country) : manager.getSimCountryIso().toUpperCase();
                Log.d(TAG, "initPhonePromptLayout(): " + ISO2);
                Phonenumber.PhoneNumber phoneNumberProto = new Phonenumber.PhoneNumber();
                try {
                    phoneNumberProto = phoneUtil.parse(phoneNumberStr, ISO2);
                } catch (NumberParseException e) {
                    Log.e(TAG, "NumberParseException was thrown: " + e.toString());
                }

                if (!phoneUtil.isValidNumber(phoneNumberProto))
                    return;

                final String phoneNumber = phoneUtil.format(phoneNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                Log.d(TAG, "initPhonePromptLayout(): " + phoneNumber);

                auth.put("uid", phoneNumber);
                auth.put("regionCode", ISO2);

                // TODO Remove JWT token generator when publishing app
                TokenGenerator tokenGenerator = new TokenGenerator("V3JuTN4Ne5siDkvydx1gNJGQWmx1EwtcXMyxQC5O");
                String token = tokenGenerator.createToken(auth);

                ref.authWithCustomToken(token, authResultHandler);


//                ref.child("users").child(ref.getAuth().getUid()).child("phoneNumber").setValue(phoneNumber, new Firebase.CompletionListener() {
//                    @Override
//                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
//                        if (firebaseError != null) {
//                            Log.e(TAG, "Data could not be saved. " + firebaseError.getMessage());
//                        } else {
//                            Log.d(TAG, "Data saved successfully.");
//
//                            if (editPhoneNum.isFocused()) {
//                                editPhoneNum.clearFocus();
//                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(editPhoneNum.getWindowToken(), 0);
//                            }
//
//                            findViewById(R.id.fab).setEnabled(true);
//                            findViewById(R.id.appbar).setEnabled(true);
//                            findViewById(R.id.container_main).setEnabled(true);
//                            findViewById(R.id.container_signup).setEnabled(false);
//                            findViewById(R.id.container_signup).setVisibility(View.GONE);
//                            findViewById(R.id.container_main).setVisibility(View.VISIBLE);
//                            findViewById(R.id.appbar).setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
            }
        });
    }

    private void initFirebase() {
        ref = new Firebase(getResources().getString(R.string.firebase_url));

        authStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
//                    findViewById(R.id.container_login).setEnabled(false);
//                    findViewById(R.id.container_login).setVisibility(View.GONE);

//                    ref.child("users").child(authData.getUid()).child("phoneNumber").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (!dataSnapshot.exists()) {
//                                findViewById(R.id.fab).setEnabled(false);
//                                findViewById(R.id.appbar).setEnabled(false);
//                                findViewById(R.id.container_main).setEnabled(false);
//                                findViewById(R.id.container_signup).setEnabled(true);
//                                findViewById(R.id.container_signup).setVisibility(View.VISIBLE);
//                                findViewById(R.id.container_main).setVisibility(View.GONE);
//                                findViewById(R.id.appbar).setVisibility(View.GONE);
//                            } else {
//                                findViewById(R.id.fab).setEnabled(true);
//                                findViewById(R.id.appbar).setEnabled(true);
//                                findViewById(R.id.container_main).setEnabled(true);
//                                findViewById(R.id.container_signup).setEnabled(false);
//                                findViewById(R.id.container_signup).setVisibility(View.GONE);
//                                findViewById(R.id.container_main).setVisibility(View.VISIBLE);
//                                findViewById(R.id.appbar).setVisibility(View.VISIBLE);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(FirebaseError firebaseError) {
//
//                        }
//                    });


                    findViewById(R.id.appbar).setEnabled(true);
                    findViewById(R.id.container_main).setEnabled(true);
                    findViewById(R.id.fab).setEnabled(true);
                    findViewById(R.id.container_login).setEnabled(false);
                    findViewById(R.id.container_signup).setEnabled(false);
                    findViewById(R.id.appbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.container_main).setVisibility(View.VISIBLE);
                    findViewById(R.id.fab).setVisibility(View.GONE);
                    findViewById(R.id.container_login).setVisibility(View.GONE);
                    findViewById(R.id.container_signup).setVisibility(View.GONE);
                } else {
                    // user is not logged in
                    findViewById(R.id.fab).setEnabled(false);
                    findViewById(R.id.appbar).setEnabled(false);
                    findViewById(R.id.container_main).setEnabled(false);
                    findViewById(R.id.container_login).setEnabled(true);
                    findViewById(R.id.container_signup).setEnabled(false);
                    findViewById(R.id.appbar).setVisibility(View.GONE);
                    findViewById(R.id.container_main).setVisibility(View.GONE);
                    findViewById(R.id.fab).setVisibility(View.GONE);
                    findViewById(R.id.container_login).setVisibility(View.VISIBLE);
                    findViewById(R.id.container_signup).setVisibility(View.GONE);
//                    Toast.makeText(getApplicationContext(), "Logged out from Firebase", Toast.LENGTH_SHORT).show();
                }
            }
        };

        authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(final AuthData authData) {
                // Authenticated successfully with payload authData
                // Authentication just completed successfully :)


                ref.child("users").child(authData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
//                            final User user = new User();

//                            final AuthData authData = ref.getAuth();
//                            final String name =
//                                    authData.getProviderData().containsKey("displayName") ?
//                                            authData.getProviderData().get("displayName").toString() : null;
//                            final String provider = authData.getProviders();
//
//                            user.setName(name);
//                            user.setProvider(provider);
//                            user.setRegionCode(ISO2);

                            final User user = new User();
                            user.setName(auth.get("name").toString());
                            user.setEmail((auth.get("email").toString()));
                            user.setRegionCode((auth.get("regionCode").toString()));

                            if (auth.containsKey("facebook")) {
                                user.addProvider("facebook", auth.get("facebook").toString());
                            }
//                            else if (auth.containsKey("google")) {
                            // Handle google here
//                            }


                            ref.child("users").child(authData.getUid()).setValue(user, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if (firebaseError != null) {
                                        Log.e(TAG, "Data could not be saved. " + firebaseError.getMessage());
                                    } else {
                                        Log.d(TAG, "Data saved successfully.");

                                        if (findViewById(R.id.edit_phone_number).isFocused()) {
                                            findViewById(R.id.edit_phone_number).clearFocus();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(findViewById(R.id.edit_phone_number).getWindowToken(), 0);
                                        }

//                                        findViewById(R.id.fab).setEnabled(true);
//                                        findViewById(R.id.appbar).setEnabled(true);
//                                        findViewById(R.id.container_main).setEnabled(true);
//                                        findViewById(R.id.container_signup).setEnabled(false);
//                                        findViewById(R.id.container_signup).setVisibility(View.GONE);
//                                        findViewById(R.id.container_main).setVisibility(View.VISIBLE);
//                                        findViewById(R.id.appbar).setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


//                ref.child("users").child(authData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (!dataSnapshot.exists()) {
//                            User user = new User();
//                            user.setProvider(authData.getProviders());
//                            if (authData.getProviderData().containsKey("displayName")) {
//                                user.setName(authData.getProviderData().get("displayName").toString());
//                            }
//
//                            ref.child("users").child(authData.getUid()).setValue(user);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(FirebaseError firebaseError) {
//                        // ignore
//                    }
//                });


                Log.d(TAG, "Firebase authenticated");
//                Toast.makeText(getApplicationContext(), "Firebase authenticated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Something went wrong :(
                switch (firebaseError.getCode()) {
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        // handle a non existing user
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        // handle an invalid password
                        break;
                    default:
                        // handle other errors
                        break;
                }
            }
        };
        ref.addAuthStateListener(authStateListener);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initViewPagerAndTabs() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new GroupsFragment(), "Groups");
        mSectionsPagerAdapter.addFragment(new EventsFragment(), "Jio");
        mSectionsPagerAdapter.addFragment(new ContactsFragment(), "Contacts");

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container_main);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
    }

    private void setFABForGroups() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGroupCreateActivity();
            }
        });
    }

    private void setFABForEvents() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }

    private void setFABForContacts() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
    }

    private void startGroupCreateActivity() {
        Intent intent = new Intent(this, GroupCreateActivity.class);
        startActivity(intent);
    }

    public void onPageSelected(int position) {
        Log.d(TAG, String.valueOf(position));
        switch (position) {
            case 0:
                Log.d(TAG, "SETFABFORGROUPS");
                setFABForGroups();
                break;
            case 1:
                setFABForEvents();
                break;
            case 2:
                setFABForContacts();
                break;
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mFacebookAccessTokenTracker.stopTracking();
        ref.removeAuthStateListener(authStateListener);
//        if (ref.getAuth() != null)
//            mGoogleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == GoogleLoginFragment.RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
//            if (resultCode != RESULT_OK) {
//                mShouldResolve = false;
//            }
//
//            mIsResolving = false;
//            mGoogleApiClient.connect();
            getSupportFragmentManager().findFragmentById(R.id.google_fragment).onActivityResult(requestCode, resultCode, data);
        } else {
            getSupportFragmentManager().findFragmentById(R.id.facebook_fragment).onActivityResult(requestCode, resultCode, data);
//            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onFacebookCurrentAccessTokenChanged(AccessToken currentAccessToken) {
        // Set the access token using
        // currentAccessToken when it's loaded or set.
        if (currentAccessToken != null) {
//                    mAuthProgressDialog.show();

            Log.d(TAG, "onFacebookCurrentAccessTokenChanged");
            GraphRequest request = GraphRequest.newMeRequest(
                    currentAccessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.d(TAG, "GraphRequest.GraphJSONObjectCallback");
                            try {
                                auth.put("email", object.get("email"));
                                auth.put("facebook", object.get("id"));
                                auth.put("name", object.get("name"));

                                Log.d(TAG, object.get("email").toString());
                                Log.d(TAG, object.get("id").toString());
                                Log.d(TAG, "NAME: " + object.get("name").toString());
                            } catch (org.json.JSONException e) {
                                Log.e(TAG, "GraphRequest.GraphJSONObjectCallback JSONException");
                            }

                            if (response.getError() != null) {
                                Log.e(TAG, response.getError().getErrorMessage());
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email,name");
            request.setParameters(parameters);
            request.executeAsync();

            initPhonePromptLayout();
//            ref.authWithOAuthToken("facebook", currentAccessToken.getToken(), authResultHandler);
        } else {
            // Logged out of Facebook and currently authenticated with Firebase using Facebook, so do a logout
//            if (ref.getAuth() != null && ref.getAuth().getProviders().equals("facebook")) {
//                ref.unauth();
////                        setAuthenticatedUser(null);
//            }
        }
    }

    public void onGoogleCurrentAccessTokenChanged(String token) {
        ref.authWithOAuthToken("google", token, authResultHandler);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
