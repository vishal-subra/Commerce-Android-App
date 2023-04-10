package com.example.beautystuffsss.model;

public class PharmacistChat {
    String name;
    String photoUrl;
    String pharmacistId;
    String latestMessage;

    public PharmacistChat(String name, String photoUrl, String pharmacistId, String latestMessage) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.pharmacistId = pharmacistId;
        this.latestMessage = latestMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPharmacistId() {
        return pharmacistId;
    }

    public void setPharmacistId(String pharmacistId) {
        this.pharmacistId = pharmacistId;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }
}
