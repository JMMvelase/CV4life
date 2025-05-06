package com.example.newgemini;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentDashboardActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        firestore = FirebaseFirestore.getInstance();

        CardView viewJobsCard = findViewById(R.id.viewJobsCard);
        CardView myApplicationsCard = findViewById(R.id.myApplicationsCard);
        CardView editCVCard = findViewById(R.id.editCVCard);
        CardView profileCard = findViewById(R.id.profileCard);

        viewJobsCard.setOnClickListener(v -> viewAvailableJobs());
        myApplicationsCard.setOnClickListener(v -> viewMyApplications());
        editCVCard.setOnClickListener(v -> editCV());
        profileCard.setOnClickListener(v -> editProfile());
    }

    private void viewAvailableJobs() {
        // Navigate to available jobs list
        // Implementation will be needed
    }

    private void viewMyApplications() {
        // Navigate to student's applications
        // Implementation will be needed
    }

    private void editCV() {
        // Navigate to CV editor
        // Implementation will be needed
    }

    private void editProfile() {
        // Create an Intent to navigate to a new ProfileActivity
        Intent intent = new Intent(this, ProfileActivity.class);

        // You can pass the current student's ID or data if needed
        // intent.putExtra("studentId", currentStudentId);

        startActivity(intent);
    }
}