package com.example.beautystuffsss.model;

public class Promotion {
    String promoId;
    String promoTitle;
    String promoDesc;
    String photoUrl;
    String datePosted;

    public Promotion(String promoId, String promoTitle, String promoDesc, String photoUrl, String datePosted) {
        this.promoId = promoId;
        this.promoTitle = promoTitle;
        this.promoDesc = promoDesc;
        this.photoUrl = photoUrl;
        this.datePosted = datePosted;
    }

    public String getPromoId() {
        return promoId;
    }

    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }

    public String getPromoTitle() {
        return promoTitle;
    }

    public void setPromoTitle(String promoTitle) {
        this.promoTitle = promoTitle;
    }

    public String getPromoDesc() {
        return promoDesc;
    }

    public void setPromoDesc(String promoDesc) {
        this.promoDesc = promoDesc;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }
}
