package com.rideshare.controller;

import com.rideshare.dao.BookingDAO;
import com.rideshare.dao.NotificationDAO;
import com.rideshare.dao.RideDAO;
import com.rideshare.model.Booking;
import com.rideshare.model.Booking.BookingStatus;
import com.rideshare.model.Notification;
import com.rideshare.model.Notification.NotificationType;
import com.rideshare.model.Ride;
import com.rideshare.model.Ride.RideStatus;
import com.rideshare.model.Ride.VehicleType;
import com.rideshare.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class RideController {

    private final RideDAO rideDAO = new RideDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public boolean offerRide(VehicleType vehicleType, String routeFrom, String routeTo,
                             LocalDateTime departureTime, int seats, double fare) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("Not logged in.");
        }

        if (routeFrom == null || routeFrom.trim().isEmpty()) {
            throw new IllegalArgumentException("Starting point is required.");
        }
        if (routeTo == null || routeTo.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination is required.");
        }
        if (departureTime == null || departureTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Departure time must be in the future.");
        }
        if (vehicleType == VehicleType.BIKE && seats != 1) {
            throw new IllegalArgumentException("Bike can only carry 1 passenger.");
        }
        if (seats <= 0) {
            throw new IllegalArgumentException("Available seats must be at least 1.");
        }
        if (fare < 0) {
            throw new IllegalArgumentException("Fare cannot be negative.");
        }

        Ride ride = new Ride();
        ride.setProviderId(currentUser.getUserId());
        ride.setVehicleType(vehicleType);
        ride.setRouteFrom(routeFrom.trim());
        ride.setRouteTo(routeTo.trim());
        ride.setDepartureTime(departureTime);
        ride.setTotalSeats(seats);
        ride.setAvailableSeats(seats);
        ride.setFare(fare);
        ride.setStatus(RideStatus.ACTIVE);

        return rideDAO.createRide(ride);
    }

    public List<Ride> getAvailableRides() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("Not logged in.");
        }
        return rideDAO.getAvailableRides(currentUser.getUserId());
    }

    public List<Ride> getMyOfferedRides() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("Not logged in.");
        }
        return rideDAO.getRidesByProvider(currentUser.getUserId());
    }

    public boolean bookRide(int rideId) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("Not logged in.");
        }

        Ride ride = rideDAO.getRideById(rideId);
        if (ride == null) {
            throw new IllegalArgumentException("Ride not found.");
        }
        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new IllegalArgumentException("This ride is no longer available.");
        }
        if (ride.getAvailableSeats() <= 0) {
            throw new IllegalArgumentException("No seats available.");
        }
        if (ride.getProviderId() == currentUser.getUserId()) {
            throw new IllegalArgumentException("You cannot book your own ride.");
        }
        if (bookingDAO.hasExistingBooking(rideId, currentUser.getUserId())) {
            throw new IllegalArgumentException("You already have a booking for this ride.");
        }

        Booking booking = new Booking();
        booking.setRideId(rideId);
        booking.setSeekerId(currentUser.getUserId());

        boolean success = bookingDAO.createBooking(booking);
        if (success) {
            notificationDAO.createNotification(new Notification(
                ride.getProviderId(),
                currentUser.getFullName() + " has requested a seat on your ride from "
                    + ride.getRouteFrom() + " to " + ride.getRouteTo() + ".",
                NotificationType.BOOKING_REQUEST
            ));
        }
        return success;
    }

    public boolean acceptBooking(int bookingId, int rideId) {
        Ride ride = rideDAO.getRideById(rideId);
        if (ride == null || ride.getAvailableSeats() <= 0) {
            return false;
        }

        boolean seatDecremented = rideDAO.decrementAvailableSeats(rideId);
        if (!seatDecremented) {
            return false;
        }

        boolean updated = bookingDAO.updateBookingStatus(bookingId, BookingStatus.ACCEPTED);
        if (updated) {
            List<Booking> bookings = bookingDAO.getBookingsForRide(rideId);
            for (Booking b : bookings) {
                if (b.getBookingId() == bookingId) {
                    notificationDAO.createNotification(new Notification(
                        b.getSeekerId(),
                        "Your booking for the ride from " + ride.getRouteFrom()
                            + " to " + ride.getRouteTo() + " has been ACCEPTED!",
                        NotificationType.RIDE_ACCEPTED
                    ));
                    break;
                }
            }
        } else {
            rideDAO.incrementAvailableSeats(rideId);
        }
        return updated;
    }

    public boolean rejectBooking(int bookingId, int rideId) {
        boolean updated = bookingDAO.updateBookingStatus(bookingId, BookingStatus.REJECTED);
        if (updated) {
            List<Booking> bookings = bookingDAO.getBookingsForRide(rideId);
            for (Booking b : bookings) {
                if (b.getBookingId() == bookingId) {
                    Ride ride = rideDAO.getRideById(rideId);
                    notificationDAO.createNotification(new Notification(
                        b.getSeekerId(),
                        "Your booking for the ride from " + ride.getRouteFrom()
                            + " to " + ride.getRouteTo() + " has been REJECTED.",
                        NotificationType.RIDE_REJECTED
                    ));
                    break;
                }
            }
        }
        return updated;
    }

    public boolean cancelBooking(int bookingId, int rideId) {
        List<Booking> bookings = bookingDAO.getBookingsForRide(rideId);
        BookingStatus previousStatus = null;
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId) {
                previousStatus = b.getStatus();
                break;
            }
        }

        boolean updated = bookingDAO.updateBookingStatus(bookingId, BookingStatus.CANCELLED);
        if (updated) {
            if (previousStatus == BookingStatus.ACCEPTED) {
                rideDAO.incrementAvailableSeats(rideId);
            }

            Ride ride = rideDAO.getRideById(rideId);
            if (ride != null) {
                notificationDAO.createNotification(new Notification(
                    ride.getProviderId(),
                    "A passenger has cancelled their booking for your ride from "
                        + ride.getRouteFrom() + " to " + ride.getRouteTo() + ".",
                    NotificationType.BOOKING_CANCELLED
                ));
            }
        }
        return updated;
    }

    public boolean cancelRide(int rideId) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("Not logged in.");
        }

        Ride ride = rideDAO.getRideById(rideId);
        if (ride == null || ride.getProviderId() != currentUser.getUserId()) {
            throw new IllegalArgumentException("You can only cancel your own rides.");
        }

        boolean updated = rideDAO.updateRideStatus(rideId, RideStatus.CANCELLED);
        if (updated) {
            List<Booking> activeBookings = bookingDAO.getActiveBookingsForRide(rideId);
            for (Booking booking : activeBookings) {
                bookingDAO.updateBookingStatus(booking.getBookingId(), BookingStatus.CANCELLED);
                notificationDAO.createNotification(new Notification(
                    booking.getSeekerId(),
                    "The ride from " + ride.getRouteFrom() + " to " + ride.getRouteTo()
                        + " has been CANCELLED by the provider.",
                    NotificationType.RIDE_CANCELLED
                ));
            }
        }
        return updated;
    }

    public List<Booking> getBookingsForRide(int rideId) {
        return bookingDAO.getBookingsForRide(rideId);
    }

    public List<Booking> getMyBookings() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("Not logged in.");
        }
        return bookingDAO.getBookingsBySeeker(currentUser.getUserId());
    }
}
