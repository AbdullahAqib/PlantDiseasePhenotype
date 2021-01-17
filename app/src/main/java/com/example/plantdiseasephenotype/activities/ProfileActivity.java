package com.example.plantdiseasephenotype.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.plantdiseasephenotype.dialogs.DeactivateAccountDialog;
import com.example.plantdiseasephenotype.dialogs.FeedbackDialog;
import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.dialogs.UpdateEmailDialog;
import com.example.plantdiseasephenotype.dialogs.UpdatePasswordDialog;
import com.example.plantdiseasephenotype.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    TextView txt_name, txt_email;
    Boolean loggedInwithEmail = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txt_name = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);

        loadUserInformation();

        for(UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()){
            if(user.getProviderId().equals("facebook.com") || user.getProviderId().equals("google.com") || user.getProviderId().equals("twitter.com")){
                loggedInwithEmail = false;
                if(user.getProviderId().equals("twitter.com")){
                    txt_email.setVisibility(View.GONE);
                }
            }
        }

        if(!loggedInwithEmail){
            findViewById(R.id.settings).setVisibility(View.GONE);
            findViewById(R.id.deactivate).setVisibility(View.GONE);
        }else{
            findViewById(R.id.update_public_profile).setOnClickListener(this);
            findViewById(R.id.update_password).setOnClickListener(this);
            findViewById(R.id.update_email).setOnClickListener(this);
            findViewById(R.id.deactivate).setOnClickListener(this);
        }

        findViewById(R.id.feedback).setOnClickListener(this);
        findViewById(R.id.history).setOnClickListener(this);
        findViewById(R.id.txt_logout).setOnClickListener(this);

//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        txt_name.setText(firebaseUser.getDisplayName());
//        txt_email.setText(firebaseUser.getEmail());

        BottomNavigationView navbar = findViewById(R.id.navbar);
        navbar.setSelectedItemId(R.id.nav_profile);
        navbar.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_public_profile:
                updatePublicProfile();
                break;
            case R.id.update_password:
                updatePassword();
                break;
            case R.id.update_email:
                updateEmail();
                break;
            case R.id.feedback:
                feedback();
                break;
            case R.id.history:
                history();
                break;
            case R.id.txt_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finishAffinity();
                break;
            case R.id.deactivate:
                deactivateAccount();
                break;
        }
    }

    private void updatePublicProfile(){
        Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
        startActivity(intent);
    }

    private void updatePassword() {

        UpdatePasswordDialog dialog = new UpdatePasswordDialog(ProfileActivity.this);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void feedback() {

        FeedbackDialog dialog = new FeedbackDialog(ProfileActivity.this);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void updateEmail() {

        UpdateEmailDialog dialog = new UpdateEmailDialog(ProfileActivity.this);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void deactivateAccount() {

        DeactivateAccountDialog dialog = new DeactivateAccountDialog(ProfileActivity.this);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void history(){
        Intent intent = new Intent(getApplicationContext(), ImagesActivity.class);
        intent.putExtra("id", R.id.history);
        startActivity(intent);
    }

    void loadUserInformation() {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user;
                        user = dataSnapshot.getValue(User.class);
                        txt_name.setText(user.getName());
                        txt_email.setText(user.getEmail());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_camera:
                startActivity(new Intent(getApplicationContext(), CameraXActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            case R.id.nav_prediction:
                startActivity(new Intent(getApplicationContext(), PredictionActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            case R.id.nav_blogs:
                startActivity(new Intent(getApplicationContext(), BlogActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            case R.id.nav_profile:
                return true;
        }
        return false;
    }
}
