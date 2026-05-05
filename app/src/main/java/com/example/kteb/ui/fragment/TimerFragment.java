package com.example.kteb.ui.fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.kteb.MainActivity;
import com.example.kteb.R;
import com.example.kteb.model.Book;
import com.example.kteb.model.DatabaseHelper;
import com.example.kteb.model.ReadingSession;
import com.example.kteb.util.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends Fragment {
    private TextView timerDisplay;
    private TextView startButton;
    private TextView stopButton;
    private TextView currentBookTitle;
    private TextView selectBookButton;
    private ImageView timerGif;
    private View rootLayout, headerBar;
    private boolean timerRunning = false;
    private long startTime = 0;
    private Handler handler = new Handler();
    private Book selectedBook = null;
    private List<Book> allBooks = new ArrayList<>();
    
    private static final String CHANNEL_ID = "kteb_timer";
    private static final int NOTIFICATION_ID = 1;
    
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (timerRunning) {
                long elapsed = System.currentTimeMillis() - startTime;
                timerDisplay.setText(formatTime(elapsed));
                updateNotification("Reading... " + formatTime(elapsed));
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        createNotificationChannel();
        
        timerDisplay = view.findViewById(R.id.timer_display);
        startButton = view.findViewById(R.id.btn_start_timer);
        stopButton = view.findViewById(R.id.btn_stop_timer);
        currentBookTitle = view.findViewById(R.id.current_book_title);
        selectBookButton = view.findViewById(R.id.btn_select_book);
        timerGif = view.findViewById(R.id.timer_gif);
        rootLayout = view.findViewById(R.id.root_layout);
        headerBar = view.findViewById(R.id.header_bar);
        applyTheme();

        Glide.with(this)
                .asGif()
                .load("file:///android_asset/time.gif")
                .into(timerGif);

        loadBooks();
        
        if (selectBookButton != null) {
            selectBookButton.setOnClickListener(v -> showBookSelector());
        }
        
        if (startButton != null) {
            startButton.setOnClickListener(v -> startTimer());
        }
        
        if (stopButton != null) {
            stopButton.setOnClickListener(v -> stopTimer());
            stopButton.setEnabled(false);
        }
    }
    
    private void loadBooks() {
        DatabaseHelper.getInstance().getBooks(new DatabaseHelper.OnBooksLoadedListener() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                allBooks.clear();
                if (books != null) {
                    allBooks.addAll(books);
                }
            }
        });
    }
    
    private void showBookSelector() {
        if (allBooks.isEmpty()) {
            Toast.makeText(getContext(), "No books in library", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] bookTitles = new String[allBooks.size()];
        for (int i = 0; i < allBooks.size(); i++) {
            bookTitles[i] = allBooks.get(i).getTitle();
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select a Book");
        builder.setItems(bookTitles, (dialog, which) -> {
            selectedBook = allBooks.get(which);
            if (currentBookTitle != null) {
                currentBookTitle.setText(selectedBook.getTitle());
            }
            Toast.makeText(getContext(), "Selected: " + selectedBook.getTitle(), Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
    
    private void startTimer() {
        timerRunning = true;
        startTime = System.currentTimeMillis();
        
        if (startButton != null) {
            startButton.setEnabled(false);
        }
        if (stopButton != null) {
            stopButton.setEnabled(true);
        }
        if (selectBookButton != null) {
            selectBookButton.setEnabled(false);
        }
        
        if (selectedBook != null && currentBookTitle != null) {
            currentBookTitle.setText("Reading: " + selectedBook.getTitle());
        }
        
        startForegroundNotification();
        handler.post(timerRunnable);
    }
    
    private void stopTimer() {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        if (selectedBook != null) {
            ReadingSession session = new ReadingSession(
                    selectedBook.getId(),
                    selectedBook.getTitle(),
                    startTime
            );
            session.setEndTime(endTime);
            session.setDuration(duration);
            
            DatabaseHelper.getInstance().addReadingSession(session, new DatabaseHelper.OnOperationCompleteListener() {
                @Override
                public void onSuccess() {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Session saved!", Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Exception e) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error saving session", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        
        timerRunning = false;
        
        if (startButton != null) {
            startButton.setEnabled(true);
        }
        if (stopButton != null) {
            stopButton.setEnabled(false);
        }
        if (selectBookButton != null) {
            selectBookButton.setEnabled(true);
        }
        
        if (currentBookTitle != null) {
            currentBookTitle.setText(selectedBook != null ? selectedBook.getTitle() : "No book selected...");
        }
        
        timerDisplay.setText("00:00:00");
        cancelNotification();
    }
    
    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Reading Timer", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Shows reading timer");
            
            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    private void startForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setContentTitle("Kteb - Reading")
                .setContentText("Timer running")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);
        
        Intent intent = new Intent(requireContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        
        NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }
    
    private void updateNotification(String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setContentTitle("Kteb - Reading")
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);
        
        Intent intent = new Intent(requireContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        
        NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }
    
    private void cancelNotification() {
        NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.cancel(NOTIFICATION_ID);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        applyTheme();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(timerRunnable);
    }

    private void applyTheme() {
        if (rootLayout != null) rootLayout.setBackgroundColor(ThemeManager.getBgColor(requireContext()));
        if (headerBar != null) headerBar.setBackgroundColor(ThemeManager.getHeaderColor(requireContext()));
    }
}