package com.example.newgemini;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ApplicationViewHolder> {

    private final Context context;
    private final List<Application> applications;
    private final boolean isRecruiter;
    private final OnApplicationActionListener actionListener;
    private final FirebaseFirestore firestore;

    public ApplicationsAdapter(Context context, List<Application> applications, boolean isRecruiter, OnApplicationActionListener actionListener) {
        this.context = context;
        this.applications = applications;
        this.isRecruiter = isRecruiter;
        this.actionListener = actionListener;
        this.firestore = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = applications.get(position);

        // Fetch the job title from Firestore dynamically if not available
        if (application.getTitle() == null || application.getTitle().isEmpty()) {
            Log.d("ApplicationsAdapter", "Fetching title for recruiterId: " + application.getRecruiterId());
            firestore.collection("jobs")
                    .whereEqualTo("recruiterId", application.getRecruiterId())
                    .limit(1) // Assuming each recruiter has one job or fetching the first job
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String jobTitle = queryDocumentSnapshots.getDocuments().get(0).getString("title");
                            application.setTitle(jobTitle); // Update the Application object
                            holder.jobTitle.setText(jobTitle); // Bind the fetched title to the TextView
                        } else {
                            holder.jobTitle.setText("No Title Available");
                            Log.e("ApplicationsAdapter", "No job found for recruiterId: " + application.getRecruiterId());
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.jobTitle.setText("Error Fetching Title");
                        Log.e("ApplicationsAdapter", "Failed to fetch job title: " + e.getMessage());
                    });
        } else {
            // If title is already available, bind it directly
            holder.jobTitle.setText(application.getTitle());
        }

        // Bind the application status to the status TextView
        holder.status.setText(application.getStatus());

        // Show Accept and Reject buttons for recruiters
        if (isRecruiter) {
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.rejectButton.setVisibility(View.VISIBLE);

            holder.acceptButton.setOnClickListener(v -> actionListener.onAccept(application));
            holder.rejectButton.setOnClickListener(v -> actionListener.onReject(application));
        } else {
            // Hide buttons for students
            holder.acceptButton.setVisibility(View.GONE);
            holder.rejectButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, status;
        Button acceptButton, rejectButton;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle); // Ensure this matches the ID in the XML
            status = itemView.findViewById(R.id.status);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }

    public interface OnApplicationActionListener {
        void onAccept(Application application);

        void onReject(Application application);
    }
}