package com.example.kteb.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.kteb.LoginActivity;
import com.example.kteb.R;
import com.example.kteb.SettingsActivity;
import com.example.kteb.util.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private TextView userEmail, signOutButton;
    private ImageView profileGif, settingsButton;
    private View rootLayout, headerBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        userEmail = view.findViewById(R.id.user_email);
        signOutButton = view.findViewById(R.id.btn_sign_out);
        profileGif = view.findViewById(R.id.profile_gif);
        settingsButton = view.findViewById(R.id.btn_settings);
        rootLayout = view.findViewById(R.id.root_layout);
        headerBar = view.findViewById(R.id.header_bar);
        applyTheme();

        Glide.with(this)
                .asGif()
                .load("file:///android_asset/fbook.gif")
                .into(profileGif);

        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && userEmail != null) {
            userEmail.setText(user.getEmail());
        }
        
        if (signOutButton != null) {
            signOutButton.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            });
        }
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