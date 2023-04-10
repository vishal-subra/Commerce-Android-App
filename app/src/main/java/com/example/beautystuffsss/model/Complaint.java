package com.example.beautystuffsss.model;

public class Complaint {
    String complaintId, complaintFor, complaintStatus,complainerId;

    public Complaint(String complaintId, String complaintFor,String complaintStatus,String complainerId) {
        this.complaintId = complaintId;
        this.complaintFor = complaintFor;
        this.complaintStatus = complaintStatus;
        this.complainerId = complainerId;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public String getComplaintFor() {
        return complaintFor;
    }

    public void setComplaintFor(String complaintFor) {
        this.complaintFor = complaintFor;
    }

    public String getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(String complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public String getComplainerId() {
        return complainerId;
    }

    public void setComplainerId(String complainerId) {
        this.complainerId = complainerId;
    }
}
