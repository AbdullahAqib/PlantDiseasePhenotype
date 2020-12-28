package com.example.plantdiseasephenotype.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.plantdiseasephenotype.utils.Item;
import com.example.plantdiseasephenotype.R;

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

    public BlogAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
        allItems = new ArrayList<>(items);
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
                Intent intent = new Intent(context, DetailActivity.class);
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
