package com.example.plantdiseasephenotype;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;

public class DeactivateAccountDialog extends Dialog implements
        android.view.View.OnClickListener{

    public Activity c;
    EditText txt_password;
    Button btn_deactivate;

    public DeactivateAccountDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.deactivate_account_dialog);

        txt_password = findViewById(R.id.txt_password);
        btn_deactivate = findViewById(R.id.btn_deactivate);

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        btn_deactivate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_deactivate:
                deactivateAccount();
                c.startActivity(new Intent(c, LoginActivity.class));
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void deactivateAccount() {

        String password = txt_password.getText().toString();

        btn_deactivate.setEnabled(false);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), password);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    deleteUserInformation();
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
                        btn_deactivate.setEnabled(true);
                    }
                });

    }

    private void deleteUserInformation() {

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(c, "Account Deactivated Successfully", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    c.startActivity(new Intent(getContext(), LoginActivity.class));
                } else {
                    Toast.makeText(c, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
