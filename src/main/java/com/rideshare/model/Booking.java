package com.rideshare.model;

import java.sql.Timestamp;

public class Booking {

    public enum BookingStatus { PENDING, ACCEPTED, REJECTED, CANCELLED }

    private int bookingId;
    private int rideId;
    private int seekerId;
    private String seekerName;
    private String seekerCollege;
    private String seekerPhone;
    private BookingStatus status;
    private Timestamp bookingTime;
    private Timestamp updatedAt;

    // Ride details for display
    private String routeFrom;
    private String routeTo;
    private String providerName;
    private String vehicleType;
    private double fare;

    public Booking() {}

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getRideId() { return rideId; }
    public void setRideId(int rideId) { this.rideId = rideId; }

    public int getSeekerId() { return seekerId; }
    public void setSeekerId(int seekerId) { this.seekerId = seekerId; }

    public String getSeekerName() { return seekerName; }
    public void setSeekerName(String seekerName) { this.seekerName = seekerName; }

    public String getSeekerCollege() { return seekerCollege; }
    public void setSeekerCollege(String seekerCollege) { this.seekerCollege = seekerCollege; }

    public String getSeekerPhone() { return seekerPhone; }
    public void setSeekerPhone(String seekerPhone) { this.seekerPhone = seekerPhone; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Timestamp getBookingTime() { return bookingTime; }
    public void setBookingTime(Timestamp bookingTime) { this.bookingTime = bookingTime; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getRouteFrom() { return routeFrom; }
    public void setRouteFrom(String routeFrom) { this.routeFrom = routeFrom; }

    public String getRouteTo() { return routeTo; }
    public void setRouteTo(String routeTo) { this.routeTo = routeTo; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
}
