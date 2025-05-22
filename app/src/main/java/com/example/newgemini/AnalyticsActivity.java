package com.example.newgemini;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class AnalyticsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private LinearLayout analyticsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        analyticsContainer = findViewById(R.id.analyticsContainer);

        loadAnalytics();
    }

    private void loadAnalytics() {
        String recruiterId = auth.getCurrentUser().getUid();
        // First, load all jobs for this recruiter
        firestore.collection("jobs")
                .whereEqualTo("recruiterId", recruiterId)
                .get()
                .addOnSuccessListener(jobsSnapshot -> {
                    Map<String, Integer> statusCountMap = new HashMap<>();
                    if (jobsSnapshot.size() == 0) {
                        TextView tv = new TextView(this);
                        tv.setText("No jobs posted yet.");
                        tv.setTextSize(16);
                        tv.setPadding(0, 16, 0, 16);
                        analyticsContainer.addView(tv);
                        return;
                    }
                    for (QueryDocumentSnapshot jobDoc : jobsSnapshot) {
                        String jobId = jobDoc.getId();
                        String title = jobDoc.getString("title");
                        String status = jobDoc.getString("status");
                        // Count job status
                        if (status == null) status = "unknown";
                        statusCountMap.put(status, statusCountMap.getOrDefault(status, 0) + 1);
                        fetchApplicationCountForJob(jobId, title, status, statusCountMap, jobsSnapshot.size());
                    }
                    // Show status summary at the top after all jobs are loaded
                    showStatusSummary(statusCountMap);
                });
    }

    private void showStatusSummary(Map<String, Integer> statusCountMap) {
        analyticsContainer.removeAllViews(); // Remove previous content
        TextView summaryTitle = new TextView(this);
        summaryTitle.setText("Jobs by Status:");
        summaryTitle.setTextSize(18);
        summaryTitle.setPadding(0, 8, 0, 8);
        analyticsContainer.addView(summaryTitle);

        for (Map.Entry<String, Integer> entry : statusCountMap.entrySet()) {
            TextView tv = new TextView(this);
            tv.setText("- " + entry.getKey() + ": " + entry.getValue());
            tv.setTextSize(16);
            analyticsContainer.addView(tv);
        }
        // Spacer
        TextView spacer = new TextView(this);
        spacer.setText("\nApplications per Job:");
        spacer.setTextSize(18);
        analyticsContainer.addView(spacer);
    }

    private void fetchApplicationCountForJob(String jobId, String title, String status, Map<String, Integer> statusCountMap, int totalJobs) {
        firestore.collection("job_applications")
                .whereEqualTo("jobId", jobId)
                .get()
                .addOnSuccessListener(appsSnapshot -> {
                    int count = appsSnapshot.size();
                    TextView tv = new TextView(this);
                    tv.setText("Job: " + title + "\nApplications: " + count + "\nStatus: " + status);
                    tv.setTextSize(16);
                    tv.setPadding(0, 16, 0, 16);
                    analyticsContainer.addView(tv);
                });
    }
}