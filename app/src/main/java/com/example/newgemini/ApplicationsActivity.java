package com.example.newgemini;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationsActivity extends AppCompatActivity implements ApplicationsAdapter.OnApplicationActionListener {

    private RecyclerView applicationsRecyclerView;
    private ApplicationsAdapter applicationsAdapter;
    private List<Application> applicationList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private TextView noApplicationsTextView;
    private boolean isRecruiter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applications);

        // Initialize Firestore and FirebaseAuth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Determine if the user is a recruiter
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            isRecruiter = user.getUid().startsWith("recruiter_");
        } else {
            isRecruiter = false;
        }

        // Initialize UI components
        applicationsRecyclerView = findViewById(R.id.applicationsRecyclerView);
        noApplicationsTextView = findViewById(R.id.noApplicationsTextView);

        // Set up RecyclerView
        applicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        applicationList = new ArrayList<>();
        applicationsAdapter = new ApplicationsAdapter(this, applicationList, isRecruiter, this);
        applicationsRecyclerView.setAdapter(applicationsAdapter);

        // Fetch applications based on the user's role
        fetchApplications();
    }

    private void fetchApplications() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User is not signed in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        Query query;

        if (isRecruiter) {
            query = firestore.collection("applications")
                    .whereEqualTo("recruiterId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING);
        } else {
            query = firestore.collection("applications")
                    .whereEqualTo("studentId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    applicationList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Application application = doc.toObject(Application.class);
                            if (application != null) {
                                application.setId(doc.getId()); // Ensure the ID is set
                                applicationList.add(application);
                            }
                        }
                        applicationsAdapter.notifyDataSetChanged();
                        toggleRecyclerViewVisibility(true);
                    } else {
                        toggleRecyclerViewVisibility(false);
                    }
                })
                .addOnFailureListener(e -> {
                    toggleRecyclerViewVisibility(false);
                    Toast.makeText(this, "Error loading applications: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleRecyclerViewVisibility(boolean showRecyclerView) {
        if (showRecyclerView) {
            applicationsRecyclerView.setVisibility(View.VISIBLE);
            noApplicationsTextView.setVisibility(View.GONE);
        } else {
            applicationsRecyclerView.setVisibility(View.GONE);
            noApplicationsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAccept(Application application) {
        updateApplicationStatus(application, "Accepted", null);
    }

    @Override
    public void onReject(Application application) {
        showFeedbackDialog(application);
    }

    private void updateApplicationStatus(Application application, String status, String feedback) {
        String applicationId = application.getId();

        if (applicationId == null || applicationId.isEmpty()) {
            Toast.makeText(this, "Invalid application ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        if (feedback != null) {
            updates.put("feedback", feedback);
        }

        firestore.collection("applications").document(applicationId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    application.setStatus(status);
                    applicationsAdapter.notifyDataSetChanged();
                    String message = (feedback != null) ? "Application rejected with feedback." : "Application accepted.";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showFeedbackDialog(Application application) {
        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        android.widget.EditText feedbackInput = dialog.findViewById(R.id.feedbackInput);
        android.widget.Button submitButton = dialog.findViewById(R.id.submitFeedbackButton);
        android.widget.Button cancelButton = dialog.findViewById(R.id.cancelFeedbackButton);

        submitButton.setOnClickListener(v -> {
            String feedback = feedbackInput.getText().toString().trim();
            if (feedback.isEmpty()) {
                Toast.makeText(this, "Please provide feedback.", Toast.LENGTH_SHORT).show();
                return;
            }
            updateApplicationStatus(application, "Rejected", feedback);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}