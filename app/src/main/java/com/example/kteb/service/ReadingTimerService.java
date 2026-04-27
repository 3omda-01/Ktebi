package com.example.kteb.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.example.kteb.MainActivity;
import com.example.kteb.R;

public class ReadingTimerService extends Service {
    private static final String CHANNEL_ID = "reading_timer_channel";
    private static final int NOTIFICATION_ID = 1;
    private final IBinder binder = new LocalBinder();
    private long startTime;
    private boolean isRunning = false;
    private String currentBookId;
    private String currentBookTitle;

    public class LocalBinder extends Binder {
        public ReadingTimerService getService() {
            return ReadingTimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("START".equals(action)) {
                currentBookId = intent.getStringExtra("bookId");
                currentBookTitle = intent.getStringExtra("bookTitle");
                startTimer();
            } else if ("STOP".equals(action)) {
                stopTimer();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reading Timer",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Shows reading timer status");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        isRunning = true;
        startForeground(NOTIFICATION_ID, createNotification());
    }

    private void stopTimer() {
        isRunning = false;
        long duration = System.currentTimeMillis() - startTime;
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    private Notification createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Reading: " + currentBookTitle)
                .setContentText("Timer running")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long getElapsedTime() {
        if (isRunning) {
            return System.currentTimeMillis() - startTime;
        }
        return 0;
    }

    public String getCurrentBookId() {
        return currentBookId;
    }

    public long getStartTime() {
        return startTime;
    }
}