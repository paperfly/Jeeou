package com.paperfly.instantjio;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.paperfly.instantjio.group.User;
import com.paperfly.instantjio.util.TokenGenerator;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    public static final String TAG = LoginFragment.class.getCanonicalName();
    // Views
    private EditText vPhoneNumber;
    private EditText vUserName;
    private EditText vRegionCode;
    private Button vActionLogin;
    // Members
    private Map<String, Object> mAuth;
    private Firebase mRef;
    private Firebase.AuthResultHandler mAuthResultHandler;
    private OnLoginListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoginListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);

        vPhoneNumber = (EditText) view.findViewById(R.id.phone_number);
        vUserName = (EditText) view.findViewById(R.id.user_name);
        vRegionCode = (EditText) view.findViewById(R.id.region_code);
        vActionLogin = (Button) view.findViewById(R.id.action_login);

        mAuth = new HashMap<>();
        initFirebase();

        vActionLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        if (BuildConfig.DEBUG) {
            vRegionCode.setText(getString(R.string.default_country));
        }

        return view;
    }

    private void initFirebase() {
        mRef = new Firebase(getString(R.string.firebase_url));
        mAuthResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authentication just completed successfully :)
                saveUser();
            }

            @Override
            public void onAuthenticationError(FirebaseError error) {
                // Something went wrong :(
                switch (error.getCode()) {
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
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message)
                .setTitle(title);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void login() {
        mListener.login();
    }

    private void saveUser() {
        final Firebase ref = mRef.child("users").child(mRef.getAuth().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    User user = new User();
                    user.setName(mAuth.get("userName").toString());
                    user.setRegionCode(mAuth.get("regionCode").toString());
                    ref.setValue(user);

                    login();
                } else {
                    if (!BuildConfig.DEBUG) {
                        showAlert("Phone number used", "It seems that the supplied phone number " +
                                "is already in use");
                        ref.unauth();
                    } else {
                        login();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, firebaseError.getMessage());
            }
        });
    }

    private void attemptLogin() {
        if (vPhoneNumber.getText().toString().trim().length() > 0 &&
                vRegionCode.getText().toString().trim().length() > 0 &&
                vUserName.getText().toString().trim().length() > 0) {
            String phoneNumber = parsePhoneNumber();

            if (phoneNumber.trim().length() > 0) {
                mRef.authWithCustomToken(getToken(phoneNumber), mAuthResultHandler);
            } else {
                showAlert("Invalid phone number", "The phone number provided does not seem to be " +
                        "valid");
            }
        } else {
            showAlert("Fields not filled in", "All fields must be filled in to proceed");
        }
    }

    private String getToken(String phoneNumber) {
        // TODO Remove JWT token generator when publishing app
        // Proper way is to generate a token on our own server and serve it to clients
        TokenGenerator tokenGenerator = new TokenGenerator("V3JuTN4Ne5siDkvydx1gNJGQWmx1EwtcXMyxQC5O");
        mAuth.put("uid", phoneNumber);
        mAuth.put("regionCode", vRegionCode.getText().toString().substring(0, 2));
        mAuth.put("userName", vUserName.getText().toString());
        return tokenGenerator.createToken(mAuth);
    }

    private String parsePhoneNumber() {
        String phoneNumberStr = vPhoneNumber.getText().toString();
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        // TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Activity.TELEPHONY_SERVICE);
        String ISO2 = vRegionCode.getText().toString().substring(0, 2); // manager.getSimCountryIso().toUpperCase()
        Phonenumber.PhoneNumber phoneNumberProto = new Phonenumber.PhoneNumber();

        try {
            phoneNumberProto = phoneUtil.parse(phoneNumberStr, ISO2);
        } catch (NumberParseException e) {
            Log.e(TAG, "NumberParseException was thrown: " + e.getMessage());
        }

        if (phoneUtil.isValidNumber(phoneNumberProto)) {
            return phoneUtil.format(phoneNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } else {
            return "";
        }
    }

    public interface OnLoginListener {
        void login();
    }
}
