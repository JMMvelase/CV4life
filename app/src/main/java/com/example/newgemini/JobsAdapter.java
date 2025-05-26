package com.example.newgemini;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
        this.firestore = FirebaseFirestore.getInstance();
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
            firestore.collection("jobs")
                    .whereEqualTo("recruiterId", application.getRecruiterId())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String jobTitle = queryDocumentSnapshots.getDocuments().get(0).getString("title");
                            application.setTitle(jobTitle);
                            holder.jobTitle.setText(jobTitle);
                        } else {
                            holder.jobTitle.setText("No Title Available");
                        }
                    })
                    .addOnFailureListener(e -> holder.jobTitle.setText("Error Fetching Title"));
        } else {
            holder.jobTitle.setText(application.getTitle());
        }

        holder.status.setText(application.getStatus());

        if (isRecruiter) {
            // Show Accept and Reject buttons
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.rejectButton.setVisibility(View.VISIBLE);

            holder.acceptButton.setOnClickListener(v -> actionListener.onAccept(application));
            holder.rejectButton.setOnClickListener(v -> actionListener.onReject(application));

            // Show cover letter
            holder.coverLetter.setVisibility(View.VISIBLE);
            holder.coverLetter.setText(
                    application.getCoverLetter() != null ? application.getCoverLetter() : "No cover letter"
            );

            // Show CV image
            holder.cvImageView.setVisibility(View.VISIBLE);
            if (application.getCvBase64() != null && !application.getCvBase64().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(application.getCvBase64(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.cvImageView.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    holder.cvImageView.setImageResource(android.R.color.darker_gray);
                }
            } else {
                holder.cvImageView.setImageResource(android.R.color.darker_gray);
            }

            // Fetch student details
            holder.applicantName.setVisibility(View.VISIBLE);
            holder.applicantEmail.setVisibility(View.VISIBLE);

            String studentId = application.getStudentId();
            if (studentId != null) {
                firestore.collection("students").document(studentId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String name = documentSnapshot.getString("name");
                                String email = documentSnapshot.getString("email");
                                holder.applicantName.setText(name != null ? name : " ");
                                holder.applicantEmail.setText(email != null ? email : " ");
                            } else {
                                holder.applicantName.setText(" ");
                                holder.applicantEmail.setText(" ");
                            }
                        })
                        .addOnFailureListener(e -> {
                            holder.applicantName.setText("Error");
                            holder.applicantEmail.setText("Error");
                        });
            } else {
                holder.applicantName.setText(" ");
                holder.applicantEmail.setText(" ");
            }
        } else {
            // Hide recruiter-only views
            holder.acceptButton.setVisibility(View.GONE);
            holder.rejectButton.setVisibility(View.GONE);
            holder.applicantName.setVisibility(View.GONE);
            holder.applicantEmail.setVisibility(View.GONE);

            // Show cover letter to student
            holder.coverLetter.setVisibility(View.VISIBLE);
            holder.coverLetter.setText(
                    application.getCoverLetter() != null ? application.getCoverLetter() : "No cover letter"
            );

            // Show CV to student
            holder.cvImageView.setVisibility(View.VISIBLE);
            if (application.getCvBase64() != null && !application.getCvBase64().isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(application.getCvBase64(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.cvImageView.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    holder.cvImageView.setImageResource(android.R.color.darker_gray);
                }
            } else {
                holder.cvImageView.setImageResource(android.R.color.darker_gray);
            }
        }
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, status;
        Button acceptButton, rejectButton;
        TextView applicantName, applicantEmail, coverLetter;
        ImageView cvImageView;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            status = itemView.findViewById(R.id.status);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            applicantName = itemView.findViewById(R.id.applicantName);
            applicantEmail = itemView.findViewById(R.id.applicantEmail);
            coverLetter = itemView.findViewById(R.id.coverLetter);
            cvImageView = itemView.findViewById(R.id.cvImageView);
        }
    }

    public interface OnApplicationActionListener {
        void onAccept(Application application);
        void onReject(Application application);
    }
}
