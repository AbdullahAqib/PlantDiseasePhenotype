package com.example.plantdiseasephenotype;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateEmailDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    EditText txt_email, txt_password;
    Button btn_update;

    public UpdateEmailDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_email_dialog);

        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        btn_update = findViewById(R.id.btn_update_email);

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        btn_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_email:
                updateEmail();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void updateEmail() {

        final String email = txt_email.getText().toString();
        String password = txt_password.getText().toString();

        txt_email.clearFocus();
        txt_password.clearFocus();

        if (email.isEmpty()) {
            txt_email.setError(c.getString(R.string.input_error_email));
            txt_email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txt_email.setError(c.getString(R.string.input_error_email_invalid));
            txt_email.requestFocus();
            return;
        }

        btn_update.setEnabled(false);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    updateUserInformation();
                                    dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(c, "Something wents wrong. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Toast.makeText(c, "Authentication Failed", Toast.LENGTH_LONG).show();
                        }
                        btn_update.setEnabled(true);
                    }
                });

    }

    private void updateUserInformation() {

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email")
                .setValue(txt_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(c, "Email Updated Successfully", Toast.LENGTH_LONG).show();
                    c.startActivity(new Intent(c, ProfileActivity.class));
                } else {
                    Toast.makeText(c, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
