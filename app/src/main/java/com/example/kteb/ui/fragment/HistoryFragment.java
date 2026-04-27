package com.example.kteb.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kteb.R;
import com.example.kteb.model.DatabaseHelper;
import com.example.kteb.adapter.SessionAdapter;
import com.example.kteb.model.ReadingSession;
import java.util.List;

public class HistoryFragment extends Fragment implements DatabaseHelper.OnSessionsLoadedListener {
    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            dbHelper = DatabaseHelper.getInstance();
            recyclerView = view.findViewById(R.id.history_recycler_view);
            
            if (recyclerView != null) {
                adapter = new SessionAdapter();
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
                
                if (dbHelper != null) {
                    dbHelper.getReadingSessions(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSessionsLoaded(List<ReadingSession> sessions) {
        if (adapter != null) {
            adapter.setSessions(sessions);
        }
    }
}