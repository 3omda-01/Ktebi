package com.example.kteb.model;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class OpenLibraryAPI {
    private static final String TAG = "OpenLibraryAPI";
    public interface BookSearchListener {
        void onResult(List<BookSearchResult> results);
        void onError(String error);
    }
    public static class BookSearchResult {
        public String key;
        public String title;
        public String author;
        public String coverId;
        public String isbn;
        
        public String getCoverUrl() {
            // Try ID first
            if (coverId != null && !coverId.isEmpty()) {
                return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
            }
            // Then ISBN
            if (isbn != null && !isbn.isEmpty()) {
                return "https://covers.openlibrary.org/b/isbn/" + isbn + "-M.jpg";
            }
            return null;
        }
    }
    
    public static void searchByTitle(String title, BookSearchListener listener) {
        new Thread(() -> {
            try {
                String url = "https://openlibrary.org/search.json?title=" + title.replace(" ", "+") + "&limit=10";
                Log.d(TAG, "Searching by title: " + url);
                
                List<BookSearchResult> results = fetchResults(url);
                if (listener != null) {
                    listener.onResult(results);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        }).start();
    }
    
    public static void searchByISBN(String isbn, BookSearchListener listener) {
        new Thread(() -> {
            try {
                String url = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";
                Log.d(TAG, "Searching by ISBN: " + url);
                
                List<BookSearchResult> results = new CopyOnWriteArrayList<>();
                
HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JSONObject json = new JSONObject(response.toString());
                JSONObject bookData = json.optJSONObject("ISBN:" + isbn);
                
                if (bookData != null) {
                    BookSearchResult result = new BookSearchResult();
                    result.isbn = isbn;
                    result.title = bookData.optString("title", "");
                    
                    JSONArray authors = bookData.optJSONArray("authors");
                    if (authors != null && authors.length() > 0) {
                        StringBuilder authorBuilder = new StringBuilder();
                        for (int i = 0; i < authors.length(); i++) {
                            if (i > 0) authorBuilder.append(", ");
                            authorBuilder.append(authors.optJSONObject(i).optString("name", ""));
                        }
                        result.author = authorBuilder.toString();
                    }
                    
                    JSONObject cover = bookData.optJSONObject("cover");
                    if (cover != null) {
                        result.coverId = cover.optString("id", null);
                    }
                    
                    results.add(result);
                    Log.d(TAG, "Found book: " + result.title + " coverId: " + result.coverId);
                }
                
                if (listener != null) {
                    listener.onResult(results);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        }).start();
    }
    
    private static List<BookSearchResult> fetchResults(String url) throws Exception {
        List<BookSearchResult> results = new CopyOnWriteArrayList<>();
        
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("User-Agent", "Kteb/1.0");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        JSONObject json = new JSONObject(response.toString());
        JSONArray docs = json.optJSONArray("docs");
        
        if (docs != null) {
            for (int i = 0; i < docs.length(); i++) {
                JSONObject doc = docs.getJSONObject(i);
                BookSearchResult result = new BookSearchResult();
                result.key = doc.optString("key", "");
                result.title = doc.optString("title", "");
                
                // cover_i can be a number or string
                if (doc.has("cover_i")) {
                    result.coverId = String.valueOf(doc.opt("cover_i"));
                }
                Log.d(TAG, "Result " + i + ": " + result.title + " coverId: " + result.coverId);
                
                JSONArray authorNames = doc.optJSONArray("author_name");
                if (authorNames != null && authorNames.length() > 0) {
                    StringBuilder authorBuilder = new StringBuilder();
                    for (int j = 0; j < authorNames.length(); j++) {
                        if (j > 0) authorBuilder.append(", ");
                        authorBuilder.append(authorNames.getString(j));
                    }
                    result.author = authorBuilder.toString();
                }
                
                JSONArray isbns = doc.optJSONArray("isbn");
                if (isbns != null && isbns.length() > 0) {
                    result.isbn = isbns.getString(0);
                }
                
                results.add(result);
                Log.d(TAG, "Found: " + result.title + " coverId: " + result.coverId);
            }
        }
        
        return results;
    }
}