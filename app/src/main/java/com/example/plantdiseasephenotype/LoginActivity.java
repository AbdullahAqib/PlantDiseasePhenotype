package com.example.plantdiseasephenotype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import javax.security.auth.login.LoginException;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txt_email, txt_password;
    ProgressBar progressBar;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private OAuthProvider.Builder twitterProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setBackgroundDrawableResource(R.mipmap.login_background);

        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        progressBar = findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        twitterProvider = OAuthProvider.newBuilder("twitter.com");

        createRequest(); // google provider

        findViewById(R.id.register_now).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.twitter_login).setOnClickListener(this);
        findViewById(R.id.facebook_login).setOnClickListener(this);
        findViewById(R.id.google_login).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        switch (view.getId()) {
            case R.id.register_now:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;
            case R.id.btn_login:
                loginWithEmailAndPassword();
                break;
            case R.id.google_login:
                loginWithGoogle();
                break;
            case R.id.facebook_login:
                loginWithFacebook();
                break;
            case R.id.twitter_login:
                loginWithTwitter();
                break;
        }
    }

    private void loginWithTwitter() {
        /* logs in with twitter */
        mAuth
            .startActivityForSignInWithProvider(this, twitterProvider.build())
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    // we can't get email
                    // see: https://developer.twitter.com/en/docs/apps/app-permissions
                    User user = new User(
                            authResult.getUser().getDisplayName(),
                            ""
                    );
                    saveUserInDatabaseAndUpdateUI(user);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Authentication with twitter failed!", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loginWithFacebook() {

        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser facebokkUser = mAuth.getCurrentUser();
                            User user = new User(
                                    facebokkUser.getDisplayName(),
                                    facebokkUser.getEmail()
                            );
                            saveUserInDatabaseAndUpdateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserInDatabaseAndUpdateUI(User user) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startHomeActivity();
                } else {
                    //display a failure message
                }
            }
        });
    }

    private void loginWithEmailAndPassword() {
        String email = txt_email.getText().toString().trim();
        String password = txt_password.getText().toString().trim();

        if (email.isEmpty()) {
            txt_email.setError(getString(R.string.input_error_email));
            txt_email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txt_email.setError(getString(R.string.input_error_email_invalid));
            txt_email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            txt_password.setError(getString(R.string.input_error_password));
            txt_password.requestFocus();
            return;
        }

        if (password.length() < 6) {
            txt_password.setError(getString(R.string.input_error_password_length));
            txt_password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    if (checkEmailVerified(mAuth.getCurrentUser())) {
                        startHomeActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Email not verified. Please confirm your email first.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void loginWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser googleUser = mAuth.getCurrentUser();

                            User user = new User(
                                    googleUser.getDisplayName(),
                                    googleUser.getEmail()
                            );

                            saveUserInDatabaseAndUpdateUI(user);

                        } else {
                            Toast.makeText(getApplicationContext(), "Sorry auth failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String providerId = user.getProviderData().get(user.getProviderData().size() - 1).getProviderId();
            Log.i("LoginActivity", providerId);
            if (providerId.equals("twitter.com")) {
                startHomeActivity();
            }
        } else {
            if (checkEmailVerified(user)) {
                startHomeActivity();
            }
        }
    }

    private void startHomeActivity() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    private boolean checkEmailVerified(FirebaseUser user) {
        if (user != null) {
            if (user.isEmailVerified())
                return true;
            else
                mAuth.signOut();
        }
        return false;
    }

}
