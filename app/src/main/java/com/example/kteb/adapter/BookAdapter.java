package com.example.kteb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.kteb.R;
import com.example.kteb.model.Book;
import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> books = new ArrayList<>();
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BookAdapter(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        
        String status = book.getStatus();
        holder.status.setText(getStatusText(status));
        holder.status.setBackgroundResource(getStatusBackground(status));
        
        String coverUrl = book.getCoverUrl();
        android.util.Log.d("BookAdapter", "Loading cover: " + coverUrl);
        if (coverUrl != null && !coverUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(coverUrl)
                    .error(R.drawable.ic_launcher_foreground)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.cover);
        } else {
            holder.cover.setImageResource(R.drawable.ic_launcher_foreground);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });
    }

    private String getStatusText(String status) {
        switch (status) {
            case "wantToRead": return "Want to Read";
            case "reading": return "Reading";
            case "completed": return "Done";
            default: return status;
        }
    }

    private int getStatusBackground(String status) {
        switch (status) {
            case "wantToRead": return R.color.xpgreen;
            case "reading": return R.color.xpblue;
            case "completed": return R.color.purple;
            default: return R.color.xpgreen;
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView author;
        TextView status;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.book_cover);
            title = itemView.findViewById(R.id.book_title);
            author = itemView.findViewById(R.id.book_author);
            status = itemView.findViewById(R.id.book_status);
        }
    }
}