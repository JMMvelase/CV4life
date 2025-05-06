package com.example.newgemini;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class RecruiterDashboardActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_dashboard);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        CardView postJobCard = findViewById(R.id.postJobCard);
        CardView viewApplicationsCard = findViewById(R.id.viewApplicationsCard);
        CardView profileCard = findViewById(R.id.profileCard);

        postJobCard.setOnClickListener(v -> showJobPostDialog());
        viewApplicationsCard.setOnClickListener(v -> viewApplications());
        profileCard.setOnClickListener(v -> editProfile());
    }

    private void showJobPostDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_post_job);
        dialog.getWindow().setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );

        EditText titleInput = dialog.findViewById(R.id.jobTitleInput);
        EditText descriptionInput = dialog.findViewById(R.id.jobDescriptionInput);
        EditText requirementsInput = dialog.findViewById(R.id.jobRequirementsInput);
        Spinner sectorSpinner = dialog.findViewById(R.id.jobSectorSpinner);
        Button postButton = dialog.findViewById(R.id.postJobButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        // Set up the spinner with job sectors
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.job_sectors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectorSpinner.setAdapter(adapter);

        postButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String requirements = requirementsInput.getText().toString().trim();
            String sector = sectorSpinner.getSelectedItem().toString();

            if (title.isEmpty() || description.isEmpty() || requirements.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> job = new HashMap<>();
            job.put("title", title);
            job.put("description", description);
            job.put("requirements", requirements);
            job.put("sector", sector);
            job.put("recruiterId", auth.getCurrentUser().getUid());
            job.put("createdAt", new Date());
            job.put("status", "open");

            firestore.collection("jobs")
                    .add(job)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Job posted successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error posting job: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void viewApplications() {
        startActivity(new Intent(this, ViewApplicationsActivity.class));
    }

    private void editProfile() {
        // Implementation for profile editing
    }
}