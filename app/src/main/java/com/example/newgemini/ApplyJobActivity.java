package com.example.newgemini;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class ApplyJobActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText coverLetterEditText;
    private ImageView cvImageView;
    private Button uploadCvButton, submitApplicationButton;
    private ProgressBar progressBar;                   // NEW

    private String jobId;
    private String recruiterId;
    private String base64Cv;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        jobId = getIntent().getStringExtra("jobId");
        recruiterId = getIntent().getStringExtra("recruiterId");

        coverLetterEditText  = findViewById(R.id.coverLetterEditText);
        cvImageView          = findViewById(R.id.cvImageView);
        uploadCvButton       = findViewById(R.id.uploadCvButton);
        submitApplicationButton = findViewById(R.id.submitApplicationButton);
        progressBar          = findViewById(R.id.progressBar);        // NEW

        uploadCvButton.setOnClickListener(v -> openFileChooser());
        submitApplicationButton.setOnClickListener(v -> submitApplication());
    }

    /** Convenience helper to flip the UI into / out of "loading" mode */
    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        uploadCvButton.setEnabled(!loading);
        submitApplicationButton.setEnabled(!loading);
        coverLetterEditText.setEnabled(!loading);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {

            setLoading(true);                              // show spinner while we work
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                cvImageView.setImageBitmap(bitmap);
                base64Cv = encodeImageToBase64(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Error loading image. Please try again.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            setLoading(false);                             // hide when done
        } else if (requestCode == PICK_IMAGE_REQUEST) {
            Toast.makeText(this, "No file selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void submitApplication() {
        String coverLetter = coverLetterEditText.getText().toString().trim();

        if (coverLetter.isEmpty()) {
            Toast.makeText(this, "Please provide a cover letter.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (base64Cv == null || base64Cv.isEmpty()) {
            Toast.makeText(this, "Please upload your CV.", Toast.LENGTH_SHORT).show();
            return;
        }
        String studentId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (studentId == null) {
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build application object
        Map<String, Object> application = new HashMap<>();
        application.put("jobId", jobId);
        application.put("recruiterId", recruiterId);
        application.put("studentId", studentId);
        application.put("coverLetter", coverLetter);
        application.put("cvBase64", base64Cv);
        application.put("status", "Pending");
        application.put("createdAt", Timestamp.now());

        setLoading(true);                                    // SHOW spinner
        firestore.collection("applications")
                .add(application)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Application submitted successfully!", Toast.LENGTH_SHORT).show();
                    setLoading(false);                       // HIDE spinner
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit application: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setLoading(false);                       // HIDE spinner
                    e.printStackTrace();
                });
    }
}
