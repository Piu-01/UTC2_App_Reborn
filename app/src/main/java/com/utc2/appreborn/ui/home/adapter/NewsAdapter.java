package com.utc2.appreborn.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.databinding.ItemNewsBinding;
import com.utc2.appreborn.ui.home.model.NewsItem;

/**
 * NewsAdapter
 * ──────────────────────────────────────────────────────────────
 * RecyclerView adapter for the Home-screen news feed.
 *
 * Extends {@link ListAdapter} which:
 *  • Runs DiffUtil on a background thread automatically.
 *  • Delivers animated item changes when new data arrives.
 *  • Exposes {@link #submitList(java.util.List)} — call it from
 *    the LiveData observer in HomeFragment.
 *
 * Uses View Binding (ItemNewsBinding) — no more findViewById().
 *
 * Package: com.utc2.appreborn.ui.home.adapter
 */
public class NewsAdapter extends ListAdapter<NewsItem, NewsAdapter.ViewHolder> {

    // ── Listener interface ────────────────────────────────────
    public interface OnNewsClickListener {
        void onNewsClick(NewsItem item);
    }

    private final OnNewsClickListener listener;

    // ── DiffUtil callback ─────────────────────────────────────
    private static final DiffUtil.ItemCallback<NewsItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<NewsItem>() {

                @Override
                public boolean areItemsTheSame(@NonNull NewsItem oldItem,
                                               @NonNull NewsItem newItem) {
                    // Items are the same entity if IDs match
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull NewsItem oldItem,
                                                  @NonNull NewsItem newItem) {
                    // Full equality check for change animations
                    return oldItem.getTitle().equals(newItem.getTitle())
                            && oldItem.getDate().equals(newItem.getDate())
                            && oldItem.getSummary().equals(newItem.getSummary());
                }
            };

    // ── Constructor ───────────────────────────────────────────
    public NewsAdapter(@NonNull OnNewsClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    // ═══════════════════════════════════════════════════════════
    //  RecyclerView.Adapter overrides
    // ═══════════════════════════════════════════════════════════

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNewsBinding itemBinding = ItemNewsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    // ═══════════════════════════════════════════════════════════
    //  ViewHolder
    // ═══════════════════════════════════════════════════════════

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemNewsBinding b; // binding alias for brevity

        ViewHolder(@NonNull ItemNewsBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(@NonNull NewsItem item) {
            b.tvNewsTitle.setText(item.getTitle());
            b.tvNewsDate.setText(item.getDate());
            b.getRoot().setOnClickListener(v -> listener.onNewsClick(item));
        }
    }
}