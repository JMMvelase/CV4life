package com.example.newgemini;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;
    private RadioGroup roleRadioGroup;
    private Button registerButton;
    private TextView loginLink, loadingText;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CardView registerCard;
    private LinearLayout formContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set default locale
        Locale.setDefault(Locale.ENGLISH);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
        registerCard = findViewById(R.id.registerCard);
        formContainer = findViewById(R.id.formContainer);
    }

    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> registerUser());
        loginLink.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        int selectedId = roleRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String role = selectedRadioButton.getText().toString();

        if (!validateInputs(email, password)) {
            return;
        }

        showLoading("Creating your account...");
        createFirebaseUser(email, password, role);
    }

    private void createFirebaseUser(String email, String password, String role) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, email, role);
                    } else {
                        handleRegistrationError(task.getException());
                    }
                });
    }

    private void saveUserToFirestore(String userId, String email, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("role", role);
        user.put("profileComplete", false);
        user.put("createdAt", LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        user.put("active", true);
        user.put("locale", Locale.getDefault().getLanguage());

        firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    Toast.makeText(RegisterActivity.this,
                            "Registration Successful!", Toast.LENGTH_SHORT).show();
                    navigateToDashboard(role);
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    Toast.makeText(RegisterActivity.this,
                            "Failed to save user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void handleRegistrationError(Exception exception) {
        hideLoading();
        String errorMessage = exception.getMessage();
        if (errorMessage.contains("email address is already in use")) {
            emailInput.setError("Email already registered");
            emailInput.requestFocus();
        } else if (errorMessage.contains("badly formatted")) {
            emailInput.setError("Invalid email format");
            emailInput.requestFocus();
        } else {
            Toast.makeText(RegisterActivity.this,
                    "Registration Failed: " + errorMessage,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return false;
        }

        return true;
    }

    private void showLoading(String message) {
        progressBar.setVisibility(View.VISIBLE);
        loadingText.setText(message);
        loadingText.setVisibility(View.VISIBLE);
        formContainer.setAlpha(0.5f);
        registerButton.setEnabled(false);
        loginLink.setEnabled(false);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        formContainer.setAlpha(1.0f);
        registerButton.setEnabled(true);
        loginLink.setEnabled(true);
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        if ("Recruiter".equals(role)) {
            intent = new Intent(RegisterActivity.this, RecruiterDashboardActivity.class);
        } else {
            intent = new Intent(RegisterActivity.this, StudentDashboardActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}