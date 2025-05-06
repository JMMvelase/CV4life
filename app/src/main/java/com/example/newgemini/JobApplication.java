package com.example.newgemini;

import com.google.firebase.Timestamp;
import java.util.Date;

public class JobApplication {
    private String applicationId;
    private String jobId;
    private String jobTitle;
    private String companyName;
    private String studentId;
    private String studentName;
    private String recruiterId;
    private String status;
    private String jobSector;
    private Date appliedAt;
    private Date updatedAt;
    private String studentCV;

    // Default constructor required for Firestore
    public JobApplication() {}

    // Getters and Setters
    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRecruiterId() { return recruiterId; }
    public void setRecruiterId(String recruiterId) { this.recruiterId = recruiterId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getJobSector() { return jobSector; }
    public void setJobSector(String jobSector) { this.jobSector = jobSector; }

    public Date getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Date appliedAt) { this.appliedAt = appliedAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getStudentCV() { return studentCV; }
    public void setStudentCV(String studentCV) { this.studentCV = studentCV; }
}