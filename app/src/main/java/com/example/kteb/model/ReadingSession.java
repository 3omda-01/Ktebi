package com.example.kteb.model;

public class ReadingSession {
    private String id;
    private String bookId;
    private String bookTitle;
    private long startTime;
    private long endTime;
    private long duration;

    public ReadingSession() {}

    public ReadingSession(String bookId, String bookTitle, long startTime) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.startTime = startTime;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
}