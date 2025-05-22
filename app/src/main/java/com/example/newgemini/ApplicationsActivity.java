package com.example.newgemini;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        isRecruiter = user != null && user.getUid().startsWith("recruiter_");

        applicationsRecyclerView = findViewById(R.id.applicationsRecyclerView);
        noApplicationsTextView = findViewById(R.id.noApplicationsTextView);

        applicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        applicationList = new ArrayList<>();
        applicationsAdapter = new ApplicationsAdapter(this, applicationList, isRecruiter, this);
        applicationsRecyclerView.setAdapter(applicationsAdapter);

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
                                application.setId(doc.getId());
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
        // HARDCODE EMAIL INTENT, NOTHING ELSE
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:someone@example.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Test Body");
        try {
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReject(Application application) {
        showFeedbackDialog(application);
    }

    private void showFeedbackDialog(Application application) {
        final Dialog dialog = new Dialog(this);
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
            testLaunchEmailIntent(application, "Rejected", feedback);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Forcefully opens email intent before updating Firestore
    private void testLaunchEmailIntent(Application application, String rejected, String feedback) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:someone@example.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Test Body");
        try {
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchEmailIntent(String recipientEmail, String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + recipientEmail));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
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
}