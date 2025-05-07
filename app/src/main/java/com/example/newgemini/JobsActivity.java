package com.example.newgemini;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class JobsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private RecyclerView jobsRecyclerView;
    private JobsAdapter jobsAdapter;
    private List<Job> jobList;
    private Spinner jobFilterSpinner;
    private TextView noJobsTextView; // TextView to display when no jobs are available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        // Set up the custom Toolbar
        Toolbar toolbar = findViewById(R.id.jobsToolbar);
        setSupportActionBar(toolbar); // Set the custom Toolbar as the ActionBar
        getSupportActionBar().setTitle("Jobs Available"); // Set the title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI Components
        jobFilterSpinner = findViewById(R.id.jobFilterSpinner);
        jobsRecyclerView = findViewById(R.id.jobsRecyclerView);
        noJobsTextView = findViewById(R.id.noJobsTextView); // Initialize the no-jobs TextView
        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobList = new ArrayList<>();
        jobsAdapter = new JobsAdapter(jobList, this);
        jobsRecyclerView.setAdapter(jobsAdapter);

        // Set up Spinner for Job Sector Filtering
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.job_sectors, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobFilterSpinner.setAdapter(spinnerAdapter);

        // Spinner item selection listener
        jobFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSector = parent.getItemAtPosition(position).toString();

                // Avoid filtering if "All" is selected
                if ("All".equalsIgnoreCase(selectedSector)) {
                    fetchAllJobsFromFirestore();
                } else {
                    fetchJobsFromFirestore(selectedSector);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fetchAllJobsFromFirestore(); // Default to fetching all jobs if nothing is selected
            }
        });

        // Fetch all jobs initially
        fetchAllJobsFromFirestore();
    }

    /**
     * Fetch all jobs without filtering.
     */
    private void fetchAllJobsFromFirestore() {
        firestore.collection("jobs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        jobList.clear();
                        jobList.addAll(queryDocumentSnapshots.toObjects(Job.class));
                        jobsAdapter.notifyDataSetChanged();
                        toggleRecyclerViewVisibility(true); // Show the RecyclerView
                    } else {
                        toggleRecyclerViewVisibility(false); // Hide the RecyclerView
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Fetch jobs filtered by the selected sector.
     * @param sectorFilter The sector to filter jobs by.
     */
    private void fetchJobsFromFirestore(String sectorFilter) {
        firestore.collection("jobs")
                .whereEqualTo("sector", sectorFilter)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        jobList.clear();
                        jobList.addAll(queryDocumentSnapshots.toObjects(Job.class));
                        jobsAdapter.notifyDataSetChanged();
                        toggleRecyclerViewVisibility(true); // Show the RecyclerView
                    } else {
                        toggleRecyclerViewVisibility(false); // Hide the RecyclerView
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Toggles the visibility of the RecyclerView and the no-jobs TextView.
     * @param showRecyclerView True to show the RecyclerView, false to hide it and show the TextView.
     */
    private void toggleRecyclerViewVisibility(boolean showRecyclerView) {
        if (showRecyclerView) {
            jobsRecyclerView.setVisibility(View.VISIBLE);
            noJobsTextView.setVisibility(View.GONE);
        } else {
            jobsRecyclerView.setVisibility(View.GONE);
            noJobsTextView.setVisibility(View.VISIBLE);
        }
    }
}