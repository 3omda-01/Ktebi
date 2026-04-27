package com.example.kteb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.kteb.model.DatabaseHelper;
import com.example.kteb.ui.fragment.BookshelfFragment;
import com.example.kteb.ui.fragment.TimerFragment;
import com.example.kteb.ui.fragment.HistoryFragment;
import com.example.kteb.ui.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }
        
        DatabaseHelper.getInstance().setUserIdFromAuth();
        
        setContentView(R.layout.activity_main);
        
        TextView navLibrary = findViewById(R.id.nav_bookshelf);
        TextView navTimer = findViewById(R.id.nav_timer);
        TextView navHistory = findViewById(R.id.nav_history);
        TextView navProfile = findViewById(R.id.nav_profile);
        
        View.OnClickListener clickListener = v -> {
            Fragment fragment = null;
            int id = v.getId();
            
            navLibrary.setTextColor(getColor(R.color.black));
            navTimer.setTextColor(getColor(R.color.black));
            navHistory.setTextColor(getColor(R.color.black));
            navProfile.setTextColor(getColor(R.color.black));
            
            if (id == R.id.nav_bookshelf) {
                fragment = new BookshelfFragment();
                navLibrary.setTextColor(getColor(R.color.xpblue));
            } else if (id == R.id.nav_timer) {
                fragment = new TimerFragment();
                navTimer.setTextColor(getColor(R.color.xpblue));
            } else if (id == R.id.nav_history) {
                fragment = new HistoryFragment();
                navHistory.setTextColor(getColor(R.color.xpblue));
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
                navProfile.setTextColor(getColor(R.color.xpblue));
            }
            
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        };
        
        navLibrary.setOnClickListener(clickListener);
        navTimer.setOnClickListener(clickListener);
        navHistory.setOnClickListener(clickListener);
        navProfile.setOnClickListener(clickListener);
        
        if (savedInstanceState == null) {
            navLibrary.setTextColor(getColor(R.color.xpblue));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BookshelfFragment())
                    .commit();
        }
    }
}