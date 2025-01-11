package com.deluxedevelopment.whatsonmywatchlist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.deluxedevelopment.whatsonmywatchlist.R;
import com.deluxedevelopment.whatsonmywatchlist.model.WatchItem;
import java.util.ArrayList;
import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {
    private List<WatchItem> watchItems; // Full list
    private List<WatchItem> filteredWatchItems; // Filtered list
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(WatchItem item);
        void onItemLongClick(WatchItem item);
        void onStatusChange(WatchItem item, WatchItem.WatchStatus newStatus);
    }

    public WatchlistAdapter(OnItemClickListener listener) {
        this.watchItems = new ArrayList<>();
        this.filteredWatchItems = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_watchlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WatchItem item = filteredWatchItems.get(position); // Use filtered list
        holder.titleText.setText(item.getTitle());
        holder.typeText.setText(item.getType());
        holder.statusText.setText(item.getStatus().toString());

        holder.itemView.setOnClickListener(v ->
                listener.onItemClick(item));

        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return filteredWatchItems.size(); // Use filtered list
    }

    public void updateItems(List<WatchItem> items) {
        this.watchItems.clear();
        this.watchItems.addAll(items);
        this.filteredWatchItems.clear();
        this.filteredWatchItems.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(WatchItem item) {
        int position = filteredWatchItems.indexOf(item);
        if (position != -1) {
            filteredWatchItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, filteredWatchItems.size());
        }
        watchItems.remove(item); // Also update the full list
    }

    public void updateItem(WatchItem item) {
        int position = -1;
        for (int i = 0; i < filteredWatchItems.size(); i++) {
            if (filteredWatchItems.get(i).getId() == item.getId()) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            filteredWatchItems.set(position, item);
            notifyItemChanged(position);
        }

        // Update the full list
        int fullListPosition = -1;
        for (int i = 0; i < watchItems.size(); i++) {
            if (watchItems.get(i).getId() == item.getId()) {
                fullListPosition = i;
                break;
            }
        }
        if (fullListPosition != -1) {
            watchItems.set(fullListPosition, item);
        }
    }

    // Filter method
    public void filter(String query) {
        filteredWatchItems.clear();
        if (query.isEmpty()) {
            filteredWatchItems.addAll(watchItems);
        } else {
            for (WatchItem item : watchItems) {
                if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        item.getType().toLowerCase().contains(query.toLowerCase())) {
                    filteredWatchItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView typeText;
        TextView statusText;

        ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.text_title);
            typeText = itemView.findViewById(R.id.text_type);
            statusText = itemView.findViewById(R.id.text_status);
        }
    }
}
