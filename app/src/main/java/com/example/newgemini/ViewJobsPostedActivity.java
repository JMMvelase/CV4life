package com.example.newgemini;

import android.os.Bundle;
import android.widget.*;
import android.view.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class ViewJobsPostedActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private LinearLayout jobsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jobs_posted);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        jobsContainer = findViewById(R.id.jobsContainer);

        loadJobs();
    }

    private void loadJobs() {
        String recruiterId = auth.getCurrentUser().getUid();
        firestore.collection("jobs")
                .whereEqualTo("recruiterId", recruiterId)
                .get()
                .addOnSuccessListener(jobSnapshots -> {
                    jobsContainer.removeAllViews();
                    if (jobSnapshots.size() == 0) {
                        TextView tv = new TextView(this);
                        tv.setText("No jobs posted yet.");
                        tv.setTextSize(16);
                        tv.setPadding(0, 16, 0, 16);
                        jobsContainer.addView(tv);
                        return;
                    }
                    for (QueryDocumentSnapshot jobDoc : jobSnapshots) {
                        addJobView(jobDoc);
                    }
                });
    }

    private void addJobView(QueryDocumentSnapshot jobDoc) {
        String title = jobDoc.getString("title");
        String status = jobDoc.getString("status");
        String jobId = jobDoc.getId();

        View jobItem = LayoutInflater.from(this).inflate(R.layout.item_job_posted, jobsContainer, false);

        TextView jobTitle = jobItem.findViewById(R.id.jobTitle);
        TextView jobStatus = jobItem.findViewById(R.id.jobStatus);
        Button deleteBtn = jobItem.findViewById(R.id.deleteJobBtn);

        jobTitle.setText(title);
        jobStatus.setText("Status: " + (status != null ? status : "N/A"));
        deleteBtn.setEnabled("accepted".equalsIgnoreCase(status));
        deleteBtn.setOnClickListener(v -> {
            firestore.collection("jobs").document(jobId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Job deleted.", Toast.LENGTH_SHORT).show();
                        jobsContainer.removeView(jobItem);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        jobsContainer.addView(jobItem);
    }
}