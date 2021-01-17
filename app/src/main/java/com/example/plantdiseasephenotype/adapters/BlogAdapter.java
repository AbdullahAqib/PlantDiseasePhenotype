package com.example.plantdiseasephenotype.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantdiseasephenotype.activities.DetailActivity;
import com.example.plantdiseasephenotype.models.Item;
import com.example.plantdiseasephenotype.R;
import com.example.plantdiseasephenotype.models.NewsArticle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import static com.example.plantdiseasephenotype.R.id.*;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogItemViewHolder> implements Filterable {

    private Context context;
    private List<Item> items;
    private List<Item> allItems;
    private List<NewsArticle> newsArticles;
    private Boolean isNews=false;

    public BlogAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
        allItems = new ArrayList<>(items);
    }

    public BlogAdapter(Context context, List<NewsArticle> newsArticles, Boolean isNews) {
        Log.i("Msg","news blog constructor");
        this.context = context;
        this.newsArticles = newsArticles;
        this.isNews = isNews;
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
        if(isNews){
            final NewsArticle newsArticle = newsArticles.get(position);
            if(!TextUtils.isEmpty(newsArticle.getTitle())) {
                holder.blogTitle.setText(newsArticle.getTitle());
            }
            if(!TextUtils.isEmpty(newsArticle.getDescription())) {
                holder.blogDescription.setText(newsArticle.getDescription());
            }
            Glide.with(context).load(newsArticle.getUrlToImage()).into(holder.blogImage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("url", newsArticle.getUrl());
                    context.startActivity(intent);
                }
            });
        }else {
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
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("url", item.getUrl());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(isNews){
            return newsArticles.size();
        }
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

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allItems);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Item item : allItems) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items.clear();
            items.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
