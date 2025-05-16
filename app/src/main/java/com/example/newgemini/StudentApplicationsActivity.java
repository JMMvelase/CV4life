package com.example.newgemini;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StudentApplicationsActivity extends AppCompatActivity {

    private RecyclerView applicationsRecyclerView;
    private ApplicationsAdapter adapter;
    private List<Application> applicationList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_applications);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        applicationsRecyclerView = findViewById(R.id.applicationsRecyclerView);
        applicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter
        applicationList = new ArrayList<>();
        adapter = new ApplicationsAdapter(this, applicationList, false, null); // Pass false for isRecruiter and null for the action listener
        applicationsRecyclerView.setAdapter(adapter);

        // Load applications
        loadApplications();
    }

    private void loadApplications() {
        String studentId = auth.getCurrentUser().getUid();

        firestore.collection("applications")
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    applicationList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Application application = doc.toObject(Application.class);
                        if (application != null) {
                            application.setId(doc.getId()); // Set Firestore document ID
                            Log.d("ApplicationsDebug", "Application fetched: " + application.getId());

                            // Fetch the job title using recruiterId if jobId is null
                            String recruiterId = application.getRecruiterId();
                            Log.d("ApplicationsDebug", "Fetching job title for recruiterId: " + recruiterId);
                            if (application.getJobId() == null && recruiterId != null) {
                                firestore.collection("jobs")
                                        .whereEqualTo("recruiterId", recruiterId)
                                        .limit(1)
                                        .get()
                                        .addOnSuccessListener(jobSnapshot -> {
                                            if (!jobSnapshot.isEmpty()) {
                                                DocumentSnapshot jobDoc = jobSnapshot.getDocuments().get(0);
                                                String jobTitle = jobDoc.getString("title");
                                                Log.d("ApplicationsDebug", "Job title fetched: " + jobTitle);
                                                application.setTitle(jobTitle);
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                Log.e("ApplicationsDebug", "No job document found for recruiterId: " + recruiterId);
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("ApplicationsDebug", "Failed to fetch job title: " + e.getMessage()));
                            }
                            applicationList.add(application);
                        }
                    }
                    adapter.notifyDataSetChanged(); // Refresh adapter after adding all applications
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading applications: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ApplicationsDebug", "Error loading applications: " + e.getMessage());
                });
    }
}