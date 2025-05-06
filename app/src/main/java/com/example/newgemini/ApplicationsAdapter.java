package com.example.newgemini;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ApplicationViewHolder> {
    private final List<JobApplication> applications;
    private final Context context;
    private final boolean isRecruiter;
    private final FirebaseFirestore firestore;

    public ApplicationsAdapter(Context context, boolean isRecruiter) {
        this.context = context;
        this.applications = new ArrayList<>();
        this.isRecruiter = isRecruiter;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                com.example.newgemini.R.layout.item_application,
                parent,
                false
        );
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        JobApplication application = applications.get(position);

        holder.jobTitleText.setText(application.getJobTitle());
        holder.companyNameText.setText(application.getCompanyName());
        holder.statusText.setText(application.getStatus());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateApplied = sdf.format(application.getAppliedAt());
        holder.dateAppliedText.setText(context.getString(com.example.newgemini.R.string.applied_date, dateApplied));

        if (isRecruiter) {
            holder.responseButtons.setVisibility(View.VISIBLE);
            holder.studentNameText.setVisibility(View.VISIBLE);
            holder.studentNameText.setText(application.getStudentName());
            setupResponseButtons(holder, application);
        } else {
            holder.responseButtons.setVisibility(View.GONE);
            holder.studentNameText.setVisibility(View.GONE);
        }

        // Set status color
        int statusColor;
        switch (application.getStatus().toLowerCase()) {
            case "accepted":
                statusColor = context.getColor(com.example.newgemini.R.color.primaryColor);
                break;
            case "rejected":
                statusColor = context.getColor(com.example.newgemini.R.color.colorPrimaryDark);
                break;
            default:
                statusColor = context.getColor(com.example.newgemini.R.color.accent);
                break;
        }
        holder.statusText.setTextColor(statusColor);
    }

    private void setupResponseButtons(ApplicationViewHolder holder, JobApplication application) {
        if (!"pending".equalsIgnoreCase(application.getStatus())) {
            holder.responseButtons.setVisibility(View.GONE);
            return;
        }

        holder.acceptButton.setOnClickListener(v -> {
            updateApplicationStatus(application, "accepted");
            sendNotification(application, "accepted");
        });

        holder.rejectButton.setOnClickListener(v -> {
            updateApplicationStatus(application, "rejected");
            sendNotification(application, "rejected");
        });

        holder.shortlistButton.setOnClickListener(v -> {
            updateApplicationStatus(application, "shortlisted");
            sendNotification(application, "shortlisted");
        });
    }

    private void updateApplicationStatus(JobApplication application, String newStatus) {
        firestore.collection("applications")
                .document(application.getApplicationId())
                .update(
                        "status", newStatus,
                        "updatedAt", new Timestamp(new Date())
                )
                .addOnSuccessListener(aVoid -> {
                    application.setStatus(newStatus);
                    notifyDataSetChanged();
                });
    }

    private void sendNotification(JobApplication application, String status) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", application.getStudentId());
        notification.put("title", context.getString(com.example.newgemini.R.string.application_update));
        notification.put("message", getNotificationMessage(status, application.getJobTitle()));
        notification.put("timestamp", new Timestamp(new Date()));
        notification.put("read", false);

        firestore.collection("notifications").add(notification);
    }

    private String getNotificationMessage(String status, String jobTitle) {
        switch (status.toLowerCase()) {
            case "accepted":
                return context.getString(com.example.newgemini.R.string.application_accepted, jobTitle);
            case "rejected":
                return context.getString(com.example.newgemini.R.string.application_rejected, jobTitle);
            case "shortlisted":
                return context.getString(com.example.newgemini.R.string.application_shortlisted, jobTitle);
            default:
                return context.getString(com.example.newgemini.R.string.application_status_updated, jobTitle);
        }
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public void updateApplications(List<JobApplication> newApplications) {
        this.applications.clear();
        this.applications.addAll(newApplications);
        notifyDataSetChanged();
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        final TextView jobTitleText;
        final TextView companyNameText;
        final TextView statusText;
        final TextView dateAppliedText;
        final TextView studentNameText;
        final View responseButtons;
        final Button acceptButton;
        final Button rejectButton;
        final Button shortlistButton;

        ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleText = itemView.findViewById(com.example.newgemini.R.id.jobTitleText);
            companyNameText = itemView.findViewById(com.example.newgemini.R.id.companyNameText);
            statusText = itemView.findViewById(com.example.newgemini.R.id.statusText);
            dateAppliedText = itemView.findViewById(com.example.newgemini.R.id.dateAppliedText);
            studentNameText = itemView.findViewById(com.example.newgemini.R.id.studentNameText);
            responseButtons = itemView.findViewById(com.example.newgemini.R.id.responseButtons);
            acceptButton = itemView.findViewById(com.example.newgemini.R.id.acceptButton);
            rejectButton = itemView.findViewById(com.example.newgemini.R.id.rejectButton);
            shortlistButton = itemView.findViewById(com.example.newgemini.R.id.shortlistButton);
        }
    }
}