package com.paperfly.instantjio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.io.ByteStreams;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.paperfly.instantjiocore.Model.User;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getCanonicalName();

    private EditText vPhoneNumber;
    private EditText vUserName;
    private EditText vRegionCode;
    private Button vActionLogin;


    private Map<String, Object> mAdditionalClaims;
    private FirebaseAuth mAuth;
//    private Firebase mRef;
//    private Firebase.AuthResultHandler mAuthResultHandler;

    private OnLoginListener mListener;

    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT >= 22) {
            view.setBackground(getResources().getDrawable(R.drawable.banana_bg_login, null));
        } else if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            view.setBackground(getResources().getDrawable(R.drawable.banana_bg_login));
        } else {
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.banana_bg_login));
        }

        vPhoneNumber = (EditText) view.findViewById(R.id.phone_number);
        vUserName = (EditText) view.findViewById(R.id.user_name);
        vRegionCode = (EditText) view.findViewById(R.id.region_code);
        vActionLogin = (Button) view.findViewById(R.id.action_login);

        mAdditionalClaims = new HashMap<>();
        initFirebase();

        vActionLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin();
                } catch (Exception e) {
                    Log.e(TAG, "Unable to login - " + e.getMessage());
                }
            }
        });

        if (BuildConfig.DEBUG) {
            vRegionCode.setText(getString(R.string.default_country));
        }

        return view;
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();

//        mRef = new Firebase(getString(R.string.firebase_url));
//        mAuthResultHandler = new Firebase.AuthResultHandler() {
//            @Override
//            public void onAuthenticated(AuthData authData) {
//                // Authentication just completed successfully :)
//                saveUser();
//            }
//
//            @Override
//            public void onAuthenticationError(FirebaseError error) {
//                // Something went wrong :(
//                switch (error.getCode()) {
//                    case FirebaseError.USER_DOES_NOT_EXIST:
//                        // handle a non existing user
//                        break;
//                    case FirebaseError.INVALID_PASSWORD:
//                        // handle an invalid password
//                        break;
//                    default:
//                        // handle other errors
//                        break;
//                }
//            }
//        };
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
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user != null ? user.getUid() : "";
        final DatabaseReference ref = userRef.child(userId);

        Log.d(TAG, "Saving user");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    User user = new User();
                    user.setName(mAdditionalClaims.get("userName").toString());
                    user.setRegionCode(mAdditionalClaims.get("regionCode").toString());
                    ref.setValue(user);

                    login();
                } else {
                    if (!BuildConfig.DEBUG) {
                        showAlert("Phone number used", "It seems that the supplied phone number " +
                                "is already in use");
//                        ref.unauth();
                        mAuth.signOut();
                    } else {
                        login();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e(TAG, firebaseError.getMessage());
            }
        });
    }

    private void attemptLogin() throws Exception {
        if (vPhoneNumber.getText().toString().trim().length() > 0 &&
                vRegionCode.getText().toString().trim().length() > 0 &&
                vUserName.getText().toString().trim().length() > 0) {
            String phoneNumber = parsePhoneNumber();

            if (phoneNumber.trim().length() > 0) {
                Log.d(TAG, "Attempting to login");
//                mRef.authWithCustomToken(getToken(phoneNumber), mAuthResultHandler);
                mAuth.signInWithCustomToken(createJWT(phoneNumber))
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    saveUser();
                                } else {
                                    Log.e(TAG, "Something went wrong when signing in - " + task.getException().getMessage());
                                }
                            }
                        });
            } else {
                showAlert("Invalid phone number", "The phone number provided does not seem to be " +
                        "valid");
            }
        } else {
            showAlert("Fields not filled in", "All fields must be filled in to proceed");
        }
    }

    private String createJWT(String uid) throws Exception {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillis = nowMillis + 360000;
        Date exp = new Date(expMillis);

        final String serviceAccountEmail = "firebase-adminsdk-44k77@popping-inferno-4928.iam.gserviceaccount.com";
        AssetManager assetManager = getContext().getAssets();
        InputStream inputStream = assetManager.open("pk2");
        byte[] keyBytes = ByteStreams.toByteArray(inputStream);

        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey key = kf.generatePrivate(spec);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setIssuer(serviceAccountEmail)
                .setSubject(serviceAccountEmail)
                .setAudience("https://identitytoolkit.googleapis.com/google.identity.identitytoolkit.v1.IdentityToolkit")
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("uid", uid)
                .signWith(SignatureAlgorithm.RS256, key);

        return jwtBuilder.compact();
    }

//    private String getToken(String phoneNumber) {
//        // TODO Remove JWT token generator when publishing app
//        // Proper way is to generate a token on our own server and serve it to clients
//        TokenGenerator tokenGenerator = new TokenGenerator("V3JuTN4Ne5siDkvydx1gNJGQWmx1EwtcXMyxQC5O");
//        mAdditionalClaims.put("uid", phoneNumber);
//        mAdditionalClaims.put("regionCode", vRegionCode.getText().toString().substring(0, 2));
//        mAdditionalClaims.put("userName", vUserName.getText().toString());
//        return tokenGenerator.createToken(mAdditionalClaims);
//    }

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
