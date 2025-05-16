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

public class ViewApplicationsActivity extends AppCompatActivity implements ApplicationsAdapter.OnApplicationActionListener {

    private RecyclerView applicationsRecyclerView;
    private ApplicationsAdapter adapter;
    private List<Application> applicationList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applications);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        applicationsRecyclerView = findViewById(R.id.applicationsRecyclerView);
        applicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter
        applicationList = new ArrayList<>();
        adapter = new ApplicationsAdapter(this, applicationList, true, this); // Pass true for isRecruiter
        applicationsRecyclerView.setAdapter(adapter);

        // Load applications
        loadApplications();
    }

    private void loadApplications() {
        String recruiterId = auth.getCurrentUser().getUid();

        firestore.collection("applications")
                .whereEqualTo("recruiterId", recruiterId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    applicationList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Application application = doc.toObject(Application.class);
                        if (application != null) {
                            application.setId(doc.getId()); // Set Firestore document ID

                            // Fetch the job title using recruiterId if jobId is null
                            if (application.getJobId() == null) {
                                firestore.collection("jobs")
                                        .whereEqualTo("recruiterId", recruiterId)
                                        .limit(1)
                                        .get()
                                        .addOnSuccessListener(jobSnapshot -> {
                                            if (!jobSnapshot.isEmpty()) {
                                                DocumentSnapshot jobDoc = jobSnapshot.getDocuments().get(0);
                                                String jobTitle = jobDoc.getString("title");
                                                application.setTitle(jobTitle);
                                                adapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("ViewApplications", "Failed to fetch job title: " + e.getMessage()));
                            }
                            applicationList.add(application);
                        }
                    }
                    adapter.notifyDataSetChanged(); // Refresh adapter after adding all applications
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading applications: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ViewApplications", "Error loading applications: " + e.getMessage());
                });
    }

    @Override
    public void onAccept(Application application) {
        updateApplicationStatus(application, "Accepted");
    }

    @Override
    public void onReject(Application application) {
        updateApplicationStatus(application, "Rejected");
    }

    private void updateApplicationStatus(Application application, String status) {
        String applicationId = application.getId();

        if (applicationId == null || applicationId.isEmpty()) {
            Toast.makeText(this, "Invalid application ID", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("applications").document(applicationId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    application.setStatus(status);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Application status updated to " + status, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update application: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}