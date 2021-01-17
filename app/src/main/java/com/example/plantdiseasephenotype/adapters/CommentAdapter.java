package com.example.plantdiseasephenotype.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.plantdiseasephenotype.models.Comment;
import com.example.plantdiseasephenotype.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    private Context mContext;
    private List<Comment> mCommments;
    public CommentAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mCommments = comments;
    }
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.CommentViewHolder(v);
    }
    @Override
    public void onBindViewHolder(CommentAdapter.CommentViewHolder holder, int position) {
        Comment currentComment = mCommments.get(position);
        holder.userName.setText(currentComment.getUserName());
        holder.commentBody.setText(currentComment.getCommentBody());
        holder.timestamp.setText(currentComment.getUploadDate());
    }
    @Override
    public int getItemCount() {
        return mCommments.size();
    }
    public class CommentViewHolder extends RecyclerView.ViewHolder{
        public TextView userName, commentBody, timestamp;
        public CommentViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            commentBody = itemView.findViewById(R.id.comment_body);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }
}
