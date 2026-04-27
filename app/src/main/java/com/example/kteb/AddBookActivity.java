package com.example.kteb;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kteb.model.Book;
import com.example.kteb.model.DatabaseHelper;
import com.example.kteb.model.OpenLibraryAPI;
import java.util.ArrayList;
import java.util.List;

public class AddBookActivity extends AppCompatActivity {
    private EditText titleInput, authorInput, isbnInput;
    private Spinner categorySpinner, statusSpinner;
    private DatabaseHelper dbHelper;
    private TextView saveButton, cancelButton, searchButton;
    private List<OpenLibraryAPI.BookSearchResult> searchResults;
    private OpenLibraryAPI.BookSearchResult selectedResult;

    private final String[] categories = {"Fiction", "Comics", "Manga", "Self-Improvement", "Educational", "Literature"};
    private final String[] statuses = {"wantToRead", "reading", "completed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        
        dbHelper = DatabaseHelper.getInstance();
        
        titleInput = findViewById(R.id.input_title);
        authorInput = findViewById(R.id.input_author);
        isbnInput = findViewById(R.id.input_isbn);
        categorySpinner = findViewById(R.id.spinner_category);
        statusSpinner = findViewById(R.id.spinner_status);
        saveButton = findViewById(R.id.btn_save_book);
        cancelButton = findViewById(R.id.btn_cancel);
        searchButton = findViewById(R.id.btn_search);
        
        setupSpinners();
        
        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveBook());
        }
        
        if (cancelButton != null) {
            cancelButton.setOnClickListener(v -> finish());
        }
        
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> {
                String query = titleInput.getText().toString().trim();
                if (query.length() >= 2) {
                    searchBooks(query);
                } else {
                    Toast.makeText(this, "Enter at least 2 characters", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void searchBooks(String query) {
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
        OpenLibraryAPI.searchByTitle(query, new OpenLibraryAPI.BookSearchListener() {
            @Override
            public void onResult(List<OpenLibraryAPI.BookSearchResult> results) {
                runOnUiThread(() -> {
                    if (results != null && !results.isEmpty()) {
                        searchResults = results;
                        selectedResult = results.get(0);
                        
                        OpenLibraryAPI.BookSearchResult r = results.get(0);
                        if (r.title != null) titleInput.setText(r.title);
                        if (r.author != null) authorInput.setText(r.author);
                        if (r.isbn != null) isbnInput.setText(r.isbn);
                        
                        Toast.makeText(AddBookActivity.this, "Found: " + r.title, Toast.LENGTH_SHORT).show();
                        Log.d("AddBook", "Found cover: " + r.getCoverUrl());
                    } else {
                        Toast.makeText(AddBookActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AddBookActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void fillFromSearchResult(OpenLibraryAPI.BookSearchResult result) {
        if (result == null) return;
        
        if (result.title != null && !result.title.isEmpty()) {
            titleInput.setText(result.title);
        }
        if (result.author != null && !result.author.isEmpty()) {
            authorInput.setText(result.author);
        }
        if (result.isbn != null && !result.isbn.isEmpty()) {
            isbnInput.setText(result.isbn);
        }
        
        String coverUrl = result.getCoverUrl();
        Log.d("AddBook", "Selected cover URL: " + coverUrl);
    }

    private void setupSpinners() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(categoryAdapter);
        
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, statuses);
        statusSpinner.setAdapter(statusAdapter);
    }

    private void saveBook() {
        if (titleInput == null) return;
        
        String title = titleInput.getText().toString().trim();
        String author = authorInput != null ? authorInput.getText().toString().trim() : "";
        String isbn = isbnInput != null ? isbnInput.getText().toString().trim() : "";
        
        if (title.isEmpty()) {
            titleInput.setError("Title is required");
            return;
        }
        
        String category = categorySpinner.getSelectedItem() != null ? 
            categorySpinner.getSelectedItem().toString() : "Fiction";
        String status = statusSpinner.getSelectedItem() != null ? 
            statusSpinner.getSelectedItem().toString() : "wantToRead";
        
        String coverUrl = null;
        
        // Use search result if available
        if (selectedResult != null) {
            coverUrl = selectedResult.getCoverUrl();
            Log.d("AddBook", "Using search result cover: " + coverUrl);
        } else if (!isbn.isEmpty()) {
            coverUrl = "https://covers.openlibrary.org/b/isbn/" + isbn + "-M.jpg";
            Log.d("AddBook", "Using ISBN cover: " + coverUrl);
        }
        
        Book book = new Book(title, author, isbn, category);
        book.setStatus(status);
        book.setCoverUrl(coverUrl);
        
        if (dbHelper != null) {
            dbHelper.addBook(book, new DatabaseHelper.OnOperationCompleteListener() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(AddBookActivity.this, "Book added!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
                
                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddBookActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
        } else {
            Toast.makeText(this, "Book added locally!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}