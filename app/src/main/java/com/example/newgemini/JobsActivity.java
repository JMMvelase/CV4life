package com.example.newgemini;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class JobsActivity extends AppCompatActivity implements JobsAdapter.ApplyJobCallback {

    private FirebaseFirestore firestore;
    private RecyclerView jobsRecyclerView;
    private JobsAdapter jobsAdapter;
    private List<Job> jobList;
    private Spinner jobFilterSpinner;
    private TextView noJobsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        Toolbar toolbar = findViewById(R.id.jobsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Jobs Available");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();

        jobFilterSpinner = findViewById(R.id.jobFilterSpinner);
        jobsRecyclerView = findViewById(R.id.jobsRecyclerView);
        noJobsTextView = findViewById(R.id.noJobsTextView);
        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobList = new ArrayList<>();
        jobsAdapter = new JobsAdapter(jobList, this, this);
        jobsRecyclerView.setAdapter(jobsAdapter);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.job_sectors, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobFilterSpinner.setAdapter(spinnerAdapter);

        jobFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSector = parent.getItemAtPosition(position).toString();

                if ("All".equalsIgnoreCase(selectedSector)) {
                    fetchAllJobsFromFirestore();
                } else {
                    fetchJobsFromFirestore(selectedSector);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fetchAllJobsFromFirestore();
            }
        });

        fetchAllJobsFromFirestore();
    }

    private void fetchAllJobsFromFirestore() {
        firestore.collection("jobs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        jobList.clear();
                        jobList.addAll(queryDocumentSnapshots.toObjects(Job.class));
                        jobsAdapter.notifyDataSetChanged();
                        toggleRecyclerViewVisibility(true);
                    } else {
                        toggleRecyclerViewVisibility(false);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchJobsFromFirestore(String sectorFilter) {
        firestore.collection("jobs")
                .whereEqualTo("sector", sectorFilter)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        jobList.clear();
                        jobList.addAll(queryDocumentSnapshots.toObjects(Job.class));
                        jobsAdapter.notifyDataSetChanged();
                        toggleRecyclerViewVisibility(true);
                    } else {
                        toggleRecyclerViewVisibility(false);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching jobs: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void toggleRecyclerViewVisibility(boolean showRecyclerView) {
        if (showRecyclerView) {
            jobsRecyclerView.setVisibility(View.VISIBLE);
            noJobsTextView.setVisibility(View.GONE);
        } else {
            jobsRecyclerView.setVisibility(View.GONE);
            noJobsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onApply(String jobId, String recruiterId) {
        Intent intent = new Intent(this, ApplyJobActivity.class);
        intent.putExtra("jobId", jobId);
        intent.putExtra("recruiterId", recruiterId);
        startActivity(intent);
    }
}