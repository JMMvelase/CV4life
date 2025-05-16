package com.example.newgemini;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ApplicationViewHolder> {

    private final Context context;
    private final List<Application> applications;
    private final boolean isRecruiter;
    private final OnApplicationActionListener actionListener;

    public ApplicationsAdapter(Context context, List<Application> applications, boolean isRecruiter, OnApplicationActionListener actionListener) {
        this.context = context;
        this.applications = applications;
        this.isRecruiter = isRecruiter;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    public void updateApplications(List<Application> newApplications) {
        applications.clear(); // Clear the existing dataset
        applications.addAll(newApplications); // Add the new dataset
        notifyDataSetChanged(); // Notify the adapter to refresh the UI
    }
    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = applications.get(position);

        holder.jobTitle.setText(application.getJobId()); // Replace with a proper Job Title if available
        holder.status.setText(application.getStatus());

        if (isRecruiter) {
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.rejectButton.setVisibility(View.VISIBLE);

            holder.acceptButton.setOnClickListener(v -> actionListener.onAccept(application));
            holder.rejectButton.setOnClickListener(v -> actionListener.onReject(application));
        } else {
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
            jobTitle = itemView.findViewById(R.id.jobTitle);
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