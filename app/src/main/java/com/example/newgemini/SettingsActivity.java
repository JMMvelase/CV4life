package com.example.newgemini;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch notificationsSwitch;
    private RadioGroup themeRadioGroup;
    private RadioButton lightThemeOption, darkThemeOption;
    private Button saveSettingsButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI components
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        lightThemeOption = findViewById(R.id.lightThemeOption);
        darkThemeOption = findViewById(R.id.darkThemeOption);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Load saved settings
        loadSettings();

        // Save settings on button click
        saveSettingsButton.setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        // Load saved notification preference
        boolean notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", true);
        notificationsSwitch.setChecked(notificationsEnabled);

        // Load saved theme preference
        String theme = sharedPreferences.getString("theme", "Light");
        if ("Light".equals(theme)) {
            lightThemeOption.setChecked(true);
        } else if ("Dark".equals(theme)) {
            darkThemeOption.setChecked(true);
        }
    }

    private void saveSettings() {
        // Save notification preference
        boolean notificationsEnabled = notificationsSwitch.isChecked();
        editor.putBoolean("notificationsEnabled", notificationsEnabled);

        // Save theme preference
        int selectedThemeId = themeRadioGroup.getCheckedRadioButtonId();
        if (selectedThemeId == R.id.lightThemeOption) {
            editor.putString("theme", "Light");
        } else if (selectedThemeId == R.id.darkThemeOption) {
            editor.putString("theme", "Dark");
        }

        // Commit changes to SharedPreferences
        editor.apply();

        // Show confirmation message
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
    }
}