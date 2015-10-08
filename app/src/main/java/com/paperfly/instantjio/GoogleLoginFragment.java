package com.paperfly.instantjio;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleLoginFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /* Request code used to invoke sign in user interactions. */
    public static final int RC_SIGN_IN = 0;
    private static final String TAG = GoogleLoginFragment.class.getSimpleName();
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    private View root;
    private OnGoogleLoginListener mCallback;


    public GoogleLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_google_login, container, false);
        initGoogleAPI();
        return root;
    }

    private void initGoogleAPI() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        root.findViewById(R.id.google_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInClicked();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnGoogleLoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnGoogleLoginListener");
        }
        if (mCallback == null) {
            Log.e(TAG, "onAttach: mCallback IS null");
        } else {
            Log.e(TAG, "onAttach: mCallback NOT null");
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();

            // Show a message to the user that we are signing in.
            Toast.makeText(getContext(), "Signed in to Google", Toast.LENGTH_SHORT).show();
        }
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
                    connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
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
                Toast.makeText(getContext(), connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show the signed-out UI
//            showSignedOutUI();
            Toast.makeText(getContext(), "Failed to sign in to Google", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getContext(), "Signed in to Google", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // ignore
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != Activity.RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }


    public interface OnGoogleLoginListener {
        void onGoogleCurrentAccessTokenChanged(String token);
    }

    private class GetIdTokenTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            String scopes = "oauth2:" + Scopes.PROFILE + " " + Scopes.EMAIL;
            try {
                return GoogleAuthUtil.getToken(getContext(), account, scopes);
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
                mCallback.onGoogleCurrentAccessTokenChanged(result);
            } else {
                // There was some error getting the ID Token
                // ...
            }
        }
    }
}
