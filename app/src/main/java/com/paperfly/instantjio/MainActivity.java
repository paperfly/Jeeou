package com.paperfly.instantjio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.paperfly.instantjio.contacts.ContactsFragment;
import com.paperfly.instantjio.events.EventsFragment;
import com.paperfly.instantjio.groups.GroupsFragment;
import com.paperfly.instantjio.login.FacebookLoginFragment;
import com.paperfly.instantjio.login.GoogleLoginFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        FacebookLoginFragment.OnFacebookLoginListener,
        GoogleLoginFragment.OnGoogleLoginListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Firebase ref;
    private Firebase.AuthStateListener authStateListener;
    private Firebase.AuthResultHandler authResultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
        initPhonePromptLayout();
        initToolbar();
        initViewPagerAndTabs();
//        initFAB();
    }

    private void initPhonePromptLayout() {
        findViewById(R.id.button_phone_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editPhoneNum = (EditText) findViewById(R.id.edit_phone_number);
                ref.child("users").child(ref.getAuth().getUid()).child("phoneNumber").setValue(editPhoneNum.getText().toString(), new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Log.e(TAG, "Data could not be saved. " + firebaseError.getMessage());
                        } else {
                            Log.d(TAG, "Data saved successfully.");
                            findViewById(R.id.container_signup).setVisibility(View.GONE);
                            findViewById(R.id.container_main).setVisibility(View.VISIBLE);
                            findViewById(R.id.appbar).setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void initFirebase() {
        ref = new Firebase(getResources().getString(R.string.firebase_url));

        authStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    findViewById(R.id.container_login).setVisibility(View.GONE);

                    ref.child("users").child(authData.getUid()).child("phoneNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                findViewById(R.id.container_signup).setVisibility(View.VISIBLE);
                                findViewById(R.id.container_main).setVisibility(View.GONE);
                                findViewById(R.id.appbar).setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.container_signup).setVisibility(View.GONE);
                                findViewById(R.id.container_main).setVisibility(View.VISIBLE);
                                findViewById(R.id.appbar).setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                } else {
                    // user is not logged in
                    findViewById(R.id.container_login).setVisibility(View.VISIBLE);
                    findViewById(R.id.container_main).setVisibility(View.GONE);
                    findViewById(R.id.appbar).setVisibility(View.GONE);
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
                            Map<String, String> map = new HashMap<>();
                            map.put("provider", authData.getProvider());
                            if (authData.getProviderData().containsKey("displayName")) {
                                map.put("displayName", authData.getProviderData().get("displayName").toString());
                            }
                            ref.child("users").child(authData.getUid()).setValue(map);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        // ignore
                    }
                });

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
        mSectionsPagerAdapter.addFragment(GroupsFragment.newInstance(), "Groups");
        mSectionsPagerAdapter.addFragment(EventsFragment.newInstance(), "Jio");
        mSectionsPagerAdapter.addFragment(ContactsFragment.newInstance(), "Contacts");

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container_main);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

//    private void initFAB() {
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }

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
            ref.authWithOAuthToken("facebook", currentAccessToken.getToken(), authResultHandler);
        } else {
            // Logged out of Facebook and currently authenticated with Firebase using Facebook, so do a logout
            if (ref.getAuth() != null && ref.getAuth().getProvider().equals("facebook")) {
                ref.unauth();
//                        setAuthenticatedUser(null);
            }
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
