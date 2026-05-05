package com.example.kteb;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kteb.util.ThemeManager;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_HEADER_COLOR = "header_color";
    private static final String KEY_BG_COLOR = "bg_color";
    private static final String KEY_ACCENT_COLOR = "accent_color";

    private View rootLayout, headerBar;
    private LinearLayout headerColorsContainer, bgColorsContainer, accentColorsContainer;
    private int selectedHeaderColor, selectedBgColor, selectedAccentColor;

    private final int[] headerColorOptions = {
            Color.parseColor("#8B7500"),
            Color.parseColor("#B8860B"),
            Color.parseColor("#DAA520"),
            Color.parseColor("#FF8C00"),
            Color.parseColor("#FF4500"),
            Color.parseColor("#DC143C"),
            Color.parseColor("#0054E3"),
            Color.parseColor("#245ED8"),
            Color.parseColor("#008080"),
            Color.parseColor("#4B0082"),
    };

    private final int[] bgColorOptions = {
            Color.parseColor("#C0C0C0"),
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#ECE9D8"),
            Color.parseColor("#E8E8E8"),
            Color.parseColor("#2C2C2C"),
            Color.parseColor("#1A1A2E"),
            Color.parseColor("#16213E"),
            Color.parseColor("#0F3460"),
            Color.parseColor("#533483"),
            Color.parseColor("#2D4059"),
    };

    private final int[] accentColorOptions = {
            Color.parseColor("#8B7500"),
            Color.parseColor("#DAA520"),
            Color.parseColor("#32CD32"),
            Color.parseColor("#00FFFF"),
            Color.parseColor("#FF69B4"),
            Color.parseColor("#9B30FF"),
            Color.parseColor("#FF0000"),
            Color.parseColor("#0054E3"),
            Color.parseColor("#008080"),
            Color.parseColor("#FFFF00"),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rootLayout = findViewById(R.id.root_layout);
        headerBar = findViewById(R.id.header_bar);

        headerColorsContainer = findViewById(R.id.header_colors);
        bgColorsContainer = findViewById(R.id.bg_colors);
        accentColorsContainer = findViewById(R.id.accent_colors);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        selectedHeaderColor = prefs.getInt(KEY_HEADER_COLOR, headerColorOptions[0]);
        selectedBgColor = prefs.getInt(KEY_BG_COLOR, bgColorOptions[0]);
        selectedAccentColor = prefs.getInt(KEY_ACCENT_COLOR, accentColorOptions[0]);

        applyTheme();

        buildColorRow(headerColorsContainer, headerColorOptions, KEY_HEADER_COLOR, selectedHeaderColor);
        buildColorRow(bgColorsContainer, bgColorOptions, KEY_BG_COLOR, selectedBgColor);
        buildColorRow(accentColorsContainer, accentColorOptions, KEY_ACCENT_COLOR, selectedAccentColor);

        TextView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }

    private void buildColorRow(LinearLayout container, int[] colors, String prefKey, int currentSelection) {
        for (int color : colors) {
            View swatch = createSwatch(color, prefKey, currentSelection);
            container.addView(swatch);
        }
    }

    private View createSwatch(int color, String prefKey, int currentSelection) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60, 60);
        params.setMargins(0, 0, 8, 0);

        ImageView swatch = new ImageView(this);
        swatch.setLayoutParams(params);
        swatch.setPadding(4, 4, 4, 4);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(8);
        swatch.setBackground(bg);

        if (color == currentSelection) {
            GradientDrawable selectedBorder = new GradientDrawable();
            selectedBorder.setColor(color);
            selectedBorder.setShape(GradientDrawable.RECTANGLE);
            selectedBorder.setCornerRadius(8);
            selectedBorder.setStroke(4, Color.WHITE);
            swatch.setBackground(selectedBorder);
        }

        final String fPrefKey = prefKey;
        final int fColor = color;
        swatch.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putInt(fPrefKey, fColor).apply();

            clearSelection((LinearLayout) swatch.getParent(), fPrefKey);

            GradientDrawable newBg = new GradientDrawable();
            newBg.setColor(fColor);
            newBg.setShape(GradientDrawable.RECTANGLE);
            newBg.setCornerRadius(8);
            newBg.setStroke(4, Color.WHITE);
            swatch.setBackground(newBg);

            switch (fPrefKey) {
                case KEY_HEADER_COLOR:
                    selectedHeaderColor = fColor;
                    break;
                case KEY_BG_COLOR:
                    selectedBgColor = fColor;
                    break;
                case KEY_ACCENT_COLOR:
                    selectedAccentColor = fColor;
                    break;
            }

            applyTheme();
        });

        return swatch;
    }

    private void clearSelection(LinearLayout container, String prefKey) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            int color;
            switch (prefKey) {
                case KEY_HEADER_COLOR:
                    color = headerColorOptions[i];
                    break;
                case KEY_BG_COLOR:
                    color = bgColorOptions[i];
                    break;
                case KEY_ACCENT_COLOR:
                    color = accentColorOptions[i];
                    break;
                default:
                    continue;
            }
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(color);
            bg.setShape(GradientDrawable.RECTANGLE);
            bg.setCornerRadius(8);
            child.setBackground(bg);
        }
    }

    private void applyTheme() {
        if (rootLayout != null) rootLayout.setBackgroundColor(ThemeManager.getBgColor(this));
        if (headerBar != null) headerBar.setBackgroundColor(ThemeManager.getHeaderColor(this));
    }
}
