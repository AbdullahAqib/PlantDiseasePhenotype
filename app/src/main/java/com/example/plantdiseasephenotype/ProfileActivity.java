package com.example.plantdiseasephenotype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    TextView txt_name, txt_email, txt_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txt_name = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);
        txt_phone = findViewById(R.id.txt_phone);

        findViewById(R.id.txt_logout).setOnClickListener(this);
        findViewById(R.id.update_information).setOnClickListener(this);
        findViewById(R.id.deactivate).setOnClickListener(this);

        BottomNavigationView navbar = findViewById(R.id.navbar);
        navbar.setSelectedItemId(R.id.nav_profile);
        navbar.setOnNavigationItemSelectedListener(this);

        loadUserInformation();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.update_information:
                startActivity(new Intent(getApplicationContext(), UpdateProfileActivity.class));
                break;
            case R.id.deactivate:
                deactivateAccount();
                break;
        }
    }

    private void deactivateAccount() {

        DeactivateAccountDialog dialog = new DeactivateAccountDialog(ProfileActivity.this);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    void loadUserInformation() {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user;
                        user = dataSnapshot.getValue(User.class);
                        txt_name.setText(user.name);
                        txt_email.setText(user.email);
                        txt_phone.setText(user.phone);
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
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
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