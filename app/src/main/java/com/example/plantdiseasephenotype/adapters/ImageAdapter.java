package com.example.plantdiseasephenotype.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantdiseasephenotype.activities.ImageDetailActivity;
import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.utils.Upload;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Upload> mUploads;
    public ImageAdapter(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.title.setText(uploadCurrent.getTitle());
        if(uploadCurrent.getDescription() == null){
            holder.description.setVisibility(View.GONE);
        }else{
            holder.description.setText(uploadCurrent.getDescription());
        }

        if(uploadCurrent.getCommentCount()==-1){
            holder.commentCount.setVisibility(View.GONE);
            holder.removeClickListener();
            holder.changeColors();
            }else {
            holder.commentCount.setText(String.valueOf(uploadCurrent.getCommentCount()) + " Comments");
        }
        holder.timestamp.setText(uploadCurrent.getUploadDate());
        Glide.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .into(holder.image);
    }
    @Override
    public int getItemCount() {
        return mUploads.size();
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title, description, timestamp, commentCount;
        public ImageView image;
        public ImageViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.uploaded_image);
            title = itemView.findViewById(R.id.image_title);
            description = itemView.findViewById(R.id.image_description);
            commentCount = itemView.findViewById(R.id.comment_count);
            timestamp = itemView.findViewById(R.id.timestamp);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            Upload upload = mUploads.get(getAdapterPosition());
            Intent intent = new Intent(mContext, ImageDetailActivity.class);
            intent.putExtra("Upload", upload);
            mContext.startActivity(intent);
        }

        public void removeClickListener(){
            itemView.setOnClickListener(null);
        }

        public void changeColors(){
            itemView.setBackgroundColor(Color.parseColor("#b5bbbd"));
        }
    }
}
