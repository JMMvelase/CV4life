package com.example.newgemini;

import java.util.Date;

public class Application {
    private String id;
    private String jobId;
    private String title;
    private String recruiterId;
    private String studentId;
    private String status;
    private Date createdAt;
    private transient String cvBase64;
    private transient String coverLetter;

    // These fields might exist in Firestore but are not needed in the app


    // Default constructor required for Firestore
    public Application() {}

    // Getters and setters for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(String recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getCvBase64() { return cvBase64; }
    public void setCvBase64(String cvBase64) { this.cvBase64 = cvBase64; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}