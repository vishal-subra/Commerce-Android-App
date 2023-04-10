package com.example.beautystuffsss.model;

public class Service {
    String serviceId, title,description;

    public Service(String serviceId, String title, String description) {
        this.serviceId = serviceId;
        this.title = title;
        this.description = description;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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
}
