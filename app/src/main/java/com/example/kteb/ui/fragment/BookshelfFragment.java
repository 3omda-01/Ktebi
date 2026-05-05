package com.example.kteb.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kteb.AddBookActivity;
import com.example.kteb.BookDetailActivity;
import com.example.kteb.R;
import com.example.kteb.model.DatabaseHelper;
import com.example.kteb.adapter.BookAdapter;
import com.example.kteb.model.Book;
import com.example.kteb.util.ThemeManager;
import java.util.List;

public class BookshelfFragment extends Fragment implements BookAdapter.OnBookClickListener, DatabaseHelper.OnBooksLoadedListener {
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private DatabaseHelper dbHelper;
    private LinearLayout categoryContainer;
    private String currentCategory = "All";

    private final String[] categories = {"All", "Fiction", "Comics", "Manga", "Self-Improvement", "Educational", "Literature"};
    private TextView tabAll, tabFiction, tabComics, tabManga, tabSelf, tabEdu, tabLit;
    private TextView fabAdd;
    private TextView bookCountText;
    private View rootLayout, headerBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookshelf, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = DatabaseHelper.getInstance();

        rootLayout = view.findViewById(R.id.root_layout);
        headerBar = view.findViewById(R.id.header_bar);
        applyTheme();

        recyclerView = view.findViewById(R.id.books_recycler_view);
        categoryContainer = view.findViewById(R.id.category_tabs);
        fabAdd = view.findViewById(R.id.fab_add_book);
        bookCountText = view.findViewById(R.id.book_count);

        tabAll = view.findViewById(R.id.tab_all);
        tabFiction = view.findViewById(R.id.tab_fiction);
        tabComics = view.findViewById(R.id.tab_comics);
        tabManga = view.findViewById(R.id.tab_manga);
        tabSelf = view.findViewById(R.id.tab_self);
        tabEdu = view.findViewById(R.id.tab_edu);
        tabLit = view.findViewById(R.id.tab_lit);

        setupRecyclerView();
        setupTabs();

        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                startActivity(intent);
            });
        }

        loadBooks();
    }

    private void setupRecyclerView() {
        adapter = new BookAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    private void setupTabs() {
        View.OnClickListener tabClick = v -> {
            resetTabColors();
            currentCategory = ((TextView)v).getText().toString();
            v.setBackgroundColor(ThemeManager.getHeaderColor(requireContext()));
            loadBooks();
        };

        tabAll.setOnClickListener(tabClick);
        tabFiction.setOnClickListener(tabClick);
        tabComics.setOnClickListener(tabClick);
        tabManga.setOnClickListener(tabClick);
        tabSelf.setOnClickListener(tabClick);
        tabEdu.setOnClickListener(tabClick);
        tabLit.setOnClickListener(tabClick);

        tabAll.setBackgroundColor(ThemeManager.getHeaderColor(requireContext()));
    }

    private void resetTabColors() {
        int gray = ThemeManager.getBgColor(requireContext());
        tabAll.setBackgroundColor(gray);
        tabFiction.setBackgroundColor(gray);
        tabComics.setBackgroundColor(gray);
        tabManga.setBackgroundColor(gray);
        tabSelf.setBackgroundColor(gray);
        tabEdu.setBackgroundColor(gray);
        tabLit.setBackgroundColor(gray);
    }
    private void loadBooks() {
        if (currentCategory.equals("All")) {
            dbHelper.getBooks(this);
        } else {
            dbHelper.getBooksByCategory(currentCategory, this);
        }
    }
    @Override
    public void onBooksLoaded(List<Book> books) {
        if (books != null) {
            Log.d("Bookshelf", "Loaded " + books.size() + " books");
            for (Book b : books) {
                Log.d("Bookshelf", "Book: " + b.getTitle() + " cover: " + b.getCoverUrl());
            }
            if (bookCountText != null) {
                bookCountText.setText(books.size() + " object(s)");
            }
        } else {
            Log.d("Bookshelf", "Books list is null");
            if (bookCountText != null) {
                bookCountText.setText("0 object(s)");
            }
        }
        adapter.setBooks(books);
    }
    @Override
    public void onBookClick(Book book) {
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        intent.putExtra("bookId", book.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        applyTheme();
    }

    private void applyTheme() {
        if (rootLayout != null) rootLayout.setBackgroundColor(ThemeManager.getBgColor(requireContext()));
        if (headerBar != null) headerBar.setBackgroundColor(ThemeManager.getHeaderColor(requireContext()));
    }
}
