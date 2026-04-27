package com.example.kteb.model;

public class Book {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String coverUrl;
    private String category;
    private String status;
    private int rating;
    private String note;
    private long dateAdded;
    private long totalReadingTime;

    public Book() {}

    public Book(String title, String author, String isbn, String category) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.status = "wantToRead";
        this.rating = 0;
        this.dateAdded = System.currentTimeMillis();
        this.totalReadingTime = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public long getDateAdded() { return dateAdded; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }

    public long getTotalReadingTime() { return totalReadingTime; }
    public void setTotalReadingTime(long totalReadingTime) { this.totalReadingTime = totalReadingTime; }
}