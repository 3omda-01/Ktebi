package com.example.kteb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kteb.R;
import com.example.kteb.model.ReadingSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
    private List<ReadingSession> sessions = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public void setSessions(List<ReadingSession> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        ReadingSession session = sessions.get(position);
        holder.bookTitle.setText(session.getBookTitle());
        holder.date.setText(dateFormat.format(new Date(session.getStartTime())));
        holder.duration.setText(formatDuration(session.getDuration()));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m";
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle;
        TextView date;
        TextView duration;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.session_book_title);
            date = itemView.findViewById(R.id.session_date);
            duration = itemView.findViewById(R.id.session_duration);
        }
    }
}