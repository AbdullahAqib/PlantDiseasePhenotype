package com.example.plantdiseasephenotype.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantdiseasephenotype.dialogs.ConfirmEmailDialog;
import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.utils.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txt_name, txt_email, txt_password, txt_confirm_password;
    Button btn_register;
    TextView login_now;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setBackgroundDrawableResource(R.mipmap.register_background);

        txt_name = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        btn_register = findViewById(R.id.btn_register);
        login_now = findViewById(R.id.login_now);
        progressBar = findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();

        login_now.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                registerUser();
                break;
            case R.id.login_now:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
    }

    private void registerUser() {
        final String name = txt_name.getText().toString().trim();
        final String email = txt_email.getText().toString().trim();
        String password = txt_password.getText().toString().trim();
        final String confirmPassword = txt_confirm_password.getText().toString().trim();

        if (name.isEmpty()) {
            txt_name.setError(getString(R.string.input_error_name));
            txt_name.requestFocus();
            return;
        }

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

        if (!password.equals(confirmPassword)) {
            txt_confirm_password.setError("Password mismatch");
            txt_confirm_password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btn_register.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            User user = new User(
                                    name,
                                    email
                            );

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    btn_register.setEnabled(true);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                        confirmEmail();
                                    } else {
                                        //display a failure message
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void confirmEmail() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           FirebaseAuth.getInstance().signOut();
                        }
                    }
                });

        ConfirmEmailDialog dialog = new ConfirmEmailDialog(RegisterActivity.this);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }
}

