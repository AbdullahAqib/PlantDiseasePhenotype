package com.example.plantdiseasephenotype.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.plantdiseasephenotype.network.FCM_API;
import com.example.plantdiseasephenotype.models.RequestNotification;
import com.example.plantdiseasephenotype.models.Comment;
import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.models.FCMDataModel;
import com.example.plantdiseasephenotype.models.FCMNotificationModel;
import com.example.plantdiseasephenotype.models.Upload;
import com.example.plantdiseasephenotype.adapters.CommentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Callback;

public class ImageDetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView title, description, comment;
    ImageView image;

    Upload upload;
    private ProgressBar mProgressCircle;
    private RecyclerView mRecyclerView;
    private CommentAdapter mAdapter;
    private List<Comment> mComments;

    DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        title = findViewById(R.id.txt_title);
        description = findViewById(R.id.txt_description);
        image = findViewById(R.id.image);
        comment = findViewById(R.id.txt_comment);
        mRecyclerView = findViewById(R.id.comment_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_bar);
        mProgressCircle.setVisibility(View.VISIBLE);
        mComments = new ArrayList<>();
        mAdapter = new CommentAdapter(ImageDetailActivity.this, mComments);
        mRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.btn_submit).setOnClickListener(this);

        if (!getIntent().hasExtra("postId")) {
            upload = (Upload) getIntent().getSerializableExtra("Upload");
            setImageDetails(upload);
        } else {
            String postId = getIntent().getStringExtra("postId");
            Log.i("Msg", "Intent Extra post id"+postId);
            FirebaseDatabase.getInstance().getReference().child("uploads")
                    .child(postId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i("Msg", dataSnapshot.toString());
                            upload = dataSnapshot.getValue(Upload.class);
                            upload.setKey(dataSnapshot.getKey());
                            setImageDetails(upload);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("Msg", "cancelled");
                        }
                    });
        }

    }

    void setImageDetails(Upload upload){

        mProgressCircle.setVisibility(View.GONE);

        title.setText(upload.getTitle());
        if (upload.getDescription() == null) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(upload.getDescription());
        }
        Glide.with(this)
                .load(upload.getImageUrl())
                .into(image);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child(upload.getKey()).child("comments");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mComments.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = postSnapshot.getValue(Comment.class);
                    mComments.add(comment);
                }
                mAdapter.notifyDataSetChanged();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ImageDetailActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }


    void addComment() {
        String commentBody = comment.getText().toString();

        if (commentBody.isEmpty()) {
            comment.setError("Please add some comment");
            comment.requestFocus();
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Comment newComment = new Comment(firebaseUser.getUid(), commentBody, firebaseUser.getDisplayName());
        String commentId = mDatabaseRef.push().getKey();
        mDatabaseRef.child(commentId).setValue(newComment);
        mDatabaseRef.child(commentId).child("timestamp").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    comment.setText("");
                    upload.increaseCommentCount();
                    Toast.makeText(ImageDetailActivity.this, "Thankyou for your response!", Toast.LENGTH_SHORT).show();
                    sendNotification(upload.getUserId());
                } else {
                    Toast.makeText(ImageDetailActivity.this, "Something wents wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendNotification(String autherId) {

        FCMNotificationModel notificationModel = new FCMNotificationModel("just received a response.", "Your Post '" + upload.getTitle() + "'");
        FCMDataModel dataModel = new FCMDataModel(upload.getKey());
        RequestNotification requestNotificaton = new RequestNotification();
        requestNotificaton.setNotificationModel(notificationModel);
        requestNotificaton.setDataModel(dataModel);
        requestNotificaton.setToken("/topics/" + autherId);

        FCM_API.FCMService apiService = FCM_API.getClient().create(FCM_API.FCMService.class);
        retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(requestNotificaton);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d("Msg", "Notification done!");
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                addComment();
                break;
        }
    }
}