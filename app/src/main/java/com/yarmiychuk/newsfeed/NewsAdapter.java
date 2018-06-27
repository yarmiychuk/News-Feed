package com.yarmiychuk.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DmitryYarmiychuk on 24.06.2018.
 * Создал DmitryYarmiychuk 24.06.2018
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context context;
    private List<NewsItem> news;

    // Constructor
    NewsAdapter(Context context, List<NewsItem> news) {
        this.context = context;
        this.news = news;
    }

    // Create new view
    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Fill view with sight data
     *
     * @param holder   - ViewHolder to filling
     * @param position - current position of ViewHolder in adapter
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // Get News for current adapter position
        NewsItem currentNews = news.get(position);
        // Date of publication
        String date = currentNews.getDate();
        if (date != null) {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(currentNews.getDate());
        } else {
            holder.tvDate.setVisibility(View.GONE);
        }
        // Tite
        holder.tvTitle.setText(currentNews.getTitle());
        // Short text of publication
        holder.tvDescription.setText(currentNews.getDescription());
        // Author of publication
        String author = currentNews.getAuthor();
        if (author != null) {
            holder.tvAuthor.setVisibility(View.VISIBLE);
            holder.tvAuthor.setText(currentNews.getAuthor());
        } else {
            holder.tvAuthor.setVisibility(View.GONE);
        }
        // OnClickListener for open link to publication in browser
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open material in browser
                int index = holder.getAdapterPosition();
                Uri webUri = Uri.parse(news.get(index).getWebLink());
                context.startActivity(new Intent(Intent.ACTION_VIEW, webUri));
            }
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public void setNewDataSet(List<NewsItem> news) {
        this.news = news;
        notifyDataSetChanged();
    }

    public void clearData() {
        news.clear();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for this adapter
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardItem;
        private TextView tvDate, tvTitle, tvDescription, tvAuthor;

        private ViewHolder(View view) {
            super(view);
            this.cardItem = view.findViewById(R.id.card_item);
            this.tvDate = view.findViewById(R.id.tv_date);
            this.tvTitle = view.findViewById(R.id.tv_title);
            this.tvDescription = view.findViewById(R.id.tv_description);
            this.tvAuthor = view.findViewById(R.id.tv_author);
        }
    }
}
