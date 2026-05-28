package com.rideshare.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Ride {

    public enum VehicleType { BIKE, CAR, LIGHT_VEHICLE }
    public enum RideStatus { ACTIVE, FULL, CANCELLED, COMPLETED }

    private int rideId;
    private int providerId;
    private String providerName;
    private String providerCollege;
    private VehicleType vehicleType;
    private String routeFrom;
    private String routeTo;
    private LocalDateTime departureTime;
    private int totalSeats;
    private int availableSeats;
    private double fare;
    private RideStatus status;
    private Timestamp createdAt;

    public Ride() {}

    public int getRideId() { return rideId; }
    public void setRideId(int rideId) { this.rideId = rideId; }

    public int getProviderId() { return providerId; }
    public void setProviderId(int providerId) { this.providerId = providerId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getProviderCollege() { return providerCollege; }
    public void setProviderCollege(String providerCollege) { this.providerCollege = providerCollege; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    public String getRouteFrom() { return routeFrom; }
    public void setRouteFrom(String routeFrom) { this.routeFrom = routeFrom; }

    public String getRouteTo() { return routeTo; }
    public void setRouteTo(String routeTo) { this.routeTo = routeTo; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getMaxPassengers() {
        return vehicleType == VehicleType.BIKE ? 1 : totalSeats;
    }
}
