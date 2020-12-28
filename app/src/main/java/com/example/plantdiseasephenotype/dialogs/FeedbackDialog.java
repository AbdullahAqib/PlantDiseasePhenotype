package com.example.plantdiseasephenotype.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.utils.Feedback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackDialog extends Dialog implements
        android.view.View.OnClickListener{

    public Activity c;
    EditText txt_feedback;
    Button btnSubmit;
    private DatabaseReference mDatabaseRef;

    public FeedbackDialog(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_feedback);

        txt_feedback = findViewById(R.id.txt_feedback);

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("feedbacks");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                submitFeedback();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void submitFeedback() {

        String txtFeedback = txt_feedback.getText().toString();

        txt_feedback.clearFocus();

        if (txtFeedback.isEmpty()) {
            txt_feedback.setError("Can't submit empty feedback.");
            txt_feedback.requestFocus();
            return;
        }

        btnSubmit.setEnabled(false);

        Feedback feedback = new Feedback(txtFeedback, FirebaseAuth.getInstance().getCurrentUser().getUid());

        String uploadId = mDatabaseRef.push().getKey();
        mDatabaseRef.child(uploadId).setValue(feedback);

        Toast.makeText(c, "Thanks for your feedback.", Toast.LENGTH_SHORT).show();

        dismiss();
    }
}
