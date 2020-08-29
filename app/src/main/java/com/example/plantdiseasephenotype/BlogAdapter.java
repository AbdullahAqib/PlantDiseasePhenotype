package com.example.plantdiseasephenotype;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.zip.Inflater;

import static com.example.plantdiseasephenotype.R.*;
import static com.example.plantdiseasephenotype.R.id.*;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogItemViewHolder> {

    private Context context;
    private List<Item> items;

    public BlogAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public BlogItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.blog_item, parent, false);
        return new BlogItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogItemViewHolder holder, int position) {
        final Item item = items.get(position);
        holder.blogTitle.setText(item.getTitle());
        Document document = Jsoup.parse(item.getContent());
        holder.blogDescription.setText(document.text());
        Element blogImage = document.select("img").first();
        String blogImageURL = blogImage.attr("src");
        Glide.with(context).load(blogImageURL).into(holder.blogImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DetailActivity.class);
                intent.putExtra("url", item.getUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class BlogItemViewHolder extends RecyclerView.ViewHolder{

        ImageView blogImage;
        TextView blogTitle, blogDescription;

        public BlogItemViewHolder(@NonNull View itemView) {
            super(itemView);
            blogImage = itemView.findViewById(blog_image);
            blogTitle = itemView.findViewById(R.id.blog_title);
            blogDescription = itemView.findViewById(blog_description);
        }
    }
}
