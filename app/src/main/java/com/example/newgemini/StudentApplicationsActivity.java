package com.example.newgemini;

import static com.example.newgemini.R.id.applicationsRecyclerView;

import android.os.Bundle;
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

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private RecyclerView applicationsRecyclerView;
    private ApplicationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_applications);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        applicationsRecyclerView = findViewById(R.id.applicationsRecyclerView);
        applicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ApplicationsAdapter(this, false); // false for student view
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
                    List<JobApplication> applications = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        JobApplication application = doc.toObject(JobApplication.class);
                        if (application != null) {
                            application.setApplicationId(doc.getId());
                            applications.add(application);
                        }
                    }
                    adapter.updateApplications(applications);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading applications: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}