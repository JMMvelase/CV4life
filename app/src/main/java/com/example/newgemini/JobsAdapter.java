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

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.JobViewHolder> {

    private final List<Job> jobList;
    private final Context context;
    private ApplyJobCallback applyJobCallback;

    public interface ApplyJobCallback {
        void onApply(String jobId, String recruiterId);
    }

    public JobsAdapter(List<Job> jobList, Context context) {
        this.jobList = jobList;
        this.context = context;
        this.applyJobCallback = applyJobCallback;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);

        holder.jobTitleTextView.setText(job.getTitle());
        holder.jobDescriptionTextView.setText(job.getDescription());
        holder.jobSectorTextView.setText(job.getSector());

        // Apply Button Click Listener
        holder.applyButton.setOnClickListener(v -> {
            if (applyJobCallback != null) {
                applyJobCallback.onApply(job.getJobId(), job.getRecruiterId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleTextView, jobDescriptionTextView, jobSectorTextView;
        Button applyButton;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleTextView = itemView.findViewById(R.id.jobTitleTextView);
            jobDescriptionTextView = itemView.findViewById(R.id.jobDescriptionTextView);
            jobSectorTextView = itemView.findViewById(R.id.jobSectorTextView);
            applyButton = itemView.findViewById(R.id.jobApplyButton);
        }
    }
}