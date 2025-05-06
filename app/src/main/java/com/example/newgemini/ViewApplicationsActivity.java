package com.example.newgemini;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;

public class ViewApplicationsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private RecyclerView applicationsRecyclerView;
    private Spinner sectorFilterSpinner;
    private ApplicationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_applications);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        applicationsRecyclerView = findViewById(R.id.applicationsRecyclerView);
        sectorFilterSpinner = findViewById(R.id.sectorFilterSpinner);

        // Set up sector spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.job_sectors, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectorFilterSpinner.setAdapter(spinnerAdapter);

        // Add "All Sectors" option at the beginning
        ArrayList<String> sectors = new ArrayList<>();
        sectors.add("All Sectors");
        for (String sector : getResources().getStringArray(R.array.job_sectors)) {
            sectors.add(sector);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sectors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectorFilterSpinner.setAdapter(adapter);

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

        // Initial load
        loadApplications("All Sectors");
    }
