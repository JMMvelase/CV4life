package com.example.newgemini;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ViewApplicationsActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private RecyclerView applicationsRecyclerView;
    private Spinner sectorFilterSpinner;
    private ApplicationsAdapter adapter;
    private ListenerRegistration applicationsListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applications);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        applicationsRecyclerView = findViewById(R.id.applicationsRecyclerView);
        sectorFilterSpinner = findViewById(R.id.sectorFilterSpinner);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Setup RecyclerView
        applicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApplicationsAdapter(this, true); // true for recruiter view
        applicationsRecyclerView.setAdapter(adapter);

        // Setup spinner
        setupSectorSpinner();

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshApplications);

        // Initial load
        loadApplications("All Sectors");
    }

    private void setupSectorSpinner() {
        ArrayList<String> sectors = new ArrayList<>();
        sectors.add("All Sectors");
        sectors.addAll(Arrays.asList(getResources().getStringArray(R.array.job_sectors)));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, sectors);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sectorFilterSpinner.setAdapter(spinnerAdapter);

        sectorFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSector = parent.getItemAtPosition(position).toString();
                loadApplications(selectedSector);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadApplications("All Sectors");
            }
        });
    }

    private void loadApplications(String sector) {
        // Remove previous listener
        if (applicationsListener != null) {
            applicationsListener.remove();
        }

        String userId = auth.getCurrentUser().getUid();
        Query query = firestore.collection("applications")
                .whereEqualTo("recruiterId", userId);

        if (!"All Sectors".equals(sector)) {
            query = query.whereEqualTo("jobSector", sector);
        }

        // Add real-time listener
        applicationsListener = query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error loading applications: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            List<JobApplication> applications = new ArrayList<>();
            if (value != null) {
                for (DocumentSnapshot doc : value.getDocuments()) {
                    JobApplication application = doc.toObject(JobApplication.class);
                    if (application != null) {
                        application.setApplicationId(doc.getId());
                        applications.add(application);
                    }
                }
            }

            adapter.updateApplications(applications);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void refreshApplications() {
        String selectedSector = sectorFilterSpinner.getSelectedItem().toString();
        loadApplications(selectedSector);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (applicationsListener != null) {
            applicationsListener.remove();
        }
    }
}