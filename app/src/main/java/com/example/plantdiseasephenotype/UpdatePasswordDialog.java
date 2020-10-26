package com.example.plantdiseasephenotype;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
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

public class UpdatePasswordDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    EditText txt_old_password, txt_new_password, txt_confirm_password;
    Button btn_update_password;

    public UpdatePasswordDialog(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_password_dialog);

        txt_old_password = findViewById(R.id.txt_old_password);
        txt_new_password = findViewById(R.id.txt_new_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        btn_update_password = findViewById(R.id.btn_update_password);

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        btn_update_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_password:
                updatePassword();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void updatePassword() {


        String oldPassword = txt_old_password.getText().toString();
        final String newPassword = txt_new_password.getText().toString();
        String confirmPassword = txt_confirm_password.getText().toString();

        txt_old_password.clearFocus();
        txt_new_password.clearFocus();
        txt_confirm_password.clearFocus();

        if (oldPassword.isEmpty()) {
            txt_old_password.setError("Old Password Required");
            txt_old_password.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            txt_old_password.setError("New Password Required");
            txt_old_password.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            txt_old_password.setError("You must need to confirm your new password");
            txt_old_password.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            txt_new_password.setError("Password length should be greater than 6");
            txt_new_password.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            txt_confirm_password.setError("Password mismatch");
            txt_confirm_password.requestFocus();
            return;
        }

        btn_update_password.setEnabled(false);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), oldPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(c, "Password Updated", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(c, "Something wents wrong. Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Toast.makeText(c, "Authentication Failed", Toast.LENGTH_LONG).show();
                        }
                        btn_update_password.setEnabled(true);
                    }
                });

    }
}
