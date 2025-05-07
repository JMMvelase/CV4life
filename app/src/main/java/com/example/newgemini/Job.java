package com.example.newgemini;

import java.util.Date;

public class Job {
    private String jobId; // Unique identifier for the job
    private String title; // Job title
    private String description; // Job description
    private String requirements; // Job requirements or qualifications
    private String sector; // Sector or industry of the job
    private String recruiterId; // ID of the recruiter posting the job
    private Date createdAt; // Timestamp for job creation
    private String status; // Status of the job (e.g., Open, Closed)

    // Default constructor required for Firestore
    public Job() {}

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(String recruiterId) {
        this.recruiterId = recruiterId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}