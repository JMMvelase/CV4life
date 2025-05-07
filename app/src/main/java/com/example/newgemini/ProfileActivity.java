package com.example.newgemini;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private EditText nameInput, emailInput, phoneInput;
    private Spinner departmentSpinner;
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
        departmentSpinner = findViewById(R.id.departmentSpinner);
        saveButton = findViewById(R.id.saveButton);

        // Set up the spinner with job sectors
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.job_sectors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(adapter);
    }

    private void loadProfileData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("students")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        nameInput.setText(document.getString("name"));
                        emailInput.setText(document.getString("email"));
                        phoneInput.setText(document.getString("phone"));

                        String department = document.getString("department");
                        if (department != null) {
                            int spinnerPosition = ((ArrayAdapter) departmentSpinner.getAdapter())
                                    .getPosition(department);
                            departmentSpinner.setSelection(spinnerPosition);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfile() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> profile = new HashMap<>();
        profile.put("name", nameInput.getText().toString().trim());
        profile.put("email", emailInput.getText().toString().trim());
        profile.put("phone", phoneInput.getText().toString().trim());
        profile.put("department", departmentSpinner.getSelectedItem().toString());

        firestore.collection("students")
                .document(userId)
                .set(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}