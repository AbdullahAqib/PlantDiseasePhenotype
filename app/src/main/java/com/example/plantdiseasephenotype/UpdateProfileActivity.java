package com.example.plantdiseasephenotype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener{

    EditText txt_name, txt_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        txt_name = findViewById(R.id.txt_name);
        txt_phone = findViewById(R.id.txt_phone);

        loadUserInformation();

        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.update_password).setOnClickListener(this);
        findViewById(R.id.update_email).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_update:
                updateUserInformation();
                break;
            case R.id.update_password:
                updatePassword();
                break;
            case R.id.update_email:
                updateEmail();
                break;
        }
    }

    private void updatePassword() {

        UpdatePasswordDialog dialog = new UpdatePasswordDialog(UpdateProfileActivity.this);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }


    private void updateEmail() {

        UpdateEmailDialog dialog = new UpdateEmailDialog(UpdateProfileActivity.this);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

//    private void updatePassword(){
//
//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//
//        final EditText old_password = new EditText(this);
//        old_password.setGravity(Gravity.CENTER);
//        old_password.setHint("Old Password");
//        old_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//        layout.addView(old_password);
//
//
//        final EditText new_password = new EditText(this);
//        new_password.setGravity(Gravity.CENTER);
//        new_password.setHint("New Password");
//        new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//        layout.addView(new_password);
//
//        AlertDialog alertDialog = new AlertDialog.Builder(this)
//                .setView(layout)
//                .setTitle("Change Password")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String oldPassword = old_password.getText().toString();
//                        final String newPassword = new_password.getText().toString();
//                        if(newPassword.length() > 6){
//                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                            AuthCredential credential = EmailAuthProvider
//                                    .getCredential(user.getEmail(), oldPassword);
//                            user.reauthenticate(credential)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_LONG).show();
//                                                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(getApplicationContext(), "Something wents wrong. Please try again later.", Toast.LENGTH_LONG).show();
//                                                }
//                                            });
//                                        }
//                                    });
//
//                        }else{
//                            Toast.makeText(getApplicationContext(), "Please make sure you have entered the password correctly.", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                })
//                .show();
//
//    }

    private void updateUserInformation() {

        User user = new User(
                txt_name.getText().toString(),
                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                txt_phone.getText().toString()
        );

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UpdateProfileActivity.this, "Information Updated", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                } else {
                    Toast.makeText(UpdateProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    void loadUserInformation() {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        txt_name.setText(user.name);
                        txt_phone.setText(user.phone);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
