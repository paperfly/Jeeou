package com.paperfly.instantjio.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.paperfly.instantjio.R;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookLoginFragment extends Fragment {

    private static final String TAG = FacebookLoginFragment.class.getCanonicalName();

    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    /* Used to track user logging in/out off Facebook */
    private AccessTokenTracker mAccessTokenTracker;

    private OnFacebookLoginListener mCallback;

    public FacebookLoginFragment() {
        // Required empty public constructor
    }

    private void initFacebookAPI(View view) {
        mLoginButton = (LoginButton) view.findViewById(R.id.facebook_login);
        mCallbackManager = CallbackManager.Factory.create();
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                Log.d(TAG, "Facebook.AccessTokenTracker.OnCurrentAccessTokenChanged");
//                // Set the access token using
//                // currentAccessToken when it's loaded or set.
//                if (currentAccessToken != null) {
////                    mAuthProgressDialog.show();
//                    ref.authWithOAuthToken("facebook", currentAccessToken.getToken(), authResultHandler);
//                } else {
//                    // Logged out of Facebook and currently authenticated with Firebase using Facebook, so do a logout
//                    if (ref.getAuth() != null && ref.getAuth().getProviders().equals("facebook")) {
//                        ref.unauth();
////                        setAuthenticatedUser(null);
//                    }
//                }
                mCallback.onFacebookCurrentAccessTokenChanged(currentAccessToken);
            }
        };
        mLoginButton.setReadPermissions(Arrays.asList("email"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFacebookLoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFacebookLoginListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_facebook_login, container, false);
        initFacebookAPI(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAccessTokenTracker.stopTracking();
    }

    public interface OnFacebookLoginListener {
        void onFacebookCurrentAccessTokenChanged(AccessToken currentAccessToken);
    }
}
