package com.example.newgemini;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentDashboardActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Find views by ID
        CardView viewJobsCard = findViewById(R.id.viewJobsCard);
        CardView myApplicationsCard = findViewById(R.id.myApplicationsCard);
        CardView editCVCard = findViewById(R.id.editCVCard);
        CardView profileCard = findViewById(R.id.profileCard);
        CardView chatbotCard = findViewById(R.id.chatbotCard);
        CardView settingsCard = findViewById(R.id.settingsCard);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set onClickListeners
        viewJobsCard.setOnClickListener(v -> viewAvailableJobs());
        myApplicationsCard.setOnClickListener(v -> viewMyApplications());
        editCVCard.setOnClickListener(v -> editCV());
        profileCard.setOnClickListener(v -> editProfile());
        settingsCard.setOnClickListener(v -> openSettings());
        chatbotCard.setOnClickListener(v -> openChatbot());
        logoutButton.setOnClickListener(v -> logout());
    }

    private void viewAvailableJobs() {
        // Navigate to available jobs list
        Intent intent = new Intent(this, JobsActivity.class);
        startActivity(intent);
    }

    private void viewMyApplications() {
        // Navigate to student's applications
        Intent intent = new Intent(this, ApplicationsActivity.class);
        startActivity(intent);
    }

    private void editCV() {
        // Navigate to CV editor
        Intent intent = new Intent(this, CVEditorActivity.class);
        startActivity(intent);
    }

    private void editProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    private void openChatbot() {
        // Navigate to Chatbot activity
        Intent intent = new Intent(this, viviActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        // Navigate to settings activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void logout() {
        // Sign out the user
        auth.signOut();

        // Navigate back to the login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}