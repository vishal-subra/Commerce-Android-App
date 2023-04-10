package com.example.beautystuffsss.model;

public class Booking {
    String bookingId, bookingTitle, bookingDate, bookingStatus;

    public Booking(String bookingId, String bookingTitle, String bookingDate, String bookingStatus) {
        this.bookingId = bookingId;
        this.bookingTitle = bookingTitle;
        this.bookingDate = bookingDate;
        this.bookingStatus = bookingStatus;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingTitle() {
        return bookingTitle;
    }

    public void setBookingTitle(String bookingTitle) {
        this.bookingTitle = bookingTitle;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
