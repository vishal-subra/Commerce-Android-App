package com.example.beautystuffsss.model;

public class HealthInfo {
    String infoTitle, infoId, infoDesc, infoYoutubeLink;
    String photoUrl;

    public HealthInfo(String infoId, String infoTitle, String infoDesc, String infoYoutubeLink, String photoUrl) {
        this.infoId = infoId;
        this.infoTitle = infoTitle;
        this.infoDesc = infoDesc;
        this.infoYoutubeLink = infoYoutubeLink;
        this.photoUrl = photoUrl;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getInfoTitle() {
        return infoTitle;
    }

    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public String getInfoDesc() {
        return infoDesc;
    }

    public void setInfoDesc(String infoDesc) {
        this.infoDesc = infoDesc;
    }

    public String getInfoYoutubeLink() {
        return infoYoutubeLink;
    }

    public void setInfoYoutubeLink(String infoYoutubeLink) {
        this.infoYoutubeLink = infoYoutubeLink;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
