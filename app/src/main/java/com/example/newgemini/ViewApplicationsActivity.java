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
    private ApplicationsAdapter applicationsAdapter;
    private List<Application> applicationList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applications);

        // Initialize Firebase Firestore and Auth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set up RecyclerView
        applicationsRecyclerView = findViewById(R.id.applicationsRecyclerView);
        applicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter
        applicationList = new ArrayList<>();
        boolean isRecruiter = true; // Modify this to determine recruiter dynamically, if needed
        applicationsAdapter = new ApplicationsAdapter(this, applicationList, isRecruiter, this);
        applicationsRecyclerView.setAdapter(applicationsAdapter);

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
                            applicationList.add(application);
                            Log.d("ViewApplicationsActivity", "Fetched application ID: " + application.getId());
                        }
                    }
                    applicationsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading applications: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ViewApplicationsActivity", "Error loading applications: " + e.getMessage());
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
                    applicationsAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Application status updated to " + status, Toast.LENGTH_SHORT).show();
                    Log.i("ViewApplicationsActivity", "Application successfully updated.");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update application: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ViewApplicationsActivity", "Failed to update application: " + e.getMessage());
                });
    }
}