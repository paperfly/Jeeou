package com.paperfly.instantjio;

import android.accounts.Account;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.paperfly.instantjio.contacts.ContactsFragment;
import com.paperfly.instantjio.events.EventsFragment;
import com.paperfly.instantjio.groups.GroupsFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private CallbackManager mFacebookCallbackManager;
    /* Used to track user logging in/out off Facebook */
    private AccessTokenTracker mFacebookAccessTokenTracker;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private Firebase ref;
    private Firebase.AuthStateListener authStateListener;
    private Firebase.AuthResultHandler authResultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
        initGoogleAPI();
        initFacebookAPI();
        initToolbar();
        initViewPagerAndTabs();
//        initFAB();
    }

    private void initFirebase() {
        ref = new Firebase("https://popping-inferno-4928.firebaseio.com/");

        authStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    findViewById(R.id.container_main).setVisibility(View.VISIBLE);
                    findViewById(R.id.appbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.container_login).setVisibility(View.GONE);
                } else {
                    // user is not logged in
                    findViewById(R.id.container_login).setVisibility(View.VISIBLE);
                    findViewById(R.id.container_main).setVisibility(View.GONE);
                    findViewById(R.id.appbar).setVisibility(View.GONE);
//                    Toast.makeText(getApplicationContext(), "Logged out from Firebase", Toast.LENGTH_SHORT).show();
                }
            }
        };

        authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData
                Toast.makeText(getApplicationContext(), "Firebase authenticated", Toast.LENGTH_SHORT).show();
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

    private void initFacebookAPI() {
        mFacebookCallbackManager = CallbackManager.Factory.create();
//        mFacebookAccessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                Log.i(TAG, "Facebook.AccessTokenTracker.OnCurrentAccessTokenChanged");
//                MainActivity.this.onFacebookAccessTokenChange(currentAccessToken);
//            }
//        };
        mFacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                Log.i(TAG, "Facebook.AccessTokenTracker.OnCurrentAccessTokenChanged");
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
        };
        // If the access token is available already assign it.
//        mFacebookAccessTokenTracker = AccessToken.getCurrentAccessToken();
    }

    private void initGoogleAPI() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        findViewById(R.id.google_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInClicked();
            }
        });
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
    protected void onStart() {
        super.onStart();
//        ref.addAuthStateListener(authStateListener);
//        if (ref.getAuth() == null)
//            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        ref.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFacebookAccessTokenTracker.stopTracking();
        ref.removeAuthStateListener(authStateListener);
//        if (ref.getAuth() != null)
//            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
//                showErrorDialog(connectionResult);
                Toast.makeText(getApplicationContext(), connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show the signed-out UI
//            showSignedOutUI();
            Toast.makeText(getApplicationContext(), "Failed to sign in to Google", Toast.LENGTH_SHORT).show();
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();

            // Show a message to the user that we are signing in.
            Toast.makeText(getApplicationContext(), "Signed in to Google", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        } else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;
        GetIdTokenTask task = new GetIdTokenTask();
        task.execute();

        // Show the signed-in UI
//        showSignedInUI();
        Toast.makeText(getApplicationContext(), "Signed in to Google", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // ignore
    }

    private void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        ref.unauth();

        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            Toast.makeText(getApplicationContext(), "Signed out from Google", Toast.LENGTH_SHORT).show();
        }
//        showSignedOutUI();
    }

    private class GetIdTokenTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            String scopes = "oauth2:" + Scopes.PROFILE + " " + Scopes.EMAIL;
            try {
                return GoogleAuthUtil.getToken(getApplicationContext(), account, scopes);
            } catch (UserRecoverableAuthException e) {
                if (!mIsResolving) {
                    mIsResolving = true;
                    startActivityForResult(e.getIntent(), RC_SIGN_IN);
                }
                return null;
            } catch (IOException e) {
                Log.e(TAG, "Error retrieving ID token.", e);
                return null;
            } catch (GoogleAuthException e) {
                Log.e(TAG, "Error retrieving ID token.", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "ID token: " + result);
            if (result != null) {
                // Successfully retrieved ID Token
                // ...
//                if (ref.getAuth() == null)
                ref.authWithOAuthToken("google", result, authResultHandler);
            } else {
                // There was some error getting the ID Token
                // ...
            }
        }
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
