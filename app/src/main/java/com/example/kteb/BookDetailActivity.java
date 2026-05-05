package com.example.kteb;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.example.kteb.model.Book;
import com.example.kteb.model.DatabaseHelper;
import com.example.kteb.util.ThemeManager;

import java.util.List;

public class BookDetailActivity extends AppCompatActivity implements DatabaseHelper.OnBooksLoadedListener {
    private TextView titleText, authorText, categoryText, statusText;
    private ImageView coverImage, starGif;
    private RatingBar ratingBar;
    private EditText noteInput;
    private TextView saveButton, deleteButton;
    private DatabaseHelper dbHelper;
    private Book currentBook;
    private String currentBookId;
    private View rootLayout, headerBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        rootLayout = findViewById(R.id.root_layout);
        headerBar = findViewById(R.id.header_bar);
        applyTheme();

        dbHelper = DatabaseHelper.getInstance();
        
        titleText = findViewById(R.id.detail_title);
        authorText = findViewById(R.id.detail_author);
        categoryText = findViewById(R.id.detail_category);
        statusText = findViewById(R.id.detail_status);
        coverImage = findViewById(R.id.detail_cover);
        starGif = findViewById(R.id.star_gif);
        ratingBar = findViewById(R.id.detail_rating);
        noteInput = findViewById(R.id.detail_note);
        saveButton = findViewById(R.id.btn_save_detail);
        deleteButton = findViewById(R.id.btn_delete_book);
        
        saveButton.setOnClickListener(v -> saveDetails());
        deleteButton.setOnClickListener(v -> deleteBook());
        
        loadBookData();
    }

    private void loadBookData() {
        String bookId = getIntent().getStringExtra("bookId");
        Log.d("BookDetail", "Loading book ID: " + bookId);
        
        if (bookId == null || bookId.isEmpty()) {
            Toast.makeText(this, "Invalid book", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        dbHelper.getBooks(new DatabaseHelper.OnBooksLoadedListener() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                if (books == null || books.isEmpty()) {
                    runOnUiThread(() -> {
                        Toast.makeText(BookDetailActivity.this, "No books found", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }
                
                Book foundBook = null;
                for (Book book : books) {
                    if (book != null && book.getId() != null && book.getId().equals(bookId)) {
                        foundBook = book;
                        break;
                    }
                }
                
                final Book finalBook = foundBook;
                runOnUiThread(() -> {
                    if (finalBook != null) {
                        currentBook = finalBook;
                        displayBook(finalBook);
                    } else {
                        Toast.makeText(BookDetailActivity.this, "Book not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    private void displayBook(Book book) {
        if (book == null) {
            Toast.makeText(this, "No book data", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d("BookDetail", "Displaying: " + book.getTitle() + " cover: " + book.getCoverUrl());
        
        if (titleText != null) titleText.setText(book.getTitle() != null ? book.getTitle() : "");
        if (authorText != null) authorText.setText(book.getAuthor() != null ? book.getAuthor() : "");
        if (categoryText != null) categoryText.setText("Category: " + (book.getCategory() != null ? book.getCategory() : ""));
        if (statusText != null) statusText.setText("Status: " + (book.getStatus() != null ? book.getStatus() : ""));
        if (ratingBar != null) ratingBar.setRating(book.getRating());
        if (noteInput != null) noteInput.setText(book.getNote() != null ? book.getNote() : "");
        
        if (coverImage != null) {
            String coverUrl = book.getCoverUrl();
            if (coverUrl != null && !coverUrl.isEmpty()) {
                Log.d("BookDetail", "Loading cover with Glide");
                Glide.with(this)
                        .load(coverUrl)
                        .error(R.drawable.ic_launcher_foreground)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(coverImage);
            } else {
                coverImage.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }

        Glide.with(this)
                .asGif()
                .load("file:///android_asset/star.gif")
                .into(starGif);
    }

    private void saveDetails() {
        if (currentBook == null) {
            Toast.makeText(this, "No book to save", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int newRating = ratingBar != null ? (int) ratingBar.getRating() : 0;
        String newNote = noteInput != null ? noteInput.getText().toString() : "";
        
        Log.d("BookDetail", "Saving - rating: " + newRating + ", note: " + newNote);
        
        currentBook.setRating(newRating);
        currentBook.setNote(newNote);
        
        dbHelper.updateBook(currentBook, new DatabaseHelper.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> Toast.makeText(BookDetailActivity.this, "Saved!", Toast.LENGTH_SHORT).show());
            }
            
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(BookDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void deleteBook() {
        if (currentBook == null || currentBook.getId() == null) {
            Toast.makeText(this, "No book to delete", Toast.LENGTH_SHORT).show();
            return;
        }
        
        dbHelper.deleteBook(currentBook.getId(), new DatabaseHelper.OnOperationCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(BookDetailActivity.this, "Book deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
            
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(BookDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public void onBooksLoaded(List<Book> books) {
        Log.d("BookDetail", "onBooksLoaded called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyTheme();
    }

    private void applyTheme() {
        if (rootLayout != null) rootLayout.setBackgroundColor(ThemeManager.getBgColor(this));
        if (headerBar != null) headerBar.setBackgroundColor(ThemeManager.getHeaderColor(this));
    }
}