package com.example.newgemini;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private EditText nameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText departmentInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        initializeViews();

        // Load existing profile data
        loadProfileData();

        // Set up save button listener
        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        departmentInput = findViewById(R.id.departmentInput);
        saveButton = findViewById(R.id.saveButton);
    }

    private void loadProfileData() {
        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch profile data from Firestore
        firestore.collection("students")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Convert document to StudentProfile object
                        StudentProfile profile = document.toObject(StudentProfile.class);
                        if (profile != null) {
                            // Populate the UI with existing data
                            nameInput.setText(profile.getName());
                            emailInput.setText(profile.getEmail());
                            phoneInput.setText(profile.getPhone());
                            departmentInput.setText(profile.getDepartment());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfile() {
        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create StudentProfile object
        StudentProfile profile = new StudentProfile(
                nameInput.getText().toString().trim(),
                emailInput.getText().toString().trim(),
                phoneInput.getText().toString().trim(),
                departmentInput.getText().toString().trim()
        );

        // Save to Firestore
        firestore.collection("students")
                .document(userId)
                .set(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}