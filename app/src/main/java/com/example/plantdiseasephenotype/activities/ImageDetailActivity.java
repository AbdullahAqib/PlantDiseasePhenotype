package com.example.plantdiseasephenotype.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.plantdiseasephenotype.utils.Comment;
import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.utils.Upload;
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
        findViewById(R.id.btn_submit).setOnClickListener(this);

        upload = (Upload) getIntent().getSerializableExtra("Upload");

        title.setText(upload.getTitle());
        if(upload.getDescription() == null){
            description.setVisibility(View.GONE);
        }else{
            description.setText(upload.getDescription());
        }
        Glide.with(this)
                .load(upload.getImageUrl())
                .into(image);


        mRecyclerView = findViewById(R.id.comment_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_bar);
        mComments = new ArrayList<>();
        mAdapter = new CommentAdapter(ImageDetailActivity.this, mComments);
        mRecyclerView.setAdapter(mAdapter);
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
                if(task.isSuccessful()){
                    comment.setText("");
                    upload.increaseCommentCount();
                    Toast.makeText(ImageDetailActivity.this, "Thankyou for your response!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ImageDetailActivity.this, "Something wents wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                }
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